package de.passwordvault.viewmodel.dialogs;

import android.os.Bundle;
import android.view.View;
import androidx.lifecycle.ViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.Objects;
import de.passwordvault.R;
import de.passwordvault.model.tags.Tag;
import de.passwordvault.model.tags.TagManager;
import de.passwordvault.view.dialogs.EditTagDialog;


/**
 * Class implements the view model for the {@link de.passwordvault.view.dialogs.EditTagDialog}.
 *
 * @author  Christian-2003
 * @version 3.3.0
 */
public class EditTagViewModel extends ViewModel {

    /**
     * Attribute stores the tag to be edited.
     */
    private Tag tag;

    /**
     * Attribute stores whether the dialog is used to create a new tag (= {@code true}) or if it
     * is used to edit an existing tag (= {@code false}).
     */
    private boolean creatingNewDialog;


    /**
     * Constructor instantiates a new view model without callback listener and emtpy tag.
     */
    public EditTagViewModel() {
        setTag(new Tag(""));
        creatingNewDialog = true;
    }


    /**
     * Method returns the tag that is being edited.
     *
     * @return  Tag that is being edited.
     */
    public Tag getTag() {
        return tag;
    }

    /**
     * Method changes the tag that is being edited.
     *
     * @param tag                   Tag to be edited.
     * @throws NullPointerException The passed argument is {@code null}.
     */
    public void setTag(Tag tag) throws NullPointerException {
        if (tag == null) {
            throw new NullPointerException();
        }
        this.tag = tag;
    }

    /**
     * Method returns whether the dialog is used to create a new tag.
     *
     * @return  Whether the dialog is used to create a new tag.
     */
    public boolean isCreatingNewDialog() {
        return creatingNewDialog;
    }

    /**
     * Method changes whether the dialog is used to create a new tag.
     *
     * @param creatingNewDialog Whether the dialog is used to create a new tag.
     */
    public void setCreatingNewDialog(boolean creatingNewDialog) {
        this.creatingNewDialog = creatingNewDialog;
    }


    /**
     * Method creates the view for the dialog.
     *
     * @param view  View from which to create the view.
     * @return      Created view.
     */
    public View createView(View view) {
        if (view == null) {
            return null;
        }

        TextInputEditText nameEditText = view.findViewById(R.id.edit_tag_name);
        nameEditText.setText(tag.getName());

        return view;
    }


    /**
     * Method processes the arguments that were passed to the {@link EditTagDialog}.
     *
     * @param args                  Passed arguments to be processed.
     * @throws ClassCastException   The {@linkplain java.io.Serializable} cannot be casted to the
     *                              respective instance.
     */
    public void processArguments(Bundle args) throws ClassCastException {
        if (args == null) {
            return;
        }

        //Process KEY_TAG:
        if (args.containsKey(EditTagDialog.KEY_TAG)) {
            setTag((Tag)args.getSerializable(EditTagDialog.KEY_TAG));
            setCreatingNewDialog(false);
        }
    }


    /**
     * Method processes the user's input. If the input is correct, it is applied to the tag and
     * saved automatically. In this case, {@code true} is returned. If the input is invalid, the
     * visuals are changed to inform the user about the invalid input and {@code false} is returned.
     *
     * @param view  View from which to retrieve the input.
     * @return      Whether the input was successfully applied.
     */
    public boolean processUserInput(View view) {
        if (view == null) {
            return false;
        }

        TextInputEditText nameEditText = view.findViewById(R.id.edit_tag_name);
        String name = Objects.requireNonNull(nameEditText.getText()).toString();
        TextInputLayout nameLayout = view.findViewById(R.id.edit_tag_name_hint);

        if (name.isEmpty()) {
            nameLayout.setError(view.getResources().getString(R.string.error_empty_input));
            return false;
        }

        if (name.equals(tag.getName())) {
            return true;
        }

        tag.setName(name);

        if (creatingNewDialog) {
            TagManager.getInstance().add(tag);
        }
        else {
            TagManager.getInstance().set(tag, tag.getUuid());
        }
        TagManager.getInstance().save();

        return true;
    }


    /**
     * Method removes the tag from the {@link TagManager}.
     */
    public void deleteTag() {
        if (creatingNewDialog) {
            return;
        }
        TagManager.getInstance().remove(tag);
        TagManager.getInstance().save();
    }

}
