package de.passwordvault.view.settings.activity_recovery

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import de.passwordvault.model.security.login.Account
import de.passwordvault.model.security.login.SecurityQuestion
import de.passwordvault.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/**
 * Implements the view model for the screen through which to configure recovery methods.
 *
 * @author  Christian-2003
 * @since   3.7.4
 */
class RecoveryViewModel(application: Application): AndroidViewModel(application) {

    /**
     * Account for which to edit the recovery methods.
     */
    private lateinit var account: Account

    /**
     * Preferences to store simple key-value pairs of data.
     */
    private val preferences = getApplication<Application>().baseContext.getSharedPreferences("settings", Context.MODE_PRIVATE)

    /**
     * Stores whether the help mode is activated.
     */
    var isHelpMode: Boolean by mutableStateOf(preferences.getBoolean("help_recovery", true))

    /**
     * List of security questions configured for the account.
     */
    val securityQuestions: MutableList<SecurityQuestion> = mutableStateListOf()

    /**
     * Security question currently being edited.
     */
    var editedSecurityQuestion: SecurityQuestion? by mutableStateOf(null)

    /**
     * Security question currently being deleted.
     */
    var deleteSecurityQuestion: SecurityQuestion? by mutableStateOf(null)


    /**
     * Initializes the view model.
     */
    fun init(account: Account) {
        this.account = account
        securityQuestions.clear()
        securityQuestions.addAll(this.account.securityQuestions)
    }


    /**
     * Method saves the security question passed as arguments. Afterwards, the edited list of
     * security questions is saved.
     *
     * @param question  Question to save.
     * @param answer    Answer to save.
     */
    fun saveSecurityQuestion(question: Int, answer: String) = viewModelScope.launch(Dispatchers.IO) {
        if (question == -1 || answer.isEmpty()) {
            editedSecurityQuestion = null
            return@launch
        }
        val index: Int = securityQuestions.indexOf(editedSecurityQuestion)
        editedSecurityQuestion = null
        if (index >= 0) {
            //Edit security question:
            securityQuestions[index] = SecurityQuestion(question, answer)
        }
        else {
            //Add new security question:
            securityQuestions.add(SecurityQuestion(question, answer))
        }
        account.securityQuestions = securityQuestions.toList()
        account.save()
    }


    /**
     * Deletes the security question passed as argument. Afterwards, the edited list of security
     * questions is saved.
     *
     * @param securityQuestion  Security question to delete.
     */
    fun deleteSecurityQuestion(securityQuestion: SecurityQuestion) = viewModelScope.launch(Dispatchers.IO) {
        val removed: Boolean = securityQuestions.remove(securityQuestion)
        deleteSecurityQuestion = null
        if (removed) {
            account.securityQuestions = securityQuestions
            account.save()
        }
    }


    /**
     * Returns a list of all security questions that can be selected by the user. If "editedSecurityQuestion"
     * is not null, it's question is included as well.
     *
     * @return  List of all available security questions.
     */
    fun getAvailableSecurityQuestions(): List<String> {
        val context: Context = getApplication<Application>().baseContext
        val allQuestions: Array<String> = context.resources.getStringArray(R.array.security_questions)
        val availableQuestions: MutableList<String> = mutableListOf()

        //Filter questions:
        var i = 0
        allQuestions.forEach { question ->
            var questionUsed = false
            securityQuestions.forEach { usedQuestion ->
                if (usedQuestion.question == i && !(editedSecurityQuestion != null && i == editedSecurityQuestion!!.question)) {
                    questionUsed = true
                }
            }
            i++
            if (!questionUsed) {
                availableQuestions.add(question)
            }
        }

        return availableQuestions.toList()
    }


    /**
     * Dismisses the help mode for the recovery.
     */
    fun dismissHelpMode() = viewModelScope.launch(Dispatchers.IO) {
        isHelpMode = false
        preferences.edit().putBoolean("help_recovery", false).apply()
    }

}
