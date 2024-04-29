package de.passwordvault.model.storage;

import android.content.res.AssetManager;
import android.util.Log;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;
import de.passwordvault.App;


/**
 * Class models a locale asset manager which can access asset files based on a locale. Files within
 * the 'assets' directory must be stored as of the following structure:
 * <code><br/>
 * assets<br/>
 * |--- folder1<br/>
 * |&nbsp;&nbsp;&nbsp;&nbsp;|--- locale1<br/>
 * |&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;|--- file1<br/>
 * |&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;|--- file2<br/>
 * |&nbsp;&nbsp;&nbsp;&nbsp;|--- locale2<br/>
 * |&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|--- file1<br/>
 * |&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|--- file2<br/>
 * |--- folder2<br/>
 * ...<br/>
 * </code>
 *
 * @author  Christian-2003
 * @version 3.5.3
 */
public class LocalizedAssetManager {

    /**
     * Field stores the tag used for logging.
     */
    private static final String TAG = "LAM";

    /**
     * Field stores the structure used within the assets folder.
     */
    private static final String FILE_STRUCTURE = "{folder}/{locale}/{filename}";

    /**
     * Field stores the URI used to access an asset file.
     */
    private static final String ASSET_FILE_URI = "file:///android_asset/{path}";

    /**
     * Field stores the default locale for assets.
     */
    private static final String DEFAULT_LOCALE = "en";


    /**
     * Attribute stores the asset manager used to access the application's assets.
     */
    private final AssetManager assets;

    /**
     * Attribute stores the folder for the assets of the asset manager.
     */
    private final String folder;

    /**
     * Attribute stores the locale for the assets within the designated folder.
     */
    private final String locale;


    /**
     * Constructor instantiates a new localized asset manager for the passed locale and folder.
     * @param folder                    Folder within the assets from which to load the assets.
     * @param locale                    Locale for which to load the assets.
     * @throws NullPointerException     The passed folder or locale is {@code null}.
     * @throws IllegalArgumentException The passed folder does not exist within the assets.
     */
    public LocalizedAssetManager(String folder, Locale locale) throws NullPointerException, IllegalArgumentException {
        if (folder == null || locale == null) {
            throw new NullPointerException();
        }
        this.folder = folder;
        assets = App.getContext().getAssets();
        if (!containsFolder()) {
            throw new IllegalArgumentException("Designated folder '" + folder + "' does not exists within assets.");
        }
        if (containsLocale(locale.getLanguage())) {
            this.locale = locale.getLanguage();
        }
        else {
            this.locale = DEFAULT_LOCALE;
        }
    }


    /**
     * Method generates the URI to access the specified asset file. If the asset file does not exist
     * for the current locale, the default locale (en) us used instead.
     *
     * @param filename                  Name of the file to open (e.g. "test.html").
     * @return                          URI for the specified file (e.g. "file:///android_asset/help/de/test.html").
     * @throws NullPointerException     The passed filename is {@code null}.
     * @throws FileNotFoundException    The passed file does not exist for any locale.
     */
    public String getFileUri(String filename) throws NullPointerException, FileNotFoundException {
        if (filename == null) {
            throw new NullPointerException();
        }
        String usedLocale = locale;
        if (!containsFile(filename, usedLocale)) {
            boolean fileNotFound = true;
            if (!usedLocale.equals(DEFAULT_LOCALE)) {
                //Try to load file with default locale:
                usedLocale = DEFAULT_LOCALE;
                fileNotFound = !containsFile(filename, usedLocale);
            }
            if (fileNotFound) {
                Log.e(TAG, "File '" + filename + "' does not exist within the folder '" + folder + "' with locale + '" + usedLocale + "'.");
                throw new FileNotFoundException("File '" + filename + "' does not exist within the folder '" + folder + "' with locale + '" + usedLocale + "'.");
            }
        }

        String filepath = generateFilePath(filename, usedLocale);
        String uri = ASSET_FILE_URI.replace("{path}", filepath);
        return uri;
    }


    /**
     * Method generates the path of the file within the assets.
     *
     * @param filename  Name of the file (e.g. "help.html").
     * @param locale    Locale for which to generate the file.
     * @return          Path to the passed file within the assets.
     */
    private String generateFilePath(String filename, String locale) {
        String generated = FILE_STRUCTURE.replace("{folder}", folder);
        generated = generated.replace("{locale}", locale);
        generated = generated.replace("{filename}", filename);
        return generated;
    }


    /**
     * Method tests whether the passed filename exists as asset within the constraints of the current
     * folder and locale.
     *
     * @param filename  Filename to be tested.
     * @param locale    Locale to use for testing.
     * @return          Whether the specified file exists within the current folder and locale.
     */
    private boolean containsFile(String filename, String locale) {
        try {
            String[] files = assets.list(folder + "/" + locale);
            if (files == null) {
                return false;
            }
            for (String s : files) {
                if (s.equals(filename)) {
                    return true;
                }
            }
            return false;
        }
        catch (IOException e) {
            return false;
        }
    }


    /**
     * Method tests whether the {@link #folder} exists within the root directory of the assets.
     *
     * @return  Whether the folder exists.
     */
    private boolean containsFolder() {
        try {
            String[] folder = assets.list("");
            if (folder == null) {
                return false;
            }
            for (String s : folder) {
                if (s.equals(this.folder)) {
                    return true;
                }
            }
            return false;
        }
        catch (IOException e) {
            return false;
        }
    }


    /**
     * Method tests whether the {@link #locale} exists within the {@link #folder} of the assets.
     *
     * @return  Whether the locale exists within the folder.
     */
    private boolean containsLocale(String locale) {
        try {
            String[] locales = assets.list(folder);
            if (locales == null) {
                return false;
            }
            for (String s : locales) {
                if (s.equals(locale)) {
                    return true;
                }
            }
            return false;
        }
        catch (IOException e) {
            return false;
        }
    }

}
