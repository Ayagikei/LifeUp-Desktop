package net.lifeupapp.app.ui.page.status

import datasource.ApiServiceImpl
import datasource.data.Skill
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import logger
import net.lifeupapp.app.base.launchSafely
import net.lifeupapp.app.ui.AppStoreImpl
import java.util.logging.Level

internal class StatusStore(
    private val coroutineScope: CoroutineScope,
    private val globalStore: AppStoreImpl
) {

    private val apiService = ApiServiceImpl

    val state =
        MutableStateFlow(StatusState(0, skills = emptyList(), coin = globalStore.coinValue ?: 0))

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
        state.value = state.value.update()
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
            runCatching {
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
