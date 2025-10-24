package de.christian2003.passwordvault.plugin.presentation.view.help

import android.content.Context
import androidx.core.content.edit


enum class HelpCard(

    private val preferencesKey: String

) {

    TAGS("help_tags"),

    HELP("help_help"),

    TARGETS("help_targets");

    fun getVisible(context: Context): Boolean {
        return context.getSharedPreferences("settings", Context.MODE_PRIVATE).getBoolean(preferencesKey, true)
    }

    fun setVisible(context: Context, isVisible: Boolean) {
        context.getSharedPreferences("settings", Context.MODE_PRIVATE).edit {
            putBoolean(preferencesKey, isVisible)
        }
    }

}
