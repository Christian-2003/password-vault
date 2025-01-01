package de.passwordvault.view.authentication.dialog_authentication;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import de.passwordvault.R;
import de.passwordvault.view.utils.components.PasswordVaultBottomSheetDialog;


/**
 * Class implements a dialog with which the user can authenticate (or register) using the master
 * password of the application.
 * This dialog is used by {@link de.passwordvault.model.security.authentication.Authenticator}.
 *
 * @author  Christian-2003
 * @version 3.5.3
 */
public class PasswordAuthenticationDialog extends PasswordVaultBottomSheetDialog<PasswordAuthenticationViewModel> {

    /**
     * Field stores the key that needs to be used when passing the ID of the resource-string used
     * as title.
     */
    public static final String ARG_TITLE_ID = "title_id";

    /**
     * Field stores the key that needs to be used to when passing a flag indicating whether the
     * dialog shall be used to register a new password.
     */
    public static final String ARG_REGISTER = "register";


    public PasswordAuthenticationDialog(@Nullable Callback attachableCallback) {
        super(PasswordAuthenticationViewModel.class, R.layout.dialog_password_authentication, attachableCallback);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (view == null) {
            return null;
        }

        viewModel.processArguments(getArguments());
        if (!viewModel.isRegistering()) {
            view.findViewById(R.id.dialog_password_authentication_confirm_container).setVisibility(View.GONE);
        }

        //Configure title:
        ((TextView)view.findViewById(R.id.text_title)).setText(viewModel.getTitleId());

        //Configure positive button:
        Button okButton = view.findViewById(R.id.button_ok);
        okButton.setOnClickListener(v -> {
            if (!viewModel.processUserInput(view)) {
                //Some inputs are incorrect:
                return;
            }
            dismiss();
            if (callback != null) {
                callback.onCallback(this, Callback.RESULT_SUCCESS);
            }
        });

        //Configure negative button:
        Button cancelButton = view.findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(v -> {
            dismiss();
            if (callback != null) {
                callback.onCallback(this, Callback.RESULT_CANCEL);
            }
        });

        return view;
    }


    /**
     * Method is called whenever the dialog execution is cancelled (e.g. through clicking out of
     * bounds of the dialog window).
     *
     * @param dialog    Dialog that is cancelled.
     */
    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        if (callback != null) {
            callback.onCallback(this, Callback.RESULT_CANCEL);
        }
        super.onCancel(dialog);
    }

}
