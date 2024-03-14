package de.passwordvault.service.autofill;

import android.os.CancellationSignal;
import android.service.autofill.AutofillService;
import android.service.autofill.FillCallback;
import android.service.autofill.FillRequest;
import android.service.autofill.SaveCallback;
import android.service.autofill.SaveRequest;
import androidx.annotation.NonNull;


/**
 * Class implements the autofill service for Password Vault. All autofill requests (fill / save) are
 * routed through this class by the operating system.
 *
 * @author  Christian-2003
 * @version 3.5.0
 */
public class PasswordVaultAutofillService extends AutofillService {

    /**
     * Method is called whenever the operating system requests the password vault autofill service
     * to fill out a view.
     *
     * @param request       Fill request which shall be carried out.
     * @param cancelSignal  Signal called when fill request cancels.
     * @param callback      Callback for the request
     */
    @Override
    public void onFillRequest(@NonNull FillRequest request, @NonNull CancellationSignal cancelSignal, @NonNull FillCallback callback) {
        FillRequestHandler handler = new FillRequestHandler(this);
        handler.onFillRequest(request, cancelSignal, callback);
    }


    /**
     * Method is called whenever the operating system requests the password vault autofill service
     * to save entered data from a remote view.
     *
     * @param request   Save request which shall be carried out.
     * @param callback  Callback for the request.
     */
    @Override
    public void onSaveRequest(@NonNull SaveRequest request, @NonNull SaveCallback callback) {
        callback.onFailure("Not implemented yet.");
    }

}
