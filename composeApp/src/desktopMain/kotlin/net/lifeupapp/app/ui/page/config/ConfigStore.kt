package net.lifeupapp.app.ui.page.config

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import net.lifeupapp.app.ui.AppStoreImpl

internal class ConfigStore(
    private val coroutineScope: CoroutineScope,
    private val globalStore: AppStoreImpl
) {

    var state: ConfigState by mutableStateOf(
        ConfigState(
            isDialogShowing = false,
            apiToken = globalStore.apiToken
        )
    )
        private set

    data class ConfigState(
        val isDialogShowing: Boolean,
        val apiToken: String
    )

    fun updateState(block: ConfigState.() -> ConfigState) {
        state = state.block()
    }

    fun updateApiToken(token: String) {
        updateState {
            copy(apiToken = token).also {
                globalStore.updateApiToken(token)
            }
        }
    }
}
