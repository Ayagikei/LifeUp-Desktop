package ui.page.list.add

import datasource.ApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import logger
import ui.AppStoreImpl
import java.util.logging.Level

internal class AddTasksStore(
    private val coroutineScope: CoroutineScope, private val globalStore: AppStoreImpl, defaultCategoryId: Long
) {

    init {
        logger.log(Level.INFO, "default category id: $defaultCategoryId")
    }

    var state = MutableStateFlow(AddTaskState(defaultCategoryId, ""))
        private set

    private val apiService = ApiService.instance

    private val addSuccessEvent = Channel<Unit>()
    val addSuccessEventFlow = addSuccessEvent.receiveAsFlow()

    private val addFailedEvent = Channel<String>()
    val addFailedEventFlow = addFailedEvent.receiveAsFlow()

    @Serializable
    data class AddTaskState(
        val categoryId: Long,
        val todo: String,
        val notes: String = "",
        val exp: Int = 0,
        val coinMin: Long = 0L,
        val coinMax: Long = 0L,
        val skills: List<Long> = emptyList(),
        val frequency: Int = 0,
        val itemId: Long = 0L,
        val itemAmount: Int = 0,
        val remoteBackgroundURL: String = ""
        // deadline: Long, we need to impl a date time picker first
    )

    fun onSkillSelected(id: Long, selected: Boolean) {
        updateState {
            if (selected) {
                if (skills.size < 3) {
                    copy(skills = skills + id)
                } else {
                    this
                }
            } else {
                copy(skills = skills - id)
            }
        }
    }

    fun onInputCoin(coin: String) {
        updateState {
            copy(coinMin = coin.toLongOrNull() ?: 0L)
        }
    }

    fun onInputCoinVar(max: String) {
        updateState {
            copy(coinMax = max.toLongOrNull() ?: 0L)
        }
    }

    fun onFrequencyChanged(frequency: Int) {
        updateState {
            copy(frequency = frequency)
        }
    }

    fun onRemoteBackgroundUrlChanged(url: String) {
        updateState {
            copy(remoteBackgroundURL = url)
        }
    }

    fun updateState(block: AddTaskState.() -> AddTaskState) {
        state.value = state.value.block()
    }

    fun onItemSelected(id: Long?) {
        updateState {
            copy(itemId = id ?: 0L)
        }
    }

    fun onItemAmountChanged(amount: Int) {
        updateState {
            copy(itemAmount = amount)
        }
    }

    fun onCategorySelected(id: Long) {
        updateState {
            copy(categoryId = id)
        }
    }

    fun onSkillExpChanged(exp: Int) {
        updateState {
            copy(exp = exp)
        }
    }

    fun onSubmitClicked() {
        coroutineScope.launch {
            val state = state.value
            val todo = state.todo
            val notes = state.notes
            val coin = state.coinMin
            val coinVar = (state.coinMax - state.coinMin).takeIf { it > 0 } ?: 0
            val exp = state.exp
            val skills = state.skills.joinToString("&skills=")
            val categoryId = state.categoryId
            val itemAmount = state.itemAmount
            val itemId = state.itemId
            val remoteBackgroundURL = state.remoteBackgroundURL

            val apiText =
                "lifeup://api/add_task?todo=$todo&notes=$notes&coin=$coin&coin_var=$coinVar&exp=$exp&skills=$skills&category=$categoryId&item_amount=$itemAmount&item_id=$itemId&remote_background_url=$remoteBackgroundURL&frequency=${state.frequency}"

            try {
                apiService.rawCall(apiText)
                addSuccessEvent.send(Unit)
            } catch (e: Exception) {
                e.printStackTrace()
                addFailedEvent.send("Failed to add task, please check again and check your LifeUp Cloud state.")
            }
        }
    }
}
