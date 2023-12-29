package de.passwordvault.model.storage.app;

import java.util.ArrayList;
import de.passwordvault.model.detail.DetailDTO;
import de.passwordvault.model.detail.Detail;
import de.passwordvault.model.entry.Entry;
import de.passwordvault.model.entry.EntryDTO;
import de.passwordvault.model.entry.EntryHandle;


/**
 * Class implements a converter which can convert the provided data transfer objects (DTOs) into
 * a list of handled {@link Entry}-instances and stores them within the {@link EntryHandle}.
 *
 * @author  Christian-2003
 * @version 3.1.0
 */
public class DTOToInstanceConverter {

    /**
     * Attribute stores the data transfer objects of the {@link Entry}.
     */
    private final ArrayList<EntryDTO> entryDTOs;

    /**
     * Attribute stores the data transfer objects of the {@link de.passwordvault.model.detail.Detail}.
     */
    private final ArrayList<DetailDTO> detailDTOs;


    /**
     * Constructor instantiates a new {@link DTOToInstanceConverter} which can convert the passed
     * DTOs into a list of {@link Entry}-instances.
     *
     * @param entryDTOs             List of entry DTOs to be converted.
     * @param detailDTOs            List of detail DTOs to be converted.
     * @throws NullPointerException One of the passed arguments is {@code null}.
     */
    public DTOToInstanceConverter(ArrayList<EntryDTO> entryDTOs, ArrayList<DetailDTO> detailDTOs) throws NullPointerException {
        if (entryDTOs == null) {
            throw new NullPointerException("Null is invalid list of entry DTOs");
        }
        if (detailDTOs == null) {
            throw new NullPointerException("Null is invalid list of detail DTOs");
        }
        this.entryDTOs = entryDTOs;
        this.detailDTOs = detailDTOs;
    }


    /**
     * Method generates the {@link Entry}- and {@link Detail}-instances based on the available
     * DTOs and returns the result.
     *
     * @return  Generated entries.
     */
    public ArrayList<Entry> generateInstances() {
        ArrayList<Entry> entries = new ArrayList<>();
        for (EntryDTO entryDTO : entryDTOs) {
            entries.add(entryDTO.getEntry());
        }
        for (DetailDTO detailDTO : detailDTOs) {
            for (Entry entry : entries) {
                if (entry.getUuid().equals(detailDTO.getEntryUuid())) {
                    entry.add(detailDTO.getDetail());
                    break;
                }
            }
        }
        return entries;
    }

}
