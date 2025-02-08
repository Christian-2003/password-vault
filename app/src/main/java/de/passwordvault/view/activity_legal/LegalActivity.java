package de.passwordvault.view.activity_legal;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import java.util.Calendar;
import de.passwordvault.R;
import de.passwordvault.view.utils.Utils;
import de.passwordvault.view.utils.components.PasswordVaultActivity;


/**
 * Class implements the activity through which users can accept (or deny) changes to the privacy
 * policy or terms of service.
 *
 * @author  Christian-2003
 * @version 3.7.2
 */
public class LegalActivity extends PasswordVaultActivity<LegalViewModel> {

    /**
     * Field stores the key with which to pass the privacy policy DTO as extra.
     */
    public static final String EXTRA_PRIVACY = "extra_privacy";

    /**
     * Field stores the key with which to pass the terms of service DTO as extra.
     */
    public static final String EXTRA_TOS = "extra_tos";

    /**
     * Field stores the result code returned when the changes are denied.
     */
    public static final int RESULT_DENIED = 931;


    /**
     * Constructor instantiates a new legal activity.
     */
    public LegalActivity() {
        super(LegalViewModel.class, R.layout.activity_legal);
    }


    /**
     * Method is called to close the activity.
     */
    @Override
    public void finish() {
        if (viewModel.isAccepted()) {
            setResult(RESULT_OK);
        }
        else {
            setResult(RESULT_DENIED);
        }
        super.finish();
    }


    /**
     * @param savedInstanceState Previously saved state of the instance.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!viewModel.processExtras(getIntent().getExtras())) {
            finish();
            return;
        }

        //Headline:
        TextView changeTextView = findViewById(R.id.text_change);
        if (viewModel.getPrivacyDto() != null && viewModel.getTosDto() != null) {
            changeTextView.setText(R.string.legal_change_privacy_tos);
        }
        else if (viewModel.getPrivacyDto() != null) {
            changeTextView.setText(R.string.legal_change_privacy);
        }
        else if (viewModel.getTosDto() != null) {
            changeTextView.setText(R.string.legal_change_tos);
        }

        //Date:
        TextView dateTextView = findViewById(R.id.text_date);
        Calendar date = null;
        if (viewModel.getPrivacyDto() != null && viewModel.getTosDto() != null) {
            if (viewModel.getPrivacyDto().getDate().before(viewModel.getTosDto().getDate())) {
                date = viewModel.getPrivacyDto().getDate();
            }
            else {
                date = viewModel.getTosDto().getDate();
            }
        }
        else if (viewModel.getPrivacyDto() != null) {
            date = viewModel.getPrivacyDto().getDate();
        }
        else if (viewModel.getTosDto() != null) {
            date = viewModel.getTosDto().getDate();
        }
        if (date != null) {
            dateTextView.setText(getString(R.string.legal_date).replace("{arg}", Utils.formatDate(date)));
        }
        else {
            dateTextView.setVisibility(View.GONE);
        }

        //Changes:
        LinearLayout privacyContainer = findViewById(R.id.container_privacy);
        LinearLayout tosContainer = findViewById(R.id.container_tos);
        if (viewModel.getPrivacyDto() != null) {
            privacyContainer.setVisibility(View.VISIBLE);
            privacyContainer.setOnClickListener(view -> openUrlInBrowserOrApp(viewModel.getPrivacyDto().getPage().getUrl()));
        }
        if (viewModel.getTosDto() != null) {
            tosContainer.setVisibility(View.VISIBLE);
            tosContainer.setOnClickListener(view -> openUrlInBrowserOrApp(viewModel.getTosDto().getPage().getUrl()));
        }

        //Buttons:
        findViewById(R.id.button_accept).setOnClickListener(this::onAcceptClicked);
        findViewById(R.id.button_deny).setOnClickListener(this::onDenyClicked);
    }


    /**
     * Method is called whenever the button to accept changes is clicked.
     *
     * @param view  View that is clicked.
     */
    private void onAcceptClicked(View view) {
        viewModel.acceptChanges(this);
        finish();
    }


    /**
     * Method is called whenever the button to deny changes is clicked.
     *
     * @param view  View that is clicked.
     */
    private void onDenyClicked(View view) {
        finish();
    }

}
