package de.passwordvault.view.settings.activity_quality_gate;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.material.textfield.TextInputEditText;
import java.io.Serializable;
import java.util.Objects;
import de.passwordvault.R;
import de.passwordvault.model.analysis.QualityGate;
import de.passwordvault.view.utils.components.PasswordVaultActivity;


/**
 * Class implements an activity which can add (or edit) entries. If the quality gate is added / edited,
 * the result code {@link #RESULT_EDITED} is used. Otherwise, {@link #RESULT_CANCELED} is used. If
 * the result code is {@code RESULT_EDITED}, an extra {@link #KEY_INDEX} is passed containing the index
 * of the edited / added detail and {@link #KEY_QUALITY_GATE} contains the edited / added quality gate.
 *
 * @author  Christian-2003
 * @version 3.6.0
 */
public class QualityGateActivity extends PasswordVaultActivity<QualityGateViewModel> implements Serializable {

    /**
     * Field stores the key with which to pass a quality gate to edit as serializable.
     */
    public static String KEY_QUALITY_GATE = "quality_gate";

    /**
     * Field stores the key with which to pass the index of the quality gate.
     */
    public static String KEY_INDEX = "index";

    /**
     * Field stores the result code for when the quality gate was edited / added.
     */
    public static int RESULT_EDITED = 69;


    /**
     * Constructor instantiates a new activity.
     */
    public QualityGateActivity() {
        super(QualityGateViewModel.class, R.layout.activity_quality_gate);
    }


    /**
     * Method is called whenever the activity is created / recreated.
     *
     * @param savedInstanceState    Previously saved state of the instance.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        CheckBox enabledCheckBox = findViewById(R.id.checkbox_enabled);
        enabledCheckBox.setChecked(viewModel.getQualityGate().isEnabled());
        LinearLayout enabledCheckBoxContainer = findViewById(R.id.checkbox_enabled_container);
        enabledCheckBoxContainer.setOnClickListener(view -> enabledCheckBox.setChecked(!enabledCheckBox.isChecked()));

        findViewById(R.id.button_back).setOnClickListener(view -> finishCancel());

        findViewById(R.id.button_save).setOnClickListener(view -> {
            if (viewModel.processUserInput(QualityGateActivity.this)) {
                finishOk();
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
     * Method finishes the activity with result code {@linkplain #RESULT_EDITED}.
     */
    private void finishOk() {
        Intent data = new Intent();
        data.putExtra(KEY_INDEX, viewModel.getIndexOfQualityGate());
        data.putExtra(KEY_QUALITY_GATE, viewModel.getQualityGate());
        setResult(RESULT_EDITED, data);
        finish();
    }

    /**
     * Method finishes the activity with result code {@linkplain #RESULT_CANCELED}.
     */
    private void finishCancel() {
        setResult(RESULT_CANCELED);
        finish();
    }

}
