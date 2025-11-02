package de.christian2003.passwordvault.plugin.presentation.view.securityquestions

import de.christian2003.passwordvault.domain.security.auth.SecurityQuestion

data class SecurityQuestionUiDto(
    val question: SecurityQuestion,
    val answer: String?,
    val hasAnswer: Boolean
)
