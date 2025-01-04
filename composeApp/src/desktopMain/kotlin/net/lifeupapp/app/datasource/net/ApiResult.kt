package net.lifeupapp.app.datasource.net

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.jsonPrimitive

// Data class representing the API result
data class ApiResult(
    val rawData: Map<String, JsonElement>
) {
    // Convenient method to get the api_result value
    val apiResult: Boolean
        get() = rawData["api_result"]?.jsonPrimitive?.boolean ?: false

    // General method to retrieve a value by key
    fun getValue(key: String): JsonElement? = rawData[key]

    // Method to directly get a value of a specific type
    fun <T> getValueAs(key: String, transform: (JsonElement) -> T): T? {
        return rawData[key]?.let(transform)
    }

    // Override toString for easier debugging
    override fun toString(): String = rawData.toString()
}
