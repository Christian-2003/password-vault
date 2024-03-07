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
     * Attribute stores the analysis algorithm with which the password analysis is performed.
     */
    private final PasswordSecurityAnalysis analysisAlgorithm;

    /**
     * Attribute indicates whether the password security analysis has been finished.
     */
    private boolean analysisFinished;


    /**
     * Constructor instantiates a new view model.
     */
    public PasswordAnalysisViewModel() {
        analysisAlgorithm = new PasswordSecurityAnalysis();
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

    /**
     * Method returns the instance that performed the password security analysis.
     *
     * @return  Instance which performed the password security analysis.
     */
    public PasswordSecurityAnalysis getAnalysisAlgorithm() {
        return analysisAlgorithm;
    }

}
