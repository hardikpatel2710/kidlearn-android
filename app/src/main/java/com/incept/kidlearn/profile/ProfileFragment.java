package com.incept.kidlearn.profile;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.incept.kidlearn.FlavorSpecific;
import com.incept.kidlearn.MainActivity;
import com.incept.kidlearn.R;
import com.incept.kidlearn.utils.common.CommonUtility;
import com.incept.kidlearn.utils.storage.FileHelper;
import com.incept.kidlearn.utils.storage.PreferencesHelper;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Hardik Patel on 06/02/18.
 */

public class ProfileFragment extends Fragment implements View.OnClickListener,
        View.OnFocusChangeListener, MainActivity.DataReceiver {

    // Kid profile shared preferences key name.
    public static final String FIRST_NAME_PREFS_KEY = "firstName";
    public static final String LAST_NAME_PREFS_KEY = "lastName";
    public static final String DATE_OF_BIRTH_PREFS_KEY = "dateOfBirth";
    private static final String KID_PROFILE_SHARED_PREFS_FILE_NAME = "profile_shared_prefs";
    private static final String IMAGE_NAME_PREFS_KEY = "imageName";
    private static final String IMAGE_PATH_PREFS_KEY = "imagePath";
    private static final String PROFILE_IMAGE_FILE_NAME = "ProfileImage.png";
    private static final int CAMERA_REQUEST_CODE = 10001;
    private static final int GALLERY_REQUEST_CODE = 10002;
    private final String CLASS_NAME = "ProfileFragment: ";
    private MainActivity mainActivity = null;

    private CircleImageView imageViewProfileImage = null;
    private EditText editTextFirstName = null;
    private EditText editTextLastName = null;
    private EditText editTextDateOfBirth = null;

    private Bitmap cameraImageBitmap = null;

    // Kid profile details to be saved in shared preferences.
    private String firstName = null;
    private String lastName = null;
    private String imagePath = null;
    private String dateOfBirth = null;

    private boolean isPermissionGranted = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.i(FlavorSpecific.APP_LOG_TAG, CLASS_NAME + "onCreateView().");

        View profileView = inflater.inflate(R.layout.fragment_profile, null);

        // Set ToolBar title.
        mainActivity = (MainActivity) getActivity();
        mainActivity.getSupportActionBar().setTitle(getString(R.string.profile_screen_title));

        // Get views references and attach click listener.
        imageViewProfileImage = profileView.findViewById(R.id.profile_image);
        editTextFirstName = profileView.findViewById(R.id.profile_first_name);
        editTextLastName = profileView.findViewById(R.id.profile_last_name);
        editTextDateOfBirth = profileView.findViewById(R.id.profile_dob);
        Button buttonSaveProfile = profileView.findViewById(R.id.profile_save_button);

        imageViewProfileImage.setOnClickListener(this);
        editTextDateOfBirth.setOnClickListener(this);
        editTextDateOfBirth.setOnFocusChangeListener(this);
        buttonSaveProfile.setOnClickListener(this);

        return profileView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.i(FlavorSpecific.APP_LOG_TAG, CLASS_NAME + "onViewCreated().");

        // Set data receiver listener.
        mainActivity.setDataReceiver(this);
    }

    @Override
    public void onClick(View view) {
        Log.i(FlavorSpecific.APP_LOG_TAG, CLASS_NAME + "onClick().");

        switch (view.getId()) {
            case R.id.profile_image:
                // Show a dialog to select image from gallery or take picture using camera.
                showImageSelectorDialog();
                break;

            case R.id.profile_dob:
                // Show date picker dialog.
                showDatePickerDialog();
                break;

            case R.id.profile_save_button:
                // Validate all provided inputs.
                if (validateKidProfile()) {
                    // Save kid profile in activity preferences.
                    saveProfileInPrefs();

                    // NOTE: For learning purpose writing data to multiple shared preferences.

                    // Save kid profile in shared preferences.
                    saveProfileInSharedPrefs();

                    // Save kid profile in database.
                }
                break;
        }
    }

    /**
     * Method for showing alert dialog to user to select image from gallery or take image using camera.
     */
    private void showImageSelectorDialog() {
        Log.i(FlavorSpecific.APP_LOG_TAG, CLASS_NAME + "showImageSelectorDialog().");

        String[] imageDialogItems = {getString(R.string.profile_image_dialog_camera),
                getString(R.string.profile_image_dialog_gallery)};

        AlertDialog.Builder imageDialog = new AlertDialog.Builder(mainActivity);

        imageDialog.setTitle(getString(R.string.profile_image_dialog_title));
        imageDialog.setItems(imageDialogItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int index) {
                switch (index) {
                    case 0:
                        onCameraClick();
                        break;
                    case 1:
                        onGalleryClick();
                        break;
                }
            }
        });

        imageDialog.show();
    }

    /**
     * Perform actions when user want to take profile picture using camera.
     */
    private void onCameraClick() {
        Log.i(FlavorSpecific.APP_LOG_TAG, CLASS_NAME + "onCameraClick().");

        // Check if user has granted permission to use camera.
        mainActivity.requestUserPermission(Manifest.permission.CAMERA, MainActivity.PERMISSION_CAMERA);
    }

    /**
     * Perform actions when user want to select profile picture from gallery.
     */
    private void onGalleryClick() {
        Log.i(FlavorSpecific.APP_LOG_TAG, CLASS_NAME + "onGalleryClick().");

        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // Check if any application exists to resolve gallery intent.
        if (galleryIntent.resolveActivity(mainActivity.getPackageManager()) != null) {
            startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
        }
    }

    @Override
    public void onFocusChange(View view, boolean isFocused) {
        Log.i(FlavorSpecific.APP_LOG_TAG, CLASS_NAME + "onFocusChange().");

        if (view.getId() == R.id.profile_dob && isFocused) {
            showDatePickerDialog();
        }
    }

    private void showDatePickerDialog() {
        Log.i(FlavorSpecific.APP_LOG_TAG, CLASS_NAME + "showDatePickerDialog().");

        // Hide keyboard if its open.
        CommonUtility.hideKeyboard(mainActivity);

        DatePickerDialog.OnDateSetListener dateOfBirthListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int dobYear, int dobMonth, int dobDay) {
                Log.d(FlavorSpecific.APP_LOG_TAG, CLASS_NAME + "showDatePickerDialog() - DoB Year - " + dobYear);
                Log.d(FlavorSpecific.APP_LOG_TAG, CLASS_NAME + "showDatePickerDialog() - DoB Month - " + dobMonth);
                Log.d(FlavorSpecific.APP_LOG_TAG, CLASS_NAME + "showDatePickerDialog() - DoB Day - " + dobDay);

                dateOfBirth = dobDay + "/" + (dobMonth + 1) + "/" + dobYear;
                editTextDateOfBirth.setText(dateOfBirth);
            }
        };

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
        int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(mainActivity, dateOfBirthListener, currentYear, currentMonth, currentDay).show();
    }

    @Override
    public void onReceived(boolean isGranted, int permissionCode) {
        Log.i(FlavorSpecific.APP_LOG_TAG, CLASS_NAME + "onReceived().");

        if (!isGranted) {
            return;
        }

        switch (permissionCode) {
            case MainActivity.PERMISSION_CAMERA:
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

                // Check if any application exists to resolve camera intent.
                if (cameraIntent.resolveActivity(mainActivity.getPackageManager()) != null) {
                    startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                }

                break;
            case MainActivity.PERMISSION_WRITE:
                isPermissionGranted = true;

                // For the very first time when write storage permission dialog is displayed to user,
                // writing file to external and internal storage will happen through this block.
                // Otherwise, it will happen through onActivityResult() block.
                if (cameraImageBitmap != null) {
                    // Store image to application internal storage.
                    FileHelper.writeToInternalStorage(mainActivity.getApplicationContext(),
                            PROFILE_IMAGE_FILE_NAME, cameraImageBitmap);

                    // NOTE: For learning purpose writing file to external storage.

                    // Generate file name based on timestamp.
                    String fileName = new Date().getTime() + ".png";

                    // Store image to external storage.
                    imagePath = FileHelper.writeToExternalStorage(mainActivity.getApplicationContext(),
                            fileName, cameraImageBitmap);
                }

                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i(FlavorSpecific.APP_LOG_TAG, CLASS_NAME + "onActivityResult().");

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            // Check if user has granted permission to use external storage.
            mainActivity.requestUserPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, MainActivity.PERMISSION_WRITE);

            // Get thumbnail and show it on image view.
            Bundle extras = data.getExtras();
            cameraImageBitmap = (Bitmap) extras.get("data");
            imageViewProfileImage.setImageBitmap(cameraImageBitmap);

            // If write permission is already granted writing to internal and external storage will
            // happen through this block. First time, it happens through onReceived(). As we show
            // permission dialog to user, we are not sure when user will grant, call gets blocked.
            if (isPermissionGranted) {
                // Store image to application internal storage.
                FileHelper.writeToInternalStorage(mainActivity.getApplicationContext(),
                        PROFILE_IMAGE_FILE_NAME, cameraImageBitmap);

                // NOTE: For learning purpose writing file to external storage.

                // Generate file name based on timestamp.
                String fileName = new Date().getTime() + ".png";

                // Store image to external storage.
                imagePath = FileHelper.writeToExternalStorage(mainActivity.getApplicationContext(),
                        fileName, cameraImageBitmap);
            }
        } else if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
            // Get selected image and show it on image view.
            Uri imageUri = data.getData();

            try {
                Bitmap galleryImageBitmap = MediaStore.Images.Media.getBitmap(mainActivity.getContentResolver(), imageUri);
                imageViewProfileImage.setImageBitmap(galleryImageBitmap);

                // Save selected image to application internal storage.
                FileHelper.writeToInternalStorage(mainActivity.getApplicationContext(),
                        PROFILE_IMAGE_FILE_NAME, galleryImageBitmap);

                // NOTE: For learning purpose writing file to external storage.

                // Generate file name based on timestamp.
                String fileName = new Date().getTime() + ".png";

                // Store image to external storage.
                imagePath = FileHelper.writeToExternalStorage(mainActivity.getApplicationContext(),
                        fileName, galleryImageBitmap);
            } catch (IOException ioe) {
                Log.e(FlavorSpecific.APP_LOG_TAG, CLASS_NAME + "onActivityResult() - Error reading image from gallery.");
                // TODO: Firebase Crachlytics.
            }
        }
    }

    private boolean validateKidProfile() {
        Log.i(FlavorSpecific.APP_LOG_TAG, CLASS_NAME + "validateKidProfile().");

        boolean isProfileValid = true;

        firstName = editTextFirstName.getText().toString();
        lastName = editTextLastName.getText().toString();
        dateOfBirth = editTextDateOfBirth.getText().toString();

        Log.d(FlavorSpecific.APP_LOG_TAG, CLASS_NAME + "validateKidProfile() - First name - " + firstName);
        Log.d(FlavorSpecific.APP_LOG_TAG, CLASS_NAME + "validateKidProfile() - Lirst name - " + lastName);
        Log.d(FlavorSpecific.APP_LOG_TAG, CLASS_NAME + "validateKidProfile() - Date of birth - " + dateOfBirth);

        // Validate first name is provided.
        if (TextUtils.isEmpty(firstName)) {
            Log.d(FlavorSpecific.APP_LOG_TAG, CLASS_NAME + "validateKidProfile() - Kid first name is not provided.");
            Toast.makeText(mainActivity, getString(R.string.provide_first_name), Toast.LENGTH_SHORT).show();
            isProfileValid = false;
        }

        // Validate last name is provided.
        if (TextUtils.isEmpty(lastName)) {
            Log.d(FlavorSpecific.APP_LOG_TAG, CLASS_NAME + "validateKidProfile() - Kid last name is not provided.");
            Toast.makeText(mainActivity, getString(R.string.provide_last_name), Toast.LENGTH_SHORT).show();
            isProfileValid = false;
        }

        // Validate date of birth is provided.
        if (TextUtils.isEmpty(dateOfBirth)) {
            Log.d(FlavorSpecific.APP_LOG_TAG, CLASS_NAME + "validateKidProfile() - Kid date of birth is not provided.");
            Toast.makeText(mainActivity, getString(R.string.provide_date_of_birth), Toast.LENGTH_SHORT).show();
            isProfileValid = false;
        }

        return isProfileValid;
    }

    private void saveProfileInPrefs() {
        Log.i(FlavorSpecific.APP_LOG_TAG, CLASS_NAME + "saveProfileInPrefs().");

        Map<String, Object> data = new HashMap<String, Object>();

        data.put(FIRST_NAME_PREFS_KEY, firstName);
        data.put(LAST_NAME_PREFS_KEY, lastName);
        data.put(DATE_OF_BIRTH_PREFS_KEY, dateOfBirth);
        data.put(IMAGE_NAME_PREFS_KEY, PROFILE_IMAGE_FILE_NAME);
        data.put(IMAGE_PATH_PREFS_KEY, imagePath);

        PreferencesHelper.writeToPreferences(mainActivity, data);
    }

    private void saveProfileInSharedPrefs() {
        Log.i(FlavorSpecific.APP_LOG_TAG, CLASS_NAME + "saveProfileInSharedPrefs().");

        Map<String, Object> data = new HashMap<String, Object>();

        data.put(FIRST_NAME_PREFS_KEY, firstName);
        data.put(LAST_NAME_PREFS_KEY, lastName);
        data.put(DATE_OF_BIRTH_PREFS_KEY, dateOfBirth);
        data.put(IMAGE_NAME_PREFS_KEY, PROFILE_IMAGE_FILE_NAME);
        data.put(IMAGE_PATH_PREFS_KEY, imagePath);

        PreferencesHelper.writeToSharedPreferences(mainActivity.getApplicationContext(),
                KID_PROFILE_SHARED_PREFS_FILE_NAME, data);
    }
}
