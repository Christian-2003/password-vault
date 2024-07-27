package de.passwordvault.view.utils.recycler_view;

import android.content.Context;
import android.view.LayoutInflater;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


/**
 * Class implements a superclass for all recycler view adapters. The class handles basic tasks like
 * Context management and the creation of a layout inflater that can be used to inflate child views.
 *
 * @author  Christian-2003
 * @version 3.7.0
 */
public abstract class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    /**
     * Attribute stores the context for the adapter.
     */
    @NonNull
    protected final Context context;

    /**
     * Attribute stores a layout inflater that can be used to inflate item views.
     */
    @NonNull
    protected final LayoutInflater layoutInflater;


    /**
     * Constructor instantiates a new recycler view adapter.
     *
     * @param context   Context for the recycler view adapter.
     */
    public RecyclerViewAdapter(@NonNull Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

}
