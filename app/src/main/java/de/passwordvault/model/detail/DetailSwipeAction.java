package de.passwordvault.model.detail;

import androidx.annotation.NonNull;


/**
 * Enum contains fields describing the available swipe actions for details, when they are swiped.
 *
 * @author  Christian-2003
 * @version 3.6.0
 */
public enum DetailSwipeAction {

    /**
     * Field stores the swipe action for deleting a detail.
     */
    DELETE("delete"),

    /**
     * Field stores the swipe action for editing a detail.
     */
    EDIT("edit");


    /**
     * Field stores the value with which to store the swipe action within the preferences.
     */
    private final String preferencesValue;


    /**
     * Constructor instantiates a new enum field.
     *
     * @param preferencesValue  Value with which to store the swipe action within the preferences.
     */
    DetailSwipeAction(String preferencesValue) {
        this.preferencesValue = preferencesValue;
    }


    /**
     * Method returns the preferences value.
     *
     * @return  Value with which to store the swipe action within the preferences.
     */
    public String getPreferencesValue() {
        return preferencesValue;
    }


    /**
     * Method returns the swipe action from the passed preferences value. If an invalid preferences
     * value is passed, {@link #EDIT} is returned.
     *
     * @param preferencesValue  Preferences value from which to return the detail swipe action.
     * @return                  Swipe action from the passed preferences value.
     */
    public static DetailSwipeAction fromPreferencesValue(String preferencesValue) {
        for (DetailSwipeAction swipeAction : values()) {
            if (swipeAction.getPreferencesValue().equals(preferencesValue)) {
                return swipeAction;
            }
        }
        return EDIT;
    }


    /**
     * Method returns the preferences value as it determines which field of the enum
     * is printed.
     *
     * @return  Preferences value.
     */
    @NonNull
    @Override
    public String toString() {
        return preferencesValue;
    }

}
