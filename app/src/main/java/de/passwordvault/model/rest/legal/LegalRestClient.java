package de.passwordvault.model.rest.legal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import de.passwordvault.App;
import de.passwordvault.R;
import de.passwordvault.model.rest.RestCallback;
import de.passwordvault.model.rest.RestClient;


/**
 * Class implements the REST client which can make calls to the REST API for legal pages.
 *
 * @author  Christian-2003
 * @version 3.7.2
 */
public class LegalRestClient extends RestClient<LegalResponse> {

    /**
     * Field stores the URL of the REST API endpoint.
     */
    private static final String URL = "https://api.passwordvault.christian2003.de/rest/legal.json";


    /**
     * Attribute stores the list of localized legal pages fetched from the server.
     */
    private final ArrayList<LocalizedLegalPage> legalPages;


    /**
     * Constructor instantiates a new REST client.
     */
    public LegalRestClient() {
        legalPages = new ArrayList<>();
    }


    /**
     * Method returns a list of all localized legal pages.
     *
     * @return  List of localized legal pages.
     */
    @NonNull
    public ArrayList<LocalizedLegalPage> getLegalPages() {
        return legalPages;
    }


    /**
     * Method fetches the data from the REST API.
     *
     * @param callback  Callback to invoke once the client finishes fetching data.
     */
    public void fetch(@Nullable RestCallback callback) {
        super.fetch(URL, LegalResponse.class, callback, this::filterResponse);
    }


    /**
     * Method filters the deserialized response to only include the most necessary data that can
     * be displayed to the user.
     *
     * @param response  Deserialized response to filter.
     */
    private boolean filterResponse(LegalResponse response) {
        if (response == null) {
            return false;
        }

        String locale = App.getContext().getString(R.string.settings_help_locale);
        String defaultLocale = App.getContext().getString(R.string.settings_help_locale_default);
        legalPages.clear();

        for (LegalPage legalPage : response.getLegalPages()) {
            boolean pageAdded = false;

            //Find legal page for locale:
            for (LocalizedLegalPage localizedLegalPage : legalPage.getLocalizedLegalPages()) {
                if (localizedLegalPage.getLanguage().equals(locale)) {
                    legalPages.add(localizedLegalPage);
                    pageAdded = true;
                    break;
                }
            }

            if (!pageAdded) {
                //Find english legal page:
                for (LocalizedLegalPage localizedLegalPage : legalPage.getLocalizedLegalPages()) {
                    if (localizedLegalPage.getLanguage().equals(defaultLocale)) {
                        legalPages.add(localizedLegalPage);
                        pageAdded = true;
                        break;
                    }
                }
            }

            if (!pageAdded && legalPage.getLocalizedLegalPages().length != 0) {
                //Add first legal page since no other legal page is available:
                legalPages.add(legalPage.getLocalizedLegalPages()[0]);
            }
        }

        return true;
    }

}
