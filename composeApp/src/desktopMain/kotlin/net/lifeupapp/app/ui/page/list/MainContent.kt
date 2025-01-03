package ui.page.list

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Checkbox
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import datasource.data.TaskCategory
import net.lifeupapp.app.ui.page.list.TodoItem
import net.lifeupapp.app.ui.text.StringText

@Composable
internal fun MainContent(
    modifier: Modifier = Modifier,
    categoryExpanded: Boolean,
    categories: List<TaskCategory>,
    selectedCategory: TaskCategory?,
    items: List<TodoItem>,
    inputText: String,
    onItemClicked: (id: Long) -> Unit,
    onItemDoneChanged: (id: Long, isDone: Boolean) -> Unit,
    onItemDeleteClicked: (id: Long) -> Unit,
    onAddItemClicked: () -> Unit,
    onInputTextChanged: (String) -> Unit,
    onCategoryClicked: (Long) -> Unit,
    onCategoryExpended: () -> Unit,
    onCategoryDismissed: () -> Unit,
    onRefreshClick: () -> Unit,
    onAddClicked: () -> Unit
) {

    Column(modifier) {
        TopAppBar(title = {
            Row(
                modifier = Modifier.clickable(
                    onClick =
                    onCategoryExpended
                )
            ) {
                if (selectedCategory == null) {
                    Text(text = StringText.module_tasks)
                    Icon(Icons.Default.ArrowDropDown, "")
                } else {
                    Text(text = selectedCategory.name)
                    Icon(Icons.Default.ArrowDropDown, "")
                }
            }
            DropdownMenu(
                expanded = categoryExpanded,
                onDismissRequest = onCategoryDismissed,
                modifier = Modifier.wrapContentSize()
            ) {
                categories.forEachIndexed { index, s ->
                    DropdownMenuItem(onClick = {
                        onCategoryClicked(s.id ?: return@DropdownMenuItem)
                    }) {
                        Text(text = s.name)
                    }
                }
            }
        }, backgroundColor = MaterialTheme.colors.primarySurface, elevation = 0.dp, actions = {
            IconButton(onAddClicked) {
                Icon(Icons.Default.Add, "Add")
            }
            IconButton(onRefreshClick) {
                Icon(Icons.Default.Refresh, "Refresh")
            }
        })

        Box(Modifier.weight(1F)) {
            ListContent(
                items = items,
                onItemClicked = onItemClicked,
                onItemDoneChanged = onItemDoneChanged,
                onItemDeleteClicked = onItemDeleteClicked
            )
        }

//        Input(
//            text = inputText,
//            onAddClicked = onAddItemClicked,
//            onTextChanged = onInputTextChanged
//        )
    }
}

@Composable
private fun ListContent(
    items: List<TodoItem>,
    onItemClicked: (id: Long) -> Unit,
    onItemDoneChanged: (id: Long, isDone: Boolean) -> Unit,
    onItemDeleteClicked: (id: Long) -> Unit,
) {
    Box {
        val listState = rememberLazyListState()

        LazyColumn(state = listState) {
            items(items) { item ->
                Item(
                    item = item,
                    onClicked = { onItemClicked(item.id) },
                    onDoneChanged = { onItemDoneChanged(item.id, it) },
                    onDeleteClicked = { onItemDeleteClicked(item.id) }
                )

                Divider()
            }
        }

        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            adapter = rememberScrollbarAdapter(scrollState = listState)
        )
    }
}

@Composable
private fun Item(
    item: TodoItem,
    onClicked: () -> Unit,
    onDoneChanged: (Boolean) -> Unit,
    onDeleteClicked: () -> Unit
) {
    Row(modifier = Modifier.clickable(onClick = onClicked)) {
        Spacer(modifier = Modifier.width(8.dp))

        Checkbox(
            checked = item.isDone,
            modifier = Modifier.align(Alignment.CenterVertically),
            onCheckedChange = onDoneChanged,
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = AnnotatedString(item.text),
            modifier = Modifier.weight(1F).align(Alignment.CenterVertically),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.width(8.dp))

//        IconButton(onClick = onDeleteClicked) {
//            Icon(
//                imageVector = Icons.Default.Delete,
//                contentDescription = null
//            )
//        }

        Spacer(modifier = Modifier.width(MARGIN_SCROLLBAR))
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun Input(
    text: String,
    onTextChanged: (String) -> Unit,
    onAddClicked: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)) {
        OutlinedTextField(
            value = text,
            modifier = Modifier
                .weight(weight = 1F)
                .onKeyEvent {
                    if (it.key == Key.Enter) {
                        onAddClicked()
                        return@onKeyEvent true
                    }
                    return@onKeyEvent false
                },
            // .onKeyUp(key = Key.Enter, action = onAddClicked),
            onValueChange = onTextChanged,
            label = { Text(text = "Add a todo") }
        )

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(onClick = onAddClicked) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null
            )
        }
    }
}
