package net.lifeupapp.app.ui.page.list

import SkillIcon
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Checkbox
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import lifeupdesktop.composeapp.generated.resources.*
import net.lifeupapp.app.datasource.data.ShopItem
import net.lifeupapp.app.datasource.data.Skill
import net.lifeupapp.app.datasource.data.SubTask
import net.lifeupapp.app.datasource.data.TaskCategory
import net.lifeupapp.app.ui.view.AsyncImage
import net.lifeupapp.app.ui.view.ClickableTextWithUrl
import net.lifeupapp.app.ui.view.loadImageBitmap
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import java.awt.Desktop
import java.net.URI

@Composable
internal fun TaskDetailsViewDialog(
    item: TodoItem,
    skills: List<Skill>,
    shopItems: List<ShopItem>,
    taskCategories: List<TaskCategory>,
    taskStore: TaskStore,
    onCloseClicked: () -> Unit
) {
    val subTaskLoadingId by taskStore.subTaskLoadingId.collectAsState()

    EnhancedLargeDialog(
        onCloseRequest = onCloseClicked,
        title = stringResource(Res.string.task_details_title)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            // 基础信息区域
            if (item.task.name.isNotBlank()) {
                item {
                    DetailItem(stringResource(Res.string.task_details_name), item.task.name)
                }
            }

            // 子任务区域 - 移动到任务名称后面
            if (item.task.subTasks.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        stringResource(Res.string.task_details_subtasks),
                        style = MaterialTheme.typography.caption.copy(color = Color.Gray)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(item.task.subTasks) { subTask ->
                    SubTaskItem(
                        subTask = subTask,
                        mainTaskId = item.task.id ?: 0L,
                        isLoading = subTaskLoadingId == subTask.id,
                        onStatusChange = { isComplete ->
                            taskStore.onSubTaskStatusChanged(
                                mainTaskId = item.task.id ?: 0L,
                                subTaskId = subTask.id,
                                isComplete = isComplete
                            )
                        }
                    )
                }
            }

            if (item.task.notes.isNotBlank()) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    DetailItem(stringResource(Res.string.task_details_notes), item.task.notes)
                }
            }

            item {
                taskCategories.find { it.id == item.task.categoryId }?.let { category ->
                    DetailItem(stringResource(Res.string.task_details_category), category.name)
                }

                DetailItem(
                    stringResource(Res.string.task_details_frequency),
                    getFrequencyText(item.task.frequency)
                )

                DetailItem(
                    stringResource(Res.string.task_details_status),
                    if (item.isDone) stringResource(Res.string.task_details_completed)
                    else stringResource(Res.string.task_details_pending)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 奖励区域
                Text(
                    stringResource(Res.string.task_details_rewards),
                    style = MaterialTheme.typography.h6
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            // 金币奖励
            if (item.task.coin > 0 || item.task.coinVariable > 0) {
                item {
                    DetailItem(
                        stringResource(Res.string.task_details_coins),
                        if (item.task.coinVariable == 0L) item.task.coin.toString()
                        else "${item.task.coin} - ${item.task.coin + item.task.coinVariable}"
                    )
                }
            }

            // 经验值奖励
            if (item.task.exp > 0) {
                item {
                    DetailItem(stringResource(Res.string.task_details_exp), item.task.exp.toString())
                }
            }

            // 技能奖励
            if (item.task.skillIds.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        stringResource(Res.string.task_details_skills),
                        style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.Bold)
                    )
                    SkillDisplay(skills.filter { item.task.skillIds.contains(it.id) })
                }
            }

            // 物品奖励
            if (item.task.items.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        stringResource(Res.string.task_details_item_reward),
                        style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.Bold)
                    )
                }

                items(item.task.items) { rewardItem ->
                    shopItems.find { it.id == rewardItem.itemId }?.let { shopItem ->
                        ItemDisplay(shopItem, rewardItem.itemCount)
                    }
                }
            }
        }

    }
}

@Composable
private fun SubTaskItem(
    subTask: SubTask,
    mainTaskId: Long,
    isLoading: Boolean,
    onStatusChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = subTask.status == 1,
            onCheckedChange = { isChecked ->
                if (!isLoading) {
                    onStatusChange(isChecked)
                }
            },
            enabled = !isLoading,
            modifier = Modifier.padding(end = 4.dp)
        )

        Box(
            modifier = Modifier.weight(1f)
        ) {
            // 主文本
            Text(
                text = subTask.todo,
                style = MaterialTheme.typography.body2.copy(
                    textDecoration = if (subTask.status == 1) TextDecoration.LineThrough else TextDecoration.None
                ),
                modifier = Modifier.fillMaxWidth()
            )

            // 奖励文本
            if (subTask.exp > 0 || (subTask.coin ?: 0 > 0) || subTask.items.isNotEmpty()) {
                Text(
                    text = buildString {
                        if (subTask.exp > 0) {
                            append(stringResource(Res.string.task_details_exp_reward, subTask.exp))
                        }
                        if (subTask.coin != null && subTask.coin > 0) {
                            if (length > 0) append(" | ")
                            append(stringResource(Res.string.task_details_coin_reward, subTask.coin))
                        }
                        if (subTask.items.isNotEmpty()) {
                            if (length > 0) append(" | ")
                            append(stringResource(Res.string.task_details_item_count_reward, subTask.items.size))
                        }
                    },
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomStart) // 将奖励文本对齐到底部
                )
            }
        }

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(16.dp),
                strokeWidth = 2.dp
            )
        }
    }
}

@Composable
private fun ItemDisplay(item: ShopItem, count: Int = 1) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        AsyncImage(
            condition = item.icon.isNotBlank() && item.icon.endsWith("/").not(),
            load = { loadImageBitmap(item.icon) },
            painterFor = { remember { BitmapPainter(it) } },
            contentDescription = stringResource(Res.string.task_details_item_icon),
            modifier = Modifier.size(40.dp),
            onError = {
                Image(
                    painter = painterResource(Res.drawable.ic_pic_loading_cir),
                    contentDescription = stringResource(Res.string.task_details_item_icon),
                    modifier = Modifier.size(40.dp)
                )
            }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(item.name, style = MaterialTheme.typography.body1)
            if (count > 1) {
                Text(
                    text = stringResource(Res.string.task_details_item_count, count),
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun DetailItem(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(label, style = MaterialTheme.typography.caption.copy(color = Color.Gray))
        ClickableTextWithUrl(value)
    }
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
private fun ClickableTextWithUrlDetection(text: String) {
    val urlPattern = Regex("""(\w+://\S+)""")
    val matches = urlPattern.findAll(text)

    if (matches.any()) {
        val annotatedString = buildAnnotatedString {
            var lastIndex = 0
            for (match in matches) {
                // Add non-URL part
                append(text.substring(lastIndex, match.range.first))

                // Add clickable URL
                addStyle(
                    style = SpanStyle(color = Color.Blue, textDecoration = TextDecoration.Underline),
                    start = length,
                    end = length + match.value.length
                )
                addStringAnnotation(
                    tag = "URL",
                    annotation = match.value,
                    start = length,
                    end = length + match.value.length
                )
                append(match.value)

                lastIndex = match.range.last + 1
            }

            // Add the last part of the text
            if (lastIndex < text.length) {
                append(text.substring(lastIndex))
            }
        }

        ClickableText(
            text = annotatedString,
            style = MaterialTheme.typography.body1,
            onClick = { offset ->
                annotatedString.getStringAnnotations(tag = "URL", start = offset, end = offset)
                    .firstOrNull()?.let { annotation ->
                        try {
                            Desktop.getDesktop().browse(URI(annotation.item))
                        } catch (e: Exception) {
                            println("Unable to open link: ${annotation.item}")
                        }
                    }
            }
        )
    } else {
        // If there is no URL, display the text normally
        Text(text, style = MaterialTheme.typography.body1)
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun SkillDisplay(skills: List<Skill>) {
    LazyRow {
        items(skills) { skill ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(end = 16.dp)
            ) {
                SkillIcon(skill)
                Text(skill.name, style = MaterialTheme.typography.caption)
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