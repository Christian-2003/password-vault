package de.passwordvault.view.entries.activity_entry;

import androidx.annotation.Nullable;
import de.passwordvault.R;
import de.passwordvault.view.utils.components.PasswordVaultActivity;
import android.os.Bundle;
import android.widget.TextView;


/**
 * Class models the EntryActivity which is used to display all information about an
 * {@linkplain de.passwordvault.model.entry.EntryAbbreviated}.
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
     * Attribute stores the text view of the toolbar.
     */
    private TextView toolbarTextView;


    /**
     * Constructor instantiates a new activity.
     */
    public EntryActivity() {
        super(EntryViewModel.class);
    }


    /**
     * Method is called whenever the activity is created.
     *
     * @param savedInstanceState    Previously saved state of the instance.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toolbarTextView = findViewById(R.id.text_toolbar);
    }

}
