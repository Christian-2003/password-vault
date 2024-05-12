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
import de.passwordvault.model.detail.DetailSwipeAction;
import de.passwordvault.viewmodel.dialogs.DetailSwipeActionViewModel;


/**
 * Class implements the dialog with which the user can change the swipe actions for swiping details.
 *
 * @author  Christian-2003
 * @version 3.5.4
 */
public class DetailSwipeActionDialog extends DialogFragment {

    /**
     * Attribute stores the view model for the dialog.
     */
    private DetailSwipeActionViewModel viewModel;


    /**
     * Method is called whenever the dialog is created.
     *
     * @param savedInstanceState    Previously saved state of the instance.
     * @return                      Created dialog.
     */
    @SuppressLint("InflateParams") //Ignore passing 'null' as root for the dialog's view.
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(DetailSwipeActionViewModel.class);

        View view = requireActivity().getLayoutInflater().inflate(R.layout.dialog_detail_swipe_action, null);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
        builder.setTitle(R.string.settings_customization_details_swipe);
        builder.setView(view);

        builder.setNegativeButton(R.string.button_cancel, (dialog, id) -> {
            dismiss();
        });

        RadioButton deleteLeftSwipeRadioButton = view.findViewById(R.id.radio_left_swipe_delete);
        RadioButton editLeftSwipeRadioButton = view.findViewById(R.id.radio_left_swipe_edit);
        RadioButton deleteRightSwipeRadioButton = view.findViewById(R.id.radio_right_swipe_delete);
        RadioButton editRightSwipeRadioButton = view.findViewById(R.id.radio_right_swipe_edit);
        switch (viewModel.getLeftSwipeAction()) {
            case EDIT:
                editLeftSwipeRadioButton.setChecked(true);
                break;
            case DELETE:
                deleteLeftSwipeRadioButton.setChecked(true);
                break;
        }
        switch (viewModel.getRightSwipeAction()) {
            case EDIT:
                editRightSwipeRadioButton.setChecked(true);
                break;
            case DELETE:
                deleteRightSwipeRadioButton.setChecked(true);
                break;
        }

        deleteLeftSwipeRadioButton.setOnCheckedChangeListener((radio, checked) -> {
            if (checked) {
                viewModel.setLeftSwipeAction(DetailSwipeAction.DELETE);
                dismiss();
            }
        });
        editLeftSwipeRadioButton.setOnCheckedChangeListener((radio, checked) -> {
            if (checked) {
                viewModel.setLeftSwipeAction(DetailSwipeAction.EDIT);
                dismiss();
            }
        });
        deleteRightSwipeRadioButton.setOnCheckedChangeListener((radio, checked) -> {
            if (checked) {
                viewModel.setRightSwipeAction(DetailSwipeAction.DELETE);
                dismiss();
            }
        });
        editRightSwipeRadioButton.setOnCheckedChangeListener((radio, checked) -> {
            if (checked) {
                viewModel.setRightSwipeAction(DetailSwipeAction.EDIT);
                dismiss();
            }
        });

        return builder.create();
    }

}
