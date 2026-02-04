package com.kurban.calory.features.barcode.data.remote.api

import com.kurban.calory.features.barcode.data.remote.model.OpenFoodFactsResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class OpenFoodFactsApi(
    private val httpClient: HttpClient = createDefaultClient()
) {
    
    suspend fun getProductByBarcode(barcode: String): Result<OpenFoodFactsResponse> {
        return try {
            val response = httpClient.get("https://world.openfoodfacts.org/api/v2/product/$barcode.json") {
                url {
                    protocol = URLProtocol.HTTPS
                    parameters.append("fields", "product_name,product_name_ru,brands,nutriments,image_front_url,image_front_small_url,quantity,serving_size")
                }
            }
            Result.success(response.body())
        } catch (e: Exception) {
            val errorMessage = if (e.message?.contains("404") == true) {
                "Product not found for barcode: $barcode"
            } else {
                "API error: ${e.message}"
            }
            Result.failure(Exception(errorMessage, e))
        }
    }
    
    companion object {
        private fun createDefaultClient(): HttpClient {
            return HttpClient {
                install(ContentNegotiation) {
                    json(Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                    })
                }
            }
        }
    }
}