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
import java.util.ArrayList


class RecoveryViewModel(application: Application): AndroidViewModel(application) {

    private lateinit var account: Account

    val securityQuestions: MutableList<SecurityQuestion> = mutableStateListOf()

    var editedSecurityQuestion: SecurityQuestion? by mutableStateOf(null)

    var deleteSecurityQuestion: SecurityQuestion? by mutableStateOf(null)


    fun init(account: Account) {
        this.account = account
        securityQuestions.clear()
        securityQuestions.addAll(this.account.securityQuestions)
    }


    fun saveSecurityQuestion(question: Int, answer: String) {
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

}
