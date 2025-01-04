package net.lifeupapp.app.ui.page.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import lifeupdesktop.composeapp.generated.resources.*
import net.lifeupapp.app.datasource.data.TaskCategory
import net.lifeupapp.app.ui.text.StringText
import net.lifeupapp.app.ui.view.PageState
import net.lifeupapp.app.ui.view.StateLayout
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun TaskContent(
    modifier: Modifier = Modifier,
    categoryExpanded: Boolean,
    categories: List<TaskCategory>,
    selectedCategory: TaskCategory?,
    items: List<TodoItem>,
    onItemClicked: (id: Long) -> Unit,
    onItemDoneChanged: (id: Long, isDone: Boolean) -> Unit,
    onCategoryClicked: (Long) -> Unit,
    onCategoryExpended: () -> Unit,
    onCategoryDismissed: () -> Unit,
    onRefreshClick: () -> Unit,
    onAddClicked: () -> Unit,
    pageState: PageState
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
            StateLayout(
                state = pageState,
                emptyText = "暂无任务 📝\n点击右上角添加新任务吧！",
                modifier = Modifier.fillMaxSize()
            ) {
                TaskListContent(
                    items = items,
                    onItemClicked = onItemClicked,
                    onItemDoneChanged = onItemDoneChanged
                )
            }
        }
    }
}

@Composable
private fun TaskListContent(
    items: List<TodoItem>,
    onItemClicked: (id: Long) -> Unit,
    onItemDoneChanged: (id: Long, isDone: Boolean) -> Unit
) {
    Box {
        val listState = rememberLazyListState()

        LazyColumn(state = listState) {
            items(items) { item ->
                Task(
                    item = item,
                    onClicked = { onItemClicked(item.id) },
                    onDoneChanged = { onItemDoneChanged(item.id, it) },
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
private fun Task(
    item: TodoItem,
    onClicked: () -> Unit,
    onDoneChanged: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClicked)
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            // Checkbox 列
            Box(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .width(32.dp)
                    .padding(top = 2.dp)  // 微调 checkbox 位置
            ) {
                Checkbox(
                    checked = item.isDone,
                    onCheckedChange = onDoneChanged,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // 主要内容列
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp)
            ) {
                // 任务标题
                Text(
                    text = item.text,
                    style = MaterialTheme.typography.subtitle1.copy(
                        textDecoration = if (item.isDone) TextDecoration.LineThrough else TextDecoration.None
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // 备注（如果有）
                if (item.task.notes.isNotBlank()) {
                    Text(
                        text = item.task.notes,
                        style = MaterialTheme.typography.body2,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                // 子任务列表（如果有）
                if (item.task.subTasks.isNotEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        item.task.subTasks.take(3).forEach { subTask ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp, top = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(
                                            color = if (subTask.status == 1)
                                                MaterialTheme.colors.primary.copy(alpha = 0.6f)
                                            else
                                                MaterialTheme.colors.onSurface.copy(alpha = 0.3f),
                                            shape = CircleShape
                                        )
                                )
                                Text(
                                    text = subTask.todo,
                                    style = MaterialTheme.typography.body2,
                                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier
                                        .padding(start = 8.dp)
                                        .weight(1f),
                                    textDecoration = if (subTask.status == 1)
                                        TextDecoration.LineThrough
                                    else
                                        TextDecoration.None
                                )
                            }
                        }

                        // 优化子任务剩余数量显示
                        if (item.task.subTasks.size > 3) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp, top = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(
                                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.15f),
                                            shape = CircleShape
                                        )
                                )
                                Text(
                                    text = stringResource(
                                        Res.string.task_details_more_subtasks,
                                        item.task.subTasks.size - 3
                                    ),
                                    style = MaterialTheme.typography.body2,
                                    color = MaterialTheme.colors.primary.copy(alpha = 0.8f),
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }
                    }
                }

                // 底部信息栏
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 奖励信息
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 经验值
                        if (item.task.exp > 0) {
                            Surface(
                                color = MaterialTheme.colors.primary.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "${item.task.exp} EXP",
                                    style = MaterialTheme.typography.caption,
                                    color = MaterialTheme.colors.primary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        // 金币
                        if (item.task.coin > 0 || item.task.coinVariable > 0) {
                            Surface(
                                color = Color(0xFFFFF7E6),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Image(
                                        painter = painterResource(Res.drawable.ic_coin),
                                        contentDescription = null,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = if (item.task.coinVariable == 0L)
                                            "${item.task.coin}"
                                        else
                                            "${item.task.coin}-${item.task.coin + item.task.coinVariable}",
                                        style = MaterialTheme.typography.caption,
                                        color = Color(0xFFD4A017)
                                    )
                                }
                            }
                        }

                        // 重复频率
                        Surface(
                            color = MaterialTheme.colors.secondary.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = getFrequencyEmoji(item.task.frequency),
                                    style = MaterialTheme.typography.caption
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = getFrequencyText(item.task.frequency),
                                    style = MaterialTheme.typography.caption,
                                    color = MaterialTheme.colors.secondary
                                )
                            }
                        }
                    }

                    // 截止时间
                    if (item.task.deadline > 0) {
                        val (color, bgColor) = getDeadlineColors(item.task.deadline)
                        Surface(
                            color = bgColor,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "⏰",
                                    style = MaterialTheme.typography.caption
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = formatDeadline(item.task.deadline),
                                    style = MaterialTheme.typography.caption,
                                    color = color
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun getFrequencyText(frequency: Int): String {
    return when (frequency) {
        0 -> stringResource(Res.string.task_details_frequency_none)
        1 -> stringResource(Res.string.task_details_frequency_daily)
        7 -> stringResource(Res.string.task_details_frequency_weekly)
        -4 -> stringResource(Res.string.task_details_frequency_monthly)
        -5 -> stringResource(Res.string.task_details_frequency_yearly)
        -1 -> stringResource(Res.string.task_details_frequency_unlimited)
        else -> stringResource(Res.string.task_details_frequency_custom, frequency)
    }
}

private fun getFrequencyEmoji(frequency: Int): String {
    return when (frequency) {
        0 -> "1️⃣"
        1 -> "📅"
        7 -> "📆"
        -4 -> "📅"
        -5 -> "📅"
        -1 -> "♾️"
        else -> "📅"
    }
}

@Composable
private fun getDeadlineColors(deadline: Long): Pair<Color, Color> {
    val now = System.currentTimeMillis()
    val diff = deadline - now
    val diffDays = diff / (24 * 60 * 60 * 1000)

    return when {
        diff < 0 -> Pair(
            MaterialTheme.colors.error,
            MaterialTheme.colors.error.copy(alpha = 0.1f)
        )

        diffDays == 0L -> Pair(
            Color(0xFFFF6B6B),
            Color(0xFFFF6B6B).copy(alpha = 0.1f)
        )

        diffDays <= 3 -> Pair(
            Color(0xFFFF922B),
            Color(0xFFFF922B).copy(alpha = 0.1f)
        )

        else -> Pair(
            MaterialTheme.colors.secondary,
            MaterialTheme.colors.secondary.copy(alpha = 0.1f)
        )
    }
}

private fun formatDeadline(timestamp: Long): String {
    val date = java.util.Date(timestamp)
    val calendar = java.util.Calendar.getInstance().apply { time = date }

    // 检查是否是 23:59
    val isEndOfDay = calendar.get(java.util.Calendar.HOUR_OF_DAY) == 23
            && calendar.get(java.util.Calendar.MINUTE) == 59

    return if (isEndOfDay) {
        java.text.SimpleDateFormat("MM-dd").format(date)
    } else {
        java.text.SimpleDateFormat("MM-dd HH:mm").format(date)
    }
}

