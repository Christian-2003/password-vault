package de.passwordvault.view.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputLayout;
import java.util.ArrayList;
import de.passwordvault.R;
import de.passwordvault.model.Observable;
import de.passwordvault.model.Observer;
import de.passwordvault.model.entry.EntryExtended;
import de.passwordvault.model.entry.EntryManager;
import de.passwordvault.model.packages.Package;
import de.passwordvault.model.packages.PackageCollection;
import de.passwordvault.model.packages.PackagesManager;
import de.passwordvault.model.packages.SerializablePackage;
import de.passwordvault.model.packages.SerializablePackageCollection;
import de.passwordvault.model.tags.Tag;
import de.passwordvault.model.tags.TagCollection;
import de.passwordvault.model.tags.TagManager;
import de.passwordvault.view.dialogs.EditTagDialog;
import de.passwordvault.view.utils.DetailsItemMoveCallback;
import de.passwordvault.view.utils.adapters.DetailsRecyclerViewAdapter;
import de.passwordvault.view.utils.adapters.PackagesLogoRecyclerViewAdapter;
import de.passwordvault.viewmodel.activities.AddEntryViewModel;
import de.passwordvault.model.detail.Detail;
import de.passwordvault.view.dialogs.ConfirmDeleteDetailDialog;
import de.passwordvault.view.dialogs.DetailDialog;
import de.passwordvault.view.utils.DialogCallbackListener;


/**
 * Class implements an activity which can add (or edit) entries.
 *
 * @author  Christian-2003
 * @version 3.4.0
 */
public class AddEntryActivity extends AppCompatActivity implements DialogCallbackListener, Observer<ArrayList<Tag>> {

    /**
     * Attribute stores the {@linkplain androidx.lifecycle.ViewModel} of this activity.
     */
    private AddEntryViewModel viewModel;

    /**
     * Attribute stores the activity result launcher used for launching an activity.
     */
    private ActivityResultLauncher<Intent> activityLauncher;

    /**
     * Attribute stores the adapter for the recycler view displaying all details.
     */
    private DetailsRecyclerViewAdapter adapter;


    /**
     * Method is called whenever the activity is created / recreated.
     *
     * @param savedInstanceState    Previously saved state of the instance.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);
        viewModel = new ViewModelProvider(this).get(AddEntryViewModel.class);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("entry")) {
            //Activity shall be used to edit an entry:
            if (viewModel.getEntry() == null) {
                viewModel.setEntry(EntryManager.getInstance().get(bundle.getString("entry")));
                viewModel.setTags(new TagCollection(viewModel.getEntry().getTags()));
            }
            ((TextView)findViewById(R.id.add_entry_title)).setText(viewModel.getEntry().getName());
            ((TextView)findViewById(R.id.add_entry_name)).setText(viewModel.getEntry().getName());
            ((TextView)findViewById(R.id.add_entry_description)).setText(viewModel.getEntry().getDescription());
        }
        else if (viewModel.getEntry() == null){
            //Activity shall be used to create a new entry:
            viewModel.setEntry(new EntryExtended());
            viewModel.setTags(new TagCollection());
        }

        RecyclerView detailsContainer = findViewById(R.id.add_entry_details_container);
        adapter = new DetailsRecyclerViewAdapter(viewModel.getEntry().getDetails(), this, this, null);
        ItemTouchHelper.Callback callback = new DetailsItemMoveCallback(adapter, true, true);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(detailsContainer);
        detailsContainer.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
        detailsContainer.setAdapter(adapter);

        setupTags();
        setupPackage();

        activityLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                Bundle results = result.getData().getExtras();
                if (results != null && results.containsKey(PackagesActivity.KEY_PACKAGES)) {
                    try {
                        ArrayList<SerializablePackage> packages = (ArrayList<SerializablePackage>)results.getSerializable(PackagesActivity.KEY_PACKAGES);
                        viewModel.getEntry().setPackages(new SerializablePackageCollection(packages).toPackageCollection());
                    }
                    catch (Exception e) {
                        Log.d("AddEntryActivity", e.getMessage());
                        return;
                    }
                    setupPackage();
                }
            }
        });

        findViewById(R.id.add_entry_package_edit_button).setOnClickListener(view -> {
            Intent intent = new Intent(AddEntryActivity.this, PackagesActivity.class);
            SerializablePackageCollection packages = new SerializablePackageCollection(viewModel.getEntry().getPackages());
            intent.putExtra(PackagesActivity.KEY_PACKAGES, packages);
            activityLauncher.launch(intent);
        });

        //Add ClickListeners to close the activity:
        findViewById(R.id.add_entry_button_back).setOnClickListener(view -> AddEntryActivity.this.finish());
        findViewById(R.id.add_entry_button_cancel).setOnClickListener(view -> {
            setResult(RESULT_CANCELED, getIntent());
            AddEntryActivity.this.finish();
        });

        //Add ClickListener to add new detail:
        findViewById(R.id.add_entry_button_add_detail).setOnClickListener(view -> {
            DetailDialog dialog = new DetailDialog();
            Bundle dialogArgs = new Bundle();
            dialogArgs.putSerializable(DetailDialog.KEY_CALLBACK_LISTENER, AddEntryActivity.this);
            dialog.setArguments(dialogArgs);
            dialog.show(getSupportFragmentManager(), "");
        });

        //Add ClickListener to save the edited entry:
        findViewById(R.id.add_entry_button_save).setOnClickListener(view -> {
            if (!processUserInput()) {
                //Some data was not entered:
                return;
            }
            setResult(RESULT_OK, getIntent());
            AddEntryActivity.this.finish();
        });

        //Add ClickListener to add new tag:
        findViewById(R.id.add_entry_button_add_tag).setOnClickListener(view -> {
            EditTagDialog dialog = new EditTagDialog();
            dialog.show(getSupportFragmentManager(), "");
        });
    }


    /**
     * Method is called whenever the {@linkplain DialogFragment} is closed through the
     * 'positive' button (i.e. the SAVE button).
     *
     * @param dialog    Dialog which called the method.
     */
    @Override
    public void onPositiveCallback(DialogFragment dialog) {
        if (dialog instanceof DetailDialog) {
            //New detail shall be added:
            DetailDialog detailDialog = (DetailDialog)dialog;
            Detail edited = detailDialog.getDetail();
            EntryExtended entry = viewModel.getEntry();
            if (viewModel.getEntry().contains(edited.getUuid())) {
                entry.set(edited);
                adapter.notifyItemChanged(entry.getDetails().indexOf(edited));
            }
            else {
                entry.add(edited);
                adapter.notifyItemInserted(entry.getDetails().size() - 1);
            }
        }
        else if (dialog instanceof ConfirmDeleteDetailDialog) {
            ConfirmDeleteDetailDialog deleteDialog = (ConfirmDeleteDetailDialog)dialog;
            int index = viewModel.getEntry().indexOf(deleteDialog.getUuid());
            viewModel.getEntry().remove(deleteDialog.getUuid());
            if (index != -1) {
                adapter.notifyItemRemoved(index);
            }
        }
    }

    /**
     * Method is called whenever the {@linkplain DialogFragment} is closed through the
     * 'negative' button (i.e. the CANCEL button).
     *
     * @param fragment  Dialog which called the method.
     */
    @Override
    public void onNegativeCallback(DialogFragment fragment) {
        //There is no need to do anything since the edited detail shall not be saved.
        if (fragment instanceof ConfirmDeleteDetailDialog) {
            ConfirmDeleteDetailDialog dialog = (ConfirmDeleteDetailDialog)fragment;
            int index = viewModel.getEntry().indexOf(dialog.getUuid());
            if (index != -1) {
                adapter.notifyItemChanged(index);
            }
        }
        else if (fragment instanceof DetailDialog) {
            DetailDialog dialog = (DetailDialog)fragment;
            int index = viewModel.getEntry().indexOf(dialog.getDetail().getUuid());
            if (index != -1) {
                adapter.notifyItemChanged(index);
            }
        }
    }


    /**
     * Method processes the user input and saves the edited entry to {@linkplain EntryManager}.
     */
    private boolean processUserInput() {
        String name = ((TextView)findViewById(R.id.add_entry_name)).getText().toString();
        String description = ((TextView)findViewById(R.id.add_entry_description)).getText().toString();

        //Test whether all required data was entered:
        boolean somethingNotEntered = false;
        if (name.isEmpty()) {
            ((TextInputLayout)findViewById(R.id.add_entry_name_hint)).setError(getResources().getString(R.string.error_empty_input));
            somethingNotEntered = true;
        }
        else {
            ((TextInputLayout)findViewById(R.id.add_entry_name_hint)).setErrorEnabled(false);
        }
        if (somethingNotEntered) {
            return false;
        }

        //Change the edited entry:
        viewModel.getEntry().setName(name);
        viewModel.getEntry().setDescription(description);
        viewModel.getEntry().notifyDataChange();
        viewModel.getEntry().setTags(viewModel.getTags());
        EntryManager.getInstance().set(viewModel.getEntry(), viewModel.getEntry().getUuid());
        return true;
    }


    /**
     * Method initializes the UI to display the tags of the entry.
     */
    private void setupTags() {
        ChipGroup chips = findViewById(R.id.add_entry_tag_container);
        chips.removeViews(0, chips.getChildCount() - 1);
        for (Tag tag : TagManager.getInstance().getData()) {
            Chip chip = new Chip(this);
            chip.setText(tag.getName());
            chip.setCheckable(true);
            chip.setCheckedIcon(AppCompatResources.getDrawable(this, R.drawable.ic_check));
            chip.setCheckedIconVisible(true);

            for (Tag entryTag : viewModel.getTags()) {
                if (tag.equals(entryTag)) {
                    chip.setChecked(true);
                }
            }

            chip.setOnLongClickListener(view -> {
                EditTagDialog dialog = new EditTagDialog();
                Bundle args = new Bundle();
                args.putSerializable(EditTagDialog.KEY_TAG, tag);
                dialog.setArguments(args);
                dialog.show(getSupportFragmentManager(), "");
                return true;
            });

            chip.setOnClickListener(view -> {
                if (chip.isChecked()) {
                    viewModel.getTags().add(tag);
                }
                else {
                    viewModel.getTags().remove(tag);
                }
            });

            chips.addView(chip, chips.getChildCount() - 1);
        }
    }


    /**
     * Method initializes the UI to display the package of the entry.
     */
    private void setupPackage() {
        PackageCollection packages = viewModel.getEntry().getPackages();

        findViewById(R.id.add_entry_package_none).setVisibility(packages.isEmpty() ? View.VISIBLE : View.GONE);

        RecyclerView recyclerView = findViewById(R.id.add_entry_package_recycler_view);
        recyclerView.setVisibility(packages.isEmpty() ? View.GONE : View.VISIBLE);
        if (!packages.isEmpty()) {
            PackagesLogoRecyclerViewAdapter adapter = new PackagesLogoRecyclerViewAdapter(viewModel.getEntry().getPackages());
            recyclerView.setAdapter(adapter);
        }
    }


    /**
     * Method is called whenever the tags managed by {@link TagManager} are changed.
     *
     * @param o                     Observed instance whose data was changed.
     * @throws NullPointerException The passed observable is {@code null}.
     */
    @Override
    public void update(Observable<ArrayList<Tag>> o) throws NullPointerException {
        setupTags();
    }


    /**
     * Method registers the instance as observer to {@link TagManager}.
     */
    @Override
    protected void onResume() {
        super.onResume();
        TagManager.getInstance().addObserver(this);
    }

    /**
     * Method removes the instance as observer to {@link TagManager}.
     */
    @Override
    protected void onPause() {
        super.onPause();
        TagManager.getInstance().removeObserver(this);
    }

}
