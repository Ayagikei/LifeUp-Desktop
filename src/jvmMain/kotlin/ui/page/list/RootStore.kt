package ui.page.list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import base.launchSafely
import datasource.ApiServiceImpl
import datasource.data.TaskCategory
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import logger
import ui.AppStoreImpl
import java.util.logging.Level
import java.util.logging.Logger

internal class RootStore(
    private val coroutineScope: CoroutineScope,
    private val globalStore: AppStoreImpl
) {

    init {
        fetchCategories()
    }

    private val completeSuccessEvent = Channel<Unit>()
    val completeSuccessEventFlow = completeSuccessEvent.receiveAsFlow()

    private fun fetchCategories() {
        coroutineScope.launchSafely(Dispatchers.IO) {
            while (globalStore.isReadyToCall.not()) {
                delay(1000L)
            }
            kotlin.runCatching {
                ApiServiceImpl.getTaskCategories()
            }.onSuccess {
                it.onSuccess {
                    if (state.currentCategoryId == null || state.currentCategoryId !in (it?.map { it.id }
                            ?: emptyList())) {
                        setState {
                            copy(categories = it ?: emptyList(), currentCategoryId = it?.firstOrNull()?.id)
                        }
                    }
                    fetchTasks()
                }
            }.onFailure {
                logger.log(Level.SEVERE, it.stackTraceToString())
                delay(2000L)
            }
        }
    }

    private fun fetchTasks() {
        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                val currentCategoryId = state.currentCategoryId ?: return@withContext
                kotlin.runCatching {
                    ApiServiceImpl.getTasks(currentCategoryId)
                }.onSuccess {
                    it.onSuccess {
                        val tasks = it?.filterNot {
                            // drop the not started tasks
                            it.startTime >= System.currentTimeMillis()
                        }?.map { TodoItem(it.id ?: 0L, it.nameExtended, it, false) } ?: emptyList()
                        setState {
                            copy(items = tasks)
                        }
                    }
                }.onFailure {
                    logger.log(Level.SEVERE, it.stackTraceToString())
                    delay(2000L)
                }
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
            coroutineScope.launch {
                kotlin.runCatching {
                    ApiServiceImpl.completeTask(id)
                }.onSuccess {
                    completeSuccessEvent.send(Unit)
                    delay(500)
                    fetchTasks()
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
//        setState {
//            val newItem =
//                TodoItem(
//                    id = items.maxOfOrNull(TodoItem::id)?.plus(1L) ?: 1L,
//                    text = inputText,
//                )
//
//            copy(items = items + newItem, inputText = "")
//        }
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
            items = emptyList()
        )

    private inline fun setState(update: RootState.() -> RootState) {
        state = state.update()
    }

    fun onCategoryClicked(id: Long) {
        setState {
            copy(categoryExpanded = false, currentCategoryId = id, items = emptyList())
        }
        fetchTasks()
    }

    fun onCategoryExpended() {
        setState {
            copy(categoryExpanded = true)
        }
    }

    fun onCategoryDismissed() {
        setState {
            copy(categoryExpanded = false)
        }
    }

    fun onRefresh() {
        fetchCategories()
    }


    data class RootState(
        val categories: List<TaskCategory> = emptyList(),
        val currentCategoryId: Long? = null,
        val categoryExpanded: Boolean = false,
        val items: List<TodoItem> = emptyList(),
        val inputText: String = "",
        val editingItemId: Long? = null,
        val snackbarText: String? = null,
    )
}
