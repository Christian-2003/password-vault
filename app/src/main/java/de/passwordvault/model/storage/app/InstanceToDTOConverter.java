package de.passwordvault.model.storage.app;

import java.util.ArrayList;
import de.passwordvault.model.detail.DetailDTO;
import de.passwordvault.model.entry.Entry;
import de.passwordvault.model.entry.EntryDTO;
import de.passwordvault.model.entry.EntryHandle;
import de.passwordvault.model.detail.Detail;


/**
 * Class implements a converter which can convert the handled entries ({@link EntryHandle#getEntries()})
 * into their respective data transfer objects (DTOs) by calling {@link #generateDTOs()}.
 *
 * @author  Christian-2003
 * @version 3.0.0
 */
public class InstanceToDTOConverter {

    /**
     * Attribute contains the data transfer objects for all handled {@link Entry}-instances.
     */
    private final ArrayList<EntryDTO> entryDTOs;

    /**
     * Attribute contains the data transfer objects for all handled {@link Detail}-instances.
     */
    private final ArrayList<DetailDTO> detailDTOs;


    /**
     * Constructor instantiates a new {@link InstanceToDTOConverter} which can convert the contents
     * of {@link EntryHandle#getEntries()} to their respective data transfer objects by calling
     * {@link #generateDTOs()}.
     */
    public InstanceToDTOConverter() {
        entryDTOs = new ArrayList<>();
        detailDTOs = new ArrayList<>();
    }


    /**
     * Method returns a list of the entry DTOs.
     *
     * @return  List of entry DTOs.
     */
    public ArrayList<EntryDTO> getEntryDTOs() {
        return entryDTOs;
    }

    /**
     * Method returns a list of the detail DTOs.
     *
     * @return  List of detail DTOs.
     */
    public ArrayList<DetailDTO> getDetailDTOs() {
        return detailDTOs;
    }


    /**
     * Method generates the DTOs for the {@link Entry} and {@link Detail} instances based
     * on {@link EntryHandle#getEntries()}.
     */
    public void generateDTOs() {
        entryDTOs.clear();
        detailDTOs.clear();
        for (Entry entry : EntryHandle.getInstance().getEntries()) {
            entryDTOs.add(new EntryDTO(entry));
            for (Detail detail : entry.getDetails()) {
                detailDTOs.add(new DetailDTO(detail, entry.getUuid()));
            }
        }
    }

}
