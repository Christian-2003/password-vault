package de.passwordvault.model.packages;

import java.util.ArrayList;


/**
 * Class models a serializable package collection. Distinguishing between regular package collections
 * and serializable package collection is required since {@linkplain android.content.pm.ApplicationInfo}
 * is not serializable and therefore cannot be passed as extra with {@linkplain android.content.Intent}.
 *
 * @author  Christian-2003
 * @version 3.5.0
 */
public class SerializablePackageCollection extends ArrayList<SerializablePackage> {

    /**
     * Constructor instantiates a new empty serializable package collection.
     */
    public SerializablePackageCollection() {
        super();
    }

    public SerializablePackageCollection(ArrayList<SerializablePackage> packages) {
        super();
        addAll(packages);
    }

    /**
     * Constructor instantiates a new serializable package collection from the passed regular package
     * collection.
     *
     * @param packages  Regular package collection from which to create the serializable package
     *                  collection.
     */
    public SerializablePackageCollection(PackageCollection packages) {
        super();
        for (Package p : packages) {
            add(new SerializablePackage(p.getPackageName()));
        }
    }


    /**
     * Method converts this serializable package collection into a regular package collection.
     *
     * @return  Converted regular package collection.
     */
    public PackageCollection toPackageCollection() {
        PackageCollection packages = new PackageCollection();
        for (SerializablePackage serializablePackage : this) {
            Package p = PackagesManager.getInstance().getPackage(serializablePackage.getPackageName());
            if (p != null) {
                packages.add(p);
            }
        }
        return packages;
    }

}
