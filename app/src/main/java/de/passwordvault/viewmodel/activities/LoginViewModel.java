package de.passwordvault.viewmodel.activities;

import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModel;
import java.util.concurrent.Executor;
import de.passwordvault.App;
import de.passwordvault.R;
import de.passwordvault.model.security.login.Account;


/**
 * Class implements the {@linkplain ViewModel} for the
 * {@link de.passwordvault.view.activities.LoginActivity}.
 *
 * @author  Christian-2003
 * @version 3.2.0
 */
public class LoginViewModel extends ViewModel {

    /**
     * Attribute stores the prompt info for the biometric prompt.
     */
    private final BiometricPrompt.PromptInfo biometricPromptInfo = new BiometricPrompt.PromptInfo.Builder().setTitle(App.getContext().getString(R.string.login_biometrics_title)).setNegativeButtonText(App.getContext().getString(R.string.button_cancel)).build();

    /**
     * Attribute stores the executor that is used for executing the biometric-login dialog.
     */
    private final Executor executor = ContextCompat.getMainExecutor(App.getContext());


    /**
     * Method returns the {@link #biometricPromptInfo}.
     *
     * @return  Prompt info of the biometric prompt.
     */
    public BiometricPrompt.PromptInfo getBiometricPromptInfo() {
        return biometricPromptInfo;
    }

    /**
     * Method returns the {@link #executor}.
     *
     * @return  Executor.
     */
    public Executor getExecutor() {
        return executor;
    }


    /**
     * Method tests whether class 3 biometrics are available on the Android device.
     *
     * @return  Whether class 3 biometrics are available.
     */
    public boolean areBiometricsAvailable() {
        BiometricManager biometricManager = BiometricManager.from(App.getContext());
        int result = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG);
        return result == BiometricManager.BIOMETRIC_SUCCESS;
    }


    /**
     * Method tests whether the provided password is correct.
     *
     * @param s Password to be tested.
     * @return  Whether the password matches.
     */
    public boolean confirmPassword(String s) throws NullPointerException {
        return Account.getInstance().isPassword(s);
    }


    /**
     * Method tests whether the login shall be done using biometrics.
     *
     * @return  Whether the login shall be done using biometrics.
     */
    public boolean useBiometrics() {
        return Account.getInstance().useBiometrics() && areBiometricsAvailable();
    }

}
