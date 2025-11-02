package de.christian2003.passwordvault.plugin.infrastructure.security.auth

import android.content.Context
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import de.christian2003.passwordvault.application.security.BiometricAuthService
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import de.christian2003.passwordvault.R


/**
 * Service implementation for biometric authentication that uses the androidx-implementation of
 * biometrics.
 *
 * @param context   Context.
 */
class AndroidBiometricAuthService(
    private val context: Context
): BiometricAuthService {

    /**
     * Authenticates the user using biometric authentication.
     *
     * @return  Whether the user was authenticated successfully.
     */
    override suspend fun authenticate(): Boolean = suspendCancellableCoroutine { continuation ->
        val activity: FragmentActivity = context as FragmentActivity
        val executor: Executor = ContextCompat.getMainExecutor(context)

        val promptInfo: BiometricPrompt.PromptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(context.getString(R.string.biometricPrompt_title))
            .setSubtitle(context.getString(R.string.biometricPrompt_subtitle))
            .setNegativeButtonText(context.getString(R.string.button_cancel))
            .build()

        val biometricPrompt = BiometricPrompt(activity, executor, object : BiometricPrompt.AuthenticationCallback() {

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                if (continuation.isActive) {
                    continuation.resume(true)
                }
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                if (continuation.isActive) {
                    continuation.resume(false)
                }
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                //This is called (e.g. if the fingerprint is wrong) - Do not resume here
            }
        })

        biometricPrompt.authenticate(promptInfo)

        //Cancel biometric authentication if coroutine is cancelled:
        continuation.invokeOnCancellation {
            biometricPrompt.cancelAuthentication()
        }
    }

}
