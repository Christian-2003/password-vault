package de.passwordvault.view.settings.activity_licenses;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import de.passwordvault.R;
import de.passwordvault.view.settings.dialog_license.LicenseDialog;
import de.passwordvault.view.utils.recycler_view.RecyclerViewAdapter;


/**
 * Class implements a recycler view adapter for the {@link LicensesActivity}.
 *
 * @author  Christian-2003
 * @version 3.7.0
 */
public class LicensesRecyclerViewAdapter extends RecyclerViewAdapter<LicensesViewModel> {

    /**
     * Class implements the view holder for the recycler view.
     */
    public static class LicenseViewHolder extends RecyclerView.ViewHolder {

        /**
         * Attribute stores the text view used to show the software name.
         */
        public final TextView softwareTextView;


        /**
         * Constructor instantiates a new view holder.
         *
         * @param itemView  Item view for which to create the view holder.
         */
        public LicenseViewHolder(View itemView) {
            super(itemView);
            softwareTextView = itemView.findViewById(R.id.text_software);
        }

    }


    /**
     * Constructor instantiates a new recycler view adapter.
     */
    public LicensesRecyclerViewAdapter(@NonNull Context context, @NonNull LicensesViewModel viewModel) {
        super(context, viewModel);
        Log.d("LRVA", "Create adapter");
        Log.d("LRVA", "Items: " + getItemCount());
    }


    /**
     * Method creates a new view holder.
     *
     * @param parent    Recycler view.
     * @param viewType  View type for the new item view.
     * @return          Created view holder.
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.item_licenses_used, parent, false);
        return new LicenseViewHolder(itemView);
    }


    /**
     * Method binds data to a view holder.
     *
     * @param holder    View holder to update.
     * @param position  Position of the item within the recycler view.
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof LicenseViewHolder) {
            LicenseViewHolder viewHolder = (LicenseViewHolder)holder;
            LicensesViewModel.UsedSoftware usedSoftware = viewModel.getUsedSoftware().get(position);
            viewHolder.softwareTextView.setText(usedSoftware.getName());
            viewHolder.itemView.setOnClickListener(view -> {
                Bundle args = new Bundle();
                args.putString(LicenseDialog.ARG_TITLE, usedSoftware.getLicenseName());
                String resourceText = viewModel.getLicenseText(usedSoftware);
                args.putString(LicenseDialog.ARG_LICENSE, resourceText == null ? "" : resourceText);
                LicenseDialog dialog = new LicenseDialog();
                dialog.setArguments(args);
                dialog.show(((AppCompatActivity)context).getSupportFragmentManager(), null);
            });
        }
    }


    /**
     * Method returns the number of items displayed by the recycler view.
     *
     * @return  Number of items displayed.
     */
    @Override
    public int getItemCount() {
        return viewModel.getUsedSoftware().size();
    }

}
