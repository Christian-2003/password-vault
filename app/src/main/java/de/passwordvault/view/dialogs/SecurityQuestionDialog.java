package de.passwordvault.view.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.Objects;
import de.passwordvault.R;
import de.passwordvault.viewmodel.dialogs.SecurityQuestionViewModel;


/**
 * Class implements a dialog through which the user can configure a security question.
 *
 * @author  Christian-2003
 * @version 3.6.0
 */
public class SecurityQuestionDialog extends DialogFragment {

    /**
     * Field stores the key with which to pass the callback listener as argument.
     */
    public static String KEY_CALLBACK_LISTENER = "callback_listener";

    /**
     * Field stores the key with which to pass the security question to edit as argument.
     */
    public static String KEY_QUESTION = "question";

    /**
     * Field stores the key with which to pass an array of all available security questions as
     * argument.
     * With this argument, pass an array of the ordinals of all available questions.
     */
    public static String KEY_QUESTIONS = "questions";


    /**
     * Attribute stores the view model for the dialog.
     */
    private SecurityQuestionViewModel viewModel;

    /**
     * Attribute stores the view for the dialog.
     */
    private View view;

    /**
     * Attribute stores the text view with the dropdown menu to select a question.
     */
    private AutoCompleteTextView questionDropdown;

    /**
     * Attribute stores the edit text to enter an answer.
     */
    private TextInputEditText answerEditText;

    /**
     * Attribute stores the text input layout which contains the dropdown to select a question.
     */
    private TextInputLayout questionContainer;

    /**
     * Attribute stores the text input layout which contains the edit text to enter the answer.
     */
    private TextInputLayout answerContainer;



    @NonNull
    @Override
    @SuppressLint("InflateParams") //Ignore passing 'null' as root for the dialog's view.
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(SecurityQuestionViewModel.class);

        if (viewModel.getCallbackListener() == null) {
            if (!viewModel.processArguments(getArguments())) {
                //Could not process arguments:
                throw new ClassCastException("Cannot process arguments");
            }
        }

        view = requireActivity().getLayoutInflater().inflate(R.layout.dialog_security_question, null, false);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
        builder.setTitle(getString(R.string.recovery_question));
        builder.setView(createView());

        builder.setPositiveButton(R.string.button_save, (dialog, id) -> {
            //Action implemented in onStart()-method!
        });
        builder.setNegativeButton(R.string.button_cancel, (dialog, id) -> {
            //Action implemented in onStart()-method!
        });

        return builder.create();
    }


    /**
     * Method configures the click listeners of the dialog buttons whenever the dialog is started.
     */
    @Override
    public void onStart() {
        super.onStart();
        AlertDialog dialog = (AlertDialog)getDialog();
        if (dialog == null) {
            //No dialog available:
            return;
        }

        //Configure positive button:
        Button positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(view -> {
            if (!processUserInput()) {
                //Some necessary data was not entered:
                return;
            }
            viewModel.getCallbackListener().onPositiveCallback(this);
            dismiss();
        });

        //Configure negative button:
        Button negativeButton = dialog.getButton(Dialog.BUTTON_NEGATIVE);
        negativeButton.setOnClickListener(view -> dismiss());
    }


    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        viewModel.getCallbackListener().onNegativeCallback(this);
    }

    /**
     * Method populates the view for the dialog.
     *
     * @return  View for the dialog.
     */
    private View createView() {
        questionDropdown = view.findViewById(R.id.input_question);
        String[] questionLabels = new String[viewModel.getAvailableQuestions().length];
        if (viewModel.getSelectedQuestionIndex() != -1) {
            questionDropdown.setText(questionLabels[viewModel.getSelectedQuestionIndex()]);
        }
        else if (viewModel.getSecurityQuestion() != null && viewModel.getSecurityQuestion().getQuestion() != null){
            questionDropdown.setText(viewModel.getSecurityQuestion().getQuestion().getQuestion());
        }
        for (int i = 0; i < questionLabels.length; i++) {
            questionLabels[i] = viewModel.getAvailableQuestions()[i].getQuestion();
        }
        questionDropdown.setAdapter(new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_dropdown_item, questionLabels));
        questionDropdown.setOnItemClickListener((parent, view, position, id) -> {
            viewModel.setSelectedQuestionIndex(position);
        });

        answerEditText = view.findViewById(R.id.input_answer);
        answerEditText.setText(viewModel.getSecurityQuestion().getAnswer());

        return view;
    }


    /**
     * Method processes the user input for the dialog. If all inputs are valid, the method returns
     * {@code true}. Otherwise {@code false} is returned and the UI is changed to inform the user
     * about invalid inputs.
     *
     * @return  Whether all inputs are valid.
     */
    private boolean processUserInput() {
        int selectedItem = viewModel.getSelectedQuestionIndex();
        boolean questionEnteredCorrectly = true;
        if (selectedItem >= 0 && selectedItem < viewModel.getAvailableQuestions().length) {
            //Question selected:
            viewModel.getSecurityQuestion().setQuestion(viewModel.getAvailableQuestions()[selectedItem]);
        }
        else if (viewModel.getSecurityQuestion().getQuestion() == null || !questionDropdown.getText().toString().equals(viewModel.getSecurityQuestion().getQuestion().getQuestion())) {
            questionEnteredCorrectly = false;
        }
        else {
            questionEnteredCorrectly = false;
        }
        if (questionContainer == null) {
            questionContainer = view.findViewById(R.id.container_question);
        }
        if (!questionEnteredCorrectly) {
            questionContainer.setError(getString(R.string.error_empty_input));
        }
        else {
            questionContainer.setErrorEnabled(false);
        }

        String answer = Objects.requireNonNull(answerEditText.getText()).toString();
        boolean answerEnteredCorrectly = true;
        if (answerContainer == null) {
            answerContainer = view.findViewById(R.id.container_answer);
        }
        if (answer.isEmpty()) {
            answerEnteredCorrectly = false;
            answerContainer.setError(getString(R.string.error_empty_input));
        }
        else {
            answerContainer.setErrorEnabled(false);
        }

        Log.d("SQD", "Result=" + (questionEnteredCorrectly && answerEnteredCorrectly));
        return questionEnteredCorrectly && answerEnteredCorrectly;
    }

}
