package de.passwordvault.viewmodel.activities;

import androidx.lifecycle.ViewModel;
import de.passwordvault.model.storage.app.ConverterException;
import de.passwordvault.model.storage.app.StorageConverter;


/**
 * Class implements a view model for the activity through which data can be converted into a new
 * format after an update.
 *
 * @author  Christian-2003
 * @version 3.3.0
 */
public class DataConversionViewModel extends ViewModel {

    /**
     * Attribute stores the converter which is used to convert data.
     */
    private final StorageConverter converter;


    /**
     * Constructor instantiates a new view model for the activity to convert data after an app update.
     */
    public DataConversionViewModel() {
        converter = new StorageConverter();
    }


    /**
     * Method converts the data into the new format.
     *
     * @throws ConverterException   Something went wrong.
     */
    public void convertData() throws ConverterException {
        converter.convert();
    }

}
