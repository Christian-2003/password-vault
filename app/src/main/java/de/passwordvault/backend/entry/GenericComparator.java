package de.passwordvault.backend.entry;

import java.util.Comparator;
import java.util.List;


/**
 * Class models a generic comparator which provides the information necessary on whether instances of
 * type {@code <T>} shall be reverse sorted or not to its subclasses.
 *
 * @param <T>   Type of the instances for which the {@linkplain Comparator} is implemented.
 * @author      Christian-2003
 * @version     2.1.0
 */
public abstract class GenericComparator<T> implements Comparator<T> {

    /**
     * Attribute stores whether the {@linkplain Comparator} should compare instances in order for
     * them to be reverse-sorted by {@linkplain java.util.Collections#sort(List, Comparator)}.
     */
    protected boolean reverseSorted;


    /**
     * Constructor instantiates a new {@link GenericComparator} which will not compare the instances
     * in order to reverse-sort them.
     */
    public GenericComparator() {
        reverseSorted = false;
    }


    /**
     * Constructor constructs a new {@link GenericComparator} which will compare the instances either
     * normally, or in order to reverse-sort them, depending on the passed argument.
     *
     * @param reverseSorted Whether the instances should be compared to reverse-sort them.
     */
    public GenericComparator(boolean reverseSorted) {
        this.reverseSorted = reverseSorted;
    }

}
