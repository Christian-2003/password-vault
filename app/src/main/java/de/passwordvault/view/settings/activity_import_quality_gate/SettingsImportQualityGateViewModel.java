package de.passwordvault.view.settings.activity_import_quality_gate;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
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
     * website, missing query parameters or invalid RegEx), an exception is thrown. If an exception
     * is thrown, all attributes of the view model relating to the URI are set to {@code null}.
     *
     * @param uriString                 String representation of the URI.
     * @throws PatternSyntaxException   The RegEx encoded in the URI is invalid.
     * @throws IllegalArgumentException The URI passed is invalid.
     */
    public void setUri(@NonNull String uriString) throws PatternSyntaxException, IllegalArgumentException {
        Uri uri;
        try {
            uri = Uri.parse(uriString);
        }
        catch (Exception e) {
            resetUriAttributes();
            throw new IllegalArgumentException(e.getMessage());
        }
        this.uri = uri;
        String dParam = uri.getQueryParameter("d");
        String regexParam = uri.getQueryParameter("regex");
        String refParam = uri.getQueryParameter("ref");
        if (dParam == null || dParam.isEmpty() || regexParam == null || regexParam.isEmpty() || refParam == null || refParam.isEmpty()) {
            resetUriAttributes();
            throw new IllegalArgumentException("Missing query parameter");
        }
        try {
            Pattern.compile(regexParam);
        }
        catch (Exception e) {
            //Always pass 0 as position since I sincerely do not give a shit at which position the
            //regex is invalid:
            resetUriAttributes();
            throw new PatternSyntaxException("Invalid RegEx pattern", regexParam, 0);
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


    /**
     * Method resets the attributes of the view model. This method can be called whenever an invalid
     * URI is passed and the
     */
    private void resetUriAttributes() {
        this.uri = null;
        this.author = null;
        this.description = null;
        this.regex = null;
    }

}
