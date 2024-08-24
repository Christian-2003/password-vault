package de.passwordvault.view.settings.activity_recovery;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import de.passwordvault.R;
import de.passwordvault.model.security.login.SecurityQuestion;
import de.passwordvault.view.general.dialog_delete.DeleteDialog;
import de.passwordvault.view.general.dialog_more.MoreDialog;
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
        adapter.setQuestionMoreListener(this::onShowMoreOptions);
        recyclerView.setAdapter(adapter);
        RecyclerViewSwipeCallback.SwipeAction leftSwipe = RecyclerViewSwipeCallback.makeLeftSwipeAction(this::onEditSecurityQuestion, this::onDeleteSecurityQuestion);
        RecyclerViewSwipeCallback.SwipeAction rightSwipe = RecyclerViewSwipeCallback.makeRightSwipeAction(this::onEditSecurityQuestion, this::onDeleteSecurityQuestion);
        RecyclerViewSwipeCallback callback = new RecyclerViewSwipeCallback(adapter, leftSwipe, rightSwipe);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);
    }


    @Override
    public void finish() {
        super.finish();
        viewModel.save();
    }

    /**
     * Listener for when a security question should be edited.
     *
     * @param holder    View holder of the view displaying the question to edit.
     */
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

    /**
     * Listener for when a security question should be deleted.
     *
     * @param holder    View holder of the view displaying the question to delete.
     */
    private void onDeleteSecurityQuestion(RecyclerView.ViewHolder holder) {
        int position = holder.getAdapterPosition() - RecoveryRecyclerViewAdapter.QUESTIONS_OFFSET;
        SecurityQuestion question = viewModel.getSecurityQuestions().get(position);
        DeleteDialog dialog = new DeleteDialog();
        Bundle args = new Bundle();
        String message = getString(R.string.delete_dialog_message);
        message = message.replace("{arg}", SecurityQuestion.getAllQuestions()[question.getQuestion()]);
        args.putString(DeleteDialog.ARG_MESSAGE, message);
        PasswordVaultBottomSheetDialog.Callback callback = (d, resultCode) -> {
            if (resultCode == PasswordVaultBottomSheetDialog.Callback.RESULT_SUCCESS) {
                viewModel.getSecurityQuestions().remove(position);
                adapter.notifyItemRemoved(position + RecoveryRecyclerViewAdapter.QUESTIONS_OFFSET);
                adapter.notifyItemChanged(RecoveryRecyclerViewAdapter.POSITION_PROGRESS_BAR);
                if (viewModel.getSecurityQuestions().isEmpty()) {
                    adapter.notifyItemChanged(RecoveryRecyclerViewAdapter.POSITION_EMPTY_PLACEHOLDER);
                }
            }
        };
        args.putSerializable(SecurityQuestionDialog.ARG_CALLBACK, callback);
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), null);
    }

    /**
     * Listener for when a new security question should be added.
     *
     * @param holder    View holder of the view requesting to add a new security question.
     */
    private void onAddSecurityQuestion(RecyclerView.ViewHolder holder) {
        String[] allQuestions = SecurityQuestion.getAllQuestions();
        String[] unusedQuestions = new String[allQuestions.length - viewModel.getSecurityQuestions().size()];
        int k = 0;
        for (int i = 0; i < allQuestions.length; i++) {
            boolean questionUnused = true;
            for (SecurityQuestion question : viewModel.getSecurityQuestions()) {
                if (question.getQuestion() == i) {
                    questionUnused = false;
                    break;
                }
            }
            if (questionUnused) {
                unusedQuestions[k++] = allQuestions[i];
            }
        }
        SecurityQuestionDialog dialog = new SecurityQuestionDialog();
        Bundle args = new Bundle();
        args.putStringArray(SecurityQuestionDialog.ARG_QUESTIONS, unusedQuestions);
        args.putSerializable(SecurityQuestionDialog.ARG_QUESTION, null);
        PasswordVaultBottomSheetDialog.Callback callback = (d, resultCode) -> {
            if (resultCode == PasswordVaultBottomSheetDialog.Callback.RESULT_SUCCESS) {
                SecurityQuestionDialog securityQuestionDialog = (SecurityQuestionDialog)d;
                if (viewModel.getSecurityQuestions().isEmpty()) {
                    adapter.notifyItemChanged(RecoveryRecyclerViewAdapter.POSITION_EMPTY_PLACEHOLDER);
                }
                viewModel.getSecurityQuestions().add(securityQuestionDialog.getQuestion());
                adapter.notifyItemInserted(adapter.getItemCount());
                adapter.notifyItemChanged(RecoveryRecyclerViewAdapter.POSITION_PROGRESS_BAR);
            }
        };
        args.putSerializable(SecurityQuestionDialog.ARG_CALLBACK, callback);
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), null);
    }


    /**
     * Listener for when the "more" button of a security question is clicked.
     *
     * @param holder    View holder of the security question whose button was clicked.
     */
    private void onShowMoreOptions(RecyclerView.ViewHolder holder) {
        int position = holder.getAdapterPosition() - RecoveryRecyclerViewAdapter.QUESTIONS_OFFSET;
        SecurityQuestion question = viewModel.getSecurityQuestions().get(position);
        MoreDialog dialog = new MoreDialog();
        Bundle args = new Bundle();
        args.putString(MoreDialog.ARG_TITLE, SecurityQuestion.getAllQuestions()[question.getQuestion()]);
        args.putInt(MoreDialog.ARG_ICON, R.drawable.ic_help);
        ArrayList<MoreDialog.Item> items = new ArrayList<>();
        items.add(new MoreDialog.Item(getString(R.string.button_edit), R.drawable.ic_edit, view -> onEditSecurityQuestion(holder)));
        items.add(new MoreDialog.Item(getString(R.string.button_delete), R.drawable.ic_delete, view -> onDeleteSecurityQuestion(holder)));
        args.putSerializable(MoreDialog.ARG_ITEMS, items);
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), null);
    }

}
