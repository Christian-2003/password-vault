package de.passwordvault.view.entries.dialog_edit_entry;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.ArrayList;
import de.passwordvault.R;
import de.passwordvault.model.tags.Tag;
import de.passwordvault.view.entries.dialog_edit_tag.EditTagDialog;
import de.passwordvault.view.utils.components.PasswordVaultBottomSheetDialog;


/**
 * Class implements the dialog to edit the dialog to edit an entry.
 *
 * @author  Christian-2003
 * @version 3.7.0
 */
public class EditEntryDialog extends PasswordVaultBottomSheetDialog<EditEntryViewModel> implements PasswordVaultBottomSheetDialog.Callback {

    /**
     * Attribute stores the key with which to pass the name to edit as string.
     */
    public static final String ARG_NAME = "arg_name";

    /**
     * Attribute stores the key with which to pass the description to edit as string.
     */
    public static final String ARG_DESCRIPTION = "arg_description";

    /**
     * Attribute stores the key with which to pass all selected tags as array list.
     */
    public static final String ARG_TAGS = "arg_tags";


    /**
     * Attribute stores the edit text used to enter the name for the entry.
     */
    private TextInputEditText nameEditText;

    /**
     * Attribute stores the parent containing the edit text to enter the name for the entry.
     */
    private TextInputLayout nameContainer;

    /**
     * Attribute stores the edit text used to enter the description for the entry.
     */
    private TextInputEditText descriptionEditText;


    /**
     * Constructor instantiates a new dialog.
     */
    public EditEntryDialog() {
        super(EditEntryViewModel.class, R.layout.dialog_edit_entry);
    }


    /**
     * Method creates a new view for the dialog.
     *
     * @param inflater              Layout inflater to use in order to inflate the dialog view.
     * @param container             Parent view of the dialog.
     * @param savedInstanceState    Previously saved state of the instance.
     * @return                      Created view for the dialog.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        Bundle args = getArguments();
        viewModel.processArgs(args);

        Context context = requireContext();

        if (view != null) {
            //Name / description:
            nameEditText = view.findViewById(R.id.input_name);
            nameContainer = view.findViewById(R.id.container_name);
            descriptionEditText = view.findViewById(R.id.input_description);
            nameEditText.setText(viewModel.getName());
            descriptionEditText.setText(viewModel.getDescription());

            //Button to add a new tag:
            view.findViewById(R.id.button_tags).setOnClickListener(v -> {
                EditTagDialog d = new EditTagDialog();
                d.show(getParentFragmentManager(), null);
            });

            //Tags:
            ChipGroup tagContainer = view.findViewById(R.id.container_tags);
            tagContainer.removeAllViews();
            for (Tag tag : viewModel.getAllTags()) {
                Chip chip = new Chip(context);
                chip.setText(tag.getName());
                chip.setCheckable(true);
                if (viewModel.getSelectedTags().contains(tag)) {
                    chip.setChecked(true);
                }
                chip.setOnCheckedChangeListener((v, checked) -> {
                    if (checked) {
                        viewModel.getSelectedTags().add(tag);
                    }
                    else {
                        viewModel.getSelectedTags().remove(tag);
                    }
                });
                tagContainer.addView(chip);
            }

            //Save button:
            view.findViewById(R.id.button_save).setOnClickListener(v -> {
                if (validateUserInput()) {
                    if (callback != null) {
                        callback.onCallback(this, Callback.RESULT_SUCCESS);
                    }
                    dismiss();
                }
            });

            //Cancel button:
            view.findViewById(R.id.button_cancel).setOnClickListener(v -> {
                if (callback != null) {
                    callback.onCallback(this, Callback.RESULT_CANCEL);
                }
                dismiss();
            });
        }

        return view;
    }


    /**
     * Method returns the name entered by the user.
     *
     * @return  Name entered by the user.
     */
    public String getName() {
        return viewModel.getName();
    }

    /**
     * Method returns the description entered by the user.
     *
     * @return  Description entered by the user.
     */
    public String getDescription() {
        return viewModel.getDescription();
    }

    /**
     * Method returns a list of all tags selected by the user.
     *
     * @return  List of selected tags.
     */
    public ArrayList<Tag> getSelectedTags() {
        return viewModel.getSelectedTags();
    }


    /**
     * Method validates the user inputs. If all inputs are valid, {@code true} is returned. Otherwise,
     * {@code false} is returned and the UI is updated to inform the user about invalid inputs.
     *
     * @return  Whether all userdata is valid.
     */
    private boolean validateUserInput() {
        if (nameEditText.getText() != null) {
            viewModel.setName(nameEditText.getText().toString());
        }
        if (descriptionEditText.getText() != null) {
            viewModel.setDescription(descriptionEditText.getText().toString());
        }
        if (viewModel.getName().isEmpty()) {
            nameContainer.setError(getString(R.string.error_empty_input));
            return false;
        }
        else {
            nameContainer.setErrorEnabled(false);
            return true;
        }
    }

    @Override
    public void onCallback(PasswordVaultBottomSheetDialog<? extends ViewModel> dialog, int resultCode) {
        Log.d("DIALOG", "Callback received: " + resultCode);
        if (resultCode == Callback.RESULT_SUCCESS && dialog instanceof EditTagDialog) {
            EditTagDialog editTagDialog = (EditTagDialog)dialog;
        }
    }

}
