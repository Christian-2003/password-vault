package de.christian2003.passwordvault.plugin.presentation.view.securityquestions

import de.christian2003.passwordvault.domain.security.auth.SecurityQuestion


/**
 * Data transfer object for the presentation layer which displays a security question.
 *
 * @param question  Security question.
 * @param answer    Answer to the security question. This is null if no answer is available.
 */
data class SecurityQuestionUiDto(
    val question: SecurityQuestion,
    val answer: CharArray?,
) {

    //Auto-generated
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SecurityQuestionUiDto

        if (question != other.question) return false
        if (!answer.contentEquals(other.answer)) return false

        return true
    }

    //Auto-generated
    override fun hashCode(): Int {
        var result = question.hashCode()
        result = 31 * result + (answer?.contentHashCode() ?: 0)
        return result
    }

}
