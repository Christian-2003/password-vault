package de.christian2003.feature.analysis.application.usecases

import de.christian2003.data.accounts.application.usecases.GetAllDetailsByTypeUseCase
import de.christian2003.data.accounts.domain.entities.Detail
import de.christian2003.data.accounts.domain.entities.DetailType
import de.christian2003.feature.analysis.application.services.CommonPasswordDetectionService
import de.christian2003.feature.analysis.application.services.DictionaryDetectionService
import de.christian2003.feature.analysis.application.services.PatternDetectionService
import de.christian2003.feature.analysis.application.services.ReuseDetectionService
import de.christian2003.feature.analysis.domain.entities.PasswordResult
import de.christian2003.feature.analysis.domain.entities.PasswordStrength
import de.christian2003.feature.analysis.domain.entities.SecurityCriteria
import de.christian2003.feature.analysis.domain.entities.SecurityResult
import javax.inject.Inject
import kotlin.math.log2
import kotlin.uuid.Uuid


/**
 * Use case to analyze the security of the passwords of all accounts stored in the app.
 *
 * @param getAllDetailsByTypeUseCase        Use case to ge all account details by a specific type.
 * @param commonPasswordDetectionService    Service detects common passwords.
 * @param dictionaryDetectionService        Service detects dictionary words inside passwords.
 * @param patternDetectionService           Service detects patterns in passwords.
 * @param reuseDetectionService             Service detects reused passwords among multiple accounts.
 */
internal class AnalyzePasswordsUseCase @Inject constructor(
    private val getAllDetailsByTypeUseCase: GetAllDetailsByTypeUseCase,
    private val commonPasswordDetectionService: CommonPasswordDetectionService,
    private val dictionaryDetectionService: DictionaryDetectionService,
    private val patternDetectionService: PatternDetectionService,
    private val reuseDetectionService: ReuseDetectionService
) {

    /**
     * Analyzes all passwords.
     *
     * @return  Result of the analysis.
     */
    suspend fun analyzePasswords(): SecurityResult {
        //Get all passwords:
        val passwords: Map<Detail, Uuid> = getAllDetailsByTypeUseCase.getAllDetailsByType(DetailType.Password)

        //Prepare:
        val reusedPasswords: Map<String, List<Uuid>> = reuseDetectionService.prepareReuseDetection(passwords)
        dictionaryDetectionService.prepareDictionaryDetection()
        commonPasswordDetectionService.preparePasswordDetection()

        //Run analysis (TODO: Multithreading):
        val passwordResults: MutableList<PasswordResult> = mutableListOf()
        passwords.forEach { password, accountId ->
            val passwordResult: PasswordResult = analyzePassword(password, accountId)
            passwordResults.add(passwordResult)
        }

        //Map passwords by strength:
        val passwordsByStrength: MutableMap<PasswordStrength, MutableList<PasswordResult>> = mutableMapOf()
        passwordResults.forEach { passwordResult ->
            val strength: PasswordStrength = when {
                passwordResult.securityScore <= 3 -> PasswordStrength.Weak
                passwordResult.securityScore <= 7 -> PasswordStrength.Medium
                passwordResult.securityScore <= 12 -> PasswordStrength.Strong
                else -> PasswordStrength.VeryStrong
            }
            if (!passwordsByStrength.contains(strength)) {
                passwordsByStrength[strength] = mutableListOf()
            }
            passwordsByStrength[strength]!!.add(passwordResult)
        }

        //Generate result
        val securityResult = SecurityResult(
            allPasswordResults = passwordResults,
            passwordResults = passwordsByStrength,
            reusedPasswords = reusedPasswords
        )

        //Cleanup:
        dictionaryDetectionService.cleanupDictionaryDetection()
        commonPasswordDetectionService.cleanupPasswordDetection()

        return securityResult
    }


    /**
     * Analyses a single password and generates the result for this specific password.
     *
     * @param passwordDetail    Detail containing the password to analyze.
     * @param accountId         ID of the account of which the password is a member.
     * @return                  Result for the analyzed password.
     */
    private fun analyzePassword(passwordDetail: Detail, accountId: Uuid): PasswordResult {
        val password: String = passwordDetail.content
        val weaknesses: MutableList<SecurityCriteria> = mutableListOf()

        //Length:
        val lengthScore: Int = when {
            password.length >= 16 -> 4
            password.length >= 12 -> 3
            password.length >= 8 -> 2
            else -> 1
        }
        if (lengthScore <= 2) {
            weaknesses.add(SecurityCriteria.Length)
        }

        //Character variety:
        val hasLower: Boolean = password.any { it.isLowerCase() }
        val hasUpper: Boolean = password.any { it.isUpperCase() }
        val hasDigit: Boolean = password.any { it.isUpperCase() }
        val hasSpecial: Boolean = password.any { !it.isLetterOrDigit() }
        val varietyScore: Int = listOf(hasLower, hasUpper, hasDigit, hasSpecial).count { it }
        if (varietyScore <= 3) {
            weaknesses.add(SecurityCriteria.CharacterVariety)
        }

        //Entropy estimation:
        var charsetSize = 0
        if (hasLower) {
            charsetSize += 26
        }
        if (hasUpper) {
            charsetSize += 26
        }
        if (hasDigit) {
            charsetSize += 10
        }
        if (hasSpecial) {
            charsetSize += 32 //Approx number of special characters available
        }
        val entropy: Double = password.length * log2(charsetSize.toDouble())
        if (entropy <= 48) {
            weaknesses.add(SecurityCriteria.Entropy)
        }

        var securityScore = 0
        securityScore += lengthScore * 2
        securityScore += varietyScore
        securityScore += (entropy / 20).toInt()

        if (patternDetectionService.containsPatterns(password)) {
            securityScore -= 2
            weaknesses.add(SecurityCriteria.PatternsAndSubstitutions)
        }

        if (reuseDetectionService.isReused(password)) {
            securityScore -= 2
            weaknesses.add(SecurityCriteria.Reuse)
        }

        if (dictionaryDetectionService.containsDictionaryWords(password)) {
            securityScore = 0
            weaknesses.add(SecurityCriteria.DictionaryWords)
        }

        if (commonPasswordDetectionService.isCommonPassword(password)) {
            securityScore = 0
            weaknesses.add(SecurityCriteria.CommonPassword)
        }

        securityScore = if (securityScore < 0) { 0 } else { securityScore }

        val passwordResult = PasswordResult(
            detailId = passwordDetail.id,
            accountId = accountId,
            securityScore = securityScore,
            weaknesses = weaknesses
        )
        return passwordResult
    }

}
