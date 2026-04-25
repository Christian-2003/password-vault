package de.christian2003.feature.analysis.domain.entities


/**
 * Security criteria for which passwords are analyzed.
 */
internal enum class SecurityCriteria {

    Length,
    CharacterVariety,
    Entropy,
    PatternsAndSubstitutions,
    DictionaryWords,
    CommonPassword,
    Reuse

}
