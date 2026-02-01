package de.christian2003.auth.models.other


/**
 * Recommendations for the auth settings.
 *
 * @property None                       No recommendation
 * @property ChangePassword             Recommendation to change the master password.
 * @property RegenerateRecoveryCodes    Recommendation to regenerate recovery codes.
 * @property EnableBiometrics           Recommendation to enable biometrics.
 */
enum class AuthRecommendation {

    None,
    ChangePassword,
    RegenerateRecoveryCodes,
    EnableBiometrics

}
