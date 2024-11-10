package de.passwordvault.view.entries.activity_packages.fragment_list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
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
 * Class implements the fragment of the {@link PackagesActivity} that
 * displays a list of all packages that are installed on the user's Android device.
 *
 * @author  Christian-2003
 * @version 3.5.2
 */
public class PackagesListFragment extends PasswordVaultFragment<PackagesViewModel> {

    /**
     * Attribute stores the view of the fragment.
     */
    private View view;

    /**
     * Attribute stores the adapter to display installed packages.
     */
    @Nullable
    private PackagesListRecyclerViewAdapter adapter;


    public PackagesListFragment() {
        super(PackagesViewModel.class, R.layout.fragment_packages_list);
    }


    /**
     * Method is called whenever the fragment view is created.
     *
     * @param inflater              Inflater for inflating the fragment views.
     * @param container             Parent of the fragment.
     * @param savedInstanceState    Previously saved state of the instance.
     * @return                      Inflated view.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = super.onCreateView(inflater, container, savedInstanceState);

        viewModel.loadPackages(this::onDataLoaded);

        return view;
    }


    /**
     * Method notifies the adapter that the data has been changed.
     */
    public void notifyPackageUnselected() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }


    public void filter(String searchQuery) {
        if (adapter != null) {
            adapter.filter(searchQuery);
        }
    }

    public void resetFilter() {
        if (adapter != null) {
            adapter.resetFilter();
        }
    }


    private void onDataLoaded() {
        requireActivity().runOnUiThread(() -> {
            ProgressBar progressBar = view.findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.GONE);

            if (adapter == null) {
                adapter = new PackagesListRecyclerViewAdapter(requireActivity(), viewModel);
                adapter.setItemClickListener(this::onPackageClicked);
                RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
                recyclerView.setAdapter(adapter);
            }
            else {
                adapter.notifyDataSetChanged();
            }
        });
    }


    private void onPackageClicked(int position) {
        if (adapter == null) {
            return;
        }
        Package clickedPackage = adapter.getPackageForAdapterPosition(position);
        if (!viewModel.getSelectedPackages().containsPackageName(clickedPackage.getPackageName())) {
            viewModel.getSelectedPackages().add(clickedPackage);
            FragmentActivity activity = requireActivity();
            if(activity instanceof PackagesActivity) {
                ((PackagesActivity)activity).notifyPackageAdded();
            }
            adapter.notifyItemChanged(position);
        }
    }

}
