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
)
