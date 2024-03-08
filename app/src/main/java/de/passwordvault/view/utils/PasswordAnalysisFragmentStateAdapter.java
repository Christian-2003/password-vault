package de.passwordvault.view.utils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import de.passwordvault.view.fragments.PasswordAnalysisDuplicatesFragment;
import de.passwordvault.view.fragments.PasswordAnalysisGeneralFragment;
import de.passwordvault.view.fragments.PasswordAnalysisListFragment;


/**
 * Class implements a fragment state adapter for the fragments within the
 * {@link de.passwordvault.view.activities.PasswordAnalysisActivity}.
 *
 * @author  Christian-2003
 * @version 3.4.0
 */
public class PasswordAnalysisFragmentStateAdapter extends FragmentStateAdapter {

    /**
     * Field stores the number of pages that the adapter can handle.
     */
    private static final int PAGE_COUNT = 3;


    /**
     * Constructor instantiates a new fragment state adapter for a view pager within the passed
     * activity.
     *
     * @param context               Activity in which the view pager is located.
     */
    public PasswordAnalysisFragmentStateAdapter(AppCompatActivity context) {
        super(context);
    }


    /**
     * Method creates the fragment within the passed position of the view pager. If a position out
     * of bounds is passed, a {@link PasswordAnalysisGeneralFragment} is returned.
     *
     * @param position  Position of the fragment that shall be generated.
     * @return          Fragment for the view pager at the specified position.
     */
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 2:
                return new PasswordAnalysisDuplicatesFragment();
            case 1:
                return new PasswordAnalysisListFragment();
            case 0:
            default:
                return new PasswordAnalysisGeneralFragment();
        }
    }

    /**
     * Method returns the number of items that the adapter handles.
     *
     * @return  Number of handled items.
     */
    @Override
    public int getItemCount() {
        return PAGE_COUNT;
    }

}
