package net.lifeupapp.app.ui.page.save

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.awtTransferable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ui.page.list.VerticalScrollbar
import ui.page.list.rememberScrollbarAdapter
import java.awt.datatransfer.DataFlavor
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
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
                    "Backup and Restore",
                    style = MaterialTheme.typography.h5
                )
            }

            item {
                // Export Section
                Text(
                    "Export Backup",
                    style = MaterialTheme.typography.h6
                )
            }

            item {
                // Export Backup Button
                Button(
                    onClick = {
                        val selectedDir = showDirectoryChooser(
                            "Select Export Directory",
                            "Export",
                            "Choose directory for backup export"
                        )
                        selectedDir?.let { dir ->
                            exportPath = dir.absolutePath
                            saveStore.onExportToDir(dir, withMedia = includeMedia)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Export")
                    Spacer(Modifier.width(8.dp))
                    Text("Choose Export Directory")
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
                    Text("Include media files in backup", style = MaterialTheme.typography.body2)
                }
            }

            item {
                exportPath?.let {
                    Text("Export Path: $it", style = MaterialTheme.typography.body2)
                }
            }

            item {
                Divider(modifier = Modifier.padding(vertical = 16.dp))
            }

            item {
                // Import Section
                Text(
                    "Import Backup",
                    style = MaterialTheme.typography.h6
                )
            }

            item {
                // Drag and Drop Area
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
                            "Imported: $importedFileName",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        Text(
                            "Drag and drop your backup file here",
                            modifier = Modifier.align(Alignment.Center),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            item {
                // Or divider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Divider(Modifier.weight(1f))
                    Text(
                        "OR",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        style = MaterialTheme.typography.body2
                    )
                    Divider(Modifier.weight(1f))
                }
            }

            item {
                // Import Button
                Button(
                    onClick = {
                        val files = showBackupFileChooser()
                        if (files.isNotEmpty()) {
                            importedFileName = File(files.first()).name
                            saveStore.onImportBackup(File(files.first()))
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Import")
                    Spacer(Modifier.width(8.dp))
                    Text("Choose Backup File")
                }
            }
        }

        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            adapter = rememberScrollbarAdapter(scrollState)
        )
    }
}

fun showDirectoryChooser(
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


fun showBackupFileChooser(): List<String> {
    val fileChooser = JFileChooser().apply {
        fileSelectionMode = JFileChooser.FILES_ONLY
        isMultiSelectionEnabled = false
        dialogTitle = "Select Backup File"
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