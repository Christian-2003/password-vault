package de.passwordvault.model.entry;

import android.graphics.drawable.Drawable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;
import java.util.UUID;
import de.passwordvault.model.Identifiable;
import de.passwordvault.model.packages.Package;
import de.passwordvault.model.packages.PackageCollection;
import de.passwordvault.model.storage.app.Storable;
import de.passwordvault.model.storage.app.StorageException;
import de.passwordvault.model.storage.csv.CsvBuilder;
import de.passwordvault.model.storage.csv.CsvParser;
import de.passwordvault.model.tags.TagCollection;


/**
 * Class models an abbreviated entry which contains only the most basic information about any type
 * of account. Further details about an entry are only available in {@link EntryExtended}.
 *
 * @author  Christian-2003
 * @version 3.5.0
 */
public class EntryAbbreviated implements Identifiable, Storable, Serializable {

    /**
     * Attribute stores type 4 UUID of the entry.
     */
    protected String uuid;

    /**
     * Attribute stores name of the entry.
     */
    protected String name;

    /**
     * Attribute stores description of the entry.
     */
    protected String description;

    /**
     * Attribute stores whether the entry shall be visible.
     */
    protected boolean visible;

    /**
     * Attribute stores the date on which the entry was created.
     */
    private Calendar created;

    /**
     * Attribute stores the date on which the entry was changed the last time.
     */
    private Calendar changed;

    /**
     * Attribute stores the tags of the entry.
     */
    private TagCollection tags;

    /**
     * Attribute stores the packages to which the entry is assigned.
     */
    private PackageCollection packages;

    /**
     * Attribute stores whether the entry was automatically created by the autofill service.
     */
    private boolean addedAutomatically;


    /**
     * Constructor instantiates a new entry without any contents.
     */
    public EntryAbbreviated() {
        setUuid(UUID.randomUUID().toString());
        setName("");
        setDescription("");
        setVisible(true);
        setCreated(Calendar.getInstance());
        setChanged(getCreated());
        setTags(new TagCollection());
        setPackages(new PackageCollection());
        setAddedAutomatically(false);
    }

    /**
     * Constructor instantiates a new Entry and copies the attributes of the passed Entry to this
     * instance.
     *
     * @param entry                 Entry whose values shall be copied to this instance.
     * @throws NullPointerException The passed entry is {@code null}.
     */
    public EntryAbbreviated(EntryAbbreviated entry) throws NullPointerException {
        if (entry == null) {
            throw new NullPointerException();
        }
        setUuid(entry.getUuid());
        setName(entry.getName());
        setDescription(entry.getDescription());
        setVisible(entry.isVisible());
        setCreated(entry.getCreated());
        setChanged(entry.getChanged());
        setTags(new TagCollection(entry.getTags()));
        setPackages(entry.getPackages());
        setAddedAutomatically(entry.isAddedAutomatically());
    }


    /**
     * Method returns the UUID of the entry.
     *
     * @return  UUID of the entry.
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Method changes the UUID of the entry.
     *
     * @param uuid                  New UUID for the entry.
     * @throws NullPointerException The passed UUID is {@code null}.
     */
    public void setUuid(String uuid) throws NullPointerException {
        if (uuid == null) {
            throw new NullPointerException();
        }
        this.uuid = uuid;
    }

    /**
     * Method returns the name of the entry.
     *
     * @return  Name of the entry.
     */
    public String getName() {
        return name;
    }

    /**
     * Method changes the name of the entry.
     *
     * @param name                  New name for the entry.
     * @throws NullPointerException The passed name is {@code null}.
     */
    public void setName(String name) throws NullPointerException {
        if (name == null) {
            throw new NullPointerException();
        }
        this.name = name;
    }

    /**
     * Method returns the description of the entry.
     *
     * @return  Description of the entry.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Method changes the description of the entry.
     *
     * @param description           New description for the entry.
     * @throws NullPointerException The passed description is {@code null}.
     */
    public void setDescription(String description) throws NullPointerException {
        if (description == null) {
            throw new NullPointerException();
        }
        this.description = description;
    }

    /**
     * Method returns whether the entry is visible.
     *
     * @return  Whether the name is visible.
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Method changes whether the name is visible.
     *
     * @param visible   Whether the name is visible.
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * Method returns the date of creation of the entry.
     *
     * @return  Date of creation of the entry.
     */
    public Calendar getCreated() {
        return created;
    }

    /**
     * Method changes the date of creation of the entry.
     *
     * @param created               New date of creation of the entry.
     * @throws NullPointerException The passed date is {@code null}.
     */
    public void setCreated(Calendar created) throws NullPointerException {
        if (created == null) {
            throw new NullPointerException();
        }
        this.created = created;
    }

    /**
     * Method returns the date of last change of the entry.
     *
     * @return  Date on which the entry was changed the last time.
     */
    public Calendar getChanged() {
        return changed;
    }

    /**
     * Method changes the date on which the entry was changed the last time.
     *
     * @param changed               New date on which the entry was changed the last time.
     * @throws NullPointerException The passed date is {@code null}.
     */
    public void setChanged(Calendar changed) throws NullPointerException {
        if (changed == null) {
            throw new NullPointerException();
        }
        this.changed = changed;
    }

    /**
     * Method returns the tags of the entry.
     *
     * @return  Tags of the entry.
     */
    public TagCollection getTags() {
        return tags;
    }

    /**
     * Method changes the tags of the entry.
     *
     * @param tags                  New tags for the entry.
     * @throws NullPointerException The passed argument is {@code null}.
     */
    public void setTags(TagCollection tags) throws NullPointerException {
        if (tags == null) {
            throw new NullPointerException();
        }
        this.tags = new TagCollection(tags);
    }

    /**
     * Method returns a collection containing all packages to which the entry is assigned.
     *
     * @return  List of packages.
     */
    public PackageCollection getPackages() {
        return packages;
    }

    /**
     * Method changes the collection of packages to which the entry is assigned to the passed
     * argument.
     *
     * @param packages              New list of packages for the entry.
     * @throws NullPointerException The passed argument is {@code null}.
     */
    public void setPackages(PackageCollection packages) throws NullPointerException {
        if (packages == null) {
            throw new NullPointerException();
        }
        this.packages = new PackageCollection(packages);
    }

    /**
     * Method returns whether the entry was added automatically by the autofill service.
     *
     * @return  Whether the entry was added automatically.
     */
    public boolean isAddedAutomatically() {
        return addedAutomatically;
    }

    /**
     * Method changes whether the entry was added automatically by the autofill service.
     *
     * @param addedAutomatically    Whether the entry was added automatically.
     */
    public void setAddedAutomatically(boolean addedAutomatically) {
        this.addedAutomatically = addedAutomatically;
    }


    /**
     * Method returns the first available logo from the list of packages.
     *
     * @return  First available logo from the packages.
     */
    public Drawable getLogo() {
        for (Package p : packages) {
            Drawable logo = p.getLogo();
            if (logo != null) {
                return logo;
            }
        }
        return null;
    }


    /**
     * Notifies this Entry that some of its data was changed. This will update the value of
     * {@linkplain #changed} to the current date and time.
     */
    public void notifyDataChange() {
        changed = Calendar.getInstance();
    }


    /**
     * Method tests whether the passed argument is contained within the {@link #name} or
     * {@link #description} of the {@link EntryAbbreviated}.
     *
     * @param s Substring to be tested if present anywhere in this entry.
     * @return  Whether the passed string is contained within this instance.
     */
    public boolean matchesFilter(CharSequence s) {
        return name.toLowerCase().contains(s) || description.toLowerCase().contains(s);
    }


    /**
     * Method tests whether the UUID of the passed entry is identical to the UUID of this entry.
     *
     * @param obj   Entry whose UUID shall be compared to the UUID of this entry.
     * @return      Whether both UUIDs are identical.
     */
    @Override
    public boolean equals(Identifiable obj) {
        return obj.getUuid().equals(uuid);
    }


    /**
     * Generates a hash for this entry based on the UUID.
     *
     * @return  Generated hash for the entry.
     */
    public int hashCode() {
        return Objects.hash(uuid);
    }


    /**
     * Method converts this instance into a string-representation which can be used for persistent
     * storage. The generated string can later be parsed into a storable through
     * {@link #fromStorable(String)}.
     *
     * @return  String-representation of this instance.
     */
    public String toStorable() {
        CsvBuilder builder = new CsvBuilder();

        builder.append(uuid);
        builder.append(name);
        builder.append(description);
        builder.append(created.getTimeInMillis());
        builder.append(changed.getTimeInMillis());
        builder.append(visible);
        builder.append(tags.toCsv());
        builder.append(packages.toCsv());
        builder.append(addedAutomatically);

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
        CsvParser parser = new CsvParser(s);
        ArrayList<String> cells = parser.parseCsv();

        for (int i = 0; i < cells.size(); i++) {
            String cell = cells.get(i);
            try {
                switch (i) {
                    case 0:
                        setUuid(cell);
                        break;
                    case 1:
                        setName(cell);
                        break;
                    case 2:
                        setDescription(cell);
                        break;
                    case 3:
                        Calendar created = Calendar.getInstance();
                        created.setTimeInMillis(Long.parseLong(cell));
                        setCreated(created);
                        break;
                    case 4:
                        Calendar changed = Calendar.getInstance();
                        changed.setTimeInMillis(Long.parseLong(cell));
                        setChanged(changed);
                        break;
                    case 5:
                        setVisible(Boolean.parseBoolean(cell));
                        break;
                    case 6:
                        setTags(new TagCollection(cell));
                        break;
                    case 7:
                        setPackages(new PackageCollection(cell));
                        break;
                    case 8:
                        setAddedAutomatically(Boolean.parseBoolean(cell));
                        break;
                }
            }
            catch (NumberFormatException e) {
                throw new StorageException("Storage corrupt");
            }
        }
    }

    /**
     * Method returns a CSV-representation of the attribute names that are generated when calling
     * {@link #toStorable()}.
     *
     * @return  CSV-representation of the attribute names.
     */
    public static String getStorableAttributes() {
        CsvBuilder builder = new CsvBuilder();

        builder.append("UUID");
        builder.append("Name");
        builder.append("Description");
        builder.append("Created");
        builder.append("Edited");
        builder.append("IsVisible");
        builder.append("Tags");
        builder.append("Packages");
        builder.append("AddedAutomatically");

        return builder.toString();
    }

}
