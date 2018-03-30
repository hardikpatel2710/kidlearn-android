package com.incept.kidlearn.utils.storage;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.incept.kidlearn.FlavorSpecific;
import com.incept.kidlearn.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Hardik Patel on 16/02/18.
 */

public class FileHelper {
    private static final String CLASS_NAME = "FileHelper: ";

    /**
     * Write image file to application's private internal storage.
     *
     * @param context     - Application's context.
     * @param fileName    - File name of image.
     * @param imageBitmap - Image bitmap file to be saved.
     */
    public static void writeToInternalStorage(Context context, String fileName, Bitmap imageBitmap) {
        Log.i(FlavorSpecific.APP_LOG_TAG, CLASS_NAME + "writeToInternalStorage().");

        if (fileName == null) {
            Log.e(FlavorSpecific.APP_LOG_TAG, CLASS_NAME + "writeToInternalStorage() - File name not provided - " + fileName);
            return;
        }

        if (imageBitmap == null) {
            Log.e(FlavorSpecific.APP_LOG_TAG, CLASS_NAME + "writeToInternalStorage() - Invalid image file provided - " + imageBitmap);
            return;
        }

        try {
            // Create FileOutputStream object.
            FileOutputStream fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);

            fileOutputStream.flush();
            fileOutputStream.close();
            Log.d(FlavorSpecific.APP_LOG_TAG, CLASS_NAME + "writeToInternalStorage() - File written to internal storage.");
        } catch (FileNotFoundException fnfe) {
            Log.e(FlavorSpecific.APP_LOG_TAG, CLASS_NAME + "writeToInternalStorage() - File not found.");
            // TODO: Firebase Crachlytics.
        } catch (IOException ioe) {
            Log.e(FlavorSpecific.APP_LOG_TAG, CLASS_NAME + "writeToInternalStorage() - Failed to close output stream.");
            // TODO: Firebase Crachlytics.
        }
    }

    /**
     * Write image file to device's external storage.
     *
     * @param context     - Application's context.
     * @param fileName    - File name of image.
     * @param imageBitmap - Image bitmap file to be saved.
     * @return - Absolute path of image stored in external storage.
     */
    public static String writeToExternalStorage(Context context, String fileName, Bitmap imageBitmap) {
        Log.i(FlavorSpecific.APP_LOG_TAG, CLASS_NAME + "writeToExternalStorage().");

        if (fileName == null) {
            Log.e(FlavorSpecific.APP_LOG_TAG, CLASS_NAME + "writeToExternalStorage() - File name not provided - " + fileName);
            return null;
        }

        if (imageBitmap == null) {
            Log.e(FlavorSpecific.APP_LOG_TAG, CLASS_NAME + "writeToExternalStorage() - Invalid image file provided - " + imageBitmap);
            return null;
        }

        // First check if external storage exists on device.
        String storageState = Environment.getExternalStorageState();
        Log.d(FlavorSpecific.APP_LOG_TAG, CLASS_NAME + "writeToExternalStorage() - External storage state - " + storageState);

        // If external storage state is 'mounted', external storage is available.
        if (Environment.MEDIA_MOUNTED.equals(storageState)) {
            Log.d(FlavorSpecific.APP_LOG_TAG, CLASS_NAME + "writeToExternalStorage() - External storage mounted.");
        }

        // If external storage is 'read only', return.
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(storageState)) {
            Log.e(FlavorSpecific.APP_LOG_TAG, CLASS_NAME + "writeToExternalStorage() - External storage is read only.");
            return null;
        }

        String extDirectoryPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        Log.d(FlavorSpecific.APP_LOG_TAG, CLASS_NAME + "writeToExternalStorage() - External Storage Directory - " + extDirectoryPath);

        // Create directory in external storage with application name.
        String appDirectoryPath = extDirectoryPath + File.separator + context.getString(R.string.app_name);

        // Check if directory exists. If not create new.
        File appDirectory = new File(appDirectoryPath);

        if (!appDirectory.exists()) {
            appDirectory.mkdirs();
            Log.d(FlavorSpecific.APP_LOG_TAG, CLASS_NAME + "writeToExternalStorage() - Application directory created - " + appDirectory.getAbsolutePath());
        }

        String bitmapFilePath = null;

        OutputStream outputStream = null;
        try {
            File bitmapFile = new File(appDirectory, fileName);
            bitmapFilePath = bitmapFile.getAbsolutePath();
            Log.d(FlavorSpecific.APP_LOG_TAG, CLASS_NAME + "writeToExternalStorage() - Bitmap file path - " + bitmapFilePath);

            bitmapFile.createNewFile();
            outputStream = new FileOutputStream(bitmapFile);
        } catch (IOException ioe) {
            Log.e(FlavorSpecific.APP_LOG_TAG, CLASS_NAME + "writeToExternalStorage() - Failed to create image file.");
            // TODO: Firebase Crachlytics.
        }

        try {
            // 100 means no compression, the lower you go, the stronger the compression
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException ioe) {
            Log.e(FlavorSpecific.APP_LOG_TAG, CLASS_NAME + "writeToExternalStorage() - Failed to close output stream.");
            // TODO: Firebase Crachlytics.
        }

        return bitmapFilePath;
    }
}
