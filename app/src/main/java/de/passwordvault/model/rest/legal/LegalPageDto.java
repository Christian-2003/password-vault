package de.passwordvault.model.rest.legal;

import androidx.annotation.NonNull;
import java.io.Serializable;
import java.util.Calendar;


/**
 * Class models a legal page that contains all important information about a legal page. Instances
 * of this class are intended to be passed to {@link de.passwordvault.view.activity_legal.LegalActivity}.
 *
 * @author  Christian-2003
 * @version 3.7.2
 */
public class LegalPageDto implements Serializable {

    /**
     * Attribute stores the localized legal page.
     */
    @NonNull
    private final LocalizedLegalPage page;

    /**
     * Attribute stores the date on which the legal page takes effect.
     */
    @NonNull
    private final Calendar date;

    /**
     * Attribute stores the version code of the page.
     */
    private final int version;


    /**
     * Constructor instantiates a new legal page.
     *
     * @param page      Localized legal page.
     * @param response  Response from which to create the page.
     */
    public LegalPageDto(@NonNull LocalizedLegalPage page, @NonNull LegalResponse response) {
        this.page = page;
        Calendar date = response.getValidDate();
        this.date = date != null ? date : Calendar.getInstance();
        this.version = response.getVersion();
    }


    /**
     * Method returns the localized legal page to use.
     *
     * @return  Localized legal page.
     */
    @NonNull
    public LocalizedLegalPage getPage() {
        return page;
    }

    /**
     * Method returns the date on which the legal page takes effect.
     *
     * @return  Date on which the legal page takes effect.
     */
    @NonNull
    public Calendar getDate() {
        return date;
    }

    /**
     * Method returns the version code of the legal page.
     *
     * @return  Version code.
     */
    public int getVersion() {
        return version;
    }

}
