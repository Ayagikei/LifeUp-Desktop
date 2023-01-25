package datasource

import datasource.data.Task
import datasource.net.HttpResponse
import net.lifeupapp.lifeup.api.content.tasks.category.TaskCategory

interface ApiService {

    companion object {
        val instance: ApiService
            get() = ApiServiceImpl
    }

    suspend fun getAppInfo(): List<Task>

    suspend fun getTasks(categoryId: Long): HttpResponse<List<Task>>

    suspend fun getTaskCategories(): HttpResponse<List<TaskCategory>>

    suspend fun completeTask(id: Long)

    suspend fun getCoin(): Long
}
