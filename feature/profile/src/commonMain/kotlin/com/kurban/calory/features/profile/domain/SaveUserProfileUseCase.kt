package com.kurban.calory.features.profile.domain

import com.kurban.calory.core.domain.CoroutineUseCase
import com.kurban.calory.features.profile.domain.model.UserProfile
import kotlinx.coroutines.CoroutineDispatcher

class SaveUserProfileUseCase(
    private val repository: UserProfileRepository,
    dispatcher: CoroutineDispatcher
) : CoroutineUseCase<UserProfile, Unit>(dispatcher) {

    override suspend fun execute(parameters: UserProfile) {
        repository.saveProfile(parameters)
    }
}
