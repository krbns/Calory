package com.kurban.calory.features.profile.data

import com.kurban.calory.features.profile.domain.UserProfileRepository
import com.kurban.calory.features.profile.domain.model.UserProfile
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

class DefaultUserProfileRepository(
    private val dataSource: UserProfileDataSource
) : UserProfileRepository {
    override suspend fun getProfile(): UserProfile? {
        return dataSource.getProfile()
    }

    override suspend fun saveProfile(profile: UserProfile) {
        dataSource.saveProfile(profile)
    }

    override fun observeProfile(dispatcher: CoroutineDispatcher): Flow<UserProfile?> = dataSource.observeProfile(dispatcher)
}
