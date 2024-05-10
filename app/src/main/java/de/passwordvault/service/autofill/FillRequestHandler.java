package de.passwordvault.service.autofill;

import android.app.PendingIntent;
import android.app.assist.AssistStructure;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Resources;
import android.os.CancellationSignal;
import android.service.autofill.AutofillService;
import android.service.autofill.Dataset;
import android.service.autofill.FillCallback;
import android.service.autofill.FillContext;
import android.service.autofill.FillRequest;
import android.service.autofill.FillResponse;
import android.util.Log;
import android.view.autofill.AutofillId;
import android.view.autofill.AutofillValue;
import android.widget.RemoteViews;
import java.util.ArrayList;
import java.util.List;
import de.passwordvault.R;
import de.passwordvault.model.security.login.Account;
import de.passwordvault.model.storage.Configuration;
import de.passwordvault.service.autofill.caching.ContentCache;
import de.passwordvault.service.autofill.caching.ContentCacheItem;
import de.passwordvault.service.autofill.caching.InvalidationCache;
import de.passwordvault.service.autofill.caching.InvalidationCacheItem;
import de.passwordvault.service.autofill.caching.MappingCache;
import de.passwordvault.service.autofill.caching.MappingCacheItem;
import de.passwordvault.service.autofill.structureparser.AssistStructureParser;
import de.passwordvault.service.autofill.structureparser.ParsedStructure;
import de.passwordvault.view.activities.AutofillAuthenticationActivity;


/**
 * Class models a handler which can handle an autofill request to fill in some remote view.
 *
 * @author  Christian-2003
 * @version 3.5.4
 */
public class FillRequestHandler {

    /**
     * Field stores the tag used for debugging messages.
     */
    private static final String TAG = "FillRequestHandler";


    /**
     * Attribute stores the autofill service for which the handler is created.
     */
    private final AutofillService autofillService;

    /**
     * Attribute stores the data fetcher used to fetch the data for the fill request.
     */
    private DataFetcher fetcher;


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
        fetcher = new DataFetcher();
    }


    /**
     * Method carries out the fill request.
     *
     * @param request       Fill request which shall be carried out.
     * @param cancelSignal  Signal called when fill request cancels.
     * @param callback      Callback for the fill request.
     */
    public void onFillRequest(FillRequest request, CancellationSignal cancelSignal, FillCallback callback) {
        Log.d(TAG, "Begin fill request");
        List<FillContext> contexts = request.getFillContexts();
        AssistStructure structure = contexts.get(contexts.size() - 1).getStructure();
        ParsedStructure parsedStructure = parseStructure(structure);

        ArrayList<UserData> userData;
        if (Configuration.useAutofillCaching()) {
            //Use caching:
            Log.d(TAG, "Use autofill caching");
            MappingCacheItem mappingCacheItem = (MappingCacheItem)MappingCache.getInstance().getItem(parsedStructure.getPackageName());
            if (mappingCacheItem == null) {
                Log.d(TAG, "Item for fill request not in cache");
                userData = fetcher.fetchUserDataForPackage(parsedStructure.getPackageName());
                if (!userData.isEmpty()) {
                    String[] uuids = new String[userData.size()];
                    for (int i = 0; i < userData.size(); i++) {
                        uuids[i] = userData.get(i).getEntryUuid();
                    }
                    MappingCache.getInstance().putItem(new MappingCacheItem(parsedStructure.getPackageName(), uuids));
                    for (UserData item : userData) {
                        ContentCache.getInstance().putItem(new ContentCacheItem(item.getEntryUuid(), item.getUsername(), item.getPassword(), item.getEntryName()));
                    }
                    MappingCache.getInstance().save();
                    ContentCache.getInstance().save();
                    Log.d(TAG, "Updated MappingCache");
                    Log.d(TAG, "Updated ContentCache");
                }
                else {
                    Log.d(TAG, "Did not update caches");
                }
            }
            else {
                Log.d(TAG, "Item for fill request in cache");
                userData = new ArrayList<>();
                String[] uuids = mappingCacheItem.getUuids();
                for (String uuid : uuids) {
                    InvalidationCacheItem invalidationCacheItem = (InvalidationCacheItem)InvalidationCache.getInstance().getItem(uuid);
                    if (invalidationCacheItem == null) {
                        Log.d(TAG, "Item valid");
                        ContentCacheItem contentCacheItem = (ContentCacheItem)ContentCache.getInstance().getItem(uuid);
                        if (contentCacheItem == null) {
                            Log.d(TAG, "Item not in ContentCache");
                            UserData data = fetcher.fetchUserDataForUuid(uuid);
                            if (data == null) {
                                mappingCacheItem.removeUuid(uuid);
                                MappingCache.getInstance().putItem(mappingCacheItem);
                                Log.d(TAG, "Updated MappingCache with removed ID");
                            }
                            else {
                                userData.add(data);
                            }
                            continue;
                        }
                        userData.add(new UserData(contentCacheItem.getEntryName(), contentCacheItem.getIdentifier(), contentCacheItem.getUsername(), contentCacheItem.getPassword()));
                    }
                    else {
                        Log.d(TAG, "Item invalid");
                        UserData data = fetcher.fetchUserDataForUuid(uuid);
                        Log.d(TAG, "Loaded item from app storage");
                        if (data == null) {
                            ContentCache.getInstance().removeItem(uuid);
                            mappingCacheItem.removeUuid(uuid);
                            MappingCache.getInstance().putItem(mappingCacheItem);
                            Log.d(TAG, "Updated MappingCache with removed ID");
                        }
                        else {
                            ContentCacheItem contentCacheItem = (ContentCacheItem)ContentCache.getInstance().getItem(uuid);
                            contentCacheItem.setCredentials(data.getUsername() == null ? "" : data.getUsername(), data.getPassword() == null ? "" : data.getPassword(), data.getEntryName());
                            ContentCache.getInstance().putItem(contentCacheItem);
                            userData.add(data);
                        }
                        InvalidationCache.getInstance().removeItem(uuid);
                    }
                }
                MappingCache.getInstance().save();
                ContentCache.getInstance().save();
                if (InvalidationCache.isLoaded()) {
                    InvalidationCache.getInstance().save();
                }
            }
        }
        else {
            //Do not use caching:
            userData = fetcher.fetchUserDataForPackage(parsedStructure.getPackageName());
        }

        try {
            FillResponse response = generateBuildResponse(userData, parsedStructure);
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
    private FillResponse generateBuildResponse(ArrayList<UserData> userData, ParsedStructure structure) {
        FillResponse.Builder responseBuilder = new FillResponse.Builder();
        if (userData.isEmpty()) {
            return responseBuilder.build();
        }

        //Generate the datasets:
        ArrayList<Dataset> datasets = new ArrayList<>();
        for (UserData data : userData) {
            if (data.getUsername() == null && data.getPassword() == null) {
                continue;
            }
            Dataset.Builder datasetBuilder = new Dataset.Builder();
            if (structure.getUsernameId() != null && data.getUsername() != null) {
                datasetBuilder.setValue(structure.getUsernameId(), AutofillValue.forText(data.getUsername()), generatePresentation(data.getUsername() == null || data.getUsername().isEmpty() ? data.getEntryName() : data.getUsername(), false));
            }
            if (structure.getPasswordId() != null && data.getPassword() != null) {
                datasetBuilder.setValue(structure.getPasswordId(), AutofillValue.forText(data.getPassword()), generatePresentation(data.getUsername() == null || data.getUsername().isEmpty() ? data.getEntryName() : data.getUsername(), true));
            }
            datasets.add(datasetBuilder.build());
        }

        if (Configuration.useAutofillAuthentication() && Account.getInstance().hasPassword()) {
            //Prompt the user to authenticate:
            Intent authenticationIntent = new Intent(autofillService, AutofillAuthenticationActivity.class);
            authenticationIntent.putExtra(AutofillAuthenticationActivity.KEY_DATASETS, datasets);
            IntentSender intentSender = PendingIntent.getActivity(autofillService, 1001, authenticationIntent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE).getIntentSender();

            AutofillId[] autofillIds;
            if (structure.getUsernameId() != null && structure.getPasswordId() != null) {
                autofillIds = new AutofillId[2];
                autofillIds[0] = structure.getUsernameId();
                autofillIds[1] = structure.getPasswordId();
            }
            else if (structure.getUsernameId() != null) {
                autofillIds = new AutofillId[1];
                autofillIds[0] = structure.getUsernameId();
            }
            else if (structure.getPasswordId() != null) {
                autofillIds = new AutofillId[1];
                autofillIds[0] = structure.getPasswordId();
            }
            else {
                autofillIds = new AutofillId[0];
            }

            responseBuilder.setAuthentication(autofillIds, intentSender, generateAuthenticationPresentation(userData.get(0).getEntryName()));
        }
        else {
            //Authentication not activated:
            for (Dataset dataset : datasets) {
                responseBuilder.addDataset(dataset);
            }
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
        RemoteViews presentation = new RemoteViews(autofillService.getPackageName(), R.layout.autofill_item_presentation);
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
     * Method generates the presentation that prompts the user to authenticate before they can access
     * the data.
     *
     * @param value                 Value to indicate the dataset. This is usually the username.
     * @return                      Generated remote view for presentation.
     * @throws NullPointerException The passed value is {@code null}.
     */
    private RemoteViews generateAuthenticationPresentation(String value) throws NullPointerException {
        if (value == null) {
            throw new NullPointerException();
        }
        RemoteViews presentation = new RemoteViews(autofillService.getPackageName(), R.layout.autofill_authentication_presentation);
        presentation.setTextViewText(R.id.autofill_authentication_presentation_text, autofillService.getBaseContext().getResources().getString(R.string.autofill_authentication_presentation).replace("{value}", value));
        return presentation;
    }

}
