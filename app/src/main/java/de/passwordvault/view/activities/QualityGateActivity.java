package de.passwordvault.view.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.Serializable;
import java.util.Objects;

import de.passwordvault.R;
import de.passwordvault.model.analysis.QualityGate;
import de.passwordvault.view.dialogs.ConfirmDeleteDialog;
import de.passwordvault.view.utils.DialogCallbackListener;
import de.passwordvault.view.utils.components.PasswordVaultBaseActivity;
import de.passwordvault.viewmodel.activities.QualityGateViewModel;


/**
 * Class implements an activity which can add (or edit) entries.
 *
 * @author  Christian-2003
 * @version 3.2.0
 */
public class QualityGateActivity extends PasswordVaultBaseActivity implements DialogCallbackListener, Serializable {

    /**
     * Field stores the key with which to pass a quality gate to edit as serializable.
     */
    public static String KEY_QUALITY_GATE = "quality_gate";

    /**
     * Field stores the key with which to pass the index of the quality gate.
     */
    public static String KEY_INDEX = "index";


    /**
     * Attribute stores the {@linkplain androidx.lifecycle.ViewModel} of this activity.
     */
    private QualityGateViewModel viewModel;


    /**
     * Method is called whenever the activity is created / recreated.
     *
     * @param savedInstanceState    Previously saved state of the instance.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quality_gate);
        viewModel = new ViewModelProvider(this).get(QualityGateViewModel.class);

        try {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                int index = extras.getInt(KEY_INDEX, -1);
                if (index >= 0) {
                    QualityGate argument = (QualityGate)getIntent().getExtras().getSerializable(KEY_QUALITY_GATE);
                    viewModel.setQualityGate(argument);
                    viewModel.setIndexOfQualityGate(index);
                }
            }
        }
        catch (Exception e) {
            //Do nothing...
        }

        TextInputEditText descriptionEditText = findViewById(R.id.quality_gate_description);
        descriptionEditText.setText(viewModel.getQualityGate().getDescription());
        TextInputEditText regexEditText = findViewById(R.id.quality_gate_regex);
        regexEditText.setText(viewModel.getQualityGate().getRegex());
        CheckBox enabledCheckBox = findViewById(R.id.quality_gate_checkbox_enabled);
        enabledCheckBox.setChecked(viewModel.getQualityGate().isEnabled());

        ImageButton deleteButton = findViewById(R.id.button_delete);
        if (viewModel.getIndexOfQualityGate() == -1) {
            deleteButton.setVisibility(View.GONE);
        }
        else {
            deleteButton.setVisibility(View.VISIBLE);
        }
        deleteButton.setOnClickListener(view -> {
            ConfirmDeleteDialog dialog = new ConfirmDeleteDialog();
            Bundle args = new Bundle();
            args.putSerializable(ConfirmDeleteDialog.KEY_CALLBACK_LISTENER, QualityGateActivity.this);
            args.putString(ConfirmDeleteDialog.KEY_OBJECT, viewModel.getQualityGate().getDescription());
            dialog.setArguments(args);
            dialog.show(getSupportFragmentManager(), null);
        });

        findViewById(R.id.button_back).setOnClickListener(view -> finish());

        findViewById(R.id.button_save).setOnClickListener(view -> {
            if (viewModel.processUserInput(QualityGateActivity.this)) {
                finish();
            }
        });

        TextView testMatchOutput = findViewById(R.id.quality_gate_match_output);
        TextInputEditText regexTextEditText = findViewById(R.id.quality_gate_regex_test);
        regexTextEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String regex = Objects.requireNonNull(regexEditText.getText()).toString();
                if (!viewModel.isRegexValid(regex)) {
                    testMatchOutput.setText(R.string.error_invalid_regex);
                    return;
                }
                if (charSequence.toString().matches(regex)) {
                    testMatchOutput.setText(R.string.quality_gate_input_match);
                }
                else {
                    testMatchOutput.setText(R.string.quality_gate_input_no_match);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    /**
     * Method is called when a positive dialog callback is triggered.
     *
     * @param dialog    Dialog which called the method.
     */
    @Override
    public void onPositiveCallback(DialogFragment dialog) {
        viewModel.removeQualityGate();
        finish();
    }


    /**
     * Method is called when a negative dialog callback is triggered.
     *
     * @param dialog    Dialog which called the method.
     */
    @Override
    public void onNegativeCallback(DialogFragment dialog) {

    }

}
