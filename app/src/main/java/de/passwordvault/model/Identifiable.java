package de.passwordvault.model;


/**
 * Interface requires implementing classes to have UUIDs which are used for identification.
 *
 * @author  Christian-2003
 * @version 3.3.0
 */
public interface Identifiable {

    /**
     * Method returns the string-representation of the UUID of the instance.
     *
     * @return  String-representation of the UUID.
     */
    String getUuid();

    /**
     * Method explicitly tests whether the UUID of the implementing class is identical to the UUID
     * of the passed object (if it is an instance of {@link Identifiable}).
     *
     * @param obj   Object to be tested.
     * @return      If the passed object is an instance of Identifiable and it's UUID is identical
     *              to the UUID of this instance.
     */
    boolean equals(Identifiable obj);

}
