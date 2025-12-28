package com.kurban.calory.features.profile.ui.model

import com.kurban.calory.features.profile.domain.model.UserGoal
import com.kurban.calory.features.profile.domain.model.UserProfile
import com.kurban.calory.features.profile.domain.model.UserSex

sealed class ProfileAction {
    object LoadProfile : ProfileAction()
    data class NameChanged(val value: String) : ProfileAction()
    data class SexSelected(val sex: UserSex) : ProfileAction()
    data class GoalSelected(val goal: UserGoal) : ProfileAction()
    data class AgeChanged(val value: String) : ProfileAction()
    data class HeightChanged(val value: String) : ProfileAction()
    data class WeightChanged(val value: String) : ProfileAction()
    object SaveProfile : ProfileAction()
    object ClearError : ProfileAction()

    data class LoadProfileSuccess(val profile: UserProfile?) : ProfileAction()
    data class LoadProfileFailure(val message: String) : ProfileAction()

    object SaveProfileStarted : ProfileAction()
    data class SaveProfileSuccess(val profile: UserProfile) : ProfileAction()
    data class SaveProfileFailure(val message: String) : ProfileAction()
}
