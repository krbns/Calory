package com.kurban.calory.features.profile.ui.model

import com.kurban.calory.features.profile.domain.model.UserGoal
import com.kurban.calory.features.profile.domain.model.UserSex

sealed class ProfileIntent {
    object LoadProfile : ProfileIntent()
    data class NameChanged(val value: String) : ProfileIntent()
    data class SexSelected(val sex: UserSex) : ProfileIntent()
    data class GoalSelected(val goal: UserGoal) : ProfileIntent()
    data class AgeChanged(val value: String) : ProfileIntent()
    data class HeightChanged(val value: String) : ProfileIntent()
    data class WeightChanged(val value: String) : ProfileIntent()
    object Save : ProfileIntent()
    object ClearError : ProfileIntent()
}
