package de.passwordvault.view.utils.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import de.passwordvault.R;


/**
 * Class implements a CtContainer, which is a subclass of {@linkplain LinearLayout}. This ViewGroup
 * must be the top-level layout for each {@link CtActivity}.
 *
 * @author  Christian-2003
 * @version 3.5.1
 */
public class CtContainer extends LinearLayout {

    /**
     * Attribute stores the title that was specified through an XML attribute.
     */
    private String title;

    /**
     * Attribute stores whether the activity shall have a back button. This is specified through an
     * XML attribute.
     */
    private boolean backButton;


    /**
     * Constructor instantiates a new CtContainer-instance.
     *
     * @param context   Context for the container.
     */
    public CtContainer(Context context) {
        super(context);
    }

    /**
     * Constructor instantiates a new CtContainer-instance.
     *
     * @param context   Context for the container.
     * @param attrs     Attribute set for the container.
     */
    public CtContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    /**
     * Constructor instantiates a new CtContainer-instance.
     *
     * @param context       Context for the container.
     * @param attrs         Attribute set for the container.
     * @param defStyleAttr  DefStyleAttr for the container.
     */
    public CtContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }


    /**
     * Method returns the title that was specified by the user. This can be {@code null} if no
     * title is specified.
     *
     * @return  User-specified title.
     */
    protected String getTitle() {
        return title;
    }

    /**
     * Method returns whether the toolbar shall have a back button. This is {@code false} by default
     * if nothing is specified by the user.
     *
     * @return  Whether the toolbar shall have a back button.
     */
    protected boolean hasBackButton() {
        return backButton;
    }


    /**
     * Method instantiates the container based on the passed AttributeSet which contains all XML
     * attributes.
     *
     * @param attrs Attributes from which to create the container.
     */
    private void init(AttributeSet attrs) {
        try (TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CtContainer)) {
            title = a.getString(R.styleable.CtContainer_android_title);
            backButton = a.getBoolean(R.styleable.CtContainer_backButton, false);
            a.recycle();
        }
        catch (Exception e) {
            //Ignore...
        }
    }

}
