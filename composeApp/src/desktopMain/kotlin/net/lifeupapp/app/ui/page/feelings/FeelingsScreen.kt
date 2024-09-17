package ui.page.feelings

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.lifeupapp.app.base.launchSafely
import net.lifeupapp.app.ui.text.StringText
import ui.AppStore
import ui.page.list.Dialog
import utils.md5
import java.awt.Desktop
import java.io.File
import java.net.URL
import javax.swing.JFileChooser

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FeelingsScreen(modifier: Modifier = Modifier) {
    val coroutineScope = rememberCoroutineScope()
    val globalStore = AppStore.current
    val model = remember { FeelingsStore(coroutineScope, globalStore) }
    val state = model.state

    FeelingsContent(modifier = modifier, items = state.feelings, onItemClicked = {
        // TODO
    }, onExportClicked = {
        model.setState { copy(showingDialog = true) }

    }, onRefreshClick = model::onRefresh, onAttachmentClicked = { attachment ->
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
    })

    if (state.showingDialog) {
        // AlertDialog()
        AlertDialog(onDismissRequest = {
            model.setState { copy(showingDialog = false) }
        }, title = {
            Text(StringText.feelings_export_dialog_title)
        }, text = {
            Text(StringText.feelings_export_dialog_desc)
        }, buttons = {
            Column {
                val groupByMethods = listOf(
                    GroupMethod(StringText.feelings_export_group_by_day, "yyyy-MM-dd"),
                    GroupMethod(StringText.feelings_export_group_by_month, "yyyy-MM"),
                    GroupMethod(StringText.feelings_export_group_by_year, "yyyy"),
                )
                val expended = remember { mutableStateOf(false) }
                val index = remember {
                    mutableStateOf(0)
                }

                TextButton(modifier = Modifier.fillMaxWidth().padding(start = 24.dp), onClick = {
                    expended.value = true
                }) {
                    Text(groupByMethods[index.value].name)
                }

                DropdownMenu(expanded = expended.value, onDismissRequest = {
                    expended.value = false
                }) {
                    groupByMethods.forEachIndexed { i, s ->
                        DropdownMenuItem(onClick = {
                            index.value = i
                            expended.value = false
                        }) {
                            Text(text = s.name, modifier = Modifier.padding(start = 10.dp))
                        }
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.padding(top = 16.dp, end = 24.dp).fillMaxWidth()
                ) {
                    TextButton(onClick = {
                        model.setState { copy(showingDialog = false) }
                    }) {
                        Text(StringText.cancel)
                    }

                    val dialogTitleString = StringText.common_dir_select_title
                    val approveButtonTextString = StringText.common_dir_select_button
                    val approveButtonToolTipTextString = StringText.common_dir_select_button_tooltip

                    TextButton(modifier = Modifier.padding(start = 16.dp), onClick = {
                        model.setState { copy(showingDialog = false) }
                        val fileChooser = JFileChooser("/").apply {
                            fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
                            this.dialogTitle = dialogTitleString
                            this.approveButtonText = approveButtonTextString
                            approveButtonToolTipText = approveButtonToolTipTextString
                        }
                        fileChooser.showOpenDialog(null /* OR null */)
                        val result = fileChooser.selectedFile
                        model.onExportDirSelected(result, groupByMethods[index.value].dateFormat)
                    }) {
                        Text(StringText.yes)
                    }
                }
            }
        })
    }

    if (state.exportProgress >= 0.0f) {
        // dialog
        Dialog(title = StringText.feelings_export_progress_dialog_title, onCloseRequest = {
            model.setState { copy(exportProgress = -1f) }
        }, content = {
            Column {
                // Text(Strings.feelings_export_progress_dialog_desc.format(state.exportProgress * 100))
                LinearProgressIndicator(
                    progress = state.exportProgress, modifier = Modifier.padding(top = 8.dp)
                )
            }
        })
    }
}

data class GroupMethod(val name: String, val dateFormat: String)