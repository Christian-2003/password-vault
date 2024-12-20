package de.passwordvault.view.settings.activity_recovery;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import de.passwordvault.R;
import de.passwordvault.model.security.login.SecurityQuestion;
import de.passwordvault.view.general.dialog_delete.DeleteDialog;
import de.passwordvault.view.general.dialog_more.Item;
import de.passwordvault.view.general.dialog_more.ItemButton;
import de.passwordvault.view.general.dialog_more.MoreDialog;
import de.passwordvault.view.general.dialog_more.MoreDialogCallback;
import de.passwordvault.view.settings.dialog_security_question.SecurityQuestionDialog;
import de.passwordvault.view.utils.components.PasswordVaultActivity;
import de.passwordvault.view.utils.components.PasswordVaultBottomSheetDialog;
import de.passwordvault.view.utils.recycler_view.RecyclerViewSwipeCallback;


/**
 * Class implements the activity that is used to configure the master password recovery.
 *
 * @author  Christian-2003
 * @version 3.7.0
 */
public class RecoveryActivity extends PasswordVaultActivity<RecoveryViewModel> implements PasswordVaultBottomSheetDialog.Callback, MoreDialogCallback {

    /**
     * Field stores the tag for the more dialog item to edit a security question.
     */
    private static final String TAG_EDIT_SECURITY_QUESTION = "edit";

    /**
     * Field stores the tag for the more dialog item to delete a security question.
     */
    private static final String TAG_DELETE_SECURITY_QUESTION = "delete";


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
     * Method is called whenever the activity closes.
     */
    @Override
    public void finish() {
        super.finish();
        viewModel.save();
    }


    /**
     * Method is called whenever a dialog callback is invoked.
     *
     * @param dialog        Dialog that was closed.
     * @param resultCode    Result code is either {@link #RESULT_SUCCESS} or {@link #RESULT_CANCEL}
     *                      and indicates how the dialog is closed.
     */
    @Override
    public void onCallback(PasswordVaultBottomSheetDialog<? extends ViewModel> dialog, int resultCode) {
        if (dialog instanceof SecurityQuestionDialog) {
            if (resultCode == PasswordVaultBottomSheetDialog.Callback.RESULT_SUCCESS) {
                SecurityQuestionDialog securityQuestionDialog = (SecurityQuestionDialog)dialog;
                if (securityQuestionDialog.getTag() == null) {
                    //Add a new security question:
                    if (viewModel.getSecurityQuestions().isEmpty()) {
                        adapter.notifyItemChanged(RecoveryRecyclerViewAdapter.POSITION_EMPTY_PLACEHOLDER);
                    }
                    viewModel.getSecurityQuestions().add(securityQuestionDialog.getQuestion());
                    adapter.notifyItemInserted(adapter.getItemCount());
                    adapter.notifyItemChanged(RecoveryRecyclerViewAdapter.POSITION_PROGRESS_BAR);
                }
                else {
                    //Edit a security question:
                    try {
                        int index = Integer.parseInt(dialog.getTag());
                        SecurityQuestion newQuestion = securityQuestionDialog.getQuestion();
                        viewModel.getSecurityQuestions().set(index, newQuestion);
                        adapter.notifyItemChanged(index + RecoveryRecyclerViewAdapter.OFFSET_QUESTIONS);
                    }
                    catch (Exception e) {
                        //Cannot parse index from tag.
                    }
                }
            }
        }
        else if (dialog instanceof DeleteDialog) {
            if (resultCode == PasswordVaultBottomSheetDialog.Callback.RESULT_SUCCESS) {
                try {
                    if (dialog.getTag() == null) {
                        return;
                    }
                    int index = Integer.parseInt(dialog.getTag());
                    viewModel.getSecurityQuestions().remove(index);
                    adapter.notifyItemRemoved(index + RecoveryRecyclerViewAdapter.OFFSET_QUESTIONS);
                    adapter.notifyItemChanged(RecoveryRecyclerViewAdapter.POSITION_PROGRESS_BAR);
                    if (viewModel.getSecurityQuestions().isEmpty()) {
                        adapter.notifyItemChanged(RecoveryRecyclerViewAdapter.POSITION_EMPTY_PLACEHOLDER);
                    }
                }
                catch (Exception e) {
                    //Cannot parse index from tag.
                }
            }
        }
    }


    /**
     * Method is called whenever the {@link MoreDialog} is dismissed with a callback.
     *
     * @param dialog    Dialog in which the action was invoked.
     * @param tag       Tag from the {@link Item} whose action is invoked.
     * @param position  Position of the {@link Item} within the dialog.
     */
    @Override
    public void onDialogItemClicked(MoreDialog dialog, String tag, int position) {
        String[] tagParts = tag.split(":"); //Tag in the form of "<TAG>:<POSITION>".
        if (tagParts.length != 2) {
            return;
        }
        String tagValue = tagParts[0];
        int index;
        try {
            index = Integer.parseInt(tagParts[1]);
        }
        catch (NumberFormatException e) {
            return;
        }
        switch (tagValue) {
            case TAG_EDIT_SECURITY_QUESTION:
                onEditSecurityQuestion(index);
                break;
            case TAG_DELETE_SECURITY_QUESTION:
                onDeleteSecurityQuestion(index);
                break;
        }
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


    /**
     * Listener for when a security question should be edited.
     *
     * @param position  Position of the view that invoked this action
     */
    private void onEditSecurityQuestion(int position) {
        //Get available security questions that can be used:
        int index = position - RecoveryRecyclerViewAdapter.OFFSET_QUESTIONS;
        SecurityQuestion question = viewModel.getSecurityQuestions().get(index);
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
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), "" + index);
    }

    /**
     * Listener for when a security question should be deleted.
     *
     * @param position  Position of the view that invoked this action.
     */
    private void onDeleteSecurityQuestion(int position) {
        int index = position - RecoveryRecyclerViewAdapter.OFFSET_QUESTIONS;
        SecurityQuestion question = viewModel.getSecurityQuestions().get(index);
        DeleteDialog dialog = new DeleteDialog();
        Bundle args = new Bundle();
        String message = getString(R.string.delete_dialog_message);
        message = message.replace("{arg}", SecurityQuestion.getAllQuestions()[question.getQuestion()]);
        args.putString(DeleteDialog.ARG_MESSAGE, message);
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), "" + index);
    }

    /**
     * Listener for when a new security question should be added.
     *
     * @param position  Position of the item which invoked this action.
     */
    private void onAddSecurityQuestion(int position) {
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
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), null);
    }


    /**
     * Listener for when the "more" button of a security question is clicked.
     *
     * @param position  Position of the view which invoked this action.
     */
    private void onShowMoreOptions(int position) {
        int index = position - RecoveryRecyclerViewAdapter.OFFSET_QUESTIONS;
        SecurityQuestion question = viewModel.getSecurityQuestions().get(index);
        MoreDialog dialog = new MoreDialog();
        Bundle args = new Bundle();
        args.putString(MoreDialog.ARG_TITLE, SecurityQuestion.getAllQuestions()[question.getQuestion()]);
        args.putInt(MoreDialog.ARG_ICON, R.drawable.ic_help);
        ArrayList<Item> items = new ArrayList<>();
        items.add(new ItemButton(getString(R.string.button_edit), TAG_EDIT_SECURITY_QUESTION + ":" + position, R.drawable.ic_edit));
        items.add(new ItemButton(getString(R.string.button_delete), TAG_DELETE_SECURITY_QUESTION + ":" + position, R.drawable.ic_delete));
        args.putSerializable(MoreDialog.ARG_ITEMS, items);
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), null);
    }

}
