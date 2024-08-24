package de.passwordvault.view.settings.dialog_recently_edited;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import de.passwordvault.R;
import de.passwordvault.view.utils.components.PasswordVaultBottomSheetDialog;


/**
 * Class implements a dialog that can be used to change the number of most recently edited entries
 * on the home fragment.
 *
 * @author  Christian-2003
 * @version 3.7.0
 */
public class RecentlyEditedDialog extends PasswordVaultBottomSheetDialog<RecentlyEditedViewModel> {

    /**
     * Constructor instantiates a new dialog.
     */
    public RecentlyEditedDialog() {
        super(RecentlyEditedViewModel.class, R.layout.dialog_recently_edited);
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
            //Configure dropdown:
            AutoCompleteTextView dropdown = view.findViewById(R.id.dropdown);
            dropdown.setText(viewModel.getSelectedOption(view.getContext()));
            dropdown.setAdapter(new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_dropdown_item, viewModel.getOptions(view.getContext())));
            dropdown.setOnItemClickListener((parent, view1, position, id) -> {
                if (position >= 0 && position < RecentlyEditedViewModel.OPTIONS.length) {
                    viewModel.setSelectedOption(position);
                    dismiss();
                }
            });

            //Configure dialog:
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
            builder.setTitle(R.string.settings_customization_home_recentlyedited);
            builder.setView(view);
            builder.setNegativeButton(R.string.button_cancel, (dialog, id) -> {
                dismiss();
            });
        }

        return view;
    }

}
