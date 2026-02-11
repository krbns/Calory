package com.kurban.calory.ios

import com.kurban.calory.features.profile.domain.ObserveUserProfileUseCase
import com.kurban.calory.features.profile.domain.SaveUserProfileUseCase
import com.kurban.calory.features.profile.domain.model.UserProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.core.Koin

class ProfileBridge internal constructor(
    koin: Koin,
    private val scope: CoroutineScope,
) {
    private val observeUserProfileUseCase = koin.get<ObserveUserProfileUseCase>()
    private val saveUserProfileUseCase = koin.get<SaveUserProfileUseCase>()

    fun observeProfile(
        onEach: (IosUserProfileDto?) -> Unit,
        onError: (String) -> Unit,
    ): DisposableHandle {
        return scope.observe(
            flow = observeUserProfileUseCase(),
            onEach = { profile -> onEach(profile?.toDto()) },
            onError = onError,
        )
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
