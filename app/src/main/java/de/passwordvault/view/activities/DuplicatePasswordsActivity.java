package de.passwordvault.view.activities;

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


public class DuplicatePasswordsActivity extends AppCompatActivity implements OnRecyclerItemClickListener<Password> {

    public static final String KEY_PASSWORDS = "passwords";


    private DuplicatePasswordsViewModel viewModel;

    private PasswordsRecyclerViewAdapter adapter;


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

        adapter = new PasswordsRecyclerViewAdapter(passwords, this, false);
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
        //TODO: Launch activity...
    }

}
