package de.passwordvault.model.rest.legal;

import androidx.annotation.NonNull;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;


/**
 * Class models a localized legal page returned from the REST-API.
 *
 * @author  Christian-2003
 * @version 3.7.2
 */
public class LocalizedLegalPage implements Serializable {

    /**
     * Attribute stores the language of the legal page.
     */
    @SerializedName("lang")
    @NonNull
    private String language;

    /**
     * Attribute stores the title of the legal page.
     */
    @SerializedName("title")
    @NonNull
    private String title;

    /**
     * Attribute stores the URL to the legal page.
     */
    @SerializedName("url")
    @NonNull
    private String url;


    /**
     * Constructor instantiates a new localized legal page.
     */
    public LocalizedLegalPage() {
        language = "";
        title = "";
        url = "";
    }


    /**
     * Method returns the language of the legal page.
     *
     * @return  Language of the legal page.
     */
    @NonNull
    public String getLanguage() {
        return language;
    }

    /**
     * Method returns the title of the legal page.
     *
     * @return  Title of the legal page.
     */
    @NonNull
    public String getTitle() {
        return title;
    }

    /**
     * Method returns the URL of the legal page.
     *
     * @return  URL of the legal page.
     */
    @NonNull
    public String getUrl() {
        return url;
    }

}
