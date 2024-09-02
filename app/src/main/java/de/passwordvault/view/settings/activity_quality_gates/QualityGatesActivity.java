package de.passwordvault.view.settings.activity_quality_gates;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;

import de.passwordvault.R;
import de.passwordvault.model.analysis.QualityGate;
import de.passwordvault.model.security.login.SecurityQuestion;
import de.passwordvault.view.general.dialog_delete.DeleteDialog;
import de.passwordvault.view.general.dialog_more.Item;
import de.passwordvault.view.general.dialog_more.ItemButton;
import de.passwordvault.view.general.dialog_more.MoreDialog;
import de.passwordvault.view.settings.activity_quality_gate.QualityGateActivity;
import de.passwordvault.view.settings.activity_recovery.RecoveryRecyclerViewAdapter;
import de.passwordvault.view.settings.dialog_security_question.SecurityQuestionDialog;
import de.passwordvault.view.utils.components.PasswordVaultActivity;
import de.passwordvault.view.utils.components.PasswordVaultBottomSheetDialog;
import de.passwordvault.view.utils.recycler_view.RecyclerViewSwipeCallback;


/**
 * Class implements an activity which can add (or edit) quality gates.
 *
 * @author  Christian-2003
 * @version 3.6.0
 */
public class QualityGatesActivity extends PasswordVaultActivity<QualityGatesViewModel> {

    /**
     * Attribute stores the adapter of the activity.
     */
    private QualityGatesRecyclerViewAdapter adapter;

    /**
     * Attribute stores the result launcher used to get a result from the activity to edit a quality
     * gate.
     */
    private final ActivityResultLauncher<Intent> qualityGateResultLauncher;


    /**
     * Constructor instantiates a new activity.
     */
    public QualityGatesActivity() {
        super(QualityGatesViewModel.class, R.layout.activity_quality_gates);

        qualityGateResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
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
                    if (viewModel.getCustomQualityGates().isEmpty()) {
                        adapter.notifyItemChanged(QualityGatesRecyclerViewAdapter.POSITION_EMPTY_PLACEHOLDER);
                    }
                    viewModel.getCustomQualityGates().add(qualityGate);
                    adapter.notifyItemInserted(QualityGatesRecyclerViewAdapter.OFFSET_DEFAULT_QUALITY_GATES + viewModel.getDefaultQualityGates().size() - 1);
                }
                else {
                    viewModel.getCustomQualityGates().set(position, qualityGate);
                    adapter.notifyItemChanged(QualityGatesRecyclerViewAdapter.OFFSET_DEFAULT_QUALITY_GATES + position);
                }
            }
        });
    }


    /**
     * Method is called when the activity closes.
     */
    @Override
    public void finish() {
        super.finish();
        viewModel.saveQualityGates();
    }


    /**
     * Method is called whenever the activity is created.
     * @param savedInstanceState    Previously saved state of the instance.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel.loadQualityGatesIfRequired();

        //Back button:
        findViewById(R.id.button_back).setOnClickListener(view -> finish());

        //Recycler view:
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        adapter = new QualityGatesRecyclerViewAdapter(this, viewModel);
        adapter.setAddQualityGateListener(this::onAddQualityGate);
        adapter.setQualityGatesMoreListener(this::onShowMoreOptions);
        recyclerView.setAdapter(adapter);
        RecyclerViewSwipeCallback.SwipeAction leftSwipe = RecyclerViewSwipeCallback.makeLeftSwipeAction(this::onEditQualityGate, this::onDeleteQualityGate);
        RecyclerViewSwipeCallback.SwipeAction rightSwipe = RecyclerViewSwipeCallback.makeRightSwipeAction(this::onEditQualityGate, this::onDeleteQualityGate);
        RecyclerViewSwipeCallback callback = new RecyclerViewSwipeCallback(adapter, leftSwipe, rightSwipe);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);
    }


    private void onEditQualityGate(int position) {
        Intent intent = new Intent(this, QualityGateActivity.class);
        int index = position - QualityGatesRecyclerViewAdapter.OFFSET_DEFAULT_QUALITY_GATES;
        intent.putExtra(QualityGateActivity.KEY_QUALITY_GATE, viewModel.getCustomQualityGates().get(index));
        intent.putExtra(QualityGateActivity.KEY_INDEX, index);
        qualityGateResultLauncher.launch(intent);
    }

    private void onDeleteQualityGate(int position) {
        int index = position - QualityGatesRecyclerViewAdapter.OFFSET_DEFAULT_QUALITY_GATES;
        QualityGate qualityGate = viewModel.getCustomQualityGates().get(index);
        DeleteDialog dialog = new DeleteDialog();
        Bundle args = new Bundle();
        String message = getString(R.string.delete_dialog_message);
        message = message.replace("{arg}", qualityGate.getDescription());
        args.putString(DeleteDialog.ARG_MESSAGE, message);
        PasswordVaultBottomSheetDialog.Callback callback = (d, resultCode) -> {
            if (resultCode == PasswordVaultBottomSheetDialog.Callback.RESULT_SUCCESS) {
                viewModel.getCustomQualityGates().remove(index);
                adapter.notifyItemRemoved(index + QualityGatesRecyclerViewAdapter.OFFSET_DEFAULT_QUALITY_GATES);
                if (viewModel.getCustomQualityGates().isEmpty()) {
                    adapter.notifyItemChanged(QualityGatesRecyclerViewAdapter.POSITION_EMPTY_PLACEHOLDER);
                }
            }
        };
        args.putSerializable(SecurityQuestionDialog.ARG_CALLBACK, callback);
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), null);
    }

    private void onAddQualityGate(int position) {
        Intent intent = new Intent(this, QualityGateActivity.class);
        qualityGateResultLauncher.launch(intent);
    }

    private void onShowMoreOptions(int position) {
        int index = position - QualityGatesRecyclerViewAdapter.OFFSET_DEFAULT_QUALITY_GATES;
        QualityGate qualityGate = viewModel.getCustomQualityGates().get(index);
        MoreDialog dialog = new MoreDialog();
        Bundle args = new Bundle();
        args.putString(MoreDialog.ARG_TITLE, qualityGate.getDescription());
        args.putInt(MoreDialog.ARG_ICON, R.drawable.ic_shield);
        ArrayList<Item> items = new ArrayList<>();
        items.add(new ItemButton(getString(R.string.button_edit), R.drawable.ic_edit, view -> onEditQualityGate(position)));
        items.add(new ItemButton(getString(R.string.button_delete), R.drawable.ic_delete, view -> onDeleteQualityGate(position)));
        args.putSerializable(MoreDialog.ARG_ITEMS, items);
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), null);
    }

}
