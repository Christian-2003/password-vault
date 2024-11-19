package de.passwordvault.view.entries.dialog_detail;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;
import java.io.Serializable;
import de.passwordvault.model.detail.Detail;
import de.passwordvault.model.detail.DetailType;


/**
 * Class implements the view model for the {@link DetailDialog}.
 *
 * @author  Christian-2003
 * @version 3.7.0
 */
public class DetailViewModel extends ViewModel {

    /**
     * Attribute stores the detail to edit.
     */
    @Nullable
    private Detail detail;

    /**
     * Attribute indicates whether the dialog is used to edit (= {@code true}) or create a new
     * detail (= {@code false}).
     */
    private boolean editingDetail;

    /**
     * Attribute indicates whether the name of the detail was entered automatically (= {@code true}) or
     * by the user (= {@code false}).
     */
    private boolean nameEnteredAutomatically;

    /**
     * Attribute indicates whether the checkbox to obfuscate the content was set automatically
     * (= {@code true}) or by the user (= {@code false}).
     */
    private boolean obfuscatedEnteredAutomatically;

    /**
     * Attribute stores an array of all type names available with details.
     */
    @NonNull
    private final String[] typeNames;

    /**
     * Attribute stores a list of all detail types.
     */
    @NonNull
    private final DetailType[] types;


    /**
     * Constructor instantiates a new view model.
     */
    public DetailViewModel() {
        detail = null;
        nameEnteredAutomatically = true;
        obfuscatedEnteredAutomatically = true;
        editingDetail = false;
        types = DetailType.values();
        typeNames = new String[types.length - 1]; //Ignore last DetailType, since this is DetailType.UNDEFINED.
        for (int i = 0; i < typeNames.length; i++) {
            typeNames[i] = types[i].getDisplayName();
        }
    }


    /**
     * Method returns the detail to edit. This is {@code null} before {@link #processArguments(Bundle)}
     * is called. Afterwards, this is never {@code null}!
     *
     * @return  Detail to edit.
     */
    @Nullable
    public Detail getDetail() {
        return detail;
    }

    /**
     * Method returns whether the dialog is currently editing (= {@code true}) or creating a new
     * detail (= {@code false}).
     *
     * @return  Whether the dialog is currently editing a detail.
     */
    public boolean isEditingDetail() {
        return editingDetail;
    }

    /**
     * Method returns whether the name was entered automatically.
     *
     * @return  Whether the name was entered automatically.
     */
    public boolean isNameEnteredAutomatically() {
        return nameEnteredAutomatically;
    }

    /**
     * Method changes whether the name was entered automatically.
     *
     * @param nameEnteredAutomatically  Whether the name was entered automatically.
     */
    public void setNameEnteredAutomatically(boolean nameEnteredAutomatically) {
        this.nameEnteredAutomatically = nameEnteredAutomatically;
    }

    /**
     * Method returns whether obfuscated was entered automatically.
     *
     * @return  Whether obfuscated was entered automatically.
     */
    public boolean isObfuscatedEnteredAutomatically() {
        return obfuscatedEnteredAutomatically;
    }

    /**
     * Method changes whether obfuscated was entered automatically.
     *
     * @param obfuscatedEnteredAutomatically    Whether obfuscated was entered automatically.
     */
    public void setObfuscatedEnteredAutomatically(boolean obfuscatedEnteredAutomatically) {
        this.obfuscatedEnteredAutomatically = obfuscatedEnteredAutomatically;
    }

    /**
     * Method returns a list of all available types for details.
     *
     * @return  Array of all available types.
     */
    @NonNull
    public DetailType[] getTypes() {
        return types;
    }

    /**
     * Method returns an array of all display names for the detail types. The indices within the
     * array correspond to the indices within the array returned by {@link #getTypes()}.
     *
     * @return  Array containing all detail type names.
     */
    @NonNull
    public String[] getTypeNames() {
        return typeNames;
    }


    /**
     * Method returns the index of the passed type name within the array {@link #typeNames}. If the
     * name was not found, {@code -1} is returned.
     *
     * @param typeName  Type name whose index to return.
     * @return          Index of the passed type name.
     */
    public int getTypeIndexFromName(@Nullable String typeName) {
        if (typeName == null) {
            return -1;
        }
        for (int i = 0; i < typeNames.length; i++) {
            if (typeNames[i].equals(typeName)) {
                return i;
            }
        }
        return -1;
    }


    /**
     * Method processes the arguments passed to the dialog.
     *
     * @param args  Bundle containing the arguments.
     */
    public void processArguments(Bundle args) {
        if (detail != null) {
            return;
        }
        if (args != null) {
            if (args.containsKey(DetailDialog.ARG_DETAIL)) {
                Serializable detail = args.getSerializable(DetailDialog.ARG_DETAIL);
                if (detail instanceof Detail) {
                    this.detail = new Detail((Detail)detail);
                    editingDetail = true;
                    nameEnteredAutomatically = false;
                    obfuscatedEnteredAutomatically = false;
                }
            }
        }
        if (detail == null) {
            detail = new Detail();
            editingDetail = false;
        }
    }

}
