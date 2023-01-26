package ui.page.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import ui.AppStore
import ui.page.list.RootStore.RootState

@Composable
fun RootContent(modifier: Modifier = Modifier) {
    val coroutineScope = rememberCoroutineScope()
    val globalStore = AppStore.current
    val model = remember { RootStore(coroutineScope, globalStore) }
    val state = model.state

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
        onRefreshClick = model::onRefresh
    )


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
        EditDialog(
            item = item,
            onCloseClicked = model::onEditorCloseClicked,
            onTextChanged = model::onEditorTextChanged,
            onDoneChanged = model::onEditorDoneChanged,
        )
    }
}

private val RootState.editingItem: TodoItem?
    get() = editingItemId?.let(items::firstById)

private fun List<TodoItem>.firstById(id: Long): TodoItem =
    first { it.id == id }
