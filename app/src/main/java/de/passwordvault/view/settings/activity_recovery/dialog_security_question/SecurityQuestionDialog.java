package de.passwordvault.view.settings.activity_recovery.dialog_security_question;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
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
import de.passwordvault.model.security.login.SecurityQuestion;
import de.passwordvault.view.settings.activity_recovery.dialog_security_question.SecurityQuestionViewModel;


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


    /**
     * Method returns the security question that was edited by the dialog.
     *
     * @return  Security question that was edited.
     */
    public SecurityQuestion getSecurityQuestion() {
        return viewModel.getSecurityQuestion();
    }


    /**
     * Method is called whenever the dialog is created.
     *
     * @param savedInstanceState    Previously saved state of the instance.
     * @return                      Created dialog.
     */
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


    /**
     * Method is called whenever the dialog is cancelled.
     *
     * @param dialog    Cancelled dialog.
     */
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
        AutoCompleteTextView questionDropdown = view.findViewById(R.id.input_question);
        if (viewModel.getSelectedQuestionIndex() != -1) {
            //A question was manually selected by the user:
            questionDropdown.setText(viewModel.getAvailableQuestions()[viewModel.getSelectedQuestionIndex()]);
        }
        else if (viewModel.getSelectedQuestionIndex() == -1 && viewModel.getSecurityQuestion().getQuestion() != SecurityQuestion.NO_QUESTION) {
            //The user is editing an existing question and has not changed the question itself:
            questionDropdown.setText(viewModel.getAllQuestions()[viewModel.getSecurityQuestion().getQuestion()]);
        }
        questionDropdown.setAdapter(new ArrayAdapter<>(view.getContext(), R.layout.list_item_dropdown, R.id.text_view, viewModel.getAvailableQuestions()));
        questionDropdown.setOnItemClickListener((parent, view, position, id) -> viewModel.setSelectedQuestionIndex(position));

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
        if (questionContainer == null) {
            questionContainer = view.findViewById(R.id.container_question);
        }
        if (answerContainer == null) {
            answerContainer = view.findViewById(R.id.container_answer);
        }

        int selectedQuestionIndex = viewModel.getIndexFromSelectedQuestion(viewModel.getSelectedQuestionIndex());
        boolean questionEnteredCorrectly = true;
        if (selectedQuestionIndex != SecurityQuestion.NO_QUESTION) {
            //Question selected:
            viewModel.getSecurityQuestion().setQuestion(selectedQuestionIndex);
            questionContainer.setErrorEnabled(false);
        }
        else {
            questionContainer.setError(getString(R.string.error_empty_input));
            questionEnteredCorrectly = false;
        }

        String answer = Objects.requireNonNull(answerEditText.getText()).toString();
        boolean answerEnteredCorrectly = true;
        if (!answer.isEmpty()) {
            viewModel.getSecurityQuestion().setAnswer(answer);
            answerContainer.setErrorEnabled(false);
        }
        else {
            answerContainer.setError(getString(R.string.error_empty_input));
            answerEnteredCorrectly = false;
        }

        return questionEnteredCorrectly && answerEnteredCorrectly;
    }

}
