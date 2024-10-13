package ui.page.feelings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import datasource.ApiServiceImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import logger
import net.lifeupapp.app.base.launchSafely
import net.lifeupapp.app.datasource.data.Feelings
import net.lifeupapp.app.ui.AppStoreImpl
import net.lifeupapp.app.ui.text.Localization
import utils.md5
import java.io.File
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.logging.Level


internal class FeelingsStore(
    private val coroutineScope: CoroutineScope,
    private val globalStore: AppStoreImpl
) {

    private val limit = 30
    private var offset = 0
    private var end: Boolean = false
    private val mutex = Mutex()

    private val apiService = ApiServiceImpl
    var state: FeelingsState by mutableStateOf(FeelingsState(0))
        private set

    data class FeelingsState(
        val state: Int,
        val feelings: List<Feelings> = emptyList(),
        val showingDialog: Boolean = false,
        val exportProgress: Float = -1f,
        val showAddedDialog: Boolean = false
    )

    init {
        fetchFeelings()
    }

    fun onRefresh() {
        coroutineScope.launchSafely {
            mutex.withLock {
                offset = 0
                end = false
                setState {
                    copy(feelings = emptyList())
                }
                fetchFeelings()
            }
        }
    }

    fun onAdd() {
        setState {
            copy(showAddedDialog = true)
        }
    }

    fun onCloseAddDialog() {
        setState {
            copy(showAddedDialog = false)
        }
    }

    // ...

    fun onExportDirSelected(dir: File, dateFormat: String) {
        coroutineScope.launchSafely(Dispatchers.IO) {
            setState {
                copy(showingDialog = false, exportProgress = 0f)
            }

            // wait for loading all feelings
            while (end.not()) {
                delay(1000L)
            }

            // group feelings by date
            val feelings = state.feelings
            val feelingsByDate = feelings.groupBy {
                SimpleDateFormat(dateFormat, Locale.US).format(it.time)
            }

            // create attachments directory
            val attachmentsDir = File(dir, "attachments")
            attachmentsDir.mkdir()

            // write each group to a markdown file
            val total = feelings.size
            var index = 0
            feelingsByDate.mapValues { (date, feelings) ->
                val file = File(dir, "$date.md")
                file.createNewFile()
                file.writeText("## $date\n\n")
                var previousDate: String? = null
                feelings.forEach { feeling ->
                    val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(feeling.time)
                    if (currentDate != previousDate && dateFormat != "yyyy-MM-dd") {
                        file.appendText("### $currentDate\n\n")
                        previousDate = currentDate
                    }
                    file.appendText("${feeling.content}\n\n")
                    feeling.attachments.forEach { attachment ->
                        val attachmentFile = File(attachmentsDir, attachment.md5() + ".jpg")
                        saveAttachmentFileTo(attachment, attachmentFile)
                        file.appendText("![](${attachmentFile.absolutePath})\n\n")
                    }

                    file.appendText(
                        "> ${feeling.title}\n> ${
                            Localization.dateTimeFormatter.format(
                                feeling.time
                            )
                        }\n\n<br />\n\n"
                    )
                    index++
                    setState {
                        copy(exportProgress = index / total.toFloat())
                    }
                }
            }
            setState { copy(exportProgress = -1f) }
        }
    }

    private fun saveAttachmentFileTo(attachment: String, destFile: File) {
        val connection = URL(attachment).openConnection()
        if (destFile.exists() && destFile.length() == connection.contentLengthLong) {
            return
        }
        destFile.createNewFile()
        val copied = connection.getInputStream().buffered().use {
            it.copyTo(destFile.outputStream())
        }
        // maybe failed

        if (copied == 0L) {
            try {
                val cachedFile = File(globalStore.cacheDir, attachment.md5() + ".jpg")
                if (cachedFile.exists() && cachedFile.length() >= 0) {
                    cachedFile.copyTo(destFile, overwrite = true)
                }
            } catch (ignore: Exception) {
                // do nothing
            }
        }
    }

    inline fun setState(update: FeelingsState.() -> FeelingsState) {
        state = state.update()
    }

    private fun fetchFeelings() {
        coroutineScope.launchSafely(Dispatchers.IO) {
            while (globalStore.isReadyToCall.not()) {
                delay(1000L)
            }
            mutex.withLock {
                runCatching {
                    apiService.getFeelings(offset, limit)
                }.onSuccess { it ->
                    offset += it.size
                    end = it.size < limit

                    setState {
                        copy(feelings = feelings + it.map {
                            it.copy(attachments = it.attachments.map {
                                apiService.getIconUrl(it)
                            })
                        })
                    }

                    if (!end) {
                        fetchFeelings()
                    }
                }.onFailure {
                    logger.log(Level.SEVERE, it.stackTraceToString())
                    delay(2000L)
                    fetchFeelings()
                }
            }
        }
    }

    fun addFeeling(content: String, time: Long, attachments: List<String>) {
        coroutineScope.launchSafely {
            val finalFiles = attachments.map { File(it) }.filter { it.exists() }
            logger.info("addFeeling finalFiles: $finalFiles")
            val transformUris = if (finalFiles.isEmpty()) emptyList<String>() else {
                apiService.uploadFilesToUris(finalFiles)
            }
            logger.info("addFeeling transformUris: $transformUris")
            apiService.createOrUpdateFeeling(
                content = content,
                time = time,
                imageUris = transformUris
            ).onSuccess {
                onCloseAddDialog()
                onRefresh()
            }.onFailure {
                // handle error toast

            }
        }

    }
}
