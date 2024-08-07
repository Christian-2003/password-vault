package de.passwordvault.view.entries.activity_packages.fragment_list;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
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
    private PackagesRecyclerViewAdapter adapter;

    /**
     * Attribute stores the search bar.
     */
    private SearchBarView searchBar;


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

        searchBar = view.findViewById(R.id.search_bar);
        searchBar.setVisibility(viewModel.isSearchBarVisible() ? View.VISIBLE : View.GONE);
        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (adapter != null) {
                    adapter.getFilter().filter(editable);
                }
            }
        });

        loadData();

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
            adapter.notifyItemChanged(position);
        }
    }


    /**
     * Method is called by the host activity when the search button is clicked.
     */
    public void searchButtonClicked() {
        viewModel.setSearchBarVisible(!viewModel.isSearchBarVisible());
        if (viewModel.isSearchBarVisible()) {
            searchBar.setVisibility(View.VISIBLE);
            searchBar.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_left));
        }
        else {
            searchBar.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_left));
            searchBar.postDelayed(() -> searchBar.setVisibility(View.GONE), getResources().getInteger(R.integer.default_anim_duration));
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


    private void loadData() {
        View progressBarContainer = view.findViewById(R.id.container_progress_bar);
        View contentContainer = view.findViewById(R.id.container_content);
        progressBarContainer.setVisibility(View.VISIBLE);
        contentContainer.setVisibility(View.GONE);

        Thread thread = new Thread(() -> {
            PackagesManager.getInstance().getSortedPackages();
            try {
                dataLoadedCallback();
            }
            catch (Exception e) {
                //Cannot update UI (maybe, the activity no longer exists)...
                Log.w("PackagesListFragment", "Thread finished when fragment nonexistent");
            }
        });
        thread.start();
    }


    private void dataLoadedCallback() {
        requireActivity().runOnUiThread(() -> {
            View progressBarContainer = view.findViewById(R.id.container_progress_bar);
            View contentContainer = view.findViewById(R.id.container_content);

            if (adapter == null) {
                adapter = new PackagesRecyclerViewAdapter(PackagesManager.getInstance().getSortedPackages(), viewModel.getPackages(), this, null);
                RecyclerView recyclerView = view.findViewById(R.id.packages_list_recycler_view);
                recyclerView.setAdapter(adapter);
            }
            else {
                adapter.notifyDataSetChanged();
            }

            progressBarContainer.setVisibility(View.GONE);
            contentContainer.setVisibility(View.VISIBLE);
        });
    }

}
