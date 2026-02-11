package com.kurban.calory.ios

import com.kurban.calory.features.profile.domain.NeedsOnboardingUseCase
import com.kurban.calory.features.profile.domain.SaveUserProfileUseCase
import com.kurban.calory.features.profile.domain.model.UserProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.core.Koin

class OnboardingBridge internal constructor(
    koin: Koin,
    private val scope: CoroutineScope,
) {
    private val needsOnboardingUseCase = koin.get<NeedsOnboardingUseCase>()
    private val saveUserProfileUseCase = koin.get<SaveUserProfileUseCase>()

    fun needsOnboarding(
        onSuccess: (Boolean) -> Unit,
        onError: (String) -> Unit,
    ) {
        scope.launch {
            needsOnboardingUseCase(Unit).foldResult(
                onSuccess = onSuccess,
                onError = onError,
            )
        }
    }

    fun saveProfile(
        name: String,
        sex: String,
        age: Int,
        heightCm: Int,
        weightKg: Double,
        goal: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        val parsedSex = parseSex(sex)
        val parsedGoal = parseGoal(goal)

        if (parsedSex == null || parsedGoal == null) {
            onError("Некорректные значения профиля")
            return
        }

        val profile = UserProfile(
            name = name.trim(),
            sex = parsedSex,
            age = age,
            heightCm = heightCm,
            weightKg = weightKg,
            goal = parsedGoal,
        )

        scope.launch {
            saveUserProfileUseCase(profile).foldResult(
                onSuccess = { onSuccess() },
                onError = onError,
            )
        }
    }
}
