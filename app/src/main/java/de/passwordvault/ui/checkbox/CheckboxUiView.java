package de.passwordvault.ui.checkbox;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import de.passwordvault.R;


/**
 * Class implements a checkbox for the password vault UI.
 *
 * @author  Christian-2003
 * @version 3.7.0
 */
public class CheckboxUiView extends LinearLayout {

    /**
     * Field stores the tag used for logging.
     */
    private static final String TAG = "CheckboxUiView";


    /**
     * Attribute stores the text view displaying the checkbox text.
     */
    private TextView text;

    /**
     * Attribute stores the checkbox.
     */
    private CheckBox checkbox;

    /**
     * Attribute stores the listener to invoke if the checked state is changed.
     */
    @Nullable
    private CompoundButton.OnCheckedChangeListener checkedChangeListener;


    /**
     * Constructor instantiates a new checkbox.
     *
     * @param context   Context for the checkbox.
     */
    public CheckboxUiView(Context context) {
        super(context);
        init(null);
    }

    /**
     * Constructor instantiates a new checkbox.
     *
     * @param context   Context for the checkbox.
     * @param attrs     Attributes for the checkbox.
     */
    public CheckboxUiView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    /**
     * Constructor instantiates a new checkbox.
     *
     * @param context       Context for the checkbox.
     * @param attrs         Attributes for the checkbox.
     * @param defStyleAttr  Style for the checkbox.
     */
    public CheckboxUiView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    /**
     * Constructor instantiates a new checkbox.
     *
     * @param context       Context for the checkbox.
     * @param attrs         Attributes for the checkbox.
     * @param defStyleAttr  Style for the checkbox.
     * @param defStyleRes   Theme for the checkbox.
     */
    public CheckboxUiView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }


    /**
     * Method returns the text of the checkbox.
     *
     * @return  Text of the checkbox.
     */
    public String getText() {
        return text.getText().toString();
    }

    /**
     * Method changes the text for the checkbox.
     *
     * @param text  New text for the checkbox.
     */
    public void setText(String text) {
        this.text.setText(text);
    }

    /**
     * Method returns whether the checkbox is checked.
     *
     * @return  Whether the checkbox is checked.
     */
    public boolean isChecked() {
        return checkbox.isChecked();
    }

    /**
     * Method changes whether the checkbox is checked.
     *
     * @param checked   Whether the checkbox is checked.
     */
    public void setChecked(boolean checked) {
        //Update the checked state using post. Otherwise, calling setChecked immediately after inflating
        //the view could sometimes not reflect the checked state in the UI. This occurred due to
        //timing related issues since the view is being inflated while the state change is invoked.
        //Since setChecked is called some time after this method is called, the method needs to remember
        //whether the checked change event listener is set while THIS method is called or not. Therefore,
        //correct checked change event triggering is possible even if setChecked is called later.
        boolean invokeCheckedChangeEvent = checkedChangeListener != null;
        post(() -> {
            CompoundButton.OnCheckedChangeListener listener = checkedChangeListener;
            if (!invokeCheckedChangeEvent) {
                checkedChangeListener = null;
            }
            checkbox.setChecked(checked);
            checkedChangeListener = listener;
        });
    }

    /**
     * Method changes the listener to invoke if the checkbox is (un)checked.
     *
     * @param checkedChangeListener New listener to invoke.
     */
    public void setCheckedChangeListener(@Nullable CompoundButton.OnCheckedChangeListener checkedChangeListener) {
        this.checkedChangeListener = checkedChangeListener;
    }


    /**
     * Method is called to initialize the view.
     *
     * @param attrs Attribute set for which to initializer
     */
    private void init(@Nullable AttributeSet attrs) {
        inflate(getContext(), R.layout.view_ui_checkbox, this);
        text = findViewById(R.id.text);
        checkbox = findViewById(R.id.checkbox);

        if (attrs != null) {
            try (TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CheckboxUiView)) {
                String text = a.getString(R.styleable.CheckboxUiView_text);
                boolean checked = a.getBoolean(R.styleable.CheckboxUiView_checked, false);
                if (text != null) {
                    setText(text);
                }
                setChecked(checked);
            }
            catch (Exception e) {
                Log.w(TAG, e.getMessage() == null ? "Unknown error while obtaining styled attributes" : e.getMessage());
            }
        }

        checkbox.setOnCheckedChangeListener((button, checked) -> {
            if (checkedChangeListener != null) {
                checkedChangeListener.onCheckedChanged(button, checked);
            }
        });


        LinearLayout container = findViewById(R.id.container);
        container.setOnClickListener(view -> {
            setChecked(!isChecked());
        });
    }

}
