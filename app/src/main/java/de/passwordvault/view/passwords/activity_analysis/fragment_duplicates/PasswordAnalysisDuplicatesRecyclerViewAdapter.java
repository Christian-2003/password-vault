package de.passwordvault.view.passwords.activity_analysis.fragment_duplicates;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.concurrent.atomic.AtomicBoolean;
import de.passwordvault.R;
import de.passwordvault.model.analysis.QualityGateManager;
import de.passwordvault.model.analysis.passwords.Password;
import de.passwordvault.view.utils.Utils;
import de.passwordvault.view.utils.recycler_view.OnRecyclerViewActionListener;
import de.passwordvault.view.utils.recycler_view.RecyclerViewAdapter;


/**
 * Class implements a recycler view adapter for the {@link PasswordAnalysisDuplicatesFragment}.
 *
 * @author  Christian-2003
 * @version 3.7.0
 */
public class PasswordAnalysisDuplicatesRecyclerViewAdapter extends RecyclerViewAdapter<PasswordAnalysisDuplicatesViewModel> {

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
     * Attribute stores the click listener invoked whenever a password is clicked.
     */
    @Nullable
    private OnRecyclerViewActionListener itemClickListener;


    /**
     * Constructor instantiates a new adapter.
     *
     * @param context   Context for the adapter.
     * @param viewModel View model for the adapter.
     */
    public PasswordAnalysisDuplicatesRecyclerViewAdapter(@NonNull Context context, @NonNull PasswordAnalysisDuplicatesViewModel viewModel) {
        super(context, viewModel);
    }


    /**
     * Method changes the click listener invoked whenever a password is clicked.
     *
     * @param itemClickListener New action listener.
     */
    public void setItemClickListener(@Nullable OnRecyclerViewActionListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }


    /**
     * Method is called whenever data shall be bound to a view holder.
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
                if (viewModel.getPasswords().isEmpty()) {
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
                Password password = viewModel.getPasswords().get(position - OFFSET_PASSWORDS);
                viewHolder.passwordTextView.setText(Utils.obfuscate(password.getCleartextPassword()));
                viewHolder.additionalTextView.setVisibility(View.GONE);

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
                    viewHolder.statusImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_ok_filled));
                    viewHolder.statusImageView.setColorFilter(ContextCompat.getColor(context, R.color.green));
                }
                else if (password.getSecurityScore() > QualityGateManager.getInstance().numberOfQualityGates() * 0.34) {
                    viewHolder.statusImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_info_filled));
                    viewHolder.statusImageView.setColorFilter(ContextCompat.getColor(context, R.color.yellow));
                }
                else {
                    viewHolder.statusImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_alert_filled));
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
     * Method creates a new view holder for the specified view type.
     *
     * @param parent    Parent for the view of the view holder.
     * @param viewType  The view type of the new View.
     * @return          Created view holder.
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case TYPE_GENERIC_EMPTY_PLACEHOLDER:
                view = layoutInflater.inflate(R.layout.item_generic_empty_placeholder, parent, false);
                return new GenericEmptyPlaceholderViewHolder(view);
            default:
                view = layoutInflater.inflate(R.layout.item_password, parent, false);
                return new PasswordViewHolder(view);
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
     * Method returns the number of items displayed by the adapter.
     *
     * @return  Number of displayed items.
     */
    @Override
    public int getItemCount() {
        return viewModel.getPasswords().size() + 1;
    }

}
