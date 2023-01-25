package ui.page.list

import kotlinx.serialization.Serializable

@Serializable
data class TodoItem(
    val id: Long = 0L,
    val text: String = "",
    val isDone: Boolean = false
)
