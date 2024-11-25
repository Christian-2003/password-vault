package de.passwordvault.view.settings.activity_url_import;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;

import de.passwordvault.model.analysis.QualityGate;
import de.passwordvault.model.analysis.QualityGateManager;

public class UrlImportViewModel extends ViewModel {

    @Nullable
    private Uri uri;

    @Nullable
    private String description;

    @Nullable
    private String regex;

    @Nullable
    private String author;


    public UrlImportViewModel() {
        uri = null;
        description = null;
        regex = null;
        author = null;
    }


    public void setUri(@NonNull Uri uri) {
        this.uri = uri;
        description = uri.getQueryParameter("d");
        regex = uri.getQueryParameter("regex");
        author = uri.getQueryParameter("ref");
    }


    @Nullable
    public String getUri() {
        if (uri == null) {
            return null;
        }
        return uri.toString();
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    @Nullable
    public String getRegex() {
        return regex;
    }

    @Nullable
    public String getAuthor() {
        return author;
    }


    public boolean importQualityGate() {
        if (description == null || regex == null || author == null) {
            return false;
        }
        QualityGate qualityGate = new QualityGate(regex, description, true, true, author);
        QualityGateManager.getInstance().addQualityGate(qualityGate);
        QualityGateManager.getInstance().saveAllQualityGates();
        return true;
    }

}
