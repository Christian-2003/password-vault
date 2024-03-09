package de.passwordvault.view.utils;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import java.io.Serializable;


/**
 * Interface can be implemented by components that launch activities. The defined callback methods
 * are called by the launched activity whenever it is finished. Implementing this activity
 * automatically implements {@linkplain Serializable}.
 * This callback is needed to prevent the use of the deprecated method
 * {@linkplain AppCompatActivity#startActivityForResult(Intent, int, Bundle)}.
 *
 * @author  Christian-2003
 * @version 3.5.0
 */
public interface ActivityCallbackListener extends Serializable {

    /**
     * Method is called when the launched activity has finished with a positive result.
     *
     * @param activity  Activity which finished positively.
     */
    void onPositiveCallback(AppCompatActivity activity);

    /**
     * Method is called when the launched activity has finished with a negative result.
     *
     * @param activity  Activity which finished negatively.
     */
    void onNegativeCallback(AppCompatActivity activity);

}
