package com.kurban.calory.features.main.ui

import androidx.lifecycle.ViewModel
import com.kurban.calory.core.domain.AppDispatchers
import com.kurban.calory.features.main.domain.AddTrackedFoodUseCase
import com.kurban.calory.features.main.domain.DeleteConsumedFoodUseCase
import com.kurban.calory.features.main.domain.GetTrackedForDayUseCase
import com.kurban.calory.features.main.domain.SearchFoodUseCase
import com.kurban.calory.features.main.domain.model.TrackedFood
import com.kurban.calory.features.main.domain.model.Food
import com.kurban.calory.features.main.ui.model.MainUiState
import com.kurban.calory.features.main.ui.model.UITrackedFood
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class MainViewModel(
    private val searchFood: SearchFoodUseCase,
    private val getTrackedForDay: GetTrackedForDayUseCase,
    private val deleteTrackedFood: DeleteConsumedFoodUseCase,
    private val addTrackedFoodUseCase: AddTrackedFoodUseCase,
    private val dispatchers: AppDispatchers,
    coroutineScope: CoroutineScope? = null,
) : ViewModel() {

    private val scope = coroutineScope ?: CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null
    private val dayId: String
        get() = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()

    init {
        loadToday()
    }

    fun onQueryChanged(newQuery: String) {
        _uiState.update { it.copy(query = newQuery, isSearching = true, error = null) }
        launchSearch(newQuery)
    }

    fun onFoodSelected(food: Food) {
        _uiState.update { it.copy(selectedFood = food, error = null) }
    }

    fun onGramsChanged(value: String) {
        _uiState.update { it.copy(gramsInput = value.filter { char -> char.isDigit() }) }
    }

    fun addSelectedFood() {
        val selected = _uiState.value.selectedFood ?: run {
            _uiState.update { it.copy(error = "Сначала выберите продукт") }
            return
        }
        val grams = _uiState.value.gramsInput.toIntOrNull() ?: run {
            _uiState.update { it.copy(error = "Введите размер порции в граммах") }
            return
        }

        scope.launch {
            when (val result = addTrackedFoodUseCase(AddTrackedFoodUseCase.Parameters(selected.name, grams))) {
                is AddTrackedFoodUseCase.Result.Success -> loadDay(result.dayId)
                is AddTrackedFoodUseCase.Result.Error -> _uiState.update { it.copy(error = result.message) }
                null -> _uiState.update { it.copy(error = "Не удалось добавить продукт") }
            }
        }
    }

    fun removeEntry(entryId: Long) {
        scope.launch {
            deleteTrackedFood(DeleteConsumedFoodUseCase.Parameters(entryId))
            loadToday()
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun clear() {
        scope.cancel()
    }

    private fun launchSearch(query: String) {
        searchJob?.cancel()
        if (query.isBlank()) {
            _uiState.update { it.copy(searchResults = emptyList(), isSearching = false) }
            return
        }

        searchJob = scope.launch {
            val result = searchFood(SearchFoodUseCase.Parameters(query)) ?: emptyList()
            _uiState.update {
                it.copy(
                    searchResults = result,
                    isSearching = false,
                    selectedFood = if (result.isNotEmpty()) result.first() else it.selectedFood
                )
            }
        }
    }

    private fun calculateTotals(consumed: List<UITrackedFood>): MacroTotals {
        return consumed.fold(MacroTotals()) { acc, food ->
            MacroTotals(
                calories = acc.calories + food.calories,
                proteins = acc.proteins + food.proteins,
                fats = acc.fats + food.fats,
                carbs = acc.carbs + food.carbs
            )
        }
    }

    private fun loadToday() {
        loadDay(dayId)
    }

    private fun loadDay(dayId: String) {
        scope.launch(dispatchers.main) {
            val tracked = withContext(dispatchers.io) {
                getTrackedForDay(GetTrackedForDayUseCase.Parameters(dayId)) ?: emptyList()
            }
            val uiItems = tracked.map { it.toUi() }
            val totals = calculateTotals(uiItems)
            _uiState.update {
                it.copy(
                    tracked = uiItems,
                    totalCalories = totals.calories,
                    totalProteins = totals.proteins,
                    totalFats = totals.fats,
                    totalCarbs = totals.carbs,
                    error = null
                )
            }
        }
    }

    private fun TrackedFood.toUi(): UITrackedFood = UITrackedFood(
        entryId = id,
        foodId = foodId,
        name = name,
        grams = grams,
        calories = calories,
        proteins = proteins,
        fats = fats,
        carbs = carbs
    )

    data class MacroTotals(
        val calories: Double = 0.0,
        val proteins: Double = 0.0,
        val fats: Double = 0.0,
        val carbs: Double = 0.0
    )
}
