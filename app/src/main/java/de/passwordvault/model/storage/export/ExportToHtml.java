package de.passwordvault.model.storage.export;

import android.net.Uri;
import android.os.ParcelFileDescriptor;
import java.io.BufferedWriter;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import de.passwordvault.App;
import de.passwordvault.R;
import de.passwordvault.model.detail.Detail;
import de.passwordvault.model.entry.Entry;
import de.passwordvault.model.entry.EntryHandle;


/**
 * Class implements a functionality which allows the User to export the application's data to a
 * readable HTML format.
 *
 * @author  Christian-2003
 * @version 3.1.0
 */
public class ExportToHtml {

    /**
     * Attribute stores the HTML for the detail container.
     */
    private final String detailContainer;

    /**
     * Attribute stores the HTML for the entry container.
     */
    private final String entryContainer;

    /**
     * Attribute stores the HTML for the HTML DOM.
     */
    private final String dom;

    /**
     * Attribute stores the URI to which the HTML shall be exported.
     */
    private final Uri uri;


    /**
     * Constructor instantiates a new {@link ExportToHtml}-instance which exports the app's data
     * to the file of the specified URI.
     *
     * @param uri                   File to which the data shall be exported.
     * @throws NullPointerException The passed URI is {@code null}.
     */
    public ExportToHtml(Uri uri) throws NullPointerException {
        if (uri == null) {
            throw new NullPointerException("Null is invalid URI");
        }
        this.uri = uri;
        detailContainer = App.getContext().getString(R.string.html_export_detail_container);
        entryContainer = App.getContext().getString(R.string.html_export_entry_container);
        dom = App.getContext().getString(R.string.html_export_dom);
    }


    /**
     * Method exports the application's data to external storage in HTML-format.
     *
     * @throws ExportException  The data could not be exported.
     */
    public void export() throws ExportException {
        ParcelFileDescriptor xml;
        try {
            xml = App.getContext().getContentResolver().openFileDescriptor(uri, "w");
        }
        catch (FileNotFoundException e) {
            throw new ExportException(e.getMessage());
        }
        FileDescriptor fs = xml.getFileDescriptor();
        if (fs == null) {
            throw new ExportException("Null is invalid file descriptor");
        }
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fs)));
            writer.write(generateHtml());
            writer.close();
            xml.close();
        }
        catch (IOException e) {
            throw new ExportException(e.getMessage());
        }
    }


    /**
     * Method generates the HTML for all data.
     *
     * @return  Generated HTML.
     */
    private String generateHtml() {
        StringBuilder entryHtml = new StringBuilder();
        for (Entry entry : EntryHandle.getInstance().getEntries()) {
            entryHtml.append(generateEntryHtml(entry));
        }
        String html = dom.replace("{document_title}", App.getContext().getString(R.string.html_export_document_title));
        return html.replace("{entries}", entryHtml.toString());
    }


    /**
     * Method generates the HTML for an entry.
     *
     * @param entry Entry for which the HTML shall be generated.
     * @return      Generated HTML.
     */
    private String generateEntryHtml(Entry entry) {
        StringBuilder detailHtml = new StringBuilder();
        for (Detail detail : entry.getDetails()) {
            String html = detailContainer.replace("{name}", detail.getName());
            detailHtml.append(html.replace("{content}", detail.getContent()));
        }
        String entryHtml = entryContainer.replace("{name}", entry.getName());
        entryHtml = entryHtml.replace("{description}", entry.getDescription());
        return entryHtml.replace("{details}", detailHtml.toString());
    }

}
