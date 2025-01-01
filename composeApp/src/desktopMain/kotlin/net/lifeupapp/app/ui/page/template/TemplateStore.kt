package ui.page.template

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

internal class TemplateStore {

    var state: TemplateState by mutableStateOf(TemplateState(0))
        private set

    data class TemplateState(
        val state: Int
    )
}
