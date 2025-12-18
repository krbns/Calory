package com.kurban.calory.features.profile.domain

import com.kurban.calory.features.profile.domain.model.UserProfile

interface UserProfileRepository {
    suspend fun getProfile(): UserProfile?

    suspend fun saveProfile(profile: UserProfile)
}
