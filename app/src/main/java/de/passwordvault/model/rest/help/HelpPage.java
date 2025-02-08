package de.passwordvault.model.rest.help;

import androidx.annotation.NonNull;
import com.google.gson.annotations.SerializedName;


/**
 * Class models a help page that is available within the response of a call to the REST-API for
 * help pages.
 *
 * @author  Christian-2003
 * @version 3.7.2
 */
public class HelpPage {

    /**
     * Attribute stores the list of localized help pages available for this page.
     */
    @SerializedName("pages")
    @NonNull
    private LocalizedHelpPage[] localizedHelpPages;


    /**
     * Constructor instantiates a new help page.
     */
    public HelpPage() {
        localizedHelpPages = new LocalizedHelpPage[0];
    }


    /**
     * Method returns the array of localized help pages.
     *
     * @return  Array of localized help pages.
     */
    @NonNull
    public LocalizedHelpPage[] getLocalizedHelpPages() {
        return localizedHelpPages;
    }

}
