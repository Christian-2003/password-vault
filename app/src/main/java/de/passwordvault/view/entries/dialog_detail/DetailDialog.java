package de.passwordvault.view.entries.dialog_detail;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import de.passwordvault.R;
import de.passwordvault.model.detail.DetailType;
import de.passwordvault.ui.checkbox.CheckboxUiView;
import de.passwordvault.view.utils.components.PasswordVaultBottomSheetDialog;
import de.passwordvault.model.detail.Detail;


/**
 * Class implements the dialog through which a {@link Detail} can be edited / added.
 *
 * @author  Christian-2003
 * @version 3.7.0
 */
public class DetailDialog extends PasswordVaultBottomSheetDialog<DetailViewModel> {

    /**
     * Class implements a text watcher for the {@link #nameEditText}.
     */
    private class NameTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        /**
         * Method informs the view model that the text within {@link #nameEditText} has changed.
         *
         * @param s         Text of the edit text.
         * @param start     Index at which the edited text starts.
         * @param before    Length before the text changed.
         * @param count     Number of characters that are being changed.
         */
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            viewModel.setNameEnteredAutomatically(false);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }

    }

    /**
     * Class implements a text watcher for the {@link #typeSelector}.
     */
    private class TypeTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }


        /**
         * Method informs the view model that the text within {@link #typeSelector} has changed.
         * This changes the text of {@link #nameEditText} and the state of {@link #obfuscateCheckbox}
         * if their content was not changed manually by the user. Both changes are made without
         * triggering the {@link #nameTextWatcher} and {@link #obfuscatedCheckedChangeListener}.
         *
         * @param s         Text of the edit text.
         * @param start     Index at which the edited text starts.
         * @param before    Length before the text changed.
         * @param count     Number of characters that are being changed.
         */
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            int index = viewModel.getTypeIndexFromName(s.toString());
            if (index == -1) {
                return;
            }
            if (viewModel.getDetail() == null) {
                return;
            }
            viewModel.getDetail().setType(viewModel.getTypes()[index]);
            if (viewModel.isNameEnteredAutomatically()) {
                nameEditText.removeTextChangedListener(nameTextWatcher);
                if (viewModel.getDetail().getType().getNamePlaceholder() == null) {
                    nameEditText.setText("");
                }
                else {
                    nameEditText.setText(viewModel.getDetail().getType().getNamePlaceholder());
                }
                nameEditText.addTextChangedListener(nameTextWatcher);
            }
            if (viewModel.isObfuscatedEnteredAutomatically()) {
                obfuscateCheckbox.setCheckedChangeListener(null);
                obfuscateCheckbox.setChecked(viewModel.getDetail().getType().shouldObfuscate());
                obfuscateCheckbox.setCheckedChangeListener(obfuscatedCheckedChangeListener);
                changeObfuscateContentState(viewModel.getDetail().getType().shouldObfuscate());
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }

    }

    /**
     * Class implements a checked change listener for the {@link #obfuscateCheckbox}.
     */
    private class ObfuscatedCheckedChangeListener implements CompoundButton.OnCheckedChangeListener {

        /**
         * Method is called whenever the {@link #obfuscateCheckbox} is clicked and informs the view
         * model that it's state has changed.
         *
         * @param checkbox  Checkbox whose state changed.
         * @param checked   Whether the checkbox is checked or not.
         */
        @Override
        public void onCheckedChanged(CompoundButton checkbox, boolean checked) {
            viewModel.setObfuscatedEnteredAutomatically(false);
            changeObfuscateContentState(checked);
            if (viewModel.getDetail() != null) {
                viewModel.getDetail().setObfuscated(checked);
            }
        }

    }


    /**
     * Field stores the key with which to pass the detail as argument.
     */
    public static final String ARG_DETAIL = "arg_detail";


    /**
     * Attribute stores the text watcher triggered whenever the name entered changes.
     */
    private NameTextWatcher nameTextWatcher;

    /**
     * Attribute stores the text watcher triggered whenever the type selected changes.
     */
    private TypeTextWatcher typeTextWatcher;

    /**
     * Attribute stores the checked change listener triggered whenever the state of the obfuscated
     * checkbox changes.
     */
    private ObfuscatedCheckedChangeListener obfuscatedCheckedChangeListener;

    /**
     * Attribute stores the edit text used to enter the name for the detail.
     */
    private TextInputEditText nameEditText;

    /**
     * Attribute stores the edit text used to enter the content for the detail.
     */
    private TextInputEditText contentEditText;

    /**
     * Attribute stores the selector used to select the type of the detail.
     */
    private AutoCompleteTextView typeSelector;

    /**
     * Attribute stores the container for the {@link #nameEditText}.
     */
    private TextInputLayout nameContainer;

    /**
     * Attribute stores the container for the {@link #contentEditText}.
     */
    private TextInputLayout contentContainer;

    /**
     * Attribute stores the checkbox through which to obfuscate the detail.
     */
    private CheckboxUiView obfuscateCheckbox;

    /**
     * Attribute store the checkbox through which to change whether the detail is visible.
     */
    private CheckboxUiView visibleCheckbox;

    /**
     * Attribute stores the checkbox through which to change whether to use the detail as username
     * for autofill.
     */
    private CheckboxUiView usernameCheckbox;

    /**
     * Attribute stores the checkbox through which to change whether to use the detail as password
     * for autofill.
     */
    private CheckboxUiView passwordCheckbox;



    /**
     * Constructor instantiates a new dialog.
     */
    public DetailDialog() {
        super(DetailViewModel.class, R.layout.dialog_detail);

        nameTextWatcher = new NameTextWatcher();
        typeTextWatcher = new TypeTextWatcher();
        obfuscatedCheckedChangeListener = new ObfuscatedCheckedChangeListener();
    }


    /**
     * Method is called whenever the view for the dialog is created.
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

        viewModel.processArguments(getArguments());

        nameEditText = view.findViewById(R.id.input_name);
        contentEditText = view.findViewById(R.id.input_content);
        typeSelector = view.findViewById(R.id.input_type);
        nameContainer = view.findViewById(R.id.container_name);
        contentContainer = view.findViewById(R.id.container_content);
        obfuscateCheckbox = view.findViewById(R.id.checkbox_obfuscate);
        visibleCheckbox = view.findViewById(R.id.checkbox_visible);
        usernameCheckbox = view.findViewById(R.id.checkbox_username);
        passwordCheckbox = view.findViewById(R.id.checkbox_password);

        nameEditText.removeTextChangedListener(nameTextWatcher);
        typeSelector.removeTextChangedListener(typeTextWatcher);
        obfuscateCheckbox.setCheckedChangeListener(null);

        if (viewModel.isEditingDetail()) {
            //Edit detail:
            ((TextView)view.findViewById(R.id.text_title)).setText(getString(R.string.detail_dialog_title_edit));
        }
        else {
            //Add detail:
            ((TextView)view.findViewById(R.id.text_title)).setText(getString(R.string.detail_dialog_title_add));
        }

        if (viewModel.getDetail() != null) {
            nameEditText.setText(viewModel.getDetail().getName());
            contentEditText.setText(viewModel.getDetail().getContent());
            changeObfuscateContentState(viewModel.getDetail().isObfuscated());
            if (viewModel.getDetail().getType() != DetailType.UNDEFINED) {
                typeSelector.setText(viewModel.getTypeNames()[viewModel.getDetail().getType().ordinal()]);
            }
            obfuscateCheckbox.setChecked(viewModel.getDetail().isObfuscated());
            obfuscateCheckbox.setCheckedChangeListener(obfuscatedCheckedChangeListener);
            visibleCheckbox.setChecked(viewModel.getDetail().isVisible());
            visibleCheckbox.setCheckedChangeListener((button, checked) -> {
                viewModel.getDetail().setVisible(checked);
            });
            usernameCheckbox.setChecked(viewModel.getDetail().isUsername());
            usernameCheckbox.setCheckedChangeListener((button, checked) -> {
                viewModel.getDetail().setUsername(checked);
            });
            passwordCheckbox.setChecked(viewModel.getDetail().isPassword());
            passwordCheckbox.setCheckedChangeListener((button, checked) -> {
                viewModel.getDetail().setPassword(checked);
            });
            typeSelector.setAdapter(new ArrayAdapter<>(view.getContext(), R.layout.list_item_dropdown, R.id.text_view, viewModel.getTypeNames()));
        }

        nameEditText.addTextChangedListener(nameTextWatcher);
        typeSelector.addTextChangedListener(typeTextWatcher);

        view.findViewById(R.id.button_cancel).setOnClickListener(v -> {
            if (callback != null) {
                callback.onCallback(this, Callback.RESULT_CANCEL);
            }
            dismiss();
        });

        view.findViewById(R.id.button_save).setOnClickListener(v -> {
            if (validateUserInput()) {
                if (callback != null) {
                    callback.onCallback(this, Callback.RESULT_SUCCESS);
                }
                dismiss();
            }
        });

        return view;
    }


    /**
     * Method changes the input type for the {@link #contentEditText}.
     *
     * @param obfuscate Whether to obfuscate the content of the edit text.
     */
    private void changeObfuscateContentState(boolean obfuscate) {
        if (obfuscate) {
            contentEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            contentContainer.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
            contentEditText.setTransformationMethod(new PasswordTransformationMethod());
        }
        else {
            contentEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            contentContainer.setEndIconMode(TextInputLayout.END_ICON_NONE);
            contentEditText.setTransformationMethod(null);
        }
    }


    /**
     * Method copies all user input to the detail within the view model and changes the UI to inform
     * the user about invalid inputs.
     *
     * @return  Whether all input is valid or not.
     */
    private boolean validateUserInput() {
        Detail detail = viewModel.getDetail();
        if (detail == null) {
            return false;
        }
        detail.setName(nameEditText.getText() != null ? nameEditText.getText().toString() : "");
        detail.setContent(contentEditText.getText() != null ? contentEditText.getText().toString() : "");
        detail.setObfuscated(obfuscateCheckbox.isChecked());
        detail.setVisible(visibleCheckbox.isChecked());
        detail.setUsername(usernameCheckbox.isChecked());
        detail.setPassword(passwordCheckbox.isChecked());
        boolean allInputsValid = true;
        if (detail.getName().isEmpty()) {
            allInputsValid = false;
            nameContainer.setError(getString(R.string.error_empty_input));
        }
        else {
            nameContainer.setErrorEnabled(false);
        }
        if (detail.getContent().isEmpty()) {
            allInputsValid = false;
            contentContainer.setError(getString(R.string.error_empty_input));
        }
        else {
            contentContainer.setErrorEnabled(false);
        }

        return allInputsValid;
    }

}
