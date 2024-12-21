package de.passwordvault.view.settings.activity_quality_gates;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import java.util.ArrayList;
import de.passwordvault.R;
import de.passwordvault.model.analysis.QualityGate;
import de.passwordvault.view.general.dialog_delete.DeleteDialog;
import de.passwordvault.view.general.dialog_more.Item;
import de.passwordvault.view.general.dialog_more.ItemButton;
import de.passwordvault.view.general.dialog_more.ItemCheckbox;
import de.passwordvault.view.general.dialog_more.ItemDivider;
import de.passwordvault.view.general.dialog_more.MoreDialog;
import de.passwordvault.view.general.dialog_more.MoreDialogCallback;
import de.passwordvault.view.settings.activity_quality_gate.QualityGateActivity;
import de.passwordvault.view.settings.activity_import_quality_gate.SettingsImportQualityGateActivity;
import de.passwordvault.view.utils.components.PasswordVaultActivity;
import de.passwordvault.view.utils.components.PasswordVaultBottomSheetDialog;
import de.passwordvault.view.utils.recycler_view.RecyclerViewSwipeCallback;


/**
 * Class implements an activity which can add (or edit) quality gates.
 *
 * @author  Christian-2003
 * @version 3.7.1
 */
public class QualityGatesActivity extends PasswordVaultActivity<QualityGatesViewModel> implements PasswordVaultBottomSheetDialog.Callback, MoreDialogCallback {

    /**
     * Field stores the tag for the more dialog item to edit a quality gate.
     */
    private static final String TAG_EDIT_QUALITY_GATE = "edit";

    /**
     * Field stores the tag for the more dialog item to delete a quality gate.
     */
    private static final String TAG_DELETE_QUALITY_GATE = "delete";

    /**
     * Field stores the tag for the more dialog item to enable / disable a quality gate.
     */
    private static final String TAG_ENABLE_QUALITY_GATE = "enable";

    /**
     * Field stores the tag for the more dialog item to share a quality gate.
     */
    private static final String TAG_SHARE_QUALITY_GATE = "share";

    /**
     * Field stores the tag for the more dialog item to add a new quality gate.
     */
    private static final String TAG_NEW_QUALITY_GATE = "new";

    /**
     * Field stores the tag for the more dialog item to import a new quality gate.
     */
    private static final String TAG_IMPORT_QUALITY_GATE = "import";


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
     * Attribute stores the result launcher used to get a result from the activity to import a quality
     * gate.
     */
    private final ActivityResultLauncher<Intent> importQualityGateResultLauncher;


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

        importQualityGateResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && adapter != null) {
                viewModel.loadQualityGatesIfRequired(true);
                adapter.notifyItemInserted(QualityGatesRecyclerViewAdapter.OFFSET_DEFAULT_QUALITY_GATES + viewModel.getCustomQualityGates().size());
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
     * Method is called whenever a dialog is dismissed with a callback.
     *
     * @param dialog        Dialog that was closed.
     * @param resultCode    Result code is either {@link #RESULT_SUCCESS} or {@link #RESULT_CANCEL}
     *                      and indicates how the dialog is closed.
     */
    public void onCallback(PasswordVaultBottomSheetDialog<? extends ViewModel> dialog, int resultCode) {
        if (resultCode == PasswordVaultBottomSheetDialog.Callback.RESULT_SUCCESS) {
            try {
                if (dialog.getTag() == null) {
                    return;
                }
                int index = Integer.parseInt(dialog.getTag());
                viewModel.getCustomQualityGates().remove(index);
                adapter.notifyItemRemoved(index + QualityGatesRecyclerViewAdapter.OFFSET_DEFAULT_QUALITY_GATES);
                if (viewModel.getCustomQualityGates().isEmpty()) {
                    adapter.notifyItemChanged(QualityGatesRecyclerViewAdapter.POSITION_EMPTY_PLACEHOLDER);
                }
            }
            catch (Exception e) {
                //Cannot parse tag containing index.
            }
        }
    }


    /**
     * Method is called whenever the {@link MoreDialog} is dismissed with a callback.
     *
     * @param dialog    Dialog in which the action was invoked.
     * @param tag       Tag from the {@link Item} whose action is invoked.
     * @param position  Position of the {@link Item} within the dialog.
     */
    @Override
    public void onDialogItemClicked(MoreDialog dialog, String tag, int position) {
        String[] tagParts = tag.split(":"); //Tag in the form "<TAG>:<POSITION>"
        if (tagParts.length != 2) {
            return;
        }
        String tagValue = tagParts[0];
        int adapterPosition;
        try {
            adapterPosition = Integer.parseInt(tagParts[1]);
        }
        catch (NumberFormatException e) {
            return;
        }
        switch (tagValue) {
            case TAG_EDIT_QUALITY_GATE:
                onEditQualityGate(adapterPosition);
                break;
            case TAG_DELETE_QUALITY_GATE:
                onDeleteQualityGate(adapterPosition);
                break;
            case TAG_ENABLE_QUALITY_GATE: {
                int index = adapterPosition - QualityGatesRecyclerViewAdapter.OFFSET_DEFAULT_QUALITY_GATES;
                if (index < viewModel.getCustomQualityGates().size()) {
                    viewModel.getCustomQualityGates().get(index).setEnabled(!viewModel.getCustomQualityGates().get(index).isEnabled());
                }
                break;
            }
            case TAG_SHARE_QUALITY_GATE: {
                int index = adapterPosition - QualityGatesRecyclerViewAdapter.OFFSET_DEFAULT_QUALITY_GATES;
                if (index < viewModel.getCustomQualityGates().size()) {
                    String url = viewModel.createShareUrl(viewModel.getCustomQualityGates().get(index));
                    if (url != null) {
                        shareDataWithSheet(url, "text/uri-list");
                    }
                }
            }
            case TAG_NEW_QUALITY_GATE: {
                onAddQualityGate(0);
                break;
            }
            case TAG_IMPORT_QUALITY_GATE: {
                Intent intent = new Intent(this, SettingsImportQualityGateActivity.class);
                importQualityGateResultLauncher.launch(intent);
                break;
            }
        }
    }


    /**
     * Method is called whenever the activity is created.
     * @param savedInstanceState    Previously saved state of the instance.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel.loadQualityGatesIfRequired(false);

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

        //More button:
        findViewById(R.id.button_more).setOnClickListener(view -> showMoreDialog());
    }


    /**
     * Method is called to edit the custom quality gate at the specified position.
     *
     * @param position  Position at which to edit the quality gate.
     */
    private void onEditQualityGate(int position) {
        Intent intent = new Intent(this, QualityGateActivity.class);
        int index = position - QualityGatesRecyclerViewAdapter.OFFSET_DEFAULT_QUALITY_GATES;
        intent.putExtra(QualityGateActivity.KEY_QUALITY_GATE, viewModel.getCustomQualityGates().get(index));
        intent.putExtra(QualityGateActivity.KEY_INDEX, index);
        qualityGateResultLauncher.launch(intent);
    }


    /**
     * Method shows a dialog asking the user to confirm the deletion of the custom quality gate at
     * the specified position.
     *
     * @param position  Position of the quality gate to delete.
     */
    private void onDeleteQualityGate(int position) {
        int index = position - QualityGatesRecyclerViewAdapter.OFFSET_DEFAULT_QUALITY_GATES;
        QualityGate qualityGate = viewModel.getCustomQualityGates().get(index);
        DeleteDialog dialog = new DeleteDialog();
        Bundle args = new Bundle();
        String message = getString(R.string.delete_dialog_message);
        message = message.replace("{arg}", qualityGate.getDescription());
        args.putString(DeleteDialog.ARG_MESSAGE, message);
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), "" + index);
    }


    /**
     * Method starts the activity to add a new quality gate.
     *
     * @param position  Position of the recycler view item clicked.
     */
    private void onAddQualityGate(int position) {
        Intent intent = new Intent(this, QualityGateActivity.class);
        qualityGateResultLauncher.launch(intent);
    }

    /**
     * Method is called whenever the {@link MoreDialog} for a custom quality gate should be displayed.
     *
     * @param position  Position of the item clicked in the recycler view adapter.
     */
    private void onShowMoreOptions(int position) {
        int index = position - QualityGatesRecyclerViewAdapter.OFFSET_DEFAULT_QUALITY_GATES;
        QualityGate qualityGate = viewModel.getCustomQualityGates().get(index);
        MoreDialog dialog = new MoreDialog();
        Bundle args = new Bundle();
        args.putString(MoreDialog.ARG_TITLE, qualityGate.getDescription());
        args.putInt(MoreDialog.ARG_ICON, R.drawable.ic_shield);
        ArrayList<Item> items = new ArrayList<>();
        items.add(new ItemButton(getString(R.string.button_edit), TAG_EDIT_QUALITY_GATE + ":" + position, R.drawable.ic_edit));
        items.add(new ItemButton(getString(R.string.button_delete),TAG_DELETE_QUALITY_GATE + ":" + position, R.drawable.ic_delete));
        items.add(new ItemDivider());
        items.add(new ItemCheckbox(getString(R.string.quality_gate_enabled), TAG_ENABLE_QUALITY_GATE + ":" + position, qualityGate.isEnabled()));
        items.add(new ItemButton(getString(R.string.quality_gates_share), TAG_SHARE_QUALITY_GATE + ":" + position, R.drawable.ic_share));
        args.putSerializable(MoreDialog.ARG_ITEMS, items);
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), null);
    }


    /**
     * Method shows the dialog displaying more options for the activity.
     */
    private void showMoreDialog() {
        MoreDialog dialog = new MoreDialog();
        Bundle args = new Bundle();
        args.putString(MoreDialog.ARG_TITLE, getString(R.string.quality_gates));
        args.putInt(MoreDialog.ARG_ICON, R.drawable.ic_shield);
        ArrayList<Item> items = new ArrayList<>();
        items.add(new ItemButton(getString(R.string.button_add_quality_gate), TAG_NEW_QUALITY_GATE + ":0", R.drawable.ic_add));
        items.add(new ItemButton(getString(R.string.quality_gates_import_title),TAG_IMPORT_QUALITY_GATE + ":0", R.drawable.ic_import));
        args.putSerializable(MoreDialog.ARG_ITEMS, items);
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), null);
    }

}
