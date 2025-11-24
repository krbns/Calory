package com.kurban.calory.features.main.ui

import com.kurban.calory.features.main.domain.FoodRepository
import com.kurban.calory.features.main.domain.SearchFoodUseCase
import com.kurban.calory.features.main.domain.model.Food
import com.kurban.calory.features.main.domain.model.TrackedFood
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
import kotlin.random.Random

class MainViewModel(
    private val searchFood: SearchFoodUseCase,
    private val foodRepository: FoodRepository,
    coroutineScope: CoroutineScope? = null,
) {

    private val scope = coroutineScope ?: CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null
    private var entryCounter = 0L

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
            _uiState.update { it.copy(error = "Pick a product first") }
            return
        }
        val grams = _uiState.value.gramsInput.toIntOrNull() ?: run {
            _uiState.update { it.copy(error = "Enter portion size in grams") }
            return
        }

        scope.launch {
            val tracked = withContext(Dispatchers.Default) {
                val food = foodRepository.addUserFood(selected.name, grams)
                food?.let {
                    TrackedFood(
                        entryId = nextEntryId(),
                        foodId = it.id,
                        name = it.name,
                        grams = grams,
                        calories = it.calories,
                        proteins = it.proteins,
                        fats = it.fats,
                        carbs = it.carbs
                    )
                }
            }

            if (tracked == null) {
                _uiState.update { it.copy(error = "Could not add this food") }
                return@launch
            }

            val updatedList = _uiState.value.consumed + tracked
            val totals = calculateTotals(updatedList)
            _uiState.update {
                it.copy(
                    consumed = updatedList,
                    totalCalories = totals.calories,
                    totalProteins = totals.proteins,
                    totalFats = totals.fats,
                    totalCarbs = totals.carbs,
                    error = null
                )
            }
        }
    }

    fun removeEntry(entryId: Long) {
        val updatedList = _uiState.value.consumed.filterNot { it.entryId == entryId }
        val totals = calculateTotals(updatedList)
        _uiState.update {
            it.copy(
                consumed = updatedList,
                totalCalories = totals.calories,
                totalProteins = totals.proteins,
                totalFats = totals.fats,
                totalCarbs = totals.carbs
            )
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

    private fun calculateTotals(consumed: List<TrackedFood>): MacroTotals {
        return consumed.fold(MacroTotals()) { acc, food ->
            MacroTotals(
                calories = acc.calories + food.calories,
                proteins = acc.proteins + food.proteins,
                fats = acc.fats + food.fats,
                carbs = acc.carbs + food.carbs
            )
        }
    }

    private fun nextEntryId(): Long {
        entryCounter += 1
        return entryCounter + Random.nextLong(1_000_000_000L)
    }

    data class MacroTotals(
        val calories: Double = 0.0,
        val proteins: Double = 0.0,
        val fats: Double = 0.0,
        val carbs: Double = 0.0
    )
}
