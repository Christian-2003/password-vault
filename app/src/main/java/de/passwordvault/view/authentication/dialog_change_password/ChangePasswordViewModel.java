package de.passwordvault.view.authentication.dialog_change_password;

import android.content.Context;
import android.view.View;
import androidx.lifecycle.ViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.Objects;
import de.passwordvault.R;
import de.passwordvault.model.security.login.Account;


/**
 * Class implements the {@linkplain ViewModel} for {@link ChangePasswordDialog}.
 *
 * @author  Christian-2003
 * @version 3.2.0
 */
public class ChangePasswordViewModel extends ViewModel {

    /**
     * Method processes and validates the user input. If some input is incorrect, the visuals of
     * the passed {@linkplain View} are changed to inform the user about the incorrect input. In this
     * case, {@code false} is returned. If the input is correct, {@code true} is returned.
     *
     * @param view  View from which the input shall be retrieved.
     * @return      Whether the user input is correct.
     */
    public boolean processUserInput(View view) {
        if (view == null) {
            return false;
        }
        Context context = view.getContext();
        boolean inputCorrect = true;

        //Test whether current password is correct:
        TextInputEditText currentPasswordEditText = view.findViewById(R.id.change_password_current);
        TextInputLayout currentPasswordLayout = view.findViewById(R.id.change_password_current_hint);
        String currentPassword = Objects.requireNonNull(currentPasswordEditText.getText()).toString();
        if (currentPassword.isEmpty()) {
            currentPasswordLayout.setError(context.getString(R.string.error_empty_input));
            inputCorrect = false;
        }
        else {
            if (!Account.getInstance().isPassword(currentPassword)) {
                currentPasswordLayout.setError(context.getString(R.string.error_passwords_incorrect));
                inputCorrect = false;
            }
            else {
                currentPasswordLayout.setErrorEnabled(false);
            }
        }

        //Test whether the new password is correct:
        TextInputEditText newPasswordEditText = view.findViewById(R.id.change_password_new);
        TextInputLayout newPasswordLayout = view.findViewById(R.id.change_password_new_hint);
        String newPassword = Objects.requireNonNull(newPasswordEditText.getText()).toString();
        TextInputEditText confirmPasswordEditText = view.findViewById(R.id.change_password_confirm_new);
        TextInputLayout confirmPasswordLayout = view.findViewById(R.id.change_password_confirm_new_hint);
        String confirmPassword = Objects.requireNonNull(confirmPasswordEditText.getText()).toString();
        if (newPassword.isEmpty()) {
            newPasswordLayout.setError(context.getString(R.string.error_empty_input));
            inputCorrect = false;
        }
        else {
            newPasswordLayout.setErrorEnabled(false);
        }
        if (confirmPassword.isEmpty()) {
            confirmPasswordLayout.setError(context.getString(R.string.error_empty_input));
            inputCorrect = false;
        }
        else {
            confirmPasswordLayout.setErrorEnabled(false);
        }
        if (!newPassword.isEmpty() && !confirmPassword.isEmpty() && !newPassword.equals(confirmPassword)) {
            confirmPasswordLayout.setError(context.getString(R.string.error_passwords_not_matching));
            inputCorrect = false;
        }

        //Apply input:
        if (!inputCorrect) {
            return false;
        }
        Account.getInstance().setPassword(newPassword);
        Account.getInstance().save();
        return true;
    }

}
