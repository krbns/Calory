package com.kurban.calory.features.main.ui.logic

import com.kurban.calory.core.ui.time.DayProvider
import com.kurban.calory.features.main.domain.AddTrackedFoodUseCase
import com.kurban.calory.features.main.domain.CalculateTotalsUseCase
import com.kurban.calory.features.main.domain.DeleteTrackedFoodUseCase
import com.kurban.calory.features.main.domain.GetTrackedForDayUseCase
import com.kurban.calory.features.main.domain.SearchFoodUseCase
import com.kurban.calory.features.main.domain.model.Food
import com.kurban.calory.features.main.domain.model.TrackedFood
import com.kurban.calory.features.main.ui.model.MainAction
import com.kurban.calory.features.main.ui.model.MainUiState
import com.kurban.calory.features.profile.domain.CalculateMacroTargetsUseCase
import com.kurban.calory.features.profile.domain.ObserveUserProfileUseCase
import com.kurban.calory.features.profile.domain.UserProfileRepository
import com.kurban.calory.features.profile.domain.model.UserGoal
import com.kurban.calory.features.profile.domain.model.UserProfile
import com.kurban.calory.features.profile.domain.model.UserSex
import com.kurban.calory.features.profile.ui.logic.ObserveUserProfileMiddleware
import com.kurban.calory.testDispatchers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MiddlewareTests {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val dispatchers = testDispatchers(testDispatcher)
    private val scope = TestScope(testDispatcher)

    @Test
    fun `SearchMiddleware dispatches search results`() = runTest(testDispatcher) {
        val results = listOf(Food(1, 0, "Apple", 52.0, 0.3, 0.2, 14.0))
        val searchUseCase = SearchFoodUseCase(
            repository = object : com.kurban.calory.features.main.domain.FoodRepository {
                override fun findFood(name: String): Food? = null
                override fun search(query: String): List<Food> = results
                override fun addUserFood(name: String, grams: Int): Food? = null
            },
            dispatcher = dispatchers.io
        )
        val middleware = SearchMiddleware(searchUseCase, dispatchers, scope)
        val actions = Channel<MainAction>(Channel.UNLIMITED)

        middleware.invoke(
            MainAction.QueryChanged("app"),
            MainUiState(),
            dispatch = { actions.send(it) },
            emitEffect = {}
        )

        val dispatched = actions.receive()
        assertTrue(dispatched is MainAction.SearchSuccess)
        assertEquals(results, (dispatched as MainAction.SearchSuccess).results)
    }

    @Test
    fun `LoadDayMiddleware emits LoadDaySuccess`() = runTest(testDispatcher) {
        val tracked = listOf(
            TrackedFood(1, 1, "Apple", 100, 52.0, 0.3, 0.2, 14.0, "day", 0L)
        )
        val getTrackedForDay = GetTrackedForDayUseCase(
            repository = object : com.kurban.calory.features.main.domain.TrackedFoodRepository {
                override suspend fun add(food: TrackedFood) {}
                override suspend fun getByDay(dayId: String): List<TrackedFood> = tracked
                override suspend fun delete(id: Long) {}
            },
            dispatcher = dispatchers.io
        )
        val middleware = LoadDayMiddleware(
            getTrackedForDay = getTrackedForDay,
            dispatchers = dispatchers,
            calculateTotals = CalculateTotalsUseCase()
        )
        val actions = Channel<MainAction>(Channel.UNLIMITED)

        middleware.invoke(
            MainAction.LoadDay("day"),
            MainUiState(),
            dispatch = { actions.send(it) },
            emitEffect = {}
        )

        val dispatched = actions.receive()
        assertTrue(dispatched is MainAction.LoadDaySuccess)
        assertEquals(tracked.first().calories, dispatched.totals.calories)
    }

    @Test
    fun `AddFoodMiddleware dispatches LoadDay on success`() = runTest(testDispatcher) {
        val addTrackedFoodUseCase = AddTrackedFoodUseCase(
            repository = object : com.kurban.calory.features.main.domain.TrackedFoodRepository {
                override suspend fun add(food: TrackedFood) {}
                override suspend fun getByDay(dayId: String): List<TrackedFood> = emptyList()
                override suspend fun delete(id: Long) {}
            },
            foodRepository = object : com.kurban.calory.features.main.domain.FoodRepository {
                override fun findFood(name: String): Food? = Food(1, 0, "Apple", 52.0, 0.3, 0.2, 14.0)
                override fun search(query: String): List<Food> = emptyList()
                override fun addUserFood(name: String, grams: Int): Food? = null
            },
            dayProvider = object : DayProvider {
                override fun currentDayId(): String = "today"
            },
            dispatcher = dispatchers.io
        )
        val middleware = AddFoodMiddleware(addTrackedFoodUseCase)
        val actions = Channel<MainAction>(Channel.UNLIMITED)
        val state = MainUiState(
            selectedFood = Food(1, 0, "Apple", 52.0, 0.3, 0.2, 14.0),
            gramsInput = "100"
        )

        middleware.invoke(
            MainAction.AddSelectedFood,
            state,
            dispatch = { actions.send(it) },
            emitEffect = {}
        )

        val dispatched = actions.receive()
        assertEquals(MainAction.LoadDay("today"), dispatched)
    }

    @Test
    fun `RemoveEntryMiddleware dispatches reload after delete`() = runTest(testDispatcher) {
        var deletedId: Long? = null
        val deleteTrackedFoodUseCase = DeleteTrackedFoodUseCase(
            repository = object : com.kurban.calory.features.main.domain.TrackedFoodRepository {
                override suspend fun add(food: TrackedFood) {}
                override suspend fun getByDay(dayId: String): List<TrackedFood> = emptyList()
                override suspend fun delete(id: Long) {
                    deletedId = id
                }
            },
            dispatcher = dispatchers.io
        )
        val middleware = RemoveEntryMiddleware(
            deleteTrackedFood = deleteTrackedFoodUseCase,
            dayProvider = object : DayProvider {
                override fun currentDayId(): String = "today"
            }
        )
        val actions = Channel<MainAction>(Channel.UNLIMITED)

        middleware.invoke(
            MainAction.RemoveEntry(42),
            MainUiState(),
            dispatch = { actions.send(it) },
            emitEffect = {}
        )

        assertEquals(42, deletedId)
        val dispatched = actions.receive()
        assertEquals(MainAction.LoadDay("today"), dispatched)
    }

    @Test
    fun `ObserveUserProfileMiddleware dispatches targets from flow`() = runTest(testDispatcher) {
        val profile = UserProfile(UserSex.MALE, age = 25, heightCm = 180, weightKg = 80.0, goal = UserGoal.GAIN_MUSCLE)
        val observeUseCase = ObserveUserProfileUseCase(
            repository = object : UserProfileRepository {
                override suspend fun getProfile(): UserProfile? = profile
                override suspend fun saveProfile(profile: UserProfile) {}
                override fun observeProfile(dispatcher: CoroutineDispatcher): Flow<UserProfile?> = flowOf(profile)
            },
            dispatcher = dispatchers.io
        )
        val middleware = ObserveUserProfileMiddleware(
            observeUserProfileUseCase = observeUseCase,
            calculateMacroTargetsUseCase = CalculateMacroTargetsUseCase(),
            scope = scope
        )
        val actions = Channel<MainAction>(Channel.UNLIMITED)

        middleware.invoke(
            MainAction.ObserveProfile,
            MainUiState(),
            dispatch = { actions.send(it) },
            emitEffect = {}
        )

        val dispatched = actions.receive()
        assertTrue(dispatched is MainAction.LoadProfileSuccess)
        assertEquals( profile.weightKg * 2.0, dispatched.targets?.proteins)
    }
}
