package de.passwordvault.view.general.dialog_more;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import de.passwordvault.R;
import de.passwordvault.view.utils.components.PasswordVaultBottomSheetDialog;


/**
 * Class implements the dialog which shows more options for an item of some sorts.
 *
 * @author  Christian-2003
 * @version 3.7.0
 */
public class MoreDialog extends PasswordVaultBottomSheetDialog<MoreViewModel> implements MoreRecyclerViewAdapter.DialogContract {

    /**
     * Field stores the key with which to pass the title for the dialog as string.
     */
    public static final String ARG_TITLE = "arg_title";

    /**
     * Field stores the key with which to pass the ID of the drawable icon for the dialog title bar.
     */
    public static final String ARG_ICON = "arg_icon";

    /**
     * Field stores the key with which to pass the list of items for the dialog as serializable.
     */
    public static final String ARG_ITEMS = "arg_items";


    /**
     * Constructor instantiates a new dialog.
     */
    public MoreDialog() {
        super(MoreViewModel.class, R.layout.dialog_more);
    }


    /**
     * Method is called whenever the view of the dialog is created.
     *
     * @param inflater              Layout inflater to use in order to inflate the dialog view.
     * @param container             Parent view of the dialog.
     * @param savedInstanceState    Previously saved state of the instance.
     * @return                      View for the dialog.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            viewModel.processArguments(args);
        }

        if (view != null) {
            ((TextView)view.findViewById(R.id.text_title)).setText(viewModel.getTitle());
            ((ImageView)view.findViewById(R.id.image_title)).setImageResource(viewModel.getIcon());
            MoreRecyclerViewAdapter adapter = new MoreRecyclerViewAdapter(requireContext(), viewModel, this);
            ((RecyclerView)view.findViewById(R.id.recycler_view)).setAdapter(adapter);
        }

        return view;
    }


    /**
     * Method is called whenever an item within the dialog is clicked.
     *
     * @param view  View of the item that was clicked.
     * @param item  Item that was clicked.
     */
    @Override
    public void onItemClicked(View view, ItemButton item) {
        item.getClickListener().onClick(view);
        dismiss();
    }

    /**
     * Method is called whenever an item's checkbox is changed.
     *
     * @param button    Checkbox.
     * @param item      Item whose checkbox was clicked.
     */
    @Override
    public void onCheckboxChecked(CompoundButton button, ItemCheckbox item) {
        item.getCheckedChangeListener().onCheckedChanged(button, button.isChecked());
        dismiss();
    }

}
