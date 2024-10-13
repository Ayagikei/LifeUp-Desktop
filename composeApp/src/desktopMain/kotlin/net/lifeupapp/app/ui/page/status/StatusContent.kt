package net.lifeupapp.app.ui.page.status

import androidx.compose.foundation.Image
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.primarySurface
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
import datasource.data.Skill
import lifeupdesktop.composeapp.generated.resources.Res
import lifeupdesktop.composeapp.generated.resources.ic_attr_charm
import lifeupdesktop.composeapp.generated.resources.ic_attr_creative
import lifeupdesktop.composeapp.generated.resources.ic_attr_endurance
import lifeupdesktop.composeapp.generated.resources.ic_attr_learning
import lifeupdesktop.composeapp.generated.resources.ic_attr_strength
import lifeupdesktop.composeapp.generated.resources.ic_attr_vitality
import lifeupdesktop.composeapp.generated.resources.ic_coin
import lifeupdesktop.composeapp.generated.resources.ic_pic_loading_cir
import logger
import net.lifeupapp.app.ui.Strings
import net.lifeupapp.app.ui.theme.unimportantText
import org.jetbrains.compose.resources.DrawableResource
import ui.page.config.Spacer4dpH
import ui.page.list.MARGIN_SCROLLBAR
import ui.page.list.rememberScrollbarAdapter
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
                painter = org.jetbrains.compose.resources.painterResource(
                    getLocalIconFilePathBySkillType(item.type)
                ),
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
                        painter = org.jetbrains.compose.resources.painterResource(Res.drawable.ic_pic_loading_cir),
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

fun getLocalIconFilePathBySkillType(type: Int): DrawableResource {
    return when (type) {
        Skill.SkillType.DEFAULT_CHARM.type -> Res.drawable.ic_attr_charm
        Skill.SkillType.DEFAULT_CREATIVE.type -> Res.drawable.ic_attr_creative
        Skill.SkillType.DEFAULT_ENDURANCE.type -> Res.drawable.ic_attr_endurance
        Skill.SkillType.DEFAULT_LEARNING.type -> Res.drawable.ic_attr_learning
        Skill.SkillType.DEFAULT_STRENGTH.type -> Res.drawable.ic_attr_strength
        Skill.SkillType.DEFAULT_VITALITY.type -> Res.drawable.ic_attr_vitality
        else -> Res.drawable.ic_pic_loading_cir
    }
}