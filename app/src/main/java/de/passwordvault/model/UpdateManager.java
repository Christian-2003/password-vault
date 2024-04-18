package de.passwordvault.model;

import android.app.Activity;
import android.util.Log;
import com.supersuman.apkupdater.ApkUpdater;


/**
 * Class implements an update manager through singleton-pattern. The manager can determine whether
 * a new app update is available. Get the singleton instance through {@link #getInstance(Activity)}.
 *
 * @author  Christian-2003
 * @version 3.5.2
 */
public class UpdateManager {

    /**
     * Interface can be implemented by the activity that instantiates the update manger. When the
     * update status is discovered, the {@link #onUpdateStatusChanged(boolean)} is called informing
     * the activity about changes in the update state.
     */
    public interface UpdateStatusChangedCallback {

        /**
         * Method is called when the update state is registered the first time.
         *
         * @param updateAvailable   Whether an update is available.
         */
        void onUpdateStatusChanged(boolean updateAvailable);

    }


    /**
     * Field stores the URL of the latest GitHub release for the application.
     */
    private static final String URL = "https://github.com/Christian-2003/password-vault/releases/latest";

    /**
     * Field stores the tag used for logging.
     */
    private static final String TAG = "UpdateManager";

    /**
     * Field stores the singleton instance of the manager.
     */
    private static UpdateManager singleton;


    /**
     * Attribute stores whether an app-update is available.
     */
    private boolean updateAvailable;

    /**
     * Attribute stores the callback for when the update status is discovered for the first time.
     */
    private final UpdateStatusChangedCallback callback;


    /**
     * Constructor instantiates a new update manager and tests whether an update for the app is
     * available.
     *
     * @param activity  Activity-context from which to create the update manager. Make sure that this
     *                  is never {@code null}!
     * @param callback  Callback for when the update status is discovered the first time.
     */
    private UpdateManager(Activity activity, UpdateStatusChangedCallback callback) {
        this.callback = callback;
        this.updateAvailable = false;

        //For some reason, the ApkUpdater-class does not work within the main thread.
        Thread updateThread = new Thread(() -> {
            try {
                ApkUpdater updater = new ApkUpdater(activity, URL);
                updater.setThreeNumbers(true);
                updateAvailable = Boolean.TRUE.equals(updater.isNewUpdateAvailable());
            }
            catch (Exception e) {
                Log.e(TAG, "Could not determine whether update is available: " + e.getMessage());
                updateAvailable = false;
            }
            //Finished getting update state:
            if (this.callback != null) {
                try {
                    this.callback.onUpdateStatusChanged(updateAvailable);
                }
                catch (Exception e) {
                    Log.e(TAG, "Cannot inform UpdateStatusChangedCallback: " + e.getMessage());
                }
            }
        });
        updateThread.start();
    }


    /**
     * Method returns whether a new update for the app is available.
     *
     * @return  Whether a new update is available.
     */
    public boolean isUpdateAvailable() {
        return updateAvailable;
    }


    /**
     * Method returns the singleton-instance for the update manager. The manager requires an activity
     * context to work - although this is only required the first time this method is called. Pass
     * {@code null} only when you are absolutely sure that the manager has been instantiated
     * already!
     *
     * @param activity              Activity-context for the manager. Ideally, this is the main activity.
     * @param callback              Callback for when the update status is discovered the first time.
     * @return                      Singleton-instance of the update manager.
     * @throws NullPointerException This method is called the first time during the app lifecycle and
     *                              the passed activity is {@code null}.
     */
    public static UpdateManager getInstance(Activity activity, UpdateStatusChangedCallback callback) throws NullPointerException {
        if (singleton == null) {
            if (activity == null) {
                throw new NullPointerException();
            }
            singleton = new UpdateManager(activity, callback);
        }
        return singleton;
    }

    /**
     * Method returns the singleton-instance for the update manager. The manager requires an activity
     * context to work - although this is only required the first time this method is called. Pass
     * {@code null} only when you are absolutely sure that the manager has been instantiated
     * already!
     *
     * @param activity              Activity-context for the manager. Ideally, this is the main activity.
     * @return                      Singleton-instance of the update manager.
     * @throws NullPointerException This method is called the first time during the app lifecycle and
     *                              the passed activity is {@code null}.
     */
    public static UpdateManager getInstance(Activity activity) throws NullPointerException {
        return getInstance(activity, null);
    }

}
