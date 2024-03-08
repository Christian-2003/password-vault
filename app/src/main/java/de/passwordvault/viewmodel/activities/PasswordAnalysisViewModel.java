package de.passwordvault.viewmodel.activities;

import androidx.lifecycle.ViewModel;
import de.passwordvault.model.analysis.passwords.PasswordSecurityAnalysis;


/**
 * Class implements a view model for the {@link de.passwordvault.view.activities.PasswordAnalysisActivity}.
 *
 * @author  Christian-2003
 * @version 3.4.0
 */
public class PasswordAnalysisViewModel extends ViewModel {

    /**
     * Attribute indicates whether the password security analysis has been finished.
     */
    private boolean analysisFinished;


    /**
     * Constructor instantiates a new view model.
     */
    public PasswordAnalysisViewModel() {
        analysisFinished = false;
    }


    /**
     * Method changes whether the analysis has been finished.
     *
     * @param analysisFinished  Whether the analysis has been finished.
     */
    public void setAnalysisFinished(boolean analysisFinished) {
        this.analysisFinished = analysisFinished;
    }

    /**
     * Method returns whether the analysis has been finished.
     *
     * @return  Whether the analysis has been finished.
     */
    public boolean isAnalysisFinished() {
        return analysisFinished;
    }

}
