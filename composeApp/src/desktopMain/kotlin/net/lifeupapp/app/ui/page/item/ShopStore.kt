package net.lifeupapp.app.ui.page.item

import datasource.ApiServiceImpl
import datasource.data.ShopCategory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import logger
import net.lifeupapp.app.base.launchSafely
import net.lifeupapp.app.datasource.constants.ItemPurchaseResult
import net.lifeupapp.app.datasource.data.ShopItem
import net.lifeupapp.app.ui.AppStoreImpl
import java.util.logging.Level

internal class ShopStore(
    private val coroutineScope: CoroutineScope,
    private val globalStore: AppStoreImpl
) {

    private val apiService = ApiServiceImpl

    private val purchaseResultEvent = Channel<ItemPurchaseResult>()
    val purchaseResultEventFlow = purchaseResultEvent.receiveAsFlow()

    var state = MutableStateFlow(
        ShopState(
            0,
            shopItems = emptyList(),
            categories = emptyList()
        )
    )

    data class ShopState(
        val state: Int,
        val categories: List<ShopCategory>,
        val shopItems: List<ShopItem> = emptyList(),
        val currentCategoryId: Long? = null,
        val categoryExpanded: Boolean = false,
        val editingItem: ShopItem? = null,
        val coin: Long? = null,
        val isReadyToBuyNext: Boolean = true
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
        fetchItems(id)
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
        state.value = state.value.update()
    }

    private fun fetchCoin() {
        coroutineScope.launchSafely(Dispatchers.IO) {
            while (globalStore.isReadyToCall.not()) {
                delay(1000L)
            }
            runCatching {
                apiService.getCoin()
            }.onSuccess {
                setState {
                    copy(coin = it.getOrNull())
                }
            }.onFailure {
                logger.log(Level.SEVERE, it.stackTraceToString())
                delay(2000L)
                fetchCoin()
            }
        }
    }

    private fun fetchCategories() {
        coroutineScope.launchSafely(Dispatchers.IO) {
            while (globalStore.isReadyToCall.not()) {
                delay(1000L)
            }
            runCatching {
                apiService.getShopItemCategories()
            }.onSuccess { it ->
                val categories = it
                if (state.value.currentCategoryId == null || state.value.currentCategoryId !in it.map { it.id }) {
                    setState {
                        copy(
                            categories = categories,
                            currentCategoryId = categories
                                .firstOrNull()?.id
                        )
                    }
                }
                fetchItems(state.value.currentCategoryId ?: return@launchSafely)
            }.onFailure {
                logger.log(Level.SEVERE, it.stackTraceToString())
                delay(2000L)
                fetchCategories()
            }
        }
    }

    // FIXME: hare may be exist a concurrent problem
    private fun fetchItems(categoryId: Long) {
        coroutineScope.launchSafely(Dispatchers.IO) {
            runCatching {
                apiService.getShopItems(categoryId)
            }.onSuccess { it ->
                setState {
                    copy(
                        shopItems = it.map {
                            it.copy(
                                icon = apiService.getIconUrl(it.icon),
                            )
                        },
                        isReadyToBuyNext = true
                    )
                }
            }.onFailure {
                logger.log(Level.SEVERE, it.stackTraceToString())
                delay(2000L)
                fetchCategories()
            }
        }
    }


    fun onPurchased(shopItem: ShopItem, desc: String) {
        setState {
            copy(isReadyToBuyNext = false)
        }
        coroutineScope.launchSafely {
            runCatching {
                val result = apiService.purchaseItem(shopItem.id, shopItem.price, desc)
                purchaseResultEvent.send(result)
                onRefresh()
            }.onFailure {
                logger.log(Level.SEVERE, it.stackTraceToString())
            }
        }
    }
}
