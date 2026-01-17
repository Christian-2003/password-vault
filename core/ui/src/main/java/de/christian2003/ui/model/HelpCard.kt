package de.christian2003.ui.model

import android.content.Context
import androidx.core.content.edit


enum class HelpCard(
    private val key: String
) {

    HelpSetupMasterPassword("help_setupMasterPassword"),

    HelpRecoveryCodes("help_recoveryCodes");

    fun getVisible(context: Context): Boolean {
        return context.getSharedPreferences("settings", Context.MODE_PRIVATE).getBoolean(key, true)
    }

    fun setVisible(context: Context, isVisible: Boolean) {
        context.getSharedPreferences("settings", Context.MODE_PRIVATE).edit {
            putBoolean(key, isVisible)
        }
    }

}
