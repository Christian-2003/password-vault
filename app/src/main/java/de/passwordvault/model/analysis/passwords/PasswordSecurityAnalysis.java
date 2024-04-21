package de.passwordvault.model.analysis.passwords;

import android.util.Log;
import java.util.ArrayList;
import de.passwordvault.model.Observable;
import de.passwordvault.model.Observer;
import de.passwordvault.model.detail.Detail;
import de.passwordvault.model.detail.DetailType;
import de.passwordvault.model.entry.EntryAbbreviated;
import de.passwordvault.model.entry.EntryExtended;
import de.passwordvault.model.entry.EntryManager;


/**
 * Class models the password security analysis. An instance of the class can analyze all passwords
 * that are available to the app. An average security score is calculated. Furthermore, the analysis
 * finds duplicate passwords.
 *
 * @author  Christian-2003
 * @version 3.4.0
 */
public class PasswordSecurityAnalysis implements Observable<ArrayList<Password>>, Runnable {

    /**
     * Field stores the singleton-instance of the class.
     */
    private static PasswordSecurityAnalysis singleton;


    /**
     * Attribute stores all observers for this instance.
     */
    private final ArrayList<Observer<ArrayList<Password>>> observers;

    /**
     * Attribute stores all passwords to be analyzed.
     */
    private final ArrayList<Password> passwords;

    /**
     * Attribute stores a list of which each item is a list of identical passwords.
     */
    private final ArrayList<ArrayList<Password>> identicalPasswords;

    /**
     * Attribute stores the average security score.
     */
    private double averageSecurityScore;

    /**
     * Attribute stores the thread that performs the analysis of the passwords.
     */
    private Thread analysisThread;

    /**
     * Attribute indicates whether the analysis has been completed.
     */
    private boolean analysisCompleted;

    /**
     * Attribute indicates whether an analysis is currently running.
     */
    private boolean analysisRunning;


    /**
     * Constructor instantiates a new instance to analyze the password security.
     */
    private PasswordSecurityAnalysis() {
        observers = new ArrayList<>();
        passwords = new ArrayList<>();
        identicalPasswords = new ArrayList<>();
        averageSecurityScore = 0.0;
        analysisThread = null;
        analysisCompleted = false;
        analysisRunning = false;
    }


    /**
     * Method returns the singleton-instance of the class.
     *
     * @return  Singleton-instance of the class.
     */
    public static PasswordSecurityAnalysis getInstance() {
        if (singleton == null) {
            singleton = new PasswordSecurityAnalysis();
        }
        return singleton;
    }


    /**
     * Method returns the average security score.
     *
     * @return  Average security score.
     */
    public double getAverageSecurityScore() {
        return averageSecurityScore;
    }

    /**
     * Method returns a list. Each list contains a list of passwords that are identical.
     *
     * @return  Identical passwords.
     */
    public ArrayList<ArrayList<Password>> getIdenticalPasswords() {
        return identicalPasswords;
    }

    /**
     * Method returns whether an analysis has completed and results are available. If this is {@code false},
     * accessing the results should be avoided.
     *
     * @return  Whether an analysis has completed.
     */
    public boolean isAnalysisCompleted() {
        return analysisCompleted;
    }

    /**
     * Method returns whether an analysis is currently running. If this is {@code true}, avoid
     * accessing the analysis results as they may be incomplete.
     *
     * @return  Whether the analysis is currently running.
     */
    public boolean isAnalysisRunning() {
        return analysisRunning;
    }


    /**
     * Method performs the password security analysis for all passwords of the application. Since
     * this analysis is very resource-intensive and takes some time, the analysis is performed from
     * a separate thread.
     */
    public void analyze() {
        analyze(false);
    }


    /**
     * Method performs the password security analysis for all passwords of the application. Since
     * this analysis is very resource-intensive and takes some time, the analysis is performed from
     * a separate thread.
     *
     * @param force Set this to {@code true} to cancel any running analysis and restart.
     */
    public void analyze(boolean force) {
        if (analysisThread == null || force) {
            cancel();
            analysisThread = new Thread(this);
            analysisThread.start();
        }
    }


    /**
     * Method cancels the password analysis.
     */
    public void cancel() {
        try {
            if (analysisThread != null && analysisThread.isAlive()) {
                analysisThread.interrupt();
            }
        }
        catch (SecurityException e) {
            Log.d("PasswordAnalysis", "Cannot interrupt password analysis thread: " + e.getMessage());
        }
        passwords.clear();
        identicalPasswords.clear();
        analysisThread = null;
        analysisRunning = false;
        analysisCompleted = false;
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
    public void addObserver(Observer<ArrayList<Password>> o) throws NullPointerException {
        if (o == null) {
            throw new NullPointerException();
        }
        observers.add(o);
    }


    /**
     * Method returns the data which is being observed. This method must always return the newest
     * data from the implemented instance.
     *
     * @return  Newest data which is being observed.
     */
    @Override
    public ArrayList<Password> getData() {
        return passwords;
    }

    /**
     * Method notifies all registered {@link Observer}s about a change of the observed data through
     * their {@link Observer#update(Observable)}-method.
     */
    @Override
    public void notifyObservers() {
        for (Observer<ArrayList<Password>> o : observers) {
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
    public boolean removeObserver(Observer<ArrayList<Password>> o) throws NullPointerException {
        if (o == null) {
            throw new NullPointerException();
        }
        return observers.remove(o);
    }


    /**
     * Method is called to analyze the passwords on a separate thread. This is done to not block the
     * UI thread since the analysis takes a considerable amount of time.
     * Please NEVER call this method. Always perform the analysis by calling {@link #analyze()}!
     */
    @Override
    public void run() {
        passwords.clear();
        identicalPasswords.clear();
        averageSecurityScore = 0.0;
        analysisCompleted = false;
        analysisRunning = true;

        //Retrieve all passwords:
        for (EntryAbbreviated abbreviated : EntryManager.getInstance().getData()) {
            EntryExtended extended = EntryManager.getInstance().get(abbreviated.getUuid(), false);
            if (extended == null) {
                continue;
            }
            for (Detail detail : extended.getDetails()) {
                if (detail.getType() == DetailType.PASSWORD) {
                    passwords.add(new Password(detail.getContent(), extended.getUuid(), extended.getName()));
                }
            }
        }

        //Security analysis:
        for (int i = 0; i < passwords.size(); i++) {
            Password password = passwords.get(i);
            averageSecurityScore += password.getSecurityScore();
            for (int j = i + 1; j < passwords.size(); j++) {
                Password comparedPassword = passwords.get(j);
                if (password.getCleartextPassword().equals(comparedPassword.getCleartextPassword())) {
                    //Identical password found:
                    boolean passwordFound = false;
                    for (ArrayList<Password> identicalPasswordGroup : identicalPasswords) {
                        if (identicalPasswordGroup.get(0).getCleartextPassword().equals(password.getCleartextPassword())) {
                            if (!identicalPasswordGroup.contains(comparedPassword)) {
                                identicalPasswordGroup.add(comparedPassword);
                            }
                            passwordFound = true;
                            break;
                        }
                    }
                    if (!passwordFound) {
                        ArrayList<Password> identicalPasswordGroup = new ArrayList<>();
                        identicalPasswordGroup.add(password);
                        identicalPasswordGroup.add(comparedPassword);
                        identicalPasswords.add(identicalPasswordGroup);
                    }
                }
            }
        }
        averageSecurityScore = averageSecurityScore / passwords.size();
        analysisCompleted = true;
        analysisRunning = false;
        notifyObservers();
    }

}
