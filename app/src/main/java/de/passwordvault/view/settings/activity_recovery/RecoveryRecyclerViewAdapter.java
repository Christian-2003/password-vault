package de.passwordvault.view.settings.activity_recovery;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.passwordvault.R;
import de.passwordvault.view.utils.recycler_view.RecyclerViewAdapter;

public class RecoveryRecyclerViewAdapter extends RecyclerViewAdapter<RecoveryViewModel> {

    /**
     * Class models the view holder for the item displaying the progress of the recovery setup.
     */
    public static class RecoveryProgressViewHolder extends RecyclerView.ViewHolder {

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
        public RecoveryProgressViewHolder(@NonNull View itemView) {
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


    public RecoveryRecyclerViewAdapter(@NonNull Context context, @NonNull RecoveryViewModel viewModel) {
        super(context, viewModel);
    }

}
