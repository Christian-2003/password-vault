package de.passwordvault.view.authentication.activity_security_question;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import de.passwordvault.R;
import de.passwordvault.model.security.authentication.AuthenticationCallback;
import de.passwordvault.model.security.authentication.AuthenticationFailure;
import de.passwordvault.model.security.authentication.Authenticator;
import de.passwordvault.view.utils.components.PasswordVaultActivity;


/**
 * Class implements the activity through which the user can enter the answers to security questions
 * in order to restore the master password.
 *
 * @author  Christian-2003
 * @version 3.6.0
 */
public class SecurityQuestionActivity extends PasswordVaultActivity<SecurityQuestionViewModel> implements AuthenticationCallback {

    /**
     * Attribute stores the recycler view adapter for the activity.
     */
    private SecurityQuestionRecyclerViewAdapter adapter;


    /**
     * Constructor instantiates a new activity.
     */
    public SecurityQuestionActivity() {
        super(SecurityQuestionViewModel.class, R.layout.activity_security_question);
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

        //Recycler view:
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        adapter = new SecurityQuestionRecyclerViewAdapter(this, viewModel);
        recyclerView.setAdapter(adapter);

        //Recover button:
        adapter.setOnRecoverClicked(position -> checkAnswerValidity());
    }


    /**
     * Method is called when the authentication succeeds.
     *
     * @param tag   Tag that was passed with the authentication request.
     */
    @Override
    public void onAuthenticationSuccess(@Nullable String tag) {
        Toast.makeText(this, R.string.recovery_success, Toast.LENGTH_SHORT).show();
        finish();
    }


    /**
     * Method is called when the authentication fails.
     *
     * @param tag   Tag that was passed with the authentication request.
     * @param code  Authentication failure code indicating why the authentication failed.
     */
    @Override
    public void onAuthenticationFailure(@Nullable String tag, @NonNull AuthenticationFailure code) {

    }


    /**
     * Method checks whether the entered answers are correct. If the answers are not correct, an error
     * message is displayed. Otherwise, a dialog to change the password is displayed.
     */
    private void checkAnswerValidity() {
        ArrayList<String> answers = new ArrayList<>();
        for (int i = 0; i < viewModel.getQuestions().size(); i++) {
            answers.add(adapter.getAnswer(i));
        }
        if (viewModel.areAnswersValid(answers)) {
            adapter.notifyItemChanged(adapter.getItemCount() - 1);
            Authenticator authenticator = new Authenticator(this, "", R.string.recovery_password);
            authenticator.authenticate(this, Authenticator.AUTH_PASSWORD, Authenticator.TYPE_REGISTER);
        }
        else {
            adapter.notifyItemChanged(adapter.getItemCount() - 1);
        }
    }

}
