package de.passwordvault.view.settings.activity_quality_gates;

import androidx.lifecycle.ViewModel;
import java.util.ArrayList;
import de.passwordvault.model.analysis.QualityGate;
import de.passwordvault.model.analysis.QualityGateManager;


/**
 * Class models a view model for the activity displaying all quality gates.
 *
 * @author  Christian-2003
 * @version 3.6.0
 */
public class QualityGatesViewModel extends ViewModel {

    /**
     * Attribute stores the default quality gates.
     */
    private ArrayList<QualityGate> defaultQualityGates;

    /**
     * Attribute stores the custom quality gates.
     */
    private ArrayList<QualityGate> customQualityGates;


    /**
     * Constructor instantiates a new view model.
     */
    public QualityGatesViewModel() {
        defaultQualityGates = null;
        customQualityGates = null;
    }


    /**
     * Method returns the default quality gates.
     *
     * @return  Default quality gates.
     */
    public ArrayList<QualityGate> getDefaultQualityGates() {
        return defaultQualityGates;
    }

    /**
     * Method returns the custom quality gates.
     *
     * @return  Custom quality gates.
     */
    public ArrayList<QualityGate> getCustomQualityGates() {
        return customQualityGates;
    }


    /**
     * Method loads all quality gates if they were not loaded previously.
     */
    public void loadQualityGatesIfRequired() {
        if (defaultQualityGates == null) {
            defaultQualityGates = new ArrayList<>();
            customQualityGates = new ArrayList<>();
            ArrayList<QualityGate> allQualityGates = QualityGateManager.getInstance().getData();
            for (QualityGate qualityGate : allQualityGates) {
                if (qualityGate.isEditable()) {
                    customQualityGates.add(qualityGate);
                }
                else {
                    defaultQualityGates.add(qualityGate);
                }
            }
        }
    }


    /**
     * Method saves all quality gates.
     */
    public void saveQualityGates() {
        if (defaultQualityGates != null && customQualityGates != null) {
            QualityGateManager.getInstance().getData().clear();
            QualityGateManager.getInstance().getData().addAll(defaultQualityGates);
            QualityGateManager.getInstance().getData().addAll(customQualityGates);
            QualityGateManager.getInstance().saveAllQualityGates();
        }
    }

}
