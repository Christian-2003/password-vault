package de.passwordvault.view.entries.activity_entry;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;
import de.passwordvault.R;
import de.passwordvault.model.detail.Detail;
import de.passwordvault.model.entry.EntryExtended;
import de.passwordvault.view.utils.Utils;
import de.passwordvault.view.utils.recycler_view.RecyclerViewAdapter;


/**
 * Class implements the recycler view adapter for the {@link EntryActivity}.
 *
 * @author  Christian-2003
 * @version 3.7.0
 */
public class EntryRecyclerViewAdapter extends RecyclerViewAdapter {

    /**
     * Class models the view holder for the view displaying general information.
     */
    public static class GeneralViewHolder extends RecyclerView.ViewHolder {

        /**
         * Attribute stores the text view displaying the name of the entry.
         */
        public TextView nameTextView;

        /**
         * Attribute stores the text view displaying the description of the entry.
         */
        public TextView descriptionTextView;

        /**
         * Attribute stores the text view displaying the date at which the entry was edited.
         */
        public TextView dateTextView;

        /**
         * Attribute stores the button used to edit the entry.
         */
        public Button editButton;

        /**
         * Attribute stores the button to delete the entry.
         */
        public Button deleteButton;

        /**
         * Attribute stores the button to add a new detail.
         */
        public ImageButton addDetailButton;

        /**
         * Attribute stores the image view displaying the app image.
         */
        public ImageView appImageView;


        /**
         * Constructor instantiates a new view holder.
         *
         * @param itemView  Item view from which to create the view holder.
         */
        public GeneralViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.text_name);
            descriptionTextView = itemView.findViewById(R.id.text_description);
            dateTextView = itemView.findViewById(R.id.text_date);
            editButton = itemView.findViewById(R.id.button_edit);
            deleteButton = itemView.findViewById(R.id.button_delete);
            addDetailButton = itemView.findViewById(R.id.button_add_detail);
            appImageView = itemView.findViewById(R.id.image_app);
        }

    }


    /**
     * Class models the view holder for the view displaying information about a detail.
     */
    public static class DetailViewHolder extends RecyclerView.ViewHolder {

        /**
         * Attribute stores the text view displaying the name of the detail.
         */
        public TextView nameTextView;

        /**
         * Attribute stores the text view displaying the content of the detail.
         */
        public TextView contentTextView;

        /**
         * Attribute stores the text view displaying the date on which the detail was edited.
         */
        public TextView dateTextView;

        /**
         * Attribute stores the image button used to copy the contents of the detail.
         */
        public ImageButton copyImageButton;

        /**
         * Attribute stores the image view displaying an image corresponding to the detail type.
         */
        public ImageView detailImageView;


        /**
         * Constructor instantiates a new view holder.
         *
         * @param itemView  Item view from which to create the view holder.
         */
        public DetailViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.text_name);
            contentTextView = itemView.findViewById(R.id.text_content);
            dateTextView = itemView.findViewById(R.id.text_date);
            copyImageButton = itemView.findViewById(R.id.button_copy);
            detailImageView = itemView.findViewById(R.id.image_detail);
        }

    }


    /**
     * Field stores the view type for the general item view.
     */
    private static final int TYPE_GENERAL = 0;

    /**
     * Field stores the view type for the detail item view.
     */
    private static final int TYPE_DETAIL = 1;


    /**
     * Attribute stores the entry to display with the adapter.
     */
    @NonNull
    private final EntryExtended entry;


    /**
     * Constructor instantiates a new recycler view adapter.
     *
     * @param context   Context for the recycler view.
     * @param entry     Data for the adapter.
     */
    public EntryRecyclerViewAdapter(@NonNull Context context, @NonNull EntryExtended entry) {
        super(context);
        this.entry = entry;
    }


    /**
     * Method binds data to the passed view holder.
     *
     * @param viewHolder    The view holder to which to bind data.
     * @param position      Position of the view holder within the dataset.
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof GeneralViewHolder) {
            GeneralViewHolder holder = (GeneralViewHolder)viewHolder;
            holder.nameTextView.setText(entry.getName());
            holder.descriptionTextView.setText(entry.getDescription());
            holder.dateTextView.setText(Utils.formatDate(entry.getChanged(), context.getString(R.string.date_format)));
            //TODO: Continue binding...
        }
        else if (viewHolder instanceof DetailViewHolder) {
            DetailViewHolder holder = (DetailViewHolder)viewHolder;
            Detail detail = entry.getDetails().get(position - 1);
            holder.nameTextView.setText(detail.getName());
            holder.contentTextView.setText(detail.getContent());
            holder.dateTextView.setText(Utils.formatDate(detail.getChanged(), context.getString(R.string.date_format)));
            holder.copyImageButton.setOnClickListener(view -> Utils.copyToClipboard(detail.getContent()));
            holder.detailImageView.setImageDrawable(AppCompatResources.getDrawable(context, detail.getType().getDrawable()));
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
            case TYPE_DETAIL:
                itemView = layoutInflater.inflate(R.layout.item_entry_detail, parent, false);
                return new DetailViewHolder(itemView);
            default:
                return new DetailViewHolder(new View(context));
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
        if (position == 0) {
            return TYPE_GENERAL;
        }
        else {
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
        return entry.getVisibleDetails().size() + 1;
    }

}
