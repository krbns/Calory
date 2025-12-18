package com.kurban.calory.features.profile.data

import com.kurban.calory.features.profile.domain.model.UserProfile

interface UserProfileDataSource {
    suspend fun getProfile(): UserProfile?

    suspend fun saveProfile(profile: UserProfile)
}
