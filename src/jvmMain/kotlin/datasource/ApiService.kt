package datasource

import datasource.data.Task

interface ApiService {

    companion object {
        val instance: ApiService
            get() = ApiServiceImpl
    }

    suspend fun getAppInfo(): List<Task>

    suspend fun getToDoItems(): List<Task>

    suspend fun completeTask(id: Long)

    suspend fun getCoin(): Long
}
