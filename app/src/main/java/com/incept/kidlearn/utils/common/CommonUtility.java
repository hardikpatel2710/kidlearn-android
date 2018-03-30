package com.incept.kidlearn.utils.common;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import com.incept.kidlearn.FlavorSpecific;

/**
 * Created by Hardik Patel on 08/03/18.
 */

public class CommonUtility {
    private static final String CLASS_NAME = "PreferencesHelper: ";

    /**
     * Hide Android soft keyboard.
     *
     * @param mainActivity
     */
    public static void hideKeyboard(Activity mainActivity) {
        Log.i(FlavorSpecific.APP_LOG_TAG, CLASS_NAME + "hideKeyboard().");

        InputMethodManager inputMethodManager = (InputMethodManager) mainActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }
}
