package de.passwordvault.view.passwords.activity_list;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import de.passwordvault.R;
import de.passwordvault.model.analysis.QualityGateManager;
import de.passwordvault.model.analysis.passwords.Password;
import de.passwordvault.model.entry.EntryAbbreviated;
import de.passwordvault.view.utils.Utils;
import de.passwordvault.view.utils.recycler_view.OnRecyclerViewActionListener;
import de.passwordvault.view.utils.recycler_view.RecyclerViewAdapter;


/**
 * Class implements the recycler view adapter for the {@link PasswordAnalysisListActivity}.
 *
 * @author  Christian-2003
 * @version 3.7.0
 */
public class PasswordAnalysisListRecyclerViewAdapter extends RecyclerViewAdapter<PasswordAnalysisListViewModel> {

    /**
     * Class implements a view holder for the password entry.
     */
    public static class PasswordViewHolder extends RecyclerView.ViewHolder {

        /**
         * Attribute stores the image view displaying a status symbol based on the security score.
         */
        public final ImageView statusImageView;

        /**
         * Attribute stores the text view displaying the password.
         */
        public final TextView passwordTextView;

        /**
         * Attribute stores the text view displaying additional info (e.g. entry name).
         */
        public final TextView additionalTextView;

        /**
         * Attribute stores the text view displaying the security score.
         */
        public final TextView scoreTextView;

        /**
         * Attribute stores the button with which to (un)obfuscate the password.
         */
        public final ImageButton obfuscateButton;


        /**
         * Constructor instantiates a new view holder for the passed view.
         *
         * @param itemView  Inflated view for which to create the view holder.
         */
        public PasswordViewHolder(View itemView) {
            super(itemView);
            statusImageView = itemView.findViewById(R.id.image_status);
            passwordTextView = itemView.findViewById(R.id.text_password);
            additionalTextView = itemView.findViewById(R.id.text_additional);
            scoreTextView = itemView.findViewById(R.id.text_score);
            obfuscateButton = itemView.findViewById(R.id.button_obfuscate);
        }

    }


    /**
     * Field stores the offset with which the passwords are displayed.
     */
    public static final int OFFSET_PASSWORDS = 1;

    /**
     * Field stores the view type for password items.
     */
    public static final int TYPE_PASSWORD_ITEM = 1;

    /**
     * Attribute stores the (filtered) passwords.
     */
    @NonNull
    private final ArrayList<Password> filteredPasswords;

    /**
     * Attribute stores the action listener invoked when an item is clicked.
     */
    @Nullable
    private OnRecyclerViewActionListener itemClickListener;


    /**
     * Constructor instantiates a new adapter.
     *
     * @param context   Context for the adapter.
     * @param viewModel View model for the adapter.
     */
    public PasswordAnalysisListRecyclerViewAdapter(@NonNull Context context, @NonNull PasswordAnalysisListViewModel viewModel) {
        super(context, viewModel);
        filteredPasswords = new ArrayList<>(viewModel.getPasswords());
    }


    /**
     * Method changes the action listener invoked when a password is clicked.
     *
     * @param itemClickListener Listener invoked when a password is clicked.
     */
    public void setItemClickListener(@Nullable OnRecyclerViewActionListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }


    /**
     * Method binds data to the passed view holder.
     *
     * @param holder    View holder to update.
     * @param position  The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (position) {
            case 0: {
                GenericEmptyPlaceholderViewHolder viewHolder = (GenericEmptyPlaceholderViewHolder)holder;
                viewHolder.imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.el_passwords));
                viewHolder.headlineTextView.setText(R.string.password_all_empty_title);
                viewHolder.supportTextView.setText(R.string.password_all_empty_info);
                if (filteredPasswords.isEmpty()) {
                    viewHolder.itemView.setVisibility(View.VISIBLE);
                    viewHolder.itemView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                }
                else {
                    viewHolder.itemView.setVisibility(View.GONE);
                    viewHolder.itemView.setLayoutParams(new ViewGroup.LayoutParams(0, 0));
                }
                break;
            }
            default: {
                PasswordViewHolder viewHolder = (PasswordViewHolder)holder;
                Password password = filteredPasswords.get(position - OFFSET_PASSWORDS);
                viewHolder.passwordTextView.setText(Utils.obfuscate(password.getCleartextPassword()));
                viewHolder.additionalTextView.setText(password.getName());

                //Obfuscate button:
                viewHolder.obfuscateButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_show));
                AtomicBoolean isObfuscated = new AtomicBoolean(true);
                viewHolder.obfuscateButton.setOnClickListener(view -> {
                    if (isObfuscated.get()) {
                        viewHolder.passwordTextView.setText(password.getCleartextPassword());
                        viewHolder.obfuscateButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_show_off));
                        isObfuscated.set(false);
                    }
                    else {
                        viewHolder.passwordTextView.setText(Utils.obfuscate(password.getCleartextPassword()));
                        viewHolder.obfuscateButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_show));
                        isObfuscated.set(true);
                    }
                });

                //Score display:
                viewHolder.scoreTextView.setText(password.getSecurityScore() + " / " + QualityGateManager.getInstance().numberOfQualityGates());
                if (password.getSecurityScore() > QualityGateManager.getInstance().numberOfQualityGates() * 0.67) {
                    viewHolder.statusImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_ok));
                    viewHolder.statusImageView.setColorFilter(ContextCompat.getColor(context, R.color.green));
                }
                else if (password.getSecurityScore() > QualityGateManager.getInstance().numberOfQualityGates() * 0.34) {
                    viewHolder.statusImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_info));
                    viewHolder.statusImageView.setColorFilter(ContextCompat.getColor(context, R.color.yellow));
                }
                else {
                    viewHolder.statusImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_alert));
                    viewHolder.statusImageView.setColorFilter(ContextCompat.getColor(context, R.color.red));
                }

                //Item click listener:
                viewHolder.itemView.setOnClickListener(view -> {
                    if (itemClickListener != null) {
                        itemClickListener.onAction(viewHolder.getAdapterPosition());
                    }
                });
                break;
            }
        }
    }


    /**
     * Method creates a new view holder for the passed view type.
     *
     * @param parent    Parent for the item view of the view holder.
     * @param viewType  The view type of the new View.
     * @return          Created view holder.
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case TYPE_PASSWORD_ITEM:
                view = layoutInflater.inflate(R.layout.item_password, parent, false);
                return new PasswordViewHolder(view);
            default:
                view = layoutInflater.inflate(R.layout.item_generic_empty_placeholder, parent, false);
                return new GenericEmptyPlaceholderViewHolder(view);
        }
    }


    /**
     * Method returns the item view type for the specified position.
     *
     * @param position  Position to query.
     * @return          View type for the queried position.
     */
    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_GENERIC_EMPTY_PLACEHOLDER;
        }
        return TYPE_PASSWORD_ITEM;
    }


    /**
     * Method returns the number of items displayed with the adapter.
     *
     * @return  Number of displayed items.
     */
    @Override
    public int getItemCount() {
        return filteredPasswords.size() + 1;
    }


    /**
     * Method resets the filter for the adapter.
     */
    public void resetFilter() {
        filteredPasswords.clear();
        filteredPasswords.addAll(viewModel.getPasswords());
        notifyDataSetChanged();
    }

    /**
     * Method uses the passed search query to filter the displayed entries.
     *
     * @param query Query to use for filtering.
     */
    public void filter(@Nullable String query) {
        if (query == null || query.isEmpty()) {
            resetFilter();
            return;
        }
        int displayedEntries = filteredPasswords.size();
        filteredPasswords.clear();
        query = query.toLowerCase();
        for (Password password : viewModel.getPasswords()) {
            if (password.getName().toLowerCase().contains(query)) {
                filteredPasswords.add(password);
            }
        }
        notifyDataSetChanged();
    }

    /**
     * Method returns the entry for the passed adapter position.
     *
     * @param position  Position whose entry to return.
     * @return          Entry at the specified position.
     */
    public Password getEntryForAdapterPosition(int position) {
        return filteredPasswords.get(position + OFFSET_PASSWORDS);
    }

}
