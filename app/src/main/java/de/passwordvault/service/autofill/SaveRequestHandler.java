package de.passwordvault.service.autofill;

import android.app.assist.AssistStructure;
import android.service.autofill.FillContext;
import android.service.autofill.SaveCallback;
import android.service.autofill.SaveRequest;
import android.view.View;
import androidx.annotation.NonNull;
import java.util.HashMap;
import java.util.List;
import de.passwordvault.App;
import de.passwordvault.R;
import de.passwordvault.model.detail.Detail;
import de.passwordvault.model.detail.DetailType;
import de.passwordvault.model.entry.EntryAbbreviated;
import de.passwordvault.model.entry.EntryExtended;
import de.passwordvault.model.packages.Package;
import de.passwordvault.model.packages.PackagesManager;
import de.passwordvault.model.storage.app.StorageManager;
import de.passwordvault.model.storage.encryption.EncryptionException;
import de.passwordvault.service.autofill.structureparser.AssistStructureParser;
import de.passwordvault.service.autofill.structureparser.ParsedStructure;


/**
 * Class handles all save requests from the autofill service.
 *
 * @author  Christian-2003
 * @version 3.5.4
 */
public class SaveRequestHandler {

    /**
     * Method is called whenever the operating system requests the password vault autofill service
     * to save entered data from a remote view.
     *
     * @param request   Save request which shall be carried out.
     * @param callback  Callback for the request.
     */
    public void onSaveRequest(@NonNull SaveRequest request, @NonNull SaveCallback callback) {
        List<FillContext> fillContexts = request.getFillContexts();
        AssistStructure assistStructure = fillContexts.get(fillContexts.size() - 1).getStructure();
        AssistStructureParser parser = new AssistStructureParser();
        ParsedStructure structure = parser.parse(assistStructure);

        //Generate entries:
        EntryExtended extended = generateEntry(structure);
        EntryAbbreviated abbreviated = new EntryAbbreviated(extended);

        StorageManager storageManager = new StorageManager();

        //Save data:
        try {
            HashMap<String, EntryAbbreviated> abbreviatedEntries;
            abbreviatedEntries = storageManager.loadAbbreviatedEntries();
            abbreviatedEntries.put(extended.getUuid(), abbreviated);
            storageManager.saveAbbreviatedEntries(abbreviatedEntries.values());
            storageManager.saveExtendedEntry(extended);
        }
        catch (EncryptionException e) {
            callback.onFailure(e.getMessage());
            return;
        }

        callback.onSuccess();
    }


    /**
     * Method generates an extended entry from the specified parsed structure.
     *
     * @param structure Parsed structure from which to create an extended entry.
     * @return          Generated extended entry.
     */
    private EntryExtended generateEntry(ParsedStructure structure) {
        EntryExtended extended = new EntryExtended();

        //Add details:
        if (structure.getPasswordText() != null && !structure.getPasswordText().isEmpty()) {
            Detail password = new Detail();
            password.setType(DetailType.PASSWORD);
            password.setPassword(true);
            password.setObfuscated(true);
            password.setContent(structure.getPasswordText());
            password.setName(DetailType.PASSWORD.getDisplayName());
            extended.add(password);
        }
        if (structure.getUsernameText() != null && !structure.getUsernameText().isEmpty()) {
            Detail username = null;
            if (structure.getUsernameHint().equals(View.AUTOFILL_HINT_USERNAME)) {
                username = new Detail();
                username.setType(DetailType.TEXT);
                username.setName(App.getContext().getString(R.string.detail_username));
            }
            else if (structure.getUsernameHint().equals(View.AUTOFILL_HINT_EMAIL_ADDRESS)) {
                username = new Detail();
                username.setType(DetailType.EMAIL);
                username.setName(DetailType.EMAIL.getDisplayName());
            }
            if (username != null) {
                username.setUsername(true);
                username.setContent(structure.getUsernameText());
                extended.add(username);
            }
        }

        //Edit extended entry:
        Package appPackage = PackagesManager.getInstance().getPackage(structure.getPackageName());
        if (appPackage != null) {
            extended.getPackages().add(appPackage);
            extended.setName(appPackage.getAppName());
        }
        else {
            extended.setName(App.getContext().getString(R.string.autofill_save_request_description));
        }
        extended.setDescription(App.getContext().getString(R.string.autofill_save_request_description));
        extended.setAddedAutomatically(true);

        return extended;
    }

}
