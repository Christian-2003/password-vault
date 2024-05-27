package de.passwordvault.model.analysis;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.ArrayList;
import de.passwordvault.App;
import de.passwordvault.R;
import de.passwordvault.model.Observable;
import de.passwordvault.model.Observer;
import de.passwordvault.model.storage.app.StorageException;
import de.passwordvault.model.storage.csv.CsvBuilder;
import de.passwordvault.model.storage.csv.CsvParser;


/**
 * Class implements the quality gate manager which manages all available quality gates for passwords.
 * The class is implemented using singleton-pattern. The singleton-instance can be retrieved
 * through {@link #getInstance()}. When first accessing the singleton-instance, quality gates are
 * automatically loaded from shared preferences.
 *
 * @author  Christian-2003
 * @version 3.5.4
 */
public class QualityGateManager implements Observable<ArrayList<QualityGate>> {

    /**
     * Field stores the singleton-instance of the quality gate manager.
     */
    private static QualityGateManager singleton;


    /**
     * Attribute stores all registered observers.
     */
    private final ArrayList<Observer<ArrayList<QualityGate>>> observers;

    /**
     * Attribute stores the default quality gates that are being managed by this instance.
     */
    private final ArrayList<QualityGate> qualityGates;

    /**
     * Attribute caches the number of active quality gates. This is done to prevent iterating through
     * all quality gates whenever the number of active quality gates is accessed. Set this to
     * {@code -1} if this number needs to be recalculated.
     */
    private int cachedNumberOfQualityGates;


    /**
     * Constructor instantiates a new QualityGateManager-instance.
     */
    private QualityGateManager() {
        observers = new ArrayList<>();
        qualityGates = new ArrayList<>();
        loadDefaultQualityGates();
        loadCustomQualityGates();
        cachedNumberOfQualityGates = -1;
    }


    /**
     * Static method returns the {@link #singleton}-instance of the quality gate manager.
     *
     * @return  Singleton-instance of the quality gate manager.
     */
    public static QualityGateManager getInstance() {
        if (singleton == null) {
            singleton = new QualityGateManager();
        }
        return singleton;
    }


    /**
     * Method adds the passed quality gates to the managed quality gates.
     *
     * @param qualityGate           Quality gate to be added.
     * @throws NullPointerException The passed quality gate is {@code null}.
     */
    public void addQualityGate(QualityGate qualityGate) throws NullPointerException {
        if (qualityGate == null) {
            throw new NullPointerException();
        }
        qualityGates.add(qualityGate);
        cachedNumberOfQualityGates = -1;
        notifyObservers();
    }


    /**
     * Method returns the quality gate at the specified index.
     *
     * @param index                         Index of the quality to be returned.
     * @return                              Quality gate at the specified index.
     * @throws IndexOutOfBoundsException    The passed index is out of bounds.
     */
    public QualityGate getQualityGate(int index) throws IndexOutOfBoundsException {
        return qualityGates.get(index);
    }


    /**
     * Method changes the quality gate at the specified index to the passed quality gate. If the
     * quality gate at the specified index is not editable, nothing happens.
     *
     * @param qualityGate                   New quality gate.
     * @param index                         Index of the quality gate to be changed.
     * @throws NullPointerException         The passed quality gate is {@code null}.
     * @throws IndexOutOfBoundsException    The passed index is out of bounds.
     */
    public void setQualityGate(QualityGate qualityGate, int index) throws NullPointerException, IndexOutOfBoundsException {
        if (qualityGate == null) {
            throw new NullPointerException();
        }
        if (isQualityGateEditable(index)) {
            qualityGates.set(index, qualityGate);
            cachedNumberOfQualityGates = -1;
            notifyObservers();
        }
    }


    /**
     * Method removes the quality gate at the specified index from the managed quality gates. If
     * the quality gate at the specified index is not editable, nothing happens.
     *
     * @param index                         Index whose quality gate shall be removed.
     * @throws IndexOutOfBoundsException    The passed index is out of bounds.
     */
    public void removeQualityGate(int index) throws IndexOutOfBoundsException {
        if (isQualityGateEditable(index)) {
            qualityGates.remove(index);
            cachedNumberOfQualityGates = -1;
            notifyObservers();
        }
    }


    /**
     * Method removes all quality gates.
     */
    public void clearQualityGates() {
        qualityGates.clear();
    }


    /**
     * Method replaces all existing quality gates with the passed list of new quality gates.
     *
     * @param qualityGates          List of new quality gates to add.
     * @param loadDefault           Whether to additionally load the default quality gates.
     * @throws NullPointerException The passed list of quality gates is {@code null}.
     */
    public void replaceQualityGates(ArrayList<QualityGate> qualityGates, boolean loadDefault) throws NullPointerException {
        if (qualityGates == null) {
            throw new NullPointerException();
        }
        clearQualityGates();
        if (loadDefault) {
            loadDefaultQualityGates();
        }
        this.qualityGates.addAll(qualityGates);
    }


    /**
     * Method calculates the number of quality gates that are applied to passwords.
     *
     * @return  Number of quality gates that are applied to passwords.
     */
    public int numberOfQualityGates() {
        if (cachedNumberOfQualityGates == -1) {
            //Cached number of quality gates needs to be recalculated:
            cachedNumberOfQualityGates = 0;
            for (QualityGate gate : qualityGates) {
                if (gate.isEnabled()) {
                    cachedNumberOfQualityGates++;
                }
            }
        }
        return cachedNumberOfQualityGates;
    }

    /**
     * Method calculates the number of quality gates that the passed string passes. Calling this
     * method automatically recalculates the cached number of active quality gates.
     *
     * @param s String to be tested.
     * @return  The number of passed quality gates.
     */
    public int calculatePassedQualityGates(String s) {
        int i = 0;
        cachedNumberOfQualityGates = 0;
        for (QualityGate gate : qualityGates) {
            if (gate.isEnabled()) {
                if (gate.matches(s)) {
                    i++;
                }
                cachedNumberOfQualityGates++;
            }
        }
        return i;
    }


    /**
     * Method returns the data which is being observed. This method must always return the newest
     * data from the implemented instance.
     *
     * @return  Newest data which is being observed.
     */
    public ArrayList<QualityGate> getData() {
        return qualityGates;
    }


    /**
     * Method registers the passed {@link Observer}. Whenever the relevant data of the implementing
     * class is changed, all Observers which were previously registered through this method are
     * informed about the changed data.
     *
     * @param o                     Observer to be registered.
     * @throws NullPointerException The passed Observer is {@code null}.
     */
    @Override
    public void addObserver(Observer<ArrayList<QualityGate>> o) throws NullPointerException {
        if (o == null) {
            throw new NullPointerException("Null is invalid Observer");
        }
        observers.add(o);
    }


    /**
     * Method notifies all registered {@link Observer}s about a change of the observed data through
     * their {@link Observer#update(Observable)}-method.
     */
    @Override
    public void notifyObservers() {
        for (Observer<ArrayList<QualityGate>> o : observers) {
            o.update(this);
        }
    }


    /**
     * Method removes the specified {@link Observer} from the implementing class. When the observed
     * data is changed in the future, the removed Observer will not be informed.
     *
     * @param o                     Observer to be removed from the implementing class' observers.
     * @return                      Whether the specified Observer was successfully removed.
     * @throws NullPointerException The passed Observer is {@code null}.
     */
    @Override
    public boolean removeObserver(Observer<ArrayList<QualityGate>> o) throws NullPointerException {
        if (o == null) {
            throw new NullPointerException("Null is invalid Observer");
        }
        return observers.remove(o);
    }


    /**
     * Method loads all custom quality gates from shared preferences.
     */
    private void loadCustomQualityGates() {
        SharedPreferences preferences = App.getContext().getSharedPreferences(App.getContext().getString(R.string.preferences_file), Context.MODE_PRIVATE);
        if (!preferences.contains(App.getContext().getString(R.string.preferences_quality_gates_custom))) {
            return;
        }

        String qualityGatesCsv = preferences.getString(App.getContext().getString(R.string.preferences_quality_gates_custom), "");
        if (qualityGatesCsv != null && !qualityGatesCsv.isEmpty()) {
            String[] qualityGatesCsvRows = qualityGatesCsv.split("\n");
            for (String csvRow : qualityGatesCsvRows) {
                if (csvRow.isEmpty()) {
                    //Ignore last row (which is always empty)
                    continue;
                }
                QualityGate qualityGate = new QualityGate();
                try {
                    qualityGate.fromStorable(csvRow);
                }
                catch (StorageException e) {
                    continue;
                }
                qualityGates.add(qualityGate);
            }
        }
    }


    /**
     * Method saves all quality gates to the shared preferences.
     */
    public void saveAllQualityGates() {
        SharedPreferences.Editor editor = App.getContext().getSharedPreferences(App.getContext().getString(R.string.preferences_file), Context.MODE_PRIVATE).edit();

        CsvBuilder defaultQualityGates = new CsvBuilder();
        StringBuilder customQualityGates = new StringBuilder();

        for (QualityGate qualityGate : qualityGates) {
            if (!qualityGate.isEditable()) {
                //Default quality gate:
                defaultQualityGates.append(qualityGate.isEnabled());
            }
            else {
                //Custom quality gate:
                customQualityGates.append(qualityGate.toStorable());
                customQualityGates.append("\n");
            }
        }
        if (customQualityGates.length() > 1) {
            customQualityGates.deleteCharAt(customQualityGates.length() - 1);
        }

        editor.putString(App.getContext().getString(R.string.preferences_quality_gates_default), defaultQualityGates.toString());
        editor.putString(App.getContext().getString(R.string.preferences_quality_gates_custom), customQualityGates.toString());

        editor.apply();
    }


    /**
     * Method generates the default quality gates based on what was stored within the shared preferences.
     */
    private void loadDefaultQualityGates() {
        SharedPreferences preferences = App.getContext().getSharedPreferences(App.getContext().getString(R.string.preferences_file), Context.MODE_PRIVATE);

        if (!preferences.contains(App.getContext().getString(R.string.preferences_quality_gates_default))) {
            //Create new quality gates:
            generateNewDefaultQualityGates();
            return;
        }

        //Load data from shared preferences:
        String qualityGates = preferences.getString(App.getContext().getString(R.string.preferences_quality_gates_default), "");
        CsvParser parser = new CsvParser(qualityGates);
        ArrayList<String> cells = parser.parseCsv();
        if (cells.size() != 5) {
            generateNewDefaultQualityGates();
            return;
        }

        for (int i = 0; i < cells.size(); i++) {
            boolean enabled = false;
            try {
                enabled = Boolean.parseBoolean(cells.get(i));
            }
            catch (Exception e) {
                //Do nothing...
            }
            switch (i) {
                case 0:
                    this.qualityGates.add(new QualityGate(App.getContext().getString(R.string.quality_gate_regex_0), App.getContext().getString(R.string.quality_gate_0), enabled, false));
                    break;
                case 1:
                    this.qualityGates.add(new QualityGate(App.getContext().getString(R.string.quality_gate_regex_1), App.getContext().getString(R.string.quality_gate_1), enabled, false));
                    break;
                case 2:
                    this.qualityGates.add(new QualityGate(App.getContext().getString(R.string.quality_gate_regex_2), App.getContext().getString(R.string.quality_gate_2), enabled, false));
                    break;
                case 3:
                    this.qualityGates.add(new QualityGate(App.getContext().getString(R.string.quality_gate_regex_3), App.getContext().getString(R.string.quality_gate_3), enabled, false));
                    break;
                case 4:
                    this.qualityGates.add(new QualityGate(App.getContext().getString(R.string.quality_gate_regex_4), App.getContext().getString(R.string.quality_gate_4), enabled, false));
                    break;
            }
        }
    }


    /**
     * Method generates new default quality gates. The default quality gates are enabled by default.
     */
    private void generateNewDefaultQualityGates() {
        qualityGates.add(new QualityGate(App.getContext().getString(R.string.quality_gate_regex_0), App.getContext().getString(R.string.quality_gate_0), true, false));
        qualityGates.add(new QualityGate(App.getContext().getString(R.string.quality_gate_regex_1), App.getContext().getString(R.string.quality_gate_1), true, false));
        qualityGates.add(new QualityGate(App.getContext().getString(R.string.quality_gate_regex_2), App.getContext().getString(R.string.quality_gate_2), true, false));
        qualityGates.add(new QualityGate(App.getContext().getString(R.string.quality_gate_regex_3), App.getContext().getString(R.string.quality_gate_3), true, false));
        qualityGates.add(new QualityGate(App.getContext().getString(R.string.quality_gate_regex_4), App.getContext().getString(R.string.quality_gate_4), true, false));
    }


    /**
     * Method tests whether the quality gate at the specified index is editable.
     *
     * @param index                 Index whose quality gate shall be tested.
     * @return                      Whether the quality gate at the passed index is editable.
     * @throws NullPointerException The passed index is out of range.
     */
    private boolean isQualityGateEditable(int index) throws NullPointerException {
        if (index < 0 || index >= qualityGates.size()) {
            throw new IndexOutOfBoundsException();
        }
        return qualityGates.get(index).isEditable();
    }

}
