package de.passwordvault.model.packages;

import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;
import de.passwordvault.model.Identifiable;


/**
 * Class models a package. A package is an app that is installed on the Android device. The class
 * implements the {@link Identifiable}-interface, using the unique package names instead of UUIDs.
 * <b>Important: This class is NOT serializable, since it contains an instance of
 * {@linkplain ApplicationInfo}!</b>
 *
 * @author  Christian-2003
 * @version 3.5.0
 */
public class Package extends SerializablePackage {

    /**
     * Attribute stores the application info from which the data of the package is loaded.
     */
    private final ApplicationInfo applicationInfo;

    /**
     * Attribute stores the name of the app.
     */
    private String appName;

    /**
     * Attribute stores the app logo as drawable. This can be {@code null} if no drawable is available.
     */
    private Drawable logo;

    /**
     * Attribute indicates whether the application logo is loaded.
     */
    private boolean logoLoaded;


    /**
     * Constructor instantiates a new package for the passed application info.
     *
     * @param applicationInfo       Application info for which to create the package.
     * @throws NullPointerException The passed application info is {@code null}.
     */
    public Package(ApplicationInfo applicationInfo) throws NullPointerException {
        super(applicationInfo.packageName);
        this.applicationInfo = applicationInfo;
        appName = null;
        logo = null;
        logoLoaded = false;
    }

    /**
     * Constructor instantiates a new package only with the package name. Use of this constructor is
     * intended for apps that were installed once but have been uninstalled afterwards. Therefore,
     * no {@linkplain ApplicationInfo} is available for these packages.
     *
     * @param packageName           Name of the package (e.g. 'com.example.app').
     * @throws NullPointerException The passed package name is {@code null}.
     */
    public Package(String packageName) throws NullPointerException {
        super(packageName);
        applicationInfo = null;
        appName = null;
        logo = null;
        logoLoaded = true;
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
     * Method returns the name of the app.
     *
     * @return  Name of the app.
     */
    public String getAppName() {
        if (appName == null) {
            if (applicationInfo == null) {
                return "";
            }
            appName = PackagesManager.getInstance().getApplicationName(applicationInfo);
            if (appName == null) {
                appName = "";
            }
        }
        return appName;
    }

    /**
     * Method returns the logo of the app as drawable. If no logo is available, this is {@code null}.
     *
     * @return  Logo of the app as drawable.
     */
    public Drawable getLogo() {
        if (!logoLoaded) {
            logo = PackagesManager.getInstance().getPackageLogo(packageName);
            logoLoaded = true;
        }
        return logo;
    }

}
