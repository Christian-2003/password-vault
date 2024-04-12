package de.passwordvault.view.utils.components;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import de.passwordvault.R;


/**
 * Class implements a custom search bar. This is required since - after more than 5 hours - I cannot
 * style the default SearchView to my liking...
 *
 * @author  Christian-2003
 * @version 3.5.1
 */
public class SearchBarView extends LinearLayout {

    /**
     * Attribute stores the edit text used to enter a search query.
     */
    private EditText queryInput;

    /**
     * Attribute stores the button to clear the search query.
     */
    private ImageButton clearButton;


    /**
     * Constructor instantiates a new search bar view.
     *
     * @param context   Context for the view.
     */
    public SearchBarView(Context context) {
        super(context);
        init();
    }

    /**
     * Constructor instantiates a new search bar view.
     *
     * @param context   Context for the view.
     * @param attrs     XML attributes for the view.
     */
    public SearchBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * Constructor instantiates a new search bar view.
     *
     * @param context       Context for the view.
     * @param attrs         XML attributes for the view.
     * @param defStyleAttr  Style for the view.
     */
    public SearchBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    /**
     * Method returns the text that is entered in the search bar.
     *
     * @return  Entered text.
     */
    public Editable getText() {
        return queryInput.getText();
    }


    /**
     * Method adds the passed text change listener to the search bar.
     *
     * @param watcher   Text watcher to add.
     */
    public void addTextChangeListener(TextWatcher watcher) {
        queryInput.addTextChangedListener(watcher);
    }

    /**
     * Method removes the passed text change listener to the search bar.
     *
     * @param watcher   Text watcher to remove.
     */
    public void removeTextChangeListener(TextWatcher watcher) {
        queryInput.removeTextChangedListener(watcher);
    }


    /**
     * Method is called when the view finished inflating and configures all it's components.
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        queryInput = findViewById(R.id.input_query);
        int primaryColor = getContext().getColor(R.color.pv_primary);
        queryInput.getTextCursorDrawable().setTint(primaryColor);
        queryInput.getTextSelectHandle().setTint(primaryColor);
        clearButton = findViewById(R.id.button_clear);
        clearButton.setOnClickListener(view -> queryInput.setText(""));
    }


    /**
     * Method initiates the search bar.
     */
    private void init() {
        inflate(getContext(), R.layout.view_searchbar, this);
    }

}
