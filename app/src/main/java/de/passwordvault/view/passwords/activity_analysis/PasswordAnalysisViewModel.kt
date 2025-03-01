package de.passwordvault.view.passwords.activity_analysis

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.passwordvault.model.analysis.QualityGateManager
import de.passwordvault.model.analysis.passwords.Password
import de.passwordvault.model.detail.DetailType
import de.passwordvault.model.entry.EntryExtended
import de.passwordvault.model.entry.EntryManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class PasswordAnalysisViewModel: ViewModel() {

    private lateinit var entryManager: EntryManager

    private lateinit var qualityGateManager: QualityGateManager

    var maxSecurityScore: Int? = null


    var isAnalysisFinished: Boolean by mutableStateOf(false)

    var isAnalysisStarted: Boolean by mutableStateOf(false)

    var securityScore: Double by mutableDoubleStateOf(0.0)

    var analyzedPasswords: List<Password> = emptyList()

    var weakPasswords: List<Password> = emptyList()

    var identicalPasswords: List<List<Password>> = emptyList()


    fun init(entryManager: EntryManager, qualityGateManager: QualityGateManager) {
        this.entryManager = entryManager
        this.qualityGateManager = qualityGateManager
        maxSecurityScore = qualityGateManager.numberOfQualityGates()
        if (!isAnalysisStarted && !isAnalysisFinished) {
            analyze()
        }
    }


    fun analyze() = viewModelScope.launch(Dispatchers.IO) {
        isAnalysisStarted = true
        isAnalysisFinished = false
        delay(1000)
        val passwords: MutableList<Password> = mutableListOf()
        val weakPasswords: MutableList<Password> = mutableListOf()
        val identicalPasswords: MutableList<MutableList<Password>> = mutableListOf()
        var securityScore = 0.0
        val requiredQualityGates: Int = Math.round(qualityGateManager.numberOfQualityGates().toDouble() * 0.5).toInt()

        //Retrieve all passwords:
        entryManager.data.forEach { abbreviated ->
            val extended: EntryExtended? = entryManager.get(abbreviated.uuid, false)
            extended?.details?.forEach { detail ->
                if (detail.type == DetailType.PASSWORD) {
                    passwords.add(Password(detail.content, extended.uuid, extended.name))
                }
            }
        }

        //Security analysis:
        for (i in 0..<passwords.size) {
            val password: Password = passwords[i]
            securityScore += password.securityScore
            for (j in (i + 1)..<passwords.size) {
                val comparedPassword: Password = passwords[j]
                if (password.cleartextPassword.equals(comparedPassword.cleartextPassword)) {
                    var passwordFound = false
                    identicalPasswords.forEach { identicalPasswordsGroup ->
                        if (identicalPasswordsGroup[0].cleartextPassword.equals(password.cleartextPassword)) {
                            if (!identicalPasswordsGroup.contains(comparedPassword)) {
                                identicalPasswordsGroup.add(comparedPassword)
                            }
                            passwordFound = true
                            return@forEach
                        }
                    }
                    if (!passwordFound) {
                        val identicalPasswordGroup: MutableList<Password> = mutableListOf()
                        identicalPasswordGroup.add(password)
                        identicalPasswordGroup.add(comparedPassword)
                        identicalPasswords.add(identicalPasswordGroup)
                    }
                }
            }
            if (password.securityScore <= requiredQualityGates) {
                weakPasswords.add(password)
            }
        }

        //Save data
        if (passwords.isNotEmpty()) {
            this@PasswordAnalysisViewModel.securityScore = securityScore / passwords.size
        }
        this@PasswordAnalysisViewModel.analyzedPasswords = passwords
        this@PasswordAnalysisViewModel.weakPasswords = weakPasswords
        this@PasswordAnalysisViewModel.identicalPasswords = identicalPasswords

        isAnalysisFinished = true
        isAnalysisStarted = false
    }

}
