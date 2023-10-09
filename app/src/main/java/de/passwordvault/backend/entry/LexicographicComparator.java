package de.passwordvault.backend.entry;

import java.util.Comparator;


/**
 * Class models a {@linkplain Comparator} which can compare {@link Entry}-instances lexicographically
 * according to their name (i.e. the return value of {@link Entry#getName()}.
 *
 * @author  Christian-2003
 * @version 2.1.0
 */
public class LexicographicComparator extends GenericComparator<Entry> {

    /**
     * Constructor instantiates a new {@link LexicographicComparator} which will not compare the
     * instances in order to reverse-sort them.
     */
    public LexicographicComparator() {
        super();
    }

    /**
     * Constructor constructs a new {@link LexicographicComparator} which will compare the instances
     * either normally, or in order to reverse-sort them, depending on the passed argument.
     *
     * @param reverseSorted Whether the instances should be compared to reverse-sort them.
     */
    public LexicographicComparator(boolean reverseSorted) {
        super(reverseSorted);
    }


    /**
     * Method compares the two {@link Entry}-instances lexicographical according to their name (i.e.
     * {@link Entry#getName()}.
     *
     * @param a                     First entry to be compared.
     * @param b                     Second entry to be compared.
     * @return                      A negative integer, zero or a positive integer as the first
     *                              argument is less than, equal to, or greater than the second.
     * @throws NullPointerException One of the arguments is {@code null}.
     */
    @Override
    public int compare(Entry a, Entry b) throws NullPointerException {
        if (a == null || b == null) {
            throw new NullPointerException("Null is invalid Entry");
        }
        if (reverseSorted) {
            return a.getName().compareToIgnoreCase(b.getName()) * -1;
        }
        return a.getName().compareToIgnoreCase(b.getName());
    }

}
