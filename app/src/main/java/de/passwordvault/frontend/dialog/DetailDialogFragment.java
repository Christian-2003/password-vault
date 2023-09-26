package de.passwordvault.frontend.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import de.passwordvault.R;
import de.passwordvault.backend.entry.Detail;


/**
 * Class models a {@linkplain Dialog} which allows the user to enter or edit a {@linkplain de.passwordvault.backend.entry.Detail}.
 * The {@linkplain android.app.Activity} that creates this dialog, must implement the interface
 * {@linkplain DialogCallbackListener}!
 *
 * @author  Christian-2003
 * @version 1.0.1
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

        //Configure the title:
        if (detail == null) {
            builder.setTitle(R.string.detail_dialog_title_add);
            detail = new Detail();
        }
        else {
            builder.setTitle(R.string.detail_dialog_title_edit);
        }

        //Configure the layout:
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_detail, null);
        ((TextView)view.findViewById(R.id.detail_dialog_name)).setText(detail.getName());
        ((TextView)view.findViewById(R.id.detail_dialog_content)).setText(detail.getContent());
        ((CheckBox)view.findViewById(R.id.detail_dialog_obfuscated)).setChecked(detail.isObfuscated());
        ((CheckBox)view.findViewById(R.id.detail_dialog_encrypted)).setChecked(detail.isEncrypted());
        ((CheckBox)view.findViewById(R.id.detail_dialog_visible)).setChecked(detail.isVisible());
        ((Spinner)view.findViewById(R.id.detail_dialog_type)).setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, Detail.GET_TYPES(getContext())));
        if (detail.getType() != -1) {
            ((Spinner) view.findViewById(R.id.detail_dialog_type)).setSelection(detail.getType());
        }
        builder.setView(view);

        //Configure buttons
        builder.setPositiveButton(R.string.button_save, (dialog, id) -> {
            //Save button:
            processUserInput(view);
            callbackListener.onPositiveCallback(DetailDialogFragment.this);
        });
        builder.setNegativeButton(R.string.button_cancel, (dialog, id) -> {
            //Cancel button:
            callbackListener.onNegativeCallback(DetailDialogFragment.this);
        });

        return builder.create();
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
            throw new ClassCastException(getActivity().toString() + " must implement DetailDialogCallbackListener");
        }
    }


    /**
     * Method processes the users input and applies it to the edited detail.
     *
     * @param view  View from which the users input shall be retrieved.
     */
    private void processUserInput(View view) {
        if (view == null) {
            return;
        }
        String name = ((EditText)view.findViewById(R.id.detail_dialog_name)).getText().toString();
        String content = ((EditText)view.findViewById(R.id.detail_dialog_content)).getText().toString();
        int type = ((Spinner)view.findViewById(R.id.detail_dialog_type)).getSelectedItemPosition();
        boolean obfuscated = ((CheckBox)view.findViewById(R.id.detail_dialog_obfuscated)).isChecked();
        boolean encrypted = ((CheckBox)view.findViewById(R.id.detail_dialog_encrypted)).isChecked();
        boolean visible = ((CheckBox)view.findViewById(R.id.detail_dialog_visible)).isChecked();
        //Test whether anything was changed:
        if (!detail.getName().equals(name) || !detail.getContent().equals(content) || detail.getType() != type || detail.isObfuscated() != obfuscated || detail.isEncrypted() != encrypted || detail.isVisible() != visible) {
            detail.notifyDataChange();
            detail.setName(name);
            detail.setContent(content);
            detail.setType(type);
            detail.setObfuscated(obfuscated);
            detail.setEncrypted(encrypted);
            detail.setVisible(visible);
        }
    }

}
