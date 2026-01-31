package de.christian2003.ui.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils


/**
 * Color generator can generate colors based on seed colors (e.g. colors from the material theme).
 * The generated colors will adapt to theme changes and be compatible visually with the theme colors.
 *
 * The generator uses HSL color theory to generate compatible colors.
 */
class ColorGenerator {

    /**
     * Generates a "positive" color based on the provided negative color (usually
     * MaterialTheme.colorScheme.error).
     *
     * @param negative  Negative color to use as seed.
     * @param darkTheme Whether the system is in dark theme.
     */
    fun generatePositiveColorFromNegativeColor(
        negative: Color,
        darkTheme: Boolean
    ): Color {
        val baseHsl: FloatArray = colorToHsl(negative)

        val hue = (baseHsl[0] + 140f) % 360f
        val saturation = 0.65f
        val lightness = if(darkTheme) { 0.65f } else { 0.4f }

        return hslToColor(
            h = hue,
            s = saturation,
            l = lightness
        )
    }


    /**
     * Converts the specified color to HSL.
     *
     * @param color Color to convert to HSL.
     * @return      Color in HSL format.
     */
    private fun colorToHsl(color: Color): FloatArray {
        val hsl = FloatArray(3)
        ColorUtils.colorToHSL(color.toArgb(), hsl)
        return hsl
    }


    /**
     * Converts the specified HSL values to a color.
     *
     * @param h Hue.
     * @param s Saturation.
     * @param l Lightness.
     * @return  Color converted from HSL.
     */
    private fun hslToColor(h: Float, s: Float, l: Float): Color {
        return Color(ColorUtils.HSLToColor(floatArrayOf(h, s, l)))
    }


    /**
     * Determines whether the hue determines a greenish color. This is important
     * since green is near the maximum perceived lightness and therefore difficult
     * to differentiate.
     *
     * @param hue   Hue.
     * @return      Whether the hue indicates a greenish color.
     */
    private fun isGreenishColor(hue: Float): Boolean {
        return hue in 70f..160f
    }

}
