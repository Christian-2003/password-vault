package de.passwordvault.view.utils.components;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import de.passwordvault.R;


/**
 * Clas implements the base activity for all activities within the app.
 *
 * @author  Christian-2003
 * @version 3.5.1
 */
public abstract class PasswordVaultBaseActivity extends AppCompatActivity {

    /**
     * Method displays an information dialog.
     *
     * @param titleId   Id of the resource-string for the dialog title.
     * @param messageId Id if the resource-string for the dialog message.
     */
    protected void showInfoDialog(int titleId, int messageId) {
        showInfoDialog(titleId, getString(messageId));
    }

    /**
     * Method displays an information dialog. If the passed message is {@code null}, an empty dialog
     * is shown.
     *
     * @param titleId   Id of the resource-string for the dialog title.
     * @param message   Message to be displayed.
     */
    protected void showInfoDialog(int titleId, String message) {
        AlertDialog dialog = new MaterialAlertDialogBuilder(this).create();
        dialog.setTitle(getString(titleId));
        if (message != null) {
            dialog.setMessage(message);
        }
        dialog.setButton(DialogInterface.BUTTON_NEUTRAL, getString(R.string.button_ok), (dialogInterface, i) -> dialogInterface.dismiss());
        dialog.show();
    }


    /**
     * Method opens the specified URL in the browser.
     *
     * @param url   URL to be opened.
     */
    protected void openUrl(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

}
