package net.lifeupapp.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import lifeupdesktop.composeapp.generated.resources.HONORSansCN_Regular
import lifeupdesktop.composeapp.generated.resources.Res


//private val LightColors = lightColorScheme(
//    primary = md_theme_light_primary,
//    onPrimary = md_theme_light_onPrimary,
//    primaryContainer = md_theme_light_primaryContainer,
//    onPrimaryContainer = md_theme_light_onPrimaryContainer,
//    secondary = md_theme_light_secondary,
//    onSecondary = md_theme_light_onSecondary,
//    secondaryContainer = md_theme_light_secondaryContainer,
//    onSecondaryContainer = md_theme_light_onSecondaryContainer,
//    tertiary = md_theme_light_tertiary,
//    onTertiary = md_theme_light_onTertiary,
//    tertiaryContainer = md_theme_light_tertiaryContainer,
//    onTertiaryContainer = md_theme_light_onTertiaryContainer,
//    error = md_theme_light_error,
//    errorContainer = md_theme_light_errorContainer,
//    onError = md_theme_light_onError,
//    onErrorContainer = md_theme_light_onErrorContainer,
//    background = md_theme_light_background,
//    onBackground = md_theme_light_onBackground,
//    surface = md_theme_light_surface,
//    onSurface = md_theme_light_onSurface,
//    surfaceVariant = md_theme_light_surfaceVariant,
//    onSurfaceVariant = md_theme_light_onSurfaceVariant,
//    outline = md_theme_light_outline,
//    inverseOnSurface = md_theme_light_inverseOnSurface,
//    inverseSurface = md_theme_light_inverseSurface,
//    inversePrimary = md_theme_light_inversePrimary,
//    surfaceTint = md_theme_light_surfaceTint,
//    outlineVariant = md_theme_light_outlineVariant,
//    scrim = md_theme_light_scrim,
//)
//
//
//private val DarkColors = darkColorScheme(
//    primary = md_theme_dark_primary,
//    onPrimary = md_theme_dark_onPrimary,
//    primaryContainer = md_theme_dark_primaryContainer,
//    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
//    secondary = md_theme_dark_secondary,
//    onSecondary = md_theme_dark_onSecondary,
//    secondaryContainer = md_theme_dark_secondaryContainer,
//    onSecondaryContainer = md_theme_dark_onSecondaryContainer,
//    tertiary = md_theme_dark_tertiary,
//    onTertiary = md_theme_dark_onTertiary,
//    tertiaryContainer = md_theme_dark_tertiaryContainer,
//    onTertiaryContainer = md_theme_dark_onTertiaryContainer,
//    error = md_theme_dark_error,
//    errorContainer = md_theme_dark_errorContainer,
//    onError = md_theme_dark_onError,
//    onErrorContainer = md_theme_dark_onErrorContainer,
//    background = md_theme_dark_background,
//    onBackground = md_theme_dark_onBackground,
//    surface = md_theme_dark_surface,
//    onSurface = md_theme_dark_onSurface,
//    surfaceVariant = md_theme_dark_surfaceVariant,
//    onSurfaceVariant = md_theme_dark_onSurfaceVariant,
//    outline = md_theme_dark_outline,
//    inverseOnSurface = md_theme_dark_inverseOnSurface,
//    inverseSurface = md_theme_dark_inverseSurface,
//    inversePrimary = md_theme_dark_inversePrimary,
//    surfaceTint = md_theme_dark_surfaceTint,
//    outlineVariant = md_theme_dark_outlineVariant,
//    scrim = md_theme_dark_scrim,
//)

private val LightColors = Colors(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    error = md_theme_light_error,
    onError = md_theme_light_onError,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
    isLight = true,
    primaryVariant = md_theme_light_tertiary,
    secondaryVariant = md_theme_light_tertiary
)

private val Typography: androidx.compose.material.Typography
    @Composable
    get() = androidx.compose.material.Typography(
        defaultFontFamily = FontFamily(
            org.jetbrains.compose.resources.Font(
                Res.font.HONORSansCN_Regular,
                FontWeight.Normal,
                FontStyle.Normal
            )
        )
    )

@OptIn(ExperimentalUnitApi::class)
@get:Composable
val androidx.compose.material.Typography.dialogTitle: TextStyle
    get() = TextStyle(fontSize = TextUnit(18f, TextUnitType.Sp))

@OptIn(ExperimentalUnitApi::class)
@get:Composable
val androidx.compose.material.Typography.dialogMessage: TextStyle
    get() = TextStyle(fontSize = TextUnit(14f, TextUnitType.Sp))

val androidx.compose.material.Colors.unimportantText: androidx.compose.ui.graphics.Color
    get() = onSurface.copy(alpha = 0.6f)

val Colors.important: Color
    get() = Color(0xFF474747)

@OptIn(ExperimentalUnitApi::class)
val androidx.compose.material.Typography.subTitle3: TextStyle
    @Composable
    get() = TextStyle(
        fontSize = TextUnit(16f, TextUnitType.Sp),
        color = MaterialTheme.colors.important
    )

@Composable
fun AppTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit
) {
//  val colors = if (!useDarkTheme) {
//    LightColors
//  } else {
//    DarkColors
//  }

    MaterialTheme(
        colors = LightColors,
        content = content,
        typography = Typography
    )
}