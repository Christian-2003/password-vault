package de.passwordvault.view.entries.activity_packages.fragment_selected;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import de.passwordvault.R;
import de.passwordvault.model.packages.Package;
import de.passwordvault.view.entries.activity_packages.PackagesViewModel;
import de.passwordvault.view.entries.activity_packages.PackagesRecyclerViewAdapter;


/**
 * Class implements the recycler view adapter for the {@link PackagesSelectedFragment}.
 *
 * @author  Christian-2003
 * @version 3.7.1
 */
public class PackagesSelectedRecyclerViewAdapter extends PackagesRecyclerViewAdapter {

    /**
     * Constructor instantiates a new recycler view adapter.
     *
     * @param context   Context for the adapter.
     * @param viewModel View model from which to source the data.
     */
    public PackagesSelectedRecyclerViewAdapter(@NonNull Context context, @NonNull PackagesViewModel viewModel) {
        super(context, viewModel);
        if (viewModel.getSelectedPackages() != null) {
            filteredPackages.addAll(viewModel.getSelectedPackages());
        }
    }


    /**
     * Method is called whenever a view holder shall be updated.
     * @param holder    The view holder to update.
     * @param position  The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (position == POSITION_EMPTY_PLACEHOLDER) {
            GenericEmptyPlaceholderViewHolder viewHolder = (GenericEmptyPlaceholderViewHolder)holder;
            viewHolder.headlineTextView.setText(context.getString(R.string.packages_selected_empty_headline));
            viewHolder.supportTextView.setText(context.getString(R.string.packages_selected_empty_support));
            viewHolder.imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.el_packages));
            if (viewModel.getSelectedPackages() == null || viewModel.getSelectedPackages().isEmpty()) {
                viewHolder.itemView.setVisibility(View.VISIBLE);
                viewHolder.itemView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
            else {
                viewHolder.itemView.setVisibility(View.GONE);
                viewHolder.itemView.setLayoutParams(new ViewGroup.LayoutParams(0, 0));
            }
        }
        else {
            if (viewModel.getSelectedPackages() == null) {
                return;
            }
            PackageViewHolder viewHolder = (PackageViewHolder)holder;
            Package p = viewModel.getSelectedPackages().get(position - OFFSET_PACKAGES);
            if (p.getAppName() == null || p.getAppName().isEmpty()) {
                viewHolder.nameTextView.setText(p.getPackageName());
            }
            else {
                viewHolder.nameTextView.setText(p.getAppName());
            }
            viewHolder.logoImageView.setImageDrawable(p.getLogo());
            viewHolder.selectedImageView.setVisibility(View.GONE);
            viewHolder.itemView.setClickable(false);
            viewHolder.itemView.setFocusable(false);
            viewHolder.removeButton.setOnClickListener((view) -> {
                if (itemClickListener != null) {
                    itemClickListener.onAction(holder.getAdapterPosition());
                }
            });
        }
    }


    /**
     * Method returns the item view type for the specified position.
     *
     * @param position  Position to query.
     * @return          View type for the queried position.
     */
    @Override
    public int getItemViewType(int position) {
        return position == POSITION_EMPTY_PLACEHOLDER ? TYPE_GENERIC_EMPTY_PLACEHOLDER : TYPE_PACKAGE;
    }


    /**
     * Method returns the number of items displayed by the recycler view.
     *
     * @return  Number of displayed items.
     */
    @Override
    public int getItemCount() {
        int count = 1;
        if (viewModel.getSelectedPackages() != null) {
            count += viewModel.getSelectedPackages().size();
        }
        return count;
    }


    /**
     * Method notifies the adapter that a package at the specified position has been removed.
     *
     * @param position  Adapter position of the package removed.
     */
    public void notifyPackageRemoved(int position) {
        filteredPackages.remove(position - OFFSET_PACKAGES);
        notifyItemRemoved(position);
    }

    /**
     * Method notifies the adapter that a package has been added (to the end of the adapter's dataset).
     */
    public void notifyPackageAdded() {
        if (!viewModel.getSelectedPackages().isEmpty()) {
            filteredPackages.add(viewModel.getSelectedPackages().get(viewModel.getSelectedPackages().size() - 1));
            notifyItemInserted(getItemCount());
        }
    }


    /**
     * Method resets the filter for the adapter.
     */
    public void resetFilter() {
        filteredPackages.clear();
        if (viewModel.getSelectedPackages() != null) {
            filteredPackages.addAll(viewModel.getSelectedPackages());
        }
        notifyDataSetChanged();
    }

    /**
     * Method filters the adapter data based on the provided query. Pass {@code null} to reset the
     * filter.
     *
     * @param query Query based on which to filter the data.
     */
    public void filter(@Nullable String query) {
        if (query == null || query.isEmpty()) {
            resetFilter();
            return;
        }
        filteredPackages.clear();
        query = query.toLowerCase();
        if (viewModel.getSelectedPackages() != null) {
            for (Package p : viewModel.getSelectedPackages()) {
                if (p.getPackageName().toLowerCase().contains(query) || p.getAppName().toLowerCase().contains(query)) {
                    filteredPackages.add(p);
                }
            }
        }
        notifyDataSetChanged();
    }

    /**
     * Method returns the package at the specified adapter position. This works even if a filter is
     * applied.
     *
     * @param position  Position to query.
     * @return          Package at the specified position.
     */
    public Package getPackageForAdapterPosition(int position) {
        return filteredPackages.get(position - OFFSET_PACKAGES);
    }

}
