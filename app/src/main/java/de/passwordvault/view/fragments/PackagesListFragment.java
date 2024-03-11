package de.passwordvault.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import de.passwordvault.R;
import de.passwordvault.model.packages.Package;
import de.passwordvault.model.packages.PackagesManager;
import de.passwordvault.view.activities.PackagesActivity;
import de.passwordvault.view.utils.OnRecyclerItemClickListener;
import de.passwordvault.view.utils.adapters.PackagesFragmentStateAdapter;
import de.passwordvault.view.utils.adapters.PackagesRecyclerViewAdapter;
import de.passwordvault.viewmodel.activities.PackagesViewModel;


/**
 * Class implements the fragment of the {@link de.passwordvault.view.activities.PackagesActivity} that
 * displays a list of all packages that are installed on the user's Android device.
 *
 * @author  Christian-2003
 * @version 3.5.0
 */
public class PackagesListFragment extends Fragment implements OnRecyclerItemClickListener<Package> {

    /**
     * Attribute stores the view model of the fragment and it's host activity.
     */
    private PackagesViewModel viewModel;


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
        View view = inflater.inflate(R.layout.fragment_packages_list, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(PackagesViewModel.class);

        view.findViewById(R.id.packages_selected_none).setVisibility(PackagesManager.getInstance().getPackages().size() == 0 ? View.VISIBLE : View.GONE);

        PackagesRecyclerViewAdapter adapter = new PackagesRecyclerViewAdapter(PackagesManager.getInstance().getPackages(), this, null);
        RecyclerView recyclerView = view.findViewById(R.id.packages_list_recycler_view);
        recyclerView.setVisibility(PackagesManager.getInstance().getPackages().size() == 0 ? View.GONE : View.VISIBLE);
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
        if (!viewModel.getPackages().containsPackageName(item.getPackageName())) {
            viewModel.getPackages().add(item);
            FragmentActivity activity = requireActivity();
            if (activity instanceof PackagesActivity) {
                ((PackagesActivity)activity).notifyPackageAdded();
            }
        }
    }

}
