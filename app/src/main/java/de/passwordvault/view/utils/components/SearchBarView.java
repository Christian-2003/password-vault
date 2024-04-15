package de.passwordvault.view.utils.components;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import java.util.Objects;
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
     * Class implements a text watcher that handles all actions that need to be done when the text
     * within the search bar view is edited.
     */
    private class SearchBarViewTextWatcher implements TextWatcher {

        /**
         * Method is called before the watched text is being changed.
         *
         * @param s     Text that is about to change.
         * @param start Index of the first character to be changed.
         * @param count Number of characters to be changed.
         * @param after New length for the text.
         */
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        /**
         * Method is called after the watched text changed.
         *
         * @param s         Changed text.
         * @param start     Index if the first character being replaced.
         * @param before    Previous length of the watched text.
         * @param count     Number of characters being changed.
         */
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        /**
         * Method is called after the watched text changed.
         *
         * @param s Changed text.
         */
        @Override
        public void afterTextChanged(Editable s) {
            int currentVisibility = SearchBarView.this.clearButton.getVisibility();
            int newVisibility = s.toString().isEmpty() ? INVISIBLE : VISIBLE;
            if (currentVisibility != newVisibility) {
                if (newVisibility == INVISIBLE) {
                    SearchBarView.this.clearButton.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_right));
                    SearchBarView.this.clearButton.postDelayed(() -> SearchBarView.this.clearButton.setVisibility(INVISIBLE), getResources().getInteger(R.integer.default_anim_duration));
                }
                else  {
                    SearchBarView.this.clearButton.setVisibility(VISIBLE);
                    SearchBarView.this.clearButton.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_right));
                }
            }
        }

    }


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
     * Method is called when the view finished inflating and configures all it's components.
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        queryInput = findViewById(R.id.input_query);
        int primaryColor = getContext().getColor(R.color.pv_primary);
        Objects.requireNonNull(queryInput.getTextCursorDrawable()).setTint(primaryColor);
        Objects.requireNonNull(queryInput.getTextSelectHandle()).setTint(primaryColor);
        queryInput.addTextChangedListener(new SearchBarViewTextWatcher());
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
