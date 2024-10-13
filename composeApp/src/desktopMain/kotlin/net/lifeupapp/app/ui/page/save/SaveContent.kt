package net.lifeupapp.app.ui.page.save

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.awtTransferable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import lifeupdesktop.composeapp.generated.resources.Res
import lifeupdesktop.composeapp.generated.resources.save_and_restore
import lifeupdesktop.composeapp.generated.resources.save_choose_backup_file
import lifeupdesktop.composeapp.generated.resources.save_choose_directory_for_backup
import lifeupdesktop.composeapp.generated.resources.save_choose_export_directory
import lifeupdesktop.composeapp.generated.resources.save_drag_drop_instruction
import lifeupdesktop.composeapp.generated.resources.save_export
import lifeupdesktop.composeapp.generated.resources.save_export_button
import lifeupdesktop.composeapp.generated.resources.save_export_path
import lifeupdesktop.composeapp.generated.resources.save_import
import lifeupdesktop.composeapp.generated.resources.save_imported_file
import lifeupdesktop.composeapp.generated.resources.save_include_media_files
import lifeupdesktop.composeapp.generated.resources.save_or
import lifeupdesktop.composeapp.generated.resources.save_select_backup_file
import lifeupdesktop.composeapp.generated.resources.save_select_export_directory
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import ui.page.list.VerticalScrollbar
import ui.page.list.rememberScrollbarAdapter
import java.awt.datatransfer.DataFlavor
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

@OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalComposeUiApi::class
)
@Composable
fun SaveScreen(modifier: Modifier = Modifier) {
    val coroutineScope = rememberCoroutineScope()
    val saveStore = remember { SaveStore(coroutineScope) }
    var importedFileName by remember { mutableStateOf<String?>(null) }
    var exportPath by remember { mutableStateOf<String?>(null) }
    var includeMedia by remember { mutableStateOf(true) }

    val scrollState = rememberLazyListState()

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            state = scrollState,
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    stringResource(Res.string.save_and_restore),
                    style = MaterialTheme.typography.h5
                )
            }

            item {
                Text(
                    stringResource(Res.string.save_export),
                    style = MaterialTheme.typography.h6
                )
            }

            item {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            val selectedDir = showDirectoryChooser(
                                getString(Res.string.save_select_export_directory),
                                getString(Res.string.save_export_button),
                                getString(Res.string.save_choose_directory_for_backup)
                            )
                            selectedDir?.let { dir ->
                                exportPath = dir.absolutePath
                                saveStore.onExportToDir(dir, withMedia = includeMedia)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowUp,
                        contentDescription = stringResource(Res.string.save_export_button)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(Res.string.save_choose_export_directory))
                }
            }

            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = includeMedia,
                        onCheckedChange = { includeMedia = it }
                    )
                    Text(
                        stringResource(Res.string.save_include_media_files),
                        style = MaterialTheme.typography.body2
                    )
                }
            }

            item {
                exportPath?.let {
                    Text(
                        stringResource(Res.string.save_export_path, it),
                        style = MaterialTheme.typography.body2
                    )
                }
            }

            item {
                Divider(modifier = Modifier.padding(vertical = 16.dp))
            }

            item {
                Text(
                    stringResource(Res.string.save_import),
                    style = MaterialTheme.typography.h6
                )
            }

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .border(2.dp, Color.Gray, RoundedCornerShape(8.dp))
                        .padding(16.dp)
                        .dragAndDropTarget(
                            shouldStartDragAndDrop = { true },
                            target = object : DragAndDropTarget {
                                override fun onDrop(event: DragAndDropEvent): Boolean {
                                    val file =
                                        event.awtTransferable.getTransferData(DataFlavor.javaFileListFlavor) as? List<File>
                                    file?.firstOrNull()?.let {
                                        importedFileName = it.name
                                        saveStore.onImportBackup(it)
                                    }
                                    return true
                                }
                            }
                        )
                ) {
                    if (importedFileName != null) {
                        Text(
                            stringResource(Res.string.save_imported_file, importedFileName!!),
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        Text(
                            stringResource(Res.string.save_drag_drop_instruction),
                            modifier = Modifier.align(Alignment.Center),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Divider(Modifier.weight(1f))
                    Text(
                        stringResource(Res.string.save_or),
                        modifier = Modifier.padding(horizontal = 16.dp),
                        style = MaterialTheme.typography.body2
                    )
                    Divider(Modifier.weight(1f))
                }
            }

            item {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            val files = showBackupFileChooser()
                            if (files.isNotEmpty()) {
                                importedFileName = File(files.first()).name
                                saveStore.onImportBackup(File(files.first()))
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        contentDescription = stringResource(Res.string.save_import)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(Res.string.save_choose_backup_file))
                }
            }
        }

        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            adapter = rememberScrollbarAdapter(scrollState)
        )
    }
}

suspend fun showDirectoryChooser(
    dialogTitle: String,
    approveButtonText: String,
    approveButtonToolTip: String
): File? {
    val fileChooser = JFileChooser("/").apply {
        fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
        this.dialogTitle = dialogTitle
        this.approveButtonText = approveButtonText
        approveButtonToolTipText = approveButtonToolTip
    }
    val result = fileChooser.showDialog(null, approveButtonText)
    return if (result == JFileChooser.APPROVE_OPTION) fileChooser.selectedFile else null
}

suspend fun showBackupFileChooser(): List<String> {
    val fileChooser = JFileChooser().apply {
        fileSelectionMode = JFileChooser.FILES_ONLY
        isMultiSelectionEnabled = false
        dialogTitle = getString(Res.string.save_select_backup_file)
        fileFilter = FileNameExtensionFilter("Backup files", "lfbak")
    }

    val result = fileChooser.showOpenDialog(null)
    return if (result == JFileChooser.APPROVE_OPTION) {
        listOf(fileChooser.selectedFile.absolutePath)
    } else {
        emptyList()
    }
}

@Preview
@Composable
fun SaveScreenPreview() {
    MaterialTheme {
        SaveScreen()
    }
}