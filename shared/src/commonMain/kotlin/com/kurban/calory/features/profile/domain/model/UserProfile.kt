package com.kurban.calory.features.profile.domain.model

data class UserProfile(
    val name: String,
    val sex: UserSex,
    val age: Int,
    val heightCm: Int,
    val weightKg: Double,
    val goal: UserGoal
)

enum class UserSex {
    MALE,
    FEMALE
}

enum class UserGoal {
    GAIN_MUSCLE,
    LOSE_WEIGHT
}
