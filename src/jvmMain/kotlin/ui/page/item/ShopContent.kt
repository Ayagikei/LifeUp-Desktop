package ui.page.item

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import datasource.data.ShopCategory
import logger
import net.lifeupapp.lifeup.api.content.shop.ShopItem
import ui.Strings
import ui.page.config.Spacer4dpH
import ui.page.list.MARGIN_SCROLLBAR
import ui.page.list.VerticalScrollbar
import ui.page.list.rememberScrollbarAdapter
import ui.theme.subTitle3
import ui.theme.unimportantText
import ui.view.AsyncImage
import ui.view.loadImageBitmap
import java.util.logging.Level

@Composable
internal fun ShopContent(
    modifier: Modifier = Modifier,
    coin: Long?,
    categoryExpanded: Boolean,
    items: List<ListItem>,
    categories: List<ShopCategory>,
    selectedCategory: ShopCategory?,
    onItemClicked: (id: Long) -> Unit,
    onCategoryClicked: (Long) -> Unit,
    onCategoryExpended: () -> Unit,
    onCategoryDismissed: () -> Unit,
    onRefreshClick: () -> Unit,
    onPurchased: (ShopItem) -> Unit
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
                    Text(text = Strings.module_achievements)
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
            IconButton(onRefreshClick) {
                Icon(Icons.Default.Refresh, "Refresh")
            }
        })


        Box(Modifier.weight(1F)) {
            ListContent(
                items = items,
                coin = coin,
                onItemClicked = onItemClicked,
                onPurchased = onPurchased
            )
        }
    }
}

sealed class ListItem {
    data class Coin(val coin: Long) : ListItem()
    data class Item(val item: ShopItem) : ListItem()
}


@Composable
private fun ListContent(
    items: List<ListItem>,
    coin: Long?,
    onItemClicked: (id: Long) -> Unit,
    onPurchased: (ShopItem) -> Unit
) {
    Box {
        val listState = rememberLazyListState()

        LazyColumn(state = listState) {
            items(items) { item ->
                when (item) {
                    is ListItem.Coin -> CoinRow(
                        number = item.coin,
                        onClicked = { onItemClicked(0L) }
                    )

                    is ListItem.Item -> Item(
                        item = item.item,
                        coin = coin,
                        onClicked = { onItemClicked(item.item.id ?: 0L) },
                        onPurchased = onPurchased
                    )
                }
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
private fun CoinRow(
    number: Long,
    onClicked: () -> Unit
) {
    Row(
        modifier = Modifier.clickable(onClick = onClicked).requiredSizeIn(minHeight = 56.dp)
            .padding(vertical = 8.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(8.dp))
        Image(
            painter = painterResource("icons/xml/ic_coin.xml"),
            contentDescription = "coin icon",
            modifier = Modifier.size(40.dp)
        )


        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = AnnotatedString(Strings.coin),
            modifier = Modifier.weight(1F).align(Alignment.CenterVertically),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.width(8.dp))


        val color = Color(255, 163, 0)
        Text(number.toString(), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = color)

        Spacer(modifier = Modifier.width(24.dp))
        Spacer(modifier = Modifier.width(MARGIN_SCROLLBAR))
    }
}

@Composable
private fun Item(
    item: ShopItem,
    coin: Long?,
    onClicked: () -> Unit,
    onPurchased: (ShopItem) -> Unit
) {
    Row(
        modifier = Modifier.clickable(onClick = onClicked).requiredSizeIn(minHeight = 56.dp)
            .padding(vertical = 8.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(8.dp))


        logger.log(Level.INFO, "item.icon: ${item.icon}")
        AsyncImage(
            condition = item.icon.isNotBlank(),
            load = {
                loadImageBitmap(item.icon)
            },
            painterFor = {
                remember { BitmapPainter(it) }
            },
            contentDescription = "skill icon",
            modifier = Modifier.size(40.dp),
            onError = {
                Image(
                    painter = painterResource("icons/xml/ic_pic_loading_cir.xml"),
                    contentDescription = "skill icon",
                    modifier = Modifier.size(40.dp)
                )
            }
        )


        Spacer(modifier = Modifier.width(8.dp))

        Column(Modifier.weight(1F).align(Alignment.CenterVertically)) {
            Text(
                text = AnnotatedString(item.name),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.subTitle3
            )
            if (item.desc.isNotBlank()) {
                Spacer4dpH()
                Text(
                    text = AnnotatedString(item.desc),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.unimportantText
                )
            }
            Spacer4dpH()
            Text(
                "${item.price} ${Strings.coin}",
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.unimportantText
            )
            Spacer4dpH()
            Text(
                Strings.item_own_number.format(item.ownNumber),
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.unimportantText
            )
        }


        Spacer(modifier = Modifier.width(8.dp))


        Column(horizontalAlignment = Alignment.End) {
            Button(
                onClick = {
                    onPurchased(item)
                },
                modifier = Modifier.align(Alignment.End),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.primary,
                    contentColor = MaterialTheme.colors.onPrimary
                ),
                enabled = coin != null && coin >= item.price
            ) {
                Text(Strings.btn_purchase)
            }
        }



        Spacer(modifier = Modifier.width(MARGIN_SCROLLBAR))
    }
}
