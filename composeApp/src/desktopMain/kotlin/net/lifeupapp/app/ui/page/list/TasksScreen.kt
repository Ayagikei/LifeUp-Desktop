package ui.page.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState
import kotlinx.coroutines.Dispatchers
import net.lifeupapp.app.base.launchSafely
import net.lifeupapp.app.ui.AppStore
import net.lifeupapp.app.ui.ScaffoldState
import net.lifeupapp.app.ui.Strings
import net.lifeupapp.app.ui.page.item.ShopStore
import net.lifeupapp.app.ui.page.list.TaskDetailsViewDialog
import net.lifeupapp.app.ui.page.list.TaskStore
import net.lifeupapp.app.ui.page.list.TaskStore.TaskState
import net.lifeupapp.app.ui.page.list.TodoItem
import net.lifeupapp.app.ui.page.list.add.AddTaskScreen
import net.lifeupapp.app.ui.page.status.StatusStore
import java.awt.Toolkit

@Composable
fun TasksScreen(modifier: Modifier = Modifier) {
    val coroutineScope = rememberCoroutineScope()
    val globalStore = AppStore.current
    val model = remember { TaskStore(coroutineScope, globalStore) }
    val state by model.state.collectAsState(Dispatchers.Main)
    val scaffoldState = ScaffoldState.current
    val text = Strings.snackbar_complete_task

    coroutineScope.launchSafely {
        model.completeSuccessEventFlow.collect {
            scaffoldState.snackbarHostState.showSnackbar(text)
        }
    }

    MainContent(
        modifier = modifier,
        items = state.items,
        categoryExpanded = state.categoryExpanded,
        categories = state.categories,
        selectedCategory = state.categories.find { it.id == state.currentCategoryId && it.id != null },
        inputText = state.inputText,
        onItemClicked = model::onItemClicked,
        onItemDoneChanged = model::onItemDoneChanged,
        onItemDeleteClicked = model::onItemDeleteClicked,
        onAddItemClicked = model::onAddItemClicked,
        onInputTextChanged = model::onInputTextChanged,
        onCategoryClicked = model::onCategoryClicked,
        onCategoryExpended = model::onCategoryExpended,
        onCategoryDismissed = model::onCategoryDismissed,
        onRefreshClick = model::onRefresh,
        onAddClicked = {
            model.showAddWindow()
        }
    )

    if (state.showAddWindow) {
        val state = mutableStateOf(state.currentCategoryId)
        showAddDialog(state.value, onCloseAction = {
            model.hideAddWindow()
        }, onCloseAndSuccessAdded = {
            model.hideAddWindow()
            coroutineScope.launchSafely {
                scaffoldState.snackbarHostState.showSnackbar(Strings.getAddTasksSuccess())
            }
            model.onRefresh()
        })
    }

//    Button(onClick = {
//        coroutineScope.launch {
//            val snackbarResult = scaffoldState.snackbarHostState.showSnackbar(
//                message = "This is your message",
//                actionLabel = "Do something"
//            )
//            when (snackbarResult) {
//                SnackbarResult.Dismissed -> TODO()
//                SnackbarResult.ActionPerformed -> TODO()
//            }
//        }
//    }) {
//        Text(text = "Click me!")
//    }

    state.editingItem?.also { item ->
        // we need skill lists
        val statusStore = remember { StatusStore(coroutineScope, globalStore) }
        // we need item list
        val shopStore = remember { ShopStore(coroutineScope, globalStore) }
        val taskStore = remember { TaskStore(coroutineScope, globalStore) }
        val statusState by statusStore.state.collectAsState(Dispatchers.Main)
        val shopState by shopStore.state.collectAsState(Dispatchers.Main)
        val taskStatus by taskStore.state.collectAsState(Dispatchers.Main)

        TaskDetailsViewDialog(
            item = item,
            skills = statusState.skills,
            shopItems = shopState.shopItems,
            taskCategories = taskStatus.categories,
            onCloseClicked = model::onEditorCloseClicked,
        )
    }
}

@Composable
private fun showAddDialog(
    defaultCategoryId: Long?,
    onCloseAction: () -> Unit,
    onCloseAndSuccessAdded: () -> Unit
) {
    val screenSize = Toolkit.getDefaultToolkit().screenSize
    val width = screenSize.width
    val height = screenSize.height
    val windowWidth = if (width > 1920) 1200 else 800
    val windowHeight = if (height > 1080) 900 else 600

    Window(
        onCloseRequest = {
            onCloseAction()
        },
        state = rememberWindowState(
            position = WindowPosition(alignment = Alignment.Center),
            size = DpSize(windowWidth.dp, windowHeight.dp)
        ),
        title = Strings.add_tasks_dialog_title
    ) {
        AddTaskScreen(
            defaultCategoryId = defaultCategoryId ?: 0L,
            addSuccess = onCloseAndSuccessAdded
        )
    }
}

private val TaskState.editingItem: TodoItem?
    get() = editingItemId?.let(items::firstById)

private fun List<TodoItem>.firstById(id: Long): TodoItem =
    first { it.id == id }
