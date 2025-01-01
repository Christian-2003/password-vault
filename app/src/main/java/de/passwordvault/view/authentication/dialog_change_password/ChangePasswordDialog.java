package de.passwordvault.view.authentication.dialog_change_password;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import de.passwordvault.R;
import de.passwordvault.view.utils.components.PasswordVaultBottomSheetDialog;


/**
 * Class implements the dialog to change a password.
 *
 * @author  Christian-2003
 * @version 3.7.1
 */
public class ChangePasswordDialog extends PasswordVaultBottomSheetDialog<ChangePasswordViewModel> {

    /**
     * Constructor instantiates a new dialog.
     */
    public ChangePasswordDialog() {
        super(ChangePasswordViewModel.class, R.layout.dialog_change_password);
    }


    /**
     * Method is called whenever the view of the dialog is created.
     *
     * @param inflater              Layout inflater to use in order to inflate the dialog view.
     * @param container             Parent view of the dialog.
     * @param savedInstanceState    Previously saved state of the instance.
     * @return                      View for the dialog.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (view == null) {
            return null;
        }

        //Configure save button:
        Button saveButton = view.findViewById(R.id.button_save);
        saveButton.setOnClickListener(v -> {
            if (!viewModel.processUserInput(view)) {
                //Some inputs are incorrect:
                return;
            }
            dismiss();
        });

        //Configure cancel button:
        Button cancelButton = view.findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(v -> dismiss());

        return view;
    }

}
