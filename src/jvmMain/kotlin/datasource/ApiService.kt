package datasource

import datasource.data.Task
import ui.list.TodoItem

interface ApiService {
    suspend fun getToDoItems(): List<Task>

    suspend fun completeTask(id: Long)
}