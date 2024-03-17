package de.passwordvault.model.packages;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import java.util.ArrayList;
import java.util.List;
import de.passwordvault.App;


/**
 * Class models a package manager which can access information about packages that are installed on
 * the Android device. The class is implemented through singleton-pattern.
 *
 * @author  Christian-2003
 * @version 3.5.0
 */
public class PackagesManager {

    /**
     * Field stores the singleton-instance of the packages manager.
     */
    private static PackagesManager singleton;


    /**
     * Attribute stores the Android package manager through which the installed packages are accessed.
     */
    private final PackageManager packageManager;

    /**
     * Attribute stores the list of packages that are installed.
     */
    private ArrayList<Package> packagesCache;


    /**
     * Constructor instantiates a new package manager.
     */
    private PackagesManager() {
        packagesCache = null;
        packageManager = App.getContext().getPackageManager();
    }


    /**
     * Static method returns the singleton-instance of the class.
     *
     * @return  Singleton instance.
     */
    public static PackagesManager getInstance() {
        if (singleton == null) {
            singleton = new PackagesManager();
        }
        return singleton;
    }


    /**
     * Method returns the list of packages that are installed on the Android device.
     *
     * @return  List of installed packages.
     */
    public ArrayList<Package> getPackages() {
        if (packagesCache == null) {
            packagesCache = new ArrayList<>();
            List<ApplicationInfo> apps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
            for (ApplicationInfo app : apps) {
                if ((app.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                    //Ignore system packages:
                    continue;
                }
                packagesCache.add(new Package(app));
            }
        }
        return packagesCache;
    }


    /**
     * Method returns the package with the specified package name. If the passed package name does
     * not exist, a package without {@linkplain ApplicationInfo} which only contains the passed
     * package name is returned.
     *
     * @param packageName           Name whose package to return.
     * @return                      Package of the passed name.
     * @throws NullPointerException The passed name is {@code null}.
     */
    public Package getPackage(String packageName) throws NullPointerException {
        if (packageName == null) {
            throw new NullPointerException();
        }
        if (packagesCache == null) {
            getPackages();
        }
        for (Package p : packagesCache) {
            if (p.getPackageName().equals(packageName)) {
                return p;
            }
        }
        return new Package(packageName);
    }


    /**
     * Method returns the logo of the package whose name is passed. If no logo is available,
     * {@code null} is returned.
     *
     * @param packageName   Name of the package whose logo shall be returned.
     * @return              Logo of the specified package as drawable.
     */
    public Drawable getPackageLogo(String packageName) {
        try {
            return packageManager.getApplicationIcon(packageName);
        }
        catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }


    /**
     * Method returns the name of the package for the passed ApplicationInfo-instance.
     *
     * @param info  Info of the application whose name shall be returned.
     * @return      Display name for the application.
     */
    public String getApplicationName(ApplicationInfo info) {
        return info.nonLocalizedLabel != null ? info.nonLocalizedLabel.toString() : info.loadLabel(packageManager).toString();
    }

}
