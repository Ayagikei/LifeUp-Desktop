package net.lifeupapp.app.ui.page.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import datasource.data.Skill
import datasource.data.TaskCategory
import lifeupdesktop.composeapp.generated.resources.*
import net.lifeupapp.app.datasource.data.ShopItem
import net.lifeupapp.app.ui.page.status.getLocalIconFilePathBySkillType
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import ui.page.list.EnhancedLargeDialog
import ui.view.AsyncImage
import ui.view.loadImageBitmap

@OptIn(ExperimentalResourceApi::class)
@Composable
internal fun TaskDetailsViewDialog(
    item: TodoItem,
    skills: List<Skill>,
    shopItems: List<ShopItem>,
    taskCategories: List<TaskCategory>,
    onCloseClicked: () -> Unit
) {
    EnhancedLargeDialog(
        onCloseRequest = onCloseClicked,
        title = stringResource(Res.string.task_details_title)
    ) {
        Column {
            DetailItem(stringResource(Res.string.task_details_name), item.task.name)
            DetailItem(stringResource(Res.string.task_details_notes), item.task.notes)
            DetailItem(
                stringResource(Res.string.task_details_category),
                taskCategories.find { it.id == item.task.categoryId }?.name
                    ?: stringResource(Res.string.task_details_unknown)
            )
            DetailItem(
                stringResource(Res.string.task_details_frequency),
                getFrequencyText(item.task.frequency)
            )
            DetailItem(
                stringResource(Res.string.task_details_status),
                if (item.isDone) stringResource(Res.string.task_details_completed) else stringResource(
                    Res.string.task_details_pending
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                stringResource(Res.string.task_details_rewards),
                style = MaterialTheme.typography.h6
            )

            Spacer(modifier = Modifier.height(8.dp))

            DetailItem(
                stringResource(Res.string.task_details_coins),
                "${item.task.coin} - ${item.task.coin + item.task.coinVariable}"
            )
            DetailItem(stringResource(Res.string.task_details_exp), item.task.exp.toString())

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                stringResource(Res.string.task_details_skills),
                style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.Bold)
            )
            SkillDisplay(skills.filter { item.task.skillIds.contains(it.id) })

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                stringResource(Res.string.task_details_item_reward),
                style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.Bold)
            )
            ItemDisplay(shopItems.find { it.id == item.task.itemId })
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun DetailItem(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(label, style = MaterialTheme.typography.caption.copy(color = Color.Gray))
        Text(value, style = MaterialTheme.typography.body1)
    }
    Spacer(modifier = Modifier.height(8.dp))
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
                Image(
                    painter = painterResource(getLocalIconFilePathBySkillType(skill.type)),
                    contentDescription = skill.name,
                    modifier = Modifier.size(40.dp)
                )
                Text(skill.name, style = MaterialTheme.typography.caption)
            }
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun ItemDisplay(item: ShopItem?) {
    if (item != null) {
        Row(verticalAlignment = Alignment.CenterVertically) {
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
            Text(item.name, style = MaterialTheme.typography.body1)
        }
    } else {
        Text(
            stringResource(Res.string.task_details_no_item_reward),
            style = MaterialTheme.typography.body1
        )
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