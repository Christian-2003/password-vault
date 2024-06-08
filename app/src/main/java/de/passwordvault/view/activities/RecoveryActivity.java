package de.passwordvault.view.activities;

import android.animation.LayoutTransition;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import de.passwordvault.R;
import de.passwordvault.model.security.login.Account;
import de.passwordvault.model.security.login.SecurityQuestion;
import de.passwordvault.model.security.login.SecurityQuestions;
import de.passwordvault.view.dialogs.SecurityQuestionDialog;
import de.passwordvault.view.utils.DialogCallbackListener;
import de.passwordvault.view.utils.OnRecyclerItemClickListener;
import de.passwordvault.view.utils.adapters.SecurityQuestionsRecyclerViewAdapter;
import de.passwordvault.view.utils.components.PasswordVaultBaseActivity;
import de.passwordvault.viewmodel.activities.RecoveryViewModel;


/**
 * Class implements the activity that is used to configure the master password recovery.
 *
 * @author  Christian-2003
 * @version 3.6.0
 */
public class RecoveryActivity extends PasswordVaultBaseActivity implements OnRecyclerItemClickListener<SecurityQuestion>, DialogCallbackListener {

    /**
     * Attribute stores the view model of the activity.
     */
    private RecoveryViewModel viewModel;

    /**
     * Attribute stores the adapter for the recycler view.
     */
    private SecurityQuestionsRecyclerViewAdapter adapter;


    /**
     * Attribute stores the linear layout which contains the security questions.
     */
    private LinearLayout securityQuestionsContainer;


    /**
     * Method is called when the item which is passed as argument is clicked by the user.
     *
     * @param item      Clicked item.
     * @param position  Index of the clicked item.
     */
    @Override
    public void onItemClick(SecurityQuestion item, int position) {
        LayoutTransition layoutTransition = securityQuestionsContainer.getLayoutTransition();
        layoutTransition.enableTransitionType(LayoutTransition.CHANGING);
    }


    /**
     * Method is called whenever the activity closes.
     */
    @Override
    public void finish() {
        super.finish();
        Account.getInstance().setSecurityQuestions(viewModel.getSecurityQuestions());
        Account.getInstance().save();
    }


    /**
     * Method is called whenever the {@linkplain DialogFragment} is closed through the
     * 'positive' button (i.e. the SAVE button).
     *
     * @param dialog    Dialog which called the method.
     */
    @Override
    public void onPositiveCallback(DialogFragment dialog) {

    }


    /**
     * Method is called whenever the {@linkplain DialogFragment} is closed through the
     * 'negative' button (i.e. the CANCEL button).
     *
     * @param dialog    Dialog which called the method.
     */
    @Override
    public void onNegativeCallback(DialogFragment dialog) {

    }


    /**
     * Method is called whenever the activity is created.
     *
     * @param savedInstanceState    Previously saved state of the instance.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(RecoveryViewModel.class);
        setContentView(R.layout.activity_recovery);

        //Back button:
        findViewById(R.id.button_back).setOnClickListener(view -> finish());

        //Button to add new question:
        findViewById(R.id.button_add_question).setOnClickListener(view -> addNewSecurityQuestion());

        securityQuestionsContainer = findViewById(R.id.container_security_questions);

        if (viewModel.getSecurityQuestions() == null) {
            viewModel.loadSecurityQuestions();
            if (viewModel.getSecurityQuestions().isEmpty()) {
                SecurityQuestion q1 = new SecurityQuestion(SecurityQuestions.QUESTION_1, "Hello");
                SecurityQuestion q2 = new SecurityQuestion(SecurityQuestions.QUESTION_2, "World");
                SecurityQuestion q3 = new SecurityQuestion(SecurityQuestions.QUESTION_3, "Cool");
                SecurityQuestion q4 = new SecurityQuestion(SecurityQuestions.QUESTION_4, "Month");
                viewModel.getSecurityQuestions().add(q1);
                viewModel.getSecurityQuestions().add(q2);
                viewModel.getSecurityQuestions().add(q3);
                viewModel.getSecurityQuestions().add(q4);
            }
        }

        adapter = new SecurityQuestionsRecyclerViewAdapter(viewModel.getSecurityQuestions(), this);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setAdapter(adapter);
    }


    private void addNewSecurityQuestion() {
        if (viewModel.getSecurityQuestions().size() >= SecurityQuestions.values().length) {
            //No more questions available:
            return;
        }

        //Get available security questions that can be used:
        int[] ordinals = new int[SecurityQuestions.values().length - viewModel.getSecurityQuestions().size()];
        int j = 0;
        for (int i = 0; i < ordinals.length; i++) {
            SecurityQuestions question = SecurityQuestions.values()[j];
            boolean questionUsed = false;
            for (SecurityQuestion usedQuestion : viewModel.getSecurityQuestions()) {
                if (usedQuestion.getQuestion() != null && usedQuestion.getQuestion() == question) {
                    questionUsed = true;
                    break;
                }
            }
            if (!questionUsed) {
                ordinals[i] = question.ordinal();
            }
            else {
                i--;
            }
            j++;
        }

        SecurityQuestionDialog dialog = new SecurityQuestionDialog();
        Bundle args = new Bundle();
        args.putSerializable(SecurityQuestionDialog.KEY_CALLBACK_LISTENER, this);
        args.putIntArray(SecurityQuestionDialog.KEY_QUESTIONS, ordinals);
        dialog.setArguments(args);

        dialog.show(getSupportFragmentManager(), "");
    }

}
