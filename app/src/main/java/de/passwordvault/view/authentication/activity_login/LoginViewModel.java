package de.passwordvault.view.authentication.activity_login;

import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModel;
import java.util.concurrent.Executor;
import de.passwordvault.App;
import de.passwordvault.R;
import de.passwordvault.model.security.login.Account;
import de.passwordvault.view.authentication.activity_login.LoginActivity;


/**
 * Class implements the {@linkplain ViewModel} for the
 * {@link LoginActivity}.
 *
 * @author  Christian-2003
 * @version 3.6.0
 */
public class LoginViewModel extends ViewModel {

    /**
     * Attribute stores the prompt info for the biometric prompt.
     */
    private final BiometricPrompt.PromptInfo biometricPromptInfo = new BiometricPrompt.PromptInfo.Builder().setTitle(App.getContext().getString(R.string.login_biometrics_title)).setNegativeButtonText(App.getContext().getString(R.string.button_cancel)).build();

    /**
     * Attribute stores whether the biometric authentication was cancelled on purpose by the user.
     */
    private boolean biometricAuthenticationCancelled;

    /**
     * Attribute stores the executor that is used for executing the biometric-login dialog.
     */
    private final Executor executor = ContextCompat.getMainExecutor(App.getContext());

    /**
     * Attribute stores whether the splash screen is currently visible.
     */
    private boolean splashScreenVisible;

    /**
     * Attribute stores whether the splash screen timer has been started.
     */
    private boolean splashScreenTimerStarted;


    /**
     * Constructor instantiates a new view model.
     */
    public LoginViewModel() {
        biometricAuthenticationCancelled = false;
        splashScreenVisible = true;
        splashScreenTimerStarted = false;
    }


    /**
     * Method returns the {@link #biometricPromptInfo}.
     *
     * @return  Prompt info of the biometric prompt.
     */
    public BiometricPrompt.PromptInfo getBiometricPromptInfo() {
        return biometricPromptInfo;
    }

    /**
     * Method returns whether the biometric authentication was cancelled by the user on purpose.
     *
     * @return  Whether biometric authentication was cancelled on purpose.
     */
    public boolean isBiometricAuthenticationCancelled() {
        return biometricAuthenticationCancelled;
    }

    /**
     * Method changes whether the biometric authentication is cancelled by the user on purpose.
     *
     * @param biometricAuthenticationCancelled  Whether biometric authentication is cancelled on
     *                                          purpose.
     */
    public void setBiometricAuthenticationCancelled(boolean biometricAuthenticationCancelled) {
        this.biometricAuthenticationCancelled = biometricAuthenticationCancelled;
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


    /**
     * Method tests whether the password recovery is enabled.
     *
     * @return  Whether the master password recovery is enabled.
     */
    public boolean usePasswordRecovery() {
        return Account.getInstance().getSecurityQuestions().size() >= Account.REQUIRED_SECURITY_QUESTIONS;
    }


    /**
     * Method returns whether the splash screen is visible.
     *
     * @return  Whether the splash screen is visible.
     */
    public boolean isSplashScreenVisible() {
        return splashScreenVisible;
    }


    /**
     * Method starts the timer for the splash screen.
     *
     * @param milliseconds  Time that the splash screen should be visible.
     */
    public void startSplashScreenTimer(int milliseconds) {
        if (!splashScreenTimerStarted) {
            splashScreenTimerStarted = true;
            Thread thread = new Thread(() -> {
                try {
                    Thread.sleep(milliseconds);
                }
                catch (InterruptedException e) {
                    //Ignore...
                }
                finally {
                    splashScreenVisible = false;
                }
            });
            thread.start();
        }
    }

}
