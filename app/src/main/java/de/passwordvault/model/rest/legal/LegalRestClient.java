package de.passwordvault.model.rest.legal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
    private static final String URL = "https://api.passwordvault.christian2003.de/rest/{arg}.json";


    @NonNull
    private final String url;

    /**
     * Attribute stores the list of localized legal pages fetched from the server.
     */
    @Nullable
    private LocalizedLegalPage page;


    /**
     * Constructor instantiates a new REST client to fetch data for the page specified.
     *
     * @param tag   Tag to use with the REST client.
     * @param page  Page name to fetch (e.g. "privacy" or "tos").
     */
    public LegalRestClient(@Nullable String tag, @NonNull String page) {
        super(tag);
        this.page = null;
        this.url = URL.replace("{arg}", page);
    }


    /**
     * Method returns a list of all localized legal pages.
     *
     * @return  List of localized legal pages.
     */
    @Nullable
    public LocalizedLegalPage getLegalPage() {
        return page;
    }


    /**
     * Method converts the data fetched to a DAO.
     *
     * @return  Legal page DTO which can be transferred in between activities.
     */
    @NonNull
    public LegalPageDto toDto() {
        return new LegalPageDto(page, response); //Both are never null when called!
    }


    /**
     * Method fetches the data from the REST API.
     *
     * @param callback  Callback to invoke once the client finishes fetching data.
     */
    public void fetch(@Nullable RestCallback callback) {
        super.fetch(url, LegalResponse.class, callback, this::filterResponse);
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

        boolean pageAdded = false;

        //Find legal page for locale:
        for (LocalizedLegalPage localizedLegalPage : response.getLegalPages()) {
            if (localizedLegalPage.getLanguage().equals(locale)) {
                page = localizedLegalPage;
                pageAdded = true;
                break;
            }
        }

        if (!pageAdded) {
            //Find english legal page:
            for (LocalizedLegalPage localizedLegalPage : response.getLegalPages()) {
                if (localizedLegalPage.getLanguage().equals(defaultLocale)) {
                    page = localizedLegalPage;
                    pageAdded = true;
                    break;
                }
            }
        }

        if (!pageAdded && response.getLegalPages().length != 0) {
            //Add first legal page since no other legal page is available:
            page = response.getLegalPages()[0];
        }

        return true;
    }

}
