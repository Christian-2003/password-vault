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
import de.passwordvault.model.entry.EntryAbbreviated;
import de.passwordvault.model.entry.EntryManager;
import de.passwordvault.view.utils.adapters.EntriesRecyclerViewAdapter;
import de.passwordvault.view.utils.OnRecyclerItemClickListener;
import de.passwordvault.viewmodel.activities.DuplicatePasswordEntriesViewModel;


public class DuplicatePasswordEntriesActivity extends AppCompatActivity implements OnRecyclerItemClickListener<EntryAbbreviated> {

    /**
     * Field stores the key that needs to be used when passing the list of passwords whose entries
     * shall be displayed.
     */
    public static final String KEY_PASSWORDS = "passwords";


    /**
     * Attribute stores the view model for the activity.
     */
    private DuplicatePasswordEntriesViewModel viewModel;


    /**
     * Method is called whenever the activity is created.
     *
     * @param savedInstanceState    Previously saved state of the instance.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duplicate_password_entries);
        viewModel = new ViewModelProvider(this).get(DuplicatePasswordEntriesViewModel.class);

        if (viewModel.getPasswords() == null) {
            Bundle args = getIntent().getExtras();
            try {
                ArrayList<Password> passwords = (ArrayList<Password>)args.get(KEY_PASSWORDS);
                if (passwords == null) {
                    Log.d("DuplicatePasswordEntriesActivity", "Argument is null.");
                    finish();
                }
                viewModel.setPasswords(passwords);
            }
            catch (ClassCastException e) {
                Log.d("DuplicatePasswordEntriesActivity", "Could not cast argument.");
                finish();
            }
        }

        findViewById(R.id.duplicate_password_entries_back_button).setOnClickListener(view -> finish());

        ArrayList<EntryAbbreviated> entries = new ArrayList<>();
        for (Password password : viewModel.getPasswords()) {
            EntryAbbreviated abbreviated = EntryManager.getInstance().getAbbreviated(password.getEntryUuid());
            if (abbreviated != null) {
                entries.add(abbreviated);
            }
        }

        EntriesRecyclerViewAdapter adapter = new EntriesRecyclerViewAdapter(entries, this);
        RecyclerView recyclerView = findViewById(R.id.duplicate_password_entries_recycler_view);
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
    public void onItemClick(EntryAbbreviated item, int position) {
        Intent intent = new Intent(this, EntryActivity.class);
        intent.putExtra("uuid", item.getUuid());
        startActivity(intent);
    }

}
