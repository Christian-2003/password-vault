package de.passwordvault.view.utils.components;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.WindowManager;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import de.passwordvault.model.storage.settings.Config;
import de.passwordvault.view.settings.activity_webview.WebViewActivity;


/**
 * Class resembles a base class for the Password Vault application that handles tasks that are required
 * with nearly all activities within the application, such as view model management.
 *
 * @param <V>   Type of the view model. If no view model is used, use the superclass {@linkplain ViewModel}
 *              and pass {@code null} as reflection type with the constructor.
 * @author      Christian-2003
 * @version     3.7.2
 */
public class PasswordVaultActivity<V extends ViewModel> extends AppCompatActivity {

    /**
     * Attribute stores the view model of the activity.
     */
    protected V viewModel;

    /**
     * Attribute stores the view model class info required to get a view model.
     */
    @Nullable
    private final Class<V> viewModelType;

    /**
     * Attribute stores the resource id of the layout resource for the activity.
     */
    @LayoutRes
    private final int layoutRes;


    /**
     * Constructor instantiates a new activity.
     *
     * @param viewModelType Type info of the view model. Pass {@code null} if no view model is
     *                      required.
     * @param layoutRes     Layout resource for the activity.
     */
    public PasswordVaultActivity(@Nullable Class<V> viewModelType, @LayoutRes int layoutRes) {
        this.viewModelType = viewModelType;
        this.layoutRes = layoutRes;
    }


    /**
     * Method is called whenever the activity is created.
     *
     * @param savedInstanceState    Previously saved state of the instance.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layoutRes);
        if (viewModelType != null) {
            viewModel = new ViewModelProvider(this).get(viewModelType);
        }
    }


    /**
     * Method enables secure mode for the activity, if set in settings. This prevents screenshots
     * from being made.
     */
    protected void enableSecureModeIfRequired() {
        if (Config.getInstance().preventScreenshots.get()) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
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


    /**
     * Method opens the specified URL either in a browser, or within the app-internal web view.
     * This depends on the settings of the user within the app.
     *
     * @param url   URL to open.
     */
    protected void openUrlInBrowserOrApp(String url) {
        if (Config.getInstance().openResourcesInBrowser.get()) {
            Uri uri;
            try {
                uri = Uri.parse(url);
            }
            catch (Exception e) {
                return;
            }
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(uri);
            startActivity(intent);
        }
        else {
            Intent intent = new Intent(this, WebViewActivity.class);
            intent.putExtra(WebViewActivity.EXTRA_URL, url);
            startActivity(intent);
        }
    }


    /**
     * Method shares the specified data through the ShareSheet. If the passed mime type is
     * {@code null}, the mime type "text/plain" is used.
     *
     * @param data      Data to share.
     * @param mimeType  Mime type for the data.
     */
    protected void shareDataWithSheet(@NonNull String data, @Nullable String mimeType) {
        if (mimeType == null) {
            mimeType = "text/plain";
        }

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, data);
        sendIntent.setType(mimeType);

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }

}
