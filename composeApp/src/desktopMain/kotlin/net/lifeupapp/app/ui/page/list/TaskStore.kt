package net.lifeupapp.app.ui.page.list

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import net.lifeupapp.app.base.launchSafely
import net.lifeupapp.app.datasource.ApiServiceImpl
import net.lifeupapp.app.datasource.data.TaskCategory
import net.lifeupapp.app.ui.AppStoreImpl
import net.lifeupapp.app.utils.logger

internal class TaskStore(
    private val coroutineScope: CoroutineScope,
    private val globalStore: AppStoreImpl
) {

    private val log = this.logger

    init {
        fetchCategories()
    }

    private val completeSuccessEvent = Channel<Unit>()
    val completeSuccessEventFlow = completeSuccessEvent.receiveAsFlow()

    private val _subTaskLoadingId = MutableStateFlow<Long?>(null)
    val subTaskLoadingId = _subTaskLoadingId.asStateFlow()

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
                log.error(it) {
                    "fetchCategories"
                }
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
                            // 更新编辑状态中的任务
                            val updatedEditingState = editingState?.let { editState ->
                                tasks.firstOrNull { it.id == editState.taskId }?.let { updatedTask ->
                                    editState.copy(task = updatedTask)
                                }
                            }
                            copy(
                                items = tasks,
                                editingState = updatedEditingState
                            )
                        }
                    }
                }.onFailure {
                    log.error(it) {
                        "fetchTasks"
                    }
                    delay(2000L)
                }
            }
        }
    }

    val state = MutableStateFlow<TaskState>(initialState())

    fun onItemClicked(id: Long) {
        val item = state.value.items.firstOrNull { it.id == id } ?: return
        setState {
            copy(editingState = EditingTaskState(id, item))
        }
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
                    log.error(it) {
                        "completeTask"
                    }
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
        setState { copy(editingState = null) }
    }

    fun onEditorTextChanged(text: String) {
        setState {
            updateItem(id = requireNotNull(editingState?.taskId)) { it.copy(text = text) }
        }
    }

    fun onEditorDoneChanged(isDone: Boolean) {
        setState {
            updateItem(id = requireNotNull(editingState?.taskId)) { it.copy(isDone = isDone) }
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

    fun onSubTaskStatusChanged(mainTaskId: Long, subTaskId: Long, isComplete: Boolean) {
        coroutineScope.launch {
            _subTaskLoadingId.value = subTaskId
            runCatching {
                val operation = if (isComplete) "complete" else "undo_complete"
                ApiServiceImpl.callApi(
                    "subtask_operation",
                    listOf(
                        "main_id" to mainTaskId.toString(),
                        "edit_id" to subTaskId.toString(),
                        "operation" to operation
                    )
                )
            }.onSuccess {
                fetchTasks()
            }.onFailure {
                log.error(it) {
                    "onSubTaskStatusChanged"
                }
            }
            _subTaskLoadingId.value = null
        }
    }

    data class EditingTaskState(
        val taskId: Long,
        val task: TodoItem
    )

    data class TaskState(
        val categories: List<TaskCategory> = emptyList(),
        val currentCategoryId: Long? = null,
        val categoryExpanded: Boolean = false,
        val items: List<TodoItem> = emptyList(),
        val inputText: String = "",
        val editingState: EditingTaskState? = null,
        val snackbarText: String? = null,
        val showAddWindow: Boolean = false,
    )
}
