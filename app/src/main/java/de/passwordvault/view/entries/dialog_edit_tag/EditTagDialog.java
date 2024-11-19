package de.passwordvault.view.entries.dialog_edit_tag;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import de.passwordvault.R;
import de.passwordvault.model.tags.Tag;
import de.passwordvault.view.utils.components.PasswordVaultBottomSheetDialog;


/**
 * Class implements the dialog to edit / add a tag.
 *
 * @author  Christian-2003
 * @version 3.7.0
 */
public class EditTagDialog extends PasswordVaultBottomSheetDialog<EditTagViewModel> {

    /**
     * Field stores the key with which to pass the tag to edit as serializable. If no tag is passed
     * this way, a new tag will be created.
     */
    public static final String ARG_TAG = "arg_tag";


    /**
     * Attribute stores the edit text used to enter the name for the tag.
     */
    private TextInputEditText nameEditText;

    /**
     * Attribute stores the container for the {@link #nameEditText}.
     */
    private TextInputLayout nameContainer;


    /**
     * Constructor instantiates a new dialog.
     */
    public EditTagDialog() {
        super(EditTagViewModel.class, R.layout.dialog_edit_tag);
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
        if (view == null) {
            return null;
        }

        Bundle args = getArguments();
        viewModel.processArguments(args);

        nameEditText = view.findViewById(R.id.input_name);
        nameContainer = view.findViewById(R.id.container_name);
        Button saveButton = view.findViewById(R.id.button_save);
        Button cancelButton = view.findViewById(R.id.button_cancel);
        Button deleteButton = view.findViewById(R.id.button_delete);
        TextView titleTextView = view.findViewById(R.id.text_title);

        if (viewModel.getTag() == null) {
            //Add new tag:
            deleteButton.setVisibility(View.GONE);
            titleTextView.setText(getText(R.string.tag_dialog_title_add));
        }
        else {
            //Edit tag:
            titleTextView.setText(getText(R.string.tag_dialog_title_edit));
            nameEditText.setText(viewModel.getTag().getName());
        }

        saveButton.setOnClickListener(v -> {
            boolean addNewTag = viewModel.getTag() == null;
            if (validateUserInput()) {
                viewModel.saveTag(addNewTag);
                if (callback != null) {
                    callback.onCallback(this, Callback.RESULT_SUCCESS);
                }
                dismiss();
            }
        });
        deleteButton.setOnClickListener(v -> {
            viewModel.deleteTag();
            if (callback != null) {
                callback.onCallback(this, Callback.RESULT_SUCCESS);
            }
            dismiss();
        });
        cancelButton.setOnClickListener(v -> {
            if (callback != null) {
                callback.onCallback(this, Callback.RESULT_CANCEL);
            }
            dismiss();
        });

        return view;
    }


    /**
     * Method validates the user inputs. If all inputs are valid, {@code true} is returned. Otherwise,
     * {@code false} is returned and the UI is updated to inform the user about invalid inputs. If
     * the method returns {@code true}, the tag within the view model will be set to a valid value.
     *
     * @return  Whether all userdata is valid.
     */
    private boolean validateUserInput() {
        if (nameEditText.getText() != null && !nameEditText.getText().toString().isEmpty()) {
            if (viewModel.getTag() == null) {
                viewModel.setTag(new Tag(nameEditText.getText().toString()));
            }
            else {
                viewModel.getTag().setName(nameEditText.getText().toString());
            }
            return true;
        }
        else {
            nameContainer.setError(getString(R.string.error_empty_input));
            return false;
        }
    }

}
