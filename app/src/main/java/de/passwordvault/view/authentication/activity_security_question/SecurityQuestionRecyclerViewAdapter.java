package de.passwordvault.view.authentication.activity_security_question;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.Objects;
import de.passwordvault.R;
import de.passwordvault.view.utils.recycler_view.OnRecyclerViewActionListener;
import de.passwordvault.view.utils.recycler_view.RecyclerViewAdapter;


/**
 * Class implements the recycler view adapter for the {@link SecurityQuestionActivity}.
 *
 * @author  Christian-2003
 * @version 3.7.1
 */
public class SecurityQuestionRecyclerViewAdapter extends RecyclerViewAdapter<SecurityQuestionViewModel> {

    /**
     * Class implements the view holder for the view displaying a security question the user needs
     * to answer.
     */
    public static class QuestionViewHolder extends RecyclerView.ViewHolder {

        /**
         * Attribute stores the text view displaying the security question the user needs to answer.
         */
        public final TextView questionTextView;

        /**
         * Attribute stores the text input layout containing the edit text to enter the answer to the
         * security question.
         */
        public final TextInputLayout answerContainer;

        /**
         * Attribute stores the input through which the user can enter an answer to the security
         * question displayed.
         */
        public final TextInputEditText answerInput;


        /**
         * Constructor instantiates a new view holder for the specified item view.
         *
         * @param itemView  Inflated view for which to create the view holder.
         */
        public QuestionViewHolder(View itemView) {
            super(itemView);
            questionTextView = itemView.findViewById(R.id.text_question);
            answerContainer = itemView.findViewById(R.id.container_answer);
            answerInput = itemView.findViewById(R.id.input_answer);
        }

    }

    /**
     * Class implements the view holder for the view displaying the button and error message at the
     * bottom of the page.
     */
    public static class RecoverViewHolder extends RecyclerView.ViewHolder {

        /**
         * Attribute stores the button through which the user can recover the master password.
         */
        public final Button recoverButton;

        /**
         * Attribute stores the text view displaying an error if an answer is incorrect.
         */
        public final TextView errorText;


        /**
         * Constructor instantiates a new view holder for the passed item view.
         *
         * @param itemView  Inflated view for which to create the view holder.
         */
        public RecoverViewHolder(View itemView) {
            super(itemView);
            recoverButton = itemView.findViewById(R.id.button_recover);
            errorText = itemView.findViewById(R.id.text_error);
        }

    }


    /**
     * Field stores the view type for the view displaying a security question.
     */
    private static final int TYPE_QUESTION = 1;

    /**
     * Field stores the view type for the view displaying the button and error message at the bottom
     * of the page.
     */
    private static final int TYPE_RECOVER = 3;

    /**
     * Field stores the offset with which the questions are displayed.
     */
    public static final int OFFSET_QUESTIONS = 1;


    /**
     * Attribute stores the action listener to invoke once the recover button is clicked.
     */
    @Nullable
    private OnRecyclerViewActionListener onRecoverClicked;

    /**
     * Attribute stores the view holders for all questions.
     */
    private final QuestionViewHolder[] questionViewHolders;


    /**
     * Constructor instantiates a new recycler view adapter.
     *
     * @param context   Context for the recycler view adapter.
     * @param viewModel View model from which to source the data.
     */
    public SecurityQuestionRecyclerViewAdapter(@NonNull Context context, @NonNull SecurityQuestionViewModel viewModel) {
        super(context, viewModel);
        onRecoverClicked = null;
        questionViewHolders= new QuestionViewHolder[viewModel.getQuestions().size()];
    }


    /**
     * Method changes the action listener to invoke once the recover button is clicked.
     *
     * @param onRecoverClicked  New action listener to invoke.
     */
    public void setOnRecoverClicked(@Nullable OnRecyclerViewActionListener onRecoverClicked) {
        this.onRecoverClicked = onRecoverClicked;
    }


    /**
     * Method returns the answer to the security question entered at the specified position.
     *
     * @param position  Position of the security question whose answer to return where 0 is the first
     *                  answer.
     * @return          Answer to the security question at the specified position.
     */
    public String getAnswer(int position) {
        if (position >= 0 && position < questionViewHolders.length && questionViewHolders[position] != null) {
            return Objects.requireNonNull(questionViewHolders[position].answerInput.getText()).toString();
        }
        return null;
    }


    /**
     * Method updates the specified view holder with data.
     *
     * @param holder    The ViewHolder which should be updated to represent the contents of the
     *                  item at the given position in the data set.
     * @param position  The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof QuestionViewHolder) {
            //Security question:
            QuestionViewHolder viewHolder = (QuestionViewHolder)holder;
            questionViewHolders[position - OFFSET_QUESTIONS] = viewHolder;
            String securityQuestion = viewModel.getQuestions().get(position - OFFSET_QUESTIONS);
            viewHolder.questionTextView.setText(securityQuestion);
        }
        else if (holder instanceof GenericInfoViewHolder) {
            //Generic info:
            GenericInfoViewHolder viewHolder = (GenericInfoViewHolder)holder;
            viewHolder.infoTextView.setText(context.getString(R.string.recovery_login_info));
        }
        else if (holder instanceof RecoverViewHolder) {
            //Recover section at the bottom of the page:
            RecoverViewHolder viewHolder = (RecoverViewHolder)holder;
            if (onRecoverClicked != null) {
                viewHolder.recoverButton.setOnClickListener(view -> onRecoverClicked.onAction(holder.getAdapterPosition()));
            }
            viewHolder.errorText.setVisibility(viewModel.areAnswersTested() && !viewModel.areAnswersValid() ? View.VISIBLE : View.GONE);
        }
    }


    /**
     * Method creates a new view holder for the specified view type.
     *
     * @param parent    The ViewGroup into which the new View will be added after it is bound to
     *                  an adapter position.
     * @param viewType  The view type of the new View.
     * @return          Created view holder.
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case TYPE_QUESTION:
                view = layoutInflater.inflate(R.layout.item_recovery_answer, parent, false);
                return new QuestionViewHolder(view);
            case TYPE_RECOVER:
                view = layoutInflater.inflate(R.layout.item_recovery_restore, parent, false);
                return new RecoverViewHolder(view);
            default:
                view = layoutInflater.inflate(R.layout.item_generic_info, parent, false);
                return new GenericInfoViewHolder(view);
        }
    }


    /**
     * Method returns the view type for the view at the position queried.
     *
     * @param position  Position to query
     * @return          View type for the position queried.
     */
    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_GENERIC_INFO;
        }
        else if (position == getItemCount() - 1) {
            return TYPE_RECOVER;
        }
        else {
            return TYPE_QUESTION;
        }
    }


    /**
     * Method returns the number of items displayed by the recycler view.
     *
     * @return  Number of items displayed.
     */
    @Override
    public int getItemCount() {
        return viewModel.getQuestions().size() + 2;
    }

}
