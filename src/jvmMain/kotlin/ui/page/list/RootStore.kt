package ui.page.list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import datasource.ApiServiceImpl
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.logging.Level
import java.util.logging.Logger

internal class RootStore {

    init {
        fetchTasks()
    }

    private fun fetchTasks() {
        GlobalScope.launch {
            kotlin.runCatching {
                ApiServiceImpl.getToDoItems()
            }.onSuccess {
                Logger.getLogger("ApiServiceImpl").log(Level.FINE, Json.encodeToString(it))
                val tasks = it.map { TodoItem(it.id ?: 0L, it.name, false) }
                setState {
                    copy(items = tasks)
                }
            }.onFailure {
                Logger.getLogger("ApiServiceImpl").log(Level.SEVERE, it.stackTraceToString())
                delay(2000L)
            }
        }
    }

    var state: RootState by mutableStateOf(initialState())
        private set

    fun onItemClicked(id: Long) {
        setState { copy(editingItemId = id) }
    }

    fun onItemDoneChanged(id: Long, isDone: Boolean) {
        setState {
            updateItem(id = id) {
                it.copy(isDone = isDone)
            }
        }
        if (isDone) {
            GlobalScope.launch {
                kotlin.runCatching {
                    ApiServiceImpl.completeTask(id)
                }.onSuccess {
                    onItemDeleteClicked(id)
                }.onFailure {
                    Logger.getLogger("ApiServiceImpl").log(Level.SEVERE, it.stackTraceToString())
                    onItemDoneChanged(id, false)
                }
            }
        }
    }

    fun onItemDeleteClicked(id: Long) {
        setState { copy(items = items.filterNot { it.id == id }) }
    }

    fun onAddItemClicked() {
        setState {
            val newItem =
                TodoItem(
                    id = items.maxOfOrNull(TodoItem::id)?.plus(1L) ?: 1L,
                    text = inputText,
                )

            copy(items = items + newItem, inputText = "")
        }
    }

    fun onInputTextChanged(text: String) {
        setState { copy(inputText = text) }
    }

    fun onEditorCloseClicked() {
        setState { copy(editingItemId = null) }
    }

    fun onEditorTextChanged(text: String) {
        setState {
            updateItem(id = requireNotNull(editingItemId)) { it.copy(text = text) }
        }
    }

    fun onEditorDoneChanged(isDone: Boolean) {
        setState {
            updateItem(id = requireNotNull(editingItemId)) { it.copy(isDone = isDone) }
        }
    }

    private fun RootState.updateItem(id: Long, transformer: (TodoItem) -> TodoItem): RootState =
        copy(items = items.updateItem(id = id, transformer = transformer))

    private fun List<TodoItem>.updateItem(
        id: Long,
        transformer: (TodoItem) -> TodoItem
    ): List<TodoItem> =
        map { item -> if (item.id == id) transformer(item) else item }

    private fun initialState(): RootState =
        RootState(
            items = (1L..5L).map { id ->
                TodoItem(id = id, text = "Some text $id")
            }
        )

    private inline fun setState(update: RootState.() -> RootState) {
        state = state.update()
    }

    data class RootState(
        val items: List<TodoItem> = emptyList(),
        val inputText: String = "",
        val editingItemId: Long? = null,
    )
}
