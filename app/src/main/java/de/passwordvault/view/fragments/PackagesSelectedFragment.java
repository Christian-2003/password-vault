package de.passwordvault.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import de.passwordvault.R;
import de.passwordvault.model.packages.Package;
import de.passwordvault.view.utils.OnRecyclerItemClickListener;
import de.passwordvault.view.utils.adapters.PackagesRecyclerViewAdapter;
import de.passwordvault.viewmodel.activities.PackagesViewModel;


/**
 * Class implements a fragment within {@link de.passwordvault.view.activities.PackagesActivity} that
 * displays the packages that were selected by the user.
 *
 * @author  Christian-2003
 * @version 3.5.0
 */
public class PackagesSelectedFragment extends Fragment implements OnRecyclerItemClickListener<Package> {

    /**
     * Attribute stores the view model of the fragment and it's hosting activity.
     */
    private PackagesViewModel viewModel;

    /**
     * Attribute stores the adapter for the recycler view displaying the selected packages.
     */
    private PackagesRecyclerViewAdapter adapter;


    /**
     * Method is called whenever the fragment view is created.
     *
     * @param inflater              Layout inflater to use when inflating views.
     * @param container             Parent of the view.
     * @param savedInstanceState    Previously saved state of the instance.
     * @return                      Inflated view.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_packages_selected, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(PackagesViewModel.class);

        view.findViewById(R.id.packages_selected_none).setVisibility(viewModel.getPackages().size() == 0 ? View.VISIBLE : View.GONE);

        adapter = new PackagesRecyclerViewAdapter(viewModel.getPackages(), null, this);
        RecyclerView recyclerView = view.findViewById(R.id.packages_selected_recycler_view);
        recyclerView.setVisibility(viewModel.getPackages().size() == 0 ? View.GONE : View.VISIBLE);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(requireActivity(), DividerItemDecoration.VERTICAL));

        return view;
    }


    /**
     * Method is called when the item which is passed as argument is clicked by the user.
     *
     * @param item      Clicked item.
     * @param position  Index of the clicked item.
     */
    @Override
    public void onItemClick(Package item, int position) {
        try {
            viewModel.getPackages().remove(item);
            adapter.notifyDataSetChanged();
        }
        catch (IndexOutOfBoundsException e) {
            //This should not be triggered. Better be safe than sorry...
        }
    }


    /**
     * Method informs the adapter of the fragment, that a package was added to the selected packages.
     */
    public void notifyPackageAdded() {
        adapter.notifyItemInserted(adapter.getItemCount());
    }

}
