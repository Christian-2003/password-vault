package de.passwordvault.model.analysis.passwords

import de.passwordvault.model.entry.EntryAbbreviated


/**
 * Class models a password that has been analyzed through the password security analysis.
 *
 * @author  Christian-2003
 * @since   3.7.3
 */
class AnalyzedPassword(

    /**
     * Security score for the password.
     */
    val securityScore: Int,

    /**
     * Password that has been analyzed.
     */
    val password: String,

    /**
     * Entry to which the analyzed password belongs.
     */
    val entry: EntryAbbreviated

)
