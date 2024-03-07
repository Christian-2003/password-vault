package de.passwordvault.view.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import de.passwordvault.R;
import de.passwordvault.model.analysis.QualityGateManager;
import de.passwordvault.model.analysis.passwords.Password;
import de.passwordvault.model.entry.EntryManager;


/**
 * Class implements an adapter for a recycler view which can display {@link Password}-instances.
 *
 * @author  Christian-2003
 * @version 3.4.0
 */
public class PasswordsRecyclerViewAdapter extends RecyclerView.Adapter<PasswordsRecyclerViewAdapter.ViewHolder> {

    /**
     * Class models a view holder for the passwords.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        /**
         * Attribute stores the text view which displays the password.
         */
        public TextView password;

        /**
         * Attribute stores the text view which displays the security score.
         */
        public TextView securityScore;

        /**
         * Attribute stores the text view which is used to display the name of the entry of which
         * the password is a part.
         */
        public TextView name;

        /**
         * Attribute stores the button which is used to obfuscate the password.
         */
        public ImageButton obfuscateButton;

        /**
         * Attribute stores the item view of this view holder.
         */
        public View itemView;


        /**
         * Constructor instantiates a new view holder for the specified item view.
         *
         * @param itemView  View for which to create the view holder.
         */
        public ViewHolder(View itemView) {
            super(itemView);
            password = itemView.findViewById(R.id.list_item_password_content);
            securityScore = itemView.findViewById(R.id.list_item_password_security_score);
            name = itemView.findViewById(R.id.list_item_password_name);
            obfuscateButton = itemView.findViewById(R.id.list_item_password_obfuscate_button);
            this.itemView = itemView;
        }

    }


    /**
     * Attribute stores the data that is displayed by the recycler view.
     */
    private final ArrayList<Password> data;

    /**
     * Attribute stores the click listener that is called when an item is clicked.
     */
    private final OnRecyclerItemClickListener<Password> clickListener;

    /**
     * Attribute stores whether the entry name shall be displayed.
     */
    private final boolean displayEntryName;


    /**
     * Constructor instantiates a new adapter for a recycler view which can display passwords. Pass
     * {@code null} as click listener if nothing shall happen when an item is clicked.
     *
     * @param data                  List of passwords to be displayed.
     * @param clickListener         Click listener that is called when a password is clicked.
     * @param displayEntryName      Indicates whether the entry name of the passwords are shown.
     * @throws NullPointerException The passed list of passwords is {@code null}.
     */
    public PasswordsRecyclerViewAdapter(ArrayList<Password> data, OnRecyclerItemClickListener<Password> clickListener, boolean displayEntryName) throws NullPointerException {
        if (data == null) {
            throw new NullPointerException();
        }
        this.data = data;
        this.clickListener = clickListener;
        this.displayEntryName = displayEntryName;
    }


    /**
     * Method creates a view holder for the specified view type.
     *
     * @param parent    The ViewGroup into which the new View will be added after it is bound to an
     *                  adapter position.
     * @param viewType  The view type of the new View.
     * @return          Generated view holder.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_password, parent, false);
        return new ViewHolder(itemView);
    }

    /**
     * Method binds the data of the detail at the specified position to the passed view holder.
     *
     * @param holder    The ViewHolder which should be updated to represent the contents of the item
     *                  at the given position in the data set.
     * @param position  The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Password password = data.get(position);
        holder.password.setText(Utils.obfuscate(password.getCleartextPassword()));
        if (displayEntryName) {
            holder.name.setText(EntryManager.getInstance().get(password.getEntryUuid()).getName());
        }
        else {
            holder.name.setVisibility(View.GONE);
        }
        holder.securityScore.setText(password.getSecurityScore() + "/" + QualityGateManager.getInstance().numberOfQualityGates());
        double securityScorePercentage = (double)password.getSecurityScore() / (double)QualityGateManager.getInstance().numberOfQualityGates();
        if (securityScorePercentage < 0.33) {
            holder.securityScore.setTextColor(holder.securityScore.getContext().getColor(R.color.red));
        }
        else if (securityScorePercentage > 0.67) {
            holder.securityScore.setTextColor(holder.securityScore.getContext().getColor(R.color.green));
        }
        else {
            holder.securityScore.setTextColor(holder.securityScore.getContext().getColor(R.color.yellow));
        }
        AtomicBoolean obfuscated = new AtomicBoolean(true);
        holder.obfuscateButton.setOnClickListener(view -> {
            if (obfuscated.get()) {
                holder.password.setText(password.getCleartextPassword());
                holder.obfuscateButton.setImageDrawable(AppCompatResources.getDrawable(holder.obfuscateButton.getContext(), R.drawable.ic_show_off));
                obfuscated.set(false);
            }
            else {
                holder.password.setText(Utils.obfuscate(password.getCleartextPassword()));
                holder.obfuscateButton.setImageDrawable(AppCompatResources.getDrawable(holder.obfuscateButton.getContext(), R.drawable.ic_show));
                obfuscated.set(true);
            }
        });
        if (clickListener != null) {
            holder.itemView.setOnClickListener(view -> clickListener.onItemClick(password, position));
        }
    }

    /**
     * Method returns the number of items being displayed by the recycler view.
     *
     * @return  Number of items being displayed.
     */
    @Override
    public int getItemCount() {
        return data.size();
    }

}
