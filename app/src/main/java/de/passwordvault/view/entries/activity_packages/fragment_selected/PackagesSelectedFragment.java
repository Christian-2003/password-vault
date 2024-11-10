package de.passwordvault.view.entries.activity_packages.fragment_selected;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import de.passwordvault.R;
import de.passwordvault.model.packages.Package;
import de.passwordvault.view.entries.activity_packages.PackagesActivity;
import de.passwordvault.view.utils.components.PasswordVaultFragment;
import de.passwordvault.view.entries.activity_packages.PackagesViewModel;


/**
 * Class implements a fragment within {@link PackagesActivity} that
 * displays the packages that were selected by the user.
 *
 * @author  Christian-2003
 * @version 3.5.2
 */
public class PackagesSelectedFragment extends PasswordVaultFragment<PackagesViewModel> {

    /**
     * Attribute stores the adapter for the recycler view displaying the selected packages.
     */
    private PackagesSelectedRecyclerViewAdapter adapter;


    public PackagesSelectedFragment() {
        super(PackagesViewModel.class, R.layout.fragment_packages_selected);
    }


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
        super.onCreateView(inflater, container, savedInstanceState);

        if (adapter == null) {
            adapter = new PackagesSelectedRecyclerViewAdapter(requireActivity(), viewModel);
            adapter.setItemClickListener(this::onDeleteClicked);
        }
        RecyclerView recyclerView = fragmentView.findViewById(R.id.recycler_view);
        recyclerView.setAdapter(adapter);

        return fragmentView;
    }





    /**
     * Method is called whenever the delete button for a selected package is clicked.
     *
     * @param position  Index of the item clicked to delete.
     */
    public void onDeleteClicked(int position) {
        try {
            Package packageToDelete = adapter.getPackageForAdapterPosition(position);
            viewModel.getSelectedPackages().remove(packageToDelete);
            adapter.notifyPackageRemoved(position);
            FragmentActivity activity = requireActivity();
            if (activity instanceof PackagesActivity) {
                ((PackagesActivity)activity).notifyPackageUnselected();
            }
            if (viewModel.getSelectedPackages().size() == 0) {
                adapter.notifyItemChanged(PackagesSelectedRecyclerViewAdapter.POSITION_EMPTY_PLACEHOLDER);
            }
        }
        catch (IndexOutOfBoundsException e) {
            //This should not be triggered. Better be safe than sorry...
        }
    }


    /**
     * Method informs the adapter of the fragment, that a package was added to the selected packages.
     */
    public void notifyPackageAdded() {
        adapter.notifyPackageAdded();
        if (viewModel.getSelectedPackages().size() == 1) {
            adapter.notifyItemChanged(PackagesSelectedRecyclerViewAdapter.POSITION_EMPTY_PLACEHOLDER);
        }
    }

}
