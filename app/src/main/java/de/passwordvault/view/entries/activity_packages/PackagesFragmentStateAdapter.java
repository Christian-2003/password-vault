package de.passwordvault.view.entries.activity_packages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import de.passwordvault.view.entries.activity_packages.fragment_list.PackagesListFragment;
import de.passwordvault.view.entries.activity_packages.fragment_selected.PackagesSelectedFragment;


/**
 * Class implements a fragment state adapter for the fragments within
 * {@link PackagesActivity}.
 *
 * @author  Christian-2003
 * @version 3.5.0
 */
public class PackagesFragmentStateAdapter extends FragmentStateAdapter {

    /**
     * Field stores the number of pages that the adapter can handle.
     */
    private static final int PAGE_COUNT = 2;


    /**
     * Attribute stores the fragments that are displayed by the view pager of this adapter.
     */
    private final Fragment[] fragments;


    /**
     * Constructor instantiates a new fragment state adapter for a view pager within the passed
     * activity.
     *
     * @param context               Activity in which the view pager is located.
     */
    public PackagesFragmentStateAdapter(AppCompatActivity context) {
        super(context);
        fragments = new Fragment[PAGE_COUNT];
    }


    /**
     * Method creates the fragment within the passed position of the view pager. If a position out
     * of bounds is passed, a {@link PackagesSelectedFragment} is returned.
     *
     * @param position  Position of the fragment that shall be generated.
     * @return          Fragment for the view pager at the specified position.
     */
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                if (fragments[position] == null) {
                    fragments[position] = new PackagesListFragment();
                }
            case 0:
            default:
                if (fragments[position] == null) {
                    fragments[position] = new PackagesSelectedFragment();
                }
        }
        return fragments[position];
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


    /**
     * Method returns the fragment at the specified position. If the fragment at the specified
     * position has not been instantiated yet, {@code null} is returned.
     *
     * @param position                      Position of the fragment to return.
     * @return                              Fragment at the specified position.
     * @throws IndexOutOfBoundsException    The specified position is out of bounds.
     */
    public Fragment getItemAt(int position) throws IndexOutOfBoundsException {
        if (position < 0 || position >= PAGE_COUNT) {
            throw new IndexOutOfBoundsException();
        }
        return fragments[position];
    }

}
