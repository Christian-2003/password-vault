package de.passwordvault.view.utils.components;

import android.os.Bundle;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;


/**
 * Class resembles a base class for the Password Vault application that handles tasks that are required
 * with nearly all activities within the application, such as view model management.
 *
 * @param <V>   Type of the view model. If no view model is used, use the superclass {@linkplain ViewModel}
 *              and pass {@code null} as reflection type with the constructor.
 * @author      Christian-2003
 * @version     3.7.0
 */
public class PasswordVaultActivity<V extends ViewModel> extends PasswordVaultBaseActivity {

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

}
