package com.kurban.calory.ios

import com.kurban.calory.core.domain.AppResult
import com.kurban.calory.features.customfood.domain.model.CustomFood
import com.kurban.calory.features.main.domain.model.Food
import com.kurban.calory.features.main.domain.model.MacroTotals
import com.kurban.calory.features.main.domain.model.TrackedFood
import com.kurban.calory.features.profile.domain.model.MacroTargets
import com.kurban.calory.features.profile.domain.model.UserGoal
import com.kurban.calory.features.profile.domain.model.UserProfile
import com.kurban.calory.features.profile.domain.model.UserSex
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

interface DisposableHandle {
    fun dispose()
}

private class JobDisposableHandle(
    private val job: Job,
) : DisposableHandle {
    override fun dispose() {
        job.cancel()
    }
}

internal class ScopeDisposableHandle(
    private val scope: CoroutineScope,
) : DisposableHandle {
    override fun dispose() {
        scope.cancel()
    }
}

internal fun <T> CoroutineScope.observe(
    flow: Flow<T>,
    onEach: (T) -> Unit,
    onError: (String) -> Unit,
): DisposableHandle {
    val job = launch {
        runCatching {
            flow.collect(onEach)
        }.onFailure { throwable ->
            onError(throwable.message ?: "Unknown error")
        }
    }

    return JobDisposableHandle(job)
}

internal inline fun <T> AppResult<T>.foldResult(
    onSuccess: (T) -> Unit,
    onError: (String) -> Unit,
) {
    when (this) {
        is AppResult.Success -> onSuccess(value)
        is AppResult.Failure -> onError(error.originalMessage)
    }
}

internal fun Food.toDto(): IosFoodDto = IosFoodDto(
    id = id,
    name = name,
    grams = grams,
    calories = calories,
    proteins = proteins,
    fats = fats,
    carbs = carbs,
)

internal fun TrackedFood.toDto(): IosTrackedFoodDto = IosTrackedFoodDto(
    id = id,
    foodId = foodId,
    name = name,
    grams = grams,
    calories = calories,
    proteins = proteins,
    fats = fats,
    carbs = carbs,
    dayId = dayId,
    timestamp = timestamp,
)

internal fun MacroTotals.toDto(): IosMacroTotalsDto = IosMacroTotalsDto(
    calories = calories,
    proteins = proteins,
    fats = fats,
    carbs = carbs,
)

internal fun MacroTargets.toDto(): IosMacroTargetsDto = IosMacroTargetsDto(
    calories = calories,
    proteins = proteins,
    fats = fats,
    carbs = carbs,
)

internal fun UserProfile.toDto(): IosUserProfileDto = IosUserProfileDto(
    name = name,
    sex = sex.name,
    age = age,
    heightCm = heightCm,
    weightKg = weightKg,
    goal = goal.name,
)

internal fun CustomFood.toDto(): IosCustomFoodDto = IosCustomFoodDto(
    id = id,
    name = name,
    calories = calories,
    proteins = proteins,
    fats = fats,
    carbs = carbs,
)

internal fun parseSex(value: String): UserSex? = when (value.uppercase()) {
    UserSex.MALE.name -> UserSex.MALE
    UserSex.FEMALE.name -> UserSex.FEMALE
    else -> null
}

internal fun parseGoal(value: String): UserGoal? = when (value.uppercase()) {
    UserGoal.GAIN_MUSCLE.name -> UserGoal.GAIN_MUSCLE
    UserGoal.LOSE_WEIGHT.name -> UserGoal.LOSE_WEIGHT
    else -> null
}
