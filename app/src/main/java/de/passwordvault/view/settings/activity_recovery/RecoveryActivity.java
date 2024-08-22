package de.passwordvault.view.settings.activity_recovery;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import de.passwordvault.R;
import de.passwordvault.model.security.login.SecurityQuestion;
import de.passwordvault.view.settings.dialog_security_question.SecurityQuestionDialog;
import de.passwordvault.view.utils.components.PasswordVaultActivity;
import de.passwordvault.view.utils.components.PasswordVaultBottomSheetDialog;
import de.passwordvault.view.utils.recycler_view.RecyclerViewSwipeCallback;


/**
 * Class implements the activity that is used to configure the master password recovery.
 *
 * @author  Christian-2003
 * @version 3.6.0
 */
public class RecoveryActivity extends PasswordVaultActivity<RecoveryViewModel> {

    /**
     * Attribute stores the adapter for the activity.
     */
    private RecoveryRecyclerViewAdapter adapter;


    /**
     * Constructor instantiates a new activity.
     */
    public RecoveryActivity() {
        super(RecoveryViewModel.class, R.layout.activity_recovery);
    }


    /**
     * Method is called whenever the activity is created.
     *
     * @param savedInstanceState    Previously saved state of the instance.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Back button:
        findViewById(R.id.button_back).setOnClickListener(view -> finish());

        if (viewModel.getSecurityQuestions() == null) {
            viewModel.loadSecurityQuestions();
        }

        //Recycler view:
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        adapter = new RecoveryRecyclerViewAdapter(this, viewModel);
        adapter.setAddQuestionListener(this::onAddSecurityQuestion);
        recyclerView.setAdapter(adapter);
        RecyclerViewSwipeCallback.SwipeAction leftSwipe = RecyclerViewSwipeCallback.makeLeftSwipeAction(this::onEditSecurityQuestion, this::onDeleteSecurityQuestion);
        RecyclerViewSwipeCallback.SwipeAction rightSwipe = RecyclerViewSwipeCallback.makeRightSwipeAction(this::onEditSecurityQuestion, this::onDeleteSecurityQuestion);
        RecyclerViewSwipeCallback callback = new RecyclerViewSwipeCallback(adapter, leftSwipe, rightSwipe);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);
    }


    private void onEditSecurityQuestion(RecyclerView.ViewHolder holder) {
        //Get available security questions that can be used:
        int position = holder.getAdapterPosition() - RecoveryRecyclerViewAdapter.QUESTIONS_OFFSET;
        SecurityQuestion question = viewModel.getSecurityQuestions().get(position);
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
        args.putSerializable(SecurityQuestionDialog.ARG_QUESTION, question);
        args.putStringArray(SecurityQuestionDialog.ARG_QUESTIONS, availableQuestions);
        PasswordVaultBottomSheetDialog.Callback callback = (d, resultCode) -> {
            if (resultCode == PasswordVaultBottomSheetDialog.Callback.RESULT_SUCCESS) {
                SecurityQuestionDialog securityQuestionDialog = (SecurityQuestionDialog)d;
                SecurityQuestion newQuestion = securityQuestionDialog.getQuestion();
                viewModel.getSecurityQuestions().set(position, newQuestion);
                adapter.notifyItemChanged(position + RecoveryRecyclerViewAdapter.QUESTIONS_OFFSET);
            }
        };
        args.putSerializable(SecurityQuestionDialog.ARG_CALLBACK, callback);
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), null);
    }

    private void onDeleteSecurityQuestion(RecyclerView.ViewHolder holder) {

    }

    private void onAddSecurityQuestion(RecyclerView.ViewHolder holder) {

    }

}
