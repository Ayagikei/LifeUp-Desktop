@file:JvmName("Utils")

package ui.page.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

internal val MARGIN_SCROLLBAR: Dp = 8.dp

@Suppress("ACTUAL_WITHOUT_EXPECT") // Workaround https://youtrack.jetbrains.com/issue/KT-37316
internal typealias ScrollbarAdapter = androidx.compose.foundation.ScrollbarAdapter

@Composable
internal fun rememberScrollbarAdapter(scrollState: LazyListState): androidx.compose.foundation.v2.ScrollbarAdapter =
    androidx.compose.foundation.rememberScrollbarAdapter(scrollState)

@Composable
internal fun VerticalScrollbar(
    modifier: Modifier,
    adapter: ScrollbarAdapter
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
    androidx.compose.ui.window.Dialog(
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
