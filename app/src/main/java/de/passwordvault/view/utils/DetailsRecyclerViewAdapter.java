package de.passwordvault.view.utils;

import android.animation.ValueAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.xml.validation.Validator;

import de.passwordvault.R;
import de.passwordvault.model.analysis.QualityGateManager;
import de.passwordvault.model.detail.Detail;
import de.passwordvault.model.detail.DetailType;


/**
 * Class implements an adapter for a recycler view displaying {@link Detail}-instances for an entry.
 *
 * @author  Christian-2003
 * @version 3.4.0
 */
public class DetailsRecyclerViewAdapter extends RecyclerView.Adapter<DetailsRecyclerViewAdapter.ViewHolder> implements DetailsItemMoveCallback.ItemTouchHelperContract {

    /**
     * Class implements a generic view holder for a detail.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        /**
         * Attribute stores the text view displaying the name of the detail.
         */
        public TextView name;

        /**
         * Attribute stores the text view displaying the content of the detail.
         */
        public TextView content;

        /**
         * Attribute stores the image button to (un-)obfuscate a detail's content if required.
         */
        public ImageButton obfuscateButton;

        /**
         * Attribute stores the inflated view that is being modeled by this view holder.
         */
        public View itemView;


        /**
         * Constructor instantiates a new view holder for the passed item view.
         *
         * @param itemView  View for which to create the view holder.
         */
        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.entry_detail_item_name);
            content = itemView.findViewById(R.id.entry_detail_item_content);
            obfuscateButton = itemView.findViewById(R.id.entry_detail_show_button);
            this.itemView = itemView;
        }

    }

    /**
     * Class implements a view holder for a password-detail.
     */
    public static class PasswordViewHolder extends ViewHolder {

        /**
         * Attribute stores the text view displaying the password security score.
         */
        public TextView securityScore;

        /**
         * Attribute stores the progress bar displaying the password security score.
         */
        public ProgressBar securityBar;


        /**
         * Constructor instantiates a new view holder for the specified item view.
         *
         * @param itemView  View for which to create a new view holder.
         */
        public PasswordViewHolder(View itemView) {
            super(itemView);
            securityScore = itemView.findViewById(R.id.entry_detail_password_security_rating);
            securityBar = itemView.findViewById(R.id.entry_detail_password_security);
        }

    }


    /**
     * Attribute stores the data that is being displayed by the recycler view.
     */
    private final ArrayList<Detail> data;


    /**
     * Constructor instantiates a new adapter for a recycler view to display the passed list of
     * details.
     *
     * @param data                  List of details to be displayed.
     * @throws NullPointerException The passed list of details is {@code null}.
     */
    public DetailsRecyclerViewAdapter(ArrayList<Detail> data) throws NullPointerException {
        if (data == null) {
            throw new NullPointerException();
        }
        this.data = data;
    }


    /**
     * Method creates a view holder for the specified view type.
     *
     * @param parent    The ViewGroup into which the new View will be added after it is bound to an
     *                  adapter position.
     * @param viewType  The view type of the new View.
     * @return          Generated view holder.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_detail_wrapper, parent, false);
        if (viewType == DetailType.PASSWORD.getPersistentId()) {
            View content = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_detail_password, parent, false);
            ((LinearLayout)itemView.findViewById(R.id.entry_detail_view_container)).addView(content);
            return new PasswordViewHolder((itemView));
        }
        return new ViewHolder(itemView);
    }

    /**
     * Method returns the view type for an item at the specified position. The view type is equivalent
     * to {@link DetailType#getPersistentId()} of the detail at the specified position.
     *
     * @param position  Position to query.
     * @return          View type of the item.
     */
    @Override
    public int getItemViewType(int position) {
        return data.get(position).getType().getPersistentId();
    }

    /**
     * Method binds the data of the detail at the specified position to the passed view holder.
     *
     * @param holder    The ViewHolder which should be updated to represent the contents of the item
     *                  at the given position in the data set.
     * @param position  The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Detail detail = data.get(position);
        holder.name.setText(detail.getName());
        if (detail.isObfuscated()) {
            holder.obfuscateButton.setVisibility(View.VISIBLE);
            holder.content.setText(Utils.obfuscate(detail.getContent()));
            AtomicBoolean obfuscated = new AtomicBoolean(true);
            holder.obfuscateButton.setOnClickListener(view -> {
                if (obfuscated.get()) {
                    obfuscated.set(false);
                    holder.content.setText(detail.getContent());
                }
                else {
                    obfuscated.set(true);
                    holder.content.setText(Utils.obfuscate(detail.getContent()));
                }
            });
        }
        else {
            holder.obfuscateButton.setVisibility(View.GONE);
            holder.content.setText(detail.getContent());
        }

        if (holder instanceof PasswordViewHolder) {
            PasswordViewHolder viewHolder = (PasswordViewHolder)holder;
            int securityScore = QualityGateManager.getInstance().calculatePassedQualityGates(detail.getContent());
            int maxSecurityScore = QualityGateManager.getInstance().numberOfQualityGates();
            viewHolder.securityBar.setMax(maxSecurityScore * 1000);
            ValueAnimator animator = ValueAnimator.ofInt(0, securityScore * 1000);
            animator.setDuration(viewHolder.securityBar.getContext().getResources().getInteger(R.integer.default_anim_duration) * 5L);
            animator.addUpdateListener(animation -> viewHolder.securityBar.setProgress((int) animation.getAnimatedValue()));
            animator.start();
            viewHolder.securityBar.setProgress(securityScore * 1000);
            String securityRating = securityScore + "/" + maxSecurityScore;
            viewHolder.securityScore.setText(securityRating);
            double securityScorePercentage = (double)securityScore / (double)maxSecurityScore;
            if (securityScorePercentage < 0.33) {
                viewHolder.securityScore.setTextColor(viewHolder.securityScore.getContext().getColor(R.color.red));
            }
            else if (securityScorePercentage > 0.67) {
                viewHolder.securityScore.setTextColor(viewHolder.securityScore.getContext().getColor(R.color.green));
            }
            else {
                viewHolder.securityScore.setTextColor(viewHolder.securityScore.getContext().getColor(R.color.yellow));
            }
        }
    }

    /**
     * Method returns the number of items being displayed by the recycler view.
     *
     * @return  Number of items being displayed.
     */
    @Override
    public int getItemCount() {
        return data.size();
    }


    /**
     * Method is called whenever an item is being moved between two positions within the recycler
     * view.
     *
     * @param fromPosition  Original position of the item.
     * @param toPosition    New position of the item.
     */
    @Override
    public void onRowMoved(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(data, i, i + 1);
            }
        }
        else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(data, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    /**
     * Method is called whenever an item within the recycler view is being selected.
     *
     * @param viewHolder    View holder of the selected item.
     */
    @Override
    public void onRowSelected(ViewHolder viewHolder) {

    }

    /**
     * Method is called whenever a previously selected item within the recycler view is unselected.
     *
     * @param viewHolder    View holder of the unselected item.
     */
    @Override
    public void onRowClear(ViewHolder viewHolder) {

    }

}
