package com.incept.kidlearn.alphabet;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.incept.kidlearn.FlavorSpecific;
import com.incept.kidlearn.MainActivity;
import com.incept.kidlearn.R;

/**
 * Created by Hardik Patel on 12/03/18.
 */

public class AlphabetFragment extends Fragment {

    private final String CLASS_NAME = "ProfileFragment: ";

    private MainActivity mainActivity = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.i(FlavorSpecific.APP_LOG_TAG, CLASS_NAME + "onCreateView().");

        View alphabetView = inflater.inflate(R.layout.layout_fragment, null);
        mainActivity = (MainActivity) getActivity();

        // Set ToolBar title. Hide ToolBar Menu and Back buttons.
        mainActivity.setToolbarTitle(getString(R.string.alphabets_screen_title));
        mainActivity.showMenuIcon(true);
        mainActivity.showBackIcon(false);

        return alphabetView;
    }

    @Override
    public void onResume() {
        super.onResume();

        Bundle kidProfileArgs = getArguments();
        String navKidName = kidProfileArgs.getString(MainActivity.ARGS_KID_NAME_KEY);
        String navLanguage = kidProfileArgs.getString(MainActivity.ARGS_LANGUAGE_KEY);
        mainActivity.setNavigationDrawer(navKidName, navLanguage);
    }
}
