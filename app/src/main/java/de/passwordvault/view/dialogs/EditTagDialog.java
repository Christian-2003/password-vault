package de.passwordvault.view.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import de.passwordvault.R;
import de.passwordvault.viewmodel.dialogs.EditTagViewModel;


/**
 * Class implements a {@linkplain Dialog} which allows the user to edit (or add) a
 * {@link de.passwordvault.model.tags.Tag}.
 *
 * @author  Christian-2003
 * @version 3.3.0
 */
public class EditTagDialog extends DialogFragment {

    /**
     * Field contains the key that needs to be used when passing a {@link de.passwordvault.model.tags.Tag}
     * as argument.
     */
    public static final String KEY_TAG = "tag";


    /**
     * Attribute stores the view model of the dialog.
     */
    private EditTagViewModel viewModel;

    /**
     * Attribute stores the inflated view of the dialog.
     */
    private View view;


    /**
     * Constructor instantiates a new {@link EditTagDialog}-instance.
     */
    public EditTagDialog() {
        //Require empty constructor.
    }


    /**
     * Method is called whenever a EditTagDialog is created.
     *
     * @param savedInstanceState    Previously saved state of the instance.
     * @return                      Created dialog.
     */
    @SuppressLint("InflateParams") //Ignore passing 'null' as root for the dialog's view.
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(EditTagViewModel.class);

        try {
            viewModel.processArguments(getArguments());
        }
        catch (Exception e) {
            throw new ClassCastException(e.getMessage());
        }

        view = requireActivity().getLayoutInflater().inflate(R.layout.dialog_edit_tag, null);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
        builder.setTitle(viewModel.isCreatingNewDialog() ? R.string.tag_dialog_title_add : R.string.tag_dialog_title_edit);
        builder.setView(viewModel.createView(view));

        builder.setPositiveButton(R.string.button_save, (dialog, id) -> {
            //Action implemented in onStart()-method!
        });
        builder.setNeutralButton(R.string.button_cancel, (dialog, id) -> {
            //Action implemented in onStart()-method!
        });
        if (!viewModel.isCreatingNewDialog()) {
            builder.setNegativeButton(R.string.button_delete, (dialog, id) -> {
                //Action implemented in onStart()-method!
            });
        }

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
            return;
        }

        //Configure positive button:
        Button positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(view -> {
            if (!viewModel.processUserInput(EditTagDialog.this.view)) {
                //Some data was not entered:
                return;
            }
            dismiss();
        });

        //Configure neutral button:
        Button neutralButton = dialog.getButton(Dialog.BUTTON_NEUTRAL);
        neutralButton.setOnClickListener(view -> dismiss());

        //Configure negative button:
        if (!viewModel.isCreatingNewDialog()) {
            Button negativeButton = dialog.getButton(Dialog.BUTTON_NEGATIVE);
            negativeButton.setOnClickListener(view -> {
                viewModel.deleteTag();
                dismiss();
            });
        }
    }

}
