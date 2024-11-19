package de.passwordvault.view.entries.dialog_edit_entry;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;
import java.util.ArrayList;
import de.passwordvault.model.tags.Tag;
import de.passwordvault.model.tags.TagManager;
import de.passwordvault.view.utils.components.PasswordVaultBottomSheetDialog;


/**
 * Class implements the view model for the {@link EditEntryDialog}.
 *
 * @author  Christian-2003
 * @version 3.7.0
 */
public class EditEntryViewModel extends ViewModel {

    /**
     * Attribute stores the name entered with the dialog.
     */
    @NonNull
    private String name;

    /**
     * Attribute stores the description entered with the dialog.
     */
    @NonNull
    private String description;

    /**
     * Attribute stores a list of all selected tags.
     */
    @NonNull
    private final ArrayList<Tag> selectedTags;

    /**
     * Attribute stores whether the global list of tags (from {@link TagManager}) changed through
     * this dialog.
     */
    private boolean tagListChanged;


    /**
     * Constructor instantiates a new view model.
     */
    public EditEntryViewModel() {
        name = "";
        description = "";
        selectedTags = new ArrayList<>();
        tagListChanged = false;
    }


    /**
     * Method returns the name entered with the dialog.
     *
     * @return  Name entered with the dialog.
     */
    @NonNull
    public String getName() {
        return name;
    }

    /**
     * Method changes the name entered with the dialog.
     *
     * @param name  New name entered by the dialog.
     */
    public void setName(@NonNull String name) {
        this. name = name;
    }

    /**
     * Method returns the description entered with the dialog.
     *
     * @return  Description entered with the dialog.
     */
    @NonNull
    public String getDescription() {
        return description;
    }

    /**
     * Method changes the description entered with the dialog.
     *
     * @param description   New description entered with the dialog.
     */
    public void setDescription(@NonNull String description) {
        this.description = description;
    }

    /**
     * Method returns a list containing all selected tags.
     *
     * @return  List containing all selected tags.
     */
    @NonNull
    public ArrayList<Tag> getSelectedTags() {
        return selectedTags;
    }

    /**
     * Method returns a list containing all available tags.
     *
     * @return  List containing all available tags.
     */
    @NonNull
    public ArrayList<Tag> getAllTags() {
        return TagManager.getInstance().getData();
    }

    /**
     * Method returns whether the global list of tags (from {@link TagManager}) changed through this
     * dialog.
     *
     * @return  Whether the global tag list changed.
     */
    public boolean isTagListChanged() {
        return tagListChanged;
    }

    /**
     * Method changes whether the global list of tags (from {@link TagManager}) changed through this
     * dialog.
     *
     * @param tagListChanged    Whether the global tag list changed.
     */
    public void setTagListChanged(boolean tagListChanged) {
        this.tagListChanged = tagListChanged;
    }


    /**
     * Method processes the arguments passed to the {@link EditEntryDialog}.
     *
     * @param args  Bundle containing the dialog arguments.
     */
    public void processArgs(@Nullable Bundle args) {
        if (args == null) {
            return;
        }
        if (args.containsKey(EditEntryDialog.ARG_NAME)) {
            name = args.getString(EditEntryDialog.ARG_NAME, "");
        }
        if (args.containsKey(EditEntryDialog.ARG_DESCRIPTION)) {
            description = args.getString(EditEntryDialog.ARG_DESCRIPTION, "");
        }
        if (args.containsKey(EditEntryDialog.ARG_TAGS)) {
            try {
                ArrayList<Tag> selectedTags = (ArrayList<Tag>)args.getSerializable(EditEntryDialog.ARG_TAGS);
                if (selectedTags != null) {
                    this.selectedTags.clear();
                    this.selectedTags.addAll(selectedTags);
                }
            }
            catch (ClassCastException e) {
                //Ignore...
            }
        }
    }

}
