package net.lifeupapp.app.ui.text

import java.text.SimpleDateFormat

object Localization {

//    private fun getAllLanguage(): List<StringText> {
//        return listOf(EnStrings(), ZhCNStrings(), ZhHantStrings())
//    }

    fun get(): StringText {
        return StringText

//        val preferLanguage = Locale.getDefault().language
//        val preferCountry = Locale.getDefault().country.uppercase()
//        logger.log(Level.INFO, "prefer language: $preferLanguage, prefer country: $preferCountry")
//
//        return getAllLanguage().firstOrNull { it.language == preferLanguage && it.perfer_country.contains(preferCountry) }
//            ?: getAllLanguage().firstOrNull { it.language == preferLanguage }
//            ?: EnStrings()
    }

    val dateTimeFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    val dateTimeFormatterWithNewLine = SimpleDateFormat("yyyy-MM-dd\nHH:mm:ss")
}