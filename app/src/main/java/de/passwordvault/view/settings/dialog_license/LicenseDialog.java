package de.passwordvault.view.settings.dialog_license;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import de.passwordvault.R;


public class LicenseDialog extends BottomSheetDialogFragment {

    public static final String ARG_TITLE = "arg_title";

    public static final String ARG_LICENSE = "arg_license";


    private LicenseViewModel viewModel;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(LicenseViewModel.class);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());

        Bundle args = getArguments();
        if (args != null) {
            viewModel.processArguments(args);
        }

        View view = getLayoutInflater().inflate(R.layout.dialog_license, null, false);
        ((TextView)view.findViewById(R.id.text_license)).setText(viewModel.getLicense());
        builder.setView(view);
        builder.setTitle(viewModel.getTitle());


        return builder.create();
    }

}
