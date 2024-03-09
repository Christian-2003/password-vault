package de.passwordvault.view.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
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
import de.passwordvault.model.packages.PackagesManager;
import de.passwordvault.model.tags.Tag;
import de.passwordvault.model.tags.TagCollection;
import de.passwordvault.model.tags.TagManager;
import de.passwordvault.view.dialogs.EditTagDialog;
import de.passwordvault.view.utils.ActivityCallbackListener;
import de.passwordvault.view.utils.DetailsItemMoveCallback;
import de.passwordvault.view.utils.adapters.DetailsRecyclerViewAdapter;
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
public class AddEntryActivity extends AppCompatActivity implements DialogCallbackListener, ActivityCallbackListener, Observer<ArrayList<Tag>> {

    /**
     * Attribute stores the {@linkplain androidx.lifecycle.ViewModel} of this activity.
     */
    private AddEntryViewModel viewModel;

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

        findViewById(R.id.add_entry_package_edit_button).setOnClickListener(view -> {
            Intent intent = new Intent(AddEntryActivity.this, PackagesActivity.class);
            intent.putExtra(PackagesActivity.KEY_CALLBACK_LISTENER, AddEntryActivity.this);
            if (AddEntryActivity.this.viewModel.getEntry().getAnchorPackageName() != null) {
                intent.putExtra(PackagesActivity.KEY_PACKAGE, AddEntryActivity.this.viewModel.getEntry().getAnchorPackageName());
            }
            AddEntryActivity.this.startActivity(intent);
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
     * Method is called when the launched activity has finished with a positive result.
     *
     * @param activity  Activity which finished positively.
     */
    @Override
    public void onPositiveCallback(AppCompatActivity activity) {
        if (activity instanceof PackagesActivity) {
            PackagesActivity packagesActivity = (PackagesActivity)activity;
            viewModel.getEntry().setAnchorPackageName(packagesActivity.getSelectedPackageName());
            setupPackage();
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
     * Method is called when the launched activity has finished with a negative result.
     *
     * @param activity  Activity which finished negatively.
     */
    @Override
    public void onNegativeCallback(AppCompatActivity activity) {

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
        LinearLayout selectedPackage = findViewById(R.id.add_entry_package_selected);
        selectedPackage.setVisibility(viewModel.getEntry().getAnchorPackageName() == null ? View.GONE : View.VISIBLE);
        findViewById(R.id.add_entry_package_none).setVisibility(viewModel.getEntry().getAnchorPackageName() == null ? View.VISIBLE : View.GONE);
        if (viewModel.getEntry().getAnchorPackageName() != null) {
            Package p = PackagesManager.getInstance().getPackage(viewModel.getEntry().getAnchorPackageName());
            if (p == null) {
                ((TextView)selectedPackage.findViewById(R.id.list_item_package_name)).setText(viewModel.getEntry().getAnchorPackageName());
                return;
            }
            ((TextView)selectedPackage.findViewById(R.id.list_item_package_name)).setText(p.getAppName());
            if (p.getLogo() != null) {
                ((ShapeableImageView)selectedPackage.findViewById(R.id.list_item_package_logo)).setImageDrawable(p.getLogo());
            }
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
