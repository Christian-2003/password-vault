package de.passwordvault.view.settings.security.quality_gate;

import android.widget.CheckBox;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import de.passwordvault.R;
import de.passwordvault.model.analysis.QualityGate;


/**
 * Class implements a view model for {@link QualityGateActivity}.
 *
 * @author  Christian-2003
 * @version 3.6.0
 */
public class QualityGateViewModel extends ViewModel {

    /**
     * Attribute stores the index of the quality gate being edited within {@link de.passwordvault.model.analysis.QualityGateManager}.
     * If this is {@code -1}, a new quality gate is being created.
     */
    private int indexOfQualityGate;

    /**
     * Attribute stores the quality gate that is being edited.
     */
    private QualityGate qualityGate;


    /**
     * Constructor instantiates a new QualityGateViewModel.
     */
    public QualityGateViewModel() {
        qualityGate = new QualityGate();
        indexOfQualityGate = -1;
    }


    /**
     * Method returns the index of the quality gate.
     *
     * @return  Index of the quality gate.
     */
    public int getIndexOfQualityGate() {
        return indexOfQualityGate;
    }

    /**
     * Method changes the index of the quality gate to the passed argument.
     *
     * @param indexOfQualityGate    New index of the quality gate.
     */
    public void setIndexOfQualityGate(int indexOfQualityGate) {
        this.indexOfQualityGate = indexOfQualityGate;
    }

    /**
     * Method returns the quality gate which is being edited.
     *
     * @return  Quality gate being edited.
     */
    public QualityGate getQualityGate() {
        return qualityGate;
    }

    /**
     * Method changes the quality gate which is being edited.
     *
     * @param qualityGate           Quality gate being edited.
     * @throws NullPointerException The passed argument is {@code null}.
     */
    public void setQualityGate(QualityGate qualityGate) throws NullPointerException {
        if (qualityGate == null) {
            throw new NullPointerException();
        }
        this.qualityGate = qualityGate;
    }


    /**
     * Method processes the user inputs and applies them to the edited quality gate. If some
     * inputs are incorrect, the visuals of the activity are changed to inform the user about the
     * incorrect inputs and {@code false} is returned. If all inputs are correct and changes were
     * successfully applied, {@code true} is returned.
     *
     * @param activity  Activity from which to retrieve the user inputs.
     * @return          Whether the inputs were successfully applied to the edited quality gate.
     */
    public boolean processUserInput(AppCompatActivity activity) {
        if (activity == null) {
            return false;
        }

        TextInputEditText descriptionEditText = activity.findViewById(R.id.quality_gate_description);
        TextInputLayout descriptionLayout = activity.findViewById(R.id.quality_gate_description_hint);
        String description = Objects.requireNonNull(descriptionEditText.getText()).toString();
        TextInputEditText regexEditText = activity.findViewById(R.id.quality_gate_regex);
        TextInputLayout regexLayout = activity.findViewById(R.id.quality_gate_regex_hint);
        String regex = Objects.requireNonNull(regexEditText.getText()).toString();

        boolean everythingEntered = true;
        if (description.isEmpty()) {
            descriptionLayout.setError(activity.getString(R.string.error_empty_input));
            everythingEntered = false;
        }
        else {
            descriptionLayout.setErrorEnabled(false);
        }
        if (regex.isEmpty()) {
            regexLayout.setError(activity.getString(R.string.error_empty_input));
            everythingEntered = false;
        }
        else if (!isRegexValid(regex)) {
            regexLayout.setError(activity.getString(R.string.error_invalid_regex));
            everythingEntered = false;
        }
        else {
            regexLayout.setErrorEnabled(false);
        }

        if (!everythingEntered) {
            return false;
        }

        CheckBox enabledCheckBox = activity.findViewById(R.id.quality_gate_checkbox_enabled);
        qualityGate.setDescription(description);
        qualityGate.setRegex(regex);
        qualityGate.setEnabled(enabledCheckBox.isChecked());

        return true;
    }


    /**
     * Method tests whether the syntax of the passed RegEx is valid.
     *
     * @param regex RegEx whose syntax to test.
     * @return      Whether the syntax is valid.
     */
    public boolean isRegexValid(String regex) {
        try {
            Pattern.compile(regex);
            return true;
        }
        catch (PatternSyntaxException e) {
            return false;
        }
    }

}
