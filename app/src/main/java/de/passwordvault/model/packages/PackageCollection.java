package de.passwordvault.model.packages;

import java.util.ArrayList;
import de.passwordvault.model.storage.csv.CsvBuilder;
import de.passwordvault.model.storage.csv.CsvParser;


/**
 * Class models a collection which can store {@link Package}-instances.
 *
 * @author  Christian-2003
 * @version 3.5.0
 */
public class PackageCollection extends ArrayList<Package> {

    /**
     * Constructor instantiates a new empty package collection.
     */
    public PackageCollection() {
        super();
    }

    /**
     * Constructor instantiates a new package collection which contains the packages of the passed
     * list.
     *
     * @param packages              Packages for this collection.
     * @throws NullPointerException The passed list is {@code null}.
     */
    public PackageCollection(ArrayList<Package> packages) throws NullPointerException {
        super();
        if (packages == null) {
            throw new NullPointerException();
        }
        addAll(packages);
    }

    /**
     * Constructor instantiates a new package collection which contains the packages of the passed
     * CSV representation. The passed argument must be generated through {@link #toCsv()}.
     *
     * @param csv                   CSV to be converted into this collection.
     * @throws NullPointerException The passed CSV is {@code null}.
     */
    public PackageCollection(String csv) throws NullPointerException {
        super();
        if (csv == null) {
            throw new NullPointerException();
        }
        CsvParser parser = new CsvParser(csv);
        ArrayList<String> packageNames = parser.parseCsv();
        for (String packageName : packageNames) {
            if (packageName == null || packageName.isEmpty()) {
                continue;
            }
            Package p = PackagesManager.getInstance().getPackage(packageName);
            if (p != null) {
                add(p);
            }
        }
    }


    /**
     * Method tests whether a package of the passed package name exists within the collection.
     *
     * @param packageName   Name of the package to be tested.
     * @return              Whether a package of the passed name exists within the collection.
     */
    public boolean containsPackageName(String packageName) {
        for (Package p : this) {
            if (p.getPackageName().equals(packageName)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Method converts the contents of the package collection into a CSV representation.
     *
     * @return  CSV representation of the packages.
     */
    public String toCsv() {
        CsvBuilder builder = new CsvBuilder();
        for (Package p : this) {
            builder.append(p.getPackageName());
        }
        return builder.toString();
    }

}
