package ui.page.feelings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import base.launchSafely
import datasource.ApiServiceImpl
import datasource.data.Feelings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import logger
import ui.AppStoreImpl
import ui.text.Localization
import utils.md5
import java.io.File
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
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
        val exportProgress: Float = -1f
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
                    if (currentDate != previousDate) {
                        file.appendText("### $currentDate\n\n")
                        previousDate = currentDate
                    }
                    file.appendText("${feeling.content}\n\n")
                    feeling.attachments.forEach { attachment ->
                        val attachmentFile = File(attachmentsDir, attachment.md5())
                        saveAttachmentFileTo(attachment, attachmentFile)
                        file.appendText("![](${attachmentFile.absolutePath})\n\n")
                    }

                    file.appendText("> ${feeling.title}\n> ${Localization.dateTimeFormatter.format(feeling.time)}\n\n<br />\n\n")
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
        connection.getInputStream().buffered().use {
            it.copyTo(destFile.outputStream())
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
                kotlin.runCatching {
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
}
