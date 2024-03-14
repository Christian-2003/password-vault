package de.passwordvault.service.autofill;

import android.app.assist.AssistStructure;
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
import java.util.List;
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
        UserData userData = new UserData("testUsername", "helloworld123");

        RemoteViews usernamePresentation = new RemoteViews(autofillService.getPackageName(), android.R.layout.simple_list_item_1);
        usernamePresentation.setTextViewText(android.R.id.text1, "my_username");
        RemoteViews passwordPresentation = new RemoteViews(autofillService.getPackageName(), android.R.layout.simple_list_item_1);
        passwordPresentation.setTextViewText(android.R.id.text1, "Password for my_username");

        try {
            Dataset.Builder datasetBuilder = new Dataset.Builder();
            if (parsedStructure.getUsernameId() != null) {
                datasetBuilder.setValue(parsedStructure.getUsernameId(), AutofillValue.forText(userData.getUsername()), usernamePresentation);
            }
            if (parsedStructure.getPasswordId() != null) {
                datasetBuilder.setValue(parsedStructure.getPasswordId(), AutofillValue.forText(userData.getPassword()), passwordPresentation);
            }
            FillResponse.Builder responseBuilder = new FillResponse.Builder();
            responseBuilder.addDataset(datasetBuilder.build());
            FillResponse response = responseBuilder.build();

            callback.onSuccess(response);
        }
        catch (Exception e) {
            Log.d("AutofillService", e.getMessage());
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

}
