package ui.page.list

import datasource.data.Task
import kotlinx.serialization.Serializable

@Serializable
data class TodoItem(
    val id: Long = 0L,
    val text: String = "",
    val task: Task,
    val isDone: Boolean = false
)
