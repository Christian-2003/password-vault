package de.passwordvault.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext


data class PasswordVaultColors(
    val primary: Color,
    val background: Color,
    val backgroundAppBar: Color,
    val backgroundNavBar: Color,
    val backgroundContainer: Color,
    val backgroundCritical: Color,
    val backgroundWarning: Color,
    val backgroundRed: Color,
    val backgroundYellow: Color,
    val backgroundGreen: Color,
    val text: Color,
    val textVariant: Color,
    val textCritical: Color,
    val textWarning: Color,
    val textWarningInteractive: Color,
    val textOnPrimary: Color,
    val divider: Color,
    val red: Color,
    val yellow: Color,
    val green: Color
)


private val darkScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = textOnPrimaryDark,
    background = backgroundDark,
    onBackground = textDark,
    surface = backgroundDark,
    onSurface = textDark,
    onSurfaceVariant = textVariantDark,
    errorContainer = backgroundCriticalDark,
    onErrorContainer = textCriticalDark,
    outline = dividerDark
)

private val darkPasswordVaultColors = PasswordVaultColors(
    primary = primaryDark,
    background = backgroundDark,
    backgroundAppBar = backgroundAppBarDark,
    backgroundNavBar = backgroundNavBarDark,
    backgroundContainer = backgroundContainerDark,
    backgroundCritical = backgroundCriticalDark,
    backgroundWarning = backgroundWarningDark,
    backgroundRed = backgroundRedDark,
    backgroundYellow = backgroundYellowDark,
    backgroundGreen = backgroundGreenDark,
    text = textDark,
    textVariant = textVariantDark,
    textCritical = textCriticalDark,
    textWarning = textWarningDark,
    textWarningInteractive = textWarningInteractiveDark,
    textOnPrimary = textOnPrimaryDark,
    divider = dividerDark,
    red = redDark,
    yellow = yellowDark,
    green = greenDark
)

private val lightScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = textOnPrimaryLight,
    background = backgroundLight,
    onBackground = textLight,
    surface = backgroundLight,
    onSurface = textLight,
    onSurfaceVariant = textVariantLight,
    errorContainer = backgroundCriticalLight,
    onErrorContainer = textCriticalLight,
    outline = dividerLight
)

private val lightPasswordVaultColors = PasswordVaultColors(
    primary = primaryLight,
    background = backgroundLight,
    backgroundAppBar = backgroundAppBarLight,
    backgroundNavBar = backgroundNavBarLight,
    backgroundContainer = backgroundContainerLight,
    backgroundCritical = backgroundCriticalLight,
    backgroundWarning = backgroundWarningLight,
    backgroundRed = backgroundRedLight,
    backgroundYellow = backgroundYellowLight,
    backgroundGreen = backgroundGreenLight,
    text = textLight,
    textVariant = textVariantLight,
    textCritical = textCriticalLight,
    textWarning = textWarningLight,
    textWarningInteractive = textWarningInteractiveLight,
    textOnPrimary = textOnPrimaryLight,
    divider = dividerLight,
    red = redLight,
    yellow = yellowLight,
    green = greenLight
)


val LocalPasswordVaultColors = staticCompositionLocalOf { lightPasswordVaultColors }


@Composable
fun PasswordVaultTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> darkScheme
        else -> lightScheme
    }

    val passwordVaultColors = if (darkTheme) darkPasswordVaultColors else lightPasswordVaultColors

    CompositionLocalProvider(LocalPasswordVaultColors provides passwordVaultColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            content = content
        )
    }
}
