package de.passwordvault.view.settings.activity_customization.dialog_darkmode;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import de.passwordvault.R;
import de.passwordvault.model.storage.settings.Config;


/**
 * Class implements a dialog that is used to change between light and dark mode for the application.
 *
 * @author  Christian-2003
 * @version 3.6.0
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

        RadioButton lightRadioButton = view.findViewById(R.id.radio_light);
        RadioButton darkRadioButton = view.findViewById(R.id.radio_dark);
        RadioButton systemRadioButton = view.findViewById(R.id.radio_system);
        switch (Config.getInstance().darkmode.get()) {
            case Config.Constants.DARKMODE_LIGHT:
                lightRadioButton.setChecked(true);
                break;
            case Config.Constants.DARKMODE_DARK:
                darkRadioButton.setChecked(true);
                break;
            default:
                systemRadioButton.setChecked(true);
                break;
        }

        lightRadioButton.setOnCheckedChangeListener((button, checked) -> {
            if (checked) {
                Config.getInstance().darkmode.set(Config.Constants.DARKMODE_LIGHT);
                Config.Methods.applyDarkmode();
                dismiss();
            }
        });

        darkRadioButton.setOnCheckedChangeListener((button, checked) -> {
            if (checked) {
                Config.getInstance().darkmode.set(Config.Constants.DARKMODE_DARK);
                Config.Methods.applyDarkmode();
                dismiss();
            }
        });

        systemRadioButton.setOnCheckedChangeListener((button, checked) -> {
            if (checked) {
                Config.getInstance().darkmode.set(Config.Constants.DARKMODE_SYSTEM);
                Config.Methods.applyDarkmode();
                dismiss();
            }
        });

        LinearLayout lightContainer = view.findViewById(R.id.container_light);
        LinearLayout darkContainer = view.findViewById(R.id.container_dark);
        LinearLayout systemContainer = view.findViewById(R.id.container_system);
        lightContainer.setOnClickListener(view -> lightRadioButton.setChecked(true));
        darkContainer.setOnClickListener(view -> darkRadioButton.setChecked(true));
        systemContainer.setOnClickListener(view -> systemRadioButton.setChecked(true));

        return builder.create();
    }

}
