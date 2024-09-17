package datasource

import datasource.data.*
import kotlinx.serialization.json.JsonElement
import net.lifeupapp.app.datasource.data.Feelings
import net.lifeupapp.app.datasource.net.HttpResponse
import java.io.File

interface ApiService {

    companion object {
        val instance: ApiService
            get() = ApiServiceImpl
    }

    suspend fun getAppInfo(): List<Task>

    // TODO: throw http error
    suspend fun getTasks(categoryId: Long): HttpResponse<List<Task>>

    // TODO: throw http error
    suspend fun getTaskCategories(): HttpResponse<List<TaskCategory>>

    suspend fun completeTask(id: Long)

    suspend fun getCoin(): Long

    suspend fun getSkills(): List<Skill>

    fun getIconUrl(icon: String): String

    suspend fun getAchievementCategories(): List<AchievementCategory>

    suspend fun getAchievement(categoryId: Long): List<Achievement>

    suspend fun getShopItemCategories(): List<ShopCategory>

    suspend fun getShopItems(categoryId: Long): List<ShopItem>

    suspend fun getFeelings(offset: Int, limit: Int): List<Feelings>

    suspend fun getInfo(): Info

    suspend fun rawCall(api: String): JsonElement?

    suspend fun purchaseItem(id: Long?, price: Long, desc: String)

    suspend fun checkUpdate(): ApiServiceImpl.LocalizedUpdateInfo?

    suspend fun createOrUpdateFeeling(
        id: Long? = null,
        content: String? = null,
        time: Long? = null,
        isFavorite: Boolean? = null,
        relateType: Int? = null,
        relateId: Long? = null,
        imageUris: List<String>? = null
    ): Result<JsonElement?>

    suspend fun uploadFilesToUris(files: List<File>): List<String>
}
