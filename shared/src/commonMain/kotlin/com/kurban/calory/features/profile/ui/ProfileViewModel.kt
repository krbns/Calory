package com.kurban.calory.features.profile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kurban.calory.core.domain.AppDispatchers
import com.kurban.calory.features.profile.domain.GetUserProfileUseCase
import com.kurban.calory.features.profile.domain.SaveUserProfileUseCase
import com.kurban.calory.features.profile.domain.model.UserGoal
import com.kurban.calory.features.profile.domain.model.UserProfile
import com.kurban.calory.features.profile.domain.model.UserSex
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileViewModel(
    private val getUserProfile: GetUserProfileUseCase,
    private val saveUserProfile: SaveUserProfileUseCase,
    private val dispatchers: AppDispatchers
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState

    init {
        loadProfile()
    }

    fun onSexSelected(sex: UserSex) {
        _uiState.update { it.copy(sex = sex, saved = false) }
    }

    fun onGoalSelected(goal: UserGoal) {
        _uiState.update { it.copy(goal = goal, saved = false) }
    }

    fun onAgeChanged(value: String) {
        _uiState.update { it.copy(ageInput = value.filter(Char::isDigit), saved = false) }
    }

    fun onHeightChanged(value: String) {
        _uiState.update { it.copy(heightInput = value.filter(Char::isDigit), saved = false) }
    }

    fun onWeightChanged(value: String) {
        val normalized = value.replace(',', '.')
        val filtered = normalized.filter { it.isDigit() || it == '.' }
        _uiState.update { it.copy(weightInput = filtered, saved = false) }
    }

    fun save() {
        val state = _uiState.value
        val age = state.ageInput.toIntOrNull()
        val height = state.heightInput.toIntOrNull()
        val weight = state.weightInput.replace(',', '.').toDoubleOrNull()

        if (age == null || age <= 0) {
            _uiState.update { it.copy(errorMessage = "Введите возраст", saved = false) }
            return
        }
        if (height == null || height <= 0) {
            _uiState.update { it.copy(errorMessage = "Введите рост", saved = false) }
            return
        }
        if (weight == null || weight <= 0.0) {
            _uiState.update { it.copy(errorMessage = "Введите вес", saved = false) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null, saved = false) }
            try {
                val profile = UserProfile(
                    sex = state.sex,
                    age = age,
                    heightCm = height,
                    weightKg = weight,
                    goal = state.goal
                )
                withContext(dispatchers.io) {
                    saveUserProfile(profile)
                }
                _uiState.update { it.copy(isSaving = false, saved = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false, errorMessage = e.message ?: "Не удалось сохранить") }
            }
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val profile = withContext(dispatchers.io) { getUserProfile(Unit) }
                if (profile != null) {
                    _uiState.update {
                        it.copy(
                            sex = profile.sex,
                            goal = profile.goal,
                            ageInput = profile.age.toString(),
                            heightInput = profile.heightCm.toString(),
                            weightInput = profile.weightKg.toString(),
                            isLoading = false
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Не удалось загрузить профиль") }
            }
        }
    }
}
