package de.passwordvault.view.settings.activity_help;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import de.passwordvault.R;
import de.passwordvault.model.rest.RestError;
import de.passwordvault.model.rest.help.LocalizedHelpPage;
import de.passwordvault.view.utils.recycler_view.OnRecyclerViewActionListener;
import de.passwordvault.view.utils.recycler_view.RecyclerViewAdapter;


/**
 * Class implements the recycler view adapter for the {@link SettingsHelpActivity}.
 *
 * @author  Christian-2003
 * @version 3.7.2
 */
public class SettingsHelpRecyclerViewAdapter extends RecyclerViewAdapter<SettingsHelpViewModel> {

    /**
     * Class models the view holder for a help page.
     */
    public static class HelpPageViewHolder extends RecyclerView.ViewHolder {

        /**
         * Attribute stores the text view displaying the title of the help page.
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
        public HelpPageViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.text_title);
            languageTextView = itemView.findViewById(R.id.text_language);
            languageImageView = itemView.findViewById(R.id.image_language);
        }

    }


    /**
     * Attribute stores the view type for help pages.
     */
    private static final int TYPE_HELP_PAGE = 1;

    /**
     * Attribute stores the offset with which the help pages are displayed within the adapter.
     */
    public static final int OFFSET_HELP_PAGES = 1;

    /**
     * Field stores the position of the empty placeholder within the adapter.
     */
    public static final int POSITION_EMPTY_PLACEHOLDER = 0;


    /**
     * Attribute stores the listener to invoke once a help page is clicked.
     */
    @Nullable
    private OnRecyclerViewActionListener helpPageClickListener;


    /**
     * Constructor instantiates a new recycler view adapter.
     *
     * @param context   Context for the adapter.
     * @param viewModel View model from which to source data.
     */
    public SettingsHelpRecyclerViewAdapter(Context context, SettingsHelpViewModel viewModel) {
        super(context, viewModel);
    }


    /**
     * Method set the click listener invoked when a help page is clicked.
     *
     * @param helpPageClickListener Listener to invoke once a help page is clicked.
     */
    public void setHelpPageClickListener(@Nullable OnRecyclerViewActionListener helpPageClickListener) {
        this.helpPageClickListener = helpPageClickListener;
    }


    /**
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (position == POSITION_EMPTY_PLACEHOLDER) {
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
                            viewHolder.headlineTextView.setText(R.string.settings_help_empty_headline_internet);
                            break;
                        case ERROR_404:
                            viewHolder.headlineTextView.setText(R.string.settings_help_empty_headline_404);
                            break;

                        case ERROR_SERIALIZATION:
                            viewHolder.headlineTextView.setText(R.string.settings_help_empty_headline_serialization);
                            break;
                    }
                }
                viewHolder.supportTextView.setText(R.string.settings_help_empty_support);
            }
        }
        else {
            HelpPageViewHolder viewHolder = (HelpPageViewHolder)holder;
            LocalizedHelpPage helpPage = viewModel.getHelpPages().get(position - OFFSET_HELP_PAGES);
            viewHolder.titleTextView.setText(helpPage.getTitle());
            if (helpPage.getLanguage().equals(context.getString(R.string.settings_help_locale))) {
                viewHolder.languageTextView.setText(R.string.settings_help_language_supported);
                viewHolder.languageImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_locale_current));
            }
            else {
                viewHolder.languageTextView.setText(R.string.settings_help_language_not_supported);
                viewHolder.languageImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_locale_en));
            }
            viewHolder.itemView.setOnClickListener(view -> {
                if (helpPageClickListener != null) {
                    helpPageClickListener.onAction(holder.getAdapterPosition());
                }
            });
        }
    }


    /**
     * Method creates a view holder for the view type specified.
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
        if (viewType == TYPE_GENERIC_EMPTY_PLACEHOLDER) {
            view = layoutInflater.inflate(R.layout.item_generic_empty_placeholder, parent, false);
            return new GenericEmptyPlaceholderViewHolder(view);
        }
        else {
            view = layoutInflater.inflate(R.layout.item_help, parent, false);
            return new HelpPageViewHolder(view);
        }
    }


    /**
     * Method returns the item view type for the position specified.
     *
     * @param position  Position to query
     * @return          View type for the position queried.
     */
    @Override
    public int getItemViewType(int position) {
        return position == POSITION_EMPTY_PLACEHOLDER ? TYPE_GENERIC_EMPTY_PLACEHOLDER : TYPE_HELP_PAGE;
    }


    /**
     * Method returns the number of items within the recycler view adapter.
     *
     * @return  Number of items within the adapter.
     */
    @Override
    public int getItemCount() {
        return viewModel.getHelpPages().size() + 1;
    }

}
