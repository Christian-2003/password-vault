package de.passwordvault.view.entries.dialog_info_entry;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import de.passwordvault.R;
import de.passwordvault.view.utils.Utils;
import de.passwordvault.view.utils.components.PasswordVaultBottomSheetDialog;


/**
 * Class implements a dialog that displays some additional information about an entry to the user.
 *
 * @author  Christian-2003
 * @version 3.7.0
 */
public class InfoEntryDialog extends PasswordVaultBottomSheetDialog<InfoEntryViewModel> {

    /**
     * Attribute stores the key with which to pass the instance of {@link de.passwordvault.model.entry.EntryExtended}
     * as serializable argument.
     */
    public static final String ARG_ENTRY = "arg_entry";


    /**
     * Constructor instantiates a new dialog.
     */
    public InfoEntryDialog() {
        super(InfoEntryViewModel.class, R.layout.dialog_info_entry);
    }


    /**
     * Method is called whenever to create the view for the dialog.
     *
     * @param inflater              Layout inflater to use in order to inflate the dialog view.
     * @param container             Parent view of the dialog.
     * @param savedInstanceState    Previously saved state of the instance.
     * @return                      Created view.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (view == null) {
            return null;
        }
        viewModel.processArguments(getArguments());

        if (viewModel.getEntry() != null) {
            ((TextView)view.findViewById(R.id.text_title)).setText(viewModel.getEntry().getName());
            ((TextView)view.findViewById(R.id.text_created)).setText(Utils.formatDate(viewModel.getEntry().getCreated()));
            ((TextView)view.findViewById(R.id.text_changed)).setText(Utils.formatDate(viewModel.getEntry().getChanged()));
            view.findViewById(R.id.text_autofill).setVisibility(viewModel.getEntry().isAddedAutomatically() ? View.VISIBLE : View.GONE);
        }

        return view;
    }

}
