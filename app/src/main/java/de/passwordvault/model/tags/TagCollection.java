package de.passwordvault.model.tags;

import java.util.ArrayList;
import de.passwordvault.model.storage.csv.CsvBuilder;
import de.passwordvault.model.storage.csv.CsvParser;


/**
 * Class models a tag collection which contains any number of {@link Tag}-instances.
 *
 * @author  Christian-2003
 * @version 3.3.0
 */
public class TagCollection extends ArrayList<Tag> {

    /**
     * Constructor instantiates a new empty tag collection.
     */
    public TagCollection() {
        super();
    }

    /**
     * Constructor instantiates a new tag collection which contains the tags of the passed list.
     *
     * @param tags                  Tags for this collection.
     * @throws NullPointerException The passed list is {@code null}.
     */
    public TagCollection(ArrayList<Tag> tags) throws NullPointerException {
        super();
        if (tags == null) {
            throw new NullPointerException();
        }
        addAll(tags);
    }

    /**
     * Constructor instantiates a new tag collection which contains the tags of the passed CSV
     * representation. The passed argument must be generated through {@link #toCsv()}.
     *
     * @param csv                   CSV to be converted into this collection.
     * @throws NullPointerException The passed CSV is {@code null}.
     */
    public TagCollection(String csv) throws NullPointerException {
        super();
        if (csv == null) {
            throw new NullPointerException();
        }
        CsvParser parser = new CsvParser(csv);
        ArrayList<String> tagUuids = parser.parseCsv();
        for (String uuid : tagUuids) {
            if (uuid == null || uuid.isEmpty()) {
                continue;
            }
            Tag tag = TagManager.getInstance().getTag(uuid);
            if (tag != null) {
                add(tag);
            }
        }
    }


    /**
     * Method converts the contents of the tag collection into a CSV representation.
     *
     * @return  CSV representation of the tags.
     */
    public String toCsv() {
        CsvBuilder builder = new CsvBuilder();

        for (Tag tag : this) {
            builder.append(tag.getUuid());
        }

        return builder.toString();
    }

}
