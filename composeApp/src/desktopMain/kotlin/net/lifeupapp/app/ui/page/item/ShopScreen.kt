package ui.page.item

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import base.json
import datasource.data.ShopItem
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import net.lifeupapp.app.base.launchSafely
import net.lifeupapp.app.ui.AppStore
import net.lifeupapp.app.ui.ScaffoldState
import net.lifeupapp.app.ui.page.item.ShopStore
import net.lifeupapp.app.ui.text.StringText
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
    val text = StringText.snackbar_purchase_item

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
            coroutineScope.launch {
                model.onPurchased(it, StringText.getPurchaseDesc(it.name))
            }
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
        title = StringText.module_achievements,
        onCloseRequest = onCloseClicked,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(StringText.oops_wip)
            Spacer8dpH()
            Text(json.encodeToString(item))
            Spacer(modifier = Modifier.height(8.dp))

        }
    }
}
