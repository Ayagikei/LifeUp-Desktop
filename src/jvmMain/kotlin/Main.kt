import androidx.compose.material.MaterialTheme
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.*
import datasource.ApiServiceImpl
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ui.list.RootContent
import java.util.logging.Level
import java.util.logging.Logger

@Composable
@Preview
fun app() {
    MaterialTheme {
        RootContent(
            modifier = Modifier.fillMaxSize()
        )
    }
}

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
