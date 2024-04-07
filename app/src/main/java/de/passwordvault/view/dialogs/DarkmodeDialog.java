package de.passwordvault.view.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.DialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import de.passwordvault.R;
import de.passwordvault.model.storage.Configuration;


/**
 * Class implements a dialog that is used to change between light and dark mode for the application.
 *
 * @author  Christian-2003
 * @version 3.5.1
 */
public class DarkmodeDialog extends DialogFragment {

    /**
     * Attribute stores the inflated view of the dialog.
     */
    private View view;


    /**
     * Method is called whenever a new dialog is created.
     *
     * @param savedInstanceState    Previously saved state of the instance.
     * @return                      Created dialog.
     */
    @SuppressLint("InflateParams") //Ignore passing 'null' as root for the dialog's view.
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        view = requireActivity().getLayoutInflater().inflate(R.layout.dialog_darkmode, null);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
        builder.setTitle(R.string.settings_customization_appearance_darkmode);
        builder.setView(view);

        builder.setNegativeButton(R.string.button_cancel, (dialog, id) -> {
            dismiss();
        });

        RadioButton lightRadioButton = view.findViewById(R.id.dialog_darkmode_radio_button_light);
        RadioButton darkRadioButton = view.findViewById(R.id.dialog_darkmode_radio_button_dark);
        RadioButton systemRadioButton = view.findViewById(R.id.dialog_darkmode_radio_button_system);
        switch (Configuration.getDarkmode()) {
            case Configuration.DARKMODE_LIGHT:
                lightRadioButton.setChecked(true);
                break;
            case Configuration.DARKMODE_DARK:
                darkRadioButton.setChecked(true);
                break;
            default:
                systemRadioButton.setChecked(true);
                break;
        }

        lightRadioButton.setOnCheckedChangeListener((button, checked) -> {
            if (checked) {
                Configuration.setDarkmode(Configuration.DARKMODE_LIGHT);
                Configuration.applyDarkmode();
                dismiss();
            }
        });

        darkRadioButton.setOnCheckedChangeListener((button, checked) -> {
            if (checked) {
                Configuration.setDarkmode(Configuration.DARKMODE_DARK);
                Configuration.applyDarkmode();
                dismiss();
            }
        });

        systemRadioButton.setOnCheckedChangeListener((button, checked) -> {
            if (checked) {
                Configuration.setDarkmode(Configuration.DARKMODE_SYSTEM);
                Configuration.applyDarkmode();
                dismiss();
            }
        });

        return builder.create();
    }

}
