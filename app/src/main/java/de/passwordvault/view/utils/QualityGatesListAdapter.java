package de.passwordvault.view.utils;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.view.View;
import java.util.ArrayList;
import de.passwordvault.R;
import de.passwordvault.model.analysis.QualityGate;
import de.passwordvault.model.analysis.QualityGateManager;
import de.passwordvault.view.activities.QualityGateActivity;


/**
 * Class implements a list adapter which adapts an arraylist of {@link QualityGate}-instances for
 * a list view.
 *
 * @author  Christian-2003
 * @version 3.3.0
 */
public class QualityGatesListAdapter extends BaseAdapter {

    /**
     * Inner class models a {@link ViewHolder} for the generated {@linkplain View}-instances of
     * the {@link QualityGatesListAdapter}.
     */
    private static class ViewHolder {

        /**
         * Attribute stores the text view displaying the description of the list item.
         */
        public TextView description;

        /**
         * Attribute stores the text view displaying the regex of the list item.
         */
        public TextView regex;

        /**
         * Attribute stores the radio button of the list item.
         */
        public RadioButton radioButton;

        /**
         * Attribute stores the check box of the list item.
         */
        public CheckBox checkBox;

        /**
         * Attribute stores the back button of the list item.
         */
        public ImageButton editButton;

    }


    /**
     * Attribute stores the quality gates which are displayed.
     */
    private final ArrayList<QualityGate> qualityGates;

    /**
     * Attribute stores the context in which the adapter operates.
     */
    private final Context context;

    /**
     * Attribute stores the layout inflater with which to inflate the views.
     */
    private final LayoutInflater inflater;


    /**
     * Constructor instantiates a new list adapter which adapts the passed list of views for a list
     * view.
     *
     * @param qualityGates          Quality gates for which this adapter shall be created.
     * @param context               Context in which the list adapter is used.
     * @throws NullPointerException One of the passed arguments is {@code null}.
     */
    public QualityGatesListAdapter(ArrayList<QualityGate> qualityGates, Context context) throws NullPointerException {
        if (qualityGates == null || context == null) {
            throw new NullPointerException();
        }
        this.qualityGates = qualityGates;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }


    /**
     * Method returns the number of quality gates that should be displayed.
     *
     * @return  Number of quality gates.
     */
    @Override
    public int getCount() {
        return qualityGates.size();
    }


    /**
     * Method returns the ID of the item at the specified position.
     *
     * @param index Position whose item ID shall be returned.
     * @return      Item at the specified index.
     */
    @Override
    public Object getItem(int index) {
        return qualityGates.get(index);
    }


    /**
     * Method returns the ID of the item at the specified position.
     *
     * @param position  Position whose item ID shall be returned.
     * @return          ID of the item at the specified ID.
     */
    @Override
    public long getItemId(int position) {
        return position;
    }


    /**
     * Method generates a view for the list view.
     *
     * @param index     Index of the quality gate for which the view shall be created.
     * @param view      View from which to create the new view.
     * @param parent    List view.
     * @return          Generated view.
     */
    @Override
    public View getView(int index, View view, ViewGroup parent) {
        QualityGate gate = (QualityGate)getItem(index);
        ViewHolder holder;

        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.list_item_quality_gate, parent, false);
            holder.regex = view.findViewById(R.id.quality_gate_list_regex);
            holder.description = view.findViewById(R.id.quality_gate_list_description);
            holder.checkBox = view.findViewById(R.id.quality_gate_list_checkbox);
            holder.radioButton = view.findViewById(R.id.quality_gate_list_radio);
            holder.editButton = view.findViewById(R.id.quality_gate_list_edit_buttom);
            view.setTag(holder);
        }
        else {
            holder = (ViewHolder)view.getTag();
        }

        holder.regex.setText(gate.getRegex());
        holder.description.setText(gate.getDescription());
        holder.checkBox.setChecked(gate.isEnabled());
        holder.radioButton.setVisibility(View.GONE);
        holder.editButton.setVisibility(gate.isEditable() ? View.VISIBLE : View.GONE);

        holder.checkBox.setOnClickListener(view1 -> {
            CheckBox checkBox = (CheckBox)view1;
            checkBox.isSelected();
            gate.setEnabled(checkBox.isChecked());
            QualityGateManager.getInstance().setQualityGate(gate, index);
        });

        holder.editButton.setOnClickListener(view1 -> {
            Intent intent = new Intent(context, QualityGateActivity.class);
            intent.putExtra(QualityGateActivity.KEY_QUALITY_GATE, gate);
            intent.putExtra(QualityGateActivity.KEY_INDEX, index);
            context.startActivity(intent);
        });

        return view;
    }

}
