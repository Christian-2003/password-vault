package de.passwordvault.view.utils;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import de.passwordvault.R;
import de.passwordvault.view.fragments.PasswordAnalysisDuplicatesFragment;
import de.passwordvault.view.fragments.PasswordAnalysisGeneralFragment;
import de.passwordvault.view.fragments.PasswordAnalysisListFragment;


public class PasswordAnalysisFragmentStateAdapter extends FragmentStateAdapter {

    private static final int PAGE_COUNT = 3;


    private final Context context;


    public PasswordAnalysisFragmentStateAdapter(AppCompatActivity context) throws NullPointerException {
        super(context);
        this.context = context;
    }


    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new PasswordAnalysisGeneralFragment();
            case 1:
                return new PasswordAnalysisListFragment();
            case 2:
                return new PasswordAnalysisDuplicatesFragment();
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return PAGE_COUNT;
    }

}
