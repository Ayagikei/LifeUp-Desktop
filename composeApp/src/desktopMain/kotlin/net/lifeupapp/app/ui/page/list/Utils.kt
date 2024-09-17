@file:JvmName("Utils")

package ui.page.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.DialogWindowScope
import androidx.compose.ui.window.rememberDialogState

internal val MARGIN_SCROLLBAR: Dp = 8.dp

@Suppress("ACTUAL_WITHOUT_EXPECT") // Workaround https://youtrack.jetbrains.com/issue/KT-37316
internal typealias ScrollbarAdapter = androidx.compose.foundation.v2.ScrollbarAdapter

@Composable
internal fun rememberScrollbarAdapter(scrollState: LazyListState): ScrollbarAdapter =
    rememberScrollbarAdapter(scrollState)

@Composable
internal fun VerticalScrollbar(
    modifier: Modifier,
    adapter: androidx.compose.foundation.v2.ScrollbarAdapter
) {
    androidx.compose.foundation.VerticalScrollbar(
        modifier = modifier,
        adapter = adapter
    )
}

@Composable
internal fun Dialog(
    title: String,
    onCloseRequest: () -> Unit,
    content: @Composable () -> Unit
) {
    androidx.compose.ui.window.DialogWindow(
        onCloseRequest = onCloseRequest,
        focusable = true,
        title = title,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            contentAlignment = Alignment.Center,
        ) {
            content()
        }
    }
}


@Composable
fun EnhancedLargeDialog(
    onCloseRequest: () -> Unit,
    title: String,
    content: @Composable DialogWindowScope.() -> Unit
) {
    val dialogState = rememberDialogState(
        width = 800.dp,
        height = 600.dp
    )

    DialogWindow(
        onCloseRequest = onCloseRequest,
        state = dialogState,
        title = title,
        resizable = true,
        focusable = true
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            Column {
                // Custom title bar
                TopAppBar(
                    title = { Text(title) },
                    actions = {
                        IconButton(onClick = onCloseRequest) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    },
                    backgroundColor = MaterialTheme.colors.primary
                )

                // Content area with scrollbar
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    val scrollState = rememberScrollState()
                    val scrollAdapter = rememberScrollbarAdapter(scrollState)
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(end = 12.dp) // Make room for scrollbar
                            .verticalScroll(scrollState)
                    ) {
                        Box(modifier = Modifier.padding(16.dp)) {
                            content()
                        }
                    }
                    VerticalScrollbar(
                        modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                        adapter = scrollAdapter
                    )
                }
            }
        }
    }
}