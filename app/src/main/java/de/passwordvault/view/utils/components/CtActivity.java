package de.passwordvault.view.utils.components;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import de.passwordvault.R;


/**
 * Class implements an activity which can handle a collapsing toolbar. The content of the activity
 * must always be {@link CtContainer}.
 *
 * @author  Christian-2003
 * @version 3.5.1
 */
public abstract class CtActivity extends PasswordVaultBaseActivity {

    /**
     * Attribute stores the app bar layout of the activity.
     */
    private AppBarLayout appBarLayout;

    /**
     * Attribute stores the collapsing toolbar layout of the activity.
     */
    private CollapsingToolbarLayout collapsingToolbarLayout;

    /**
     * Attribute stores the toolbar of the activity.
     */
    private Toolbar toolbar;

    /**
     * Attribute stores the back-button if the activity.
     */
    private ImageButton buttonBack;

    /**
     * Attribute stores the nested scroll view which contains the content of the activity.
     */
    private NestedScrollView container;

    /**
     * Attribute stores the CtContainer which contains the entire content of the activity.
     */
    private CtContainer content;

    /**
     * Attribute stores the linear layout which contains the custom image buttons for the toolbar.
     */
    private LinearLayout imageButtons;


    /**
     * Method returns the {@linkplain ImageButton} of the specified resource-ID from within the
     * toolbar. If no image button with the specified ID exists, {@code null} is returned.
     *
     * @param id    ID of the image button to return.
     * @return      Image button with the specified ID within the toolbar.
     */
    @Nullable
    public View findToolbarButtonById(@IdRes int id) {
        for (int i = 0; i < imageButtons.getChildCount(); i++) {
            View view = imageButtons.getChildAt(i);
            if (view.getId() == id) {
                return view;
            }
        }
        return null;
    }

    /**
     * Method returns the back button of the toolbar.
     *
     * @return  Back button of the toolbar.
     */
    @Nullable
    public View getToolbarBackButton() {
        return buttonBack;
    }


    /**
     * Method sets the content view of the activity. The layout whose resource-ID is passed must be
     * a {@link CtContainer}!
     *
     * @param layoutRes             Layout to set as content of the activity.
     * @throws ClassCastException   The passed layout is no CtContainer.
     */
    @Override
    public final void setContentView(@LayoutRes int layoutRes) throws ClassCastException {
        container = findViewById(R.id.custom_activity_content_container);
        try {
            content = (CtContainer)getLayoutInflater().inflate(layoutRes, container, false);
        }
        catch (ClassCastException e) {
            throw new ClassCastException("Activity content must be CustomActivityContainer. Current type: " + getLayoutInflater().inflate(layoutRes, null).getClass().getTypeName());
        }
        container.addView(content);
        collapsingToolbarLayout.setTitle(content.getTitle() == null ? getString(R.string.app_name) : content.getTitle());

        int toolbarInsetStart = toolbar.getContentInsetStart();
        int toolbarInsetEnd = toolbar.getContentInsetEnd();
        buttonBack = findViewById(R.id.button_back);
        if (content.hasBackButton()) {
            buttonBack.setVisibility(View.VISIBLE);
            buttonBack.setOnClickListener(view -> finish());
            toolbarInsetStart = buttonBack.getMinimumWidth();
        }
        else {
            buttonBack.setVisibility(View.GONE);
        }

        for (int i = 0; i < content.getChildCount(); i++) {
            View child = content.getChildAt(i);
            if (child instanceof ImageButton) {
                content.removeView(child);
                imageButtons.addView(child);
                toolbarInsetEnd += child.getMinimumWidth();
                i--;
            }
        }
        toolbar.setContentInsetsRelative(toolbarInsetStart, toolbarInsetEnd);

    }


    /**
     * Method is called whenever the activity is created.
     *
     * @param savedInstanceState    Previously saved state of the instance.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_ct);
        appBarLayout = findViewById(R.id.app_bar_layout);
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar_layout);
        toolbar = findViewById(R.id.toolbar);
        imageButtons = findViewById(R.id.image_buttons);
    }

}
