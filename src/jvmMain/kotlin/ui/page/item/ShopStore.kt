package ui.page.item

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import base.launchSafely
import datasource.ApiServiceImpl
import datasource.data.ShopCategory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import logger
import net.lifeupapp.lifeup.api.content.shop.ShopItem
import ui.AppStoreImpl
import java.util.logging.Level

internal class ShopStore(
    private val coroutineScope: CoroutineScope,
    private val globalStore: AppStoreImpl
) {

    private val apiService = ApiServiceImpl
    var state: ShopState by mutableStateOf(
        ShopState(
            0,
            shopItems = emptyList(),
            categories = emptyList()
        )
    )
        private set

    data class ShopState(
        val state: Int,
        val categories: List<ShopCategory>,
        val shopItems: List<ShopItem> = emptyList(),
        val currentCategoryId: Long? = null,
        val categoryExpanded: Boolean = false,
        val editingItem: ShopItem? = null,
        val coin: Long? = null
    )

    init {
        onRefresh()
    }

    fun onRefresh() {
        fetchCategories()
        fetchCoin()
    }

    fun onItemClicked(id: Long) {
        setState {
            copy(editingItem = shopItems.find { it.id == id })
        }
    }

    fun onExitItemDetails() {
        setState {
            copy(editingItem = null)
        }
    }

    fun onCategoryClicked(id: Long) {
        setState {
            copy(categoryExpanded = false, currentCategoryId = id, shopItems = emptyList())
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

    private inline fun setState(update: ShopState.() -> ShopState) {
        state = state.update()
    }

    private fun fetchCoin() {
        coroutineScope.launchSafely(Dispatchers.IO) {
            while (globalStore.isReadyToCall.not()) {
                delay(1000L)
            }
            kotlin.runCatching {
                apiService.getCoin()
            }.onSuccess { it ->
                setState {
                    copy(coin = it)
                }
            }.onFailure {
                logger.log(Level.SEVERE, it.stackTraceToString())
                delay(2000L)
                fetchCategories()
            }
        }
    }

    private fun fetchCategories() {
        coroutineScope.launchSafely(Dispatchers.IO) {
            while (globalStore.isReadyToCall.not()) {
                delay(1000L)
            }
            kotlin.runCatching {
                apiService.getShopItemCategories()
            }.onSuccess { it ->
                val categories = it
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
                logger.log(Level.SEVERE, it.stackTraceToString())
                delay(2000L)
                fetchCategories()
            }
        }
    }

    // FIXME: hare may be exist a concurrent problem
    private fun fetchAchievement(categoryId: Long) {
        coroutineScope.launchSafely(Dispatchers.IO) {
            kotlin.runCatching {
                apiService.getShopItems(categoryId)
            }.onSuccess { it ->
                setState {
                    copy(shopItems = it.map {
                        it.copy(
                            icon = apiService.getIconUrl(it.icon)
                        )
                    })
                }
            }.onFailure {
                logger.log(Level.SEVERE, it.stackTraceToString())
                delay(2000L)
                fetchCategories()
            }
        }
    }

    fun onPurchased(shopItem: ShopItem) {
        //
    }
}
