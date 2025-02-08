package de.passwordvault.view.passwords.activity_duplicates;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import de.passwordvault.R;
import de.passwordvault.model.analysis.QualityGateManager;
import de.passwordvault.model.analysis.passwords.Password;
import de.passwordvault.model.entry.EntryAbbreviated;
import de.passwordvault.view.utils.Utils;
import de.passwordvault.view.utils.recycler_view.OnRecyclerViewActionListener;
import de.passwordvault.view.utils.recycler_view.RecyclerViewAdapter;


/**
 * Class implements the recycler view adapter for the {@link DuplicatePasswordEntriesActivity}.
 *
 * @author  Christian-2003
 * @version 3.7.2
 */
public class DuplicatePasswordEntriesRecyclerViewAdapter extends RecyclerViewAdapter<DuplicatePasswordEntriesViewModel> {

    /**
     * Class models the view holder for the entries displayed on this page.
     */
    public static class EntryViewHolder extends RecyclerView.ViewHolder {

        /**
         * Attribute stores the image view displaying the app icon of the entry.
         */
        public final ImageView logoImageView;

        /**
         * Attribute stores the text view displaying the abbreviation for the entry in case no
         * app icon is available.
         */
        public final TextView abbreviationTextView;

        /**
         * Attribute stores the text view displaying the name of the entry.
         */
        public final TextView nameTextView;

        /**
         * Attribute stores the text view displaying the description of the entry.
         */
        public final TextView descriptionTextView;


        /**
         * Constructor instantiates a new view holder for the passed view.
         *
         * @param itemView  Inflated view for which to create the view holder.
         */
        public EntryViewHolder(View itemView) {
            super(itemView);
            logoImageView = itemView.findViewById(R.id.image_logo);
            abbreviationTextView = itemView.findViewById(R.id.text_abbreviation);
            nameTextView = itemView.findViewById(R.id.text_name);
            descriptionTextView = itemView.findViewById(R.id.text_description);
        }

    }

    /**
     * Class models the view holder for the header displayed on the page.
     */
    public static class HeaderViewHolder extends RecyclerView.ViewHolder {

        /**
         * Attribute stores the text view displaying the duplicate password.
         */
        public final TextView passwordTextView;

        /**
         * Attribute stores the text view displaying the button through which to obfuscate the
         * password displayed.
         */
        public final ImageButton obfuscateButton;

        /**
         * Attribute stores the text view displaying the number of accounts using the password.
         */
        public final TextView numberTextView;

        /**
         * Attribute stores the progress bar displaying the password security score.
         */
        public final ProgressBar scoreProgressBar;

        /**
         * Attribute stores the text view displaying the password security score.
         */
        public final TextView scoreTextView;


        public HeaderViewHolder(View itemView) {
            super(itemView);
            passwordTextView = itemView.findViewById(R.id.text_password);
            obfuscateButton = itemView.findViewById(R.id.button_obfuscate);
            numberTextView = itemView.findViewById(R.id.text_number);
            scoreProgressBar = itemView.findViewById(R.id.progress_bar);
            scoreTextView = itemView.findViewById(R.id.text_score);
        }

    }


    /**
     * Field stores the view type for entries.
     */
    private static final int TYPE_DUPLICATES_ENTRY = 1;

    /**
     * Field stores the view type for the header displayed at the top of the page.
     */
    private static final int TYPE_DUPLICATES_HEADER = 3;

    /**
     * Field stores the offset for the list of entries within the adapter.
     */
    public static final int OFFSET_ENTRIES = 2;

    /**
     * Attribute stores the position for the header within the recycler view.
     */
    public static final int POSITION_HEADER = 0;


    /**
     * Attribute stores the action listener invoked when an item is clicked.
     */
    @Nullable
    private OnRecyclerViewActionListener itemClickListener;

    /**
     * Attribute stores a list of (filtered) entries to display to the user.
     */
    @NonNull
    private final ArrayList<EntryAbbreviated> filteredEntries;


    /**
     * Constructor instantiates a new recycler view adapter.
     *
     * @param context   Context for the adapter.
     * @param viewModel View model for the adapter.
     */
    public DuplicatePasswordEntriesRecyclerViewAdapter(@NonNull Context context, @NonNull DuplicatePasswordEntriesViewModel viewModel) {
        super(context, viewModel);
        filteredEntries = new ArrayList<>(viewModel.getEntries());
    }


    /**
     * Method changes the click listener invoked when an item is clicked.
     *
     * @param itemClickListener New listener.
     */
    public void setItemClickListener(@Nullable OnRecyclerViewActionListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }


    /**
     * Method creates a new view holder.
     *
     * @param parent    Parent for the item view of the view holder.
     * @param viewType  The view type of the new View.
     * @return          Created view holder.
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case TYPE_DUPLICATES_HEADER:
                view = layoutInflater.inflate(R.layout.item_password_duplicate_header, parent, false);
                return new HeaderViewHolder(view);
            case TYPE_GENERIC_HEADLINE:
                view = layoutInflater.inflate(R.layout.item_generic_headline, parent, false);
                return new GenericHeadlineViewHolder(view);
            default:
                view = layoutInflater.inflate(R.layout.item_entry, parent, false);
                return new EntryViewHolder(view);
        }
    }


    /**
     * Method binds data to the view holder at the specified position.
     *
     * @param holder    View holder to update.
     * @param position  The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof EntryViewHolder) {
            EntryViewHolder viewHolder = (EntryViewHolder)holder;
            EntryAbbreviated entry = filteredEntries.get(position - OFFSET_ENTRIES);
            Drawable logo = entry.getLogo();
            viewHolder.logoImageView.setImageDrawable(logo);
            if (logo == null) {
                viewHolder.abbreviationTextView.setText("" + entry.getName().charAt(0));
            }
            viewHolder.abbreviationTextView.setVisibility(logo == null ? View.VISIBLE : View.GONE);
            viewHolder.nameTextView.setText(entry.getName());
            viewHolder.descriptionTextView.setText(entry.getDescription());
            viewHolder.itemView.setOnClickListener(view -> {
                if (itemClickListener != null) {
                    itemClickListener.onAction(holder.getAdapterPosition());
                }
            });
        }
        else if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder viewHolder = (HeaderViewHolder)holder;
            Password password = viewModel.getPasswords().get(0);
            viewHolder.passwordTextView.setText(Utils.obfuscate(password.getCleartextPassword()));
            viewHolder.numberTextView.setText(context.getString(R.string.password_results_duplicates_number).replace("{arg}", "" + viewModel.getEntries().size()));
            viewHolder.scoreTextView.setText("" + password.getSecurityScore() + " / " + QualityGateManager.getInstance().numberOfQualityGates());
            viewHolder.scoreTextView.setTextColor(Utils.getPasswordSecurityScoreColor(password.getSecurityScore()));

            viewHolder.scoreProgressBar.setMax(QualityGateManager.getInstance().numberOfQualityGates() * 1000);
            if (!viewModel.isScoreBarAnimated()) {
                ValueAnimator animator = ValueAnimator.ofInt(0, password.getSecurityScore() * 1000);
                animator.setDuration(context.getResources().getInteger(R.integer.default_anim_duration) * 5L);
                animator.addUpdateListener(animation -> viewHolder.scoreProgressBar.setProgress((int) animation.getAnimatedValue()));
                animator.start();
                viewModel.setScoreBarAnimated(true);
            }
            else {
                viewHolder.scoreProgressBar.setProgress(password.getSecurityScore() * 1000);
            }

            AtomicBoolean obfuscated = new AtomicBoolean(true);
            viewHolder.obfuscateButton.setOnClickListener(view -> {
                viewHolder.passwordTextView.setText(obfuscated.get() ? password.getCleartextPassword() : Utils.obfuscate(password.getCleartextPassword()));
                viewHolder.obfuscateButton.setImageDrawable(ContextCompat.getDrawable(context, obfuscated.get() ? R.drawable.ic_show_off : R.drawable.ic_show));
                obfuscated.set(!obfuscated.get());
            });
        }
        else if (holder instanceof GenericHeadlineViewHolder) {
            GenericHeadlineViewHolder viewHolder = (GenericHeadlineViewHolder)holder;
            viewHolder.headlineTextView.setText(context.getString(R.string.password_results_duplicates_accounts));
            viewHolder.dividerView.setVisibility(View.VISIBLE);
        }
    }


    /**
     * Method returns the view type for the specified position.
     *
     * @param position  Position to query.
     * @return          View type for the queried position.
     */
    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case 0:
                return TYPE_DUPLICATES_HEADER;
            case 1:
                return TYPE_GENERIC_HEADLINE;
            default:
                return TYPE_DUPLICATES_ENTRY;
        }
    }


    /**
     * Method returns the number of items displayed.
     *
     * @return  Number of items displayed.
     */
    @Override
    public int getItemCount() {
        return filteredEntries.size() + 2;
    }


    /**
     * Method resets the filter for the adapter.
     */
    public void resetFilter() {
        filteredEntries.clear();
        filteredEntries.addAll(viewModel.getEntries());
        notifyDataSetChanged();
    }

    /**
     * Method uses the passed search query to filter the displayed entries.
     *
     * @param query Query to use for filtering.
     */
    public void filter(@Nullable String query) {
        if (query == null || query.isEmpty()) {
            resetFilter();
            return;
        }
        int count = filteredEntries.size();
        filteredEntries.clear();
        query = query.toLowerCase();
        for (EntryAbbreviated entry : viewModel.getEntries()) {
            if (entry.matchesFilter(query)) {
                filteredEntries.add(entry);
            }
        }
        notifyDataSetChanged();
    }

    /**
     * Method returns the entry for the passed adapter position.
     *
     * @param position  Position whose entry to return.
     * @return          Entry at the specified position.
     */
    public EntryAbbreviated getEntryForAdapterPosition(int position) {
        return filteredEntries.get(position - OFFSET_ENTRIES);
    }

}
