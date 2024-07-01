package de.passwordvault.view.utils.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import de.passwordvault.R;


/**
 * Class models a list button view, which can be placed below any list to add a new item
 * to the list.
 *
 * @author  Christian-2003
 * @version 3.6.0
 */
public class ListButtonView extends LinearLayout {

    /**
     * Attribute stores the text for the button defined through the XML attributes.
     */
    private String attrText;

    /**
     * Attribute stores the text view displaying the button text.
     */
    private TextView textView;


    /**
     * Constructor instantiates a new list button view.
     *
     * @param context   Context for the view.
     */
    public ListButtonView(Context context) {
        super(context);
    }

    /**
     * Constructor instantiates a new list button view.
     *
     * @param context   Context for the view.
     * @param attrs     Attributes for the view.
     */
    public ListButtonView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    /**
     * Constructor instantiates a new list button view.
     *
     * @param context       Context for the view.
     * @param attrs         Attributes for the view.
     * @param defStyleAttr  Style for the view.
     */
    public ListButtonView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    /**
     * Constructor instantiates a new list button view.
     *
     * @param context       Context for the view.
     * @param attrs         Attributes for the view.
     * @param defStyleAttr  Style for the view.
     * @param defStyleRes   Theme for the view.
     */
    public ListButtonView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }


    /**
     * Method changes the text of the list button to the passed resource string.
     *
     * @param id    ID of the string-resource to which to set the button text.
     */
    public void setText(@StringRes int id) {
        setText(getContext().getResources().getString(id));
    }

    /**
     * Method changes the text of the list button to the passed argument.
     *
     * @param text  Text for the list button.
     */
    public void setText(String text) {
        textView.setText(text);
    }


    /**
     * Method is called when the view finished inflating and configures all it's components.
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        textView = findViewById(R.id.text_view_button);
        if (attrText != null) {
            setText(attrText);
        }
    }


    /**
     * Method inflates the view.
     */
    private void init(AttributeSet attrs) {
        try (TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ListButtonView)) {
            attrText = a.getString(R.styleable.ListButtonView_text);
            a.recycle();
        }
        catch (Exception e) {
            //Ignore...
        }
        inflate(getContext(), R.layout.view_list_button, this);
    }

}
