package com.kurban.calory.features.profile.ui

import com.kurban.calory.features.profile.domain.model.UserGoal
import com.kurban.calory.features.profile.domain.model.UserProfile
import com.kurban.calory.features.profile.domain.model.UserSex
import com.kurban.calory.features.profile.ui.logic.profileReducer
import com.kurban.calory.features.profile.ui.model.ProfileAction
import com.kurban.calory.features.profile.ui.model.ProfileUiState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class ProfileReducerTest {

    private val reducer = profileReducer()

    @Test
    fun `load profile success fills fields`() {
        val profile = UserProfile(UserSex.FEMALE, 30, 170, 60.0, UserGoal.LOSE_WEIGHT)
        val updated = reducer(ProfileUiState(isLoading = true), ProfileAction.LoadProfileSuccess(profile))

        assertEquals("30", updated.ageInput)
        assertEquals("170", updated.heightInput)
        assertEquals("60.0", updated.weightInput)
        assertFalse(updated.isLoading)
        assertEquals(UserSex.FEMALE, updated.sex)
        assertEquals(UserGoal.LOSE_WEIGHT, updated.goal)
    }

    @Test
    fun `save profile failure stores error`() {
        val updated = reducer(ProfileUiState(isSaving = true), ProfileAction.SaveProfileFailure("err"))

        assertEquals("err", updated.errorMessage)
        assertFalse(updated.isSaving)
        assertFalse(updated.saved)
    }

    @Test
    fun `inputs are filtered`() {
        val afterAge = reducer(ProfileUiState(), ProfileAction.AgeChanged("1a2"))
        assertEquals("12", afterAge.ageInput)

        val afterHeight = reducer(ProfileUiState(), ProfileAction.HeightChanged("1b8c0"))
        assertEquals("180", afterHeight.heightInput)

        val afterWeight = reducer(ProfileUiState(), ProfileAction.WeightChanged("7x0,5y"))
        assertEquals("70.5", afterWeight.weightInput)
    }
}
