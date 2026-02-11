package com.kurban.calory.ios

data class IosFoodDto(
    val id: Long,
    val name: String,
    val grams: Int,
    val calories: Double,
    val proteins: Double,
    val fats: Double,
    val carbs: Double,
)

data class IosTrackedFoodDto(
    val id: Long,
    val foodId: Long,
    val name: String,
    val grams: Int,
    val calories: Double,
    val proteins: Double,
    val fats: Double,
    val carbs: Double,
    val dayId: String,
    val timestamp: Long,
)

data class IosMacroTotalsDto(
    val calories: Double,
    val proteins: Double,
    val fats: Double,
    val carbs: Double,
)

data class IosMacroTargetsDto(
    val calories: Double,
    val proteins: Double,
    val fats: Double,
    val carbs: Double,
)

data class IosUserProfileDto(
    val name: String,
    val sex: String,
    val age: Int,
    val heightCm: Int,
    val weightKg: Double,
    val goal: String,
)

data class IosCustomFoodDto(
    val id: Long,
    val name: String,
    val calories: Double,
    val proteins: Double,
    val fats: Double,
    val carbs: Double,
)

data class IosMainStateDto(
    val dayId: String,
    val trackedFoods: List<IosTrackedFoodDto>,
    val totals: IosMacroTotalsDto,
    val macroTargets: IosMacroTargetsDto?,
    val barcodeAvailable: Boolean,
)
