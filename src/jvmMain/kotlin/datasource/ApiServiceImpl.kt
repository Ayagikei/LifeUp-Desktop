package datasource

import base.OkHttpClientHolder
import base.json
import datasource.data.Achievement
import datasource.data.Skill
import datasource.data.Task
import datasource.data.TaskCategory
import datasource.net.HttpResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long
import net.lifeupapp.lifeup.api.content.achievements.category.AchievementCategory
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
            val url = (OkHttpClientHolder.host + "/api").toHttpUrl().newBuilder()
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

    override fun getIconUrl(icon: String): String {
        val host = OkHttpClientHolder.host
        val url = (host).toHttpUrl().newBuilder()
            .addPathSegment("files")
            .addPathSegment(icon)
            .build()
        return url.toString()
    }
}