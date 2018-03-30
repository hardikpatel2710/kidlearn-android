package com.incept.kidlearn;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.incept.kidlearn.alphabet.AlphabetFragment;
import com.incept.kidlearn.profile.ProfileFragment;
import com.incept.kidlearn.utils.storage.PreferencesHelper;

/**
 * Created by Hardik Patel on 05/01/18.
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int PERMISSION_CAMERA = 101;
    public static final int PERMISSION_WRITE = 102;
    private final String CLASS_NAME = "MainActivity: ";
    private DrawerLayout drawerLayout = null;
    private Toolbar appToolbar = null;

    private DataReceiver dataReceiver = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set MainActivity's content view as layout_main.
        setContentView(R.layout.layout_main);

        // Get references to XML views.
        getViewReferences();

        // Load fragment.
        loadFragment();
    }

    private void getViewReferences() {
        // Set application tool bar.
        appToolbar = findViewById(R.id.app_toolbar);
        setSupportActionBar(appToolbar);

        drawerLayout = findViewById(R.id.navigation_drawer);

        // Toolbar view references.
        ImageView toolbarMenu = findViewById(R.id.btn_toolbar_menu);
        ImageView toolbarBack = findViewById(R.id.btn_toolbar_back);

        toolbarMenu.setOnClickListener(this);
        toolbarBack.setOnClickListener(this);
    }

    private void loadFragment() {
        Log.i(FlavorSpecific.APP_LOG_TAG, CLASS_NAME + "loadFragment().");

        Object firstName = null;
        Object lastName = null;
        Object dateOfBirth = null;

        // Read profile details from shared preferences.
        firstName = PreferencesHelper.readFromPreferences(this,
                ProfileFragment.FIRST_NAME_PREFS_KEY, PreferencesHelper.PREFS_TYPE_STRING);
        lastName = PreferencesHelper.readFromPreferences(this,
                ProfileFragment.LAST_NAME_PREFS_KEY, PreferencesHelper.PREFS_TYPE_STRING);
        dateOfBirth = PreferencesHelper.readFromPreferences(this,
                ProfileFragment.DATE_OF_BIRTH_PREFS_KEY, PreferencesHelper.PREFS_TYPE_STRING);

        // If profile details are not stored then load profile fragment and hide navigation drawer icon.
        if (firstName == null || lastName == null || dateOfBirth == null) {
            Log.d(FlavorSpecific.APP_LOG_TAG, CLASS_NAME + "loadFragment() - Profile details " +
                    "are not present, loading ProfileFragment.");

            ProfileFragment profileFragment = new ProfileFragment();

            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(R.id.fragment_container, profileFragment);
            transaction.commit();

            return;
        }

        // Otherwise, load alphabet fragment .
        Log.d(FlavorSpecific.APP_LOG_TAG, CLASS_NAME + "loadFragment() - Profile details " +
                "are present, loading AlphabetFragment.");

        AlphabetFragment alphabetFragment = new AlphabetFragment();

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.fragment_container, alphabetFragment);
        transaction.commit();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_toolbar_menu:
                openNavigationDrawer();
                break;
            case R.id.btn_toolbar_back:

                break;
            case R.id.btn_navigation_back:
                closeNavigationDrawer();
                break;
        }
    }

    private void openNavigationDrawer() {
        // If navigation drawer is closed, open it.
        if (!drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.openDrawer(Gravity.LEFT);
        }

        // Navigation drawer view references.
        ImageView drawerBack = findViewById(R.id.btn_navigation_back);
        drawerBack.setOnClickListener(this);
    }

    private void closeNavigationDrawer() {
        // If navigation drawer is open, close it.
        if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.closeDrawer(Gravity.LEFT);
        }
    }

    /**
     * Method to set DataReceiver by Fragment. Fragments should set receiver using this method.
     *
     * @param dataReceiver
     */
    public void setDataReceiver(DataReceiver dataReceiver) {
        Log.i(FlavorSpecific.APP_LOG_TAG, CLASS_NAME + "setDataReceiver().");
        this.dataReceiver = dataReceiver;
    }

    /**
     * Generic method to request permission from user to access device resources.
     *
     * @param permission  - Permission which needs to be granted.
     * @param requestCode - Request code for permission to be granted.
     */
    public void requestUserPermission(String permission, int requestCode) {
        Log.i(FlavorSpecific.APP_LOG_TAG, CLASS_NAME + "requestUserPermission().");

        // If permission is already granted by user, return true.
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            Log.d(FlavorSpecific.APP_LOG_TAG, CLASS_NAME + "requestUserPermission() - "
                    + permission + " is already granted.");

            if (dataReceiver != null) {
                dataReceiver.onReceived(true, requestCode);
            }

            return;
        }

        // If permission is not granted. Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            // Show an explanation to the user *asynchronously* -- don't block this thread waiting for
            // the user's response! After the user sees the explanation, try again to request the permission.
            switch (requestCode) {
                case PERMISSION_CAMERA:
                    Toast.makeText(this, getString(R.string.permission_camera), Toast.LENGTH_LONG).show();
                    break;
                case PERMISSION_WRITE:
                    Toast.makeText(this, getString(R.string.permission_write), Toast.LENGTH_LONG).show();
                    break;
            }
        } else {
            Log.d(FlavorSpecific.APP_LOG_TAG, CLASS_NAME + "requestUserPermission() - " +
                    "requesting " + permission + " permission.");

            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i(FlavorSpecific.APP_LOG_TAG, CLASS_NAME + "onRequestPermissionsResult().");

        // If Fragment has not set any data receiver, return.
        if (dataReceiver == null) {
            Log.e(FlavorSpecific.APP_LOG_TAG, CLASS_NAME + "onRequestPermissionsResult - " +
                    "Data receiver is not set by Fragment.");
            return;
        }

        switch (requestCode) {
            case PERMISSION_CAMERA:
                // If request is cancelled, result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(FlavorSpecific.APP_LOG_TAG, CLASS_NAME + "onRequestPermissionsResult - " +
                            "Camera permission granted by user.");

                    // Permission was granted, return TRUE to fragment.
                    dataReceiver.onReceived(true, PERMISSION_CAMERA);
                } else {
                    Log.d(FlavorSpecific.APP_LOG_TAG, CLASS_NAME + "onRequestPermissionsResult - " +
                            "Camera permission denied by user.");

                    // Permission denied, return FALSE to fragment.
                    dataReceiver.onReceived(false, PERMISSION_CAMERA);
                }
                return;

            case PERMISSION_WRITE:
                // If request is cancelled, result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(FlavorSpecific.APP_LOG_TAG, CLASS_NAME + "onRequestPermissionsResult - " +
                            "External storage write permission granted by user.");

                    // Permission was granted, return TRUE to fragment.
                    dataReceiver.onReceived(true, PERMISSION_WRITE);
                } else {
                    Log.d(FlavorSpecific.APP_LOG_TAG, CLASS_NAME + "onRequestPermissionsResult - " +
                            "External storage write permission denied by user.");

                    // Permission denied, return FALSE to fragment.
                    dataReceiver.onReceived(false, PERMISSION_WRITE);
                }
                return;
        }
    }

    /**
     * Interface for providing callback to Fragments after permission is granted by user.
     */
    public interface DataReceiver {
        void onReceived(boolean isGranted, int permissionCode);
    }
}
