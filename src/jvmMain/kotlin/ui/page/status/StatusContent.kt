package ui.page.status

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
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
import datasource.data.Skill
import logger
import ui.Strings
import ui.page.config.Spacer4dpH
import ui.page.list.MARGIN_SCROLLBAR
import ui.page.list.VerticalScrollbar
import ui.page.list.rememberScrollbarAdapter
import ui.theme.unimportantText
import ui.view.AsyncImage
import ui.view.loadImageBitmap
import java.util.logging.Level

@Composable
internal fun StatusContent(
    modifier: Modifier = Modifier,
    coin: Long,
    items: List<Skill>,
    onItemClicked: (id: Long) -> Unit,
    onRefreshClick: () -> Unit
) {

    Column(modifier) {
        TopAppBar(title = {
            Text(text = Strings.status)
        }, backgroundColor = MaterialTheme.colors.primarySurface, elevation = 0.dp, actions = {
            IconButton(onRefreshClick) {
                Icon(Icons.Default.Refresh, "Refresh")
            }
        })

        val listItem =
            mutableListOf<ListItem>().apply {
                add(ListItem.CoinType(coin))
                addAll(items.map { ListItem.SkillType(it) })
            }


        Box(Modifier.weight(1F)) {
            ListContent(
                items = listItem,
                onItemClicked = onItemClicked
            )
        }
    }
}

sealed class ListItem {
    data class CoinType(val number: Long) : ListItem()
    data class SkillType(val skill: Skill) : ListItem()
}

@Composable
private fun ListContent(
    items: List<ListItem>,
    onItemClicked: (id: Long) -> Unit
) {
    Box {
        val listState = rememberLazyListState()

        LazyColumn(state = listState) {
            items(items) { item ->
                if (item is ListItem.SkillType) {
                    Item(
                        item = item.skill,
                        onClicked = { onItemClicked(item.skill.id ?: 0L) },
                    )
                } else {
                    CoinRow(
                        number = (item as ListItem.CoinType).number,
                        onClicked = {
                            // TODO
                        })
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
            text = AnnotatedString("Coin"),
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
    item: Skill,
    onClicked: () -> Unit
) {
    Row(
        modifier = Modifier.clickable(onClick = onClicked).requiredSizeIn(minHeight = 56.dp)
            .padding(vertical = 8.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(8.dp))

        if (item.type > Skill.SkillType.USER.type && item.icon.isBlank()) {
            Image(
                painter = painterResource("icons/xml/${getLocalIconFilePathBySkillType(item.type)}"),
                contentDescription = "skill icon",
                modifier = Modifier.size(40.dp)
            )
        } else {
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
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = AnnotatedString(item.name),
            modifier = Modifier.weight(1F).align(Alignment.CenterVertically),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.width(8.dp))

//        IconButton(onClick = onDeleteClicked) {
//            Icon(
//                imageVector = Icons.Default.Delete,
//                contentDescription = null
//            )
//        }

        Column(horizontalAlignment = Alignment.End) {
            val color = if (item.color == 0) {
                MaterialTheme.colors.primary
            } else {
                Color(item.color)
            }

            Text(
                Strings.level_display.format(item.level),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Spacer4dpH()
            Text(
                Strings.total_exp_display.format(item.exp),
                fontSize = 12.sp,
                color = MaterialTheme.colors.unimportantText
            )
            Spacer4dpH()
            Text(
                Strings.to_next_exp_display.format(item.untilNextLevelExp),
                fontSize = 12.sp,
                color = MaterialTheme.colors.unimportantText
            )
        }



        Spacer(modifier = Modifier.width(MARGIN_SCROLLBAR))
    }
}

private fun getLocalIconFilePathBySkillType(type: Int): String {
    return when (type) {
        Skill.SkillType.DEFAULT_CHARM.type -> "ic_attr_charm.xml"
        Skill.SkillType.DEFAULT_CREATIVE.type -> "ic_attr_creative.xml"
        Skill.SkillType.DEFAULT_ENDURANCE.type -> "ic_attr_endurance.xml"
        Skill.SkillType.DEFAULT_LEARNING.type -> "ic_attr_learning.xml"
        Skill.SkillType.DEFAULT_STRENGTH.type -> "ic_attr_strength.xml"
        Skill.SkillType.DEFAULT_VITALITY.type -> "ic_attr_vitality.xml"
        else -> "ic_pic_loading_cir.xml"
    }
}