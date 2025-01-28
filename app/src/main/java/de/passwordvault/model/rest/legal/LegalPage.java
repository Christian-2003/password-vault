package de.passwordvault.model.rest.legal;

import androidx.annotation.NonNull;
import com.google.gson.annotations.SerializedName;


/**
 * Class models a legal page.
 *
 * @author  Christian-2003
 * @version 3.7.2
 */
public class LegalPage {

    /**
     * Attribute stores the version code of the legal page.
     */
    @SerializedName("version")
    private int version;

    /**
     * Attribute stores the string-representation of the date from which this legal page is valid.
     */
    @SerializedName("valid")
    @NonNull
    private String valid;

    /**
     * Attribute stores the list of localized legal pages.
     */
    @SerializedName("pages")
    @NonNull
    private LocalizedLegalPage[] pages;


    /**
     * Constructor instantiates a new legal page.
     */
    public LegalPage() {
        version = -1;
        valid = "";
        pages = new LocalizedLegalPage[0];
    }


    /**
     * Method returns the version code of the legal page.
     *
     * @return  Version code of the legal page.
     */
    public int getVersion() {
        return version;
    }

    /**
     * Method returns the string-representation of the date from which the legal page is valid.
     *
     * @return  String-representation of the date from which the legal page is valid.
     */
    @NonNull
    public String getValid() {
        return valid;
    }

    /**
     * Method returns the array of localized legal pages.
     *
     * @return  Array of localized legal pages.
     */
    @NonNull
    public LocalizedLegalPage[] getLocalizedLegalPages() {
        return pages;
    }

}
