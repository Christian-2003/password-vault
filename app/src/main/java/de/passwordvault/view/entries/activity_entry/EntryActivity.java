package de.passwordvault.view.entries.activity_entry;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import de.passwordvault.R;
import de.passwordvault.model.detail.Detail;
import de.passwordvault.model.detail.DetailType;
import de.passwordvault.model.entry.EntryExtended;
import de.passwordvault.model.entry.EntryManager;
import de.passwordvault.model.packages.SerializablePackage;
import de.passwordvault.model.packages.SerializablePackageCollection;
import de.passwordvault.model.tags.Tag;
import de.passwordvault.view.entries.activity_packages.PackagesActivity;
import de.passwordvault.view.entries.dialog_detail.DetailDialog;
import de.passwordvault.view.entries.dialog_edit_entry.EditEntryDialog;
import de.passwordvault.view.entries.dialog_edit_tag.EditTagDialog;
import de.passwordvault.view.entries.dialog_info_entry.InfoEntryDialog;
import de.passwordvault.view.general.dialog_delete.DeleteDialog;
import de.passwordvault.view.general.dialog_more.Item;
import de.passwordvault.view.general.dialog_more.ItemButton;
import de.passwordvault.view.general.dialog_more.ItemDivider;
import de.passwordvault.view.general.dialog_more.MoreDialog;
import de.passwordvault.view.general.dialog_more.MoreDialogCallback;
import de.passwordvault.view.passwords.activity_analysis.PasswordAnalysisActivity;
import de.passwordvault.view.utils.Utils;
import de.passwordvault.view.utils.components.PasswordVaultActivity;
import de.passwordvault.view.utils.components.PasswordVaultBottomSheetDialog;
import de.passwordvault.view.utils.recycler_view.RecyclerViewSwipeCallback;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Objects;


/**
 * Class models an activity which can display an {@link EntryExtended} to the user.
 *
 * @author  Christian-2003
 * @version 3.7.0
 */
public class EntryActivity extends PasswordVaultActivity<EntryViewModel> implements PasswordVaultBottomSheetDialog.Callback, MoreDialogCallback {

    /**
     * Field stores the key to use when passing the UUID of an entry to show as extra with the intent.
     */
    public static final String KEY_ID = "uuid";

    /**
     * Field stores the result code returned if the displayed entry has been edited.
     */
    public static final int RESULT_EDITED = 314;

    /**
     * Field stores the result code returned if the displayed entry is deleted.
     */
    public static final int RESULT_DELETED = 315;

    /**
     * Field stores the tag for the more dialog to show items for details.
     */
    private static final String TAG_DIALOG_DETAIL = "detail";

    /**
     * Field stores the tag for the more dialog to show items for the entry.
     */
    private static final String TAG_DIALOG_ENTRY = "entry";

    /**
     * Field stores the tag for the more dialog item to edit a detail.
     */
    private static final String TAG_EDIT_DETAIL = "edit";

    /**
     * Field stores the tag for the more dialog item to delete a detail.
     */
    private static final String TAG_DELETE_DETAIL = "delete";

    /**
     * Field stores the tag for the more dialog item to copy the content of a detail.
     */
    private static final String TAG_COPY_DETAIL = "copy";

    /**
     * Field stores the tag for the more dialog item to edit the entry.
     */
    private static final String TAG_EDIT_ENTRY = "edit";

    /**
     * Field stores the tag for the more dialog item to delete the entry.
     */
    private static final String TAG_DELETE_ENTRY = "delete";

    /**
     * Field stores the tag for the more dialog item to select an app for autofill.
     */
    private static final String TAG_SELECT_APP = "select";

    /**
     * Field stores the tag for the more dialog item to show additional info of the entry.
     */
    private static final String TAG_SHOW_INFO = "info";

    /**
     * Field stores the tag for the more dialog item to analyze the password security of all
     * passwords.
     */
    private static final String TAG_PASSWORD_ANALYSIS = "analysis";

    /**
     * Field stores the tag for the {@link DetailDialog} to add a new detail.
     */
    private static final String TAG_ADD_DETAIL = "copy";


    /**
     * Attribute stores the recycler view adapter
     */
    private EntryRecyclerViewAdapter adapter;

    /**
     * Attribute stores the text view displaying the entry name.
     */
    private TextView appBarTextView;

    /**
     * Attribute stores the launcher used to start the {@link de.passwordvault.view.entries.activity_packages.PackagesActivity}.
     */
    private final ActivityResultLauncher<Intent> packagesLauncher;

    /**
     * Attribute stores the dialog to edit the entry. This is always {@code null} unless the dialog
     * is currently visible to the user.
     */
    @Nullable
    private EditEntryDialog editEntryDialog;


    /**
     * Constructor instantiates a new activity.
     */
    public EntryActivity() {
        super(EntryViewModel.class, R.layout.activity_entry);

        packagesLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == PackagesActivity.RESULT_OK && result.getData() != null && result.getData().getExtras() != null) {
                Bundle extras = result.getData().getExtras();
                if (extras.containsKey(PackagesActivity.KEY_PACKAGES)) {
                    //Even though the PackagesActivity returns a SerializablePackageCollection as extra,
                    //this must be cast to an ArrayList<SerializablePackage>! Otherwise, the app crashes
                    //due do a ClassCastException because the result value cannot be cast to a
                    //SerializablePackageCollection:
                    ArrayList<SerializablePackage> packages = (ArrayList<SerializablePackage>)extras.getSerializable(PackagesActivity.KEY_PACKAGES);
                    if (packages != null && viewModel.getEntry() != null) {
                        viewModel.getEntry().setPackages(new SerializablePackageCollection(packages).toPackageCollection());
                    }
                    adapter.notifyItemChanged(EntryRecyclerViewAdapter.POSITION_GENERAL);
                    viewModel.entryVisiblyEdited();
                }
            }
        });
    }


    /**
     * Method is called whenever a dialog callback is invoked.
     *
     * @param dialog        Dialog that was closed.
     * @param resultCode    Result code is either {@link #RESULT_SUCCESS} or {@link #RESULT_CANCEL}
     *                      and indicates how the dialog is closed.
     */
    @Override
    public void onCallback(PasswordVaultBottomSheetDialog<? extends ViewModel> dialog, int resultCode) {
        if (dialog instanceof EditEntryDialog) {
            //Edit entry:
            EditEntryDialog editEntryDialog = (EditEntryDialog)dialog;
            if (resultCode == PasswordVaultBottomSheetDialog.Callback.RESULT_SUCCESS && viewModel.getEntry() != null) {
                viewModel.getEntry().setName(editEntryDialog.getName());
                viewModel.getEntry().setDescription(editEntryDialog.getDescription());
                viewModel.getEntry().getTags().clear();
                for (Tag tag : editEntryDialog.getSelectedTags()) {
                    viewModel.getEntry().getTags().add(tag);
                }
                appBarTextView.setText(viewModel.getEntry().getName());
                adapter.notifyItemChanged(EntryRecyclerViewAdapter.POSITION_GENERAL);
                viewModel.entryVisiblyEdited();
            }
            else if (editEntryDialog.isTagListChanged()) {
                //A tag might have been deleted or renamed:
                adapter.notifyItemChanged(EntryRecyclerViewAdapter.POSITION_GENERAL);
            }
            this.editEntryDialog = null; //Remove reference to dialog
        }
        else if (dialog instanceof DeleteDialog) {
            if (resultCode == PasswordVaultBottomSheetDialog.Callback.RESULT_SUCCESS) {
                if (dialog.getTag() == null) {
                    //Delete entry:
                    EntryManager.getInstance().remove(Objects.requireNonNull(viewModel.getEntry()).getUuid());
                    viewModel.setResultCode(RESULT_DELETED);
                    finish();
                }
                else {
                    //Delete detail:
                    if (viewModel.getEntry() != null) {
                        viewModel.getEntry().remove(dialog.getTag());
                        adapter.notifyItemRangeChanged(EntryRecyclerViewAdapter.OFFSET_DETAILS, adapter.getItemCount() - 1);
                        if (viewModel.getEntry().getDetails().isEmpty()) {
                            adapter.notifyItemChanged(EntryRecyclerViewAdapter.POSITION_DETAILS_EMPTY_PLACEHOLDER);
                        }
                    }
                }
            }
        }
        else if (dialog instanceof DetailDialog) {
            if (resultCode == PasswordVaultBottomSheetDialog.Callback.RESULT_SUCCESS) {
                DetailDialog detailDialog = (DetailDialog)dialog;
                if (detailDialog.getTag() != null && viewModel.getEntry() != null) {
                    if (detailDialog.getTag().equals(TAG_ADD_DETAIL)) {
                        //Add detail:
                        Detail detail = detailDialog.getDetail();
                        if (detail != null) {
                            boolean hideEmptyPlaceholder = viewModel.getEntry().getDetails().isEmpty();
                            viewModel.getEntry().add(detail);
                            adapter.notifyItemRangeChanged(EntryRecyclerViewAdapter.OFFSET_DETAILS, adapter.getItemCount() - 1);
                            if (hideEmptyPlaceholder) {
                                adapter.notifyItemChanged(EntryRecyclerViewAdapter.POSITION_DETAILS_EMPTY_PLACEHOLDER);
                            }
                        }
                    }
                    else if (detailDialog.getTag().equals(TAG_EDIT_DETAIL)) {
                        //Edit detail:
                        Detail detail = detailDialog.getDetail();
                        if (detail != null) {
                            viewModel.getEntry().set(detail);
                            adapter.notifyItemRangeChanged(EntryRecyclerViewAdapter.OFFSET_DETAILS, adapter.getItemCount() - 1);
                        }
                    }
                    viewModel.entryEdited();
                }
            }
        }
        else if (dialog instanceof EditTagDialog) {
            if (resultCode == PasswordVaultBottomSheetDialog.Callback.RESULT_SUCCESS) {
                //Tag added, edited or deleted:
                if (editEntryDialog != null) {
                    editEntryDialog.notifyTagsChanged();
                }
            }
        }
    }


    /**
     * Method is called whenever an item within the {@link MoreDialog} is clicked.
     *
     * @param dialog    Dialog in which the action was invoked.
     * @param tag       Tag from the {@link Item} whose action is invoked.
     * @param position  Position of the {@link Item} within the dialog.
     */
    @Override
    public void onDialogItemClicked(MoreDialog dialog, String tag, int position) {
        if (dialog.getTag() != null) {
            if (dialog.getTag().equals(TAG_DIALOG_DETAIL)) {
                //Actions for details:
                String[] tagParts = tag.split(":");
                if (tagParts.length != 2) {
                    return;
                }
                int adapterPosition;
                try {
                    adapterPosition = Integer.parseInt(tagParts[1]);
                }
                catch (NumberFormatException e) {
                    return;
                }
                switch (tagParts[0]) {
                    case TAG_EDIT_DETAIL: {
                        onEditDetail(adapterPosition);
                        break;
                    }
                    case TAG_DELETE_DETAIL: {
                        onDeleteDetail(adapterPosition);
                        break;
                    }
                    case TAG_COPY_DETAIL: {
                        Detail detail = adapter.getDetailForAdapterPosition(adapterPosition);
                        if (detail != null) {
                            Utils.copyToClipboard(detail.getContent());
                        }
                        break;
                    }
                    case TAG_PASSWORD_ANALYSIS: {
                        Intent intent = new Intent(this, PasswordAnalysisActivity.class);
                        startActivity(intent);
                        break;
                    }
                }
            }
            else if (dialog.getTag().equals(TAG_DIALOG_ENTRY)) {
                //Actions for entry:
                switch (tag) {
                    case TAG_EDIT_ENTRY: {
                        onEditEntryClicked(0);
                        break;
                    }
                    case TAG_DELETE_ENTRY: {
                        onDeleteClicked(null);
                        break;
                    }
                    case TAG_ADD_DETAIL: {
                        onAddDetail(0);
                        break;
                    }
                    case TAG_SELECT_APP: {
                        onPackagesClicked(0);
                        break;
                    }
                    case TAG_SHOW_INFO: {
                        InfoEntryDialog infoEntryDialog = new InfoEntryDialog();
                        Bundle args = new Bundle();
                        args.putSerializable(InfoEntryDialog.ARG_ENTRY, viewModel.getEntry());
                        infoEntryDialog.setArguments(args);
                        infoEntryDialog.show(getSupportFragmentManager(), null);
                        break;
                    }
                }
            }
        }
    }


    /**
     * Method is called whenever the activity is closed.
     */
    @Override
    public void finish() {
        if (viewModel.isEdited() && viewModel.getEntry() != null) {
            viewModel.getEntry().notifyDataChange();
            EntryManager.getInstance().set(viewModel.getEntry(), viewModel.getEntry().getUuid());
        }
        if (viewModel.isVisiblyEdited()) {
            //No need to save data, since visibly changes necessitate "regular" changes as well.
            if (viewModel.getResultCode() != RESULT_DELETED) {
                viewModel.setResultCode(RESULT_EDITED);
            }
        }
        setResult(viewModel.getResultCode());
        Log.d("EntryActivity", "Set result " + (viewModel.getResultCode() == RESULT_EDITED ? "EDITED" : "DELETED") + " for entry " + viewModel.getEntry().getName());
        super.finish();
    }


    /**
     * Method is called whenever the activity is created.
     *
     * @param savedInstanceState    Previously saved state of the instance.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableSecureModeIfRequired();

        //Get entry to display or create new entry:
        if (viewModel.getEntry() == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null && extras.containsKey(KEY_ID)) {
                String uuid = extras.getString(KEY_ID);
                EntryExtended entry = EntryManager.getInstance().get(uuid);
                if (entry != null) {
                    viewModel.setEntry(new EntryExtended(entry));
                }
            }
            if (viewModel.getEntry() == null) {
                EntryExtended entry = new EntryExtended();
                entry.setName(getString(R.string.entry_name_placeholder).replace("{arg}", (EntryManager.getInstance().size() + 1) + ""));
                viewModel.setEntry(entry);
            }
        }

        //Configure recycler view adapter:
        adapter = new EntryRecyclerViewAdapter(this, viewModel);
        adapter.setEditEntryListener(this::onEditEntryClicked);
        adapter.setDeleteClickListener(this::onDeleteClicked);
        adapter.setEditPackagesListener(this::onPackagesClicked);
        adapter.setAddDetailListener(this::onAddDetail);
        adapter.setMoreListener(this::showDetailMoreDialog);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setAdapter(adapter);

        RecyclerViewSwipeCallback.SwipeAction leftSwipe = RecyclerViewSwipeCallback.makeLeftSwipeAction(this::onEditDetail, this::onDeleteDetail);
        RecyclerViewSwipeCallback.SwipeAction rightSwipe = RecyclerViewSwipeCallback.makeRightSwipeAction(this::onEditDetail, this::onDeleteDetail);
        EntryRecyclerViewMoveCallback callback = new EntryRecyclerViewMoveCallback(adapter, leftSwipe, rightSwipe);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);

        appBarTextView = findViewById(R.id.text_appbar);
        appBarTextView.setText(viewModel.getEntry().getName());
        appBarTextView.setOnClickListener(view -> onEditEntryClicked(0));

        //Back button:
        findViewById(R.id.button_back).setOnClickListener(view -> finish());

        //More button:
        findViewById(R.id.button_more).setOnClickListener(view -> showEntryMoreDialog());
    }


    /**
     * Method is called whenever the entire entry shall be deleted.
     *
     * @param view  View that was clicked.
     */
    private void onDeleteClicked(View view) {
        DeleteDialog dialog = new DeleteDialog();
        Bundle args = new Bundle();
        args.putString(DeleteDialog.ARG_MESSAGE, getString(R.string.delete_dialog_message).replace("{arg}", viewModel.getEntry().getName()));
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), null);
    }

    /**
     * Method is called whenever the button to add a new detail is clicked. This shows the dialog
     * to add a detail. Adding the detail happens in
     * {@link #onCallback(PasswordVaultBottomSheetDialog, int)}.
     *
     * @param position  Adapter position at which the button was clicked.
     */
    private void onAddDetail(int position) {
        DetailDialog detailDialog = new DetailDialog();
        detailDialog.show(getSupportFragmentManager(), TAG_ADD_DETAIL);
    }

    /**
     * Method is called whenever a detail shall be edited. This shows the dialog to edit the detail.
     * Editing the detail happens in {@link #onCallback(PasswordVaultBottomSheetDialog, int)}.
     *
     * @param position  Adapter position of the detail to edit.
     */
    private void onEditDetail(int position) {
        Detail detail = adapter.getDetailForAdapterPosition(position);
        if (detail != null) {
            Bundle args = new Bundle();
            args.putSerializable(DetailDialog.ARG_DETAIL, detail);
            DetailDialog detailDialog = new DetailDialog();
            detailDialog.setArguments(args);
            detailDialog.show(getSupportFragmentManager(), TAG_EDIT_DETAIL);
        }
    }

    /**
     * Method is called whenever a detail shall be deleted. This shows the dialog to confirm deletion.
     * Deleting the detail happens in {@link #onCallback(PasswordVaultBottomSheetDialog, int)}.
     *
     * @param position  Position of the detail to delete.
     */
    private void onDeleteDetail(int position) {
        Detail detail = adapter.getDetailForAdapterPosition(position);
        if (detail != null) {
            Bundle args = new Bundle();
            args.putString(DeleteDialog.ARG_MESSAGE, getString(R.string.delete_dialog_message).replace("{arg}", detail.getName()));
            DeleteDialog deleteDialog = new DeleteDialog();
            deleteDialog.setArguments(args);
            deleteDialog.show(getSupportFragmentManager(), detail.getUuid());
        }
    }


    /**
     * Method is called whenever the entry shall be edited. This shows the dialog to edit the entry.
     *
     * @param position  Adapter position from which the method is called.
     */
    private void onEditEntryClicked(int position) {
        if (viewModel.getEntry() != null) {
            editEntryDialog = new EditEntryDialog();
            Bundle args = new Bundle();
            args.putString(EditEntryDialog.ARG_NAME, viewModel.getEntry().getName());
            args.putString(EditEntryDialog.ARG_DESCRIPTION, viewModel.getEntry().getDescription());
            args.putSerializable(EditEntryDialog.ARG_TAGS, new ArrayList<>(viewModel.getEntry().getTags()));
            editEntryDialog.setArguments(args);
            editEntryDialog.show(getSupportFragmentManager(), null);
        }
    }

    /**
     * Method is called whenever the packages of the entry shall be edited.
     *
     * @param position  Adapter position from which the method is called.
     */
    private void onPackagesClicked(int position) {
        if (viewModel.getEntry() != null) {
            Intent intent = new Intent(this, PackagesActivity.class);
            intent.putExtra(PackagesActivity.KEY_PACKAGES, new SerializablePackageCollection(viewModel.getEntry().getPackages()));
            packagesLauncher.launch(intent);
        }
    }

    /**
     * Method is called whenever the dialog the show more options for details shall be shown.
     *
     * @param position  Adapter position for which to show the dialog.
     */
    private void showDetailMoreDialog(int position) {
        Detail detail = adapter.getDetailForAdapterPosition(position);
        if (detail == null) {
            return;
        }

        Bundle args = new Bundle();
        args.putString(MoreDialog.ARG_TITLE, detail.getName());
        args.putInt(MoreDialog.ARG_ICON, R.drawable.ic_entry); //TODO: Change icon for dialog
        ArrayList<Item> items = new ArrayList<>();
        items.add(new ItemButton(getString(R.string.button_edit), TAG_EDIT_DETAIL + ":" + position, R.drawable.ic_edit));
        items.add(new ItemButton(getString(R.string.button_delete), TAG_DELETE_DETAIL + ":" + position, R.drawable.ic_delete));
        items.add(new ItemDivider());
        items.add(new ItemButton(getString(R.string.button_copy_detail), TAG_COPY_DETAIL + ":" + position, R.drawable.ic_copy));
        if (detail.getType() == DetailType.PASSWORD) {
            items.add(new ItemButton(getString(R.string.button_analyze_password_security), TAG_PASSWORD_ANALYSIS + ":" + position, R.drawable.ic_lock));
        }
        args.putSerializable(MoreDialog.ARG_ITEMS, items);

        MoreDialog dialog = new MoreDialog();
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), TAG_DIALOG_DETAIL);
    }


    /**
     * Method shows the more dialog for the entry.
     */
    private void showEntryMoreDialog() {
        if (viewModel.getEntry() == null) {
            return;
        }
        Bundle args = new Bundle();
        args.putString(MoreDialog.ARG_TITLE, viewModel.getEntry().getName());
        args.putInt(MoreDialog.ARG_ICON, R.drawable.ic_entry);
        ArrayList<Item> items = new ArrayList<>();
        items.add(new ItemButton(getString(R.string.entry_more_edit), TAG_EDIT_ENTRY, R.drawable.ic_edit));
        items.add(new ItemButton(getString(R.string.entry_more_delete), TAG_DELETE_DETAIL, R.drawable.ic_delete));
        items.add(new ItemDivider());
        items.add(new ItemButton(getString(R.string.entry_more_add_detail), TAG_ADD_DETAIL, R.drawable.ic_add));
        items.add(new ItemButton(getString(R.string.entry_more_select_app), TAG_SELECT_APP, R.drawable.ic_password));
        items.add(new ItemButton(getString(R.string.entry_more_info), TAG_SHOW_INFO, R.drawable.ic_info));
        args.putSerializable(MoreDialog.ARG_ITEMS, items);

        MoreDialog dialog = new MoreDialog();
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), TAG_DIALOG_ENTRY);
    }

}
