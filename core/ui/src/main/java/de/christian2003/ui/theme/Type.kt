package de.christian2003.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import de.christian2003.ui.R


val AppTypography = Typography()


/**
 * Some OEMs (e.g. Samsung One UI) override font settings. In this case it is impossible to set
 * a monospace font because the OEM overrides this setting with the system's default font. For these
 * cases, we need to ship a custom monospace font that can be used. Set a monospace font as follows:
 *
 * Text(
 *     text = "Hello, World!",
 *     style = MaterialTheme.typography.bodyLarge.copy(
 *         fontFamily = RobotoMono
 *     )
 * )
 */
val RobotoMono = FontFamily(
    Font(R.font.roboto_mono_regular)
)
