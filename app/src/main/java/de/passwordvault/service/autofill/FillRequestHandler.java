package de.passwordvault.service.autofill;

import android.app.assist.AssistStructure;
import android.content.res.Resources;
import android.os.CancellationSignal;
import android.service.autofill.AutofillService;
import android.service.autofill.Dataset;
import android.service.autofill.FillCallback;
import android.service.autofill.FillContext;
import android.service.autofill.FillRequest;
import android.service.autofill.FillResponse;
import android.util.Log;
import android.view.autofill.AutofillValue;
import android.widget.RemoteViews;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import de.passwordvault.R;
import de.passwordvault.model.detail.Detail;
import de.passwordvault.model.detail.DetailType;
import de.passwordvault.model.entry.EntryAbbreviated;
import de.passwordvault.model.entry.EntryExtended;
import de.passwordvault.model.packages.Package;
import de.passwordvault.model.storage.app.StorageManager;
import de.passwordvault.service.autofill.structureparser.AssistStructureParser;
import de.passwordvault.service.autofill.structureparser.ParsedStructure;


/**
 * Class models a handler which can handle an autofill request to fill in some remote view.
 *
 * @author  Christian-2003
 * @version 3.5.0
 */
public class FillRequestHandler {

    /**
     * Attribute stores the autofill service for which the handler is created.
     */
    private final AutofillService autofillService;


    /**
     * Constructor instantiates a new fill request handler for the passed autofill service.
     *
     * @param autofillService       Autofill service for which to create the handler.
     * @throws NullPointerException The passed autofill service is {@code null}.
     */
    public FillRequestHandler(AutofillService autofillService) throws NullPointerException {
        if (autofillService == null) {
            throw new NullPointerException();
        }
        this.autofillService = autofillService;
    }


    /**
     * Method carries out the fill request.
     *
     * @param request       Fill request which shall be carried out.
     * @param cancelSignal  Signal called when fill request cancels.
     * @param callback      Callback for the fill request.
     */
    public void onFillRequest(FillRequest request, CancellationSignal cancelSignal, FillCallback callback) {
        List<FillContext> contexts = request.getFillContexts();
        AssistStructure structure = contexts.get(contexts.size() - 1).getStructure();
        ParsedStructure parsedStructure = parseStructure(structure);
        ArrayList<UserData> userData = fetchData(parsedStructure.getPackageName());

        try {
            FillResponse response = buildFillResponse(userData, parsedStructure);
            callback.onSuccess(response);
        }
        catch (Exception e) {
            Log.d("AutofillService", e.getMessage() == null ? "No message provided" : e.getMessage());
            callback.onFailure(e.getMessage());
        }
    }


    /**
     * Method parses the passed assist structure.
     *
     * @param structure Assist structure to be parsed.
     * @return          Parsed structure.
     */
    private ParsedStructure parseStructure(AssistStructure structure) {
        AssistStructureParser parser = new AssistStructureParser();
        return parser.parse(structure);
    }


    /**
     * Method generates the fill response which is returned to the app that called this service.
     *
     * @param userData  List of UserData instances to incorporate within the fill response.
     * @param structure Parsed structure for which the fill response shall be generated.
     * @return          Generated fill response.
     */
    private FillResponse buildFillResponse(ArrayList<UserData> userData, ParsedStructure structure) {
        FillResponse.Builder responseBuilder = new FillResponse.Builder();

        for (UserData data : userData) {
            if (data.getUsername() == null && data.getPassword() == null) {
                continue;
            }
            Dataset.Builder datasetBuilder = new Dataset.Builder();
            if (structure.getUsernameId() != null && data.getUsername() != null) {
                datasetBuilder.setValue(structure.getUsernameId(), AutofillValue.forText(data.getUsername()), generatePresentation(data.getUsername(), false));
            }
            if (structure.getPasswordId() != null && data.getPassword() != null) {
                datasetBuilder.setValue(structure.getPasswordId(), AutofillValue.forText(data.getPassword()), generatePresentation(data.getUsername() == null ? data.getEntryName() : data.getUsername(), true));
            }
            responseBuilder.addDataset(datasetBuilder.build());
        }

        return responseBuilder.build();
    }


    /**
     * Method generates a remote view which can be used to display the autofill dataset to the user
     * within another application.
     *
     * @param value                 Value to indicate the dataset. This is usually the username.
     * @param password              Flag indicates whether the generated presentation is used to
     *                              present a password (= {@code true}) or not (= {@code false}).
     * @return                      Generated remote view for presentation.
     * @throws NullPointerException The passed value is {@code null}.
     */
    private RemoteViews generatePresentation(String value, boolean password) throws NullPointerException {
        if (value == null) {
            throw new NullPointerException();
        }
        RemoteViews presentation = new RemoteViews(autofillService.getPackageName(), R.layout.list_item_autofill);
        Resources resources = autofillService.getBaseContext().getResources();
        if (password) {
            presentation.setTextViewText(R.id.list_item_autofill_text, resources.getString(R.string.autofill_password_presentation).replace("{value}", value));
        }
        else {
            presentation.setTextViewText(R.id.list_item_autofill_text, resources.getString(R.string.autofill_username_presentation).replace("{value}", value));
        }
        return presentation;
    }


    /**
     * Method fetches a list of UserData instances that contain information for autofill based on the
     * passed package name.
     *
     * @param packageName           Name of the package for which data shall be fetched.
     * @return                      List of fetched user data.
     * @throws NullPointerException The passed package name is {@code null}.
     */
    private ArrayList<UserData> fetchData(String packageName) throws NullPointerException {
        if (packageName == null) {
            throw new NullPointerException();
        }
        ArrayList<UserData> fetchedData = new ArrayList<>();
        StorageManager manager = new StorageManager();
        HashMap<String, EntryAbbreviated> entries;
        try {
            entries = manager.loadAbbreviatedEntries();
        }
        catch (Exception e) {
            return fetchedData;
        }
        for (EntryAbbreviated abbreviated : entries.values()) {
            for (Package p : abbreviated.getPackages()) {
                if (p.getPackageName().equals(packageName)) {
                    //Found entry:
                    EntryExtended extended;
                    try {
                        extended = manager.loadExtendedEntry(abbreviated);
                        fetchedData.add(generateUserDataForEntry(extended));
                    }
                    catch (Exception e) {
                        //Ignore...
                    }
                }
            }
        }
        return fetchedData;
    }


    /**
     * Method generates a UserData-instance for the passed extended entry.
     *
     * @param entry                 Entry for which to generate an instance of UserData.
     * @return                      Generated UserData-instance.
     * @throws NullPointerException The passed entry is {@code null}.
     */
    private UserData generateUserDataForEntry(EntryExtended entry) throws NullPointerException {
        if (entry == null) {
            throw new NullPointerException();
        }
        String username = null;
        String password = null;

        for (Detail detail : entry.getDetails()) {
            if (username == null && detail.isUsername()) {
                username = detail.getContent();
            }
            else if (password == null && detail.isPassword()) {
                password = detail.getContent();
            }
        }

        //If username or password were not specified, try to find next best option:
        if (username == null || password == null) {
            for (Detail detail : entry.getDetails()) {
                if (username == null && detail.getType() == DetailType.EMAIL) {
                    username = detail.getContent();
                }
                if (password == null && detail.getType() == DetailType.PASSWORD) {
                    password = detail.getContent();
                }
            }
        }

        return new UserData(entry.getName(), username, password);
    }

}
