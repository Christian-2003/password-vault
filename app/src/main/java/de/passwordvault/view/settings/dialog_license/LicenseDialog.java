package de.passwordvault.view.settings.dialog_license;

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
 * Class implements the dialog displaying the license text of a used software.
 *
 * @author  Christian-2003
 * @version 3.7.0
 */
public class LicenseDialog extends PasswordVaultBottomSheetDialog<LicenseViewModel> {

    /**
     * Field stores the key with which to pass the title of the dialog.
     */
    public static final String ARG_TITLE = "arg_title";

    /**
     * Field stores the key with which to pass the license text of the dialog.
     */
    public static final String ARG_LICENSE = "arg_license";


    /**
     * Constructor instantiates a new dialog.
     */
    public LicenseDialog() {
        super(LicenseViewModel.class, R.layout.dialog_license);
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
            ((TextView)view.findViewById(R.id.text_title)).setText(viewModel.getTitle());
            ((TextView)view.findViewById(R.id.text_license)).setText(viewModel.getLicense());
        }

        return view;
    }

}
