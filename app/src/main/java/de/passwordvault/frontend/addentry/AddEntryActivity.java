package de.passwordvault.frontend.addentry;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.passwordvault.R;
import de.passwordvault.backend.Singleton;
import de.passwordvault.backend.entry.Detail;
import de.passwordvault.backend.entry.Entry;
import de.passwordvault.frontend.dialog.ConfirmDeleteDetailDialogFragment;
import de.passwordvault.frontend.dialog.DetailDialogFragment;
import de.passwordvault.frontend.dialog.DialogCallbackListener;


/**
 * Class implements an activity which can add (or edit) entries.
 *
 * @author  Christian-2003
 * @version 1.0.1
 */
public class AddEntryActivity extends AppCompatActivity implements DialogCallbackListener {

    /**
     * Attribute stores the {@linkplain androidx.lifecycle.ViewModel} of this activity.
     */
    private AddEntryViewModel viewModel;

    /**
     * Attribute stores the container which displays the details.
     */
    private LinearLayout detailsContainer;


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

        detailsContainer = findViewById(R.id.add_entry_details_container);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("entry")) {
            //Activity shall be used to edit an entry:
            if (viewModel.getEntry() == null) {
                viewModel.setEntry(new Entry(Singleton.ENTRIES.getEntry(bundle.getString("entry"))));
            }
            ((TextView)findViewById(R.id.add_entry_title)).setText(viewModel.getEntry().getName());
            ((TextView)findViewById(R.id.add_entry_name)).setText(viewModel.getEntry().getName());
            ((TextView)findViewById(R.id.add_entry_description)).setText(viewModel.getEntry().getDescription());
            for (Detail detail : viewModel.getEntry().getDetails()) {
                addDetailToContainer(detail);
            }
        }
        else if (viewModel.getEntry() == null){
            //Activity shall be used to create a new entry:
            viewModel.setEntry(new Entry());
        }

        //Add ClickListeners to close the activity:
        findViewById(R.id.add_entry_button_back).setOnClickListener(view -> AddEntryActivity.this.finish());
        findViewById(R.id.add_entry_button_cancel).setOnClickListener(view -> {
            setResult(RESULT_CANCELED, getIntent());
            AddEntryActivity.this.finish();
        });

        //Add ClickListener to add new detail:
        findViewById(R.id.add_entry_button_add_detail).setOnClickListener(view -> {
            DetailDialogFragment dialog = new DetailDialogFragment(null);
            dialog.show(getSupportFragmentManager(), "");
        });

        //Add ClickListener to save the edited entry:
        findViewById(R.id.add_entry_button_save).setOnClickListener(view -> {
            processUserInput();
            setResult(RESULT_OK, getIntent());
            AddEntryActivity.this.finish();
        });
    }


    /**
     * Method adds the passed {@linkplain Detail} to the {@linkplain #detailsContainer}.
     *
     * @param detail    Detail which shall be added to the container.
     */
    private void addDetailToContainer(Detail detail) {
        //View created = Singleton.GENERATE_DETAIL_VIEW(AddEntryActivity.this, detail);
        DetailViewBuilder builder = new DetailViewBuilder(this, detail);
        View created = builder.inflate();

        ImageButton deleteButton = created.findViewById(R.id.entry_detail_delete_button);
        deleteButton.setVisibility(View.VISIBLE);
        deleteButton.setOnClickListener(view -> {
            ConfirmDeleteDetailDialogFragment dialog = new ConfirmDeleteDetailDialogFragment(detail);
            dialog.show(getSupportFragmentManager(), null);
        });
        ImageButton editButton = created.findViewById(R.id.entry_detail_edit_button);
        editButton.setVisibility(View.VISIBLE);
        editButton.setOnClickListener(view -> {
            DetailDialogFragment dialog = new DetailDialogFragment(detail);
            dialog.show(getSupportFragmentManager(), "");
        });

        detailsContainer.addView(created);
        View.inflate(AddEntryActivity.this, R.layout.divider_horizontal, detailsContainer);
    }


    /**
     * Method is called whenever the {@linkplain DialogFragment} is closed through the
     * 'positive' button (i.e. the SAVE button).
     *
     * @param dialog    Dialog which called the method.
     */
    public void onPositiveCallback(DialogFragment dialog) {
        if (dialog instanceof DetailDialogFragment) {
            //New detail shall be added:
            DetailDialogFragment detailDialog = (DetailDialogFragment)dialog;
            Detail edited = detailDialog.getDetail();
            Entry entry = viewModel.getEntry();
            if (viewModel.getEntry().contains(edited.getUuid())) {
                entry.set(edited);
            }
            else {
                entry.add(edited);
            }
            viewModel.setEntry(entry);
        }
        else if (dialog instanceof ConfirmDeleteDetailDialogFragment) {
            ConfirmDeleteDetailDialogFragment deleteDialog = (ConfirmDeleteDetailDialogFragment)dialog;
            Entry entry = viewModel.getEntry();
            entry.remove(deleteDialog.getUuid());
            viewModel.setEntry(entry);
        }
        //Redraw detailContainer:
        detailsContainer.removeAllViews();
        for (Detail detail : viewModel.getEntry().getDetails()) {
            addDetailToContainer(detail);
        }
    }

    /**
     * Method is called whenever the {@linkplain DialogFragment} is closed through the
     * 'negative' button (i.e. the CANCEL button).
     *
     * @param dialog    Dialog which called the method.
     */
    public void onNegativeCallback(DialogFragment dialog) {
        //There is no need to do anything since the edited detail shall not be saved.
    }


    /**
     * Method processes the user input and saves the edited entry to {@linkplain Singleton#ENTRIES}.
     */
    private void processUserInput() {
        String name = ((TextView)findViewById(R.id.add_entry_name)).getText().toString();
        String description = ((TextView)findViewById(R.id.add_entry_description)).getText().toString();
        viewModel.getEntry().setName(name);
        viewModel.getEntry().setDescription(description);
        viewModel.getEntry().notifyDataChange();
        Singleton.ENTRIES.set(viewModel.getEntry());
    }

}
