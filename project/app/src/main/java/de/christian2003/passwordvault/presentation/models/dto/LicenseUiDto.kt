package de.christian2003.passwordvault.presentation.models.dto


/**
 * UI DTO for a license of a single software dependency.
 *
 * @param displayName       Display name (e.g. "Library Name").
 * @param artifactGroup     Artifact group (e.g. "com.example.library")
 * @param artifactName      Artifact name (e.g. "library").
 * @param licenseFileName   Resource name of the file storing the license text (e.g. "license_apache20").
 */
internal data class LicenseUiDto(
    val displayName: String,
    val artifactGroup: String,
    val artifactName: String,
    val licenseFileName: String
)
