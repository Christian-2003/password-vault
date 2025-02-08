package de.passwordvault.model.rest.help;

import androidx.annotation.NonNull;
import com.google.gson.annotations.SerializedName;


/**
 * Class models the help response returned from the REST-API for help pages.
 *
 * @author  Christian-2003
 * @version 3.7.2
 */
public class HelpResponse {

    /**
     * Attribute stores the version of the REST response.
     */
    @SerializedName("version")
    private int version;

    /**
     * Attribute stores the array of help pages.
     */
    @SerializedName("tutorials")
    @NonNull
    private HelpPage[] helpPages;


    /**
     * Constructor instantiates a new help response.
     */
    public HelpResponse() {
        version = -1;
        helpPages = new HelpPage[0];
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
     * Method returns the array of help pages.
     *
     * @return  Array of help pages.
     */
    @NonNull
    public HelpPage[] getHelpPages() {
        return helpPages;
    }

}
