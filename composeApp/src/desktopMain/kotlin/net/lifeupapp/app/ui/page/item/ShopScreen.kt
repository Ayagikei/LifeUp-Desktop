package ui.page.item

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import base.json
import net.lifeupapp.app.base.launchSafely
import datasource.data.ShopItem
import kotlinx.serialization.encodeToString
import ui.AppStore
import ui.ScaffoldState
import ui.Strings
import ui.page.config.Spacer8dpH
import ui.page.list.Dialog

@Composable
fun ShopScreen(modifier: Modifier = Modifier) {
    val coroutineScope = rememberCoroutineScope()
    val globalStore = AppStore.current
    val model = remember { ShopStore(coroutineScope, globalStore) }
    val state by model.state.collectAsState()
    val coin = state.coin
    val scaffoldState = ScaffoldState.current
    val text = Strings.snackbar_purchase_item

    coroutineScope.launchSafely {
        model.purchaseSuccessEventFlow.collect {
            scaffoldState.snackbarHostState.showSnackbar(text)
        }
    }

    val listItem = mutableListOf<ListItem>().apply {
        coin?.let {
            add(ListItem.Coin(it))
        }
        state.shopItems.map {
            add(ListItem.Item(it))
        }
    }

    ShopContent(
        modifier = modifier,
        coin = coin,
        categoryExpanded = state.categoryExpanded,
        selectedCategory = state.categories.find { it.id == state.currentCategoryId && it.id != null },
        categories = state.categories,
        items = listItem,
        onItemClicked = model::onItemClicked,
        onCategoryClicked = model::onCategoryClicked,
        onCategoryExpended = model::onCategoryExpended,
        onCategoryDismissed = model::onCategoryDismissed,
        onRefreshClick = model::onRefresh,
        onPurchased = {
            model.onPurchased(it, globalStore.strings.purchase_desc.format(it.name))
        },
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
    item: ShopItem,
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
