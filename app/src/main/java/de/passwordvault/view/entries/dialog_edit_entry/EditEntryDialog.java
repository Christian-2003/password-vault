package de.passwordvault.view.entries.dialog_edit_entry;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.ArrayList;
import de.passwordvault.R;
import de.passwordvault.model.tags.Tag;
import de.passwordvault.model.tags.TagManager;
import de.passwordvault.view.entries.dialog_edit_tag.EditTagDialog;
import de.passwordvault.view.utils.components.PasswordVaultBottomSheetDialog;


/**
 * Class implements the dialog to edit the dialog to edit an entry.
 *
 * @author  Christian-2003
 * @version 3.7.0
 */
public class EditEntryDialog extends PasswordVaultBottomSheetDialog<EditEntryViewModel> {

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
     * Attribute stores the chip group displaying all tags.
     */
    private ChipGroup tagContainer;


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
            tagContainer = view.findViewById(R.id.container_tags);
            notifyTagsChanged();

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
     * Method notifies the dialog that the tags have changed. This makes the dialog redraw the views
     * displaying the tags.
     */
    public void notifyTagsChanged() {
        if (tagContainer != null) {
            Context context = getContext();
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
                chip.setOnLongClickListener(v -> {
                    EditTagDialog d = new EditTagDialog();
                    Bundle args = new Bundle();
                    args.putSerializable(EditTagDialog.ARG_TAG, tag);
                    d.setArguments(args);
                    d.show(getParentFragmentManager(), null);
                    return true;
                });
                tagContainer.addView(chip);
            }
        }
        if (viewModel != null) {
            viewModel.setTagListChanged(true);
        }
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
     * Method returns whether the global list of tags (from {@link TagManager}) changed through this
     * dialog. Even if a callback of type
     * {@link de.passwordvault.view.utils.components.PasswordVaultBottomSheetDialog.Callback#RESULT_CANCEL}
     * is invoked, this method may still return {@code true}.
     *
     * @return  Whether the global tag list changed.
     */
    public boolean isTagListChanged() {
        return viewModel.isTagListChanged();
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

}
