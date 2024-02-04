package de.passwordvault.model.tags;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.ArrayList;
import de.passwordvault.App;
import de.passwordvault.R;
import de.passwordvault.model.Observable;
import de.passwordvault.model.Observer;
import de.passwordvault.model.storage.csv.CsvBuilder;
import de.passwordvault.model.storage.csv.CsvParser;


/**
 * Class implements the tag manager which manages all available tags for {@link de.passwordvault.model.entry.Entry}-
 * instances. The class is implemented using singleton-pattern. The singleton-instance can be
 * retrieved through {@link #getInstance()}. When accessing the singleton-instance for the first time,
 * all previously saved tags are loaded from shared preferences. Tags must be saved manually through
 * {@link #save()}.
 *
 * @author  Christian-2003
 * @version 3.3.0
 */
public class TagManager implements Observable<ArrayList<Tag>> {

    /**
     * Field stores the singleton-instance of the tag manager.
     */
    private static TagManager singleton;


    /**
     * Attribute stores all registered observers.
     */
    private final ArrayList<Observer<ArrayList<Tag>>> observers;

    /**
     * Attribute stores the tags that are being managed.
     */
    private final ArrayList<Tag> tags;

    /**
     * Attribute stores whether changes were made to the tags that require saving the tags.
     */
    private boolean stagedChanges;


    /**
     * Constructor instantiates a new tag manager instance and automatically loads previously saved
     * tags from shared preferences.
     * The constructor is private to comply with singleton-pattern.
     */
    private TagManager() {
        observers = new ArrayList<>();
        tags = new ArrayList<>();
        stagedChanges = false;
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
     * Method adds the specified tag to the managed tags.
     *
     * @param tag                   Tag to be added.
     * @throws NullPointerException The passed tag is {@code null}.
     */
    public void addTag(Tag tag) throws NullPointerException {
        if (tag == null) {
            throw new NullPointerException();
        }
        tags.add(tag);
        stagedChanges = true;
        notifyObservers();
    }


    /**
     * Method replaces the tag at the specified index with the passed argument.
     *
     * @param tag                           Tag with which to replace the tag at the specified index.
     * @param index                         Index at which to replace the tag.
     * @throws NullPointerException         The passed tag is {@code null}.
     * @throws IndexOutOfBoundsException    The passed index is out of bounds.
     */
    public void setTag(Tag tag, int index) throws NullPointerException, IndexOutOfBoundsException {
        if (tag == null) {
            throw new NullPointerException();
        }
        tags.set(index, tag);
        stagedChanges = true;
        notifyObservers();
    }

    /**
     * Method replaces one of the managed tags that has the specified UUID with the passed tag.
     *
     * @param tag                   Tag with which to replace the managed tag.
     * @param uuid                  UUID of the tag to be replaced.
     * @throws NullPointerException One of the arguments is {@code null}.
     */
    public void setTag(Tag tag, String uuid) throws NullPointerException {
        if (tag == null) {
            throw new NullPointerException();
        }
        int index = indexOf(uuid);
        if (index != -1) {
            tags.set(index, tag);
        }
        stagedChanges = true;
        notifyObservers();
    }


    /**
     * Method returns the tag with the specified UUID. If no tag with the specified UUID exists,
     * {@code null} is returned.
     *
     * @param uuid                  UUID of the tag to be returned.
     * @return                      Tag with the specified UUID.
     * @throws NullPointerException The passed UUID is {@code null}.
     */
    public Tag getTag(String uuid) throws NullPointerException {
        int index = indexOf(uuid);
        if (index != -1) {
            return tags.get(index);
        }
        return null;
    }


    /**
     * Method removes the passed tag from the managed tags.
     *
     * @param tag                   Tag to be removed.
     * @return                      Whether the tag was removed.
     * @throws NullPointerException The passed tag is {@code null}.
     */
    public boolean removeTag(Tag tag) throws NullPointerException {
        if (tag == null) {
            throw new NullPointerException();
        }
        boolean removed = tags.remove(tag);
        if (removed) {
            stagedChanges = true;
            notifyObservers();
        }
        return removed;
    }

    /**
     * Method removes the tag at the specified index from the managed tags.
     *
     * @param index                 Index of the tag to be removed.
     * @return                      Whether the tag was removed.
     * @throws NullPointerException The passed tag is {@code null}.
     */
    public boolean removeTag(int index) throws NullPointerException {
        boolean removed = tags.remove(index) != null;
        if (removed) {
            stagedChanges = true;
            notifyObservers();
        }
        return removed;
    }

    /**
     * Method removes the tag with the specified UUID from the managed tags.
     *
     * @param uuid                  UUID of the tag to be removed.
     * @return                      Whether the tag was removed.
     * @throws NullPointerException The passed tag is {@code null}.
     */
    public boolean removeTag(String uuid) throws NullPointerException {
        int index = indexOf(uuid);
        if (index != -1) {
            boolean removed = removeTag(index);
            if (removed) {
                stagedChanges = true;
                notifyObservers();
            }
            return removed;
        }
        return false;
    }


    /**
     * Method saves the managed tags to the shared preferences.
     */
    public void save() {
        save(false);
    }

    /**
     * Method saves the managed tags to the shared preferences.
     *
     * @param force Whether tags shall be saved even when no changes were made.
     */
    public void save(boolean force) {
        if (!stagedChanges && !force) {
            return;
        }
        SharedPreferences.Editor editor = App.getContext().getSharedPreferences(App.getContext().getString(R.string.preferences_file), Context.MODE_PRIVATE).edit();
        CsvBuilder builder = new CsvBuilder();

        for (Tag tag : tags) {
            builder.append(tag.getUuid());
            builder.append(tag.getName());
            builder.newLine();
        }

        editor.putString(App.getContext().getString(R.string.preferences_tags), builder.toString());

        editor.apply();
    }


    /**
     * Method returns the data which is being observed. This method must always return the newest
     * data from the implemented instance.
     *
     * @return  Newest data which is being observed.
     */
    @Override
    public ArrayList<Tag> getData() {
        return tags;
    }


    /**
     * Method registers the passed {@link Observer}. Whenever the relevant data of the implementing
     * class is changed, all Observers which were previously registered through this method are
     * informed about the changed data.
     *
     * @param o                     Observer to be registered.
     * @throws NullPointerException The passed Observer is {@code null}.
     */
    @Override
    public void addObserver(Observer<ArrayList<Tag>> o) throws NullPointerException {
        if (o == null) {
            throw new NullPointerException("Null is invalid Observer");
        }
        observers.add(o);
    }


    /**
     * Method notifies all registered {@link Observer}s about a change of the observed data through
     * their {@link Observer#update(Observable)}-method.
     */
    @Override
    public void notifyObservers() {
        for (Observer<ArrayList<Tag>> o : observers) {
            o.update(this);
        }
    }


    /**
     * Method removes the specified {@link Observer} from the implementing class. When the observed
     * data is changed in the future, the removed Observer will not be informed.
     *
     * @param o                     Observer to be removed from the implementing class' observers.
     * @return                      Whether the specified Observer was successfully removed.
     * @throws NullPointerException The passed Observer is {@code null}.
     */
    @Override
    public boolean removeObserver(Observer<ArrayList<Tag>> o) throws NullPointerException {
        if (o == null) {
            throw new NullPointerException("Null is invalid Observer");
        }
        return observers.remove(o);
    }


    /**
     * Method returns the index of the specified UUID within the list of tags. If no tag with the
     * specified UUID exists, {@code -1} is returned instead.
     *
     * @param uuid                  UUID whose tag to search.
     * @return                      Index of the tag with the specified UUID.
     * @throws NullPointerException The passed UUID is {@ode null}.
     */
    private int indexOf(String uuid) {
        if (uuid == null) {
            throw new NullPointerException();
        }
        int index = 0;
        for (Tag tag : tags) {
            if (tag.getUuid().equals(uuid)) {
                return index;
            }
            index++;
        }
        return -1;
    }


    /**
     * Method loads previously stored tags from the shared preferences.
     */
    private void load() {
        SharedPreferences preferences = App.getContext().getSharedPreferences(App.getContext().getString(R.string.preferences_file), Context.MODE_PRIVATE);

        String tagsContent = preferences.getString(App.getContext().getString(R.string.preferences_tags), "");
        if (tagsContent == null || tagsContent.isEmpty()) {
            return;
        }

        String[] lines = tagsContent.split("\n");
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
            tags.add(new Tag(tagContent.get(1), tagContent.get(0)));
        }
    }

}
