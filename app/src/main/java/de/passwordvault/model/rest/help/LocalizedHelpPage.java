package de.passwordvault.model.rest.help;

import androidx.annotation.NonNull;
import com.google.gson.annotations.SerializedName;


/**
 * Class models a localized help page returned from the REST-API.
 *
 * @author  Christian-2003
 * @version 3.7.2
 */
public class LocalizedHelpPage {

    /**
     * Attribute stores the language of the help page.
     */
    @SerializedName("lang")
    @NonNull
    private String language;

    /**
     * Attribute stores the title of the help page.
     */
    @SerializedName("title")
    @NonNull
    private String title;

    /**
     * Attribute stores the URL to the help page.
     */
    @SerializedName("url")
    @NonNull
    private String url;


    /**
     * Constructor instantiates a new localized help page.
     */
    public LocalizedHelpPage() {
        language = "";
        title = "";
        url = "";
    }


    /**
     * Method returns the language of the help page.
     *
     * @return  Language of the help page.
     */
    @NonNull
    public String getLanguage() {
        return language;
    }

    /**
     * Method returns the title of the help page.
     *
     * @return  Title of the help page.
     */
    @NonNull
    public String getTitle() {
        return title;
    }

    /**
     * Method returns the URL of the help page.
     *
     * @return  URL of the help page.
     */
    @NonNull
    public String getUrl() {
        return url;
    }

}
