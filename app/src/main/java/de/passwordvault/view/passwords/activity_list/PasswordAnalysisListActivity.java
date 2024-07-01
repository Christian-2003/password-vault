package de.passwordvault.view.passwords.activity_list;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import de.passwordvault.R;
import de.passwordvault.model.analysis.passwords.Password;
import de.passwordvault.model.analysis.passwords.PasswordSecurityAnalysis;
import de.passwordvault.view.entries.activity_entry.EntryActivity;
import de.passwordvault.view.utils.recycler_view.OnRecyclerItemClickListener;
import de.passwordvault.view.passwords.PasswordsRecyclerViewAdapter;
import de.passwordvault.view.utils.components.PasswordVaultBaseActivity;


/**
 * Class implements the fragment which shows a list of all passwords that were analyzed during the
 * password security analysis.
 *
 * @author  Christian-2003
 * @version 3.5.2
 */
public class PasswordAnalysisListActivity extends PasswordVaultBaseActivity implements OnRecyclerItemClickListener<Password> {

    /**
     * Method is called whenever the activity is created.
     *
     * @param savedInstanceState    Previously saved state of the instance.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_analysis_list);

        findViewById(R.id.button_back).setOnClickListener(view -> finish());

        boolean passwordsFound = PasswordSecurityAnalysis.getInstance().getData().size() != 0;
        findViewById(R.id.password_analysis_list_none_container).setVisibility(passwordsFound ? View.GONE : View.VISIBLE);

        PasswordsRecyclerViewAdapter adapter = new PasswordsRecyclerViewAdapter(PasswordSecurityAnalysis.getInstance().getData(), this, true);
        RecyclerView recyclerView = findViewById(R.id.password_analysis_recycler_view);
        recyclerView.setVisibility(passwordsFound ? View.VISIBLE : View.GONE);
        recyclerView.setAdapter(adapter);
    }


    /**
     * Method is called when the item which is passed as argument is clicked by the user.
     *
     * @param item      Clicked item.
     * @param position  Index of the clicked item.
     */
    @Override
    public void onItemClick(Password item, int position) {
        Intent intent = new Intent(this, EntryActivity.class);
        intent.putExtra("uuid", item.getEntryUuid());
        startActivity(intent);
    }

}
