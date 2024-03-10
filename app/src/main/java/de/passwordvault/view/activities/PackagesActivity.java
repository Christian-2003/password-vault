package de.passwordvault.view.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.imageview.ShapeableImageView;
import de.passwordvault.R;
import de.passwordvault.model.packages.Package;
import de.passwordvault.model.packages.PackagesManager;
import de.passwordvault.view.utils.OnRecyclerItemClickListener;
import de.passwordvault.view.utils.adapters.PackagesRecyclerViewAdapter;
import de.passwordvault.viewmodel.activities.PackagesViewModel;


/**
 * Class implements the activity which shows a list of installed apps (packages) to the user, from
 * which the user can select one.
 *
 * @author  Christian-2003
 * @version 3.5.0
 */
public class PackagesActivity extends AppCompatActivity implements OnRecyclerItemClickListener<Package> {

    /**
     * Field stores the key that needs to be used when passing the selected package as argument.
     */
    public static final String KEY_PACKAGE = "package";


    /**
     * Attribute stores the view model of the activity.
     */
    private PackagesViewModel viewModel;


    /**
     * Method is called whenever a new view is created.
     *
     * @param savedInstanceState    Previously saved state of the instance.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(PackagesViewModel.class);
        setContentView(R.layout.activity_packages);

        Bundle args = getIntent().getExtras();
        if (!viewModel.processArguments(args)) {
            finish();
            return;
        }

        findViewById(R.id.packages_back_button).setOnClickListener(view -> finish());
        Button removePackageButton = findViewById(R.id.packages_remove_button);
        removePackageButton.setVisibility(viewModel.getSelectedPackageName() == null ? View.GONE : View.VISIBLE);
        removePackageButton.setOnClickListener(view -> removePackage());

        LinearLayout selectedItem = findViewById(R.id.packages_selected_item);
        selectedItem.setVisibility(viewModel.getSelectedPackageName() == null ? View.GONE : View.VISIBLE);
        findViewById(R.id.packages_selected_none).setVisibility(viewModel.getSelectedPackageName() == null ? View.VISIBLE : View.GONE);
        if (viewModel.getSelectedPackageName() != null) {
            Drawable logo = PackagesManager.getInstance().getPackageLogo(viewModel.getSelectedPackageName());
            if (logo != null) {
                ((ShapeableImageView)selectedItem.findViewById(R.id.list_item_package_logo)).setImageDrawable(logo);
            }
            ((TextView)selectedItem.findViewById(R.id.list_item_package_name)).setText(PackagesManager.getInstance().getPackage(viewModel.getSelectedPackageName()).getAppName());
        }

        PackagesRecyclerViewAdapter adapter = new PackagesRecyclerViewAdapter(PackagesManager.getInstance().getPackages(), this);
        RecyclerView recyclerView = findViewById(R.id.packages_recycler_view);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }


    /**
     * Method is called whenever the activity is closed.
     */
    @Override
    public void finish() {
        Intent intent = new Intent(PackagesActivity.this, AddEntryActivity.class);
        intent.putExtra(PackagesActivity.KEY_PACKAGE, viewModel.getSelectedPackageName());
        setResult(RESULT_OK, intent);
        super.finish();
    }


    /**
     * Method is called when the item which is passed as argument is clicked by the user.
     *
     * @param item      Clicked item.
     * @param position  Index of the clicked item.
     */
    @Override
    public void onItemClick(Package item, int position) {
        viewModel.setSelectedPackageName(item.getPackageName());
        finish();
    }


    /**
     * Method removes the package and closes the activity.
     */
    private void removePackage() {
        viewModel.setSelectedPackageName(null);
        finish();
    }

}
