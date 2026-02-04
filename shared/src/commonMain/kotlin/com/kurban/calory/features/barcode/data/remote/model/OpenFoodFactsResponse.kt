package com.kurban.calory.features.barcode.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenFoodFactsResponse(
    val code: String,
    val status: Int,
    val product: OpenFoodFactsProduct? = null
)

@Serializable  
data class OpenFoodFactsProduct(
    @SerialName("product_name")
    val productName: String? = null,
    
    @SerialName("product_name_ru")
    val productNameRu: String? = null,
    
    val brands: String? = null,
    
    val nutriments: OpenFoodFactsNutriments? = null,
    
    @SerialName("image_front_url")
    val imageFrontUrl: String? = null,
    
    @SerialName("image_front_small_url")
    val imageFrontSmallUrl: String? = null,
    
    val quantity: String? = null,
    
    @SerialName("serving_size")
    val servingSize: String? = null
)

@Serializable
data class OpenFoodFactsNutriments(
    @SerialName("energy-kcal_100g")
    val energyKcal100g: Double? = null,
    
    @SerialName("proteins_100g")
    val proteins100g: Double? = null,
    
    @SerialName("fat_100g")
    val fat100g: Double? = null,
    
    @SerialName("carbohydrates_100g")
    val carbohydrates100g: Double? = null,
    
    @SerialName("energy-kcal_serving")
    val energyKcalServing: Double? = null,
    
    @SerialName("proteins_serving")
    val proteinsServing: Double? = null,
    
    @SerialName("fat_serving")
    val fatServing: Double? = null,
    
    @SerialName("carbohydrates_serving")
    val carbohydratesServing: Double? = null
)