package de.passwordvault.view.entries.activity_packages;

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


/**
 * Class implements the superclass for all recycler view adapters displaying packages.
 *
 * @author  Christian-2003
 * @version 3.7.0
 */
public abstract class PackagesRecyclerViewAdapter extends RecyclerViewAdapter<PackagesViewModel> {

    /**
     * Class models a view holder for the packages.
     */
    public static class PackageViewHolder extends RecyclerView.ViewHolder {

        /**
         * Attribute stores the image view displaying the package logo.
         */
        public final ImageView logoImageView;

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
    protected OnRecyclerViewActionListener itemClickListener;

    /**
     * Attribute stores the list of filtered packages.
     */
    @NonNull
    protected final ArrayList<Package> filteredPackages;


    /**
     * Constructor instantiates a new recycler view adapter.
     *
     * @param context   Context for the adapter.
     * @param viewModel View model for the adapter.
     */
    public PackagesRecyclerViewAdapter(@NonNull Context context, @NonNull PackagesViewModel viewModel) {
        super(context, viewModel);
        filteredPackages = new ArrayList<>();
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

}
