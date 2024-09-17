package net.lifeupapp.app.ui.page.list

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import datasource.data.ShopItem
import datasource.data.Skill
import datasource.data.TaskCategory
import lifeupdesktop.composeapp.generated.resources.Res
import lifeupdesktop.composeapp.generated.resources.ic_pic_loading_cir
import org.jetbrains.compose.resources.painterResource
import ui.page.list.EnhancedLargeDialog
import ui.page.list.TodoItem
import ui.page.status.getLocalIconFilePathBySkillType
import ui.view.AsyncImage
import ui.view.loadImageBitmap

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
        title = "Task Details"
    ) {
        Column {
            DetailItem("Task Name", item.task.name)
            DetailItem("Notes", item.task.notes)
            DetailItem(
                "Category",
                taskCategories.find { it.id == item.task.categoryId }?.name ?: "Unknown"
            )
            DetailItem("Frequency", getFrequencyText(item.task.frequency))
            DetailItem("Status", if (item.isDone) "Completed" else "Pending")

            Spacer(modifier = Modifier.height(16.dp))

            Text("Rewards", style = MaterialTheme.typography.h6)

            Spacer(modifier = Modifier.height(8.dp))

            DetailItem("Coins", "${item.task.coin} - ${item.task.coin + item.task.coinVariable}")
            DetailItem("EXP", item.task.exp.toString())

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Skills",
                style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.Bold)
            )
            SkillDisplay(skills.filter { item.task.skillIds.contains(it.id) })

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Item Reward",
                style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.Bold)
            )
            ItemDisplay(shopItems.find { it.id == item.task.itemId })
        }
    }
}


@Composable
private fun DetailItem(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(label, style = MaterialTheme.typography.caption.copy(color = Color.Gray))
        Text(value, style = MaterialTheme.typography.body1)
    }
    Spacer(modifier = Modifier.height(8.dp))
}

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

@Composable
private fun ItemDisplay(item: ShopItem?) {
    if (item != null) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                condition = item.icon.isNotBlank() && item.icon.endsWith("/").not(),
                load = { loadImageBitmap(item.icon) },
                painterFor = { remember { BitmapPainter(it) } },
                contentDescription = "item icon",
                modifier = Modifier.size(40.dp),
                onError = {
                    Image(
                        painter = painterResource(Res.drawable.ic_pic_loading_cir),
                        contentDescription = "item icon",
                        modifier = Modifier.size(40.dp)
                    )
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(item.name, style = MaterialTheme.typography.body1)
        }
    } else {
        Text("No item reward", style = MaterialTheme.typography.body1)
    }
}

private fun getFrequencyText(frequency: Int): String {
    return when (frequency) {
        0 -> "None"
        1 -> "Daily"
        7 -> "Weekly"
        -4 -> "Monthly"
        -5 -> "Yearly"
        -1 -> "Unlimited"
        else -> "Custom: $frequency days"
    }
}