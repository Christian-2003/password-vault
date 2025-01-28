package de.passwordvault.view.settings.activity_about;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Calendar;
import de.passwordvault.BuildConfig;
import de.passwordvault.R;
import de.passwordvault.model.rest.RestError;
import de.passwordvault.model.rest.legal.LocalizedLegalPage;
import de.passwordvault.view.utils.recycler_view.OnRecyclerViewActionListener;
import de.passwordvault.view.utils.recycler_view.RecyclerViewAdapter;


/**
 * Class implements the recycler view adapter for the {@link SettingsAboutActivity}.
 *
 * @author  Christian-2003
 * @version 3.7.2
 */
public class SettingsAboutRecyclerViewAdapter extends RecyclerViewAdapter<SettingsAboutViewModel> {

    /**
     * Class models the view holder for the header of the activity.
     */
    public static class PasswordVaultViewHolder extends RecyclerView.ViewHolder {

        /**
         * Attribute stores the text view displaying the version of the app.
         */
        public final TextView versionTextView;

        /**
         * Attribute stores the text view displaying the copyright notice.
         */
        public final TextView copyrightTextView;


        /**
         * Constructor instantiates a new view holder for the view specified.
         *
         * @param itemView  Inflated view for which to create the view holder.
         */
        public PasswordVaultViewHolder(View itemView) {
            super(itemView);
            versionTextView = itemView.findViewById(R.id.settings_about_software_version);
            copyrightTextView = itemView.findViewById(R.id.settings_about_software_copyright);
        }

    }

    /**
     * Class models the view holder for a legal page.
     */
    public static class LegalPageViewHolder extends RecyclerView.ViewHolder {

        /**
         * Attribute stores the text view displaying the title of the legal page.
         */
        public final TextView titleTextView;

        /**
         * Attribute stores the text view displaying the supported language.
         */
        public final TextView languageTextView;

        /**
         * Attribute stores the image view displaying a flag to indicate the supported language.
         */
        public final ImageView languageImageView;


        /**
         * Constructor instantiates a new view holder for the view specified.
         *
         * @param itemView  Inflated view for which to create the view holder.
         */
        public LegalPageViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.text_title);
            languageTextView = itemView.findViewById(R.id.text_language);
            languageImageView = itemView.findViewById(R.id.image_language);
        }

    }


    private static final int TYPE_LEGAL_PAGE = 1;

    private static final int TYPE_PASSWORD_VAULT = 3;

    public static final int POSITION_PASSWORD_VAULT = 0;

    public static final int POSITION_HEADLINE = 1;

    public static final int POSITION_EMPTY_PLACEHOLDER = 2;

    public static final int OFFSET_LEGAL_PAGES = 3;


    @Nullable
    private OnRecyclerViewActionListener legalPageClickListener;



    public SettingsAboutRecyclerViewAdapter(Context context, SettingsAboutViewModel viewModel) {
        super(context, viewModel);
        legalPageClickListener = null;
    }


    public void setLegalPageClickListener(@Nullable OnRecyclerViewActionListener legalPageClickListener) {
        this.legalPageClickListener = legalPageClickListener;
    }


    /**
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (position) {
            case POSITION_PASSWORD_VAULT: {
                //Password Vault:
                PasswordVaultViewHolder viewHolder = (PasswordVaultViewHolder)holder;
                String version = BuildConfig.VERSION_NAME;
                if (BuildConfig.DEBUG) {
                    version += " (Debug Build)";
                }
                viewHolder.versionTextView.setText(context.getString(R.string.settings_about_software_version).replace("{arg}", version));
                String copyright = "" + Calendar.getInstance().get(Calendar.YEAR);
                viewHolder.copyrightTextView.setText(context.getString(R.string.settings_about_software_copyright).replace("{arg}", copyright));
                break;
            }
            case POSITION_HEADLINE: {
                //Headline:
                GenericHeadlineViewHolder viewHolder = (GenericHeadlineViewHolder)holder;
                viewHolder.dividerView.setVisibility(View.VISIBLE);
                viewHolder.headlineTextView.setText(R.string.settings_about_usage);
                break;
            }
            case POSITION_EMPTY_PLACEHOLDER: {
                //Empty placeholder:
                GenericEmptyPlaceholderViewHolder viewHolder = (GenericEmptyPlaceholderViewHolder)holder;
                if (viewModel.isFinished() && viewModel.getError() == RestError.SUCCESS) {
                    viewHolder.itemView.setVisibility(View.GONE);
                    viewHolder.itemView.setLayoutParams(new ViewGroup.LayoutParams(0, 0));
                }
                else {
                    viewHolder.itemView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    viewHolder.itemView.setVisibility(View.VISIBLE);
                    viewHolder.imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.el_help));
                    if (viewModel.getError() != null) {
                        switch (viewModel.getError()) {
                            case ERROR_INTERNET:
                                viewHolder.headlineTextView.setText(R.string.settings_about_empty_headline_internet);
                                break;
                            case ERROR_404:
                                viewHolder.headlineTextView.setText(R.string.settings_about_empty_headline_404);
                                break;

                            case ERROR_SERIALIZATION:
                                viewHolder.headlineTextView.setText(R.string.settings_about_empty_headline_serialization);
                                break;
                        }
                    }
                    viewHolder.supportTextView.setText(R.string.settings_about_empty_support);
                }
                break;
            }
            default: {
                //Legal page:
               LegalPageViewHolder viewHolder = (LegalPageViewHolder)holder;
                LocalizedLegalPage legalPage = viewModel.getLegalPages().get(position - OFFSET_LEGAL_PAGES);
                viewHolder.titleTextView.setText(legalPage.getTitle());
                if (legalPage.getLanguage().equals(context.getString(R.string.settings_help_locale))) {
                    viewHolder.languageTextView.setText(R.string.settings_about_language_supported);
                    viewHolder.languageImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_locale_current));
                }
                else {
                    viewHolder.languageTextView.setText(R.string.settings_about_language_not_supported);
                    viewHolder.languageImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_locale_en));
                }
                viewHolder.itemView.setOnClickListener(view -> {
                    if (legalPageClickListener != null) {
                        legalPageClickListener.onAction(holder.getAdapterPosition());
                    }
                });
                break;
            }
        }
    }


    /**
     * Method creates a new view holder for the view type specified.
     *
     * @param parent    The ViewGroup into which the new View will be added after it is bound to
     *                  an adapter position.
     * @param viewType  The view type of the new View.
     * @return          Created view holder.
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case TYPE_PASSWORD_VAULT:
                view = layoutInflater.inflate(R.layout.item_about_passwordvault, parent, false);
                return new PasswordVaultViewHolder(view);
            case TYPE_GENERIC_HEADLINE:
                view = layoutInflater.inflate(R.layout.item_generic_headline, parent, false);
                return new GenericHeadlineViewHolder(view);
            case TYPE_GENERIC_EMPTY_PLACEHOLDER:
                view = layoutInflater.inflate(R.layout.item_generic_empty_placeholder, parent, false);
                return new GenericEmptyPlaceholderViewHolder(view);
            default:
                view = layoutInflater.inflate(R.layout.item_about_legal, parent, false);
                return new LegalPageViewHolder(view);
        }
    }


    /**
     * Method returns the item view type for the position specified.
     *
     * @param position  Position to query.
     * @return          View type for the position queried.
     */
    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case POSITION_PASSWORD_VAULT:
                return TYPE_PASSWORD_VAULT;
            case POSITION_HEADLINE:
                return TYPE_GENERIC_HEADLINE;
            case POSITION_EMPTY_PLACEHOLDER:
                return TYPE_GENERIC_EMPTY_PLACEHOLDER;
            default:
                return TYPE_LEGAL_PAGE;
        }
    }


    /**
     * Method returns the number of items displayed by the recycler view.
     *
     * @return  Number of items displayed.
     */
    @Override
    public int getItemCount() {
        return viewModel.getLegalPages().size() + 3;
    }

}
