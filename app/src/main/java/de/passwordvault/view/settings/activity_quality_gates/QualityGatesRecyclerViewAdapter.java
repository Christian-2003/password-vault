package de.passwordvault.view.settings.activity_quality_gates;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import de.passwordvault.R;
import de.passwordvault.model.analysis.QualityGate;
import de.passwordvault.view.utils.recycler_view.GenericRecyclerViewAdapter;


/**
 * Class implements the recycler view adapter for the activity displaying all quality gates.
 *
 * @author  Christian-2003
 * @version 3.6.0
 */
public class QualityGatesRecyclerViewAdapter extends GenericRecyclerViewAdapter<QualityGatesRecyclerViewAdapter.ViewHolder, QualityGate> {

    /**
     * Class models a view holder for the adapter.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        /**
         * Attribute stores the text view displaying the description.
         */
        public final TextView descriptionTextView;

        /**
         * Attribute stores the text view displaying the regex.
         */
        public final TextView regexTextView;

        /**
         * Attribute stores the checkbox displaying whether the quality gate is enabled.
         */
        public final CheckBox enabledCheckBox;


        /**
         * Constructor instantiates a new view holder for the passed view.
         *
         * @param itemView  View for which to create the view holder.
         */
        public ViewHolder(View itemView) {
            super(itemView);
            descriptionTextView = itemView.findViewById(R.id.text_view_description);
            regexTextView = itemView.findViewById(R.id.text_view_regex);
            enabledCheckBox = itemView.findViewById(R.id.checkbox_enabled);
        }

    }


    /**
     * Constructor instantiates a new recycler view adapter.
     *
     * @param context   Activity context for the adapter.
     * @param data      Data for the adapter.
     */
    public QualityGatesRecyclerViewAdapter(@NonNull Context context, @NonNull ArrayList<QualityGate> data) {
        super(context, data);
    }


    /**
     * Method is called whenever a new view holder needs to be created.
     *
     * @param parent    Recycler view.
     * @param viewType  Type of the view.
     * @return          Created view holder.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.list_item_quality_gates, parent, false);
        return new ViewHolder(itemView);
    }


    /**
     * Method is called whenever data is bound to a view holder.
     *
     * @param holder    View holder to which to bind data.
     * @param position  Position of the view holder within the data.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        QualityGate qualityGate = data.get(position);
        holder.descriptionTextView.setText(qualityGate.getDescription());
        holder.regexTextView.setText(qualityGate.getRegex());
        holder.enabledCheckBox.setChecked(qualityGate.isEnabled());
        holder.enabledCheckBox.setOnCheckedChangeListener((checkbox, checked) -> qualityGate.setEnabled(checked));
    }

}
