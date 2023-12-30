package de.passwordvault.view.utils;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.concurrent.atomic.AtomicBoolean;
import de.passwordvault.R;
import de.passwordvault.model.analysis.PasswordSecurity;
import de.passwordvault.model.detail.Detail;


/**
 * Class models a builder which can generate a view to display a detail.
 * The generated view is based on {@link R.layout#view_detail_wrapper}.
 *
 * @author  Christian-2003
 * @version 3.1.0
 */
public class DetailViewBuilder {

    /**
     * Attribute stores the context in which the detail view is created.
     */
    private Context context;

    /**
     * Attribute stores the wrapper which contains the detailed view.
     */
    private View wrapper;

    /**
     * Attribute stores the view which contains the content of the detail.
     */
    private View content;

    /**
     * Attribute stores the detail whose view shall be generated.
     */
    private Detail detail;


    /**
     * Constructor instantiates a new DetailViewBuilder.
     *
     * @param context   Context in which the view shall be generated.
     * @param detail    Detail whose view shall be generated.
     */
    public DetailViewBuilder(Context context, Detail detail) {
        if (context == null || detail == null) {
            //Cannot build any view:
            return;
        }
        this.context = context;
        this.detail = detail;
    }


    /**
     * Method builds the view.
     *
     * @return  Generated view.
     */
    public View inflate() {
        //Begin building the view:
        inflateWrapper();

        //Inflate content:
        boolean somethingInflated = false;
        switch (detail.getType()) {
            case PASSWORD:
                inflatePasswordView();
                somethingInflated = true;
                break;
        }

        //Add content to wrapper:
        if (somethingInflated) {
            ((LinearLayout) wrapper.findViewById(R.id.entry_detail_view_container)).addView(content);
        }

        return wrapper;
    }


    /**
     * Method inflates the wrapper for the view.
     */
    private void inflateWrapper() {
        wrapper = View.inflate(context, R.layout.view_detail_wrapper, null);
        ((TextView)wrapper.findViewById(R.id.entry_detail_item_name)).setText(detail.getName());
        TextView textContent = wrapper.findViewById(R.id.entry_detail_item_content);
        if (detail.isObfuscated()) {
            textContent.setText(Utils.obfuscate(detail.getContent()));
            ImageButton obfuscateButton = wrapper.findViewById(R.id.entry_detail_show_button);
            obfuscateButton.setVisibility(View.VISIBLE);
            AtomicBoolean obfuscated = new AtomicBoolean(true);
            obfuscateButton.setOnClickListener(view -> {
                if (obfuscated.get()) {
                    obfuscated.set(false);
                    textContent.setText(detail.getContent());
                } else {
                    obfuscated.set(true);
                    textContent.setText(Utils.obfuscate(detail.getContent()));
                }
            });
        }
        else {
            textContent.setText(detail.getContent());
        }
    }


    /**
     * Method inflates the view which displays a password.
     */
    private void inflatePasswordView() {
        content = View.inflate(context, R.layout.view_detail_password, null);
        int securityScore = PasswordSecurity.PERFORM_SECURITY_ANALYSIS(detail.getContent());
        ProgressBar passwordSecurity = (ProgressBar)content.findViewById(R.id.entry_detail_password_security);
        passwordSecurity.setMax(PasswordSecurity.MAX_SECURITY_SCORE);
        passwordSecurity.setProgress(securityScore);
        TextView securityRatingView = content.findViewById(R.id.entry_detail_password_security_rating);
        String securityRating = securityScore + "/" + PasswordSecurity.MAX_SECURITY_SCORE;
        securityRatingView.setText(securityRating);
        if (securityScore == 0 || securityScore == 1) {
            securityRatingView.setTextColor(context.getColor(R.color.red));
        }
        else if (securityScore == 2 || securityScore == 3) {
            securityRatingView.setTextColor(context.getColor(R.color.yellow));
        }
        else if (securityScore == 4 || securityScore == 5) {
            securityRatingView.setTextColor(context.getColor(R.color.green));
        }
    }

}
