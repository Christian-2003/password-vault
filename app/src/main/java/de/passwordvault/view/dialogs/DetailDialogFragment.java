package de.passwordvault.view.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import de.passwordvault.R;
import de.passwordvault.model.detail.DetailType;
import de.passwordvault.view.utils.DialogCallbackListener;
import de.passwordvault.model.detail.Detail;


/**
 * Class models a {@linkplain Dialog} which allows the user to enter or edit a {@linkplain Detail}.
 * The {@linkplain android.app.Activity} that creates this dialog, must implement the interface
 * {@linkplain DialogCallbackListener}!
 *
 * @author  Christian-2003
 * @version 3.0.0
 */
public class DetailDialogFragment extends DialogFragment {

    /**
     * Attribute stores the detail which shall be edited / created:
     */
    private Detail detail;

    /**
     * Attribute stores the callback listener which waits for this dialog to be closed.
     */
    private DialogCallbackListener callbackListener;

    /**
     * Attribute stores the view for the dialog.
     */
    private View view;


    /**
     * Constructor constructs a new DetailDialogFragment which allows the user to edit the passed
     * {@linkplain Dialog}. If there is no Detail to edit (and the user shall create a new Detail
     * instead), pass {@code null}.
     *
     * @param detail    Detail to be edited or {@code null} if a new detail shall be created.
     */
    public DetailDialogFragment(Detail detail) {
        if (detail == null) {
            this.detail = null;
        }
        else {
            this.detail = new Detail(detail);
        }
    }


    public Detail getDetail() {
        return detail;
    }


    /**
     * Method is called whenever a DetailDialog is created.
     *
     * @param savedInstanceState    Previously saved state of the instance.
     * @return                      Created dialog.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        setRetainInstance(true);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        view = requireActivity().getLayoutInflater().inflate(R.layout.dialog_detail, null);

        //Configure the title:
        if (detail == null) {
            builder.setTitle(R.string.detail_dialog_title_add);
            ((AutoCompleteTextView)view.findViewById(R.id.detail_dialog_type)).setText(DetailType.TEXT.getDisplayName());
            detail = new Detail();
        }
        else {
            builder.setTitle(R.string.detail_dialog_title_edit);
        }

        //Configure the layout:
        ((TextView)view.findViewById(R.id.detail_dialog_name)).setText(detail.getName());
        ((TextView)view.findViewById(R.id.detail_dialog_content)).setText(detail.getContent());
        ((CheckBox)view.findViewById(R.id.detail_dialog_obfuscated)).setChecked(detail.isObfuscated());
        ((CheckBox)view.findViewById(R.id.detail_dialog_visible)).setChecked(detail.isVisible());
        if (detail.getType() != DetailType.UNDEFINED) {
            ((AutoCompleteTextView)view.findViewById(R.id.detail_dialog_type)).setText(Detail.GET_TYPES()[detail.getType().ordinal()]);
        }
        ((AutoCompleteTextView)view.findViewById(R.id.detail_dialog_type)).setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, Detail.GET_TYPES()));
        builder.setView(view);

        //Add buttons:
        builder.setPositiveButton(R.string.button_save, (dialog, id) -> {
            //Action implemented in onStart()-method!
        });
        builder.setNegativeButton(R.string.button_cancel, (dialog, id) -> {
            //Action implemented in onStart()-method!
        });

        return builder.create();
    }


    /**
     * Method configures the ClickListeners of the dialog buttons whenever the dialog is started.
     */
    @Override
    public void onStart() {
        super.onStart();
        AlertDialog dialog = (AlertDialog)getDialog();
        if (dialog == null) {
            //No dialog available:
            return;
        }

        //Configure positive button:
        Button positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(view -> {
            if (!processUserInput(DetailDialogFragment.this.view)) {
                //Some necessary data was not entered:
                return;
            }
            dismiss();
            callbackListener.onPositiveCallback(DetailDialogFragment.this);
        });

        //Configure negative button:
        Button negativeButton = dialog.getButton(Dialog.BUTTON_NEGATIVE);
        negativeButton.setOnClickListener(view -> {
            dismiss();
            callbackListener.onNegativeCallback(DetailDialogFragment.this);
        });
    }


    /**
     * Method is called whenever this dialog window is attached to an activity.
     *
     * @param context               Context to which the dialog window is attached.
     * @throws ClassCastException   The activity which creates this dialog window does not implement
     *                              {@linkplain DialogCallbackListener}-interface.
     */
    @Override
    public void onAttach(@NonNull Context context) throws ClassCastException {
        super.onAttach(context);
        try {
            callbackListener = (DialogCallbackListener)context;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement DialogCallbackListener");
        }
    }


    /**
     * Method processes the users input and applies it to the edited detail. If some user data is
     * incorrect or missing, {@code false} is returned and the {@lnk Detail} will not be updated.
     * Otherwise the detail will be updated and {@code true} will be returned.
     *
     * @param view  View from which the users input shall be retrieved.
     * @return      Whether the user input was correct or not.
     */
    private boolean processUserInput(View view) {
        if (view == null) {
            return false;
        }
        String name = ((TextInputEditText)view.findViewById(R.id.detail_dialog_name)).getText().toString();
        String content = ((TextInputEditText)view.findViewById(R.id.detail_dialog_content)).getText().toString();
        DetailType type = Detail.GET_TYPE_BY_NAME(((AutoCompleteTextView)view.findViewById(R.id.detail_dialog_type)).getText().toString());
        boolean obfuscated = ((CheckBox)view.findViewById(R.id.detail_dialog_obfuscated)).isChecked();
        boolean visible = ((CheckBox)view.findViewById(R.id.detail_dialog_visible)).isChecked();
        //Test whether everything was entered:
        boolean somethingNotEntered = false;
        if (name == null || name.isEmpty()) {
            ((TextInputLayout)view.findViewById(R.id.detail_dialog_name_hint)).setError(getResources().getString(R.string.error_empty_input));
            somethingNotEntered = true;
        }
        else {
            ((TextInputLayout)view.findViewById(R.id.detail_dialog_name_hint)).setErrorEnabled(false);
        }
        if (content == null || content.isEmpty()) {
            ((TextInputLayout)view.findViewById(R.id.detail_dialog_content_hint)).setError(getResources().getString(R.string.error_empty_input));
            somethingNotEntered = true;
        }
        else {
            ((TextInputLayout)view.findViewById(R.id.detail_dialog_content_hint)).setErrorEnabled(false);
        }
        if (somethingNotEntered) {
            return false;
        }

        //Test whether anything was changed:
        if (!detail.getName().equals(name) || !detail.getContent().equals(content) || detail.getType() != type || detail.isObfuscated() != obfuscated || detail.isVisible() != visible) {
            detail.notifyDataChange();
            detail.setName(name);
            detail.setContent(content);
            detail.setType(type);
            detail.setObfuscated(obfuscated);
            detail.setVisible(visible);
        }
        return true;
    }

}
