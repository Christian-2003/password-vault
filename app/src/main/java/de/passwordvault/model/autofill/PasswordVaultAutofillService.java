package de.passwordvault.model.autofill;

import android.app.assist.AssistStructure;
import android.os.CancellationSignal;
import android.service.autofill.AutofillService;
import android.service.autofill.Dataset;
import android.service.autofill.FillCallback;
import android.service.autofill.FillContext;
import android.service.autofill.FillRequest;
import android.service.autofill.FillResponse;
import android.service.autofill.SaveCallback;
import android.service.autofill.SaveRequest;
import android.util.Log;
import android.view.autofill.AutofillValue;
import android.widget.RemoteViews;
import androidx.annotation.NonNull;
import java.util.List;


public class PasswordVaultAutofillService extends AutofillService {

    @Override
    public void onFillRequest(@NonNull FillRequest request, @NonNull CancellationSignal cancellationSignal, @NonNull FillCallback callback) {
        List<FillContext> contexts = request.getFillContexts();
        AssistStructure structure = contexts.get(contexts.size() - 1).getStructure();
        ParsedStructure parsedStructure = parseStructure(structure);
        UserData userData = new UserData();
        userData.username = "testUsername";
        userData.password = "helloworld123";

        RemoteViews usernamePresentation = new RemoteViews(getPackageName(), android.R.layout.simple_list_item_1);
        usernamePresentation.setTextViewText(android.R.id.text1, "my_username");
        RemoteViews passwordPresentation = new RemoteViews(getPackageName(), android.R.layout.simple_list_item_1);
        passwordPresentation.setTextViewText(android.R.id.text1, "Password for my_username");

        try {
            Dataset.Builder datasetBuilder = new Dataset.Builder();
            if (parsedStructure.usernameId != null) {
                datasetBuilder.setValue(parsedStructure.usernameId, AutofillValue.forText(userData.username), usernamePresentation);
            }
            if (parsedStructure.passwordId != null) {
                datasetBuilder.setValue(parsedStructure.passwordId, AutofillValue.forText(userData.password), passwordPresentation);
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

    @Override
    public void onSaveRequest(@NonNull SaveRequest request, @NonNull SaveCallback callback) {
        callback.onFailure("Not implemented yet.");
    }



    private ParsedStructure parseStructure(AssistStructure structure) {
        generated = new ParsedStructure();
        traverseStructure(structure);
        return generated;
    }

    private ParsedStructure generated;

    private void traverseStructure(AssistStructure structure) {
        Log.d("AutofillService", "App: " + structure.getActivityComponent().getPackageName());
        int nodes = structure.getWindowNodeCount();
        for (int i = 0; i < nodes; i++) {
            AssistStructure.WindowNode windowNode = structure.getWindowNodeAt(i);
            AssistStructure.ViewNode viewNode = windowNode.getRootViewNode();
            traverseNode(viewNode);
        }
    }

    private void traverseNode(AssistStructure.ViewNode viewNode) {
        if (viewNode.getAutofillHints() != null && viewNode.getAutofillHints().length > 0) {
            String[] autofillHints = viewNode.getAutofillHints();
            for (String autofillHint : autofillHints) {
                Log.d("AutofillService", "Hint \"" + autofillHint + "\" for node \"" + viewNode.getTextIdEntry() + "\"");
                if (autofillHint.contains("password")) {
                    generated.passwordId = viewNode.getAutofillId();
                    Log.d("AutofillService", "retrieved password");
                    break;
                }
                else if (autofillHint.contains("username")) {
                    generated.usernameId = viewNode.getAutofillId();
                    break;
                }
            }
        }

        for (int i = 0; i < viewNode.getChildCount(); i++) {
            AssistStructure.ViewNode childNode = viewNode.getChildAt(i);
            traverseNode(childNode);
        }
    }

}
