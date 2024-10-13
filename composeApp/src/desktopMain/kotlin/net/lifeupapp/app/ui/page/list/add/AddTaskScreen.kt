package net.lifeupapp.app.ui.page.list.add

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import datasource.data.ShopItem
import datasource.data.Skill
import datasource.data.TaskCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import lifeupdesktop.composeapp.generated.resources.Res
import lifeupdesktop.composeapp.generated.resources.add_tasks_frequency_desc
import lifeupdesktop.composeapp.generated.resources.ic_coin
import lifeupdesktop.composeapp.generated.resources.ic_pic_loading_cir
import net.lifeupapp.app.base.launchSafely
import net.lifeupapp.app.ui.AppStore
import net.lifeupapp.app.ui.Strings
import net.lifeupapp.app.ui.page.item.ShopStore
import net.lifeupapp.app.ui.page.list.TaskStore
import net.lifeupapp.app.ui.page.status.StatusStore
import net.lifeupapp.app.ui.page.status.getLocalIconFilePathBySkillType
import org.jetbrains.compose.resources.stringResource
import ui.page.config.Spacer16dpH
import ui.page.config.Spacer16dpW
import ui.page.config.Spacer24dpH
import ui.page.config.Subtitle
import ui.view.AsyncImage
import ui.view.loadImageBitmap

@Composable
fun AddTaskScreen(modifier: Modifier = Modifier, defaultCategoryId: Long, addSuccess: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    val globalStore = AppStore.current
    val model = remember { AddTasksStore(coroutineScope, globalStore, defaultCategoryId) }
    // we need skill lists
    val statusStore = remember { StatusStore(coroutineScope, globalStore) }
    // we need item list
    val shopStore = remember { ShopStore(coroutineScope, globalStore) }
    val taskStore = remember { TaskStore(coroutineScope, globalStore) }
    val state by model.state.collectAsState(Dispatchers.Main)
    val statusState by statusStore.state.collectAsState(Dispatchers.Main)
    val shopState by shopStore.state.collectAsState(Dispatchers.Main)
    val taskStatus by taskStore.state.collectAsState(Dispatchers.Main)
    val scaffoldState = rememberScaffoldState()

    coroutineScope.launchSafely {
        launch {
            model.addSuccessEventFlow.collect {
                addSuccess()
            }
        }

        launch {
            model.addFailedEventFlow.collect {
                scaffoldState.snackbarHostState.showSnackbar(it)
            }
        }
    }

    Scaffold(
        scaffoldState = scaffoldState
    ) {
        AddTaskContent(
            modifier,
            state,
            statusState.skills,
            shopState.shopItems,
            taskCategories = taskStatus.categories,
            onInputToDo = {
                model.updateState {
                    copy(todo = it)
                }
            },
            onInputNotes = {
                model.updateState {
                    copy(notes = it)
                }
            },
            onSkillSelected = model::onSkillSelected,
            onInputCoin = model::onInputCoin,
            onInputCoinMax = model::onInputCoinVar,
            onFrequencyChanged = model::onFrequencyChanged,
            onRemoteBackgroundChanged = model::onRemoteBackgroundUrlChanged,
            onItemSelected = model::onItemSelected,
            onItemAmountChanged = model::onItemAmountChanged,
            onCategorySelected = model::onCategorySelected,
            onSkillExpChanged = model::onSkillExpChanged,
            onSubmitClicked = model::onSubmitClicked
        )
    }
}

@Composable
private fun AddTaskContent(
    modifier: Modifier = Modifier,
    state: AddTasksStore.AddTaskState,
    skills: List<Skill>,
    shopItems: List<ShopItem>,
    taskCategories: List<TaskCategory>,
    onInputToDo: (String) -> Unit,
    onInputNotes: (String) -> Unit,
    onInputCoin: (String) -> Unit,
    onInputCoinMax: (String) -> Unit,
    onSkillSelected: (id: Long, selected: Boolean) -> Unit,
    onFrequencyChanged: (value: Int) -> Unit,
    onRemoteBackgroundChanged: (String) -> Unit /* TODO */,
    onItemSelected: (Long?) -> Unit,
    onItemAmountChanged: (Int) -> Unit,
    onCategorySelected: (Long) -> Unit,
    onSkillExpChanged: (Int) -> Unit,
    onSubmitClicked: () -> Unit
) {
    val scrollState = rememberScrollState()
    Surface(
        modifier = modifier.fillMaxSize()
    ) {
        Box {
            Column(modifier = Modifier.padding(24.dp).verticalScroll(scrollState)) {
                BaseConfigs(
                    state,
                    onInputToDo,
                    onInputNotes,
                    onFrequencyChanged,
                    taskCategories,
                    onCategorySelected,
                    onSubmitClicked
                )

                RewardConfigs(
                    state,
                    onInputCoin,
                    onInputCoinMax,
                    skills,
                    onSkillSelected,
                    shopItems,
                    onItemSelected,
                    onItemAmountChanged,
                    onSkillExpChanged
                )
            }

            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd),
                adapter = rememberScrollbarAdapter(scrollState = scrollState)
            )
        }
    }
}

@Composable
private fun RewardConfigs(
    state: AddTasksStore.AddTaskState,
    onInputCoin: (String) -> Unit,
    onInputCoinMax: (String) -> Unit,
    skills: List<Skill>,
    onSkillSelected: (id: Long, selected: Boolean) -> Unit,
    shopItems: List<ShopItem>,
    onItemSelected: (Long?) -> Unit,
    onItemAmountChanged: (Int) -> Unit,
    onSkillExpChanged: (Int) -> Unit
) {
    Subtitle(Strings.add_tasks_title_reward)
    Spacer16dpH()
    // coin input
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = org.jetbrains.compose.resources.painterResource(Res.drawable.ic_coin),
            contentDescription = "coin icon",
            modifier = Modifier.size(40.dp)
        )
        Spacer16dpW()
        TextField(
            modifier = Modifier.width(120.dp),
            value = state.coinMin.toString(),
            onValueChange = {
                onInputCoin(it)
            },
            label = { Text(Strings.add_tasks_reward_coin_min) },
            singleLine = true
        )
        Text(text = "-", modifier = Modifier.padding(horizontal = 8.dp))
        TextField(
            modifier = Modifier.width(120.dp),
            value = state.coinMax.toString(),
            onValueChange = {
                onInputCoinMax(it)
            },
            label = { Text(Strings.add_tasks_reward_coin_max) },
            singleLine = true
        )
    }

    Spacer16dpH()


    Spacer24dpH()
    SkillSelector(skills, state, onSkillSelected, onSkillExpChanged)

    Spacer24dpH()
    Subtitle(Strings.add_tasks_reward_shop_items)

    Spacer16dpH()
    // shop item selection

    val (shopExpanded, setShopExpanded) = remember { mutableStateOf(false) }

    Row(verticalAlignment = Alignment.CenterVertically) {
        val selectedItem = shopItems.find { it.id == state.itemId }
        if (selectedItem != null) {
            ItemIcon(selectedItem)
            Spacer16dpW()
        }
        Box(modifier = Modifier.weight(1f)) {
            val (searchText, setSearchText) = remember { mutableStateOf("") }
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { setShopExpanded(true) }
            ) {
                Text(selectedItem?.name ?: Strings.common_unselected)
            }
            DropdownMenu(expanded = shopExpanded, onDismissRequest = { setShopExpanded(false) }) {
                TextField(
                    value = searchText,
                    onValueChange = setSearchText,
                    label = { Text(Strings.common_search) },
                    singleLine = true
                )
                TextButton(modifier = Modifier.fillMaxWidth(), onClick = {
                    onItemSelected(null)
                    setShopExpanded(false)
                }) {
                    Text(Strings.common_unselected)
                }
                shopItems.filter {
                    if (searchText.trim().isNotBlank()) {
                        it.name.contains(searchText.trim(), ignoreCase = true)
                    } else {
                        true
                    }
                }.forEachIndexed { index, item ->
                    Row {
                        ItemIcon(item, 32.dp)
                        DropdownMenuItem(onClick = {
                            onItemSelected(item.id)
                            setShopExpanded(false)
                        }) {
                            Text(text = item.name)
                        }
                    }

                }
            }
            Spacer(modifier = Modifier.width(16.dp))
        }
        Spacer16dpW()
        OutlinedTextField(
            value = state.itemAmount.toString(),
            onValueChange = { onItemAmountChanged(it.toIntOrNull() ?: 0) },
            label = { Text(Strings.add_tasks_reward_shop_items_quantity) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.width(120.dp)
        )

    }
}

@Composable
private fun ItemIcon(selectedItem: ShopItem, size: Dp = 56.dp) {
    AsyncImage(
        condition = selectedItem.icon.isNotBlank() && selectedItem.icon.endsWith("/").not(),
        load = {
            loadImageBitmap(selectedItem.icon)
        },
        painterFor = {
            remember { BitmapPainter(it) }
        },
        contentDescription = "item icon",
        modifier = Modifier.size(size),
        onError = {
            Image(
                painter = org.jetbrains.compose.resources.painterResource(Res.drawable.ic_pic_loading_cir),
                contentDescription = "item icon",
                modifier = Modifier.size(size)
            )
        }
    )
}

@Composable
private fun SkillSelector(
    skills: List<Skill>,
    state: AddTasksStore.AddTaskState,
    onSkillSelected: (id: Long, selected: Boolean) -> Unit,
    onSkillExpChanged: (Int) -> Unit
) {
    Subtitle(Strings.add_tasks_title_skills)

    Spacer16dpH()
    // skill selection
    LazyRow {
        items(skills.size) { index ->
            val skill = skills[index]
            val selected = state.skills.contains(skill.id)

            if (skill.type > Skill.SkillType.USER.type && skill.icon.isBlank()) {
                Image(
                    painter = org.jetbrains.compose.resources.painterResource(
                        getLocalIconFilePathBySkillType(skill.type)
                    ),
                    contentDescription = "skill icon",
                    colorFilter = if (selected) null else ColorFilter.colorMatrix(ColorMatrix().apply {
                        setToSaturation(0f)
                    }),
                    modifier = Modifier.size(60.dp).padding(8.dp).clickable {
                        onSkillSelected(skill.id!!, !selected)
                    })
            } else {
                AsyncImage(
                    condition = skill.icon.isNotBlank(),
                    load = {
                        loadImageBitmap(skill.icon)
                    },
                    painterFor = {
                        remember { BitmapPainter(it) }
                    },
                    contentDescription = skill.name,
                    modifier = Modifier.size(60.dp).padding(8.dp).clickable {
                        onSkillSelected(skill.id!!, !selected)
                    },
                    colorFilter = if (selected) null else ColorFilter.colorMatrix(ColorMatrix().apply {
                        setToSaturation(0f)
                    }),
                    onError = {
                        Image(
                            painter = org.jetbrains.compose.resources.painterResource(Res.drawable.ic_pic_loading_cir),
                            contentDescription = "skill icon",
                            modifier = Modifier.size(60.dp),
                            colorFilter = if (selected) null else ColorFilter.colorMatrix(
                                ColorMatrix().apply {
                                    setToSaturation(0f)
                                }),
                        )
                    })
            }
        }
    }

    if (state.skills.isNotEmpty()) {
        Spacer16dpH()
        TextField(
            value = state.exp.toString(),
            onValueChange = { onSkillExpChanged(it.toIntOrNull() ?: 0) },
            label = { Text(Strings.add_tasks_exp) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun BaseConfigs(
    state: AddTasksStore.AddTaskState,
    onInputToDo: (String) -> Unit,
    onInputNotes: (String) -> Unit,
    onFrequencyChanged: (value: Int) -> Unit,
    taskCategories: List<TaskCategory>,
    onCategorySelected: (Long) -> Unit,
    onSubmitClicked: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Subtitle(Strings.add_tasks_title_base, modifier = Modifier.weight(1f))
        IconButton(onClick = onSubmitClicked) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null
            )
        }
    }

    // category
    Box {
        val (expanded, setExpanded) = remember { mutableStateOf(false) }
        OutlinedButton(onClick = {
            setExpanded(true)
        }) {
            Text(
                text = taskCategories.find { it.id == state.categoryId }?.name
                    ?: Strings.common_unknown
            )
            Icon(Icons.Default.ArrowDropDown, "")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { setExpanded(false) }) {
            taskCategories.filter {
                // only the keep the normal category(skipping the ALL list and other smart lists)
                (it.id ?: 0) > 0L && it.type == 0
            }.forEachIndexed { index, item ->
                DropdownMenuItem(onClick = {
                    setExpanded(false)
                    onCategorySelected(item.id!!)
                }) {
                    Text(text = item.name)
                }
            }
        }
    }


    Spacer16dpH()
    // text input
    TextField(modifier = Modifier.fillMaxWidth(), value = state.todo, onValueChange = {
        onInputToDo(it)
    }, label = { Text(Strings.add_tasks_todo) })
    Spacer16dpH()
    TextField(modifier = Modifier.fillMaxWidth(), value = state.notes, onValueChange = {
        onInputNotes(it)
    }, label = { Text(Strings.add_tasks_notes) })

    Spacer16dpH()

    // frequency dropdown
    val frequencyOptions =
        listOf(
            Strings.add_tasks_frequency_none,
            Strings.add_tasks_frequency_daily,
            Strings.add_tasks_frequency_weekly,
            Strings.add_tasks_frequency_monthly,
            Strings.add_tasks_frequency_yearly,
            Strings.add_tasks_frequency_unlimited
        )

    val (expanded, setExpanded) = remember { mutableStateOf(false) }



    Box {
        OutlinedButton(onClick = {
            setExpanded(true)
        }) {
            Text(
                text = stringResource(
                    Res.string.add_tasks_frequency_desc,
                    frequencyOptions[frequencyToIndex(state.frequency)]
                )
            )
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { setExpanded(false) }) {
            frequencyOptions.forEachIndexed { index, option ->
                DropdownMenuItem(onClick = {
                    setExpanded(false)
                    onFrequencyChanged(
                        when (index) {
                            0 -> 0
                            1 -> 1
                            2 -> 7
                            3 -> -4
                            4 -> -5
                            5 -> -1
                            else -> 0
                        }
                    )
                }) {
                    Text(text = option)
                }
            }
        }
    }

    Spacer24dpH()
}

private fun frequencyToIndex(frequency: Int): Int {
    return when (frequency) {
        0 -> 0
        1 -> 1
        7 -> 2
        -4 -> 3
        -5 -> 4
        -1 -> 5
        else -> 0
    }
}


@Preview
@Composable
fun AddTaskScreenPreview() {
//    AddTaskContent(state = AddTasksStore.AddTaskState(0L, "Test"), onInputToDo = {
//
//    })
}