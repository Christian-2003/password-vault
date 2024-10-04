package de.passwordvault.view.entries.activity_entry;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;
import de.passwordvault.R;
import de.passwordvault.model.entry.EntryExtended;
import de.passwordvault.model.entry.EntryManager;
import de.passwordvault.model.packages.PackageCollection;
import de.passwordvault.model.packages.SerializablePackage;
import de.passwordvault.model.packages.SerializablePackageCollection;
import de.passwordvault.model.tags.Tag;
import de.passwordvault.view.entries.activity_packages.PackagesActivity;
import de.passwordvault.view.entries.dialog_edit_entry.EditEntryDialog;
import de.passwordvault.view.entries.dialog_edit_entry.EditEntryViewModel;
import de.passwordvault.view.entries.dialog_edit_tag.EditTagDialog;
import de.passwordvault.view.general.dialog_delete.DeleteDialog;
import de.passwordvault.view.utils.components.PasswordVaultActivity;
import de.passwordvault.view.utils.components.PasswordVaultBottomSheetDialog;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;


/**
 * Class models an activity which can display an {@link EntryExtended} to the user.
 *
 * @author  Christian-2003
 * @version 3.7.0
 */
public class EntryActivity extends PasswordVaultActivity<EntryViewModel> implements PasswordVaultBottomSheetDialog.Callback {

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

    /**
     * Attribute stores the text view displaying the entry name.
     */
    private TextView appBarTextView;

    /**
     * Attribute stores the launcher used to start the {@link de.passwordvault.view.entries.activity_packages.PackagesActivity}.
     */
    private ActivityResultLauncher<Intent> packagesLauncher;


    /**
     * Constructor instantiates a new activity.
     */
    public EntryActivity() {
        super(EntryViewModel.class, R.layout.activity_entry);

        packagesLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == PackagesActivity.RESULT_OK && result.getData() != null && result.getData().getExtras() != null) {
                Bundle extras = result.getData().getExtras();
                if (extras.containsKey(PackagesActivity.KEY_PACKAGES)) {
                    //Even though the PackagesActivity returns a SerializablePackageCollection as extra,
                    //this must be cast to an ArrayList<SerializablePackage>! Otherwise, the app crashes
                    //due do a ClassCastException because the result value cannot be cast to a
                    //SerializablePackageCollection:
                    ArrayList<SerializablePackage> packages = (ArrayList<SerializablePackage>)extras.getSerializable(PackagesActivity.KEY_PACKAGES);
                    if (packages != null && viewModel.getEntry() != null) {
                        viewModel.getEntry().setPackages(new SerializablePackageCollection(packages).toPackageCollection());
                    }
                    adapter.notifyItemChanged(EntryRecyclerViewAdapter.POSITION_GENERAL);
                }
            }
        });
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
        adapter = new EntryRecyclerViewAdapter(this, viewModel);

        adapter.setEditEntryListener(this::onEditEntryClicked);
        adapter.setDeleteClickListener(this::onDeleteClicked);
        adapter.setEditPackagesListener(this::onPackagesClicked);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setAdapter(adapter);

        appBarTextView = findViewById(R.id.text_appbar);
        appBarTextView.setText(viewModel.getEntry().getName());

        //Back button:
        findViewById(R.id.button_back).setOnClickListener(view -> finish());
    }


    private void onDeleteClicked(View view) {
        DeleteDialog dialog = new DeleteDialog();
        Bundle args = new Bundle();
        args.putString(DeleteDialog.ARG_MESSAGE, getString(R.string.delete_dialog_message).replace("{arg}", viewModel.getEntry().getName()));
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), null);
    }

    @Override
    public void onCallback(PasswordVaultBottomSheetDialog<? extends ViewModel> dialog, int resultCode) {
        if (dialog instanceof EditEntryDialog) {
            EditEntryDialog editEntryDialog = (EditEntryDialog)dialog;
            if (resultCode == PasswordVaultBottomSheetDialog.Callback.RESULT_SUCCESS && viewModel.getEntry() != null) {
                viewModel.getEntry().setName(editEntryDialog.getName());
                viewModel.getEntry().setDescription(editEntryDialog.getDescription());
                viewModel.getEntry().getTags().clear();
                for (Tag tag : editEntryDialog.getSelectedTags()) {
                    viewModel.getEntry().getTags().add(tag);
                }
                appBarTextView.setText(viewModel.getEntry().getName());
                adapter.notifyItemChanged(EntryRecyclerViewAdapter.POSITION_GENERAL);
            }
        }
        else if (dialog instanceof DeleteDialog) {
            if (resultCode == PasswordVaultBottomSheetDialog.Callback.RESULT_SUCCESS) {
                EntryManager.getInstance().remove(Objects.requireNonNull(viewModel.getEntry()).getUuid());
                finish();
            }
        }
        else if (dialog instanceof EditTagDialog) {
            Log.d("EditActivity", "Received callback: " + resultCode);
        }
    }


    private void onEditEntryClicked(int position) {
        if (viewModel.getEntry() != null) {
            EditEntryDialog dialog = new EditEntryDialog();
            Bundle args = new Bundle();
            args.putString(EditEntryDialog.ARG_NAME, viewModel.getEntry().getName());
            args.putString(EditEntryDialog.ARG_DESCRIPTION, viewModel.getEntry().getDescription());
            args.putSerializable(EditEntryDialog.ARG_TAGS, new ArrayList<>(viewModel.getEntry().getTags()));
            dialog.setArguments(args);
            dialog.show(getSupportFragmentManager(), null);
        }
    }

    private void onPackagesClicked(int position) {
        if (viewModel.getEntry() != null) {
            Intent intent = new Intent(this, PackagesActivity.class);
            intent.putExtra(PackagesActivity.KEY_PACKAGES, new SerializablePackageCollection(viewModel.getEntry().getPackages()));
            packagesLauncher.launch(intent);
            Log.d("EntryActivity", "Launched intent to edit packages");
        }
    }

}
