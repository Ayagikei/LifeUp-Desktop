package net.lifeupapp.app.ui.page.list

import kotlinx.serialization.Serializable
import net.lifeupapp.app.datasource.data.Task

@Serializable
data class TodoItem(
    val id: Long = 0L,
    val text: String = "",
    val task: Task,
    val isDone: Boolean = false,
    val detailed: Task
)
