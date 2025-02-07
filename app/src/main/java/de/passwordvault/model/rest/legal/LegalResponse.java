package de.passwordvault.model.rest.legal;

import androidx.annotation.NonNull;
import com.google.gson.annotations.SerializedName;


/**
 * Class models the legal response returned from the REST-API for legal pages.
 *
 * @author  Christian-2003
 * @version 3.7.2
 */
public class LegalResponse {

    /**
     * Attribute stores the version of the REST response.
     */
    @SerializedName("version")
    private int version;

    /**
     * Attribute stores the string representation of the date on which the legal page takes effect.
     */
    @SerializedName("valid")
    private String valid;

    /**
     * Attribute stores the array of localized legal pages.
     */
    @SerializedName("pages")
    @NonNull
    private LocalizedLegalPage[] legalPages;


    /**
     * Constructor instantiates a new legal response.
     */
    public LegalResponse() {
        version = -1;
        legalPages = new LocalizedLegalPage[0];
    }


    /**
     * Method returns the version of the response.
     *
     * @return  Version of the response.
     */
    public int getVersion() {
        return version;
    }


    /**
     * Method returns the array of legal pages.
     *
     * @return  Array of legal pages.
     */
    @NonNull
    public LocalizedLegalPage[] getLegalPages() {
        return legalPages;
    }

}
