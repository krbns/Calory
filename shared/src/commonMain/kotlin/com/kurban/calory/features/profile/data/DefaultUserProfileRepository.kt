package com.kurban.calory.features.profile.data

import com.kurban.calory.features.profile.domain.UserProfileRepository
import com.kurban.calory.features.profile.domain.model.UserProfile

class DefaultUserProfileRepository(
    private val dataSource: UserProfileDataSource
) : UserProfileRepository {
    override suspend fun getProfile(): UserProfile? {
        return dataSource.getProfile()
    }

    override suspend fun saveProfile(profile: UserProfile) {
        dataSource.saveProfile(profile)
    }
}
