package de.christian2003.passwordvault.domain.security


/**
 * Interface provides a service with which to copy data to the Android clipboard.
 */
interface ClipboardService {

    /**
     * Copies data to the clipboard.
     *
     * @param label         Label for the clip.
     * @param data          Data for the clip.
     * @param isSensitive   Whether the clip data is sensitive.
     */
    fun copyToClipboard(label: String, data: String, isSensitive: Boolean)

}
