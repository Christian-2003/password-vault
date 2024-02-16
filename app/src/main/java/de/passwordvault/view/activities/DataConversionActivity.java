package de.passwordvault.view.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import de.passwordvault.BuildConfig;
import de.passwordvault.R;
import de.passwordvault.model.storage.Configuration;
import de.passwordvault.model.storage.app.ConverterException;
import de.passwordvault.viewmodel.activities.DataConversionViewModel;


/**
 * Class implements the activity through which data conversion happens after an update.
 *
 * @author  Christian-2003
 * @version 3.3.0
 */
public class DataConversionActivity extends AppCompatActivity {

    /**
     * Attribute stores the view model of the activity.
     */
    private DataConversionViewModel viewModel;


    /**
     * Method is called whenever the activity is created.
     *
     * @param savedInstanceState    Previously saved state of the activity.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_conversion);
        viewModel = new ViewModelProvider(this).get(DataConversionViewModel.class);

        TextView versionLabel = findViewById(R.id.data_conversion_version_label);
        versionLabel.setText(getString(R.string.data_conversion_subline).replace("{version}", BuildConfig.VERSION_NAME));

        findViewById(R.id.data_conversion_cancel).setOnClickListener(view -> finish());
        findViewById(R.id.data_conversion_continue_anyway).setOnClickListener(view -> continueToMainActivity());

        try {
            viewModel.convertData();
            continueToMainActivity();
        }
        catch (ConverterException e) {
            AlertDialog dialog = new MaterialAlertDialogBuilder(this).create();
            dialog.setTitle(getString(R.string.data_conversion_error));
            dialog.setMessage(e.getMessage());
            dialog.setButton(DialogInterface.BUTTON_NEUTRAL, getString(R.string.button_ok), (dialogInterface, i) -> dialogInterface.dismiss());
            dialog.show();
        }
    }


    /**
     * Method opens the {@link MainActivity}.
     */
    private void continueToMainActivity() {
        Configuration.setConvertedStorage1(true);

        Intent intent = new Intent(DataConversionActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        startActivity(intent);
    }

}
