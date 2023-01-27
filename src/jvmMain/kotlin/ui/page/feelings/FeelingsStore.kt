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
        val feelings: List<Feelings> = emptyList()
    )

    init {
        fetchFeelings()
    }

    fun onRefresh() {
        fetchFeelings()
    }

    private inline fun setState(update: FeelingsState.() -> FeelingsState) {
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
