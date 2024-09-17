package net.lifeupapp.app.ui.page.list

import datasource.ApiServiceImpl
import datasource.data.TaskCategory
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import logger
import net.lifeupapp.app.base.launchSafely
import ui.AppStoreImpl
import ui.page.list.TodoItem
import java.util.logging.Level
import java.util.logging.Logger

internal class TaskStore(
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
            runCatching {
                ApiServiceImpl.getTaskCategories()
            }.onSuccess {
                it.onSuccess {
                    if (state.value.currentCategoryId == null || state.value.currentCategoryId !in (it?.map { it.id }
                            ?: emptyList())) {
                        setState {
                            copy(categories = it?.filter {
                                it.isNormalList() && it.isNotArchived()
                            }?.sortedBy { it.order } ?: emptyList(),
                                currentCategoryId = it?.firstOrNull()?.id)
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
                val currentCategoryId = state.value.currentCategoryId ?: return@withContext
                runCatching {
                    ApiServiceImpl.getTasks(currentCategoryId)
                }.onSuccess {
                    it.onSuccess {
                        val tasks = it?.filterNot {
                            // drop the not started tasks
                            it.startTime >= System.currentTimeMillis()
                        }?.map { TodoItem(it.id ?: 0L, it.nameExtended, it, false, it) }
                            ?: emptyList()
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

    val state = MutableStateFlow<TaskState>(initialState())

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
                runCatching {
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

    private fun TaskState.updateItem(id: Long, transformer: (TodoItem) -> TodoItem): TaskState =
        copy(items = items.updateItem(id = id, transformer = transformer))

    private fun List<TodoItem>.updateItem(
        id: Long,
        transformer: (TodoItem) -> TodoItem
    ): List<TodoItem> =
        map { item -> if (item.id == id) transformer(item) else item }

    private fun initialState(): TaskState =
        TaskState(
            items = emptyList()
        )

    private inline fun setState(update: TaskState.() -> TaskState) {
        state.value = state.value.update()
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

    fun showAddWindow() {
        setState {
            copy(showAddWindow = true)
        }
    }

    fun hideAddWindow() {
        setState {
            copy(showAddWindow = false)
        }
    }


    data class TaskState(
        val categories: List<TaskCategory> = emptyList(),
        val currentCategoryId: Long? = null,
        val categoryExpanded: Boolean = false,
        val items: List<TodoItem> = emptyList(),
        val inputText: String = "",
        val editingItemId: Long? = null,
        val snackbarText: String? = null,
        val showAddWindow: Boolean = false,
    )
}
