package com.incept.kidlearn.alphabet;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.incept.kidlearn.R;

/**
 * Created by Hardik Patel on 12/03/18.
 */

public class AlphabetFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View alphabetView = inflater.inflate(R.layout.layout_fragment, null);

        return alphabetView;
    }
}
