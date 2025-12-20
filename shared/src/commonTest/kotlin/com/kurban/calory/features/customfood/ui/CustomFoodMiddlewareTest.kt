package com.kurban.calory.features.customfood.ui

import com.kurban.calory.core.ui.time.DayProvider
import com.kurban.calory.features.customfood.domain.AddCustomFoodToDiaryUseCase
import com.kurban.calory.features.customfood.domain.CreateCustomFoodUseCase
import com.kurban.calory.features.customfood.domain.ObserveCustomFoodsUseCase
import com.kurban.calory.features.customfood.domain.model.CustomFood
import com.kurban.calory.features.customfood.ui.logic.AddCustomFoodToDiaryMiddleware
import com.kurban.calory.features.customfood.ui.logic.CreateCustomFoodMiddleware
import com.kurban.calory.features.customfood.ui.logic.ObserveCustomFoodsMiddleware
import com.kurban.calory.features.customfood.ui.model.CustomFoodAction
import com.kurban.calory.features.customfood.ui.model.CustomFoodEffect
import com.kurban.calory.features.customfood.ui.model.CustomFoodUiState
import com.kurban.calory.features.main.domain.TrackedFoodRepository
import com.kurban.calory.features.main.domain.model.TrackedFood
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

class CustomFoodMiddlewareTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private val scope = TestScope(dispatcher)

    @Test
    fun `observe middleware dispatches foods`() = runTest(dispatcher) {
        val foods = listOf(CustomFood(1, "Apple", 50.0, 0.3, 0.2, 12.0))
        val repository = object : com.kurban.calory.features.customfood.domain.CustomFoodRepository {
            override fun observeAll(dispatcher: CoroutineDispatcher): Flow<List<CustomFood>> = flowOf(foods)
            override suspend fun add(food: com.kurban.calory.features.customfood.domain.NewCustomFood): CustomFood = foods.first()
            override suspend fun getById(id: Long): CustomFood? = foods.firstOrNull()
        }
        val middleware = ObserveCustomFoodsMiddleware(
            observeCustomFoods = ObserveCustomFoodsUseCase(repository, dispatcher),
            scope = scope
        )
        val actions = Channel<CustomFoodAction>(Channel.UNLIMITED)

        middleware.invoke(
            CustomFoodAction.ObserveFoods,
            CustomFoodUiState(),
            dispatch = { actions.send(it) },
            emitEffect = {}
        )

        val dispatched = actions.receive()
        assertTrue(dispatched is CustomFoodAction.FoodsUpdated)
        assertEquals(foods, (dispatched as CustomFoodAction.FoodsUpdated).foods)
    }

    @Test
    fun `create middleware emits success effect`() = runTest(dispatcher) {
        val repository = object : com.kurban.calory.features.customfood.domain.CustomFoodRepository {
            override fun observeAll(dispatcher: CoroutineDispatcher): Flow<List<CustomFood>> = flowOf(emptyList())
            override suspend fun add(food: com.kurban.calory.features.customfood.domain.NewCustomFood): CustomFood =
                CustomFood(1, food.name, food.calories, food.proteins, food.fats, food.carbs)

            override suspend fun getById(id: Long): CustomFood? = null
        }
        val middleware = CreateCustomFoodMiddleware(
            createCustomFoodUseCase = CreateCustomFoodUseCase(repository, dispatcher)
        )
        val effects = Channel<CustomFoodEffect>(Channel.UNLIMITED)
        val actions = Channel<CustomFoodAction>(Channel.UNLIMITED)

        middleware.invoke(
            CustomFoodAction.CreateFood(
                CreateCustomFoodUseCase.Parameters("Apple", 10.0, 1.0, 1.0, 1.0)
            ),
            CustomFoodUiState(),
            dispatch = { actions.send(it) },
            emitEffect = { effects.send(it) }
        )

        assertTrue(actions.receive() is CustomFoodAction.CreateFoodSuccess)
        val effect = effects.receive()
        assertTrue(effect is CustomFoodEffect.FoodCreated)
    }

    @Test
    fun `add to diary middleware emits effect on success`() = runTest(dispatcher) {
        val food = CustomFood(1, "Apple", 10.0, 1.0, 1.0, 1.0)
        val repository = object : com.kurban.calory.features.customfood.domain.CustomFoodRepository {
            override fun observeAll(dispatcher: CoroutineDispatcher): Flow<List<CustomFood>> = flowOf(listOf(food))
            override suspend fun add(food: com.kurban.calory.features.customfood.domain.NewCustomFood): CustomFood = food.run { CustomFood(1, name, calories, proteins, fats, carbs) }
            override suspend fun getById(id: Long): CustomFood? = food
        }
        val middleware = AddCustomFoodToDiaryMiddleware(
            addCustomFoodToDiary = AddCustomFoodToDiaryUseCase(
                customFoodRepository = repository,
                trackedFoodRepository = object : TrackedFoodRepository {
                    override suspend fun add(food: TrackedFood) {}
                    override suspend fun getByDay(dayId: String): List<TrackedFood> = emptyList()
                    override fun observeByDay(dayId: String, dispatcher: CoroutineDispatcher): Flow<List<TrackedFood>> = flowOf(emptyList())
                    override suspend fun delete(id: Long) {}
                },
                dayProvider = object : DayProvider {
                    override fun currentDayId(): String = "today"
                },
                dispatcher = dispatcher
            )
        )
        val effects = Channel<CustomFoodEffect>(Channel.UNLIMITED)

        middleware.invoke(
            CustomFoodAction.AddToDiary(foodId = 1, grams = 100),
            CustomFoodUiState(foods = listOf(food), filteredFoods = listOf(food)),
            dispatch = { },
            emitEffect = { effects.send(it) }
        )

        val effect = effects.receive()
        assertTrue(effect is CustomFoodEffect.AddedToDiary)
    }
}
