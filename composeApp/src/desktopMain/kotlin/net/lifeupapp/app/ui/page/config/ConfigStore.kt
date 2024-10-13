package ui.page.config

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import net.lifeupapp.app.ui.AppStoreImpl

internal class ConfigStore(
    private val coroutineScope: CoroutineScope,
    private val globalStore: AppStoreImpl
) {

    var state: TemplateState by mutableStateOf(TemplateState(isDialogShowing = false))
        private set

    data class TemplateState(
        val isDialogShowing: Boolean
    )

    fun updateState(block: TemplateState.() -> TemplateState) {
        state = state.block()
    }
}
