package de.passwordvault.view.settings.activity_recovery

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import de.passwordvault.model.security.login.Account
import de.passwordvault.model.security.login.SecurityQuestion

class RecoveryViewModel: ViewModel() {

    private lateinit var account: Account

    val securityQuestions: MutableList<SecurityQuestion> = mutableListOf()

    var editedSecurityQuestion: SecurityQuestion? by mutableStateOf(null)


    fun init(account: Account) {
        this.account = account
        securityQuestions.clear()
        securityQuestions.addAll(this.account.securityQuestions)
    }

}
