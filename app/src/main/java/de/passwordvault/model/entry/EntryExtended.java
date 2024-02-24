package de.passwordvault.model.entry;

import java.util.ArrayList;
import de.passwordvault.model.detail.Detail;
import de.passwordvault.model.storage.app.Storable;
import de.passwordvault.model.storage.app.StorageException;
import de.passwordvault.model.storage.csv.CsvConfiguration;


/**
 * Class models an extended entry which contains all available information about an account-entry.
 *
 * @author  Christian-2003
 * @version 3.3.0
 */
public class EntryExtended extends EntryAbbreviated implements Storable {

    /**
     * Attribute stores the details of the entry.
     */
    private ArrayList<Detail> details;


    /**
     * Constructor instantiates a new entry without any contents.
     */
    public EntryExtended() {
        super();
        setDetails(new ArrayList<>());
    }

    /**
     * Constructor instantiates a new Entry and copies the attributes of the passed Entry to this
     * instance.
     *
     * @param entry                 Entry whose values shall be copied to this instance.
     * @throws NullPointerException The passed entry is {@code null}.
     */
    public EntryExtended(EntryExtended entry) throws NullPointerException {
        super(entry);
        setDetails(entry.getDetails());
    }

    /**
     * Constructor instantiates a new Entry and copies the attributes of the passed Entry to this
     * instance.
     *
     * @param entry                 Entry whose values shall be copied to this instance.
     * @throws NullPointerException The passed entry is {@code null}.
     */
    public EntryExtended(EntryAbbreviated entry) throws NullPointerException {
        super(entry);
        details = new ArrayList<>();
    }


    /**
     * Method returns all details of the entry.
     *
     * @return  Details of the entry.
     */
    public ArrayList<Detail> getDetails() {
        return details;
    }

    /**
     * Method changes the details of the entry.
     *
     * @param details               New list of details for the entry.
     * @throws NullPointerException The passed list is {@code null}.
     */
    public void setDetails(ArrayList<Detail> details) throws NullPointerException {
        if (details == null) {
            throw new NullPointerException();
        }
        this.details = details;
    }

    /**
     * Method returns an {@linkplain ArrayList} of visible {@linkplain Detail} instances that are
     * handled by this Entry. The returned list contains all details whose {@linkplain Detail#isVisible()}
     * method returns {@code true}.
     *
     * @return  List of visible details.
     */
    public ArrayList<Detail> getVisibleDetails() {
        ArrayList<Detail> visibleDetails = new ArrayList<>();
        for (Detail detail : details) {
            if (detail.isVisible()) {
                visibleDetails.add(detail);
            }
        }
        return visibleDetails;
    }


    /**
     * Method returns an {@linkplain ArrayList} of invisible {@linkplain Detail} instances that are
     * handled by this Entry. The returned list contains all details whose {@linkplain Detail#isVisible()}
     * method returns {@code false}.
     *
     * @return  List of invisible details.
     */
    public ArrayList<Detail> getInvisibleDetails() {
        ArrayList<Detail> invisibleDetails = new ArrayList<>();
        for (Detail detail : details) {
            if (!detail.isVisible()) {
                invisibleDetails.add(detail);
            }
        }
        return invisibleDetails;
    }


    /**
     * Method adds the passed detail to the list of handled details. If a detail with the passed
     * detail's UUID already exists, nothing happens and {@code false} is returned.
     *
     * @param detail Detail to be added to the list of details.
     */
    public void add(Detail detail) {
        if (containsUuid(detail.getUuid()) == -1) {
            details.add(detail);
        }
    }

    /**
     * Method removes the detail of the passed UUID from the list of details. If no detail with the
     * specified UUID exists, {@code null} is returned. Otherwise the removed detail is returned.
     *
     * @param uuid UUID whose detail shall be removed.
     */
    public void remove(String uuid) {
        int index = containsUuid(uuid);
        if (index != -1) {
            details.remove(index);
        }
    }

    /**
     * Method returns the detail of the passed UUID. If no detail with the specified UUID exists,
     * {@code null} is returned.
     *
     * @param uuid  UUID whose detail shall be returned.
     * @return      Detail of the passed UUID.
     */
    public Detail get(String uuid) {
        int index = containsUuid(uuid);
        if (index != -1) {
            return details.get(index);
        }
        return null;
    }

    /**
     * Method changes the detail with the same UUID with the passed detail. If no detail is replaced,
     * {@code false} is returned. Otherwise, {@code true} is returned.
     *
     * @param detail    Detail to replace the detail with the same UUID.
     * @return          Whether a detail was successfully replaced.
     */
    public boolean set(Detail detail) {
        int index = containsUuid(detail.getUuid());
        if (index == -1) {
            //Detail with UUID does not already exist:
            return false;
        }
        details.set(index, detail);
        return true;
    }

    /**
     * Method returns the index of the detail with the passed UUID.
     *
     * @param uuid  UUID of the detail whose index to return.
     * @return      Index of the detail with the passed UUID.
     */
    public int indexOf(String uuid) {
        for (int i = 0; i < details.size(); i++) {
            if (details.get(i).getUuid().endsWith(uuid)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Method tests whether the entry contains a detail of the passed UUID.
     *
     * @param uuid  UUID to be tested.
     * @return      Whether a detail of the passed UUID exists.
     */
    public boolean contains(String uuid) {
        return containsUuid(uuid) != -1;
    }

    /**
     * Method returns the number of details that are managed by this entry.
     *
     * @return  Number of handled details.
     */
    public int size() {
        return details.size();
    }


    /**
     * Method converts this instance into a string-representation which can be used for persistent
     * storage. The generated string can later be parsed into a storable through
     * {@link #fromStorable(String)}.
     *
     * @return  String-representation of this instance.
     */
    @Override
    public String toStorable() {
        StringBuilder builder = new StringBuilder();
        //Put any information that is no detail above the '######'!
        builder.append("######");
        builder.append(CsvConfiguration.ROW_DIVIDER);
        for (Detail detail : details) {
            builder.append(detail.toStorable());
            builder.append(CsvConfiguration.ROW_DIVIDER);
        }
        return builder.toString();
    }

    /**
     * Method creates the instance from it's passed string-representation. The passed string - which
     * is a storable's string-representation - must be generated by {@link #toStorable()} beforehand.
     *
     * @param s                     String-representation from which to create the instance.
     * @throws StorageException     The passed string could not be converted into an instance.
     * @throws NullPointerException The passed string is {@code null}.
     */
    @Override
    public void fromStorable(String s) throws StorageException, NullPointerException {
        if (s == null) {
            throw new NullPointerException();
        }
        String[] lines = s.split("" + CsvConfiguration.ROW_DIVIDER);
        int startOfDetails = 0;
        for (String line : lines) {
            //Parse any information that is no detail while this loop runs!
            if (line.equals("######")) {
                startOfDetails++;
                break;
            }
            startOfDetails++;
        }
        //Begin parsing details:
        for (int i = startOfDetails; i < lines.length; i++) {
            Detail detail = new Detail();
            detail.fromStorable(lines[i]);
            details.add(detail);
        }
    }


    /**
     * Method tests whether a detail with the specified UUID exists in the list of details. If no
     * detail with the specified UUID exists, {@code -1} is returned.
     *
     * @param uuid  UUID to be tested.
     * @return      Index of the detail with the specified UUID.
     */
    private int containsUuid(String uuid) {
        for (int i = 0; i < details.size(); i++) {
            if (details.get(i).getUuid().equals(uuid)) {
                return i;
            }
        }
        return -1;
    }

}
