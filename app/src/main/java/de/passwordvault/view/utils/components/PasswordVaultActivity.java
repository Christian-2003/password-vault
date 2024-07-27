package de.passwordvault.view.utils.components;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;


/**
 * Class resembles a base class for the Password Vault application that handles tasks that are required
 * with nearly all activities within the application, such as view model management.
 *
 * @param <T>   Type of the view model. If no view model is used, use the superclass {@linkplain ViewModel}
 *              and pass {@code null} as reflection type with the constructor.
 * @author      Christian-2003
 * @version     3.7.0
 */
public class PasswordVaultActivity<T extends ViewModel> extends PasswordVaultBaseActivity {

    /**
     * Attribute stores the view model of the activity.
     */
    @Nullable
    protected T viewModel;

    /**
     * Attribute stores the view model class info required to get a view model.
     */
    @Nullable
    private final Class<T> viewModelType;


    /**
     * Constructor instantiates a new activity.
     *
     * @param viewModelType Type info of the view model. Pass {@code null} if no view model is
     *                      required.
     */
    public PasswordVaultActivity(@Nullable Class<T> viewModelType) {
        this.viewModelType = viewModelType;
    }


    /**
     * Method is called whenever the activity is created.
     *
     * @param savedInstanceState    Previously saved state of the instance.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (viewModelType != null) {
            viewModel = new ViewModelProvider(this).get(viewModelType);
        }
    }

}
