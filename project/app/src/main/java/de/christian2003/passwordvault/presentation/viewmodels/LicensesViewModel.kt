package de.christian2003.passwordvault.presentation.viewmodels

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.christian2003.core.ui.model.HelpCard
import de.christian2003.passwordvault.R
import de.christian2003.passwordvault.presentation.models.licenses.LicenseUiDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVRecord
import java.io.InputStream
import javax.inject.Inject


/**
 * View model for the screen through which users see all software used by Password Vault.
 *
 * @param application   Application.
 */
@HiltViewModel
internal class LicensesViewModel @Inject constructor(
    application: Application
): AndroidViewModel(application) {

    /**
     * Whether the help card is visible.
     */
    var isHelpCardVisible: Boolean by mutableStateOf(HelpCard.Licenses.getVisible(application))
        private set

    /**
     * List of all software used by Password Vault.
     * Is null while the list is being loaded.
     */
    var licenses: List<LicenseUiDto>? by mutableStateOf(null)
        private set

    /**
     * Software currently displayed in a dialog.
     * Is null if no software is currently displayed.
     */
    var displayedLicense: LicenseUiDto? by mutableStateOf(null)
        private set

    /**
     * License text of the software currently displayed in a dialog.
     * Is null if no software is currently displayed.
     */
    var displayedLicenseText: String? by mutableStateOf(null)
        private set


    /**
     * Initializes the view model.
     */
    init {
        viewModelScope.launch(Dispatchers.IO) {
            loadLicenses()
        }
    }


    /**
     * Loads all licenses.
     */
    private fun loadLicenses() {
        if (licenses == null) {
            val licenses: MutableList<LicenseUiDto> = mutableListOf()
            try {
                application.resources.openRawResource(R.raw.licenses).use { inputStream ->
                    val format: CSVFormat = CSVFormat.DEFAULT
                        .builder()
                        .setHeader("label", "artifact", "license")
                        .get()
                    val parser: CSVParser = CSVParser.parse(inputStream, Charsets.UTF_8, format)
                    for (record: CSVRecord in parser.records) {
                        val displayName: String? = record.get("label")
                        val artifact: String? = record.get("artifact")
                        val licenseFileName: String? = record.get("license")

                        if (!displayName.isNullOrBlank() && !artifact.isNullOrBlank() && !licenseFileName.isNullOrBlank()) {
                            //artifact is in format "<group>:<name>" (e.g. "com.example.library:library")
                            val artifactParts: List<String> = artifact.split(':')

                            if (artifactParts.size == 2) {
                                val artifactGroup: String = artifactParts[0]
                                val artifactName: String = artifactParts[1]
                                val license = LicenseUiDto(displayName, artifactGroup, artifactName, licenseFileName)
                                licenses.add(license)
                            }
                        }
                    }
                }
                this@LicensesViewModel.licenses = licenses
            }
            catch (e: Exception) {
                Log.w("Licenses", "Cannot load licenses: ${e.message ?: "Unknown exception"}")
            }
        }
    }


    /**
     * Shows the dialog for the specified license. Pass null to hide any license.
     *
     * @param license   License to show. Pass null to hide any license.
     */
    fun showLicense(license: LicenseUiDto? = null) = viewModelScope.launch(Dispatchers.IO) {
        if (license == null) {
            //Hide license:
            displayedLicense = null
            displayedLicenseText = null
        }
        else {
            //Show license:
            val content: ByteArray
            try {
                val application = getApplication<Application>()
                val resources = application.resources
                val inputStream: InputStream = resources.openRawResource(resources.getIdentifier(license.licenseFileName, "raw", application.packageName))
                content = ByteArray(inputStream.available())
                inputStream.read(content)
                inputStream.close()

                displayedLicense = license
                displayedLicenseText = String(content)
            }
            catch (_: Exception) {
                displayedLicense = null
                displayedLicenseText = null
            }
        }
    }


    /**
     * Dismisses the help card.
     */
    fun dismissHelpCard() {
        HelpCard.Licenses.setVisible(application, false)
        isHelpCardVisible = false
    }

}
