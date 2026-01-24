package de.christian2003.security.infrastructure.services

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
import de.christian2003.security.domain.services.BiometricsService
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.Executor
import javax.inject.Inject
import kotlin.coroutines.resume
import de.christian2003.security.R


/**
 * Implementation for the biometric service using the androidx.biometric library.
 *
 * @param context   Application context.
 */
@ActivityScoped
class AndroidBiometricsService @Inject constructor(
    @param:ActivityContext private val context: Context
): BiometricsService {

    /**
     * Shows a biometric prompt to the user with which to authenticate.
     *
     * @return  Whether biometric authentication was successful.
     */
    override suspend fun authenticate(): Boolean = suspendCancellableCoroutine { continuation ->
        val activity: FragmentActivity = context as FragmentActivity
        val executor: Executor = ContextCompat.getMainExecutor(context)

        val promptInfo: BiometricPrompt.PromptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(context.getString(R.string.biometricPrompt_title))
            .setSubtitle(context.getString(R.string.biometricPrompt_subtitle))
            .setNegativeButtonText(context.getString(R.string.biometricPrompt_buttonCancel))
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
