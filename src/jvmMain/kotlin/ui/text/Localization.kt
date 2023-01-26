package ui.text

import logger
import ui.text.i18n.EnStrings
import ui.text.i18n.ZhCNStrings
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.Level

object Localization {

    private fun getAllLanguage(): List<StringText> {
        return listOf(EnStrings(), ZhCNStrings())
    }

    fun get(): StringText {
        val preferLanguage = Locale.getDefault().language
        logger.log(Level.INFO, "prefer language: $preferLanguage")
        return getAllLanguage().firstOrNull { it.language == preferLanguage } ?: EnStrings()
    }

    val dateTimeFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    val dateTimeFormatterWithNewLine = SimpleDateFormat("yyyy-MM-dd\nHH:mm:ss")
}