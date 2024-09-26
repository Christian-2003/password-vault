package de.passwordvault.view.entries.activity_packages.fragment_list;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import de.passwordvault.R;
import de.passwordvault.model.packages.Package;
import de.passwordvault.model.packages.PackagesManager;
import de.passwordvault.view.entries.activity_packages.PackagesActivity;
import de.passwordvault.view.utils.recycler_view.OnRecyclerItemClickListener;
import de.passwordvault.view.entries.activity_packages.PackagesRecyclerViewAdapter;
import de.passwordvault.view.utils.components.PasswordVaultBaseFragment;
import de.passwordvault.view.utils.components.SearchBarView;
import de.passwordvault.view.entries.activity_packages.PackagesViewModel;


/**
 * Class implements the fragment of the {@link PackagesActivity} that
 * displays a list of all packages that are installed on the user's Android device.
 *
 * @author  Christian-2003
 * @version 3.5.2
 */
public class PackagesListFragment extends PasswordVaultBaseFragment implements OnRecyclerItemClickListener<Package> {

    /**
     * Attribute stores the view model of the fragment and it's host activity.
     */
    private PackagesViewModel viewModel;

    /**
     * Attribute stores the view of the fragment.
     */
    private View view;

    /**
     * Attribute stores the adapter to display installed packages.
     */
    @Nullable
    private PackagesListRecyclerViewAdapter adapter;


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
        view = inflater.inflate(R.layout.fragment_packages_list, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(PackagesViewModel.class);

        viewModel.loadPackages(this::onDataLoaded);

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
        if (!viewModel.getSelectedPackages().containsPackageName(item.getPackageName())) {
            viewModel.getSelectedPackages().add(item);
            FragmentActivity activity = requireActivity();
            if (activity instanceof PackagesActivity) {
                ((PackagesActivity)activity).notifyPackageAdded();
            }
            adapter.notifyItemChanged(position);
        }
    }


    /**
     * Method notifies the adapter that the data has been changed.
     */
    public void notifyPackageUnselected() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
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
