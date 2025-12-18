package com.kurban.calory.features.profile.domain

import com.kurban.calory.features.profile.domain.model.MacroTargets
import com.kurban.calory.features.profile.domain.model.UserGoal
import com.kurban.calory.features.profile.domain.model.UserProfile
import com.kurban.calory.features.profile.domain.model.UserSex
import kotlin.math.max
import kotlin.math.roundToInt

class CalculateMacroTargetsUseCase {
    operator fun invoke(profile: UserProfile): MacroTargets {
        val bmr = when (profile.sex) {
            UserSex.MALE -> 10.0 * profile.weightKg + 6.25 * profile.heightCm - 5.0 * profile.age + 5.0
            UserSex.FEMALE -> 10.0 * profile.weightKg + 6.25 * profile.heightCm - 5.0 * profile.age - 161.0
        }

        val tdee = bmr * ACTIVITY_FACTOR
        val calories = when (profile.goal) {
            UserGoal.GAIN_MUSCLE -> tdee + CALORIE_SURPLUS
            UserGoal.LOSE_WEIGHT -> tdee - CALORIE_DEFICIT
        }

        val proteinGrams = profile.weightKg * when (profile.goal) {
            UserGoal.GAIN_MUSCLE -> PROTEIN_GAIN_PER_KG
            UserGoal.LOSE_WEIGHT -> PROTEIN_LOSS_PER_KG
        }
        val fatGrams = profile.weightKg * when (profile.goal) {
            UserGoal.GAIN_MUSCLE -> FAT_GAIN_PER_KG
            UserGoal.LOSE_WEIGHT -> FAT_LOSS_PER_KG
        }
        val carbGrams = max(0.0, (calories - proteinGrams * 4.0 - fatGrams * 9.0) / 4.0)

        return MacroTargets(
            calories = calories.roundToInt().toDouble(),
            proteins = proteinGrams.roundToInt().toDouble(),
            fats = fatGrams.roundToInt().toDouble(),
            carbs = carbGrams.roundToInt().toDouble()
        )
    }

    private companion object {
        const val ACTIVITY_FACTOR = 1.2
        const val CALORIE_SURPLUS = 300.0
        const val CALORIE_DEFICIT = 500.0
        const val PROTEIN_GAIN_PER_KG = 2.0
        const val PROTEIN_LOSS_PER_KG = 1.6
        const val FAT_GAIN_PER_KG = 1.0
        const val FAT_LOSS_PER_KG = 0.8
    }
}
