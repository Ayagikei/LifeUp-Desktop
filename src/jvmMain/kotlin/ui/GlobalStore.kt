package ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ui.list.RootStore
import ui.text.EnStrings

object GlobalStore {

    var strings = EnStrings()


    var dialogStatus: DialogStatus? by mutableStateOf(null)
        private set
    init {
        showDialog("test") {

        }
    }



    fun showDialog(
        title: String,
        message: String = "这是message这是message这是message这是message这是message这是message这是message",
        positiveButton: String = strings.yes,
        negativeButton: String = strings.cancel,
        negativeAction: () -> Unit = {
          dialogStatus = null
        },
        positiveAction: () -> Unit
    ) {
        dialogStatus = DialogStatus(
            title, message, positiveButton, negativeButton, positiveAction, negativeAction
        )
    }
}