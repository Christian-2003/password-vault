package de.passwordvault.view.entries.activity_entry;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import de.passwordvault.R;
import de.passwordvault.model.entry.EntryExtended;
import de.passwordvault.model.entry.EntryManager;
import de.passwordvault.view.utils.components.PasswordVaultActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Objects;


/**
 * Class models an activity which can display an {@link EntryExtended} to the user.
 *
 * @author  Christian-2003
 * @version 3.7.0
 */
public class EntryActivity extends PasswordVaultActivity<EntryViewModel> {

    /**
     * Field stores the key to use when passing the UUID of an entry to show as extra with the intent.
     */
    public static final String KEY_ID = "uuid";

    /**
     * Field stores the result code returned if the displayed entry has been edited.
     */
    public static final int RESULT_EDITED = 314;

    /**
     * Field stores the result code returned if the displayed entry is deleted.
     */
    public static final int RESULT_DELETED = 315;


    /**
     * Attribute stores the recycler view adapter
     */
    private EntryRecyclerViewAdapter adapter;

    private TextView toolbarTextView;

    private ImageButton backButton;

    private LinearLayout selectionButtonsContainer;


    /**
     * Constructor instantiates a new activity.
     */
    public EntryActivity() {
        super(EntryViewModel.class, R.layout.activity_entry);
    }


    /**
     * Method is called whenever the activity is created.
     *
     * @param savedInstanceState    Previously saved state of the instance.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Get entry to display or create new entry:
        if (viewModel.getEntry() == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null && extras.containsKey(KEY_ID)) {
                String uuid = extras.getString(KEY_ID);
                EntryExtended entry = EntryManager.getInstance().get(uuid);
                if (entry != null) {
                    viewModel.setEntry(new EntryExtended(entry));
                }
            }
            if (viewModel.getEntry() == null) {
                viewModel.setEntry(new EntryExtended());
            }
        }

        //Configure recycler view adapter:
        adapter = new EntryRecyclerViewAdapter(this, viewModel.getEntry());
        adapter.setEditClickListener(this::onEditClicked);
        adapter.setDeleteClickListener(this::onDeleteClicked);
        adapter.setImageClickListener(this::onAppImageClicked);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setAdapter(adapter);

        toolbarTextView = findViewById(R.id.text_toolbar);
        toolbarTextView.setText(viewModel.getEntry().getName());

        backButton = findViewById(R.id.button_back);
        backButton.setOnClickListener(view -> finish());

        selectionButtonsContainer = findViewById(R.id.container_selection_buttons);
        selectionButtonsContainer.setVisibility(View.GONE);
    }


    private void onEditClicked(View view) {

    }

    private void onDeleteClicked(View view) {
        EntryManager.getInstance().remove(Objects.requireNonNull(viewModel.getEntry()).getUuid());
        finish();
    }

    private void onAppImageClicked(View view) {

    }

}
