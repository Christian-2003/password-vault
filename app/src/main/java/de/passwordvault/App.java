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
     * Field stores the executor service used to asynchronously execute code.
     */
    private static final ExecutorService executorService = Executors.newFixedThreadPool(4);


    /**
     * Method is called whenever the application is created.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }


    /**
     * Method returns an executor which can be used to asynchronously execute code.
     *
     * @return  Executor for asynchronous code execution.
     */
    public static Executor getExecutor() {
        return executorService;
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
