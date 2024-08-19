package de.passwordvault.view.utils.components;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import java.io.Serializable;


/**
 * Class implements a generic bottom sheet dialog that handles some basic operations that are
 * required by (almost) all dialogs.
 *
 * @param <V>   Type of the view model to use for the dialog.
 * @author      Christian-2003
 * @version     3.7.0
 */
public class PasswordVaultBottomSheetDialog<V extends ViewModel> extends BottomSheetDialogFragment {

    /**
     * Interface can be used as a callback for a bottom sheet dialog.
     */
    public interface Callback extends Serializable {

        /**
         * Field stores the result code used with a callback whose dialog was closed successfully.
         */
        int RESULT_SUCCESS = 0;

        /**
         * Field stores the result code used with a callback whose dialog was closed on cancel.
         */
        int RESULT_CANCEL = 2;


        /**
         * Method is used as callback whenever a dialog closes.
         *
         * @param dialog        Dialog that was closed.
         * @param resultCode    Result code is either {@link #RESULT_SUCCESS} or {@link #RESULT_CANCEL}
         *                      and indicates how the dialog is closed.
         */
        void onCallback(PasswordVaultBottomSheetDialog<? extends ViewModel> dialog, int resultCode);

    }


    /**
     * Field stores the key with which to pass a callback as argument.
     */
    public static final String ARG_CALLBACK = "arg_callback";


    /**
     * Attribute stores the type of the view model for the dialog.
     */
    @Nullable
    private final Class<V> viewModelType;

    /**
     * Attribute stores the ID of the layout resource to use for the dialog.
     */
    @LayoutRes
    private final int layoutRes;

    /**
     * Attribute stores the view model for the dialog.
     */
    protected V viewModel;


    /**
     * Constructor instantiates a new dialog.
     *
     * @param viewModelType Type of the view model for the dialog. Pass {@code null} if no view model
     *                      is used for the dialog.
     * @param layoutRes     ID of the layout resource to use for the dialog.
     */
    public PasswordVaultBottomSheetDialog(@Nullable Class<V> viewModelType, @LayoutRes int layoutRes) {
        this.viewModelType = viewModelType;
        this.layoutRes = layoutRes;
    }


    /**
     * Method is called whenever a new view is created for the dialog.
     *
     * @param inflater              Layout inflater to use in order to inflate the dialog view.
     * @param container             Parent view of the dialog.
     * @param savedInstanceState    Previously saved state of the instance.
     * @return                      Inflated view for the dialog.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (viewModelType != null) {
            viewModel = new ViewModelProvider(this).get(viewModelType);
        }
        return inflater.inflate(layoutRes, container, false);
    }


    /**
     * Method is called whenever the dialog is created.
     *
     * @param savedInstanceState    Previously saved state of the instance.
     * @return                      Created dialog.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (getDialog() != null) {
            getDialog().setOnShowListener(view -> {
                BottomSheetDialog dialog = (BottomSheetDialog)view;
                View bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
                if (bottomSheet != null) {
                    BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            });
        }
        return super.onCreateDialog(savedInstanceState);
    }

}
