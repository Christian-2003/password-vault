package de.christian2003.passwordvault.presentation.models.other

import androidx.annotation.DrawableRes
import de.christian2003.passwordvault.R
import java.util.Locale


/**
 * Maps flag drawables to locales.
 *
 * @param locale    Locale.
 * @param drawable  Drawable flag for the locale.
 */
internal enum class LocaleDrawable(
    private val locale: Locale,
    @param:DrawableRes val drawable: Int
) {

    En(Locale.forLanguageTag("en-US"), R.drawable.ic_lang_en),

    De(Locale.forLanguageTag("de-DE"), R.drawable.ic_lang_de),

    Fr(Locale.forLanguageTag("fr-FR"), R.drawable.ic_lang_fr),

    Es(Locale.forLanguageTag("es-ES"), R.drawable.ic_lang_es);


    companion object {

        /**
         * Returns the locale drawable for the specified locale.
         * If locale is unknown, falls back to 'En'.
         *
         * @param locale    Locale whose drawable to return.
         * @return          Drawable for the provided locale.
         */
        fun forLocale(locale: Locale): LocaleDrawable {
            val drawable: LocaleDrawable? = entries.find { it.locale == locale }
            return drawable ?: En
        }

    }

}
