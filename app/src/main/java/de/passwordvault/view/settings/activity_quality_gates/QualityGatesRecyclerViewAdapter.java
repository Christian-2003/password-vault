package de.passwordvault.view.settings.activity_quality_gates;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import de.passwordvault.R;
import de.passwordvault.model.analysis.QualityGate;
import de.passwordvault.view.utils.recycler_view.OnRecyclerViewActionListener;
import de.passwordvault.view.utils.recycler_view.RecyclerViewAdapter;
import de.passwordvault.view.utils.recycler_view.RecyclerViewSwipeCallback;


/**
 * Class implements the recycler view adapter for the activity displaying all quality gates.
 *
 * @author  Christian-2003
 * @version 3.7.0
 */
public class QualityGatesRecyclerViewAdapter extends RecyclerViewAdapter<QualityGatesViewModel> implements RecyclerViewSwipeCallback.SwipeContract {

    /**
     * Class models the view holder for the item displaying a quality gate.
     */
    public static class QualityGatesItemViewHolder extends RecyclerView.ViewHolder {

        /**
         * Attribute stores the text view displaying the description of the quality gate.
         */
        public final TextView descriptionTextView;

        /**
         * Attribute stores the text view displaying the regex of the quality gate.
         */
        public final TextView regexTextView;

        /**
         * Attribute stores the checkbox through which to (de-)activate the quality gate.
         */
        public final CheckBox activeCheckBox;

        /**
         * Attribute stores the button through which to show more options for the quality gate.
         */
        public final ImageButton moreButton;


        /**
         * Constructor instantiates a new view holder for the passed view.
         *
         * @param itemView  Inflated view from which to create the view holder.
         */
        public QualityGatesItemViewHolder(View itemView) {
            super(itemView);
            descriptionTextView = itemView.findViewById(R.id.text_description);
            regexTextView = itemView.findViewById(R.id.text_regex);
            activeCheckBox = itemView.findViewById(R.id.checkbox);
            moreButton = itemView.findViewById(R.id.button_more);
        }

    }


    /**
     * Field stores the view type for the quality gates item.
     */
    public static final int TYPE_QUALITY_GATES_ITEM = 1;

    /**
     * Field stores the offset for the default quality gates within the adapter.
     */
    public static final int OFFSET_DEFAULT_QUALITY_GATES = 3;

    /**
     * Field stores the position of the empty placeholder displayed when no custom quality gates
     * are available.
     */
    public static final int POSITION_EMPTY_PLACEHOLDER = 2;


    /**
     * Attribute stores the listener invoked to add a new quality gate.
     */
    @Nullable
    private OnRecyclerViewActionListener addQualityGateListener;

    /**
     * Attribute stores the listener invoked to show more options for a quality gate.
     */
    @Nullable
    private OnRecyclerViewActionListener qualityGatesMoreListener;


    /**
     * Constructor instantiates a new recycler view adapter.
     *
     * @param context   Context for the adapter.
     * @param viewModel View model for the adapter.
     */
    public QualityGatesRecyclerViewAdapter(@NonNull Context context, @NonNull QualityGatesViewModel viewModel) {
        super(context, viewModel);
    }


    /**
     * Method changes the listener invoked when a new quality gate shall be added.
     *
     * @param addQualityGateListener    New listener.
     */
    public void setAddQualityGateListener(@Nullable OnRecyclerViewActionListener addQualityGateListener) {
        this.addQualityGateListener = addQualityGateListener;
    }

    /**
     * Method changes the listener invoked when more options for a quality gate shall be dispalyed.
     *
     * @param qualityGatesMoreListener  New listener.
     */
    public void setQualityGatesMoreListener(@Nullable OnRecyclerViewActionListener qualityGatesMoreListener) {
        this.qualityGatesMoreListener = qualityGatesMoreListener;
    }


    /**
     * Method is called whenever a new view holder needs to be created.
     *
     * @param parent    Parent for the item view of the created view holder.
     * @param viewType  The view type of the new View.
     * @return          Created view holder.
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        View view;
        switch (viewType) {
            case TYPE_GENERIC_INFO:
                view = layoutInflater.inflate(R.layout.item_generic_info, parent, false);
                holder = new GenericInfoViewHolder(view);
                break;
            case TYPE_GENERIC_HEADLINE:
                view = layoutInflater.inflate(R.layout.item_generic_headline, parent, false);
                holder = new GenericHeadlineViewHolder(view);
                break;
            case TYPE_GENERIC_HEADLINE_BUTTON:
                view = layoutInflater.inflate(R.layout.item_generic_headline_button, parent, false);
                holder = new GenericHeadlineButtonViewHolder(view);
                break;
            case TYPE_GENERIC_EMPTY_PLACEHOLDER:
                view = layoutInflater.inflate(R.layout.item_generic_empty_placeholder, parent, false);
                holder = new GenericEmptyPlaceholderViewHolder(view);
                break;
            default:
                view = layoutInflater.inflate(R.layout.item_quality_gates, parent, false);
                holder = new QualityGatesItemViewHolder(view);
                break;
        }
        return holder;
    }


    /**
     * Method is called whenever data should be bound to a view holder.
     * @param holder    View holder to update.
     * @param position  The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        try {
            if (position == 0) {
                GenericInfoViewHolder viewHolder = (GenericInfoViewHolder)holder;
                viewHolder.infoTextView.setText(R.string.quality_gates_info);
            }
            if (position == 1) {
                GenericHeadlineButtonViewHolder viewHolder = (GenericHeadlineButtonViewHolder)holder;
                viewHolder.headlineTextView.setText(R.string.quality_gates_custom);
                viewHolder.buttonImageView.setImageResource(R.drawable.ic_add);
                viewHolder.dividerView.setVisibility(View.GONE);
                viewHolder.itemView.setOnClickListener(view -> {
                    if (addQualityGateListener != null) {
                        addQualityGateListener.onAction(holder.getAdapterPosition());
                    }
                });
            }
            if (position == 2) {
                GenericEmptyPlaceholderViewHolder viewHolder = (GenericEmptyPlaceholderViewHolder)holder;
                if (viewModel.getCustomQualityGates().isEmpty()) {
                    viewHolder.headlineTextView.setText(R.string.quality_gates_custom_empty_headline);
                    viewHolder.supportTextView.setText(R.string.quality_gates_custom_empty_support);
                    viewHolder.imageView.setImageResource(R.drawable.el_quality_gates);
                    viewHolder.itemView.setVisibility(View.VISIBLE);
                    viewHolder.itemView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                }
                else {
                    viewHolder.itemView.setVisibility(View.VISIBLE);
                    viewHolder.itemView.setLayoutParams(new ViewGroup.LayoutParams(0, 0));
                }
            }
            if (position == OFFSET_DEFAULT_QUALITY_GATES + viewModel.getCustomQualityGates().size()) {
                GenericHeadlineViewHolder viewHolder = (GenericHeadlineViewHolder)holder;
                viewHolder.headlineTextView.setText(R.string.quality_gates_default);
            }
            else {
                QualityGatesItemViewHolder viewHolder = (QualityGatesItemViewHolder)holder;
                QualityGate qualityGate;
                if (position - OFFSET_DEFAULT_QUALITY_GATES < viewModel.getCustomQualityGates().size()) {
                    //Custom quality gate:
                    qualityGate = viewModel.getCustomQualityGates().get(position - OFFSET_DEFAULT_QUALITY_GATES);
                    viewHolder.activeCheckBox.setVisibility(View.GONE);
                    viewHolder.moreButton.setVisibility(View.VISIBLE);
                    viewHolder.moreButton.setOnClickListener(view -> {
                        if (qualityGatesMoreListener != null) {
                            qualityGatesMoreListener.onAction(holder.getAdapterPosition());
                        }
                    });
                }
                else {
                    //Default quality gate:
                    qualityGate = viewModel.getDefaultQualityGates().get(position - OFFSET_DEFAULT_QUALITY_GATES - viewModel.getCustomQualityGates().size() - 1);
                    viewHolder.activeCheckBox.setVisibility(View.VISIBLE);
                    viewHolder.activeCheckBox.setChecked(qualityGate.isEnabled());
                    viewHolder.activeCheckBox.setOnCheckedChangeListener((view, checked) -> qualityGate.setEnabled(checked));
                    viewHolder.moreButton.setVisibility(View.GONE);
                }
                viewHolder.descriptionTextView.setText(qualityGate.getDescription());
                viewHolder.regexTextView.setText(qualityGate.getRegex());
            }
        }
        catch (ClassCastException e) {
            Log.d(TAG, "Cannot bind view holder at position " + position + ": " + e.getMessage());
        }
    }


    /**
     * Method returns the view type for the item at the specified position.
     *
     * @param position  Position to query
     * @return          View type for the specified position.
     */
    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_GENERIC_INFO;
        }
        else if (position == 1) {
            return TYPE_GENERIC_HEADLINE_BUTTON;
        }
        else if (position == 2) {
            return TYPE_GENERIC_EMPTY_PLACEHOLDER;
        }
        else if (position == OFFSET_DEFAULT_QUALITY_GATES + viewModel.getCustomQualityGates().size()) {
            return TYPE_GENERIC_HEADLINE;
        }
        else {
            return TYPE_QUALITY_GATES_ITEM;
        }
    }


    /**
     * Method returns the number of items within the recycler view.
     *
     * @return  Number of items within the recycler view.
     */
    @Override
    public int getItemCount() {
        return viewModel.getDefaultQualityGates().size() + viewModel.getCustomQualityGates().size() + 4;
    }


    /**
     * Method is called to determine whether the view at the specified position supports swiping.
     *
     * @param position  Position to query.
     * @return          Whether the view at the specified position supports swiping.
     */
    public boolean supportsSwipe(int position) {
        return position >= OFFSET_DEFAULT_QUALITY_GATES && position < OFFSET_DEFAULT_QUALITY_GATES + viewModel.getCustomQualityGates().size();
    }

}
