package de.passwordvault.view.passwords.activity_list;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import de.passwordvault.model.analysis.passwords.Password;
import de.passwordvault.model.analysis.passwords.PasswordSecurityAnalysis;

public class PasswordAnalysisListViewModel extends ViewModel {

    /**
     * Attribute stores all passwords that are analyzed.
     */
    @NonNull
    private final ArrayList<Password> passwords;

    /**
     * Attribute stores the search query entered by the user. This is {@code null} if the search bar
     * is not visible and no searching shall be performed.
     */
    @Nullable
    private String searchQuery;


    /**
     * Constructor instantiates a new view model.
     */
    public PasswordAnalysisListViewModel() {
        passwords = PasswordSecurityAnalysis.getInstance().getData();
        searchQuery = null;
    }


    /**
     * Method returns the list of passwords that are being displayed.
     *
     * @return  List of analyzed passwords.
     */
    @NonNull
    public ArrayList<Password> getPasswords() {
        return passwords;
    }


    /**
     * Method returns the search query entered by the user. This is {@code null} if the search bar
     * is currently not visible.
     *
     * @return  Search query.
     */
    @Nullable
    public String getSearchQuery() {
        return searchQuery;
    }

    /**
     * Method changes the search query entered by the user. Pass {@code null} to indicate that no
     * searching shall be performed.
     *
     * @param searchQuery   New search query.
     */
    public void setSearchQuery(@Nullable String searchQuery) {
        this.searchQuery = searchQuery;
    }

}
