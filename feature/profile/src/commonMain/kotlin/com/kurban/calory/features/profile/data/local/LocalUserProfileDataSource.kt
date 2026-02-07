package com.kurban.calory.features.profile.data.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import app.cash.sqldelight.db.SqlDriver
import com.kurban.calory.features.profile.data.UserProfileDataSource
import com.kurban.calory.features.profile.domain.model.UserGoal
import com.kurban.calory.features.profile.domain.model.UserProfile
import com.kurban.calory.features.profile.domain.model.UserSex
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import sqldelight.userProfileScheme.profile.UserProfileDatabase

class LocalUserProfileDataSource(
    private val database: UserProfileDatabase,
    private val driver: SqlDriver
) : UserProfileDataSource {

    override suspend fun getProfile(): UserProfile? {
        val entity = runCatching {
            database.userProfileQueries.selectProfile().executeAsOneOrNull()
        }.recoverCatching { error ->
            ensureTable()
            database.userProfileQueries.selectProfile().executeAsOneOrNull()
        }.getOrNull() ?: return null
        val sex = entity.sex.toUserSex() ?: return null
        val goal = entity.goal.toUserGoal() ?: return null

        return UserProfile(
            name = entity.name,
            sex = sex,
            age = entity.age.toInt(),
            heightCm = entity.heightCm.toInt(),
            weightKg = entity.weightKg,
            goal = goal
        )
    }

    override suspend fun saveProfile(profile: UserProfile) {
        ensureTable()
        database.userProfileQueries.upsertProfile(
            name = profile.name,
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
            .catch { error ->
                ensureTable()
                emit(null)
            }
            .map { entity ->
                if (entity == null) return@map null
                val sex = entity.sex.toUserSex() ?: return@map null
                val goal = entity.goal.toUserGoal() ?: return@map null

                UserProfile(
                    name = entity.name,
                    sex = sex,
                    age = entity.age.toInt(),
                    heightCm = entity.heightCm.toInt(),
                    weightKg = entity.weightKg,
                    goal = goal
                )
            }
    }

    private fun ensureTable() {
        driver.execute(
            identifier = null,
            sql = """
                CREATE TABLE IF NOT EXISTS UserProfileEntity(
                    id INTEGER NOT NULL PRIMARY KEY,
                    name TEXT NOT NULL,
                    sex TEXT NOT NULL,
                    age INTEGER NOT NULL,
                    heightCm INTEGER NOT NULL,
                    weightKg REAL NOT NULL,
                    goal TEXT NOT NULL
                );
            """.trimIndent(),
            parameters = 0
        )
        runCatching {
            driver.execute(
                identifier = null,
                sql = "ALTER TABLE UserProfileEntity ADD COLUMN name TEXT NOT NULL DEFAULT ''",
                parameters = 0
            )
        }
    }
}

private fun String.toUserSex(): UserSex? = runCatching { UserSex.valueOf(this) }.getOrNull()

private fun String.toUserGoal(): UserGoal? = runCatching { UserGoal.valueOf(this) }.getOrNull()
