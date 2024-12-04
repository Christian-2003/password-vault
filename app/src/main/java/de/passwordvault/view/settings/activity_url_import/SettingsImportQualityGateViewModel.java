package de.passwordvault.view.settings.activity_url_import;

import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;
import de.passwordvault.model.analysis.QualityGate;
import de.passwordvault.model.analysis.QualityGateManager;


/**
 * Class implements the view model for the {@link SettingsImportQualityGateActivity}.
 *
 * @author  Christian-2003
 * @version 3.7.1
 */
public class SettingsImportQualityGateViewModel extends ViewModel {

    /**
     * Attribute stores the URI entered by the user.
     */
    @Nullable
    private Uri uri;

    /**
     * Attribute stores the description of the quality gate to import.
     */
    @Nullable
    private String description;

    /**
     * Attribute stores the regex of the quality gate to import.
     */
    @Nullable
    private String regex;

    /**
     * Attribute stores the author of the quality gate to import.
     */
    @Nullable
    private String author;


    /**
     * Constructor instantiates a new view model.
     */
    public SettingsImportQualityGateViewModel() {
        uri = null;
        description = null;
        regex = null;
        author = null;
    }


    /**
     * Method sets the URI for the quality gate to import. If the URI entered is invalid (e.g. wrong
     * website or missing query parameters), an exception is thrown.
     *
     * @param uriString                 String representation of the URI.
     * @throws IllegalArgumentException The URI passed is invalid.
     */
    public void setUri(@NonNull String uriString) throws IllegalArgumentException {
        Uri uri;
        try {
            uri = Uri.parse(uriString);
        }
        catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        this.uri = uri;
        String dParam = uri.getQueryParameter("d");
        String regexParam = uri.getQueryParameter("regex");
        String refParam = uri.getQueryParameter("ref");
        if (dParam == null || dParam.isEmpty() || regexParam == null || regexParam.isEmpty() || refParam == null || refParam.isEmpty()) {
            throw new IllegalArgumentException("Missing query parameter");
        }
        description = dParam;
        regex = regexParam;
        author = refParam;
    }


    /**
     * Method returns the URI entered by the user.
     *
     * @return  URI entered by the user.
     */
    @Nullable
    public String getUri() {
        if (uri == null) {
            return null;
        }
        return uri.toString();
    }

    /**
     * Method returns the description of the quality gate to import. If no URI was set, this is
     * {@code null}.
     *
     * @return  Description of the quality gate to import.
     */
    @Nullable
    public String getDescription() {
        return description;
    }

    /**
     * Method returns the regex of the quality gate to import. If no URI was set, this is
     * {@code null}.
     *
     * @return  Regex of the quality gate to import.
     */
    @Nullable
    public String getRegex() {
        return regex;
    }

    /**
     * Method returns the author of the quality gate to import. If no URI was set, this is
     * {@code null}.
     *
     * @return  Author of the quality gate to import.
     */
    @Nullable
    public String getAuthor() {
        return author;
    }


    /**
     * Method imports the quality gate.
     *
     * @return  Whether the quality gate was imported successfully.
     */
    public boolean importQualityGate() {
        if (description == null || regex == null || author == null) {
            return false;
        }
        QualityGate qualityGate = new QualityGate(regex, description, true, true, author);
        QualityGateManager.getInstance().addQualityGate(qualityGate);
        QualityGateManager.getInstance().saveAllQualityGates();
        return true;
    }

}
