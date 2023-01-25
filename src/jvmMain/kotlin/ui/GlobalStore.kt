package ui

import AppScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import datasource.ApiService
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import logger
import okhttp3.HttpUrl
import ui.text.EnStrings
import ui.text.StringText
import java.util.concurrent.atomic.AtomicBoolean
import java.util.logging.Level
import java.util.prefs.Preferences

object GlobalStore {

    private val apiService = ApiService.instance

    var strings = EnStrings()
        private set


    var dialogStatus: DialogStatus? by mutableStateOf(null)
        private set

    var coinValue: Long? by mutableStateOf(null)
        private set

    val preferences = Preferences.userRoot()

    var ip by mutableStateOf(preferences.get("ip", ""))
    var port by mutableStateOf(preferences.get("port", "13276"))

    var isReadyToCall = false
        private set

    private var fetchCoin: Boolean = false
    private val fetchCoinFlow = flow {
        while (true) {
            if (fetchCoin) {
                kotlin.runCatching {
                    apiService.getCoin()
                }.onSuccess {
                    emit(it)
                }.onFailure {
                    emit(null)
                    logger.log(Level.SEVERE, "get coin error", it)
                }
            }
            kotlinx.coroutines.delay(1500)
        }
    }.flowOn(AppScope.coroutineContext)

    init {
        updateIpOrPort()
    }


    fun updateIpOrPort(ip: String = GlobalStore.ip, port: String = GlobalStore.port) {
        this.ip = ip
        this.port = port

        val validHost = kotlin.runCatching {
            HttpUrl.Builder().scheme("http").host(ip).port(port = port.toIntOrNull() ?: 13276).build()
        }.onFailure {
            logger.log(Level.SEVERE, "update ip or port error", it)
        }.isSuccess

        isReadyToCall = validHost
        if (isReadyToCall) {
            Preferences.userRoot().apply {
                put("ip", ip)
                put("port", port)
            }
        }
    }

    private var fetching = AtomicBoolean(false)
    fun fetchCoin() {
        if (fetching.get()) {
            return
        }
        AppScope.launch {
            kotlin.runCatching {
                fetching.set(true)
                apiService.getCoin()
            }.onSuccess {
                coinValue = it
            }.onFailure {
                coinValue = null
                logger.log(Level.SEVERE, "get coin error", it)
            }
            fetching.set(false)
        }
    }


    fun showDialog(
        title: String,
        message: String = "",
        positiveButton: String = strings.yes,
        negativeButton: String = strings.cancel,
        negativeAction: () -> Unit = {
            dialogStatus = null
        },
        positiveAction: () -> Unit
    ) {
        dialogStatus = DialogStatus(
            title, message, positiveButton, negativeButton, positiveAction, negativeAction
        )
    }
}

val Strings: StringText
    get() = GlobalStore.strings