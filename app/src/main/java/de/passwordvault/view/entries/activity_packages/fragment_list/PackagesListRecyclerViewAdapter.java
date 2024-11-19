package de.passwordvault.view.entries.activity_packages.fragment_list;

import android.content.Context;
import android.util.Log;
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
 * Class implements the recycler view adapter for the {@link PackagesListFragment}.
 *
 * @author  Christian-2003
 * @version 3.7.0
 */
public class PackagesListRecyclerViewAdapter extends PackagesRecyclerViewAdapter {

    /**
     * Constructor instantiates a new recycler view adapter.
     *
     * @param context   Context for the adapter.
     * @param viewModel View model for the adapter.
     */
    public PackagesListRecyclerViewAdapter(@NonNull Context context, @NonNull PackagesViewModel viewModel) {
        super(context, viewModel);
        if (viewModel.getAllPackages() != null) {
            filteredPackages.addAll(viewModel.getAllPackages());
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
            viewHolder.headlineTextView.setText(context.getString(R.string.packages_list_empty_headline));
            viewHolder.supportTextView.setText(context.getString(R.string.packages_list_empty_support));
            viewHolder.imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.el_packages));
            if (filteredPackages.size() == 0) {
                viewHolder.itemView.setVisibility(View.VISIBLE);
                viewHolder.itemView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
            else {
                viewHolder.itemView.setVisibility(View.GONE);
                viewHolder.itemView.setLayoutParams(new ViewGroup.LayoutParams(0, 0));
            }
        }
        else {
            if (viewModel.getAllPackages() == null) {
                return;
            }
            PackageViewHolder viewHolder = (PackageViewHolder)holder;
            Package p = filteredPackages.get(position - OFFSET_PACKAGES);
            viewHolder.nameTextView.setText(p.getAppName());
            viewHolder.logoImageView.setImageDrawable(p.getLogo());
            viewHolder.removeButton.setVisibility(View.GONE);
            viewHolder.selectedImageView.setVisibility(viewModel.getSelectedPackages().contains(p) ? View.VISIBLE : View.GONE);
            viewHolder.itemView.setOnClickListener(view -> {
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
        return filteredPackages.size() + 1;
    }


    /**
     * Method resets the filter for the adapter.
     */
    public void resetFilter() {
        filteredPackages.clear();
        if (viewModel.getAllPackages() != null) {
            filteredPackages.addAll(viewModel.getAllPackages());
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
        if (viewModel.getAllPackages() != null) {
            for (Package p : viewModel.getAllPackages()) {
                if (p.getPackageName().toLowerCase().contains(query) || p.getAppName().toLowerCase().contains(query)) {
                    filteredPackages.add(p);
                }
            }
        }
        notifyDataSetChanged();
        Log.d("Adapter", "Filtered " + filteredPackages.size() + " packages for " + query);
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
