package de.christian2003.core.ui.model

import android.content.Context
import androidx.core.content.edit


enum class HelpCard(
    private val key: String
) {

    HelpSetupMasterPassword("help_setupMasterPassword"),

    HelpRecoveryCodes("help_recoveryCodes"),

    HelpBiometrics("help_biometrics"),

    HelpRecovery("help_recovery"),

    Tags("help_tags"),

    Targets("help_targets"),

    Detail("help_detail"),

    Account("help_account"),

    Help("help_help"),

    Autofill("help_autofill");

    fun getVisible(context: Context): Boolean {
        return context.getSharedPreferences("settings", Context.MODE_PRIVATE).getBoolean(key, true)
    }

    fun setVisible(context: Context, isVisible: Boolean) {
        context.getSharedPreferences("settings", Context.MODE_PRIVATE).edit {
            putBoolean(key, isVisible)
        }
    }

}
