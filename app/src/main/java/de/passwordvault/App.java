package de.passwordvault;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;


/**
 * This singleton class stores the global {@link Context} for the entire application. This is needed
 * for easier access to the context within the model of the application.
 *
 * @author  Christian-2003
 * @version 3.0.0
 */
public class App extends Application {

    /**
     * Attribute stores the context from this {@link Application}.
     */
    @SuppressLint("StaticFieldLeak")
    private static Context context;


    /**
     * Method is called whenever the application is created.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }


    /**
     * Method returns the application context.
     *
     * @return  Application context.
     */
    public static Context getContext() {
        return context;
    }

}
