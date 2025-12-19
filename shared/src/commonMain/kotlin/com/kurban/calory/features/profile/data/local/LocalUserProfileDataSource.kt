package com.kurban.calory.features.profile.data.local

import com.kurban.calory.features.profile.data.UserProfileDataSource
import com.kurban.calory.features.profile.domain.model.UserGoal
import com.kurban.calory.features.profile.domain.model.UserProfile
import com.kurban.calory.features.profile.domain.model.UserSex
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import kotlinx.coroutines.CoroutineDispatcher
import sqldelight.userProfileScheme.profile.UserProfileDatabase

class LocalUserProfileDataSource(
    private val database: UserProfileDatabase
) : UserProfileDataSource {

    override suspend fun getProfile(): UserProfile? {
        val entity = database.userProfileQueries.selectProfile().executeAsOneOrNull() ?: return null
        val sex = entity.sex.toUserSex() ?: return null
        val goal = entity.goal.toUserGoal() ?: return null

        return UserProfile(
            sex = sex,
            age = entity.age.toInt(),
            heightCm = entity.heightCm.toInt(),
            weightKg = entity.weightKg,
            goal = goal
        )
    }

    override suspend fun saveProfile(profile: UserProfile) {
        database.userProfileQueries.upsertProfile(
            sex = profile.sex.name,
            age = profile.age.toLong(),
            heightCm = profile.heightCm.toLong(),
            weightKg = profile.weightKg,
            goal = profile.goal.name
        )
    }

    override fun observeProfile(dispatcher: CoroutineDispatcher): Flow<UserProfile?> {
        return database.userProfileQueries.selectProfile()
            .asFlow()
            .mapToOneOrNull(dispatcher)
            .map { entity ->
                if (entity == null) return@map null
                val sex = entity.sex.toUserSex() ?: return@map null
                val goal = entity.goal.toUserGoal() ?: return@map null

                UserProfile(
                    sex = sex,
                    age = entity.age.toInt(),
                    heightCm = entity.heightCm.toInt(),
                    weightKg = entity.weightKg,
                    goal = goal
                )
            }
    }
}

private fun String.toUserSex(): UserSex? = runCatching { UserSex.valueOf(this) }.getOrNull()

private fun String.toUserGoal(): UserGoal? = runCatching { UserGoal.valueOf(this) }.getOrNull()
