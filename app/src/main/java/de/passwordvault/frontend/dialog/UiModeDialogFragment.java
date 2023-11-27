package de.passwordvault.frontend.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import de.passwordvault.R;


/**
 * Class implements a dialog window which allows the user to change the UI mode between "Light",
 * "Dark" and "System Default".
 *
 * @author  Christian-2003
 * @version 2.2.2
 */
public class UiModeDialogFragment extends DialogFragment {

    /**
     * Attribute stores the current UI mode.
     */
    private int uiMode;

    /**
     * Attribute stores the class which called this dialog.
     */
    private DialogCallbackListener callbackListener;


    /**
     * Constructor instantiates a new {@link UiModeDialogFragment} which has the passed UI mode
     * selected.
     *
     * @param uiMode                Currently selected UI mode.
     * @param context               Fragment which called this dialog.
     * @throws NullPointerException The passed context is {@code null}.
     * @throws ClassCastException   The passed context does not implement {@link DialogCallbackListener}.
     */
    public UiModeDialogFragment(int uiMode, Fragment context) throws NullPointerException, ClassCastException {
        if (context == null) {
            throw new NullPointerException("Null is invalid context");
        }
        callbackListener = (DialogCallbackListener)context;
        this.uiMode = uiMode;
    }


    /**
     * Method returns the UI mode that was selected by the user within this dialog.
     *
     * @return  Selected UI mode.
     */
    public int getUiMode() {
        return uiMode;
    }


    /**
     * Method is called whenever a UiModeDialog is created.
     *
     * @param savedInstanceState    Previously saved state of the instance.
     * @return                      Created dialog.
     */
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        setRetainInstance(true);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = requireActivity().getLayoutInflater().inflate(R.layout.dialog_settings_uimode, null);

        //Configure the title:
        builder.setTitle(R.string.settings_ui_mode);

        //Configure the view:
        RadioButton lightMode = view.findViewById(R.id.dialog_settings_uimode_light);
        RadioButton darkMode = view.findViewById(R.id.dialog_settings_uimode_dark);
        RadioButton systemMode = view.findViewById(R.id.dialog_settings_uimode_system);
        switch (uiMode) {
            case 1:
                lightMode.setChecked(true);
                break;
            case 2:
                darkMode.setChecked(true);
                break;
            default:
                systemMode.setChecked(true);
                break;
        }
        lightMode.setOnClickListener(v -> {
            uiMode = 1;
            callbackListener.onPositiveCallback(UiModeDialogFragment.this);
            dismiss();
        });
        darkMode.setOnClickListener(v -> {
            uiMode = 2;
            callbackListener.onPositiveCallback(UiModeDialogFragment.this);
            dismiss();
        });
        systemMode.setOnClickListener(v -> {
            uiMode = 0;
            callbackListener.onPositiveCallback(UiModeDialogFragment.this);
            dismiss();
        });
        builder.setView(view);

        //Add dialog buttons:
        builder.setNegativeButton(R.string.button_cancel, (dialog, id) -> {
            callbackListener.onNegativeCallback(UiModeDialogFragment.this);
        });

        return builder.create();
    }

}
