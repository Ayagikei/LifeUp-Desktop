package net.lifeupapp.app.ui.page.save

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.lifeupapp.app.datasource.ApiServiceImpl
import org.lighthousegames.logging.logging
import java.io.File
import java.net.URL

internal class SaveStore(
    private val coroutineScope: CoroutineScope,
) {
    private val log = logging("SaveStore")

    private val apiService = ApiServiceImpl

    var state: SaveState by mutableStateOf(SaveState(0))
        private set

    data class SaveState(
        val state: Int
    )

    fun onExportToDir(dir: File, withMedia: Boolean) {
        coroutineScope.launch {
            runCatching {
                apiService.exportDataToDir(withMedia)
            }.onSuccess {
                runCatching {
                    log.info {
                        "Export data with url: $it"
                    }
                    URL(it).openStream().use { input ->
                        val file = File(dir, "backup_${System.currentTimeMillis()}.lfbak")
                        if (file.exists()) {
                            file.delete()
                        }
                        file.parentFile.mkdirs()
                        file.createNewFile()
                        file.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                }.onSuccess {
                    log.info {
                        "Export data success"
                    }
                }.onFailure {
                    log.error(it) {
                        "Failed to export data"
                    }
                }
            }.onFailure {
                log.error(it) {
                    "Failed to export data"
                }
            }
        }
    }

    fun onImportBackup(file: File) {
        coroutineScope.launch {
            runCatching {
                apiService.importData(file)
            }.onSuccess {
                log.info {
                    "Import data success"
                }
            }.onFailure {
                log.error(it) {
                    "Failed to import data"
                }
            }
        }
    }
}
