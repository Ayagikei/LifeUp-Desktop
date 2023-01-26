package ui.text

import java.util.*

object Localization {
    fun get(): StringText {
        Locale.getDefault().language.let {
            if (it == "zh") {
                // TODO
                return EnStrings()
            } else {
                return EnStrings()
            }
        }
    }


}