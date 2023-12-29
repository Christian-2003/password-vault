package de.passwordvault.model.storage.app;

import java.util.ArrayList;
import de.passwordvault.App;
import de.passwordvault.R;
import de.passwordvault.model.detail.DetailDTO;
import de.passwordvault.model.entry.Entry;
import de.passwordvault.model.entry.EntryDTO;
import de.passwordvault.model.storage.encryption.EncryptionException;
import de.passwordvault.model.storage.file.EncryptedFileReader;
import de.passwordvault.model.storage.file.EncryptedFileWriter;


/**
 * Class implements a manager which can store the handled entries from the
 * {@link de.passwordvault.model.entry.EntryHandle} to secondary storage and vice versa.
 *
 * @author  Christian-2003
 * @version 3.0.0
 */
public class StorageManager {

    /**
     * Method saves the handled entries from the {@link de.passwordvault.model.entry.EntryHandle} to
     * secondary storage.
     *
     * @return  Whether the handled entries were successfully saved.
     */
    public boolean save() {
        //Convert handled entries into DTOs:
        InstanceToDTOConverter converter = new InstanceToDTOConverter();
        converter.generateDTOs();
        ArrayList<DetailDTO> detailDTOs = converter.getDetailDTOs();
        ArrayList<EntryDTO> entryDTOs = converter.getEntryDTOs();

        //Generate CSV from DTOs:
        StringBuilder detailCsv = new StringBuilder();
        for (int i = 0; i < detailDTOs.size(); i++) {
            detailCsv.append(detailDTOs.get(i).getCsv());
            if (i < detailDTOs.size() - 1) {
                detailCsv.append("\n");
            }
        }
        StringBuilder entryCsv = new StringBuilder();
        for (int i = 0; i < entryDTOs.size(); i++) {
            entryCsv.append(entryDTOs.get(i).getCsv());
            if (i < entryDTOs.size() - 1) {
                entryCsv.append("\n");
            }
        }

        //Save CSV to secondary storage:
        String entryFile = App.getContext().getString(R.string.entries_file);
        String detailsFile = App.getContext().getString(R.string.details_file);
        EncryptedFileWriter writer = new EncryptedFileWriter();
        try {
            writer.write(entryFile, entryCsv.toString());
            writer.write(detailsFile, detailCsv.toString());
        }
        catch (EncryptionException e) {
            return false;
        }
        return true;
    }


    /**
     * Method loads the entries from secondary storage. The loaded entries will be stored within
     * the {@link de.passwordvault.model.entry.EntryHandle}.
     *
     * @return  List of the loaded entries.
     */
    public ArrayList<Entry> load() {
        //Read file content:
        EncryptedFileReader reader = new EncryptedFileReader();
        String entryFile = App.getContext().getString(R.string.entries_file);
        String detailsFile = App.getContext().getString(R.string.details_file);
        String entryCsv = "";
        String detailCsv = "";
        try {
            entryCsv = reader.read(entryFile);
            detailCsv = reader.read(detailsFile);
        }
        catch (EncryptionException e) {
            return null;
        }

        //Convert content into DTO instances:
        ArrayList<EntryDTO> entryDTOs = new ArrayList<>();
        ArrayList<DetailDTO> detailDTOs = new ArrayList<>();
        if (entryCsv != null) {
            String[] entries = entryCsv.split("\n");
            for (String s : entries) {
                if (s != null && !s.isEmpty()) {
                    entryDTOs.add(new EntryDTO(s));
                }
            }
        }
        if (detailCsv != null) {
            String[] details = detailCsv.split("\n");
            for (String s : details) {
                if (s != null & !s.isEmpty()) {
                    detailDTOs.add(new DetailDTO(s));
                }
            }
        }

        //Convert DTOs into Entry-instances:
        DTOToInstanceConverter converter = new DTOToInstanceConverter(entryDTOs, detailDTOs);
        return converter.generateInstances();
    }

}
