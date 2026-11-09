package de.christian2003.feature.export.presentation.model.dto

import androidx.annotation.StringRes


/**
 * Descriptor for export / import services.
 *
 * @param id                ID of the service.
 * @param serviceMimeTypes  Set of mime types supported by the service.
 * @param titleId           String resource containing the title for the service.
 * @param subtitleId        String resource containing the subtitle for the service.
 */
internal data class ServiceDescriptorUiDto(
    val id: String,
    val serviceMimeTypes: Set<String>,
    @param:StringRes val titleId: Int,
    @param:StringRes val subtitleId: Int
)
