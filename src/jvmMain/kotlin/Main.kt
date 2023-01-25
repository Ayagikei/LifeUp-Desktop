import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.window.*
import kotlinx.coroutines.GlobalScope
import ui.GlobalStore
import ui.page.config.ConfigContent
import ui.page.list.RootContent
import ui.theme.AppTheme
import ui.view.fakeDialog
import java.util.logging.Logger

@ExperimentalUnitApi
@Composable
@Preview
fun app() {
    AppTheme {
        var selectedItem by remember { mutableStateOf(0) }
        val items = listOf("Home", "Search", "Settings")
        val icons = listOf(Icons.Filled.Home, Icons.Filled.Search, Icons.Filled.Settings)
        val model = remember { GlobalStore }

        Row {
            NavigationRail {
                items.forEachIndexed { index, item ->
                    NavigationRailItem(icon = { Icon(icons[index], contentDescription = item) },
                        label = { Text(item) },
                        selected = selectedItem == index,
                        onClick = { selectedItem = index })
                }
            }
            if (selectedItem == 0) {
                Box(Modifier.fillMaxSize()) {
                    RootContent()
                }
            } else if (selectedItem == 2) {
                Box(Modifier.fillMaxSize()) {
                    ConfigContent()
                }
            }
        }
        val dialogStatus = model.dialogStatus
        if (dialogStatus != null) {
            fakeDialog(dialogStatus)
        }
    }
}


@OptIn(ExperimentalUnitApi::class)
fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "LifeUp",
        state = rememberWindowState(
            position = WindowPosition(alignment = Alignment.Center),
        ),
    ) {
        app()
    }
}

val AppScope = GlobalScope

val Any?.logger: Logger
    get() = Logger.getLogger((this?.javaClass ?: Any::class.java).toGenericString())