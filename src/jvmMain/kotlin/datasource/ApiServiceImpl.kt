package datasource

import base.OkHttpClientHolder
import base.json
import datasource.data.*
import datasource.net.HttpResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
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

    override suspend fun getTasks(categoryId: Long): HttpResponse<List<Task>> {
        return withContext(Dispatchers.IO) {
            val request = Request.Builder().url(OkHttpClientHolder.host + "/tasks/${categoryId}").build()
            val response = okHttpClient.newCall(request).execute()
            json.decodeFromString(response.body?.string() ?: "")
        }
    }

    override suspend fun getTaskCategories(): HttpResponse<List<TaskCategory>> {
        return withContext(Dispatchers.IO) {
            val request = Request.Builder().url(OkHttpClientHolder.host + "/tasks_categories").build()
            val response = okHttpClient.newCall(request).execute()
            json.decodeFromString(response.body?.string() ?: "")
        }
    }

    override suspend fun completeTask(id: Long) {
        return withContext(Dispatchers.IO) {
            val url = (OkHttpClientHolder.host + "/api/contentprovider").toHttpUrl().newBuilder()
                .addQueryParameter("url", "lifeup://api/complete?id=${id}&ui=false")
                .build()
            val request = Request.Builder().url(url).build()
            okHttpClient.newCall(request).execute()
        }
    }

    override suspend fun getCoin(): Long {
        return withContext(Dispatchers.IO) {
            val request = Request.Builder().url(OkHttpClientHolder.host + "/coin").build()
            val response = okHttpClient.newCall(request).execute()
            json.decodeFromString<HttpResponse<JsonObject>>(
                response.body?.string() ?: ""
            ).data!!.getValue("value").jsonPrimitive.long
        }
    }

    override suspend fun getSkills(): List<Skill> {
        return withContext(Dispatchers.IO) {
            val request = Request.Builder().url(OkHttpClientHolder.host + "/skills").build()
            val response = okHttpClient.newCall(request).execute()
            json.decodeFromString<HttpResponse<List<Skill>>>(response.body?.string() ?: "").dataOrThrow()
        }
    }

    override suspend fun getAchievementCategories(): List<AchievementCategory> {
        return withContext(Dispatchers.IO) {
            val request = Request.Builder().url(OkHttpClientHolder.host + "/achievement_categories").build()
            val response = okHttpClient.newCall(request).execute()
            json.decodeFromString<HttpResponse<List<AchievementCategory>>>(response.body?.string() ?: "").dataOrThrow()
        }
    }

    override suspend fun getAchievement(categoryId: Long): List<Achievement> {
        return withContext(Dispatchers.IO) {
            val request = Request.Builder().url(OkHttpClientHolder.host + "/achievements/${categoryId}").build()
            val response = okHttpClient.newCall(request).execute()
            json.decodeFromString<HttpResponse<List<Achievement>>>(response.body?.string() ?: "").dataOrThrow()
        }
    }

    override suspend fun getShopItemCategories(): List<ShopCategory> {
        return withContext(Dispatchers.IO) {
            val request = Request.Builder().url(OkHttpClientHolder.host + "/items_categories").build()
            val response = okHttpClient.newCall(request).execute()
            json.decodeFromString<HttpResponse<List<ShopCategory>>>(response.body?.string() ?: "").dataOrThrow()
        }
    }

    override suspend fun getShopItems(categoryId: Long): List<ShopItem> {
        return withContext(Dispatchers.IO) {
            val request = Request.Builder().url(OkHttpClientHolder.host + "/items/${categoryId}").build()
            val response = okHttpClient.newCall(request).execute()
            json.decodeFromString<HttpResponse<List<ShopItem>>>(response.body?.string() ?: "").dataOrThrow()
        }
    }

    override suspend fun getFeelings(offset: Int, limit: Int): List<Feelings> {
        return withContext(Dispatchers.IO) {
            val url = (OkHttpClientHolder.host + "/feelings").toHttpUrl().newBuilder()
                .addQueryParameter("offset", offset.toString())
                .addQueryParameter("limit", limit.toString())
                .build()

            val request = Request.Builder().url(url).build()
            val response = okHttpClient.newCall(request).execute()
            json.decodeFromString<HttpResponse<List<Feelings>>>(response.body?.string() ?: "").dataOrThrow()
        }
    }

    override suspend fun getInfo(): Info {
        return withContext(Dispatchers.IO) {
            val request = Request.Builder().url(OkHttpClientHolder.host + "/info").build()
            val response = okHttpClient.newCall(request).execute()
            json.decodeFromString<HttpResponse<Info>>(response.body?.string() ?: "").dataOrThrow()
        }
    }

    override suspend fun purchaseItem(id: Long?, price: Long, desc: String) {
        return withContext(Dispatchers.IO) {
            val url = (OkHttpClientHolder.host + "/api/contentprovider").toHttpUrl().newBuilder()
                .addQueryParameter("url", "lifeup://api/item?id=${id}&own_number=1&own_number_type=relative")
                .addQueryParameter("url", "lifeup://api/penalty?type=coin&content=${desc}&number=${price}&silent=true")
                .build()
            val request = Request.Builder().url(url).build()
            okHttpClient.newCall(request).execute()
        }
    }

    override fun getIconUrl(icon: String): String {
        if (icon.isEmpty()) {
            return ""
        }
        val host = OkHttpClientHolder.host
        val url = (host).toHttpUrl().newBuilder()
            .addPathSegment("files")
            .addPathSegment(icon)
            .build()
        return url.toString()
    }
}