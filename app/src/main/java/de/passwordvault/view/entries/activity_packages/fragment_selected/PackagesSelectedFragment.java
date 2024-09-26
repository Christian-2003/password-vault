package de.passwordvault.view.entries.activity_packages.fragment_selected;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import de.passwordvault.R;
import de.passwordvault.model.packages.Package;
import de.passwordvault.view.entries.activity_packages.PackagesActivity;
import de.passwordvault.view.utils.recycler_view.OnRecyclerItemClickListener;
import de.passwordvault.view.entries.activity_packages.PackagesRecyclerViewAdapter;
import de.passwordvault.view.utils.components.PasswordVaultBaseFragment;
import de.passwordvault.view.entries.activity_packages.PackagesViewModel;


/**
 * Class implements a fragment within {@link PackagesActivity} that
 * displays the packages that were selected by the user.
 *
 * @author  Christian-2003
 * @version 3.5.2
 */
public class PackagesSelectedFragment extends PasswordVaultBaseFragment implements OnRecyclerItemClickListener<Package> {

    /**
     * Attribute stores the view model of the fragment and it's hosting activity.
     */
    private PackagesViewModel viewModel;

    /**
     * Attribute stores the inflated view of the fragment.
     */
    private View view;

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
        view = inflater.inflate(R.layout.fragment_packages_selected, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(PackagesViewModel.class);

        setupList();

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
            viewModel.getSelectedPackages().remove(item);
            adapter.notifyDataSetChanged();
            if (viewModel.getSelectedPackages().isEmpty()) {
                //Removed last package:
                setupList();
            }
            ((PackagesActivity)requireActivity()).notifyPackageUnselected();
        }
        catch (IndexOutOfBoundsException e) {
            //This should not be triggered. Better be safe than sorry...
        }
    }


    /**
     * Method informs the adapter of the fragment, that a package was added to the selected packages.
     */
    public void notifyPackageAdded() {
        if (viewModel.getSelectedPackages().size() == 1) {
            //Added first package:
            setupList();
        }
        adapter.notifyItemInserted(adapter.getItemCount());
    }


    /**
     * Method sets up the recycler view displaying the packages.
     */
    private void setupList() {
        view.findViewById(R.id.packages_selected_none).setVisibility(viewModel.getSelectedPackages().size() == 0 ? View.VISIBLE : View.GONE);

        RecyclerView recyclerView = view.findViewById(R.id.packages_selected_recycler_view);
        recyclerView.setVisibility(viewModel.getSelectedPackages().size() == 0 ? View.GONE : View.VISIBLE);
        if (adapter == null) {
            adapter = new PackagesRecyclerViewAdapter(viewModel.getSelectedPackages(), null, this);
            recyclerView.setAdapter(adapter);
        }
    }

}
