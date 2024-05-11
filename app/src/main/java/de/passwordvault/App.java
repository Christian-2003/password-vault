package de.passwordvault;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * This singleton class stores the global {@link Context} for the entire application. This is needed
 * for easier access to the context within the model of the application.
 *
 * @author  Christian-2003
 * @version 3.0.0
 */
public class App extends Application {

    /**
     * Field stores the context from this {@link Application}.
     */
    @SuppressLint("StaticFieldLeak")
    private static Context context;

    /**
     * Attribute stores the executor service used for asynchronous operations.
     */
    private final ExecutorService executorService;


    /**
     * Constructor instantiates the app. This is called when the app starts.
     */
    public App() {
        executorService = Executors.newFixedThreadPool(4);
    }


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
