package de.passwordvault.view.settings.activity_quality_gates;

import android.os.Build;
import android.provider.Settings;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import de.passwordvault.App;
import de.passwordvault.R;
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


    /**
     * Method creates the URL to share the specified quality gate. If the URL cannot be created,
     * {@code null} is returned.
     *
     * @param qualityGate   Quality gate to share.
     * @return              URL to share the quality gate.
     */
    @Nullable
    public String createShareUrl(QualityGate qualityGate) {
        try {
            String url = App.getContext().getString(R.string.url_quality_gate);
            url = url.replace("{description}", URLEncoder.encode(qualityGate.getDescription(), "UTF-8"));
            url = url.replace("{regex}", URLEncoder.encode(qualityGate.getRegex(), "UTF-8"));
            if (qualityGate.getAuthor() == null) {
                url = url.replace("{author}", URLEncoder.encode(Settings.Global.getString(App.getContext().getContentResolver(), "device_name"), "UTF-8"));
            }
            else {
                url = url.replace("{author}", URLEncoder.encode(qualityGate.getAuthor(), "UTF-8"));
            }
            return url;
        }
        catch (IOException e) {
            return null;
        }
    }

}
