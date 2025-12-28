package com.kurban.calory.features.profile.ui

import com.kurban.calory.testDispatchers
import com.kurban.calory.features.profile.domain.GetUserProfileUseCase
import com.kurban.calory.features.profile.domain.SaveUserProfileUseCase
import com.kurban.calory.features.profile.domain.model.UserGoal
import com.kurban.calory.features.profile.domain.model.UserProfile
import com.kurban.calory.features.profile.domain.model.UserSex
import com.kurban.calory.features.profile.ui.logic.LoadProfileMiddleware
import com.kurban.calory.features.profile.ui.logic.SaveProfileMiddleware
import com.kurban.calory.features.profile.ui.model.ProfileAction
import com.kurban.calory.features.profile.ui.model.ProfileEffect
import com.kurban.calory.features.profile.ui.model.ProfileUiState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ProfileMiddlewareTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private val dispatchers = testDispatchers(dispatcher)

    @Test
    fun `LoadProfileMiddleware dispatches success`() = runTest(dispatcher) {
        val profile = UserProfile("John", UserSex.MALE, 25, 180, 80.0, UserGoal.GAIN_MUSCLE)
        val middleware = LoadProfileMiddleware(
            getUserProfileUseCase = GetUserProfileUseCase(
                repository = object : com.kurban.calory.features.profile.domain.UserProfileRepository {
                    override suspend fun getProfile(): UserProfile? = profile
                    override suspend fun saveProfile(profile: UserProfile) {}
                    override fun observeProfile(dispatcher: CoroutineDispatcher) = throw UnsupportedOperationException()
                },
                dispatcher = dispatchers.io
            ),
            dispatchers = dispatchers
        )

        val actions = Channel<ProfileAction>(Channel.UNLIMITED)
        middleware.invoke(
            ProfileAction.LoadProfile,
            ProfileUiState(),
            dispatch = { actions.send(it) },
            emitEffect = {}
        )

        val dispatched = actions.receive()
        assertTrue(dispatched is ProfileAction.LoadProfileSuccess)
        assertEquals(profile, dispatched.profile)
    }

    @Test
    fun `SaveProfileMiddleware validates and saves`() = runTest(dispatcher) {
        var savedProfile: UserProfile? = null
        val middleware = SaveProfileMiddleware(
            saveUserProfileUseCase = SaveUserProfileUseCase(
                repository = object : com.kurban.calory.features.profile.domain.UserProfileRepository {
                    override suspend fun getProfile(): UserProfile? = null
                    override suspend fun saveProfile(profile: UserProfile) {
                        savedProfile = profile
                    }
                    override fun observeProfile(dispatcher: CoroutineDispatcher) = throw UnsupportedOperationException()
                },
                dispatcher = dispatchers.io
            ),
            dispatchers = dispatchers
        )
        val state = ProfileUiState(
            nameInput = "Anna",
            sex = UserSex.FEMALE,
            goal = UserGoal.LOSE_WEIGHT,
            ageInput = "30",
            heightInput = "165",
            weightInput = "55.0"
        )
        val actions = Channel<ProfileAction>(Channel.UNLIMITED)
        val effects = Channel<ProfileEffect>(Channel.UNLIMITED)

        middleware.invoke(
            ProfileAction.SaveProfile,
            state,
            dispatch = { actions.send(it) },
            emitEffect = { effects.send(it) }
        )

        val start = actions.receive()
        assertEquals(ProfileAction.SaveProfileStarted, start)
        val success = actions.receive()
        assertTrue(success is ProfileAction.SaveProfileSuccess)
        assertFalse(effects.tryReceive().isSuccess)
        assertEquals(state.sex, savedProfile?.sex)
        assertEquals(state.goal, savedProfile?.goal)
        assertEquals("Anna", savedProfile?.name)
    }

    @Test
    fun `SaveProfileMiddleware emits failure on invalid data`() = runTest(dispatcher) {
        val middleware = SaveProfileMiddleware(
            saveUserProfileUseCase = SaveUserProfileUseCase(
                repository = object : com.kurban.calory.features.profile.domain.UserProfileRepository {
                    override suspend fun getProfile(): UserProfile? = null
                    override suspend fun saveProfile(profile: UserProfile) {}
                    override fun observeProfile(dispatcher: CoroutineDispatcher) = throw UnsupportedOperationException()
                },
                dispatcher = dispatchers.io
            ),
            dispatchers = dispatchers
        )
        val state = ProfileUiState(ageInput = "abc", heightInput = "", weightInput = "")
        val actions = Channel<ProfileAction>(Channel.UNLIMITED)

        middleware.invoke(
            ProfileAction.SaveProfile,
            state,
            dispatch = { actions.send(it) },
            emitEffect = {}
        )

        val failure = actions.receive()
        assertTrue(failure is ProfileAction.SaveProfileFailure)
    }
}
