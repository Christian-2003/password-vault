package de.passwordvault.view.utils.recycler_view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;
import de.passwordvault.R;


/**
 * Class implements a superclass for all recycler view adapters. The class handles basic tasks like
 * Context management and the creation of a layout inflater that can be used to inflate child views.
 *
 * @param <V>   Type of the view model of the activity / fragment hosting the recycler view.
 * @author      Christian-2003
 * @version     3.7.0
 */
public abstract class RecyclerViewAdapter<V extends ViewModel> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    /**
     * Class models the view holder for the generic info item.
     */
    public static class GenericInfoViewHolder extends RecyclerView.ViewHolder {

        /**
         * Attribute stores the text view displaying the info text.
         */
        public final TextView infoTextView;


        /**
         * Constructor creates a new view holder.
         *
         * @param itemView  Inflated view from which to create the view holder.
         */
        private GenericInfoViewHolder(@NonNull View itemView) {
            super(itemView);
            infoTextView = itemView.findViewById(R.id.text);
        }

    }

    /**
     * Class models the view holder for the generic headline item.
     */
    public static class GenericHeadlineViewHolder extends RecyclerView.ViewHolder {

        /**
         * Attribute stores the text view displaying the headline.
         */
        public final TextView headlineTextView;


        /**
         * Constructor instantiates a new view holder.
         *
         * @param itemView  Inflated view from which to create the view holder.
         */
        public GenericHeadlineViewHolder(@NonNull View itemView) {
            super(itemView);
            headlineTextView = (TextView)itemView;
        }

    }

    /**
     * Class models the view holder for the generic headline button item.
     */
    public static class GenericHeadlineButtonViewHolder extends RecyclerView.ViewHolder {

        /**
         * Attribute stores the text view displaying the headline.
         */
        public final TextView headlineTextView;

        /**
         * Attribute stores the image view displaying the button icon.
         */
        public final ImageView buttonImageView;


        /**
         * Constructor instantiates a new view holder.
         *
         * @param itemView  Inflated view from which to create the view holder.
         */
        public GenericHeadlineButtonViewHolder(@NonNull View itemView) {
            super(itemView);
            headlineTextView = itemView.findViewById(R.id.text);
            buttonImageView = itemView.findViewById(R.id.image);
        }

    }


    /**
     * Attribute stores the context for the adapter.
     */
    @NonNull
    protected final Context context;

    /**
     * Attribute stores the view model of the activity / fragment hosting the recycler view of this
     * adapter.
     */
    @NonNull
    protected final V viewModel;

    /**
     * Attribute stores a layout inflater that can be used to inflate item views.
     */
    @NonNull
    protected final LayoutInflater layoutInflater;


    /**
     * Constructor instantiates a new recycler view adapter.
     *
     * @param context   Context for the recycler view adapter.
     * @param viewModel View model of the activity / fragment hosting the recycler view.
     */
    public RecyclerViewAdapter(@NonNull Context context, @NonNull V viewModel) {
        this.context = context;
        this.viewModel = viewModel;
        layoutInflater = LayoutInflater.from(context);
    }

}
