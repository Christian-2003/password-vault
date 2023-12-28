package de.passwordvault.view.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import de.passwordvault.R;
import de.passwordvault.view.activities.AddEntryActivity;
import de.passwordvault.view.activities.MainActivity;


/**
 * Class implements the {@linkplain Fragment} that is used as home screen for the application. It is
 * used within the {@linkplain MainActivity}.
 *
 * @author  Christian-2003
 * @version 3.0.0
 */
public class HomeFragment extends Fragment {

    /**
     * Default constructor instantiates a new HomeFragment.
     */
    public HomeFragment() {
        // Required empty public constructor
    }


    /**
     * Method is called whenever the HomeFragment is created or recreated.
     *
     * @param savedInstanceState    Previously saved state of the instance.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    /**
     * Method is called whenever the {@linkplain View} for the HomeFragment is created.
     *
     * @param inflater              LayoutInflater for the fragment.
     * @param container             Container which contains the fragment.
     * @param savedInstanceState    Previously saved state of the instance.
     * @return                      Generated view.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflated = inflater.inflate(R.layout.fragment_home, container, false);

        Button addAccountButton = inflated.findViewById(R.id.home_add_account_button);
        addAccountButton.setOnClickListener(view -> startActivity(new Intent(HomeFragment.this.getActivity(), AddEntryActivity.class)));

        Button viewAccountsButton = inflated.findViewById(R.id.home_show_accounts_button);
        viewAccountsButton.setOnClickListener(view -> {
            FragmentActivity fragmentActivity = HomeFragment.this.getActivity();
            if (fragmentActivity instanceof MainActivity) {
                MainActivity mainActivity = (MainActivity)fragmentActivity;
                mainActivity.switchToFragment(R.id.menu_entries);
            }
            else {
                Toast.makeText(getContext(), "Error, AppCompatActivity not MainActivity class.", Toast.LENGTH_SHORT).show();
            }
        });

        // Inflate the layout for this fragment
        return inflated;
    }

}
