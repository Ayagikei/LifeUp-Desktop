package ui.page.achievement

import androidx.compose.foundation.Image
import androidx.compose.foundation.VerticalScrollbar
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import datasource.data.Achievement
import datasource.data.AchievementCategory
import lifeupdesktop.composeapp.generated.resources.Res
import lifeupdesktop.composeapp.generated.resources.ic_pic_loading_cir
import logger
import net.lifeupapp.app.ui.text.Localization.dateTimeFormatterWithNewLine
import ui.Strings
import ui.page.config.Spacer4dpH
import ui.page.list.MARGIN_SCROLLBAR
import ui.page.list.rememberScrollbarAdapter
import ui.theme.subTitle3
import ui.theme.unimportantText
import ui.view.AsyncImage
import ui.view.loadImageBitmap
import utils.getColorBetween
import java.util.logging.Level

@Composable
internal fun AchievementContent(
    modifier: Modifier = Modifier,
    categoryExpanded: Boolean,
    items: List<Achievement>,
    categories: List<AchievementCategory>,
    selectedCategory: AchievementCategory?,
    onItemClicked: (id: Long) -> Unit,
    onCategoryClicked: (Long) -> Unit,
    onCategoryExpended: () -> Unit,
    onCategoryDismissed: () -> Unit,
    onRefreshClick: () -> Unit
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
                onItemClicked = onItemClicked
            )
        }
    }
}


@Composable
private fun ListContent(
    items: List<Achievement>,
    onItemClicked: (id: Long) -> Unit
) {
    Box {
        val listState = rememberLazyListState()

        LazyColumn(state = listState) {
            items(items) { item ->
                if (item.isNormalAchievement()) {
                    Item(
                        item = item,
                        onClicked = { onItemClicked(item.id ?: 0L) },
                    )
                } else {
                    Subcategory(item = item)
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
private fun Item(
    item: Achievement,
    onClicked: () -> Unit
) {
    Row(
        modifier = Modifier.clickable(onClick = onClicked).requiredSizeIn(minHeight = 56.dp)
            .padding(vertical = 8.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(8.dp))


        logger.log(Level.INFO, "item.icon: ${item.iconUri}")
        AsyncImage(
            condition = item.iconUri.isNotBlank(),
            load = {
                loadImageBitmap(item.iconUri)
            },
            painterFor = {
                remember { BitmapPainter(it) }
            },
            contentDescription = "skill icon",
            modifier = Modifier.size(40.dp),
            onError = {
                Image(
                    painter = org.jetbrains.compose.resources.painterResource(Res.drawable.ic_pic_loading_cir),
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
        }


        Spacer(modifier = Modifier.width(8.dp))


        Column(horizontalAlignment = Alignment.End) {
            Text(
                "${item.progress}%",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(
                    getColorBetween(
                        (item.progress / 100.0).toFloat(),
                        MaterialTheme.colors.unimportantText.toArgb(),
                        MaterialTheme.colors.primary.toArgb()
                    )
                )
            )
            if (item.unlockedTime != 0L) {
                Spacer4dpH()
                Text(
                    dateTimeFormatterWithNewLine.format(item.unlockedTime),
                    fontSize = 12.sp,
                    color = MaterialTheme.colors.unimportantText,
                    textAlign = TextAlign.End
                )
            }
        }



        Spacer(modifier = Modifier.width(MARGIN_SCROLLBAR))
    }
}


@Composable
private fun Subcategory(
    item: Achievement,
) {
    Row(
        modifier = Modifier.requiredSizeIn(minHeight = 56.dp)
            .padding(vertical = 8.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(8.dp))

//        logger.log(Level.INFO, "item.icon: ${item.iconUri}")
//        AsyncImage(
//            condition = item.iconUri.isNotBlank(),
//            load = {
//                loadImageBitmap(item.iconUri)
//            },
//            painterFor = {
//                remember { BitmapPainter(it) }
//            },
//            contentDescription = "skill icon",
//            modifier = Modifier.size(40.dp),
//            onError = {
//                Image(
//                    painter = painterResource("icons/xml/ic_pic_loading_cir.xml"),
//                    contentDescription = "skill icon",
//                    modifier = Modifier.size(40.dp)
//                )
//            }
//        )
//

        Column(Modifier.weight(1F).align(Alignment.CenterVertically)) {
            Text(
                text = AnnotatedString(item.name),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.h6
            )
        }

        Spacer(modifier = Modifier.width(MARGIN_SCROLLBAR))
    }
}
