package de.passwordvault.view.settings.dialog_license;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextClock;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import de.passwordvault.R;


public class LicenseDialog extends BottomSheetDialogFragment {

    public static final String ARG_TITLE = "arg_title";

    public static final String ARG_LICENSE = "arg_license";


    private LicenseViewModel viewModel;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(LicenseViewModel.class);
        Bundle args = getArguments();
        if (args != null) {
            viewModel.processArguments(args);
        }

        View view = inflater.inflate(R.layout.dialog_license, container, false);
        ((TextView)view.findViewById(R.id.text_title)).setText(viewModel.getTitle());
        ((TextView)view.findViewById(R.id.text_license)).setText(viewModel.getLicense());

        return view;
    }


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
