package de.passwordvault.view.settings.security.quality_gates;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import de.passwordvault.R;
import de.passwordvault.model.analysis.QualityGate;
import de.passwordvault.view.dialogs.ConfirmDeleteDialog;
import de.passwordvault.view.settings.security.quality_gate.QualityGateActivity;
import de.passwordvault.view.utils.DialogCallbackListener;
import de.passwordvault.view.utils.RecyclerItemSwipeCallback;
import de.passwordvault.view.utils.components.PasswordVaultBaseActivity;


/**
 * Class implements an activity which can add (or edit) quality gates.
 *
 * @author  Christian-2003
 * @version 3.6.0
 */
public class QualityGatesActivity extends PasswordVaultBaseActivity implements DialogCallbackListener {

    /**
     * Attribute stores the view model for the activity.
     */
    private QualityGatesViewModel viewModel;

    /**
     * Attribute stores the activity result launcher to edit / add a quality gate.
     */
    private final ActivityResultLauncher<Intent> qualityGatesResultLauncher;

    /**
     * Attribute stores the adapter for the custom quality gates.
     */
    private QualityGatesRecyclerViewAdapter customQualityGatesAdapter;


    /**
     * Constructor instantiates a new activity.
     */
    public QualityGatesActivity() {
        qualityGatesResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == QualityGateActivity.RESULT_EDITED) {
                if (result.getData() == null) {
                    return;
                }
                Bundle args = result.getData().getExtras();
                if (args == null || !args.containsKey(QualityGateActivity.KEY_INDEX) || !args.containsKey(QualityGateActivity.KEY_QUALITY_GATE)) {
                    return;
                }
                int position = args.getInt(QualityGateActivity.KEY_INDEX);
                QualityGate qualityGate = (QualityGate)args.getSerializable(QualityGateActivity.KEY_QUALITY_GATE);
                if (position == -1) {
                    //Added quality gate:
                    viewModel.getCustomQualityGates().add(qualityGate);
                    customQualityGatesAdapter.notifyItemInserted(viewModel.getCustomQualityGates().size() - 1);
                }
                else {
                    viewModel.getCustomQualityGates().set(position, qualityGate);
                    customQualityGatesAdapter.notifyItemChanged(position);
                }
            }
        });
    }


    /**
     * Method is called on positive dialog callback.
     *
     * @param fragment  Dialog which called the method.
     */
    @Override
    public void onPositiveCallback(DialogFragment fragment) {
        if (fragment instanceof ConfirmDeleteDialog) {
            String tag = fragment.getTag();
            if (tag != null) {
                int position;
                try {
                    position = Integer.parseInt(tag);
                    viewModel.getCustomQualityGates().remove(position);
                }
                catch (NumberFormatException | IndexOutOfBoundsException e) {
                    return;
                }
                customQualityGatesAdapter.notifyItemRemoved(position);
            }
        }
    }

    /**
     * Method is called on negative dialog callback.
     * @param fragment  Dialog which called the method.
     */
    @Override
    public void onNegativeCallback(DialogFragment fragment) {

    }


    /**
     * Method is called whenever the activity is created / recreated.
     *
     * @param savedInstanceState    Previously saved state of the instance.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(QualityGatesViewModel.class);
        setContentView(R.layout.activity_quality_gates);
        viewModel.loadQualityGatesIfRequired();

        //Back button:
        findViewById(R.id.button_back).setOnClickListener(view -> finish());

        //Button to add quality gates:
        findViewById(R.id.button_add).setOnClickListener(view -> qualityGatesResultLauncher.launch(new Intent(this, QualityGateActivity.class)));

        //Custom quality gates:
        RecyclerItemSwipeCallback.SwipeAction<QualityGate> leftSwipeAction = RecyclerItemSwipeCallback.makeLeftSwipeAction(this::editQualityGate, this::deleteQualityGate);
        RecyclerItemSwipeCallback.SwipeAction<QualityGate> rightSwipeAction = RecyclerItemSwipeCallback.makeRightSwipeAction(this::editQualityGate, this::deleteQualityGate);
        RecyclerView customRecyclerView = findViewById(R.id.recycler_view_custom);
        customQualityGatesAdapter = new QualityGatesRecyclerViewAdapter(this, viewModel.getCustomQualityGates());
        RecyclerItemSwipeCallback<QualityGate> callback = new RecyclerItemSwipeCallback<>(customQualityGatesAdapter, leftSwipeAction, rightSwipeAction);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(customRecyclerView);
        customRecyclerView.setAdapter(customQualityGatesAdapter);

        //Default quality gates:
        RecyclerView defaultRecyclerView = findViewById(R.id.recycler_view_default);
        QualityGatesRecyclerViewAdapter defaultQualityGatesAdapter = new QualityGatesRecyclerViewAdapter(this, viewModel.getDefaultQualityGates());
        defaultRecyclerView.setAdapter(defaultQualityGatesAdapter);
    }


    /**
     * Method is called whenever the activity is destroyed.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.saveQualityGates();
    }


    /**
     * Method is called whenever a quality gate shall be edited.
     *
     * @param qualityGate   Quality gate to edit.
     * @param position      Position of the quality gate within the data.
     */
    private void editQualityGate(QualityGate qualityGate, int position) {
        Intent intent = new Intent(this, QualityGateActivity.class);
        intent.putExtra(QualityGateActivity.KEY_QUALITY_GATE, qualityGate);
        intent.putExtra(QualityGateActivity.KEY_INDEX, position);
        qualityGatesResultLauncher.launch(intent);
    }


    /**
     * Method is called whenever a quality gate shall be deleted, which shows a dialog to confirm
     * deletion. On Positive callback, the quality gate is deleted.
     *
     * @param qualityGate   Quality gate to delete.
     * @param position      Position of the quality gate within the data.
     */
    private void deleteQualityGate(QualityGate qualityGate, int position) {
        ConfirmDeleteDialog dialog = new ConfirmDeleteDialog();
        Bundle args = new Bundle();
        args.putString(ConfirmDeleteDialog.KEY_OBJECT, qualityGate.getDescription());
        args.putSerializable(ConfirmDeleteDialog.KEY_CALLBACK_LISTENER, this);
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), "" + position);
    }

}
