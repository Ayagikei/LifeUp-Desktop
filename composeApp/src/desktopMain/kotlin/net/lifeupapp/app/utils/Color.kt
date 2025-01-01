package utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb


fun getColorBetween(fraction: Float, startColor: Int, endColor: Int): Int {
    val startColor = Color(startColor)
    val endColor = Color(endColor)
    val redCurrent = (startColor.red + fraction * (endColor.red - startColor.red)).toFloat()
    val blueCurrent = (startColor.blue + fraction * (endColor.blue - startColor.blue)).toFloat()
    val greenCurrent = (startColor.green + fraction * (endColor.green - startColor.green)).toFloat()

    return Color(redCurrent, greenCurrent, blueCurrent).toArgb()
}