package datasource

import base.OkHttpClientHolder
import datasource.data.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Request

object ApiServiceImpl : ApiService {

    private val okHttpClient
        get() = OkHttpClientHolder.okHttpClient

    override suspend fun getAppInfo(): List<Task> {
        TODO()
    }

    override suspend fun getToDoItems(): List<Task> {
        return withContext(Dispatchers.IO) {
            val request = Request.Builder().url(OkHttpClientHolder.host + "/tasks").build()
            val response = okHttpClient.newCall(request).execute()
            Json.decodeFromString(response.body?.string() ?: "")
        }
    }

    override suspend fun completeTask(id: Long) {
        return withContext(Dispatchers.IO) {
            val url = (OkHttpClientHolder.host + "/api").toHttpUrl().newBuilder()
                .addQueryParameter("url", "lifeup://api/complete?id=${id}&ui=true")
                .build()
            val request = Request.Builder().url(url).build()
            val response = okHttpClient.newCall(request).execute()
        }
    }

    override suspend fun getCoin(): Long {
        return withContext(Dispatchers.IO) {
            val request = Request.Builder().url(OkHttpClientHolder.host + "/coin").build()
            val response = okHttpClient.newCall(request).execute()
            Json.decodeFromString<JsonObject>(response.body?.string() ?: "").getValue("value").jsonPrimitive.long
        }
    }
}