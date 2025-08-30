package de.christian2003.passwordvault.plugin.infrastructure.security

import android.content.ClipData
import android.content.ClipDescription
import android.content.ClipboardManager
import android.os.Handler
import android.os.Looper
import android.os.PersistableBundle
import android.util.Log
import de.christian2003.passwordvault.domain.security.ClipboardService


class AndroidClipboardService(

    private val clipboardManager: ClipboardManager

): ClipboardService {

    override fun copyToClipboard(label: String, data: String, isSensitive: Boolean) {
        val item = ClipData.Item(data)

        val mimeTypes: Array<String> = arrayOf("text/plain")

        val description = ClipDescription(label, mimeTypes)
        description.extras = PersistableBundle()
        description.extras.putBoolean(ClipDescription.EXTRA_IS_SENSITIVE, isSensitive)

        val data = ClipData(description, item)

        clipboardManager.setPrimaryClip(data)

        if (isSensitive) {
            Handler(Looper.getMainLooper()).postDelayed(this::clearClipboard, 10000)
        }
    }


    private fun clearClipboard() {
        clipboardManager.clearPrimaryClip()
        Log.d("Clipboard", "Cleared primary clip")
    }

}
