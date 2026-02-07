package com.kurban.calory.features.profile.domain

import com.kurban.calory.features.profile.domain.model.UserProfile
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

interface UserProfileRepository {
    suspend fun getProfile(): UserProfile?

    suspend fun saveProfile(profile: UserProfile)

    fun observeProfile(dispatcher: CoroutineDispatcher): Flow<UserProfile?>
}
