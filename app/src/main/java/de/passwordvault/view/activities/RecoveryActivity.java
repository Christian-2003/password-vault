package de.passwordvault.view.activities;

import android.animation.LayoutTransition;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import de.passwordvault.R;
import de.passwordvault.model.security.login.Account;
import de.passwordvault.model.security.login.SecurityQuestion;
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
        if (dialog instanceof SecurityQuestionDialog) {
            SecurityQuestion question = ((SecurityQuestionDialog)dialog).getSecurityQuestion();
            if (question.getQuestion() == SecurityQuestion.NO_QUESTION || question.getAnswer() == null || question.getAnswer().isEmpty()) {
                //Question is invalid:
                return;
            }
            for (int i = 0; i < viewModel.getSecurityQuestions().size(); i++) {
                if (viewModel.getSecurityQuestions().get(i).equals(question)) {
                    viewModel.getSecurityQuestions().set(i, question);
                    adapter.notifyItemChanged(i);
                    return;
                }
            }
            viewModel.getSecurityQuestions().add(question);
            adapter.notifyItemInserted(viewModel.getSecurityQuestions().size() - 1);
        }
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
        }

        adapter = new SecurityQuestionsRecyclerViewAdapter(viewModel.getSecurityQuestions(), this);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setAdapter(adapter);
    }


    /**
     * Method starts a dialog to add a new security question.
     */
    private void addNewSecurityQuestion() {
        if (viewModel.getSecurityQuestions().size() >= SecurityQuestion.getAllQuestions().length) {
            //No more questions available:
            return;
        }

        //Get available security questions that can be used:
        int numberOfAllQuestions = SecurityQuestion.getAllQuestions().length;
        String[] availableQuestions = new String[numberOfAllQuestions - viewModel.getSecurityQuestions().size()];
        Log.d("RA", "Created " + (numberOfAllQuestions - viewModel.getSecurityQuestions().size()) + " security questions.");

        int k = 0;
        for (int i = 0; i < numberOfAllQuestions; i++) {
            boolean questionUsed = false;
            for (SecurityQuestion usedQuestion : viewModel.getSecurityQuestions()) {
                if (usedQuestion.getQuestion() == i) {
                    questionUsed = true;
                    break;
                }
            }
            if (questionUsed) {
                continue;
            }
            availableQuestions[k++] = SecurityQuestion.getAllQuestions()[i];
        }

        SecurityQuestionDialog dialog = new SecurityQuestionDialog();
        Bundle args = new Bundle();
        args.putSerializable(SecurityQuestionDialog.KEY_CALLBACK_LISTENER, this);
        args.putStringArray(SecurityQuestionDialog.KEY_QUESTIONS, availableQuestions);
        dialog.setArguments(args);

        dialog.show(getSupportFragmentManager(), "");
    }

}
