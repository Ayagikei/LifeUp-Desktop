package net.lifeupapp.app.ui.page.save

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import datasource.ApiServiceImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import logger
import java.io.File
import java.net.URL
import java.util.logging.Level

internal class SaveStore(
    private val coroutineScope: CoroutineScope,
) {

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
                    logger.log(Level.INFO, "Export data with url: $it")
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
                    logger.log(Level.INFO, "Export data success")
                }.onFailure {
                    logger.log(Level.SEVERE, "Failed to export data", it)
                }
            }.onFailure {
                logger.log(Level.SEVERE, "Failed to export data", it)
            }
        }
    }

    fun onImportBackup(file: File) {
        coroutineScope.launch {
            runCatching {
                apiService.importData(file)
            }.onSuccess {

            }.onFailure {

            }
        }
    }
}
