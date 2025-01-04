package net.lifeupapp.app.ui.page.status

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import net.lifeupapp.app.base.Cache
import net.lifeupapp.app.base.launchSafely
import net.lifeupapp.app.datasource.ApiServiceImpl
import net.lifeupapp.app.datasource.data.Skill
import net.lifeupapp.app.ui.AppStoreImpl
import net.lifeupapp.app.ui.view.PageState
import net.lifeupapp.app.utils.logger

internal class StatusStore(
    private val coroutineScope: CoroutineScope,
    private val globalStore: AppStoreImpl
) {

    private val apiService = ApiServiceImpl
    private val log = this.logger

    val state = MutableStateFlow(
        StatusState(
            pageState = PageState.Loading,
            skills = emptyList(),
            coin = globalStore.coinValue ?: 0
        )
    )

    data class StatusState(
        val pageState: PageState,
        val skills: List<Skill> = emptyList(),
        val coin: Long
    )

    private val skillsCache = Cache {
        apiService.getSkills().map {
            it.copy(
                icon = if (it.icon.isBlank()) "" else {
                    apiService.getIconUrl(it.icon)
                }
            )
        }
    }

    init {
        fetchSkills()
    }

    fun onRefresh() {
        skillsCache.clear()
        setState { copy(pageState = PageState.Loading) }
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
                    skillsCache.get()
                }
                val coin = async {
                    apiService.getCoin()
                }
                RequestResult(skills.await(), coin.await().getOrThrow())
            }.onSuccess { it ->
                val skills = it.skills
                val coin = it.coin
                setState {
                    copy(
                        skills = skills,
                        coin = coin,
                        pageState = if (skills.isEmpty()) PageState.Empty else PageState.Content
                    )
                }
            }.onFailure {
                log.error(it) {
                    "fetchSkills"
                }
                setState { copy(pageState = PageState.Error(it.message ?: "未知错误")) }
                delay(2000L)
                fetchSkills()
            }
        }
    }
}
