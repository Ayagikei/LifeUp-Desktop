package ui.page.feelings

import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.Image
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import datasource.data.Feelings
import lifeupdesktop.composeapp.generated.resources.Res
import lifeupdesktop.composeapp.generated.resources.ic_coin
import lifeupdesktop.composeapp.generated.resources.ic_pic_loading_cir
import ui.Strings
import ui.page.config.Spacer4dpH
import ui.page.config.Spacer8dpH
import ui.page.list.MARGIN_SCROLLBAR
import ui.page.list.rememberScrollbarAdapter
import ui.text.Localization
import ui.theme.important
import ui.theme.unimportantText
import ui.view.AsyncImage
import ui.view.loadImageBitmap

@Composable
internal fun FeelingsContent(
    modifier: Modifier = Modifier,
    items: List<Feelings>,
    onItemClicked: (id: Long) -> Unit,
    onExportClicked: () -> Unit,
    onRefreshClick: () -> Unit,
    onAttachmentClicked: (attachment: String) -> Unit
) {

    Column(modifier) {
        TopAppBar(title = {
            Text(text = Strings.module_feelings)
        }, backgroundColor = MaterialTheme.colors.primarySurface, elevation = 0.dp, actions = {
            IconButton(onExportClicked) {
                Icon(Icons.Default.Build, "Export") // fixme: change to download icon
            }
            IconButton(onRefreshClick) {
                Icon(Icons.Default.Refresh, "Refresh")
            }
        })


        Box(Modifier.weight(1F)) {
            ListContent(
                items = items,
                onItemClicked = onItemClicked,
                onAttachmentClicked = onAttachmentClicked
            )
        }
    }
}


@Composable
private fun ListContent(
    items: List<Feelings>,
    onItemClicked: (id: Long) -> Unit,
    onAttachmentClicked: (attachment: String) -> Unit
) {
    Box {
        val listState = rememberLazyListState()

        LazyColumn(state = listState) {
            items(items) { item ->
                Item(
                    item = item,
                    onClicked = { onItemClicked(item.id ?: 0L) },
                    onAttachmentClicked = onAttachmentClicked
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
            painter = org.jetbrains.compose.resources.painterResource(Res.drawable.ic_coin),
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

        Spacer(modifier = Modifier.width(MARGIN_SCROLLBAR))
    }
}

@Composable
private fun Item(
    item: Feelings,
    onClicked: () -> Unit,
    onAttachmentClicked: (attachment: String) -> Unit
) {
    Row(
        modifier = Modifier.requiredSizeIn(minHeight = 56.dp)
            .padding(vertical = 8.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Spacer8dpH()
            Row(modifier = Modifier.fillMaxWidth()) {
                SelectionContainer(modifier = Modifier.weight(1F)) {
                    Text(item.content, style = MaterialTheme.typography.body1, color = MaterialTheme.colors.important)
                }
            }
            if (item.attachments.isNotEmpty()) {
                Spacer4dpH()

                val listState = rememberLazyListState()
                LazyRow(state = listState) {
                    itemsIndexed(item.attachments) { index, item ->
                        Box(modifier = Modifier.clickable {
                            onAttachmentClicked(item)
                        }.padding(8.dp)) {
                            AsyncImage(
                                condition = item.isNotBlank(),
                                load = {
                                    loadImageBitmap(item)
                                },
                                painterFor = {
                                    remember { BitmapPainter(it) }
                                },
                                contentDescription = "skill icon",
                                modifier = Modifier.size(156.dp),
                                onError = {
                                    Image(
                                        painter = org.jetbrains.compose.resources.painterResource(Res.drawable.ic_pic_loading_cir),
                                        contentDescription = "skill icon",
                                        modifier = Modifier.size(156.dp)
                                    )
                                }
                            )
                        }
                    }
                }

                HorizontalScrollbar(
                    modifier = Modifier.align(Alignment.CenterHorizontally).wrapContentSize(),
                    adapter = rememberScrollbarAdapter(scrollState = listState)
                )
            }
            Spacer4dpH()

            Column(horizontalAlignment = Alignment.End, modifier = Modifier.fillMaxWidth().padding(end = 16.dp)) {
                Text(item.title, style = MaterialTheme.typography.caption, color = MaterialTheme.colors.unimportantText)
                Spacer4dpH()
                Text(
                    Localization.dateTimeFormatter.format(item.time),
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.unimportantText
                )
            }

            Spacer8dpH()
        }

        Spacer(modifier = Modifier.width(MARGIN_SCROLLBAR))
    }
}