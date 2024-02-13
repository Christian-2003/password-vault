package de.passwordvault.model.entry;

import java.util.Comparator;


/**
 * Class models a {@linkplain Comparator} which can compare {@link EntryAbbreviated}-instances
 * according to the dates on which they were created (i.e. the return value of
 * {@link EntryAbbreviated#getCreated()}.
 *
 * @author  Christian-2003
 * @version 3.3.0
 */
public class TimeComparator extends GenericComparator<EntryAbbreviated> {

    /**
     * Constructor instantiates a new {@link TimeComparator} which will not compare the
     * instances in order to reverse-sort them.
     */
    public TimeComparator() {
        super();
    }

    /**
     * Constructor constructs a new {@link TimeComparator} which will compare the instances
     * either normally, or in order to reverse-sort them, depending on the passed argument.
     *
     * @param reverseSorted Whether the instances should be compared to reverse-sort them.
     */
    public TimeComparator(boolean reverseSorted) {
        super(reverseSorted);
    }


    /**
     * Method compares the two {@link EntryAbbreviated}-instances according to the dates on which
     * they were created (i.e. {@link EntryAbbreviated#getCreated()}.
     *
     * @param a                     First entry to be compared.
     * @param b                     Second entry to be compared.
     * @return                      A negative integer, zero or a positive integer as the first
     *                              argument is less than, equal to, or greater than the second.
     * @throws NullPointerException One of the arguments is {@code null}.
     */
    @Override
    public int compare(EntryAbbreviated a, EntryAbbreviated b) throws NullPointerException {
        if (a == null || b == null) {
            throw new NullPointerException("Null is invalid Entry");
        }
        if (reverseSorted) {
            return a.getCreated().compareTo(b.getCreated()) * -1;
        }
        return a.getCreated().compareTo(b.getCreated());
    }

}
