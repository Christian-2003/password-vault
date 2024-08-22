package de.passwordvault.view.settings.activity_recovery;

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
import de.passwordvault.view.entries.activity_add_entry.dialog_delete.ConfirmDeleteDialog;
import de.passwordvault.view.settings.activity_recovery.dialog_security_question.SecurityQuestionDialog;
import de.passwordvault.view.utils.DialogCallbackListener;
import de.passwordvault.view.utils.components.PasswordVaultActivity;
import de.passwordvault.view.utils.recycler_view.OnRecyclerItemClickListener;
import de.passwordvault.view.utils.recycler_view.RecyclerItemSwipeCallback;
import de.passwordvault.view.utils.components.PasswordVaultBaseActivity;
import de.passwordvault.view.utils.recycler_view.RecyclerViewSwipeCallback;


/**
 * Class implements the activity that is used to configure the master password recovery.
 *
 * @author  Christian-2003
 * @version 3.6.0
 */
public class RecoveryActivity extends PasswordVaultActivity<RecoveryViewModel> {


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
        RecoveryRecyclerViewAdapter adapter = new RecoveryRecyclerViewAdapter(this, viewModel);
        recyclerView.setAdapter(adapter);
        RecyclerViewSwipeCallback.SwipeAction leftSwipe = RecyclerViewSwipeCallback.makeLeftSwipeAction(this::onEditSecurityQuestion, this::onDeleteSecurityQuestion);
        RecyclerViewSwipeCallback.SwipeAction rightSwipe = RecyclerViewSwipeCallback.makeRightSwipeAction(this::onEditSecurityQuestion, this::onDeleteSecurityQuestion);
        RecyclerViewSwipeCallback callback = new RecyclerViewSwipeCallback(adapter, leftSwipe, rightSwipe);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);
    }


    private void onEditSecurityQuestion(RecyclerView.ViewHolder holder) {

    }

    private void onDeleteSecurityQuestion(RecyclerView.ViewHolder holder) {

    }

}
