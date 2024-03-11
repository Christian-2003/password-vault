package de.passwordvault.view.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import de.passwordvault.R;
import de.passwordvault.model.detail.Detail;
import de.passwordvault.model.entry.EntryExtended;
import de.passwordvault.model.entry.EntryManager;
import de.passwordvault.model.packages.PackageCollection;
import de.passwordvault.model.tags.Tag;
import de.passwordvault.model.tags.TagCollection;
import de.passwordvault.view.utils.DetailsItemMoveCallback;
import de.passwordvault.view.utils.adapters.DetailsRecyclerViewAdapter;
import de.passwordvault.view.utils.OnRecyclerItemClickListener;
import de.passwordvault.view.utils.Utils;
import de.passwordvault.view.utils.adapters.PackagesLogoRecyclerViewAdapter;
import de.passwordvault.viewmodel.activities.EntryViewModel;
import de.passwordvault.view.dialogs.ConfirmDeleteDialog;
import de.passwordvault.view.utils.DialogCallbackListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import java.io.Serializable;


/**
 * Class models the EntryActivity which is used to display all information about an
 * {@linkplain de.passwordvault.model.entry.EntryAbbreviated}.
 *
 * @author  Christian-2003
 * @version 3.4.0
 */
public class EntryActivity extends AppCompatActivity implements DialogCallbackListener, Serializable, OnRecyclerItemClickListener<Detail> {

    /**
     * Attribute stores the {@linkplain androidx.lifecycle.ViewModel} for the EntryActivity.
     */
    private EntryViewModel viewModel;


    /**
     * Method is called whenever the {@linkplain DialogFragment} is closed through the
     * 'positive' button (i.e. the SAVE button).
     *
     * @param dialog    Dialog which called the method.
     */
    public void onPositiveCallback(DialogFragment dialog) {
        //This entry shall be deleted:
        EntryManager.getInstance().remove(viewModel.getEntry().getUuid());
        EntryActivity.this.finish();
    }

    /**
     * Method is called whenever the {@linkplain DialogFragment} is closed through the
     * 'negative' button (i.e. the CANCEL button).
     *
     * @param dialog    Dialog which called the method.
     */
    public void onNegativeCallback(DialogFragment dialog) {

    }


    /**
     * Method is called whenever the activity is created / recreated.
     *
     * @param savedInstanceState    Previously saved state of the instance.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        viewModel = new ViewModelProvider(this).get(EntryViewModel.class);
        if (viewModel.getEntry() == null) {
            Bundle bundle = getIntent().getExtras();
            EntryExtended entry = EntryManager.getInstance().get((String)bundle.get("uuid"));
            if (entry == null) {
                Toast.makeText(getApplicationContext(), "Entry '"+ (String)bundle.get("uuid") + "' does not exist.", Toast.LENGTH_SHORT).show();
                finish(); //Close activity.
            }
            viewModel.setEntry(entry);
        }

        drawActivity();
    }


    /**
     * Method is called whenever any {@linkplain android.app.Activity} that was started by this
     * Activity through {@linkplain #startActivityForResult(Intent, int, Bundle)} has finished.
     * This is done to redraw this Activity with an edited / updated {@linkplain EntryExtended}.
     *
     * @param requestCode   Code that was requested from the started Activity.
     * @param resultCode    Result code from the started Activity.
     * @param data          Supplied data.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == 1) {
            viewModel.setEntry(EntryManager.getInstance().get(viewModel.getEntry().getUuid()));
            drawActivity(); //Redraw activity
        }
    }


    /**
     * Method is called when the item which is passed as argument is clicked by the user.
     *
     * @param item      Clicked item.
     * @param position  Index of the clicked item.
     */
    @Override
    public void onItemClick(Detail item, int position) {
        Utils.copyToClipboard(item.getContent());
    }


    /**
     * Method draws this {@linkplain android.app.Activity}.
     */
    private void drawActivity() {
        //Add ClickListener to exit the activity:
        findViewById(R.id.entry_back_button).setOnClickListener(view -> EntryActivity.this.finish());

        //Add ClickListener to show / hide the extended information:
        findViewById(R.id.entry_button_show_more).setOnClickListener(view -> {
            viewModel.setExtendedInfoShown(!viewModel.isExtendedInfoShown());
            showOrHideExtendedInfo();
        });

        //Add ClickListener to delete the entry:
        findViewById(R.id.entry_button_delete).setOnClickListener(view -> {
            ConfirmDeleteDialog dialog = new ConfirmDeleteDialog();
            Bundle dialogArgs = new Bundle();
            dialogArgs.putString(ConfirmDeleteDialog.KEY_OBJECT, viewModel.getEntry().getName());
            dialogArgs.putSerializable(ConfirmDeleteDialog.KEY_CALLBACK_LISTENER, EntryActivity.this);
            dialog.setArguments(dialogArgs);
            dialog.show(getSupportFragmentManager(), null);
        });

        //Add ClickListener to edit the entry:
        findViewById(R.id.entry_button_edit).setOnClickListener(view -> {
            Intent intent = new Intent(EntryActivity.this, AddEntryActivity.class);
            intent.putExtra("entry", viewModel.getEntry().getUuid());
            EntryActivity.this.startActivityForResult(intent, 1);
        });

        EntryExtended entry = viewModel.getEntry();
        if (entry == null) {
            finish();
            return;
        }
        ((TextView)findViewById(R.id.entry_title)).setText(entry.getName());
        ((TextView)findViewById(R.id.entry_name)).setText(entry.getName());
        if (entry.getDescription().isEmpty()) {
            findViewById(R.id.entry_description_container).setVisibility(View.GONE);
        }
        else {
            findViewById(R.id.entry_description_container).setVisibility(View.VISIBLE);
        }
        ((TextView)findViewById(R.id.entry_description)).setText(entry.getDescription());
        String dateFormat = getString(R.string.date_format);
        ((TextView)findViewById(R.id.entry_created)).setText(Utils.formatDate(entry.getCreated(), dateFormat));
        ((TextView)findViewById(R.id.entry_changed)).setText(Utils.formatDate(entry.getChanged(), dateFormat));

        //Add tags:
        populateTagsContainer(entry.getTags());

        //Add packages:
        populatePackagesContainer(entry.getPackages());

        //Add visible details:
        RecyclerView visibleDetailsContainer = findViewById(R.id.entry_details_container);
        DetailsRecyclerViewAdapter visibleDetailsAdapter = new DetailsRecyclerViewAdapter(viewModel.getEntry().getVisibleDetails(), this, this, this);
        ItemTouchHelper.Callback visibleDetailsCallback = new DetailsItemMoveCallback(visibleDetailsAdapter, false, false);
        ItemTouchHelper visibleDetailsTouchHelper = new ItemTouchHelper(visibleDetailsCallback);
        visibleDetailsTouchHelper.attachToRecyclerView(visibleDetailsContainer);
        visibleDetailsContainer.setAdapter(visibleDetailsAdapter);

        //Add hidden details:
        RecyclerView hiddenDetailsContainer = findViewById(R.id.entry_hidden_details_container);
        DetailsRecyclerViewAdapter hiddenDetailsAdapter = new DetailsRecyclerViewAdapter(viewModel.getEntry().getInvisibleDetails(), this, this, this);
        ItemTouchHelper.Callback hiddenDetailsCallback = new DetailsItemMoveCallback(hiddenDetailsAdapter, false, false);
        ItemTouchHelper hiddenDetailsTouchHelper = new ItemTouchHelper(hiddenDetailsCallback);
        hiddenDetailsTouchHelper.attachToRecyclerView(hiddenDetailsContainer);
        hiddenDetailsContainer.setAdapter(hiddenDetailsAdapter);

        //Optionally show hidden details:
        if (viewModel.getEntry().getInvisibleDetails().isEmpty()) {
            findViewById(R.id.entry_hidden_details_container_title).setVisibility(View.GONE);
            hiddenDetailsContainer.setVisibility(View.GONE);
        }
        else {
            findViewById(R.id.entry_hidden_details_container_title).setVisibility(View.VISIBLE);
            hiddenDetailsContainer.setVisibility(View.VISIBLE);
        }

        showOrHideExtendedInfo();
    }


    /**
     * Method populates the tag container with the tags within the specified tag collection.
     *
     * @param collection    Collection with whose tags to populate the tag container.
     */
    private void populateTagsContainer(TagCollection collection) {
        if (collection.isEmpty()) {
            findViewById(R.id.entry_tag_group).setVisibility(View.GONE);
        }
        else {
            findViewById(R.id.entry_tag_group).setVisibility(View.VISIBLE);
            ChipGroup chips = findViewById(R.id.entry_tag_container);
            chips.removeAllViews();
            for (Tag tag : collection) {
                Chip chip = new Chip(this);
                chip.setText(tag.getName());
                chips.addView(chip);
            }
        }
    }


    private void populatePackagesContainer(PackageCollection packages) {
        if (packages.isEmpty()) {
            findViewById(R.id.entry_packages_container).setVisibility(View.GONE);
        }
        else {
            findViewById(R.id.entry_packages_container).setVisibility(View.VISIBLE);
            RecyclerView recyclerView = findViewById(R.id.entry_packages_recycler_view);
            PackagesLogoRecyclerViewAdapter adapter = new PackagesLogoRecyclerViewAdapter(packages);
            recyclerView.setAdapter(adapter);
        }
    }


    /**
     * Method shows or hides the extended info (content of the container {@code R.id.entry_additional_info_container})
     * based on the state of {@linkplain EntryViewModel#isExtendedInfoShown()}.
     */
    private void showOrHideExtendedInfo() {
        if (viewModel.isExtendedInfoShown()) {
            ((Button)findViewById(R.id.entry_button_show_more)).setText(getString(R.string.button_show_less));
            EntryActivity.this.findViewById(R.id.entry_additional_info_container).setVisibility(View.VISIBLE);
        }
        else {
            ((Button)findViewById(R.id.entry_button_show_more)).setText(getString(R.string.button_show_more));
            EntryActivity.this.findViewById(R.id.entry_additional_info_container).setVisibility(View.GONE);
        }
    }

}
