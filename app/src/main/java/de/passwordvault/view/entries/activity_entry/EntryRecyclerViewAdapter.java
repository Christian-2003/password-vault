package de.passwordvault.view.entries.activity_entry;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;
import de.passwordvault.R;
import de.passwordvault.model.analysis.QualityGateManager;
import de.passwordvault.model.detail.Detail;
import de.passwordvault.model.detail.DetailType;
import de.passwordvault.model.entry.EntryExtended;
import de.passwordvault.model.tags.Tag;
import de.passwordvault.view.utils.Utils;
import de.passwordvault.view.utils.recycler_view.OnRecyclerViewActionListener;
import de.passwordvault.view.utils.recycler_view.RecyclerViewAdapter;


/**
 * Class implements the recycler view adapter for the {@link EntryActivity}.
 *
 * @author  Christian-2003
 * @version 3.7.0
 */
public class EntryRecyclerViewAdapter extends RecyclerViewAdapter<EntryViewModel> implements EntryRecyclerViewMoveCallback.EntryMoveContract {

    /**
     * Class models the view holder for the view displaying general information.
     */
    public static class GeneralViewHolder extends RecyclerView.ViewHolder {

        /**
         * Attribute stores the text view displaying the description of the entry.
         */
        public final TextView descriptionTextView;

        /**
         * Attribute stores the text view displaying the abbreviation of the account name if no
         * app logo is available.
         */
        public final TextView abbreviationTextView;

        /**
         * Attribute stores the button used to edit the entry.
         */
        public final Button editButton;

        /**
         * Attribute stores the button to delete the entry.
         */
        public final Button deleteButton;

        /**
         * Attribute stores the image view displaying the app image.
         */
        public final ImageView appImageView;

        /**
         * Attribute stores the chip group used as container for the tags.
         */
        public final ChipGroup tagsContainer;


        /**
         * Constructor instantiates a new view holder.
         *
         * @param itemView  Item view from which to create the view holder.
         */
        public GeneralViewHolder(View itemView) {
            super(itemView);
            descriptionTextView = itemView.findViewById(R.id.text_description);
            abbreviationTextView = itemView.findViewById(R.id.text_abbreviation);
            editButton = itemView.findViewById(R.id.button_edit);
            deleteButton = itemView.findViewById(R.id.button_delete);
            appImageView = itemView.findViewById(R.id.image_app);
            tagsContainer= itemView.findViewById(R.id.container_tags);
        }

    }

    /**
     * Class models the view holder for the view displaying the button to expand / collapse the
     * list of invisible details.
     */
    public static class MoreButtonViewHolder extends RecyclerView.ViewHolder {

        /**
         * Attribute store the more button to expand / collapse the list of invisible details.
         */
        public final Button moreButton;


        /**
         * Constructor instantiates a new view holder for the specified view.
         *
         * @param itemView  Inflated view for which to create the view holder.
         */
        public MoreButtonViewHolder(View itemView) {
            super(itemView);
            moreButton = itemView.findViewById(R.id.button_more);
        }

    }

    /**
     * Class models the view holder for the view displaying information about a detail.
     */
    public static class DetailViewHolder extends RecyclerView.ViewHolder {

        /**
         * Attribute stores the text view displaying the name of the detail.
         */
        public final TextView nameTextView;

        /**
         * Attribute stores the text view displaying the content of the detail.
         */
        public final TextView contentTextView;

        /**
         * Attribute stores the image button used to show a dialog displaying more options for the
         * detail.
         */
        public final ImageButton moreImageButton;

        /**
         * Attribute stores the image view displaying an image corresponding to the detail type.
         */
        public final ImageView detailImageView;

        /**
         * Attribute stores the image button used to (un-)obfuscate the content of the detail.
         */
        public final ImageButton obfuscateImageButton;

        /**
         * Attribute stores the image view displaying whether the detail is used as password or
         * username for autofill.
         */
        public final ImageView autofillImageView;


        /**
         * Constructor instantiates a new view holder.
         *
         * @param itemView  Item view from which to create the view holder.
         */
        public DetailViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.text_name);
            contentTextView = itemView.findViewById(R.id.text_content);
            moreImageButton = itemView.findViewById(R.id.button_more);
            detailImageView = itemView.findViewById(R.id.image_detail);
            obfuscateImageButton = itemView.findViewById(R.id.button_obfuscate);
            autofillImageView = itemView.findViewById(R.id.image_autofill);
        }

    }

    /**
     * Class models the view holder for the view displaying information about a detail password.
     */
    public static class DetailPasswordViewHolder extends DetailViewHolder {

        /**
         * Attribute stores the text view displaying the password security score.
         */
        public final TextView scoreTextView;

        /**
         * Attribute stores the progress bar displaying the score progress bar.
         */
        public final ProgressBar scoreProgressBar;


        /**
         * Constructor instantiates a new view holder for the specified item view.
         *
         * @param itemView  Inflated view for which to create the view holder.
         */
        public DetailPasswordViewHolder(View itemView) {
            super(itemView);
            scoreTextView = itemView.findViewById(R.id.text_score);
            scoreProgressBar = itemView.findViewById(R.id.progress_bar);
        }

    }


    /**
     * Attribute stores the offset with which the details begin displaying within the adapter.
     */
    public static final int OFFSET_DETAILS = 3;

    /**
     * Attribute stores the position of the general info within the adapter.
     */
    public static final int POSITION_GENERAL = 0;

    /**
     * Attribute stores the position of the empty placeholder for the details.
     */
    public static final int POSITION_DETAILS_EMPTY_PLACEHOLDER = 2;


    /**
     * Field stores the view type for the general item view.
     */
    private static final int TYPE_GENERAL = 0;

    /**
     * Field stores the view type for the detail item view.
     */
    private static final int TYPE_DETAIL = 1;

    /**
     * Field stores the view type for the more button.
     */
    private static final int TYPE_MORE_BUTTON = 3;

    /**
     * Field stores the view type for the detail item view displaying a password.
     */
    private static final int TYPE_DETAIL_PASSWORD = 5;


    /**
     * Attribute stores the click listener which is called when the delete button is clicked.
     */
    @Nullable
    private View.OnClickListener deleteClickListener;

    /**
     * Attribute stores the action listener to invoke when the entry shall be edited.
     */
    @Nullable
    private OnRecyclerViewActionListener editEntryListener;

    /**
     * Attribute stores the action listener to invoke when the packages for the entry shall be changed.
     */
    @Nullable
    private OnRecyclerViewActionListener editPackagesListener;

    /**
     * Attribute stores the action listener to invoke when the button to add a new detail is clicked.
     */
    @Nullable
    private OnRecyclerViewActionListener addDetailListener;

    /**
     * Attribute stores the action listener to invoke when the 'more' button of a detail is clicked.
     */
    @Nullable
    private OnRecyclerViewActionListener moreListener;


    /**
     * Constructor instantiates a new recycler view adapter.
     *
     * @param context   Context for the recycler view.
     * @param viewModel View model from which to source the data.
     */
    public EntryRecyclerViewAdapter(@NonNull Context context, @NonNull EntryViewModel viewModel) {
        super(context, viewModel);
        editEntryListener = null;
        editPackagesListener = null;
        addDetailListener = null;
        moreListener = null;
    }


    /**
     * Method changes the click listener that is called when the edit button is clicked.
     *
     * @param editEntryListener New listener.
     */
    public void setEditEntryListener(@Nullable OnRecyclerViewActionListener editEntryListener) {
        this.editEntryListener = editEntryListener;
    }

    /**
     * Method changes the click listener that is called when the delete button is clicked.
     *
     * @param clickListener New click listener.
     */
    public void setDeleteClickListener(@Nullable View.OnClickListener clickListener) {
        this.deleteClickListener = clickListener;
    }

    /**
     * Method changes the click listener that is called when the app image is clicked.
     *
     * @param editPackagesListener  New click listener.
     */
    public void setEditPackagesListener(@Nullable OnRecyclerViewActionListener editPackagesListener) {
        this.editPackagesListener = editPackagesListener;
    }

    /**
     * Method changes the action listener to invoke when the button to add a new detail is clicked.
     * 
     * @param addDetailListener New listener to invoke.
     */
    public void setAddDetailListener(@Nullable OnRecyclerViewActionListener addDetailListener) {
        this.addDetailListener = addDetailListener;
    }

    /**
     * Method changes the action listener to invoked when the 'more' button of a detail is clicked.
     *
     * @param moreListener  New listener to invoke.
     */
    public void setMoreListener(@Nullable OnRecyclerViewActionListener moreListener) {
        this.moreListener = moreListener;
    }


    /**
     * Method binds data to the passed view holder.
     *
     * @param viewHolder    The view holder to which to bind data.
     * @param position      Position of the view holder within the dataset.
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        EntryExtended entry = viewModel.getEntry();
        if (entry == null) {
            return;
        }
        if (viewHolder instanceof GeneralViewHolder) {
            GeneralViewHolder holder = (GeneralViewHolder)viewHolder;
            holder.descriptionTextView.setText(entry.getDescription());
            holder.descriptionTextView.setVisibility(entry.getDescription().isEmpty() ? View.GONE : View.VISIBLE);
            holder.editButton.setOnClickListener(view -> {
                if (editEntryListener != null) {
                    editEntryListener.onAction(holder.getAdapterPosition());
                }
            });
            holder.deleteButton.setOnClickListener(view -> {
                if (deleteClickListener != null) {
                    deleteClickListener.onClick(view);
                }
            });
            Drawable appLogo = viewModel.getEntry().getLogo();
            if (appLogo == null) {
                holder.appImageView.setVisibility(View.GONE);
                holder.abbreviationTextView.setVisibility(View.VISIBLE);
                holder.abbreviationTextView.setText(!entry.getName().isEmpty() ? "" + entry.getName().charAt(0) : "?");
                holder.abbreviationTextView.setOnClickListener(view -> {
                    if (editPackagesListener != null) {
                        editPackagesListener.onAction(holder.getAdapterPosition());
                    }
                });
            }
            else {
                //Create a new drawable in the next line. If this would not be done, the app icon in the
                //list view (e.g. in EntriesFragment) would be stretched after closing the EntryActivity,
                //since the drawable is stretched for display in this activity:
                appLogo = appLogo.getConstantState().newDrawable().mutate();
                holder.appImageView.setBackground(appLogo);
                holder.appImageView.setVisibility(View.VISIBLE);
                holder.abbreviationTextView.setVisibility(View.GONE);
                holder.appImageView.setOnClickListener(view -> {
                    if (editPackagesListener != null) {
                        editPackagesListener.onAction(holder.getAdapterPosition());
                    }
                });
            }
            holder.tagsContainer.removeAllViews();
            for (Tag tag : entry.getTags()) {
                Chip chip = new Chip(context);
                chip.setText(tag.getName());
                holder.tagsContainer.addView(chip);
            }
        }
        else if (viewHolder instanceof GenericHeadlineButtonViewHolder) {
            GenericHeadlineButtonViewHolder holder = (GenericHeadlineButtonViewHolder)viewHolder;
            holder.dividerView.setVisibility(View.VISIBLE);
            holder.buttonImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_add));
            holder.headlineTextView.setText(R.string.entry_details_info);
            holder.itemView.setOnClickListener(view -> {
                if (addDetailListener != null) {
                    addDetailListener.onAction(holder.getAdapterPosition());
                }
            });
        }
        else if (viewHolder instanceof GenericEmptyPlaceholderViewHolder) {
            GenericEmptyPlaceholderViewHolder holder = (GenericEmptyPlaceholderViewHolder)viewHolder;
            holder.imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.el_details));
            holder.headlineTextView.setText(R.string.entry_details_empty_headline);
            holder.supportTextView.setText(R.string.entry_details_empty_support);
            if (!entry.getVisibleDetails().isEmpty()) {
                holder.itemView.setVisibility(View.GONE);
                holder.itemView.setLayoutParams(new ViewGroup.LayoutParams(0, 0));
            }
            else {
                holder.itemView.setVisibility(View.VISIBLE);
                holder.itemView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
        }
        else if (viewHolder instanceof DetailViewHolder) {
            DetailViewHolder holder = (DetailViewHolder)viewHolder;
            Detail detail;
            boolean visible;
            AtomicBoolean obfuscated = new AtomicBoolean(false);
            if (position < getOffsetInvisibleDetails()) {
                detail = entry.getVisibleDetails().get(position - OFFSET_DETAILS);
                visible = true;
            }
            else {
                detail = entry.getInvisibleDetails().get(position - getOffsetInvisibleDetails());
                visible = viewModel.areInvisibleDetailsExpanded();
            }
            if (visible) {
                holder.itemView.setVisibility(View.VISIBLE);
                holder.itemView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                holder.nameTextView.setText(detail.getName());
                if (detail.isObfuscated()) {
                    holder.contentTextView.setText(Utils.obfuscate(detail.getContent()));
                }
                else {
                    holder.contentTextView.setText(detail.getContent());
                }
                holder.moreImageButton.setOnClickListener(view -> {
                    if (moreListener != null) {
                        moreListener.onAction(viewHolder.getAdapterPosition());
                    }
                });
                holder.detailImageView.setImageDrawable(AppCompatResources.getDrawable(context, detail.getType().getDrawable()));
                holder.obfuscateImageButton.setVisibility(detail.isObfuscated() ? View.VISIBLE : View.GONE);
                holder.obfuscateImageButton.setOnClickListener(view -> {
                    if (obfuscated.get()) {
                        holder.obfuscateImageButton.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_show));
                        holder.contentTextView.setText(Utils.obfuscate(detail.getContent()));
                    }
                    else {
                        holder.obfuscateImageButton.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_show_off));
                        holder.contentTextView.setText(detail.getContent());
                    }
                    obfuscated.set(!obfuscated.get());
                });
                holder.autofillImageView.setVisibility(detail.isPassword() || detail.isUsername() ? View.VISIBLE : View.GONE);
                if (detail.isPassword()) {
                    holder.autofillImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_password));
                }
                else if (detail.isUsername()) {
                    holder.autofillImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_email));
                }
            }
            else {
                holder.itemView.setVisibility(View.GONE);
                holder.itemView.setLayoutParams(new ViewGroup.LayoutParams(0, 0));
            }

            //Test whether item view displays password:
            if (holder instanceof DetailPasswordViewHolder) {
                DetailPasswordViewHolder passwordHolder = (DetailPasswordViewHolder)holder;
                int securityScore = QualityGateManager.getInstance().calculatePassedQualityGates(detail.getContent());
                int maxSecurityScore = QualityGateManager.getInstance().numberOfQualityGates();
                passwordHolder.scoreProgressBar.setMax(maxSecurityScore * 100);
                ValueAnimator animator = ValueAnimator.ofInt(0, securityScore * 100);
                animator.setDuration(context.getResources().getInteger(R.integer.default_anim_duration) * 5L);
                animator.addUpdateListener(anim -> passwordHolder.scoreProgressBar.setProgress((int)anim.getAnimatedValue()));
                animator.start();
                //passwordHolder.scoreProgressBar.setProgress(securityScore * 100);
                String securityScoreString = securityScore + " / " + maxSecurityScore;
                passwordHolder.scoreTextView.setText(securityScoreString);
            }
        }
        else if (viewHolder instanceof MoreButtonViewHolder) {
            MoreButtonViewHolder holder = (MoreButtonViewHolder)viewHolder;
            if (viewModel.getEntry().getInvisibleDetails().isEmpty()) {
                holder.moreButton.setVisibility(View.GONE);
                holder.itemView.setLayoutParams(new ViewGroup.LayoutParams(0, 0));
                return;
            }
            else {
                holder.moreButton.setVisibility(View.VISIBLE);
                holder.itemView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                holder.moreButton.setOnClickListener(view -> {
                    viewModel.setInvisibleDetailsExpanded(!viewModel.areInvisibleDetailsExpanded());
                    for (int i = getOffsetInvisibleDetails(); i < getOffsetInvisibleDetails() + viewModel.getEntry().getInvisibleDetails().size(); i++) {
                        notifyItemChanged(i);
                    }
                    notifyItemChanged(holder.getAdapterPosition());
                });
            }
            if (viewModel.areInvisibleDetailsExpanded()) {
                holder.moreButton.setText(context.getString(R.string.button_show_less));
            }
            else {
                holder.moreButton.setText(context.getString(R.string.button_show_more));
            }
        }
    }


    /**
     * Method creates a view holder for the specified view type.
     *
     * @param parent    Parent for the view of the view holder.
     * @param viewType  View type.
     * @return          Created view holder.
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        switch (viewType) {
            case TYPE_GENERAL:
                itemView = layoutInflater.inflate(R.layout.item_entry_general, parent, false);
                return new GeneralViewHolder(itemView);
            case TYPE_GENERIC_HEADLINE_BUTTON:
                itemView = layoutInflater.inflate(R.layout.item_generic_headline_button, parent, false);
                return new GenericHeadlineButtonViewHolder(itemView);
            case TYPE_GENERIC_EMPTY_PLACEHOLDER:
                itemView = layoutInflater.inflate(R.layout.item_generic_empty_placeholder, parent, false);
                return new GenericEmptyPlaceholderViewHolder(itemView);
            case TYPE_MORE_BUTTON:
                itemView = layoutInflater.inflate(R.layout.item_entry_more_button, parent, false);
                return new MoreButtonViewHolder(itemView);
            case TYPE_DETAIL_PASSWORD:
                itemView = layoutInflater.inflate(R.layout.item_entry_detail_password, parent, false);
                return new DetailPasswordViewHolder(itemView);
            case TYPE_DETAIL:
            default:
                itemView = layoutInflater.inflate(R.layout.item_entry_detail, parent, false);
                return new DetailViewHolder(itemView);
        }
    }


    /**
     * Method determines the view type for items at the passed position.
     *
     * @param position  Position to query
     * @return          View type at the queried position.
     */
    @Override
    public int getItemViewType(int position) {
        if (position == POSITION_GENERAL) {
            return TYPE_GENERAL;
        }
        else if (position == 1) {
            return TYPE_GENERIC_HEADLINE_BUTTON;
        }
        else if (position == POSITION_DETAILS_EMPTY_PLACEHOLDER) {
            return TYPE_GENERIC_EMPTY_PLACEHOLDER;
        }
        else if (position == getPositionMoreButton()) {
            return TYPE_MORE_BUTTON;
        }
        else {
            Detail detail = getDetailForAdapterPosition(position);
            if (detail != null && detail.getType() == DetailType.PASSWORD) {
                return TYPE_DETAIL_PASSWORD;
            }
            return TYPE_DETAIL;
        }
    }


    /**
     * Method returns the number of items displayed by the recycler view adapter.
     *
     * @return  Number of items for the recycler view adapter.
     */
    @Override
    public int getItemCount() {
        if (viewModel.getEntry() == null) {
            return 0;
        }
        //Display the details plus the following:
        // - General view
        // - Headline details
        // - Empty placeholder for the list of details
        // - More button
        return viewModel.getEntry().getDetails().size() + 4;
    }


    /**
     * Method determines whether the queried position supports swiping.
     *
     * @param position  Position to query.
     * @return          Whether the queried position supports swiping.
     */
    @Override
    public boolean supportsSwipe(int position) {
        if (viewModel.getEntry() != null) {
            int offsetInvisibleDetails = getOffsetInvisibleDetails();
            return (position >= OFFSET_DETAILS && position < offsetInvisibleDetails - 1) || position >= offsetInvisibleDetails;
        }
        return false;
    }

    /**
     * Method is called to move a detail between the specified positions.
     *
     * @param from  Position of the detail to move.
     * @param to    Position to which to move the detail.
     */
    @Override
    public void onRowMove(int from, int to) {
        if (viewModel.getEntry() == null) {
            return;
        }

        ArrayList<Detail> visibleDetails = viewModel.getEntry().getVisibleDetails();
        ArrayList<Detail> invisibleDetails = viewModel.getEntry().getInvisibleDetails();
        int offsetInvisibleDetails = getOffsetInvisibleDetails();

        if (from >= offsetInvisibleDetails && to >= offsetInvisibleDetails) {
            //Invisible details:
            if (from < to) {
                for (int i = from - offsetInvisibleDetails; i < to - offsetInvisibleDetails; i++) {
                    Collections.swap(invisibleDetails, i, i + 1);
                }
            }
            else {
                for (int i = from - offsetInvisibleDetails; i > to - offsetInvisibleDetails; i--) {
                    Collections.swap(invisibleDetails, i, i - 1);
                }
            }
        }
        else if (from >= OFFSET_DETAILS && from < offsetInvisibleDetails - 1 && to >= OFFSET_DETAILS && to < offsetInvisibleDetails - 1) {
            //Visible details:
            if (from < to) {
                for (int i = from - OFFSET_DETAILS; i < to - OFFSET_DETAILS; i++) {
                    Collections.swap(visibleDetails, i, i + 1);
                }
            }
            else {
                for (int i = from - OFFSET_DETAILS; i > to - OFFSET_DETAILS; i--) {
                    Collections.swap(visibleDetails, i, i - 1);
                }
            }
        }
        else {
            //Invalid positions passed:
            return;
        }

        viewModel.getEntry().getDetails().clear();
        viewModel.getEntry().getDetails().addAll(visibleDetails);
        viewModel.getEntry().getDetails().addAll(invisibleDetails);

        notifyItemMoved(from, to);
    }

    /**
     * Method determines the range in between a detail at the specified position can be moved.
     * The method returns {@code null} if the item at the specified position does not support
     * moving (e.g. if the item is not a detail).
     *
     * @param position  Position of the detail for which to determine the movable range.
     * @return          Move range for the detail at the specified position.
     */
    @Nullable
    @Override
    public EntryRecyclerViewMoveCallback.MoveRange getMoveRange(int position) {
        if (viewModel.getEntry() != null) {
            int offsetInvisibleDetails = getOffsetInvisibleDetails();
            if (position >= offsetInvisibleDetails) {
                return new EntryRecyclerViewMoveCallback.MoveRange(offsetInvisibleDetails, offsetInvisibleDetails + viewModel.getEntry().getInvisibleDetails().size() - 1);
            }
            else if (position >= OFFSET_DETAILS && position < offsetInvisibleDetails - 1) {
                return new EntryRecyclerViewMoveCallback.MoveRange(OFFSET_DETAILS, offsetInvisibleDetails - 1);
            }
        }
        return null;
    }

    /**
     * Method determines whether the item at the specified position can be moved.
     *
     * @param position  Position of the item for which to determine whether it can be moved.
     * @return          Whether the item at the specified position can be moved.
     */
    @Override
    public boolean supportsMove(int position) {
        if (viewModel.getEntry() != null) {
            int offsetInvisibleDetails = getOffsetInvisibleDetails();
            return (position >= OFFSET_DETAILS && position < offsetInvisibleDetails - 1) || position >= offsetInvisibleDetails;
        }
        return false;
    }



    /**
     * Method returns the position of the more button within the recycler view.
     *
     * @return  Position of the more button.
     */
    public int getPositionMoreButton() {
        if (viewModel.getEntry() == null) {
            return 0;
        }
        return viewModel.getEntry().getVisibleDetails().size() + 3;
    }

    /**
     * Method returns the offset with which the invisible details are displayed within the adapter.
     *
     * @return  Offset with which the invisible details are displayed.
     */
    public int getOffsetInvisibleDetails() {
        if (viewModel.getEntry() == null) {
            return 0;
        }
        return viewModel.getEntry().getVisibleDetails().size() + 4;
    }

    /**
     * Method returns the detail for the specified adapter position. If the adapter position does
     * not show a detail, {@code null} is returned.
     *
     * @param position  Adapter position for which to return the detail.
     * @return          Detail for the specified adapter position.
     */
    @Nullable
    public Detail getDetailForAdapterPosition(int position) {
        if (viewModel.getEntry() == null) {
            return null;
        }
        int offsetInvisibleDetails = getOffsetInvisibleDetails();
        if (position >= offsetInvisibleDetails) {
            //Invisible details:
            return viewModel.getEntry().getInvisibleDetails().get(position - offsetInvisibleDetails);
        }
        else if (position >= OFFSET_DETAILS && position < offsetInvisibleDetails - 1) {
            //Visible details:
            return viewModel.getEntry().getVisibleDetails().get(position - OFFSET_DETAILS);
        }
        else {
            return null;
        }
    }

}
