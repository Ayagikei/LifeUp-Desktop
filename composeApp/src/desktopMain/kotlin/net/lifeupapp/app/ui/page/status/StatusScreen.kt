package ui.page.status

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import kotlinx.coroutines.Dispatchers
import ui.AppStore

@Composable
fun StatusScreen(modifier: Modifier = Modifier) {
    val coroutineScope = rememberCoroutineScope()
    val globalStore = AppStore.current
    val model = remember { StatusStore(coroutineScope, globalStore) }
    val state = model.state.collectAsState(Dispatchers.Main).value


    StatusContent(
        modifier = modifier,
        items = state.skills,
        coin = state.coin,
        onItemClicked = {
            // TODO
        },
        onRefreshClick = model::onRefresh
    )
}