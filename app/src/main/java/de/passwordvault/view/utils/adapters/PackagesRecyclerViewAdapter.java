package de.passwordvault.view.utils.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.imageview.ShapeableImageView;
import java.util.ArrayList;
import de.passwordvault.R;
import de.passwordvault.model.packages.Package;
import de.passwordvault.view.utils.OnRecyclerItemClickListener;


/**
 * Class implements an adapter for a recycler view which can display {@link Package}-instances.
 *
 * @author  Christian-2003
 * @version 3.5.0
 */
public class PackagesRecyclerViewAdapter extends RecyclerView.Adapter<PackagesRecyclerViewAdapter.ViewHolder> {

    /**
     * Class models a view holder for the recycler view.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        /**
         * Attribute stores the image view displaying the app logo.
         */
        public ShapeableImageView logo;

        /**
         * Attribute stores the text view displaying the app name.
         */
        public TextView name;

        /**
         * Attribute stores the item view for which this view holder exists.
         */
        public View itemView;


        /**
         * Constructor instantiates a new view holder for the passed view.
         *
         * @param itemView  Inflated view for which to create a new view holder.
         */
        public ViewHolder(View itemView) {
            super(itemView);
            logo = itemView.findViewById(R.id.list_item_package_logo);
            name = itemView.findViewById(R.id.list_item_package_name);
            this.itemView = itemView;
        }

    }


    /**
     * Attribute stores the list of packages that shall be displayed.
     */
    private final ArrayList<Package> data;

    /**
     * Attribute stores the click listener that shall be called when an item in the recycler view is
     * clicked.
     */
    private final OnRecyclerItemClickListener<Package> clickListener;


    /**
     * Constructor instantiates a new adapter for a recycler view which can display packages.
     *
     * @param data                  List of packages to be displayed.
     * @param clickListener         Click listener that shall be called when an item is clicked.
     * @throws NullPointerException The passed list of packages is {@code null}.
     */
    public PackagesRecyclerViewAdapter(ArrayList<Package> data, OnRecyclerItemClickListener<Package> clickListener) throws NullPointerException {
        if (data == null) {
            throw new NullPointerException();
        }
        this.data = data;
        this.clickListener = clickListener;
    }


    /**
     * Method creates a new view holder.
     *
     * @param parent    The ViewGroup into which the new View will be added after it is bound to an
     *                  adapter position.
     * @param viewType  The view type of the new View.
     * @return          Created view holder.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_package, parent, false);
        return new ViewHolder(itemView);
    }

    /**
     * Method binds the data of the item at the specified position to the specified view holder.
     *
     * @param holder    The ViewHolder which should be updated to represent the contents of the item
     *                  at the given position in the data set.
     * @param position  The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Package p = data.get(position);
        if (p.getLogo() != null) {
            holder.logo.setImageDrawable(p.getLogo());
        }
        holder.name.setText(p.getAppName());
        if (clickListener != null) {
            holder.itemView.setOnClickListener(view -> clickListener.onItemClick(p, position));
        }
    }

    /**
     * Method returns the number of items being displayed through the recycler view.
     *
     * @return  Number of displayed items.
     */
    @Override
    public int getItemCount() {
        return data.size();
    }

}
