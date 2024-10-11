package de.passwordvault.view.entries.activity_add_entry.dialog_detail;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import androidx.lifecycle.ViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.Objects;
import de.passwordvault.R;
import de.passwordvault.model.detail.Detail;
import de.passwordvault.model.detail.DetailType;
import de.passwordvault.view.entries.activity_add_entry.dialog_detail.DetailDialog;
import de.passwordvault.view.utils.DialogArgumentException;
import de.passwordvault.view.utils.DialogCallbackListener;


/**
 * Class implements the {@linkplain ViewModel} for the {@link DetailDialog}-class.
 *
 * @author  Christian-2003
 * @version 3.5.0
 */
public class DetailViewModel extends ViewModel {

    /**
     * Attribute stores the {@link DialogCallbackListener} which waits for the
     * {@link DetailDialog} to be closed.
     */
    private DialogCallbackListener callbackListener;

    /**
     * Attribute stores the {@link Detail} which shall be edited / created.
     */
    private Detail detail;

    /**
     * Attribute stores whether the name of the detail was entered automatically (= {@code true}) or
     * by the user (= {@code false}).
     */
    private boolean nameEnteredAutomatically;

    /**
     * Attribute stores whether the checkbox to obfuscate the content was set automatically
     * (= {@code true}) or by the user (= {@code false}).
     */
    private boolean obfuscatedEnteredAutomatically;


    /**
     * Method returns the {@link #detail} that is being edited / created by the dialog.
     *
     * @return  Detail of he dialog.
     */
    public Detail getDetail() {
        return detail;
    }

    /**
     * Method changes the {@link #detail} which is being edited / created by the dialog.
     *
     * @param detail                New detail for the dialog.
     * @throws NullPointerException The passed detail is {@code null}.
     */
    public void setDetail(Detail detail) throws NullPointerException {
        if (detail == null) {
            throw new NullPointerException("Null is invalid detail");
        }
        this.detail = detail;
    }

    /**
     * Method returns the {@link #callbackListener} of the dialog.
     *
     * @return  Callback listener of the dialog.
     */
    public DialogCallbackListener getCallbackListener() {
        return callbackListener;
    }

    /**
     * Method changes the {@link #callbackListener} of the dialog.
     *
     * @param callbackListener      New callback listener for the dialog.
     * @throws NullPointerException The passed callback listener is {@code null}.
     */
    public void setCallbackListener(DialogCallbackListener callbackListener) throws NullPointerException {
        if (callbackListener == null) {
            throw new NullPointerException("Null is invalid CallbackListener");
        }
        this.callbackListener = callbackListener;
    }


    /**
     * Method creates the {@linkplain View} for the {@link DetailDialog}.
     *
     * @param view  Inflated view which shall be created.
     * @return      Created view.
     */
    public View createView(View view) {
        if (view == null) {
            return null;
        }

        AutoCompleteTextView detailTypeTextView = view.findViewById(R.id.input_type);
        if (detail == null) {
            //Dialog to create new detail:
            detailTypeTextView.setText(DetailType.TEXT.getDisplayName());
            detail = new Detail();
            nameEnteredAutomatically = true;
            obfuscatedEnteredAutomatically = true;
        }
        else {
            nameEnteredAutomatically = false;
            obfuscatedEnteredAutomatically = false;
        }

        TextInputEditText nameEditText = view.findViewById(R.id.input_name);
        nameEditText.setText(detail.getName());
        TextInputEditText contentEditText = view.findViewById(R.id.input_content);
        contentEditText.setText(detail.getContent());
        TextInputLayout contentLayout = view.findViewById(R.id.container_content);
        CheckBox obfuscatedCheckBox = view.findViewById(R.id.checkbox_obfuscate);
        obfuscatedCheckBox.setChecked(detail.isObfuscated());
        CheckBox visibleCheckBox = view.findViewById(R.id.checkbox_visible);
        visibleCheckBox.setChecked(detail.isVisible());
        if (detail.getType() != DetailType.UNDEFINED) {
            detailTypeTextView.setText(Detail.getTypes()[detail.getType().ordinal()]);
        }
        if (detail.isObfuscated()) {
            obfuscateContentEditText(true, view);
        }
        detailTypeTextView.setAdapter(new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_dropdown_item, Detail.getTypes()));

        CheckBox usernameCheckbox = view.findViewById(R.id.checkbox_username);
        usernameCheckbox.setChecked(detail.isUsername());
        CheckBox passwordCheckbox = view.findViewById(R.id.checkbox_password);
        passwordCheckbox.setChecked(detail.isPassword());

        obfuscatedCheckBox.setOnClickListener(view1 -> obfuscatedEnteredAutomatically = false);

        //Make EditText for content obfuscated when CheckBox is checked:
        obfuscatedCheckBox.setOnCheckedChangeListener((compoundButton, b) -> obfuscateContentEditText(obfuscatedCheckBox.isChecked(), view));

        detailTypeTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                DetailType type = Detail.getTypeByName(charSequence.toString());
                String namePlaceholder = type.getNamePlaceholder();
                if (namePlaceholder != null && nameEnteredAutomatically) {
                    nameEditText.setText(namePlaceholder);
                }
                else if (nameEnteredAutomatically) {
                    nameEditText.setText("");
                }
                if (type.shouldObfuscate() && obfuscatedEnteredAutomatically) {
                    obfuscatedCheckBox.setChecked(true);
                }
                else if (obfuscatedEnteredAutomatically) {
                    obfuscatedCheckBox.setChecked(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        nameEditText.setOnFocusChangeListener((view12, hasFocus) -> {
            if (!hasFocus) {
                TextInputEditText editText = (TextInputEditText) view12;
                if (!Objects.requireNonNull(editText.getText()).toString().isEmpty() || !nameEnteredAutomatically) {
                    nameEnteredAutomatically = false;
                }
            }
        });

        return view;
    }


    /**
     * Method processes the arguments that were passed to the {@link DetailDialog}.
     *
     * @param args                      Passed arguments to be processed.
     * @throws NullPointerException     Some arguments are {@code null}.
     * @throws ClassCastException       The {@linkplain java.io.Serializable} cannot be casted to
     *                                  {@link DialogCallbackListener}.
     * @throws DialogArgumentException  Some arguments are missing.
     */
    public void processArguments(Bundle args) throws NullPointerException, ClassCastException, DialogArgumentException {
        if (args == null) {
            throw new NullPointerException("Null is invalid bundle");
        }

        //Process KEY_DETAIL:
        if (args.containsKey(DetailDialog.KEY_DETAIL)) {
            setDetail((Detail) args.getSerializable(DetailDialog.KEY_DETAIL));
        }
        //When no detail was passed, a new detail shall be created with this dialog.

        //Process KEY_CALLBACK_LISTENER:
        if (args.containsKey(DetailDialog.KEY_CALLBACK_LISTENER)) {
            try {
                setCallbackListener((DialogCallbackListener) args.getSerializable(DetailDialog.KEY_CALLBACK_LISTENER));
            }
            catch (ClassCastException e) {
                e.printStackTrace();
                throw e;
            }
        }
        else {
            throw new DialogArgumentException("Missing argument KEY_CALLBACK_LISTENER");
        }
    }


    /**
     * Method processes the user's input and updates the {@link #detail} accordingly. If some userdata
     * is incorrect or missing, the passed view will be updated to inform the user about the problem
     * and {@code false} will be returned. Otherwise, {@code true} will be returned.
     *
     * @param view  View from which to retrieve the data.
     * @return      Whether all data was successfully processed.
     */
    public boolean processUserInput(View view) {
        if (view == null) {
            return false;
        }

        //Retrieve required data:
        TextInputEditText nameEditText = view.findViewById(R.id.input_name);
        String name = Objects.requireNonNull(nameEditText.getText()).toString();
        TextInputEditText contentEditText = view.findViewById(R.id.input_content);
        String content = Objects.requireNonNull(contentEditText.getText()).toString();
        AutoCompleteTextView detailTypeTextView = view.findViewById(R.id.input_type);
        DetailType detailType = Detail.getTypeByName(detailTypeTextView.getText().toString());
        CheckBox obfuscatedCheckBox = view.findViewById(R.id.checkbox_obfuscate);
        boolean obfuscated = obfuscatedCheckBox.isChecked();
        CheckBox visibleCheckBox = view.findViewById(R.id.checkbox_visible);
        boolean visible = visibleCheckBox.isChecked();
        CheckBox usernameCheckbox = view.findViewById(R.id.checkbox_username);
        boolean username = usernameCheckbox.isChecked();
        CheckBox passwordCheckbox = view.findViewById(R.id.checkbox_password);
        boolean password = passwordCheckbox.isChecked();

        boolean inputCorrect = true;

        //Test whether name was entered correctly:
        TextInputLayout nameLayout = view.findViewById(R.id.container_name);
        if (name.isEmpty()) {
            nameLayout.setError(view.getResources().getString(R.string.error_empty_input));
            inputCorrect = false;
        }
        else {
            nameLayout.setErrorEnabled(false);
        }

        //Test whether content was entered correctly:
        TextInputLayout contentLayout = view.findViewById(R.id.container_content);
        if (content.isEmpty()) {
            contentLayout.setError(view.getResources().getString(R.string.error_empty_input));
            inputCorrect = false;
        }
        else {
            contentLayout.setErrorEnabled(false);
        }

        //Process the input:
        if (!inputCorrect) {
            return false;
        }

        //Test whether anything was changed at all:
        if (!detail.getName().equals(name) || !detail.getContent().equals(content) || detail.getType() != detailType || detail.isObfuscated() != obfuscated || detail.isVisible() != visible || detail.isUsername() != username || detail.isPassword() != password) {
            detail.notifyDataChange();
            detail.setName(name);
            detail.setContent(content);
            detail.setType(detailType);
            detail.setObfuscated(obfuscated);
            detail.setVisible(visible);
            detail.setUsername(username);
            detail.setPassword(password);
        }
        return true;
    }


    /**
     * Method changes whether the EditText which is used to enter the content of the detail should
     * be obfuscated.
     *
     * @param obfuscate Whether the content should be obfuscated.
     * @param view      Inflated dialog view.
     */
    private void obfuscateContentEditText(boolean obfuscate, View view) {
        TextInputEditText contentEditText = view.findViewById(R.id.input_content);
        TextInputLayout contentLayout = view.findViewById(R.id.container_content);
        if (obfuscate) {
            contentEditText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            contentLayout.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
            contentEditText.setTransformationMethod(new PasswordTransformationMethod());
        }
        else {
            contentEditText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            contentLayout.setEndIconMode(TextInputLayout.END_ICON_NONE);
            contentEditText.setTransformationMethod(null);
        }
    }

}
