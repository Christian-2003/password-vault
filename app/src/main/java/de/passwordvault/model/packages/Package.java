package de.passwordvault.model.packages;

import android.graphics.drawable.Drawable;


/**
 * Class models a package. A package is an app that is installed on the Android device.
 *
 * @author  Christian-2003
 * @version 3.5.0
 */
public class Package {

    /**
     * Attribute stores the package name of the package, e.g. {@code com.example.app_name}.
     */
    private String packageName;

    /**
     * Attribute stores the name of the app.
     */
    private String appName;

    /**
     * Attribute stores the app logo as drawable. This can be {@code null} if no drawable is available.
     */
    private Drawable logo;


    /**
     * Constructor instantiates a new package with the passed arguments.
     *
     * @param packageName           Name of the package (e.g. {@code com.example.app_name}).
     * @param appName               Name of the app.
     * @param logo                  App-logo as drawable.
     * @throws NullPointerException Package or app name is {@code null}.
     */
    public Package(String packageName, String appName, Drawable logo) throws NullPointerException {
        if (packageName == null || appName == null) {
            throw new NullPointerException();
        }
        this.packageName = packageName;
        this.appName = appName;
        this.logo = logo;
    }


    /**
     * Method returns the name of the package.
     *
     * @return  Name of the package.
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * Method changes the name of the package to the passed argument.
     *
     * @param packageName           New package name.
     * @throws NullPointerException The passed argument is {@code null}.
     */
    public void setPackageName(String packageName) throws NullPointerException {
        if (packageName == null) {
            throw new NullPointerException();
        }
        this.packageName = packageName;
    }

    /**
     * Method returns the name of the app.
     *
     * @return  Name of the app.
     */
    public String getAppName() {
        return appName;
    }

    /**
     * Method changes the name of the app to the passed argument.
     *
     * @param appName               New name for the app.
     * @throws NullPointerException The passed argument is {@code null}.
     */
    public void setAppName(String appName) throws NullPointerException {
        if (appName == null) {
            throw new NullPointerException();
        }
        this.appName = appName;
    }

    /**
     * Method returns the logo of the app as drawable. If no logo is available, this is {@code null}.
     *
     * @return  Logo of the app as drawable.
     */
    public Drawable getLogo() {
        return logo;
    }

    /**
     * Method changes the logo of the package to the passed drawable.
     *
     * @param logo  New logo for the app.
     */
    public void setLogo(Drawable logo) {
        this.logo = logo;
    }

}
