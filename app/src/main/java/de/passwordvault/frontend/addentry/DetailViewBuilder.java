package de.passwordvault.frontend.addentry;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.atomic.AtomicBoolean;

import de.passwordvault.R;
import de.passwordvault.backend.Singleton;
import de.passwordvault.backend.analysis.PasswordSecurity;
import de.passwordvault.backend.entry.Detail;


/**
 * Class models a builder which can generate a view to display a detail.
 * The generated view is based on {@link R.layout#view_detail_wrapper}.
 *
 * @author  Christian-2003
 * @version 1.0.0
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
            case Detail.TYPE_PASSWORD:
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
            textContent.setText(Singleton.OBFUSCATE(detail.getContent()));
            ImageButton obfuscateButton = wrapper.findViewById(R.id.entry_detail_show_button);
            obfuscateButton.setVisibility(View.VISIBLE);
            AtomicBoolean obfuscated = new AtomicBoolean(true);
            obfuscateButton.setOnClickListener(view -> {
                if (obfuscated.get()) {
                    obfuscated.set(false);
                    textContent.setText(detail.getContent());
                } else {
                    obfuscated.set(true);
                    textContent.setText(Singleton.OBFUSCATE(detail.getContent()));
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
        switch (securityScore) {
            case 5:
                content.findViewById(R.id.entry_detail_password_security_5).setVisibility(View.VISIBLE);
            case 4:
                content.findViewById(R.id.entry_detail_password_security_4).setVisibility(View.VISIBLE);
            case 3:
                content.findViewById(R.id.entry_detail_password_security_3).setVisibility(View.VISIBLE);
            case 2:
                content.findViewById(R.id.entry_detail_password_security_2).setVisibility(View.VISIBLE);
            case 1:
                content.findViewById(R.id.entry_detail_password_security_1).setVisibility(View.VISIBLE);
            case 0:
                content.findViewById(R.id.entry_detail_password_security_0).setVisibility(View.VISIBLE);
        }
        TextView securityRatingView = content.findViewById(R.id.entry_detail_password_security_rating);
        switch (securityScore) {
            case 0:
                securityRatingView.setText(context.getString(R.string.password_0));
                securityRatingView.setTextColor(context.getColor(R.color.red));
                break;
            case 1:
                securityRatingView.setText(context.getString(R.string.password_1));
                securityRatingView.setTextColor(context.getColor(R.color.red));
                break;
            case 2:
                securityRatingView.setText(context.getString(R.string.password_2));
                securityRatingView.setTextColor(context.getColor(R.color.yellow));
                break;
            case 3:
                securityRatingView.setText(context.getString(R.string.password_3));
                securityRatingView.setTextColor(context.getColor(R.color.yellow));
                break;
            case 4:
                securityRatingView.setText(context.getString(R.string.password_4));
                securityRatingView.setTextColor(context.getColor(R.color.green));
                break;
            case 5:
                securityRatingView.setText(context.getString(R.string.password_5));
                securityRatingView.setTextColor(context.getColor(R.color.green));
                break;
        }
    }

}
