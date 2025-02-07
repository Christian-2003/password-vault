package de.passwordvault.view.settings.activity_about;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;
import de.passwordvault.model.rest.RestCallback;
import de.passwordvault.model.rest.RestError;
import de.passwordvault.model.rest.legal.LegalRestClient;
import de.passwordvault.model.rest.legal.LocalizedLegalPage;


/**
 * Class implements the view model for the {@link SettingsAboutActivity}.
 *
 * @author  Christian-2003
 * @version 3.7.2
 */
public class SettingsAboutViewModel extends ViewModel {

    /**
     * Attribute stores the tag used with the REST client fetching the privacy policy.
     */
    public static final String TAG_PRIVACY = "privacy";

    /**
     * Attribute stores the tag used with the REST client fetching the terms of service.
     */
    public static final String TAG_TOS = "tos";


    /**
     * Attribute stores the REST client fetching data about the privacy policy.
     */
    @NonNull
    private final LegalRestClient privacyRestClient;

    /**
     * Attribute stores the REST client fetching data about the terms of service.
     */
    @NonNull
    private final LegalRestClient tosRestClient;

    /**
     * Attribute stores the REST error generated when fetching the privacy policy.
     */
    @Nullable
    private RestError privacyError;

    /**
     * Attribute stores the REST error generated when fetching the terms of service.
     */
    @Nullable
    private RestError tosError;


    /**
     * Constructor instantiates a new view model.
     */
    public SettingsAboutViewModel() {
        privacyRestClient = new LegalRestClient(TAG_PRIVACY, "privacy");
        tosRestClient = new LegalRestClient(TAG_TOS, "tos");
        privacyError = null;
        tosError = null;
    }


    /**
     * Method returns the error generated while fetching the privacy policy. This is {@code null}
     * before the REST client finished fetching data.
     *
     * @return  Error generated.
     */
    @Nullable
    public RestError getPrivacyError() {
        return privacyError;
    }

    /**
     * Method returns the error generated while fetching the terms of service. This is {@code null}
     * before the REST client finished fetching data.
     *
     * @return  Error generated.
     */
    @Nullable
    public RestError getTosError() {
        return tosError;
    }

    /**
     * Method changes the error generated while fetching the privacy policy.
     *
     * @param privacyError  New error generated while fetching.
     */
    public void setPrivacyError(@NonNull RestError privacyError) {
        this.privacyError = privacyError;
    }

    /**
     * Method changes the error generated while fetching the terms of service.
     *
     * @param tosError  New error generated while fetching.
     */
    public void setTosError(@NonNull RestError tosError) {
        this.tosError = tosError;
    }

    @Nullable
    public LocalizedLegalPage getPrivacyPage() {
        return privacyRestClient.getLegalPage();
    }

    @Nullable
    public LocalizedLegalPage getTosPage() {
        return tosRestClient.getLegalPage();
    }

    /**
     * Method begins fetching data from the REST API.
     *
     * @param callback  Callback to invoke once the REST client finishes fetching.
     */
    public void fetchData(@Nullable RestCallback callback) {
        privacyRestClient.fetch(callback);
        tosRestClient.fetch(callback);
    }

}
