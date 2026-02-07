package com.kurban.calory.features.barcode.data.remote.mapper

import com.kurban.calory.features.barcode.data.remote.model.OpenFoodFactsNutriments
import com.kurban.calory.features.barcode.data.remote.model.OpenFoodFactsProduct
import com.kurban.calory.features.barcode.domain.model.BarcodeProduct
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.days

class OpenFoodFactsProductMapper {
    
    fun mapToBarcodeProduct(
        barcode: String,
        product: OpenFoodFactsProduct,
        clock: Clock = Clock.System
    ): BarcodeProduct? {
        val name = product.productNameRu?.takeIf { it.isNotBlank() } 
            ?: product.productName?.takeIf { it.isNotBlank() }
            ?: return null
            
        val calories = extractCalories(product.nutriments, product.servingSize)
        val proteins = extractMacronutrient(
            nutriments = product.nutriments,
            servingSize = product.servingSize,
            servingSelector = { it.proteinsServing },
            per100gSelector = { it.proteins100g }
        )
        val fats = extractMacronutrient(
            nutriments = product.nutriments,
            servingSize = product.servingSize,
            servingSelector = { it.fatServing },
            per100gSelector = { it.fat100g }
        )
        val carbs = extractMacronutrient(
            nutriments = product.nutriments,
            servingSize = product.servingSize,
            servingSelector = { it.carbohydratesServing },
            per100gSelector = { it.carbohydrates100g }
        )
        
        val now = clock.now()
        
        return BarcodeProduct(
            id = 0L, // Will be set by database
            barcode = barcode,
            name = name,
            brand = product.brands?.takeIf { it.isNotBlank() },
            calories = calories,
            proteins = proteins,
            fats = fats,
            carbs = carbs,
            servingSize = parseServingSize(product.servingSize) ?: 100,
            imageUrl = product.imageFrontUrl ?: product.imageFrontSmallUrl,
            isFavorite = false,
            cachedAt = now.epochSeconds,
            expiresAt = (now + 7.days).epochSeconds
        )
    }
    
    private fun extractCalories(
        nutriments: OpenFoodFactsNutriments?,
        servingSize: String?
    ): Double {
        if (nutriments == null) return 0.0
        
        // Prefer 100g values for consistency with the rest of the app
        nutriments.energyKcal100g?.let { return it }

        if (!servingSize.isNullOrBlank()) {
            nutriments.energyKcalServing?.let { return it }
        }

        return 0.0
    }
    
    private fun extractMacronutrient(
        nutriments: OpenFoodFactsNutriments?,
        servingSize: String?,
        servingSelector: (OpenFoodFactsNutriments) -> Double?,
        per100gSelector: (OpenFoodFactsNutriments) -> Double?
    ): Double {
        if (nutriments == null) return 0.0
        
        // Prefer 100g values for consistency
        per100gSelector(nutriments)?.let { return it }

        if (!servingSize.isNullOrBlank()) {
            servingSelector(nutriments)?.let { return it }
        }

        return 0.0
    }
    
    private fun parseServingSize(servingSize: String?): Int? {
        if (servingSize == null) return null
        
        // Extract number from strings like "100 g", "250ml", "1 serving (30g)"
        val regex = Regex("""(\d+)""")
        return regex.find(servingSize)?.groupValues?.get(1)?.toIntOrNull()
    }
}
