import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.Typography
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import ui.DialogStatus
import ui.GlobalStore
import ui.list.RootContent
import ui.theme.AppTheme
import ui.theme.dialogTitle

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
            }
        }
        val dialogStatus = model.dialogStatus
        if (dialogStatus != null) {
            fakeDialog(dialogStatus)
        }
    }
}

@ExperimentalUnitApi
@Composable
fun fakeDialog(dialogStatus: DialogStatus) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f))
            .clickable(interactionSource = interactionSource, indication = null) {

            }, contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(0.55f).wrapContentSize()
                .background(Color.White, shape = RoundedCornerShape(16.dp))
        ) {
            Column(
                Modifier.wrapContentSize().padding(16.dp),
            ) {
                Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        dialogStatus.title,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.dialogTitle
                    )
                }
                Spacer(Modifier.height(24.dp))
                Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        dialogStatus.message,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.body2
                    )
                }
                Spacer(Modifier.height(24.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = dialogStatus.negativeAction, modifier = Modifier.wrapContentSize()) {
                        Text(dialogStatus.negativeButton)
                    }
                    TextButton(onClick = dialogStatus.positiveAction, modifier = Modifier.wrapContentSize()) {
                        Text(dialogStatus.positiveButton)
                    }
                }
            }
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
