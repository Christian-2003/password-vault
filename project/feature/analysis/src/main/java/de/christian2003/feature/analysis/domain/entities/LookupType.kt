package de.christian2003.feature.analysis.domain.entities


/**
 * Types of files to query for the password lookup.
 *
 * @property CommonPasswords    Lookup of common passwords (e.g. "abcdef", "password", "123456")
 * @property DictionaryWords    Lookup of common dictionary words (e.g. "the", "and", "house")
 */
internal enum class LookupType {

    CommonPasswords,
    DictionaryWords

}
