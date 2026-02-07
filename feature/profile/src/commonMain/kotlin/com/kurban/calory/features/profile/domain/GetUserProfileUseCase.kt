package com.kurban.calory.features.profile.domain

import com.kurban.calory.core.domain.CoroutineUseCase
import com.kurban.calory.features.profile.domain.model.UserProfile
import kotlinx.coroutines.CoroutineDispatcher

class GetUserProfileUseCase(
    private val repository: UserProfileRepository,
    dispatcher: CoroutineDispatcher
) : CoroutineUseCase<Unit, UserProfile?>(dispatcher) {

    override suspend fun execute(parameters: Unit): UserProfile? {
        return repository.getProfile()
    }
}
