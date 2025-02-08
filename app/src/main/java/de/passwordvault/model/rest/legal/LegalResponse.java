package de.passwordvault.model.rest.legal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import kotlinx.coroutines.internal.ExceptionsConstructorKt;


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
     * Method returns the date from which the legal page is valid as calendar instance. If something
     * goes wrong, this will be null.
     *
     * @return  Date from which the legal page is valid.
     */
    @Nullable
    public Calendar getValidDate() {
        try {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            calendar.setTime(Objects.requireNonNull(format.parse(valid)));
            return calendar;
        }
        catch (Exception e) {
            return null;
        }
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
