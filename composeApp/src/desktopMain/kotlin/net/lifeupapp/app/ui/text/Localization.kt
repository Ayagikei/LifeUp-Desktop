package net.lifeupapp.app.ui.text

import java.text.SimpleDateFormat

object Localization {

    fun get(): StringText {
        return StringText
    }

    val dateTimeFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    val dateTimeFormatterWithNewLine = SimpleDateFormat("yyyy-MM-dd\nHH:mm:ss")
}