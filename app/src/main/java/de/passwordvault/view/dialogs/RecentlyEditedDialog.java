package de.passwordvault.view.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import de.passwordvault.R;
import de.passwordvault.model.storage.Configuration;


/**
 * Class implements a dialog that can be used to change the number of most recently edited entries
 * on the home fragment.
 *
 * @author  Christian-2003
 * @version 3.5.2
 */
public class RecentlyEditedDialog extends DialogFragment {

    /**
     * Field stores all options that can be selected by the user.
     */
    private static final int[] OPTIONS = { 3, 5, 7, 10, 15 };


    /**
     * Method is called whenever the dialog is created.
     *
     * @param savedInstanceState    Previously saved state of the instance.
     * @return                      Created dialog.
     */
    @SuppressLint("InflateParams") //Ignore passing 'null' as root for the dialog's view.
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        View view = requireActivity().getLayoutInflater().inflate(R.layout.dialog_recently_edited, null, false);

        //Configure dropdown:
        AutoCompleteTextView dropdown = view.findViewById(R.id.dropdown);
        dropdown.setText(getSelectedOption(view.getContext()));
        dropdown.setAdapter(new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_dropdown_item, getOptions(view.getContext())));
        dropdown.setOnItemClickListener((parent, view1, position, id) -> {
            if (position >= 0 && position < OPTIONS.length) {
                Configuration.setNumberOfRecentlyEdited(OPTIONS[position]);
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
        return builder.create();
    }


    /**
     * Method returns the text displaying the currently selected item.
     *
     * @param context   Context from which to load string-resources.
     * @return          Text for the selected option.
     */
    private static String getSelectedOption(Context context) {
        return context.getString(R.string.settings_customization_home_recentlyedited_number_option).replace("{arg}", "" + Configuration.getNumberOfRecentlyEdited());
    }


    /**
     * Method returns an array containing all options to display to the user.
     *
     * @param context   Context from which to load string-resources.
     * @return          Array of strings containing all options.
     */
    private static String[] getOptions(Context context) {
        String[] options = new String[OPTIONS.length];
        String placeholderText = context.getString(R.string.settings_customization_home_recentlyedited_number_option);
        for (int i = 0; i < options.length; i++) {
            options[i] = placeholderText.replace("{arg}", "" + OPTIONS[i]);
        }
        return options;
    }

}
