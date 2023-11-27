package de.passwordvault.frontend.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import de.passwordvault.R;


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
     * @param uiMode    Currently selected UI mode.
     */
    public UiModeDialogFragment(int uiMode) {
        this.uiMode = uiMode;
    }


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
                lightMode.setSelected(true);
                break;
            case 2:
                darkMode.setSelected(true);
                break;
            default:
                systemMode.setSelected(true);
                break;
        }
        lightMode.setOnClickListener(v -> {
            uiMode = 1;
            callbackListener.onPositiveCallback(UiModeDialogFragment.this);
        });
        darkMode.setOnClickListener(v -> {
            uiMode = 2;
            callbackListener.onPositiveCallback(UiModeDialogFragment.this);
        });
        systemMode.setOnClickListener(v -> {
            uiMode = 0;
            callbackListener.onPositiveCallback(UiModeDialogFragment.this);
        });
        builder.setView(view);

        //Add dialog buttons:
        builder.setNegativeButton(R.string.button_cancel, (dialog, id) -> {
            callbackListener.onNegativeCallback(UiModeDialogFragment.this);
        });

        return builder.create();
    }


    /**
     * Method is called whenever this dialog window is attached to an activity.
     *
     * @param context               Context to which the dialog window is attached.
     * @throws ClassCastException   The activity which creates this dialog window does not implement
     *                              {@linkplain DialogCallbackListener}-interface.
     */
    @Override
    public void onAttach(@NonNull Context context) throws ClassCastException {
        super.onAttach(context);
        try {
            callbackListener = (DialogCallbackListener)context;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement DialogCallbackListener");
        }
    }

}
