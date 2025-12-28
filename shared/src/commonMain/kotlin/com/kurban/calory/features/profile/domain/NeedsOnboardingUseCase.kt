package com.kurban.calory.features.profile.domain

import com.kurban.calory.core.domain.CoroutineUseCase
import kotlinx.coroutines.CoroutineDispatcher

class NeedsOnboardingUseCase(
    private val repository: UserProfileRepository,
    dispatcher: CoroutineDispatcher
) : CoroutineUseCase<Unit, Boolean>(dispatcher) {
    override suspend fun execute(parameters: Unit): Boolean {
        val profile = repository.getProfile()
        return profile == null
    }
}
