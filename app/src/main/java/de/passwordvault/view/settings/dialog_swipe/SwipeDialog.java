package de.passwordvault.view.settings.dialog_swipe;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import de.passwordvault.R;
import de.passwordvault.model.detail.DetailSwipeAction;
import de.passwordvault.view.utils.components.PasswordVaultBottomSheetDialog;


/**
 * Class implements the dialog with which the user can change the swipe actions for swiping details.
 *
 * @author  Christian-2003
 * @version 3.7.0
 */
public class SwipeDialog extends PasswordVaultBottomSheetDialog<SwipeViewModel> {

    /**
     * Constructor instantiates a new dialog.
     */
    public SwipeDialog() {
        super(SwipeViewModel.class, R.layout.dialog_swipe);
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

        if (view != null) {
            Context context = requireContext();
            ColorStateList editColorStateList = ContextCompat.getColorStateList(context, R.color.primary);
            ColorStateList deleteColorStateList = ContextCompat.getColorStateList(context, R.color.text_critical);

            //Setup left swipe action:
            RadioButton deleteLeftSwipeRadioButton = view.findViewById(R.id.radio_left_swipe_delete);
            RadioButton editLeftSwipeRadioButton = view.findViewById(R.id.radio_left_swipe_edit);
            ImageView leftSwipeImageView = view.findViewById(R.id.left_swipe_image);
            LinearLayout leftSwipeImageContainer = view.findViewById(R.id.left_swipe_image_container);
            switch (viewModel.getLeftSwipeAction()) {
                case EDIT:
                    editLeftSwipeRadioButton.setChecked(true);
                    leftSwipeImageView.setImageResource(R.drawable.ic_edit);
                    leftSwipeImageContainer.setBackgroundTintList(editColorStateList);
                    break;
                case DELETE:
                    deleteLeftSwipeRadioButton.setChecked(true);
                    leftSwipeImageView.setImageResource(R.drawable.ic_delete);
                    leftSwipeImageContainer.setBackgroundTintList(deleteColorStateList);
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
                    rightSwipeImageContainer.setBackgroundTintList(editColorStateList);
                    break;
                case DELETE:
                    deleteRightSwipeRadioButton.setChecked(true);
                    rightSwipeImageView.setImageResource(R.drawable.ic_delete);
                    rightSwipeImageContainer.setBackgroundTintList(deleteColorStateList);
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
        }

        return view;
    }

}
