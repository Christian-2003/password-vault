package de.christian2003.feature.analysis.application.services

import de.christian2003.data.accounts.domain.entities.Detail
import javax.inject.Inject
import kotlin.uuid.Uuid


/**
 * Service through which to detect whether account passwords are used multiple times.
 */
internal class ReuseDetectionService @Inject constructor() {

    /**
     * Passwords that are used multiple times are mapped to a list of accounts where they are used.
     */
    private val duplicatePasswords: MutableMap<String, List<Uuid>> = mutableMapOf()


    /**
     * Prepares the reuse detection with the provided map of password details.
     *
     * @param passwordDetails   Maps each password detail to the account ID of which it is a member.
     */
    fun prepareReuseDetection(passwordDetails: Map<Detail, Uuid>) {
        duplicatePasswords.clear()

        val details: List<Pair<Detail, Uuid>> = passwordDetails.toList()

        for (i: Int in 0 until details.size) {
            val pivotPassword: Detail = details[i].first
            val pivotAccountId: Uuid = details[i].second

            if (!duplicatePasswords.contains(pivotPassword.content)) {
                val duplicatePasswordAccounts: MutableList<Uuid> = mutableListOf()

                for (j: Int in (i + 1) until details.size) {
                    val currentPassword: Detail = details[j].first
                    val currentAccountId: Uuid = details[j].second

                    if (pivotPassword.content == currentPassword.content) {
                        duplicatePasswordAccounts.add(currentAccountId)
                    }
                }

                if (duplicatePasswordAccounts.isNotEmpty()) {
                    duplicatePasswordAccounts.add(pivotAccountId)
                    duplicatePasswords.put(pivotPassword.content, duplicatePasswordAccounts)
                }
            }
        }
    }


    /**
     * Checks whether the specified password is reused among multiple accounts.
     *
     * @param password  Password to check.
     * @return          Whether the password is reused.
     */
    fun isReused(password: String): Boolean {
        return duplicatePasswords.contains(password)
    }

}
