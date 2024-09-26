package de.passwordvault.view.entries.activity_packages.fragment_list;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.imageview.ShapeableImageView;
import java.util.ArrayList;
import de.passwordvault.R;
import de.passwordvault.model.packages.Package;
import de.passwordvault.view.entries.activity_packages.PackagesViewModel;
import de.passwordvault.view.utils.recycler_view.OnRecyclerViewActionListener;
import de.passwordvault.view.utils.recycler_view.RecyclerViewAdapter;


public class PackagesListRecyclerViewAdapter extends RecyclerViewAdapter<PackagesViewModel> {

    /**
     * Class models a view holder for the packages.
     */
    public static class PackageViewHolder extends RecyclerView.ViewHolder {

        /**
         * Attribute stores the image view displaying the package logo.
         */
        public final ShapeableImageView logoImageView;

        /**
         * Attribute stores the text view displaying the package name.
         */
        public final TextView nameTextView;

        /**
         * Attribute stores the button to remove the package.
         */
        public final ImageButton removeButton;

        /**
         * Attribute stores the image view indicating whether the package is selected or not.
         */
        public final ImageView selectedImageView;


        /**
         * Constructor instantiates a new view holder for the passed view.
         *
         * @param itemView  Inflated view for which to create the view holder.
         */
        public PackageViewHolder(View itemView) {
            super(itemView);
            logoImageView = itemView.findViewById(R.id.image_logo);
            nameTextView = itemView.findViewById(R.id.text_name);
            removeButton = itemView.findViewById(R.id.button_remove);
            selectedImageView = itemView.findViewById(R.id.image_selected);
        }

    }


    /**
     * Field stores the view type for packages.
     */
    public static final int TYPE_PACKAGE = 1;

    /**
     * Field stores the offset with which the packages are displayed.
     */
    public static final int OFFSET_PACKAGES = 1;

    /**
     * Field stores the position of the empty placeholder displayed when no data is displayed.
     */
    public static final int POSITION_EMPTY_PLACEHOLDER = 0;


    /**
     * Attribute stores the action listener to invoke when a package is clicked.
     */
    @Nullable
    private OnRecyclerViewActionListener itemClickListener;

    /**
     * Attribute stores the list of filtered packages.
     */
    @NonNull
    private final ArrayList<Package> filteredPackages;


    /**
     * Constructor instantiates a new recycler view adapter.
     *
     * @param context   Context for the adapter.
     * @param viewModel View model for the adapter.
     */
    public PackagesListRecyclerViewAdapter(@NonNull Context context, @NonNull PackagesViewModel viewModel) {
        super(context, viewModel);
        if (viewModel.getAllPackages() != null) {
            filteredPackages = new ArrayList<>(viewModel.getAllPackages());
        }
        else {
            filteredPackages = new ArrayList<>();
        }
    }


    /**
     * Method changes the click listener invoked when a package is clicked.
     *
     * @param itemClickListener New item click listener.
     */
    public void setItemClickListener(@Nullable OnRecyclerViewActionListener itemClickListener) {
        this.itemClickListener = itemClickListener;
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
            if (viewModel.getAllPackages() == null || viewModel.getAllPackages().size() == 0) {
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
            Package p = viewModel.getAllPackages().get(position - OFFSET_PACKAGES);
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
     * Method creates a new view holder for the specified view type.
     *
     * @param parent    Parent for the view of the view holder.
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
            view = layoutInflater.inflate(R.layout.item_packages, parent, false);
            return new PackageViewHolder(view);
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
        if (viewModel.getAllPackages() != null) {
            count += viewModel.getAllPackages().size();
        }
        return count;
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
