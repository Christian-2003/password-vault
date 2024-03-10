package de.passwordvault.model.packages;

import java.io.Serializable;
import de.passwordvault.model.Identifiable;


/**
 * Class implements a serializable package. Distinguishing between {@link Package} and this class
 * is required since {@linkplain android.content.pm.ApplicationInfo} does not implement the
 * {@linkplain Serializable}-interface. Therefore, Package is not serializable.
 *
 * @author  Christian-2003
 * @version 3.5.0
 */
public class SerializablePackage implements Identifiable, Serializable {

    /**
     * Attribute stores the package name of the package, e.g. {@code com.example.app_name}.
     */
    protected final String packageName;


    /**
     * Method returns the name of the package.
     *
     * @return  Name of the package.
     */
    public String getPackageName() {
        return packageName;
    }


    /**
     * Constructor instantiates a new serializable package.
     *
     * @param packageName           Name of the package.
     * @throws NullPointerException The passed package name is {@code null}.
     */
    public SerializablePackage(String packageName) throws NullPointerException {
        if (packageName == null) {
            throw new NullPointerException();
        }
        this.packageName = packageName;
    }


    /**
     * Method returns the string-representation of the package name of the instance.
     *
     * @return  String-representation of the package name.
     */
    @Override
    public String getUuid() {
        return packageName;
    }

    /**
     * Method explicitly tests whether the package name of the implementing class is identical to the
     * package name of the passed object (if it is an instance of {@link Identifiable}).
     *
     * @param obj   Object to be tested.
     * @return      If the passed object is an instance of Identifiable and it's package name is
     *              identical to the package name of this instance.
     */
    @Override
    public boolean equals(Identifiable obj) {
        return obj.getUuid().equals(packageName);
    }

}
