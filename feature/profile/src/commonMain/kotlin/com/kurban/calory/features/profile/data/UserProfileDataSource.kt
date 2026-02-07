package com.kurban.calory.features.profile.data

import com.kurban.calory.features.profile.domain.model.UserProfile
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

interface UserProfileDataSource {
    suspend fun getProfile(): UserProfile?

    suspend fun saveProfile(profile: UserProfile)

    fun observeProfile(dispatcher: CoroutineDispatcher): Flow<UserProfile?>
}
