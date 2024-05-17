package de.passwordvault.model.tags;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.ArrayList;
import de.passwordvault.App;
import de.passwordvault.R;
import de.passwordvault.model.GenericManager;
import de.passwordvault.model.PersistableManager;
import de.passwordvault.model.entry.EntryAbbreviated;
import de.passwordvault.model.entry.EntryExtended;
import de.passwordvault.model.entry.EntryManager;
import de.passwordvault.model.storage.csv.CsvBuilder;
import de.passwordvault.model.storage.csv.CsvParser;


/**
 * Class implements the tag manager which manages all available tags for
 * {@link de.passwordvault.model.entry.EntryAbbreviated}- instances. The class is implemented using
 * singleton-pattern. The singleton-instance can be retrieved through {@link #getInstance()}. When
 * accessing the singleton-instance for the first time, all previously saved tags are loaded from
 * shared preferences. Tags must be saved manually through {@link #save()}.
 *
 * @author  Christian-2003
 * @version 3.5.4
 */
public class TagManager extends GenericManager<Tag> implements PersistableManager {

    /**
     * Field stores the singleton-instance of the tag manager.
     */
    private static TagManager singleton;



    /**
     * Constructor instantiates a new tag manager instance and automatically loads previously saved
     * tags from shared preferences.
     * The constructor is private to comply with singleton-pattern.
     */
    private TagManager() {
        super();
        load();
    }


    /**
     * Static method returns the {@link #singleton}-instance of the tag manager.
     *
     * @return  Singleton-instance of the tag manager.
     */
    public static TagManager getInstance() {
        if (singleton == null) {
            singleton = new TagManager();
        }
        return singleton;
    }


    /**
     * Method removes the passed item from the managed items.
     *
     * @param item                  Item which shall be removed.
     * @return                      Whether the item was removed.
     * @throws NullPointerException The passed item is {@code null}.
     */
    @Override
    public boolean remove(Tag item) throws NullPointerException {
        if (super.remove(item)) {
            deleteTagFromAllEntries(item);
            return true;
        }
        return false;
    }

    /**
     * Method removes the item with the passed UUID from the managed items.
     *
     * @param uuid                  UUID of the item to be removed.
     * @return                      Removed item or {@code null} if no item could be removed.
     * @throws NullPointerException The passed UUID is {@code null}.
     */
    @Override
    public Tag remove(String uuid) throws NullPointerException {
        Tag removed = super.remove(uuid);
        if (removed != null) {
            deleteTagFromAllEntries(removed);
        }
        return removed;
    }


    /**
     * Method saves the managed tags to the shared preferences.
     */
    @Override
    public void save() {
        save(false);
    }

    /**
     * Method saves the managed tags to the shared preferences.
     *
     * @param force Whether tags shall be saved even when no changes were made.
     */
    @Override
    public void save(boolean force) {
        if (!changesMade && !force) {
            return;
        }
        SharedPreferences.Editor editor = App.getContext().getSharedPreferences(App.getContext().getString(R.string.preferences_file), Context.MODE_PRIVATE).edit();

        editor.putString(App.getContext().getString(R.string.preferences_tags), toCsv());

        editor.apply();
    }


    /**
     * Method loads previously stored tags from the shared preferences.
     */
    @Override
    public void load() {
        SharedPreferences preferences = App.getContext().getSharedPreferences(App.getContext().getString(R.string.preferences_file), Context.MODE_PRIVATE);

        String tagsContent = preferences.getString(App.getContext().getString(R.string.preferences_tags), "");
        fromCsv(tagsContent);
    }


    /**
     * Converts the managed tags into a CSV representation.
     *
     * @return  CSV representation of the tags.
     */
    public String toCsv() {
        CsvBuilder builder = new CsvBuilder();

        for (Tag tag : items.values()) {
            builder.append(tag.getUuid());
            builder.append(tag.getName());
            builder.newLine();
        }

        return builder.toString();
    }


    /**
     * Converts a CSV representation of the tags into tags.
     *
     * @param csv   CSV to convert into tags.
     */
    public void fromCsv(String csv) {
        if (csv == null || csv.isEmpty()) {
            return;
        }
        items.clear();

        String[] lines = csv.split("\n");
        for (String line : lines) {
            if (line == null || line.isEmpty()) {
                continue;
            }
            CsvParser parser = new CsvParser(line);
            ArrayList<String> tagContent = parser.parseCsv();
            if (tagContent.size() != 2) {
                //Tag corrupt:
                continue;
            }
            add(new Tag(tagContent.get(1), tagContent.get(0)));
        }
    }


    /**
     * Method starts a thread that removes the passed tag from all entries.
     *
     * @param deleteTag Tag which was deleted and now needs to be removed from all entries.
     */
    private void deleteTagFromAllEntries(Tag deleteTag) {
        Thread deleteThread = new Thread(() -> {
            for (EntryAbbreviated entry : EntryManager.getInstance().getData()) {
                entry.getTags().removeIf(tag -> tag.equals(deleteTag));
            }
            for (EntryExtended entry : EntryManager.getInstance().getExtendedEntryCache()) {
                entry.getTags().removeIf(tag -> tag.equals(deleteTag));
            }
        });
        deleteThread.start();
    }


    /**
     * Method returns the header that describes the columns for the CSV generated when calling
     * {@link #toCsv()}.
     *
     * @return  Header for the CSV-representation of the tags.
     */
    public static String getStorableAttributes() {
        return "UUID,Name";
    }

}
