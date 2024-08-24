package de.passwordvault.view.settings.dialog_darkmode;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import de.passwordvault.R;
import de.passwordvault.view.utils.components.PasswordVaultBottomSheetDialog;


/**
 * Class implements a dialog that is used to change between light and dark mode for the application.
 *
 * @author  Christian-2003
 * @version 3.7.0
 */
public class DarkmodeDialog extends PasswordVaultBottomSheetDialog<DarkmodeViewModel> {

    /**
     * Constructor instantiates a new dialog.
     */
    public DarkmodeDialog() {
        super(DarkmodeViewModel.class, R.layout.dialog_darkmode);
    }


    /**
     * Method is called whenever the view of the dialog is created.
     *
     * @param inflater              Layout inflater to use in order to inflate the dialog view.
     * @param container             Parent view of the dialog.
     * @param savedInstanceState    Previously saved state of the instance.
     * @return                      View for the dialog.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        if (view != null) {
            RadioButton lightRadioButton = view.findViewById(R.id.radio_light);
            RadioButton darkRadioButton = view.findViewById(R.id.radio_dark);
            RadioButton systemRadioButton = view.findViewById(R.id.radio_system);
            switch (viewModel.getDarkmode()) {
                case DarkmodeViewModel.LIGHT:
                    lightRadioButton.setChecked(true);
                    break;
                case DarkmodeViewModel.DARK:
                    darkRadioButton.setChecked(true);
                    break;
                default:
                    systemRadioButton.setChecked(true);
                    break;
            }

            lightRadioButton.setOnCheckedChangeListener((button, checked) -> {
                if (checked) {
                    viewModel.setDarkmode(DarkmodeViewModel.LIGHT);
                    dismiss();
                }
            });

            darkRadioButton.setOnCheckedChangeListener((button, checked) -> {
                if (checked) {
                    viewModel.setDarkmode(DarkmodeViewModel.DARK);
                    dismiss();
                }
            });

            systemRadioButton.setOnCheckedChangeListener((button, checked) -> {
                if (checked) {
                    viewModel.setDarkmode(DarkmodeViewModel.SYSTEM);
                    dismiss();
                }
            });

            LinearLayout lightContainer = view.findViewById(R.id.container_light);
            LinearLayout darkContainer = view.findViewById(R.id.container_dark);
            LinearLayout systemContainer = view.findViewById(R.id.container_system);
            lightContainer.setOnClickListener(v -> lightRadioButton.setChecked(true));
            darkContainer.setOnClickListener(v -> darkRadioButton.setChecked(true));
            systemContainer.setOnClickListener(v -> systemRadioButton.setChecked(true));

            //Change color of the display of the system mode:
            updateSystemDisplay(view);
        }

        return view;
    }


    /**
     * Method updates the background tint colors for the views displaying the current ui mode of
     * the OS.
     *
     * @param view  View of the dialog.
     */
    private void updateSystemDisplay(@NonNull View view) {
        Context context = requireContext();
        ColorStateList background;
        ColorStateList head;
        ColorStateList trail;
        if (viewModel.isUsingDarkmode()) {
            background = ContextCompat.getColorStateList(context, R.color.dark_background);
            head = ContextCompat.getColorStateList(context, R.color.dark_highlight);
            trail = ContextCompat.getColorStateList(context, R.color.dark_container);
        }
        else {
            background = ContextCompat.getColorStateList(context, R.color.light_background);
            head = ContextCompat.getColorStateList(context, R.color.light_highlight);
            trail = ContextCompat.getColorStateList(context, R.color.light_container);
        }
        view.findViewById(R.id.container_system_display).setBackgroundTintList(background);
        view.findViewById(R.id.container_system_head1).setBackgroundTintList(head);
        view.findViewById(R.id.container_system_head2).setBackgroundTintList(head);
        view.findViewById(R.id.container_system_trail1).setBackgroundTintList(trail);
        view.findViewById(R.id.container_system_trail2).setBackgroundTintList(trail);
    }

}
