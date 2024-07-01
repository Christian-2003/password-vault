package de.passwordvault.view.activities;

import android.animation.LayoutTransition;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import de.passwordvault.R;
import de.passwordvault.model.detail.DetailSwipeAction;
import de.passwordvault.model.security.login.Account;
import de.passwordvault.model.security.login.SecurityQuestion;
import de.passwordvault.model.storage.settings.Config;
import de.passwordvault.view.dialogs.ConfirmDeleteDialog;
import de.passwordvault.view.dialogs.SecurityQuestionDialog;
import de.passwordvault.view.utils.DialogCallbackListener;
import de.passwordvault.view.utils.OnRecyclerItemClickListener;
import de.passwordvault.view.utils.RecyclerItemSwipeCallback;
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
     * Attribute stores the text view displaying the number of configured security questions.
     */
    private TextView scoreTextView;

    /**
     * Attribute stores the text view displaying an error message if less than five security
     * questions are configured.
     */
    private TextView scoreErrorTextView;

    /**
     * Attribute stores the progress bar that visually displays how many security questions are
     * configured.
     */
    private ProgressBar scoreProgressBar;


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
     * @param fragment  Dialog which called the method.
     */
    @Override
    public void onPositiveCallback(DialogFragment fragment) {
        if (fragment instanceof SecurityQuestionDialog) {
            SecurityQuestion question = ((SecurityQuestionDialog)fragment).getSecurityQuestion();
            if (question.getQuestion() == SecurityQuestion.NO_QUESTION || question.getAnswer() == null || question.getAnswer().isEmpty()) {
                //Question is invalid:
                return;
            }
            for (int i = 0; i < viewModel.getSecurityQuestions().size(); i++) {
                if (viewModel.getSecurityQuestions().get(i).getQuestion() == question.getQuestion()) {
                    viewModel.getSecurityQuestions().set(i, question);
                    adapter.notifyItemChanged(i);
                    return;
                }
            }
            viewModel.getSecurityQuestions().add(question);
            adapter.notifyItemInserted(viewModel.getSecurityQuestions().size() - 1);
            updateProgress();
        }
        else if (fragment instanceof ConfirmDeleteDialog) {
            ConfirmDeleteDialog dialog = (ConfirmDeleteDialog)fragment;
            int position = -1;
            if (dialog.getTag() != null) {
                try {
                    position = Integer.parseInt(dialog.getTag());
                }
                catch (NumberFormatException e) {
                    //Ignore...
                }
            }
            if (position != -1) {
                viewModel.getSecurityQuestions().remove(position);
                adapter.notifyItemRemoved(position);
                updateProgress();
            }
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

        initRecyclerView();
        updateProgress();
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

        dialog.show(getSupportFragmentManager(), "add");
    }


    /**
     * Method updates the progress for the configured security questions.
     */
    private void updateProgress() {
        if (scoreTextView == null) {
            scoreTextView = findViewById(R.id.text_view_score);
            scoreErrorTextView = findViewById(R.id.text_view_error);
            scoreProgressBar = findViewById(R.id.progress_bar_score);
        }
        int score = 0;
        if (viewModel.getSecurityQuestions() != null) {
            score = viewModel.getSecurityQuestions().size();
        }
        String securityQuestions = score + " / " + Account.REQUIRED_SECURITY_QUESTIONS;
        scoreTextView.setText(securityQuestions);
        scoreErrorTextView.setVisibility(score >= Account.REQUIRED_SECURITY_QUESTIONS ? View.GONE : View.VISIBLE);
        scoreProgressBar.setMax(Account.REQUIRED_SECURITY_QUESTIONS * 100);
        scoreProgressBar.setProgress(score * 100, true);
    }


    /**
     * Method is called whenever a security question shall be deleted.
     *
     * @param question  Security question to delete.
     * @param position  Position of the security question within the recycler view.
     */
    private void deleteSecurityQuestion(SecurityQuestion question, int position) {
        ConfirmDeleteDialog dialog = new ConfirmDeleteDialog();
        Bundle args = new Bundle();
        args.putSerializable(ConfirmDeleteDialog.KEY_CALLBACK_LISTENER, this);
        args.putString(ConfirmDeleteDialog.KEY_MESSAGE, getString(R.string.recovery_delete_message));
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), "" + position);
    }


    /**
     * Method is called whenever a security question shall be edited.
     *
     * @param question  Security question to edit.
     * @param position  Position of the security question within the recycler view.
     */
    private void editSecurityQuestion(SecurityQuestion question, int position) {
        //Get available security questions that can be used:
        int numberOfAllQuestions = SecurityQuestion.getAllQuestions().length;
        String[] availableQuestions = new String[numberOfAllQuestions - viewModel.getSecurityQuestions().size() + 1];

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
        availableQuestions[availableQuestions.length - 1] = SecurityQuestion.getAllQuestions()[question.getQuestion()];

        SecurityQuestionDialog dialog = new SecurityQuestionDialog();
        Bundle args = new Bundle();
        args.putSerializable(SecurityQuestionDialog.KEY_CALLBACK_LISTENER, this);
        args.putSerializable(SecurityQuestionDialog.KEY_QUESTION, question);
        args.putStringArray(SecurityQuestionDialog.KEY_QUESTIONS, availableQuestions);
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), "edit");
    }


    /**
     * Method instantiates the recycler view for the activity.
     */
    private void initRecyclerView() {
        adapter = new SecurityQuestionsRecyclerViewAdapter(viewModel.getSecurityQuestions(), this);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        RecyclerItemSwipeCallback.SwipeAction<SecurityQuestion> leftSwipeCallback = null;
        DetailSwipeAction leftSwipeAction = Config.getInstance().leftSwipeAction.get();
        if (leftSwipeAction == DetailSwipeAction.DELETE) {
            leftSwipeCallback = new RecyclerItemSwipeCallback.SwipeAction<>(R.drawable.ic_delete, R.color.pv_red, this::deleteSecurityQuestion);
        }
        else if (leftSwipeAction == DetailSwipeAction.EDIT) {
            leftSwipeCallback = new RecyclerItemSwipeCallback.SwipeAction<>(R.drawable.ic_edit, R.color.pv_primary, this::editSecurityQuestion);
        }

        RecyclerItemSwipeCallback.SwipeAction<SecurityQuestion> rightSwipeCallback = null;
        DetailSwipeAction rightSwipeAction = Config.getInstance().rightSwipeAction.get();
        if (rightSwipeAction == DetailSwipeAction.DELETE) {
            rightSwipeCallback = new RecyclerItemSwipeCallback.SwipeAction<>(R.drawable.ic_delete, R.color.pv_red, this::deleteSecurityQuestion);
        }
        else if (rightSwipeAction == DetailSwipeAction.EDIT) {
            rightSwipeCallback = new RecyclerItemSwipeCallback.SwipeAction<>(R.drawable.ic_edit, R.color.pv_primary, this::editSecurityQuestion);
        }

        RecyclerItemSwipeCallback<SecurityQuestion> callback = new RecyclerItemSwipeCallback<>(adapter, leftSwipeCallback, rightSwipeCallback);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        recyclerView.setAdapter(adapter);
    }

}
