package de.passwordvault.view.entries.activity_entry;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.imageview.ShapeableImageView;
import java.util.ArrayList;
import de.passwordvault.R;
import de.passwordvault.model.packages.Package;


/**
 * Class implements an adapter for a recycler view which displays a list of package logos.
 *
 * @author      Christian-2003
 * @version     3.5.0
 * @deprecated  This no longer used and will be removed once the new UI is fully implemented.
 */
@Deprecated
public class PackagesLogoRecyclerViewAdapter extends RecyclerView.Adapter<PackagesLogoRecyclerViewAdapter.ViewHolder> {

    /**
     * Class models a view holder for the recycler view.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        /**
         * Attribute stores the shapeable image view displaying the logo of the package.
         */
        public ShapeableImageView logo;


        /**
         * Constructor instantiates a new view holder for the passed (inflated) view.
         *
         * @param itemView  View for which to create the view holder.
         */
        public ViewHolder(View itemView) {
            super(itemView);
            logo = itemView.findViewById(R.id.list_item_package_logo_image_view);
        }

    }


    /**
     * Attribute stores the packages that shall be displayed by the recycler view.
     */
    private final ArrayList<Package> data;


    /**
     * Constructor instantiates a new adapter for the recycler view which displays the logos for the
     * specified list of packages.
     *
     * @param data                  List of packages whose logos shall be displayed by the recycler
     *                              view.
     * @throws NullPointerException The passed list is {@code null}.
     */
    public PackagesLogoRecyclerViewAdapter(ArrayList<Package> data) throws NullPointerException {
        if (data == null) {
            throw new NullPointerException();
        }
        this.data = data;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_package_logo, parent, false);
        return new ViewHolder(view);
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
        Drawable logo = data.get(position).getLogo();
        if (logo != null) {
            holder.logo.setImageDrawable(logo);
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
