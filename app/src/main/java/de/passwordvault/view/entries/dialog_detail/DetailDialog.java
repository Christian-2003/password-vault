package de.passwordvault.view.entries.dialog_detail;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
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

    private class NameTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            viewModel.setNameEnteredAutomatically(false);
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }


    private class TypeTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            Log.d("DetailDialog", "Type changed to " + charSequence);
            int index = viewModel.getTypeIndexFromName(charSequence.toString());
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

    private class ObfuscatedCheckedChangeListener implements CompoundButton.OnCheckedChangeListener {

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


    private NameTextWatcher nameTextWatcher;

    private TypeTextWatcher typeTextWatcher;

    private ObfuscatedCheckedChangeListener obfuscatedCheckedChangeListener;

    private TextInputEditText nameEditText;

    private TextInputEditText contentEditText;

    private AutoCompleteTextView typeSelector;

    private TextInputLayout nameContainer;

    private TextInputLayout contentContainer;

    private CheckboxUiView obfuscateCheckbox;

    private CheckboxUiView visibleCheckbox;

    private CheckboxUiView usernameCheckbox;

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

        Log.d("DetailDialog", "Setup complete");
        return view;
    }


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

}
