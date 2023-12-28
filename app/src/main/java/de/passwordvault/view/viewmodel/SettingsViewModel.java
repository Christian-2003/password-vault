package de.passwordvault.view.viewmodel;

import androidx.lifecycle.ViewModel;

import de.passwordvault.view.fragments.SettingsFragment;


/**
 * Class models a {@linkplain ViewModel} for the {@linkplain SettingsFragment} which contains all
 * relevant data that shall be persistent throughout activity changes.
 *
 * @author  Christian-2003
 * @version 2.2.2
 */
public class SettingsViewModel extends ViewModel {

    /**
     * Attribute stores the UI mode (light, dark or system default) for the application.
     * This is either 0 (System Default), 1 (Light Mode) or 2 (Dark Mode).
     */
    private int uiMode;


    /**
     * Constructor instantiates a new {@link SettingsFragment} with default values for all
     * attributes.
     */
    public SettingsViewModel() {
        uiMode = 0;
    }


    public int getUiMode() {
        return uiMode;
    }

    public void setUiMode(int uiMode) {
        this.uiMode = uiMode;
    }

}
