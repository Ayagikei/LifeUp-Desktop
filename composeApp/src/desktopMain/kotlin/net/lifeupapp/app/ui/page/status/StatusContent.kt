package net.lifeupapp.app.ui.page.status

import SkillIcon
import androidx.compose.foundation.Image
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lifeupdesktop.composeapp.generated.resources.Res
import lifeupdesktop.composeapp.generated.resources.ic_coin
import net.lifeupapp.app.datasource.data.Skill
import net.lifeupapp.app.ui.Strings
import net.lifeupapp.app.ui.page.config.Spacer4dpH
import net.lifeupapp.app.ui.page.list.MARGIN_SCROLLBAR
import net.lifeupapp.app.ui.page.list.rememberScrollbarAdapter
import net.lifeupapp.app.ui.theme.unimportantText

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

        SkillIcon(skill = item)

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
            Spacer4dpH()
            Box(modifier = Modifier.width(120.dp)) {
                LinearProgressIndicator(
                    progress = (item.exp - item.currentLevelExp).toFloat() / (item.exp - item.currentLevelExp + item.untilNextLevelExp).toFloat(),
                    modifier = Modifier.fillMaxWidth().height(4.dp),
                    color = color
                )
            }
        }



        Spacer(modifier = Modifier.width(MARGIN_SCROLLBAR))
    }
}

