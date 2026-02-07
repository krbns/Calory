package com.kurban.calory.features.profile.domain

import com.kurban.calory.features.profile.domain.model.UserProfile
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

class ObserveUserProfileUseCase(
    private val repository: UserProfileRepository,
    private val dispatcher: CoroutineDispatcher
) {
    operator fun invoke(): Flow<UserProfile?> = repository.observeProfile(dispatcher).flowOn(dispatcher)
}
