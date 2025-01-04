package net.lifeupapp.app.ui.page.achievement

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import net.lifeupapp.app.base.launchSafely
import net.lifeupapp.app.datasource.ApiServiceImpl
import net.lifeupapp.app.datasource.data.Achievement
import net.lifeupapp.app.datasource.data.AchievementCategory
import net.lifeupapp.app.ui.AppStoreImpl
import net.lifeupapp.app.utils.logger

internal class AchievementStore(
    private val coroutineScope: CoroutineScope,
    private val globalStore: AppStoreImpl
) {

    private val log = this.logger

    private val apiService = ApiServiceImpl
    var state: AchievementState by mutableStateOf(
        AchievementState(
            0,
            achievements = emptyList(),
            categories = emptyList()
        )
    )
        private set

    data class AchievementState(
        val state: Int,
        val categories: List<AchievementCategory>,
        val achievements: List<Achievement> = emptyList(),
        val currentCategoryId: Long? = null,
        val categoryExpanded: Boolean = false,
        val editingItem: Achievement? = null
    )

    init {
        fetchCategories()
    }

    fun onRefresh() {
        fetchCategories()
    }

    fun onItemClicked(id: Long) {
        setState {
            copy(editingItem = achievements.find { it.id == id })
        }
    }

    fun onExitItemDetails() {
        setState {
            copy(editingItem = null)
        }
    }

    fun onCategoryClicked(id: Long) {
        setState {
            copy(categoryExpanded = false, currentCategoryId = id, achievements = emptyList())
        }
        fetchAchievement(id)
    }

    fun onCategoryExpended() {
        setState {
            copy(categoryExpanded = true)
        }
    }

    fun onCategoryDismissed() {
        setState {
            copy(categoryExpanded = false)
        }
    }

    private inline fun setState(update: AchievementState.() -> AchievementState) {
        state = state.update()
    }


    private fun fetchCategories() {
        coroutineScope.launchSafely(Dispatchers.IO) {
            while (globalStore.isReadyToCall.not()) {
                delay(1000L)
            }
            runCatching {
                apiService.getAchievementCategories()
            }.onSuccess { it ->
                val categories =
                    it.filter { it.type != AchievementCategory.AchievementType.SYSTEM.type }
                if (state.currentCategoryId == null || state.currentCategoryId !in it.map { it.id }) {
                    setState {
                        copy(
                            categories = categories,
                            currentCategoryId = categories
                                .firstOrNull()?.id
                        )
                    }
                }
                fetchAchievement(state.currentCategoryId ?: return@launchSafely)
            }.onFailure {
                log.error(it) {
                    "fetchCategories"
                }
                delay(2000L)
                fetchCategories()
            }
        }
    }

    // FIXME: hare may be exist a concurrent problem
    private fun fetchAchievement(categoryId: Long) {
        coroutineScope.launchSafely(Dispatchers.IO) {
            runCatching {
                apiService.getAchievement(categoryId)
            }.onSuccess { it ->
                setState {
                    copy(achievements = it.map {
                        it.copy(
                            iconUri = apiService.getIconUrl(it.iconUri)
                        )
                    })
                }
            }.onFailure {
                log.error(it) {
                    "fetchAchievement"
                }
                delay(2000L)
                fetchCategories()
            }
        }
    }
}
