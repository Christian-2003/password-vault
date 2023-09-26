package de.passwordvault.frontend.entry;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import de.passwordvault.R;
import de.passwordvault.backend.Singleton;
import de.passwordvault.backend.entry.Detail;
import de.passwordvault.backend.entry.Entry;
import de.passwordvault.frontend.addentry.AddEntryActivity;
import de.passwordvault.frontend.dialog.ConfirmDeleteDialogFragment;
import de.passwordvault.frontend.dialog.DialogCallbackListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;


/**
 * Class models the EntryActivity which is used to display all information about an
 * {@linkplain de.passwordvault.backend.entry.Entry}.
 *
 * @author  Christian-2003
 * @version 1.0.1
 */
public class EntryActivity extends AppCompatActivity implements DialogCallbackListener {

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
        Singleton.ENTRIES.remove(viewModel.getEntry().getUuid());
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
            Entry entry = Singleton.ENTRIES.getEntry((String)bundle.get("uuid"));
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
     * This is done to redraw this Activity with an edited / updated {@linkplain Entry}.
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
            viewModel.setEntry(Singleton.ENTRIES.getEntry(viewModel.getEntry().getUuid()));
            drawActivity(); //Redraw activity
        }
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
            ConfirmDeleteDialogFragment dialog = new ConfirmDeleteDialogFragment(viewModel.getEntry().getName());
            dialog.show(getSupportFragmentManager(), null);
        });

        //Add ClickListener to edit the entry:
        findViewById(R.id.entry_button_edit).setOnClickListener(view -> {
            Intent intent = new Intent(EntryActivity.this, AddEntryActivity.class);
            intent.putExtra("entry", viewModel.getEntry().getUuid());
            EntryActivity.this.startActivityForResult(intent, 1);
        });

        Entry entry = viewModel.getEntry();
        ((TextView)findViewById(R.id.entry_title)).setText(entry.getName());
        ((TextView)findViewById(R.id.entry_name)).setText(entry.getName());
        ((TextView)findViewById(R.id.entry_description)).setText(entry.getDescription());
        ((TextView)findViewById(R.id.entry_uuid)).setText(entry.getUuid());
        String dateFormat = getString(R.string.date_format);
        ((TextView)findViewById(R.id.entry_created)).setText(Singleton.FORMAT_DATE(entry.getCreated(), dateFormat));
        ((TextView)findViewById(R.id.entry_changed)).setText(Singleton.FORMAT_DATE(entry.getChanged(), dateFormat));

        //Add details:
        ArrayList<Detail> details = entry.getVisibleDetails();
        populateDetailsContainer(entry.getVisibleDetails(), R.id.entry_details_container);
        populateDetailsContainer(entry.getInvisibleDetails(), R.id.entry_hidden_details_container);

        showOrHideExtendedInfo();
    }


    /**
     * Method populates the specified container {@code containerId} with the passed list of
     * {@linkplain Detail} instances. If the passed {@linkplain ArrayList} of Details is empty,
     * the container and it's corresponding headline is hidden from the activity.
     *
     * @param details       List of details to be shown in the container.
     * @param containerId   ID of the container in which the passed details shall be shown.
     */
    private void populateDetailsContainer(ArrayList<Detail> details, int containerId) {
        LinearLayout detailsContainer = findViewById(containerId);
        if (details.isEmpty()) {
            detailsContainer.setVisibility(View.GONE);
            if (containerId == R.id.entry_hidden_details_container) {
                findViewById(R.id.entry_hidden_details_container_title).setVisibility(View.GONE);
            }
            else if (containerId == R.id.entry_details_container) {
                findViewById(R.id.entry_details_container_title).setVisibility(View.GONE);
            }
            return;
        }

        detailsContainer.removeAllViews();
        for (int i = 0; i < details.size(); i++) {
            Detail detail = details.get(i);
            View created = Singleton.GENERATE_DETAIL_VIEW(EntryActivity.this, detail);
            detailsContainer.addView(created);
            if (i < details.size() - 1) {
                View.inflate(EntryActivity.this, R.layout.divider_horizontal, detailsContainer);
            }
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
