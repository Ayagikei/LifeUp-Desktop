package ui.page.feelings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import base.launchSafely
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ui.AppStore
import utils.md5
import java.awt.Desktop
import java.io.File
import java.net.URL

@Composable
fun FeelingsScreen(modifier: Modifier = Modifier) {
    val coroutineScope = rememberCoroutineScope()
    val globalStore = AppStore.current
    val model = remember { FeelingsStore(coroutineScope, globalStore) }
    val state = model.state

    FeelingsContent(
        modifier = modifier,
        items = state.feelings,
        onItemClicked = {
            // TODO
        },
        onRefreshClick = model::onRefresh,
        onAttachmentClicked = { attachment ->
            coroutineScope.launchSafely {
                withContext(Dispatchers.IO) {
                    if (!Desktop.isDesktopSupported() || Desktop.getDesktop().isSupported(Desktop.Action.OPEN).not()) {
                        return@withContext
                    }
                    val destFile = File(globalStore.cacheDir, attachment.md5() + ".jpg")
                    if (destFile.exists() && destFile.length() > 0) {
                        //
                    } else {
                        destFile.createNewFile()
                        URL(attachment).openStream().buffered().use {
                            it.copyTo(destFile.outputStream())
                        }
                    }
                    Desktop.getDesktop().open(destFile)
                }
            }
        }
    )
}