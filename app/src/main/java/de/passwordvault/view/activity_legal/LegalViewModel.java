package de.passwordvault.view.activity_legal;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;
import java.io.Serializable;
import de.passwordvault.model.rest.legal.LegalPageDto;


/**
 * Class implements the view model for the {@link LegalActivity}.
 *
 * @author  Christian-2003
 * @version 3.7.2
 */
public class LegalViewModel extends ViewModel {

    /**
     * Attribute stores the DTO for the privacy policy.
     */
    @Nullable
    private LegalPageDto privacyDto;

    /**
     * Attribute stores the DTO for the terms of service.
     */
    @Nullable
    private LegalPageDto tosDto;

    /**
     * Attribute stores whether the changes have been accepted.
     */
    private boolean accepted;


    /**
     * Constructor instantiates a new view model.
     */
    public LegalViewModel() {
        privacyDto = null;
        tosDto = null;
        accepted = false;
    }


    /**
     * Method returns the privacy policy DTO.
     *
     * @return  Privacy policy DTO.
     */
    @Nullable
    public LegalPageDto getPrivacyDto() {
        return privacyDto;
    }

    /**
     * Method returns the terms of service DTO.
     *
     * @return  Terms of service DTO.
     */
    @Nullable
    public LegalPageDto getTosDto() {
        return tosDto;
    }

    /**
     * Method returns whether the changes have been accepted.
     *
     * @return  Whether the changes have been accepted.
     */
    public boolean isAccepted() {
        return accepted;
    }

    /**
     * Method processes the extras passed to the view model.
     *
     * @param extras    Extras to process.
     * @return          Whether the extras have been processed successfully.
     */
    public boolean processExtras(@Nullable Bundle extras) {
        if (privacyDto != null || tosDto != null) {
            return true;
        }
        if (extras != null) {
            boolean success = false;
            Serializable privacy = extras.getSerializable(LegalActivity.EXTRA_PRIVACY);
            if (privacy != null) {
                try {
                    this.privacyDto = (LegalPageDto)privacy;
                    success = true;
                }
                catch (ClassCastException e) {
                    Log.e("Legal", e.getMessage() != null ? e.getMessage() : "ClassCastException");
                }
            }
            Serializable tos = extras.getSerializable(LegalActivity.EXTRA_TOS);
            if (tos != null) {
                try {
                    this.tosDto = (LegalPageDto)tos;
                    success = true;
                }
                catch (ClassCastException e) {
                    Log.e("Legal", e.getMessage() != null ? e.getMessage() : "ClassCastException");
                }
            }
            return success;
        }
        return false;
    }


    /**
     * Method accepts the changes applied.
     *
     * @param context   Context.
     */
    public void acceptChanges(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences("settings", Context.MODE_PRIVATE).edit();
        if (privacyDto != null) {
            editor.putInt("privacy_version", privacyDto.getVersion());
        }
        if (tosDto != null) {
            editor.putInt("tos_version", tosDto.getVersion());
        }
        editor.apply();
        accepted = true;
    }

}
