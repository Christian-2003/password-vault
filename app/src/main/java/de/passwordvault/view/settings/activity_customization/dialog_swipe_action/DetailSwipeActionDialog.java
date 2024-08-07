package de.passwordvault.view.settings.activity_customization.dialog_swipe_action;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import de.passwordvault.R;
import de.passwordvault.model.detail.DetailSwipeAction;


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

        //Setup left swipe action:
        RadioButton deleteLeftSwipeRadioButton = view.findViewById(R.id.radio_left_swipe_delete);
        RadioButton editLeftSwipeRadioButton = view.findViewById(R.id.radio_left_swipe_edit);
        ImageView leftSwipeImageView = view.findViewById(R.id.left_swipe_image);
        LinearLayout leftSwipeImageContainer = view.findViewById(R.id.left_swipe_image_container);
        switch (viewModel.getLeftSwipeAction()) {
            case EDIT:
                editLeftSwipeRadioButton.setChecked(true);
                leftSwipeImageView.setImageResource(R.drawable.ic_edit);
                leftSwipeImageContainer.setBackgroundColor(requireContext().getColor(R.color.pv_primary));
                break;
            case DELETE:
                deleteLeftSwipeRadioButton.setChecked(true);
                leftSwipeImageView.setImageResource(R.drawable.ic_delete);
                leftSwipeImageContainer.setBackgroundColor(requireContext().getColor(R.color.pv_red));
                break;
        }

        //Setup right swipe action:
        RadioButton deleteRightSwipeRadioButton = view.findViewById(R.id.radio_right_swipe_delete);
        RadioButton editRightSwipeRadioButton = view.findViewById(R.id.radio_right_swipe_edit);
        ImageView rightSwipeImageView = view.findViewById(R.id.right_swipe_image);
        LinearLayout rightSwipeImageContainer = view.findViewById(R.id.right_swipe_image_container);
        switch (viewModel.getRightSwipeAction()) {
            case EDIT:
                editRightSwipeRadioButton.setChecked(true);
                rightSwipeImageView.setImageResource(R.drawable.ic_edit);
                rightSwipeImageContainer.setBackgroundColor(requireContext().getColor(R.color.pv_primary));
                break;
            case DELETE:
                deleteRightSwipeRadioButton.setChecked(true);
                rightSwipeImageView.setImageResource(R.drawable.ic_delete);
                rightSwipeImageContainer.setBackgroundColor(requireContext().getColor(R.color.pv_red));
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

        LinearLayout editLeftSwipeContainer = view.findViewById(R.id.container_left_swipe_edit);
        LinearLayout deleteLeftSwipeContainer = view.findViewById(R.id.container_left_swipe_delete);
        LinearLayout editRightSwipeContainer = view.findViewById(R.id.container_right_swipe_edit);
        LinearLayout deleteRightSwipeContainer = view.findViewById(R.id.container_right_swipe_delete);
        editLeftSwipeContainer.setOnClickListener(view1 -> editLeftSwipeRadioButton.setChecked(true));
        deleteLeftSwipeContainer.setOnClickListener(view1 -> deleteLeftSwipeRadioButton.setChecked(true));
        editRightSwipeContainer.setOnClickListener(view1 -> editRightSwipeRadioButton.setChecked(true));
        deleteRightSwipeContainer.setOnClickListener(view1 -> deleteRightSwipeRadioButton.setChecked(true));

        return builder.create();
    }

}
