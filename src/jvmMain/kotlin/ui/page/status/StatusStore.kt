package ui.page.status

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import base.launchSafely
import datasource.ApiServiceImpl
import datasource.data.Skill
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import logger
import ui.AppStoreImpl
import java.util.logging.Level

internal class StatusStore(
    private val coroutineScope: CoroutineScope,
    private val globalStore: AppStoreImpl
) {

    private val apiService = ApiServiceImpl
    var state: StatusState by mutableStateOf(StatusState(0, skills = emptyList(), coin = globalStore.coinValue ?: 0))
        private set

    data class StatusState(
        val state: Int,
        val skills: List<Skill> = emptyList(),
        val coin: Long
    )

    init {
        fetchSkills()
    }

    fun onRefresh() {
        fetchSkills()
    }

    private inline fun setState(update: StatusState.() -> StatusState) {
        state = state.update()
    }

    data class RequestResult(
        val skills: List<Skill>,
        val coin: Long
    )

    private fun fetchSkills() {
        coroutineScope.launchSafely(Dispatchers.IO) {
            while (globalStore.isReadyToCall.not()) {
                delay(1000L)
            }
            kotlin.runCatching {
                val skills = async {
                    apiService.getSkills()
                }
                val coin = async {
                    apiService.getCoin()
                }
                RequestResult(skills.await(), coin.await())
            }.onSuccess { it ->
                val skills = it.skills
                val coin = it.coin
                setState {
                    copy(skills = skills.map {
                        it.copy(
                            icon = if (it.icon.isBlank()) "" else {
                                apiService.getIconUrl(it.icon)
                            }
                        )
                    }, coin = coin)
                }
            }.onFailure {
                logger.log(Level.SEVERE, it.stackTraceToString())
                delay(2000L)
                fetchSkills()
            }
        }
    }
}
