package net.lifeupapp.app.ui.page.item

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import base.json
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import lifeupdesktop.composeapp.generated.resources.*
import net.lifeupapp.app.base.launchSafely
import net.lifeupapp.app.datasource.constants.ItemPurchaseResult
import net.lifeupapp.app.datasource.data.ShopItem
import net.lifeupapp.app.ui.AppStore
import net.lifeupapp.app.ui.ScaffoldState
import net.lifeupapp.app.ui.text.StringText
import org.jetbrains.compose.resources.stringResource
import ui.page.config.Spacer8dpH
import ui.page.item.ListItem
import ui.page.item.ShopContent
import ui.page.list.Dialog

@Composable
fun ShopScreen(modifier: Modifier = Modifier) {
    val coroutineScope = rememberCoroutineScope()
    val globalStore = AppStore.current
    val model = remember { ShopStore(coroutineScope, globalStore) }
    val state by model.state.collectAsState()
    val coin = state.coin
    val scaffoldState = ScaffoldState.current

    // 获取不同的购买结果文本
    val purchaseResultTexts = mapOf(
        ItemPurchaseResult.PurchaseSuccess to stringResource(Res.string.purchase_success),
        ItemPurchaseResult.DatabaseError to stringResource(Res.string.purchase_database_error),
        ItemPurchaseResult.NotEnoughCoin to stringResource(Res.string.purchase_not_enough_coin),
        ItemPurchaseResult.ItemNotFound to stringResource(Res.string.purchase_item_not_found),
        ItemPurchaseResult.PurchaseAndUseSuccess to stringResource(Res.string.purchase_and_use_success),
        ItemPurchaseResult.PurchaseSuccessAndUseFailure to stringResource(Res.string.purchase_success_use_failure)
    )

    // 监听购买结果
    coroutineScope.launchSafely {
        model.purchaseResultEventFlow.collect { result ->
            val text = purchaseResultTexts[result] ?: return@collect
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
