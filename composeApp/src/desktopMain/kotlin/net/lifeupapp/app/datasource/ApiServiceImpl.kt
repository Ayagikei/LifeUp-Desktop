package datasource

import base.OkHttpClientHolder
import base.json
import datasource.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long
import logger
import net.lifeupapp.app.datasource.data.Feelings
import net.lifeupapp.app.datasource.net.HttpResponse
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.*
import java.util.logging.Level

object ApiServiceImpl : ApiService {

    private val okHttpClient
        get() = OkHttpClientHolder.okHttpClient

    override suspend fun getAppInfo(): List<Task> {
        TODO()
    }

    override suspend fun getTasks(categoryId: Long): HttpResponse<List<Task>> {
        return withContext(Dispatchers.IO) {
            val request =
                Request.Builder().url(OkHttpClientHolder.host + "/tasks/${categoryId}").build()
            val response = okHttpClient.newCall(request).execute()
            json.decodeFromString(response.body?.string() ?: "")
        }
    }

    override suspend fun getTaskCategories(): HttpResponse<List<TaskCategory>> {
        return withContext(Dispatchers.IO) {
            val request =
                Request.Builder().url(OkHttpClientHolder.host + "/tasks_categories").build()
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
            json.decodeFromString<HttpResponse<List<Skill>>>(response.body?.string() ?: "")
                .dataOrThrow()
        }
    }

    override suspend fun getAchievementCategories(): List<AchievementCategory> {
        return withContext(Dispatchers.IO) {
            val request =
                Request.Builder().url(OkHttpClientHolder.host + "/achievement_categories").build()
            val response = okHttpClient.newCall(request).execute()
            json.decodeFromString<HttpResponse<List<AchievementCategory>>>(
                response.body?.string() ?: ""
            ).dataOrThrow()
        }
    }

    override suspend fun getAchievement(categoryId: Long): List<Achievement> {
        return withContext(Dispatchers.IO) {
            val request =
                Request.Builder().url(OkHttpClientHolder.host + "/achievements/${categoryId}")
                    .build()
            val response = okHttpClient.newCall(request).execute()
            json.decodeFromString<HttpResponse<List<Achievement>>>(response.body?.string() ?: "")
                .dataOrThrow()
        }
    }

    override suspend fun getShopItemCategories(): List<ShopCategory> {
        return withContext(Dispatchers.IO) {
            val request =
                Request.Builder().url(OkHttpClientHolder.host + "/items_categories").build()
            val response = okHttpClient.newCall(request).execute()
            json.decodeFromString<HttpResponse<List<ShopCategory>>>(response.body?.string() ?: "")
                .dataOrThrow()
        }
    }

    override suspend fun getShopItems(categoryId: Long): List<ShopItem> {
        return withContext(Dispatchers.IO) {
            val request =
                Request.Builder().url(OkHttpClientHolder.host + "/items/${categoryId}").build()
            val response = okHttpClient.newCall(request).execute()
            json.decodeFromString<HttpResponse<List<ShopItem>>>(response.body?.string() ?: "")
                .dataOrThrow()
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
            json.decodeFromString<HttpResponse<List<Feelings>>>(response.body?.string() ?: "")
                .dataOrThrow()
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
                .addQueryParameter(
                    "url",
                    "lifeup://api/item?id=${id}&own_number=1&own_number_type=relative"
                )
                .addQueryParameter(
                    "url",
                    "lifeup://api/penalty?type=coin&content=${desc}&number=${price}&silent=true"
                )
                .build()
            val request = Request.Builder().url(url).build()
            okHttpClient.newCall(request).execute()
        }
    }

    override suspend fun rawCall(api: String): JsonElement? {
        return withContext(Dispatchers.IO) {
            val url = (OkHttpClientHolder.host + "/api/contentprovider").toHttpUrl().newBuilder()
                .addQueryParameter("url", api)
                .build()
            val request = Request.Builder().url(url).build()
            val response = okHttpClient.newCall(request).execute()
            json.decodeFromString<HttpResponse<JsonElement>>(response.body?.string() ?: "")
                .successOrThrow()
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

    @Serializable
    data class UpdateInfoMap(
        val versionCode: Int,
        val downloadUrl: String?,
        val localeInfo: Map<String, UpdateInfo>
    )

    @Serializable
    data class UpdateInfo(
        val versionName: String?,
        val downloadUrl: String?,
        val releaseNotes: String?,
        val downloadWebsite: String?
    )

    data class LocalizedUpdateInfo(
        val versionCode: Int,
        val versionName: String?,
        val downloadUrl: String?,
        val releaseNotes: String?,
        val downloadWebsite: String?
    )

    private const val UPDATE_URL = "http://cdn.lifeupapp.fun/version/version.json"


    override suspend fun checkUpdate(): LocalizedUpdateInfo? {
        return withContext(Dispatchers.IO) {
            val request = Request.Builder().url(UPDATE_URL).build()
            val response = try {
                okHttpClient.newCall(request).execute()
            } catch (ignore: Exception) {
                // failed to check update
                return@withContext null
            }
            if (response.isSuccessful) {
                val jsonText = response.body?.string()
                jsonText?.let {
                    val updateInfo = json.decodeFromString<UpdateInfoMap>(it)
                    val locale = Locale.getDefault()
                    val bestMatchedUpdateInfo =
                        updateInfo.localeInfo["${locale.language.lowercase()}_${locale.country.lowercase()}"]
                            ?: updateInfo.localeInfo[locale.language.lowercase()]
                            ?: updateInfo.localeInfo["en"]

                    return@let LocalizedUpdateInfo(
                        versionCode = updateInfo.versionCode,
                        downloadUrl = updateInfo.downloadUrl,
                        versionName = bestMatchedUpdateInfo?.versionName ?: "",
                        releaseNotes = bestMatchedUpdateInfo?.releaseNotes ?: "",
                        downloadWebsite = bestMatchedUpdateInfo?.downloadWebsite ?: ""
                    )
                }
            } else {
                null
            }
        }
    }

    fun buildLifeUpUrl(api: String, params: List<Pair<String, String>>): String {
        val lifeUpUrlBuilder = HttpUrl.Builder()
            .scheme("http")
            .host("api")
            .addPathSegment(api)

        params.forEach { (key, value) ->
            lifeUpUrlBuilder.addQueryParameter(key, value)
        }

        return lifeUpUrlBuilder.build().toString().replaceFirst("http", "lifeup")
    }

    suspend fun callApi(
        api: String,
        params: List<Pair<String, String>> = emptyList()
    ): Result<JsonElement?> {
        return withContext(Dispatchers.IO) {
            try {
                val lifeUpUrl = buildLifeUpUrl(api, params)
                this@ApiServiceImpl.logger.log(Level.INFO, "Calling API: $lifeUpUrl")
                val url =
                    (OkHttpClientHolder.host + "/api/contentprovider").toHttpUrl().newBuilder()
                        .addQueryParameter("url", lifeUpUrl)
                        .build()

                val request = Request.Builder().url(url).build()
                val response = okHttpClient.newCall(request).execute()
                val responseBody =
                    response.body?.string() ?: throw IllegalStateException("Empty response body")

                return@withContext Result.success(
                    json.decodeFromString<HttpResponse<JsonElement>>(
                        responseBody
                    ).successOrThrow()
                )
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun createOrUpdateFeeling(
        id: Long?,
        content: String?,
        time: Long?,
        isFavorite: Boolean?,
        relateType: Int?,
        relateId: Long?,
        imageUris: List<String>?
    ): Result<JsonElement?> {
        val params = arrayListOf<Pair<String, String>>()

        id?.let { params.add("id" to it.toString()) }
        content?.let { params.add("content" to it) }
        time?.let { params.add("time" to it.toString()) }
        isFavorite?.let { params.add("is_favorite" to it.toString()) }
        relateType?.let { params.add("relate_type" to it.toString()) }
        relateId?.let { params.add("relate_id" to it.toString()) }
        imageUris?.let { uris ->
            uris.forEachIndexed { index, uri ->
                params.add("image_uris" to uri)
            }
        }

        return callApi("feeling", params)
    }

    override suspend fun uploadFilesToUris(files: List<File>): List<String> {
        return withContext(Dispatchers.IO) {
            val url = (OkHttpClientHolder.host + "/files/upload").toHttpUrl()
            val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
            files.forEach {
                builder.addFormDataPart(
                    "file",
                    it.name,
                    it.asRequestBody("application/octet-stream".toMediaType())
                )
            }
            val request = Request.Builder().url(url).post(builder.build()).build()
            val response = okHttpClient.newCall(request).execute()
            json.decodeFromString<HttpResponse<List<String>>>(response.body?.string() ?: "")
                .dataOrThrow()
        }
    }

    override suspend fun exportDataToDir(withMedia: Boolean): String {
        return withContext(Dispatchers.IO) {
            val url = (OkHttpClientHolder.host + "/data/export").toHttpUrl().newBuilder()
                .addQueryParameter(
                    "withMedia",
                    withMedia.toString()
                ).build()
            val request = Request.Builder().url(url).get().build()
            val response = okHttpClient.newCall(request).execute()
            val data =
                json.decodeFromString<HttpResponse<JsonObject>>(response.body?.string() ?: "")
                    .dataOrThrow()
            // 从 JsonElement 获取 “backup_file_uri” string 类型
            val backupFileUri = data.getValue("backup_file_uri").jsonPrimitive.content
            if (backupFileUri.isBlank()) {
                throw IllegalStateException("backup_file_uri is null or blank")
            } else {
                return@withContext getIconUrl(backupFileUri)
            }
        }
    }

    override suspend fun importData(file: File) {
        withContext(Dispatchers.IO) {
            val url = (OkHttpClientHolder.host + "/data/import").toHttpUrl()
            val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
            file.let {
                builder.addFormDataPart(
                    "file",
                    it.name,
                    it.asRequestBody("application/octet-stream".toMediaType())
                )
            }
            val request = Request.Builder().url(url).post(builder.build()).build()
            val response = okHttpClient.newCall(request).execute()
            json.decodeFromString<HttpResponse<JsonElement?>>(response.body?.string() ?: "")
                .successOrThrow()
        }
    }
}