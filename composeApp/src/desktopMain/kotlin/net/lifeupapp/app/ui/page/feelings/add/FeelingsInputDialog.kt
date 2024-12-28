package net.lifeupapp.app.ui.page.feelings.add

import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.rememberDialogState
import kotlinx.coroutines.launch
import lifeupdesktop.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import ui.page.list.rememberScrollbarAdapter
import ui.view.AsyncImage
import ui.view.loadImageBitmap
import java.io.File
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.util.*
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeelingsInputDialog(
    onCloseRequest: () -> Unit,
    onSubmit: (content: String, timestamp: Long, attachments: List<String>) -> Unit
) {
    val dialogState = rememberDialogState(width = 800.dp, height = 700.dp)
    var content by remember { mutableStateOf("") }
    var selectedImagePaths by remember { mutableStateOf(listOf<String>()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val maxImages = 9

    val currentDate = remember { Calendar.getInstance() }
    val datePickerState =
        rememberDatePickerState(initialSelectedDateMillis = currentDate.timeInMillis)
    val timePickerState = rememberTimePickerState(
        initialHour = currentDate.get(Calendar.HOUR_OF_DAY),
        initialMinute = currentDate.get(Calendar.MINUTE),
        is24Hour = true
    )

    DialogWindow(
        onCloseRequest = onCloseRequest,
        state = dialogState,
        title = stringResource(Res.string.feelings_input_dialog_title),
        resizable = true
    ) {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(scrollState)
            ) {
                // Title bar with submit button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(stringResource(Res.string.feelings_input_dialog_title), style = MaterialTheme.typography.h6)
                    Button(
                        onClick = {
                            if (content.isNotBlank()) {
                                coroutineScope.launch {
                                    val timestamp =
                                        combineDateTime(datePickerState, timePickerState)
                                    onSubmit(content, timestamp, selectedImagePaths)
                                    onCloseRequest()
                                }
                            }
                        }
                    ) {
                        Text(stringResource(Res.string.feelings_input_dialog_submit))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Content input
                TextField(
                    value = content,
                    onValueChange = { content = it },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 200.dp),
                    label = { Text(stringResource(Res.string.feelings_input_content_label)) },
                    placeholder = { Text(stringResource(Res.string.feelings_input_content_placeholder)) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Date and Time selection
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(onClick = { showDatePicker = true }) {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = stringResource(Res.string.feelings_input_select_date)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            stringResource(
                                Res.string.feelings_input_date_format,
                                getSelectedDateString(datePickerState)
                            )
                        )
                    }
                    OutlinedButton(onClick = { showTimePicker = true }) {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = stringResource(Res.string.feelings_input_select_time)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            stringResource(
                                Res.string.feelings_input_time_format,
                                getSelectedTimeString(timePickerState)
                            )
                        )
                    }
                }

                if (showDatePicker) {
                    DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        confirmButton = {
                            TextButton(onClick = { showDatePicker = false }) {
                                Text(stringResource(Res.string.feelings_input_ok))
                            }
                        }
                    ) {
                        DatePicker(state = datePickerState)
                    }
                }

                if (showTimePicker) {
                    TimePickerDialog(
                        onDismiss = { showTimePicker = false },
                        onConfirm = { showTimePicker = false }
                    ) {
                        TimePicker(state = timePickerState)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Image attachment button
                if (selectedImagePaths.size < maxImages) {
                    OutlinedButton(
                        onClick = {
                            coroutineScope.launch {
                                val paths =
                                    showImageFileChooser(org.jetbrains.compose.resources.getString(Res.string.feelings_input_select_images))
                                if (paths.isNotEmpty()) {
                                    val newPaths = (selectedImagePaths + paths).take(maxImages)
                                    selectedImagePaths = newPaths
                                }
                            }
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = stringResource(Res.string.feelings_input_add_image)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(Res.string.feelings_input_add_image, selectedImagePaths.size, maxImages))
                    }
                } else {
                    Text(
                        stringResource(Res.string.feelings_input_max_images, maxImages),
                        style = MaterialTheme.typography.caption,
                        modifier = Modifier.align(Alignment.End)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Image preview
                if (selectedImagePaths.isNotEmpty()) {
                    Text(
                        stringResource(Res.string.feelings_input_selected_images),
                        style = MaterialTheme.typography.subtitle1
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    val listState = rememberLazyListState()
                    LazyRow(state = listState) {
                        itemsIndexed(selectedImagePaths, key = { index, path ->
                            path
                        }) { index, path ->
                            Box(modifier = Modifier.padding(end = 8.dp)) {
                                AsyncImage(
                                    condition = File(path).exists(),
                                    load = { loadImageBitmap(File(path)) },
                                    painterFor = {
                                        remember(path) { BitmapPainter(it) }
                                    },
                                    contentDescription = stringResource(
                                        Res.string.feelings_input_attachment_desc,
                                        index
                                    ),
                                    modifier = Modifier.size(156.dp),
                                    contentScale = ContentScale.Crop,
                                    onError = {
                                        Image(
                                            painter = org.jetbrains.compose.resources.painterResource(
                                                Res.drawable.ic_pic_loading_cir
                                            ),
                                            contentDescription = "skill icon",
                                            modifier = Modifier.size(40.dp)
                                        )
                                    }
                                )
                                IconButton(
                                    onClick = {
                                        selectedImagePaths = selectedImagePaths - path
                                    },
                                    modifier = Modifier.align(Alignment.TopEnd)
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = stringResource(Res.string.feelings_input_delete_image),
                                        tint = androidx.compose.ui.graphics.Color.Red
                                    )
                                }
                            }
                        }
                    }
                    HorizontalScrollbar(
                        modifier = Modifier.align(Alignment.CenterHorizontally).fillMaxWidth(),
                        adapter = rememberScrollbarAdapter(scrollState = listState)
                    )
                }
            }
        }
    }
}

fun showImageFileChooser(title: String): List<String> {
    val fileChooser = JFileChooser().apply {
        fileSelectionMode = JFileChooser.FILES_ONLY
        isMultiSelectionEnabled = true
        dialogTitle = title
        fileFilter = FileNameExtensionFilter("Image files", "jpg", "jpeg", "png", "gif", "webp")
    }

    val result = fileChooser.showOpenDialog(null)
    return if (result == JFileChooser.APPROVE_OPTION) {
        fileChooser.selectedFiles.map { it.absolutePath }
    } else {
        emptyList()
    }
}

@Composable
fun TimePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.feelings_input_select_time)) },
        text = { content() },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(Res.string.feelings_input_ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.feelings_input_cancel))
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
fun getSelectedDateString(state: DatePickerState): String {
    return state.selectedDateMillis?.let { millis ->
        val date = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
        "${date.month.name.lowercase().capitalize()} ${date.dayOfMonth}, ${date.year}"
    } ?: "Not set"
}

@OptIn(ExperimentalMaterial3Api::class)
fun getSelectedTimeString(state: TimePickerState): String {
    return String.format("%02d:%02d", state.hour, state.minute)
}

@OptIn(ExperimentalMaterial3Api::class)
fun combineDateTime(dateState: DatePickerState, timeState: TimePickerState): Long {
    val date = dateState.selectedDateMillis?.let {
        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
    } ?: LocalDate.now()
    val time = LocalTime.of(timeState.hour, timeState.minute)
    return date.atTime(time).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
}
