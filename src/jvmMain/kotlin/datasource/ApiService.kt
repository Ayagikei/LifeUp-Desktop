package datasource

import datasource.data.*
import datasource.net.HttpResponse
import net.lifeupapp.lifeup.api.content.achievements.category.AchievementCategory
import net.lifeupapp.lifeup.api.content.feelings.Feelings
import net.lifeupapp.lifeup.api.content.info.Info
import net.lifeupapp.lifeup.api.content.shop.ShopItem

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
}
