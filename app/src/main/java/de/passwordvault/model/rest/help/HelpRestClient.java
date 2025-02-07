package de.passwordvault.model.rest.help;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import de.passwordvault.App;
import de.passwordvault.R;
import de.passwordvault.model.rest.RestCallback;
import de.passwordvault.model.rest.RestClient;


/**
 * Class implements the REST client which can make calls to the REST API for help pages.
 *
 * @author  Christian-2003
 * @version 3.7.2
 */
public class HelpRestClient extends RestClient<HelpResponse> {

    /**
     * Field stores the URL of the REST API endpoint.
     */
    private static final String URL = "https://api.passwordvault.christian2003.de/rest/help.json";


    /**
     * Attribute stores the list of localized help pages fetched from the server.
     */
    private final ArrayList<LocalizedHelpPage> helpPages;


    /**
     * Constructor instantiates a new REST client.
     *
     * @param tag   Tag to use with the REST client.
     */
    public HelpRestClient(@Nullable String tag) {
        super(tag);
        helpPages = new ArrayList<>();
    }


    /**
     * Method returns a list of all localized help pages.
     *
     * @return  List of help pages.
     */
    @NonNull
    public ArrayList<LocalizedHelpPage> getHelpPages() {
        return helpPages;
    }


    /**
     * Method fetches the data from the REST API.
     *
     * @param callback  Callback to invoke once the client finishes fetching data.
     */
    public void fetch(@Nullable RestCallback callback) {
        super.fetch(URL, HelpResponse.class, callback, this::filterResponse);
    }


    /**
     * Method filters the deserialized response to only include the most necessary data that can
     * be displayed to the user.
     *
     * @param response  Deserialized response to filter.
     */
    private boolean filterResponse(HelpResponse response) {
        if (response == null) {
            return false;
        }

        String locale = App.getContext().getString(R.string.settings_help_locale);
        String defaultLocale = App.getContext().getString(R.string.settings_help_locale_default);
        helpPages.clear();

        for (HelpPage helpPage : response.getHelpPages()) {
            boolean pageAdded = false;

            //Find help page for locale:
            for (LocalizedHelpPage localizedHelpPage : helpPage.getLocalizedHelpPages()) {
                if (localizedHelpPage.getLanguage().equals(locale)) {
                    helpPages.add(localizedHelpPage);
                    pageAdded = true;
                    break;
                }
            }

            if (!pageAdded) {
                //Find english help page:
                for (LocalizedHelpPage localizedHelpPage : helpPage.getLocalizedHelpPages()) {
                    if (localizedHelpPage.getLanguage().equals(defaultLocale)) {
                        helpPages.add(localizedHelpPage);
                        pageAdded = true;
                        break;
                    }
                }
            }

            if (!pageAdded && helpPage.getLocalizedHelpPages().length != 0) {
                //Add first help page since no other help page is available:
                helpPages.add(helpPage.getLocalizedHelpPages()[0]);
            }
        }

        return true;
    }

}
