package de.passwordvault.view.activities;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import java.util.ArrayList;
import de.passwordvault.R;
import de.passwordvault.view.utils.adapters.EnterSecurityAnswerRecyclerViewAdapter;
import de.passwordvault.view.utils.components.PasswordVaultBaseActivity;
import de.passwordvault.viewmodel.activities.SecurityQuestionViewModel;


/**
 * Class implements the activity through which the user can enter the answers to security questions
 * in order to restore the master password.
 *
 * @author  Christian-2003
 * @version 3.6.0
 */
public class SecurityQuestionActivity extends PasswordVaultBaseActivity {

    /**
     * Class implements a callback for when the displayed security question is changed.
     */
    private class OnPageChangeCallback extends ViewPager2.OnPageChangeCallback {

        /**
         * Attribute stores the index of the last security question.
         */
        private final int lastPage;


        /**
         * Constructor instantiates a new on page change callback.
         *
         * @param numberOfPages Number of questions.
         */
        public OnPageChangeCallback(int numberOfPages) {
            super();
            this.lastPage = numberOfPages - 1;
        }


        /**
         * Method is called whenever the page is scrolled.
         * @param position              Position index of the first page currently being displayed.
         *                              Page position+1 will be visible if positionOffset is nonzero.
         * @param positionOffset        Value from [0, 1) indicating the offset from the page at
         *                              position.
         * @param positionOffsetPixels  Value in pixels indicating the offset from position.
         */
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }


        /**
         * Method is called whenever a new page is selected.
         *
         * @param position  Position index of the new selected page.
         */
        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
            previousButton.setEnabled(position != 0);
            nextButton.setEnabled(position != lastPage);
        }


        /**
         * Method is called whenever the scroll state changes.
         *
         * @param state New scroll state.
         */
        @Override
        public void onPageScrollStateChanged(int state) {
            super.onPageScrollStateChanged(state);
        }

    }


    /**
     * Attribute stores the view model of the activity.
     */
    private SecurityQuestionViewModel viewModel;

    /**
     * Attribute stores the adapter for the security questions.
     */
    private EnterSecurityAnswerRecyclerViewAdapter adapter;

    /**
     * Attribute stores the button with which to get to the next question.
     */
    private Button previousButton;

    /**
     * Attribute stores the button with which to get to the previous question.
     */
    private Button nextButton;


    /**
     * Method is called whenever the activity is created.
     *
     * @param savedInstanceState    Previously saved state of the instance.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_question);
        viewModel = new ViewModelProvider(this).get(SecurityQuestionViewModel.class);

        //Back button:
        findViewById(R.id.button_back).setOnClickListener(view -> finish());

        //View pager and tab layout:
        ArrayList<String> questions = viewModel.getQuestions();
        ViewPager2 viewPager = findViewById(R.id.view_pager);
        viewPager.registerOnPageChangeCallback(new OnPageChangeCallback(questions.size()));
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        adapter = new EnterSecurityAnswerRecyclerViewAdapter(questions);
        viewPager.setAdapter(adapter);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {}).attach();

        //Add spacing between dots below view pager:
        int margin = getResources().getDimensionPixelSize(R.dimen.default_margin);
        for (int i = 0; i < tabLayout.getTabCount() - 1; i++) {
            View tab = ((ViewGroup)tabLayout.getChildAt(0)).getChildAt(i);
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams)tab.getLayoutParams();
            layoutParams.setMargins(0, 0, margin, 0);
            tab.requestLayout();
        }

        //Buttons of view pager:
        previousButton = findViewById(R.id.button_previous);
        previousButton.setOnClickListener(view -> viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true));
        nextButton = findViewById(R.id.button_next);
        nextButton.setOnClickListener(view -> viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true));
    }

}