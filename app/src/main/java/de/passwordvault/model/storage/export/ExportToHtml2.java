package de.passwordvault.model.storage.export;

import android.net.Uri;
import android.os.ParcelFileDescriptor;
import java.io.BufferedWriter;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.Collection;
import de.passwordvault.App;
import de.passwordvault.R;
import de.passwordvault.model.detail.Detail;
import de.passwordvault.model.entry.EntryAbbreviated;
import de.passwordvault.model.entry.EntryExtended;
import de.passwordvault.model.storage.app.StorageManager;
import de.passwordvault.view.utils.Utils;


/**
 * Class can export all entries into readable HTML format.
 *
 * @author  Christian-2003
 * @version 3.5.2
 */
public class ExportToHtml2 {

    /**
     * Attribute stores the HTML container for an entry within the content-list.
     */
    private final String htmlContentListContainer;

    /**
     * Attribute stores the HTML container for a detail.
     */
    private final String htmlDetailContainer;

    /**
     * Attribute stores the HTML container for an obfuscated detail.
     */
    private final String htmlDetailObfuscatedContainer;

    /**
     * Attribute stores the HTML container for an entry within the entries-list.
     */
    private final String htmlEntryListContainer;

    /**
     * Attribute stores the HTML for the HTML DOM.
     */
    private final String dom;

    /**
     * Attribute stores the CSS stylesheet for the document.
     */
    private final String styles;

    /**
     * Attribute stores the JS scripts for the document.
     */
    private final String scripts;

    /**
     * Attribute stores the URI to which the HTML shall be exported.
     */
    private final Uri uri;


    /**
     * Constructor instantiates a new ExportToHtml2-instance through which to export all entries
     * into an HTML file with the specified URI.
     *
     * @param uri                   File to which the data shall be exported.
     * @throws NullPointerException The passed URI is {@code null}.
     */
    public ExportToHtml2(Uri uri) throws NullPointerException {
        if (uri == null) {
            throw new NullPointerException();
        }
        this.uri = uri;
        htmlContentListContainer = Utils.readRawResource(R.raw.html2_export_content_list_container);
        htmlDetailContainer = Utils.readRawResource(R.raw.html2_export_detail_container);
        htmlDetailObfuscatedContainer = Utils.readRawResource(R.raw.html2_export_detail_obfuscated_container);
        htmlEntryListContainer = Utils.readRawResource(R.raw.html2_export_entry_list_container);
        dom = Utils.readRawResource(R.raw.html2_export_file);
        styles = Utils.readRawResource(R.raw.html2_export_styles);
        scripts = Utils.readRawResource(R.raw.html2_export_scripts);
    }


    /**
     * Method exports all entries into the specified file.
     *
     * @throws ExportException  The export could not be created.
     */
    public void export() throws ExportException {
        ParcelFileDescriptor xml;
        try {
            xml = App.getContext().getContentResolver().openFileDescriptor(uri, "w");
        }
        catch (FileNotFoundException e) {
            throw new ExportException(e.getMessage());
        }
        if (xml == null) {
            throw new ExportException("Null is invalid parcel file descriptor");
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
        catch (Exception e) {
            throw new ExportException(e.getMessage());
        }
    }


    /**
     * Method generates the entire HTML document.
     *
     * @return              HTML document.
     * @throws Exception    The HTML document could not be created.
     */
    private String generateHtml() throws Exception {
        StringBuilder entryListHtml = new StringBuilder();
        StringBuilder contentListHtml = new StringBuilder();
        StorageManager manager = new StorageManager();
        Collection<EntryAbbreviated> entries = manager.loadAbbreviatedEntries().values();
        int position = 0;
        for (EntryAbbreviated abbreviated : entries) {
            EntryExtended extended = manager.loadExtendedEntry(abbreviated);
            if (extended == null) {
                continue;
            }
            entryListHtml.append(generateEntryListHtml(extended, position));
            contentListHtml.append(generateEntryHtml(extended, position));
            position++;
        }
        String html = dom.replaceAll("\\{title\\}", App.getContext().getString(R.string.html_export_document_title));
        html = html.replace("{content_list}", contentListHtml.toString());
        html = html.replace("{entry_list}", entryListHtml.toString());
        html = html.replace("{date}", Utils.formatDate(Calendar.getInstance(), App.getContext().getString(R.string.date_format)));
        html = html.replace("{js}", scripts);
        html = html.replace("{css}", styles);
        return html;
    }


    /**
     * Method generates the HTML for an entry within the entries-list at the left side of the
     * document.
     *
     * @param entry     Entry for which to create the HTML.
     * @param position  Index of the entry within the list of entries.
     * @return          HTML for the entry within the entries-list.
     */
    private String generateEntryListHtml(EntryExtended entry, int position) {
        String html = htmlEntryListContainer.replace("{id}", "entry" + position);
        html = html.replace("{name}", entry.getName());
        html = html.replace("{description}", entry.getDescription());
        return html;
    }


    /**
     * Method creates the HTML for an entire entry.
     *
     * @param entry     Entry for which to create the HTML.
     * @param position  Index of the entry within the list of entries.
     * @return          HTML for the entry.
     */
    private String generateEntryHtml(EntryExtended entry, int position) {
        String html = htmlContentListContainer.replace("{id}", "entry" + position);
        StringBuilder details = new StringBuilder();
        for (Detail detail : entry.getDetails()) {
            details.append(generateDetailHtml(detail));
        }
        html = html.replace("{content}", details.toString());
        return html;
    }


    /**
     * Method creates the HTML for a single detail.
     *
     * @param detail    Detail for which to create the HTML.
     * @return          HTML for the passed detail.
     */
    private String generateDetailHtml(Detail detail) {
        String html;
        if (detail.isObfuscated()) {
            html = htmlDetailObfuscatedContainer.replace("{obfuscated}", Utils.obfuscate(detail.getContent()));
        }
        else {
            html = htmlDetailContainer;
        }
        html = html.replace("{name}", detail.getName());
        html = html.replace("{content}", detail.getContent());
        return html;
    }

}
