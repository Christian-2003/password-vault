package de.passwordvault.view.general.dialog_delete;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import de.passwordvault.R;
import de.passwordvault.view.utils.components.PasswordVaultBottomSheetDialog;


/**
 * Class implements a dialog asking the user to confirm the deletion of some item.
 *
 * @author  Christian-2003
 * @version 3.7.0
 */
public class DeleteDialog extends PasswordVaultBottomSheetDialog<DeleteViewModel> {

    /**
     * Field stores the key with which to pass the message for the dialog.
     */
    public static final String ARG_MESSAGE = "arg_message";


    /**
     * Constructor instantiates a new dialog.
     */
    public DeleteDialog() {
        super(DeleteViewModel.class, R.layout.dialog_delete);
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

        Bundle args = getArguments();
        if (args != null) {
            viewModel.processArguments(args);
        }

        if (view != null) {
            ((TextView)view.findViewById(R.id.text_message)).setText(viewModel.getMessage());

            view.findViewById(R.id.button_cancel).setOnClickListener(v -> dismiss());
            view.findViewById(R.id.button_delete).setOnClickListener(v -> {
                if (callback != null) {
                    callback.onCallback(this, Callback.RESULT_SUCCESS);
                    dismiss();
                }
            });
        }

        return view;
    }

}
