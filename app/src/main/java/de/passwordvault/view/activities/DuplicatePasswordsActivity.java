package de.passwordvault.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import de.passwordvault.R;
import de.passwordvault.model.analysis.passwords.Password;
import de.passwordvault.view.utils.OnRecyclerItemClickListener;
import de.passwordvault.view.utils.PasswordsRecyclerViewAdapter;
import de.passwordvault.viewmodel.activities.DuplicatePasswordsViewModel;


/**
 * Class implements an activity which shows a list of duplicate passwords.
 *
 * @author  Christian-2003
 * @version 3.4.0
 */
public class DuplicatePasswordsActivity extends AppCompatActivity implements OnRecyclerItemClickListener<Password> {

    /**
     * Field stores the key that needs to be used when passing the list of duplicate passwords.
     */
    public static final String KEY_PASSWORDS = "passwords";


    /**
     * Attribute stores the view model for the activity.
     */
    private DuplicatePasswordsViewModel viewModel;


    /**
     * Method is called whenever the activity is created.
     *
     * @param savedInstanceState    Previously saved state of the instance.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duplicate_passwords);
        viewModel = new ViewModelProvider(this).get(DuplicatePasswordsViewModel.class);

        if (viewModel.getPasswords() == null) {
            Bundle bundle = getIntent().getExtras();
            try {
                ArrayList<ArrayList<Password>> passwords = (ArrayList<ArrayList<Password>>) bundle.get(KEY_PASSWORDS);
                if (passwords == null) {
                    finish();
                    Log.d("DuplicatePasswordsActivity", "Argument is null.");
                    return;
                }
                viewModel.setPasswords(passwords);
            }
            catch (ClassCastException e) {
                finish();
                Log.d("DuplicatePasswordsActivity", "Could not cast argument.");
                return;
            }
        }

        findViewById(R.id.duplicate_passwords_back_button).setOnClickListener(view -> finish());

        ArrayList<Password> passwords = new ArrayList<>();
        for (ArrayList<Password> password : viewModel.getPasswords()) {
            passwords.add(password.get(0));
        }

        PasswordsRecyclerViewAdapter adapter = new PasswordsRecyclerViewAdapter(passwords, this, false);
        RecyclerView recyclerView = findViewById(R.id.duplicate_passwords_recycler_view);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }


    /**
     * Method is called when the item which is passed as argument is clicked by the user.
     *
     * @param item      Clicked item.
     * @param position  Index of the clicked item.
     */
    @Override
    public void onItemClick(Password item, int position) {
        ArrayList<Password> passwords = viewModel.getPasswords().get(position);
        Intent intent = new Intent(this, DuplicatePasswordEntriesActivity.class);
        intent.putExtra(DuplicatePasswordEntriesActivity.KEY_PASSWORDS, passwords);
        startActivity(intent);
    }

}
