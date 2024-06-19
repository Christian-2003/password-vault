package de.passwordvault.view.utils.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import de.passwordvault.R;


/**
 * Class implements an adapter for a recycler view displaying views through which the user can enter
 * an answer to a security question.
 *
 * @author  Christian-2003
 * @version 3.6.0
 */
public class EnterSecurityAnswerRecyclerViewAdapter extends RecyclerView.Adapter<EnterSecurityAnswerRecyclerViewAdapter.ViewHolder> {

    /**
     * Class implements a view holder for this recycler view.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        /**
         * Attribute stores the text view displaying the question.
         */
        private final TextView questionTextView;

        /**
         * Attribute stores the edit text through which the user enters the answer.
         */
        private final TextInputEditText answerEditText;


        /**
         * Constructor instantiates a new view holder.
         *
         * @param itemView  View from which to create the view holder.
         */
        public ViewHolder(View itemView) {
            super(itemView);
            questionTextView = itemView.findViewById(R.id.text_view_question);
            answerEditText = itemView.findViewById(R.id.edit_text_answer);
        }


        /**
         * Method returns the text view with which to display the security question.
         *
         * @return  Text with which to display the question.
         */
        public TextView getQuestionTextView() {
            return questionTextView;
        }

        /**
         * Method returns the edit text in which the user enters the answer to the question.
         *
         * @return  Edit text through which to enter the answer.
         */
        public TextInputEditText getAnswerEditText() {
            return answerEditText;
        }

    }


    /**
     * Attribute stores the questions to display to the user.
     */
    private final ArrayList<String> data;


    /**
     * Constructor instantiates a new recycler view adapter.
     *
     * @param data                  Questions for the adapter.
     * @throws NullPointerException The passed list of questions is {@code null}.
     */
    public EnterSecurityAnswerRecyclerViewAdapter(ArrayList<String> data) throws NullPointerException {
        if (data == null) {
            throw new NullPointerException();
        }
        this.data = data;
    }


    /**
     * Method creates a new view holder.
     *
     * @param parent    Parent of the view for which to create a view holder.
     * @param viewType  Type of the view.
     * @return          Created view holder.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_enter_security_question, parent, false);
        return new ViewHolder(view);
    }


    /**
     * Method binds data to the passed view holder.
     *
     * @param holder    View holder to which to bind the data.
     * @param position  Position of the view holder within the recycler view.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.getQuestionTextView().setText(data.get(position));
    }


    /**
     * Method returns the number of items within the adapter.
     *
     * @return  Number of items within the adapter.
     */
    @Override
    public int getItemCount() {
        return data.size();
    }

}
