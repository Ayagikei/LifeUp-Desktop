package net.lifeupapp.app.datasource.net

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.jsonPrimitive

data class ApiResult(
    val rawData: Map<String, JsonElement>
) {
    // 获取 api_result 的便捷方法
    val apiResult: Boolean
        get() = rawData["api_result"]?.jsonPrimitive?.boolean ?: false

    // 通用的获取值方法
    fun getValue(key: String): JsonElement? = rawData[key]

    // 直接获取特定类型的值
    fun <T> getValueAs(key: String, transform: (JsonElement) -> T): T? {
        return rawData[key]?.let(transform)
    }

    // 重写 toString 方便调试
    override fun toString(): String = rawData.toString()
}
