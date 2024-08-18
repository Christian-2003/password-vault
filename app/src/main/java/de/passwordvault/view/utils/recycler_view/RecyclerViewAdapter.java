package de.passwordvault.view.utils.recycler_view;

import android.content.Context;
import android.view.LayoutInflater;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;


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
