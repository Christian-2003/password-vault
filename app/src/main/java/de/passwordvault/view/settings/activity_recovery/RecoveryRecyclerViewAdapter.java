package de.passwordvault.view.settings.activity_recovery;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import de.passwordvault.R;
import de.passwordvault.model.security.login.Account;
import de.passwordvault.model.security.login.SecurityQuestion;
import de.passwordvault.view.utils.recycler_view.RecyclerViewAdapter;


public class RecoveryRecyclerViewAdapter extends RecyclerViewAdapter<RecoveryViewModel> {

    /**
     * Class models the view holder for the item displaying the progress bar of the recovery setup.
     */
    public static class RecoveryProgressBarViewHolder extends RecyclerView.ViewHolder {

        /**
         * Attribute stores the text view displaying the progress score.
         */
        public final TextView scoreTextView;

        /**
         * Attribute stores the progress bar displaying the progress.
         */
        public final ProgressBar progressBar;

        /**
         * Attribute stores the text view displaying the error message in case the setup is not
         * finished.
         */
        public final TextView errorTextView;


        /**
         * Constructor instantiates a new view holder.
         *
         * @param itemView  Inflated view from which to create the view holder.
         */
        public RecoveryProgressBarViewHolder(@NonNull View itemView) {
            super(itemView);
            scoreTextView = itemView.findViewById(R.id.text_score);
            progressBar = itemView.findViewById(R.id.progress_bar);
            errorTextView = itemView.findViewById(R.id.text_error);
        }

    }

    /**
     * Class models the view holder for the item displaying a security question.
     */
    public static class RecoveryQuestionViewHolder extends RecyclerView.ViewHolder {

        /**
         * Attribute stores the text view displaying the security question.
         */
        public final TextView questionTextView;

        /**
         * Attribute stores the text view displaying the answer.
         */
        public final TextView answerTextView;

        /**
         * Attribute stores the container which contains the answer which can be collapsed.
         */
        public final LinearLayout containerAnswer;


        /**
         * Constructor instantiates a new view holder.
         *
         * @param itemView  Inflated view from which to create the view holder.
         */
        public RecoveryQuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            questionTextView = itemView.findViewById(R.id.text_question);
            answerTextView = itemView.findViewById(R.id.text_answer);
            containerAnswer = itemView.findViewById(R.id.container_answer);
        }

    }


    /**
     * Field stores the view type for the recovery progress.
     */
    public static final int TYPE_RECOVERY_PROGRESS_BAR = 1;

    /**
     * Field stores the view type for the recovery question.
     */
    public static final int TYPE_RECOVERY_QUESTION = 3;


    /**
     * Constructor instantiates a new recycler view adapter.
     *
     * @param context   Context for the adapter.
     * @param viewModel View model for the adapter.
     */
    public RecoveryRecyclerViewAdapter(@NonNull Context context, @NonNull RecoveryViewModel viewModel) {
        super(context, viewModel);
    }


    /**
     * Method creates a new view holder for the specified view type.
     *
     * @param parent    Parent for the item view of the view holder.
     * @param viewType  View type of the new view.
     * @return          Created view holder.
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        View view;
        switch (viewType) {
            case TYPE_GENERIC_INFO:
                view = layoutInflater.inflate(R.layout.item_generic_info, parent, false);
                holder = new GenericInfoViewHolder(view);
                break;
            case TYPE_GENERIC_HEADLINE:
                view = layoutInflater.inflate(R.layout.item_generic_headline, parent, false);
                holder = new GenericHeadlineViewHolder(view);
                break;
            case TYPE_GENERIC_HEADLINE_BUTTON:
                view = layoutInflater.inflate(R.layout.item_generic_headline_button, parent, false);
                holder = new GenericHeadlineButtonViewHolder(view);
                break;
            case TYPE_RECOVERY_PROGRESS_BAR:
                view = layoutInflater.inflate(R.layout.item_recovery_progress_bar, parent, false);
                holder = new RecoveryProgressBarViewHolder(view);
                break;
            default:
                view = layoutInflater.inflate(R.layout.item_recovery_question, parent, false);
                holder = new RecoveryQuestionViewHolder(view);
                break;
        }
        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        try {
            switch (position) {
                case 0: {
                    GenericInfoViewHolder viewHolder = (GenericInfoViewHolder)holder;
                    viewHolder.infoTextView.setText(R.string.recovery_info);
                    break;
                }
                case 1: {
                    GenericHeadlineViewHolder viewHolder = (GenericHeadlineViewHolder)holder;
                    viewHolder.headlineTextView.setText(R.string.recovery_progress);
                    viewHolder.dividerView.setVisibility(View.GONE);
                    break;
                }
                case 2: {
                    RecoveryProgressBarViewHolder viewHolder = (RecoveryProgressBarViewHolder)holder;
                    String scoreText = context.getString(R.string.recovery_score);
                    scoreText = scoreText.replace("{arg}", "" + viewModel.getSecurityQuestions().size());
                    scoreText = scoreText.replace("{max}", "" + Account.REQUIRED_SECURITY_QUESTIONS);
                    viewHolder.scoreTextView.setText(scoreText);
                    viewHolder.progressBar.setMax(Account.REQUIRED_SECURITY_QUESTIONS);
                    viewHolder.progressBar.setProgress(viewModel.getSecurityQuestions().size(), true);
                    viewHolder.errorTextView.setVisibility(viewModel.getSecurityQuestions().size() < Account.REQUIRED_SECURITY_QUESTIONS ? View.VISIBLE : View.GONE);
                    break;
                }
                case 3: {
                    GenericHeadlineButtonViewHolder viewHolder = (GenericHeadlineButtonViewHolder)holder;
                    viewHolder.headlineTextView.setText(R.string.recovery_questions);
                    viewHolder.buttonImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_add));
                    viewHolder.itemView.setOnClickListener(view -> {
                        Toast.makeText(context, "Add new question", Toast.LENGTH_SHORT).show();
                        //TODO: Add new question!
                    });
                    break;
                }
                default: {
                    RecoveryQuestionViewHolder viewHolder = (RecoveryQuestionViewHolder)holder;
                    SecurityQuestion question = viewModel.getSecurityQuestions().get(position - 4);
                    String[] allQuestions = context.getResources().getStringArray(R.array.security_questions);
                    viewHolder.questionTextView.setText(allQuestions[question.getQuestion()]);
                    viewHolder.answerTextView.setText(question.getAnswer());
                    viewHolder.containerAnswer.setVisibility(question.isExpanded() ? View.VISIBLE : View.GONE);
                    viewHolder.itemView.setOnClickListener(view -> {
                        if (!recyclerView.isAnimating()) {
                            viewHolder.containerAnswer.setVisibility(question.isExpanded() ? View.GONE : View.VISIBLE);
                            question.setExpanded(!question.isExpanded());
                            notifyItemChanged(position);
                        }
                    });
                    break;
                }
            }
        }
        catch (ClassCastException e) {
            Log.d(TAG, "Cannot bind view holder at position " + position + ": " + e.getMessage());
        }
    }


    /**
     * Method returns the number of items within the recycler view.
     *
     * @return  Number of items within the adapter.
     */
    @Override
    public int getItemCount() {
        return viewModel.getSecurityQuestions().size() + 4;
    }


    /**
     * Method returns the view type for the specified position.
     *
     * @param position  Position to query
     * @return          View type for the specified position.
     */
    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case 0:
                return TYPE_GENERIC_INFO;
            case 1:
                return TYPE_GENERIC_HEADLINE;
            case 2:
                return TYPE_RECOVERY_PROGRESS_BAR;
            case 3:
                return TYPE_GENERIC_HEADLINE_BUTTON;
            default:
                return TYPE_RECOVERY_QUESTION;
        }
    }

}
