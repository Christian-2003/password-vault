package de.passwordvault.view.dialogs;

import android.annotation.SuppressLint;
import androidx.appcompat.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import de.passwordvault.R;
import de.passwordvault.view.utils.DialogCallbackListener;
import de.passwordvault.model.detail.Detail;
import de.passwordvault.viewmodel.dialogs.DetailViewModel;


/**
 * Class models a {@linkplain Dialog} which allows the user to enter or edit a {@linkplain Detail}.
 * The {@linkplain android.app.Activity} that creates this dialog, must implement the interface
 * {@linkplain DialogCallbackListener}!
 *
 * @author  Christian-2003
 * @version 3.5.1.
 */
public class DetailDialog extends DialogFragment {

    /**
     * Field contains the key that needs to be used when passing the detail as argument.
     */
    public static final String KEY_DETAIL = "detail";

    /**
     * Field contains the key that needs to be used when passing a
     * {@link de.passwordvault.view.utils.DialogCallbackListener} as argument.
     */
    public static final String KEY_CALLBACK_LISTENER = "callback_listener";

    /**
     * Attribute stores the {@link DetailViewModel} for this {@linkplain DialogFragment}.
     */
    private DetailViewModel viewModel;

    /**
     * Attribute stores the view for the dialog.
     */
    private View view;

    /**
     * Attribute stores whether the dialog is currently dismissing.
     */
    private boolean dismissing;


    /**
     * Constructor constructs a new {@link DetailDialog}-instance.
     */
    public DetailDialog() {
        dismissing = false;
    }


    /**
     * Method returns the {@link Detail} which is created / edited by this dialog.
     *
     * @return  Detail which is being edited / created.
     */
    public Detail getDetail() {
        return viewModel.getDetail();
    }


    /**
     * Method is called whenever a DetailDialog is created.
     *
     * @param savedInstanceState    Previously saved state of the instance.
     * @return                      Created dialog.
     */
    @SuppressLint("InflateParams") //Ignore passing 'null' as root for the dialog's view.
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(DetailViewModel.class);

        try {
            viewModel.processArguments(getArguments());
        }
        catch (Exception e) {
            //Converting every occurring exception to 'ClassCastException' to match methods signature
            //must surely be the stupidest decision made in this project so far...
            throw new ClassCastException(e.getMessage());
        }

        view = requireActivity().getLayoutInflater().inflate(R.layout.dialog_detail, null, false);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
        builder.setTitle(viewModel.getDetail() == null ? R.string.detail_dialog_title_add : R.string.detail_dialog_title_edit);
        builder.setView(viewModel.createView(view));

        builder.setPositiveButton(R.string.button_save, (dialog, id) -> {
            //Action implemented in onStart()-method!
        });
        builder.setNegativeButton(R.string.button_cancel, (dialog, id) -> {
            //Action implemented in onStart()-method!
        });

        return builder.create();
    }


    /**
     * Method configures the ClickListeners of the dialog buttons whenever the dialog is started.
     */
    @Override
    public void onStart() {
        super.onStart();
        AlertDialog dialog = (AlertDialog)getDialog();
        if (dialog == null) {
            //No dialog available:
            return;
        }

        //Configure positive button:
        Button positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(view -> {
            if (!viewModel.processUserInput(DetailDialog.this.view)) {
                //Some necessary data was not entered:
                return;
            }
            dismissPositive();
        });

        //Configure negative button:
        Button negativeButton = dialog.getButton(Dialog.BUTTON_NEGATIVE);
        negativeButton.setOnClickListener(view -> dismissNegative());
    }


    /**
     * Method is called when the dialog is cancelled either through clicking the cancel button or by
     * clicking out of bounds of the dialog window.
     *
     * @param dialog    Dialog which is cancelled.
     */
    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        if (!dismissing) {
            dismissNegative();
        }
        super.onCancel(dialog);
    }


    /**
     * Method dismisses the dialog with a positive callback.
     */
    private void dismissPositive() {
        viewModel.getCallbackListener().onPositiveCallback(DetailDialog.this);
        dismissing = true;
        dismiss();
    }

    /**
     * Method dismisses the dialog with a negative callback.
     */
    private void dismissNegative() {
        viewModel.getCallbackListener().onNegativeCallback(DetailDialog.this);
        dismissing = true;
        dismiss();
    }

}
