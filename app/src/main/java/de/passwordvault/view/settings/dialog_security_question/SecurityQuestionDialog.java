package de.passwordvault.view.settings.dialog_security_question;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.Objects;
import de.passwordvault.R;
import de.passwordvault.model.security.login.SecurityQuestion;
import de.passwordvault.view.utils.components.PasswordVaultBottomSheetDialog;


/**
 * Class implements a dialog through which the user can configure a security question.
 *
 * @author  Christian-2003
 * @version 3.7.0
 */
public class SecurityQuestionDialog extends PasswordVaultBottomSheetDialog<SecurityQuestionViewModel> {

    /**
     * Field stores the key with which to pass a security question as argument.
     */
    public static final String ARG_QUESTION = "arg_question";

    /**
     * Field stores the key with which to pass a list of available questions as argument.
     */
    public static final String ARG_QUESTIONS = "arg_questions";


    /**
     * Attribute stores the container displaying the question.
     */
    private TextInputLayout questionContainer;

    /**
     * Attribute stores the container displaying the answer.
     */
    private TextInputLayout answerContainer;

    /**
     * Attribute stores the edit text in which the answer is entered.
     */
    private TextInputEditText answerEditText;


    /**
     * Constructor instantiates a new dialog.
     */
    public SecurityQuestionDialog() {
        super(SecurityQuestionViewModel.class, R.layout.dialog_security_question);
    }


    public SecurityQuestion getQuestion() {
        return viewModel.getQuestion();
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

        Bundle args = getArguments();
        if (args != null) {
            viewModel.processArguments(args);
        }

        if (view != null) {
            AutoCompleteTextView questionDropdown = view.findViewById(R.id.input_question);
            if (viewModel.getSelectedQuestionIndex() != -1) {
                //A question was manually selected by the user:
                questionDropdown.setText(viewModel.getAvailableQuestions()[viewModel.getSelectedQuestionIndex()]);
            }
            else if (viewModel.getSelectedQuestionIndex() == -1 && viewModel.getQuestion().getQuestion() != SecurityQuestion.NO_QUESTION) {
                //The user is editing an existing question and has not changed the question itself:
                questionDropdown.setText(viewModel.getAllQuestions()[viewModel.getQuestion().getQuestion()]);
            }
            questionDropdown.setAdapter(new ArrayAdapter<>(view.getContext(), R.layout.list_item_dropdown, R.id.text_view, viewModel.getAvailableQuestions()));
            questionDropdown.setOnItemClickListener((parent, v, position, id) -> viewModel.setSelectedQuestionIndex(position));

            answerEditText = view.findViewById(R.id.input_answer);
            answerEditText.setText(viewModel.getQuestion().getAnswer());

            view.findViewById(R.id.button_save).setOnClickListener(v -> {
                if (!processUserInput(view)) {
                    //Some necessary data was not entered:
                    return;
                }
                if (callback != null) {
                    callback.onCallback(this, Callback.RESULT_SUCCESS);
                }
                dismiss();
            });

            view.findViewById(R.id.button_cancel).setOnClickListener(v -> dismiss());
        }

        return view;
    }


    /**
     * Method processes the user input for the dialog. If all inputs are valid, the method returns
     * {@code true}. Otherwise {@code false} is returned and the UI is changed to inform the user
     * about invalid inputs.
     *
     * @param view  View of the dialog.
     * @return      Whether all inputs are valid.
     */
    private boolean processUserInput(View view) {
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
            viewModel.getQuestion().setQuestion(selectedQuestionIndex);
            questionContainer.setErrorEnabled(false);
        }
        else {
            questionContainer.setError(getString(R.string.error_empty_input));
            questionEnteredCorrectly = false;
        }

        String answer = Objects.requireNonNull(answerEditText.getText()).toString();
        boolean answerEnteredCorrectly = true;
        if (!answer.isEmpty()) {
            viewModel.getQuestion().setAnswer(answer);
            answerContainer.setErrorEnabled(false);
        }
        else {
            answerContainer.setError(getString(R.string.error_empty_input));
            answerEnteredCorrectly = false;
        }

        return questionEnteredCorrectly && answerEnteredCorrectly;
    }

}
