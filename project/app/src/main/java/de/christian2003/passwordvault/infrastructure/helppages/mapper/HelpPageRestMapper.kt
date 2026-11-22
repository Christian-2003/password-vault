package de.christian2003.passwordvault.infrastructure.helppages.mapper

import de.christian2003.passwordvault.domain.entities.HelpPage
import de.christian2003.passwordvault.infrastructure.helppages.dto.HelpPageItemRestDto
import de.christian2003.passwordvault.infrastructure.helppages.dto.HelpPageRestDto
import java.util.Locale
import javax.inject.Inject


/**
 * Mapper which maps the REST DTO for help pages to its domain representation.
 */
internal class HelpPageRestMapper @Inject constructor() {

    /**
     * Fallback locale to use as fallback if no localization exists for current language.
     */
    private val fallbackLocale: Locale = Locale.forLanguageTag("en-US")


    /**
     * Maps the REST DTO to its domain representation.
     *
     * @param dto   DTO to map to the domain.
     * @return      Mapped domain representation.
     */
    fun toDomain(dto: HelpPageRestDto, locale: Locale): HelpPage? {
        val item: HelpPageItemRestDto? = getItemForLocaleOrFallback(dto.items, locale)
        var domain: HelpPage? = null

        if (item != null) {
            domain = HelpPage(
                title = item.title,
                description = item.description,
                pageUrl = item.url,
                previewImageUrl = dto.previewImage,
                lang = item.locale,
                isInCurrentLang = item.locale == locale
            )
        }

        return domain
    }


    private fun getItemForLocaleOrFallback(items: List<HelpPageItemRestDto>, locale: Locale): HelpPageItemRestDto? {
        var item: HelpPageItemRestDto? = items.firstOrNull { it.locale == locale }

        if (item == null) {
            item = items.firstOrNull { it.locale == fallbackLocale }
        }

        return item
    }

}
