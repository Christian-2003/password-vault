package de.passwordvault.view.entries.dialog_edit_tag;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;
import de.passwordvault.model.tags.Tag;
import de.passwordvault.model.tags.TagManager;


/**
 * Class implements the view model for the {@link EditTagDialog}.
 *
 * @author  Christian-2003
 * @version 3.7.0
 */
public class EditTagViewModel extends ViewModel {

    /**
     * Attribute stores the tag to edit. This is {@code null} if a new tag should be created.
     */
    @Nullable
    private Tag tag;

    /**
     * Attribute stores whether the arguments have been processed through a call to
     * {@link #processArguments(Bundle)}.
     */
    private boolean argsProcessed;


    /**
     * Constructor instantiates a new view model.
     */
    public EditTagViewModel() {
        tag = null;
        argsProcessed = false;
    }


    /**
     * Method returns the tag being edited by the dialog. This is {@code null} if a new tag should
     * be created.
     *
     * @return  Tag being edited / added.
     */
    @Nullable
    public Tag getTag() {
        return tag;
    }

    /**
     * Method changes the tag to edit.
     *
     * @param tag   Tag which is being edited / added.
     */
    public void setTag(@NonNull Tag tag) {
        this.tag = tag;
    }


    /**
     * Method processes the arguments passed to the dialog.
     *
     * @param args  Bundle containing all arguments.
     */
    public void processArguments(Bundle args) {
        if (argsProcessed) {
            return;
        }
        argsProcessed = true;
        if (args == null) {
            return;
        }
        if (args.containsKey(EditTagDialog.ARG_TAG)) {
            try {
                tag = (Tag)args.getSerializable(EditTagDialog.ARG_TAG);
            }
            catch (Exception e) {
                //Ignore...
            }
        }
    }


    /**
     * Method deletes the tag being edited from all tags.
     */
    public void deleteTag() {
        if (tag != null) {
            boolean removed = TagManager.getInstance().remove(tag);
            if (removed) {
                TagManager.getInstance().save();
            }
        }
    }

    /**
     * Method saves the tag being edited / added to permanent storage.
     *
     * @param addTag    Whether the tag was added (= {@code true}) or edited (= {@code false}).
     */
    public void saveTag(boolean addTag) {
        if (tag != null) {
            if (addTag) {
                TagManager.getInstance().add(tag);
            }
            TagManager.getInstance().save(true); //If tag name was edited, force saving is required.
        }
    }

}
