package ui.page.achievement

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import base.json
import datasource.data.Achievement
import kotlinx.serialization.encodeToString
import ui.AppStore
import ui.Strings
import ui.page.config.Spacer8dpH
import ui.page.list.Dialog

@Composable
fun AchievementScreen(modifier: Modifier = Modifier) {
    val coroutineScope = rememberCoroutineScope()
    val globalStore = AppStore.current
    val model = remember { AchievementStore(coroutineScope, globalStore) }
    val state = model.state

    AchievementContent(
        modifier = modifier,
        categoryExpanded = state.categoryExpanded,
        selectedCategory = state.categories.find { it.id == state.currentCategoryId && it.id != null },
        categories = state.categories,
        items = state.achievements,
        onItemClicked = model::onItemClicked,
        onCategoryClicked = model::onCategoryClicked,
        onCategoryExpended = model::onCategoryExpended,
        onCategoryDismissed = model::onCategoryDismissed,
        onRefreshClick = model::onRefresh
    )

    state.editingItem?.also { item ->
        DetailDialog(
            item = item,
            onCloseClicked = model::onExitItemDetails,
        )
    }
}

@Composable
internal fun DetailDialog(
    item: Achievement,
    onCloseClicked: () -> Unit
) {
    Dialog(
        title = Strings.module_achievements,
        onCloseRequest = onCloseClicked,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(Strings.oops_wip)
            Spacer8dpH()
            Text(json.encodeToString(item))
            Spacer(modifier = Modifier.height(8.dp))

        }
    }
}
