package de.passwordvault.view.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import de.passwordvault.R;
import de.passwordvault.view.utils.DialogCallbackListener;
import de.passwordvault.view.viewmodel.SettingsViewModel;
import de.passwordvault.view.activities.MainActivity;
import de.passwordvault.view.dialogs.UiModeDialogFragment;


/**
 * Class implements the {@linkplain Fragment} that displays all settings within the
 * {@linkplain MainActivity}.
 *
 * @author  Christian-2003
 * @version 3.0.0
 */
public class SettingsFragment extends Fragment implements DialogCallbackListener {

    /**
     * Attribute stores the {@linkplain androidx.lifecycle.ViewModel} for this fragment.
     */
    private SettingsViewModel viewModel;

    /**
     * Attribute stores the {@linkplain SharedPreferences} that are used to store all settings.
     */
    private SharedPreferences preferences;

    /**
     * Attribute stores the {@linkplain SharedPreferences.Editor} which is used to edit settings.
     */
    private SharedPreferences.Editor preferencesEditor;

    /**
     * Attribute stores the inflated view of the fragment.
     */
    private View inflated;

    /**
     * Attribute stores the tag that is used for logs.
     */
    private static final String TAG = "Settings";


    /**
     * Default constructor instantiates a new SettingsFragment.
     */
    public SettingsFragment() {
        // Required empty public constructor
    }


    /**
     * Method is called whenever the SettingsFragment is created or recreated.
     *
     * @param savedInstanceState    Previously saved state of the instance.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(SettingsViewModel.class);
        preferences = getContext().getSharedPreferences(getString(R.string.preferences_file), Context.MODE_PRIVATE);
        preferencesEditor = preferences.edit();
    }


    /**
     * Method is called whenever the {@linkplain View} for the SettingsFragment is created.
     *
     * @param inflater              LayoutInflater for the fragment.
     * @param container             Container which contains the fragment.
     * @param savedInstanceState    Previously saved state of the instance.
     * @return                      Generated view.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        inflated = inflater.inflate(R.layout.fragment_settings, container, false);

        //Update all settings:
        updateUiMode(preferences.getInt(getString(R.string.preferences_uimode), 0));

        //Add click listeners:
        inflated.findViewById(R.id.settings_ui_mode_clickable).setOnClickListener(view -> {
            UiModeDialogFragment dialog = new UiModeDialogFragment(viewModel.getUiMode(), SettingsFragment.this);
            dialog.setTargetFragment(SettingsFragment.this, 1);
            dialog.show(getActivity().getSupportFragmentManager(), "");
        });

        inflated.findViewById(R.id.settings_used_software_clickable).setOnClickListener(view -> startActivity(new Intent(getActivity(), OssLicensesMenuActivity.class)));

        return inflated;
    }


    /**
     * Method is called whenever the {@linkplain DialogFragment} is closed through the
     * 'positive' button (i.e. the SAVE button).
     *
     * @param obj    Dialog which called the method.
     */
    public void onPositiveCallback(DialogFragment obj) {
        if (obj instanceof UiModeDialogFragment) {
            UiModeDialogFragment dialog = (UiModeDialogFragment)obj;
            updateUiMode(dialog.getUiMode());
        }
    }

    /**
     * Method is called whenever the {@linkplain DialogFragment} is closed through the
     * 'negative' button (i.e. the CANCEL button).
     *
     * @param obj    Dialog which called the method.
     */
    public void onNegativeCallback(DialogFragment obj) {
        //Do nothing...
    }


    private void updateUiMode(int uiMode) {
        if (viewModel.getUiMode() == uiMode) {
            //UI mode was not changed:
            return;
        }
        viewModel.setUiMode(uiMode);
        preferencesEditor.putInt(getString(R.string.preferences_uimode), uiMode);
        preferencesEditor.apply();
        switch (viewModel.getUiMode()) {
            case 1:
                //Switch to light mode:
                ((TextView)inflated.findViewById(R.id.settings_ui_mode)).setText(getString(R.string.settings_ui_mode_light));
                getActivity().setTheme(R.style.Theme_PasswordVault_Light);
                Log.d(TAG, "Changed UI mode to: LIGHT MODE");
                break;
            case 2:
                //Switch to dark mode:
                ((TextView)inflated.findViewById(R.id.settings_ui_mode)).setText(getString(R.string.settings_ui_mode_dark));
                getActivity().setTheme(R.style.Theme_PasswordVault_Dark);
                Log.d(TAG, "Changed UI mode to: DARK MODE");
                break;
            default:
                //Switch to system mode:
                ((TextView)inflated.findViewById(R.id.settings_ui_mode)).setText(getString(R.string.settings_ui_mode_system));
                Log.d(TAG, "Changed UI mode to: SYSTEM DEFAULT MODE");
                break;
        }
    }

}
