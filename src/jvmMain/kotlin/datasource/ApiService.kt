package datasource

import datasource.data.Achievement
import datasource.data.Skill
import datasource.data.Task
import datasource.data.TaskCategory
import datasource.net.HttpResponse
import net.lifeupapp.lifeup.api.content.achievements.category.AchievementCategory

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
}
