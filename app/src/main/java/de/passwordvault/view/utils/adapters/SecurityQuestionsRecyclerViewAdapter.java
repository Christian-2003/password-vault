package de.passwordvault.view.utils.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import de.passwordvault.R;
import de.passwordvault.model.security.login.SecurityQuestion;
import de.passwordvault.view.utils.OnRecyclerItemClickListener;


/**
 * Class implements a recycler view adapter for security questions.
 *
 * @author  Christian-2003
 * @version 3.6.0
 */
public class SecurityQuestionsRecyclerViewAdapter extends RecyclerView.Adapter<SecurityQuestionsRecyclerViewAdapter.ViewHolder> {

    /**
     * Class implements a view holder for the recycler view adapter.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        /**
         * Attribute stores the text view displaying the question.
         */
        private final TextView questionTextView;

        /**
         * Attribute stores the text view displaying the answer.
         */
        private final TextView answerTextView;

        /**
         * Attribute stores the linear layout which contains the answer.
         */
        private final LinearLayout answerContainer;


        /**
         * Constructor instantiates a new view holder for the passed view.
         *
         * @param itemView  View from which to create the view holder.
         */
        public ViewHolder(View itemView) {
            super(itemView);
            questionTextView = itemView.findViewById(R.id.text_question);
            answerTextView = itemView.findViewById(R.id.text_answer);
            answerContainer = itemView.findViewById(R.id.container_answer);
        }


        /**
         * Method returns the text view displaying the question.
         *
         * @return  Text view displaying the question.
         */
        public TextView getQuestionTextView() {
            return questionTextView;
        }

        /**
         * Method returns the text view displaying the answer.
         *
         * @return  Text view displaying the answer.
         */
        public TextView getAnswerTextView() {
            return answerTextView;
        }

        /**
         * Method returns the linear layout containing the answer.
         *
         * @return  Linear layout containing the answer.
         */
        public LinearLayout getAnswerContainer() {
            return answerContainer;
        }

    }


    /**
     * Attribute stores the data to be displayed by the recycler view.
     */
    private final ArrayList<SecurityQuestion> data;

    /**
     * Attribute stores the click listener for when an item is clicked.
     */
    private final OnRecyclerItemClickListener<SecurityQuestion> itemClickListener;

    /**
     * Attribute stores the recycler view.
     */
    private RecyclerView recyclerView;


    /**
     * Constructor instantiates a new recycler view adapter.
     *
     * @param data                  Data for the adapter.
     * @param itemClickListener     Callback for when an item in the recycler view is clicked.
     * @throws NullPointerException The passed data is {@code null}.
     */
    public SecurityQuestionsRecyclerViewAdapter(ArrayList<SecurityQuestion> data, OnRecyclerItemClickListener<SecurityQuestion> itemClickListener) throws NullPointerException {
        if (data == null) {
            throw new NullPointerException();
        }
        this.data = data;
        this.itemClickListener = itemClickListener;
    }


    /**
     * Method is called to create a new view holder.
     *
     * @param parent    Recycler view to which the view is bound.
     * @param viewType  Type of the view.
     * @return          Created view holder.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_security_question, parent, false);
        return new ViewHolder(view);
    }


    /**
     * Method is called to bind the view holder at the specified position to the corresponding
     * security question.
     *
     * @param holder    View holder to which to bind the data.
     * @param position  Position of the view holder.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SecurityQuestion question = data.get(position);
        holder.getQuestionTextView().setText(SecurityQuestion.getAllQuestions()[question.getQuestion()]);
        holder.getAnswerTextView().setText(question.getAnswer());
        holder.getAnswerContainer().setVisibility(question.isExpanded() ? View.VISIBLE : View.GONE);
        holder.itemView.setOnClickListener(view -> {
            if (!recyclerView.isAnimating()) {
                question.setExpanded(!question.isExpanded());
                holder.getAnswerContainer().setVisibility(question.isExpanded() ? View.VISIBLE : View.GONE);
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(question, position);
                }
                notifyItemChanged(position);
            }
        });
    }


    /**
     * Method is called whenever the recycler view is attached to this adapter.
     *
     * @param recyclerView  The RecyclerView instance which started observing this adapter.
     */
    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }


    /**
     * Method returns the number of items within the adapter.
     *
     * @return  Number of items.
     */
    @Override
    public int getItemCount() {
        return data.size();
    }

}
