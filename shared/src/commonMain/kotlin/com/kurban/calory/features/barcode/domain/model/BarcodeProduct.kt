package com.kurban.calory.features.barcode.domain.model

data class BarcodeProduct(
    val id: Long? = null,
    val barcode: String,
    val name: String,
    val brand: String? = null,
    val calories: Double,
    val proteins: Double,
    val fats: Double,
    val carbs: Double,
    val servingSize: Int,
    val imageUrl: String? = null,
    val isFavorite: Boolean = false,
    val cachedAt: Long,
    val expiresAt: Long
) {
    val macroNutrients: MacroNutrients
        get() = MacroNutrients(
            calories = calories,
            proteins = proteins,
            fats = fats,
            carbs = carbs
        )
}

data class MacroNutrients(
    val calories: Double,
    val proteins: Double,
    val fats: Double,
    val carbs: Double
)