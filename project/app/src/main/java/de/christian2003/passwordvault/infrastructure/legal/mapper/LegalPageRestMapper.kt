package de.christian2003.passwordvault.infrastructure.legal.mapper

import de.christian2003.passwordvault.domain.entities.LegalPage
import de.christian2003.passwordvault.infrastructure.legal.dto.LegalPageItemRestDto
import de.christian2003.passwordvault.infrastructure.legal.dto.LegalPageRestDto
import java.util.Locale
import javax.inject.Inject


/**
 * Mapper which maps the REST DTO for legal pages to its domain representation.
 */
internal class LegalPageRestMapper @Inject constructor() {

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
    fun toDomain(dto: LegalPageRestDto, locale: Locale): LegalPage? {
        val item: LegalPageItemRestDto? = getItemForLocaleOrFallback(dto.items, locale)
        var domain: LegalPage? = null

        if (item != null) {
            domain = LegalPage(
                valid = dto.valid,
                version = dto.version,
                pageUrl = item.pageUrl
            )
        }

        return domain
    }


    /**
     * From the list of legal page items, this gets the item with the provided locale. If no item
     * with the locale exists, the default locale is returned as fallback. If the default locale is
     * not in the list either, null may be returned.
     *
     * @param items     List of legal page items.
     * @param locale    Locale whose item to return.
     * @return          Item with the specified item, default or null.
     */
    private fun getItemForLocaleOrFallback(items: List<LegalPageItemRestDto>, locale: Locale): LegalPageItemRestDto? {
        var item: LegalPageItemRestDto? = items.firstOrNull { it.locale == locale }

        if (item == null) {
            item = items.firstOrNull { it.locale == fallbackLocale }
        }

        return item
    }

}
