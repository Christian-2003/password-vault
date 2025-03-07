package de.passwordvault.view.passwords.activity_analysis

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.passwordvault.model.analysis.QualityGateManager
import de.passwordvault.model.analysis.passwords.AnalyzedPassword
import de.passwordvault.model.detail.DetailType
import de.passwordvault.model.entry.EntryExtended
import de.passwordvault.model.entry.EntryManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class PasswordAnalysisViewModel: ViewModel() {

    private lateinit var entryManager: EntryManager

    private lateinit var qualityGateManager: QualityGateManager

    private var isAnalysisFinished = false

    var maxSecurityScore: Int? = null

    var thresholdGood: Float? = null

    var thresholdNeutral: Float? = null

    var isAnalysisStarted: Boolean by mutableStateOf(false)

    var securityScore: Double by mutableDoubleStateOf(0.0)

    var analyzedPasswords: List<AnalyzedPassword> = emptyList()

    var weakPasswords: List<AnalyzedPassword> = emptyList()

    var identicalPasswords: List<List<AnalyzedPassword>> = emptyList()


    fun init(entryManager: EntryManager, qualityGateManager: QualityGateManager) {
        this.entryManager = entryManager
        this.qualityGateManager = qualityGateManager
        maxSecurityScore = qualityGateManager.numberOfQualityGates()
        thresholdGood = maxSecurityScore!! * 0.67f
        thresholdNeutral = maxSecurityScore!! * 0.34f
        if (!isAnalysisStarted && !isAnalysisFinished) {
            analyze()
        }
    }


    fun analyze() = viewModelScope.launch(Dispatchers.IO) {
        isAnalysisStarted = true
        isAnalysisFinished = false
        val passwords: MutableList<AnalyzedPassword> = mutableListOf()
        val weakPasswords: MutableList<AnalyzedPassword> = mutableListOf()
        val identicalPasswords: MutableList<MutableList<AnalyzedPassword>> = mutableListOf()
        var securityScore = 0.0
        val requiredQualityGates: Int = Math.round(qualityGateManager.numberOfQualityGates().toDouble() * 0.5).toInt()

        //Retrieve all passwords:
        entryManager.data.forEach { abbreviated ->
            val extended: EntryExtended? = entryManager.get(abbreviated.uuid, false)
            extended?.details?.forEach { detail ->
                if (detail.type == DetailType.PASSWORD) {
                    val passwordSecurityScore: Int = qualityGateManager.calculatePassedQualityGates(detail.content)
                    passwords.add(AnalyzedPassword(passwordSecurityScore, detail.content, abbreviated))
                }
            }
        }

        //Security analysis:
        for (i in 0..<passwords.size) {
            val password: AnalyzedPassword = passwords[i]
            securityScore += password.securityScore
            for (j in (i + 1)..<passwords.size) {
                val comparedPassword: AnalyzedPassword = passwords[j]
                if (password.password.equals(comparedPassword.password)) {
                    var passwordFound = false
                    identicalPasswords.forEach { identicalPasswordsGroup ->
                        if (identicalPasswordsGroup[0].password.equals(password.password)) {
                            if (!identicalPasswordsGroup.contains(comparedPassword)) {
                                identicalPasswordsGroup.add(comparedPassword)
                            }
                            passwordFound = true
                            return@forEach
                        }
                    }
                    if (!passwordFound) {
                        val identicalPasswordGroup: MutableList<AnalyzedPassword> = mutableListOf()
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
