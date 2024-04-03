package de.passwordvault.view.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import de.passwordvault.R;
import de.passwordvault.model.storage.Configuration;
import de.passwordvault.viewmodel.dialogs.LanguageViewModel;


/**
 * Class implements a dialog which allows the user to change the language of the app. If the language
 * was changed, the dialog triggers a
 * {@link de.passwordvault.view.utils.DialogCallbackListener#onPositiveCallback(DialogFragment)}.
 * Otherwise, a {@link de.passwordvault.view.utils.DialogCallbackListener#onNegativeCallback(DialogFragment)}
 * is triggered to inform the calling activity that the language was not changed.
 *
 * @author  Christian-2003
 * @version 3.5.1
 */
public class LanguageDialog extends DialogFragment {

    /**
     * Field stores the key that needs to be used when the callback listener is passed as argument.
     */
    public static final String KEY_CALLBACK_LISTENER = "callback_listener";


    /**
     * Attribute stores the view model of the dialog.
     */
    private LanguageViewModel viewModel;

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
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) throws ClassCastException {
        super.onCreateDialog(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(LanguageViewModel.class);
        view = requireActivity().getLayoutInflater().inflate(R.layout.dialog_language, null);

        if (viewModel.getCallbackListener() == null) {
            viewModel.processArguments(getArguments());
        }

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
        builder.setTitle(R.string.settings_appearance_language);
        builder.setView(view);

        builder.setNegativeButton(R.string.button_cancel, (dialog, id) -> dismiss());

        RadioButton englishRadioButton = view.findViewById(R.id.dialog_language_radio_button_english);
        RadioButton germanRadioButton = view.findViewById(R.id.dialog_language_radio_button_german);
        RadioButton systemRadioButton = view.findViewById(R.id.dialog_language_radio_button_system);
        switch (Configuration.getLanguage()) {
            case Configuration.LANGUAGE_ENGLISH:
                englishRadioButton.setChecked(true);
                break;
            case Configuration.LANGUAGE_GERMAN:
                germanRadioButton.setChecked(true);
                break;
            default:
                systemRadioButton.setChecked(true);
                break;
        }

        englishRadioButton.setOnCheckedChangeListener((button, checked) -> {
            if (checked) {
                Configuration.setLanguage(Configuration.LANGUAGE_ENGLISH);
                dismissSuccessfully();
                return;
            }
            dismiss();
        });

        germanRadioButton.setOnCheckedChangeListener((button, checked) -> {
            if (checked) {
                Configuration.setLanguage(Configuration.LANGUAGE_GERMAN);
                dismissSuccessfully();
                return;
            }
            dismiss();
        });

        systemRadioButton.setOnCheckedChangeListener((button, checked) -> {
            if (checked) {
                Configuration.setLanguage(Configuration.LANGUAGE_SYSTEM);
                dismissSuccessfully();
                return;
            }
            dismiss();
        });

        return builder.create();
    }


    /**
     * Method closes this dialog informing the callback that the language was not changed.
     */
    @Override
    public void dismiss() {
        super.dismiss();
        if (viewModel.getCallbackListener() != null) {
            viewModel.getCallbackListener().onNegativeCallback(this);
        }
    }


    /**
     * Method closes this dialog informing the callback that the language was changed.
     */
    private void dismissSuccessfully() {
        super.dismiss();
        if (viewModel.getCallbackListener() != null) {
            viewModel.getCallbackListener().onPositiveCallback(this);
        }
    }

}
