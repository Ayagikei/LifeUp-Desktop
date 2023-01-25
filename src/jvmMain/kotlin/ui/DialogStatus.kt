package ui

data class DialogStatus(
    val title: String = "",
    val message: String = "",
    val positiveButton: String = "",
    val negativeButton: String = "",
    val positiveAction: () -> Unit = {  },
    val negativeAction: () -> Unit = {  }
)

