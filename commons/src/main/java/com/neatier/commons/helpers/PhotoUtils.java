/*
 * Copyright (C) 2017 Extremenet Ltd., All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 *  Proprietary and confidential.
 *  All information contained herein is, and remains the property of Extremenet Ltd.
 *  The intellectual and technical concepts contained herein are proprietary to Extremenet Ltd.
 *   and may be covered by U.S. and Foreign Patents, pending patents, and are protected
 *  by trade secret or copyright law. Dissemination of this information or reproduction of
 *  this material is strictly forbidden unless prior written permission is obtained from
 *   Extremenet Ltd.
 *
 */

package com.neatier.commons.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.FileProvider;
import com.neatier.commons.exception.InternalErrorException;
import java.io.File;
import rx.Observable;
import trikita.log.Log;

import static android.app.Activity.RESULT_OK;

/**
 * Contains helper functions using the Android Camera and photos stored on the device.
 *
 * @author László Gálosi
 * @since 22/03/17
 */
public class PhotoUtils {
    public static final String EXTRA_OUTPUT_PATH = "CameraOutputPath";

    public static final String INTENT_ACTION_HW_CHOOSER = "android.intent.action.hwCHOOSER";

    /**
     * Returns the result {@link Uri} from the specified {@link Intent} as a result for
     * {@link Intent#ACTION_CHOOSER} or {@link Intent#ACTION_PICK} or {@link
     * MediaStore#ACTION_IMAGE_CAPTURE}
     * intent{@link Activity#onActivityResult(int, int, Intent)} handling all device specific cases
     * when the resulting intent is null or it's action is null. The fallbackBundle contains, the
     * {@link MediaStore#EXTRA_OUTPUT} parameter for the ACTION_IMAGE_CAPTURE intent which
     * determines
     * the captured photo file location as an Uri.
     */
    @Nullable public static Uri imageUriFromResultIntent(int resultCode, Intent data,
          final BundleWrapper fallbackBundle) {
        if (resultCode != RESULT_OK) {
            return null;
        }
        String action = MediaStore.ACTION_IMAGE_CAPTURE;
        if (data != null && Intent.ACTION_PICK.equals(data.getAction())) {
            //it was a pick action with a valid picture.
            action = Intent.ACTION_PICK;
        }
        switch (action) {
            case Intent.ACTION_PICK:
                return data.getData();
            case MediaStore.ACTION_IMAGE_CAPTURE:
                if (data != null && data.getData() != null) {
                    return data.getData();
                }
                return (Uri) fallbackBundle.getAs(MediaStore.EXTRA_OUTPUT, Parcelable.class);
        }
        return null;
    }

    /**
     * Determines the result action from the specified {@link Intent} as a result for
     * {@link Intent#ACTION_CHOOSER} or {@link Intent#ACTION_PICK} or {@link
     * MediaStore#ACTION_IMAGE_CAPTURE}
     * intent{@link Activity#onActivityResult(int, int, Intent)} handling all device specific cases
     * when the resulting intent is null or it's action is null. The fallbackBundle contains, the
     * {@link PhotoUtils#EXTRA_OUTPUT_PATH} parameter which contains the captured photo file
     * location as file path. So if any null cases mentioned above, it checks whether the file is
     * exists and
     * contains data, and returns {@link MediaStore#ACTION_IMAGE_CAPTURE} or the specified intent's
     * action if not null.
     */
    @Nullable public static String actionOfResultIntent(int resultCode, Intent resultIntent,
          final BundleWrapper fallbackBundle, Context context) {
        final Uri mediaUri =
              PhotoUtils.imageUriFromResultIntent(resultCode, resultIntent, fallbackBundle);
        final File fileFromUri = getFileFromContentUri(mediaUri, fallbackBundle, context);
        //If the file created via the chooser intent is exist, and has length
        // determines that this was taken by the camera.
        if (fileFromUri.exists() && fileFromUri.length() > 0) {
            return resultIntent != null && resultIntent.getAction() != null
                   ? resultIntent.getAction()
                   : MediaStore.ACTION_IMAGE_CAPTURE;
        }
        return resultIntent != null ? resultIntent.getAction() : null;
    }

    /**
     * Returns a File from the given content Uri.
     *
     * @param contentUri the content Uri to extract the File
     * @param extraBundle bundle containing {@link #EXTRA_OUTPUT_PATH}
     * @param context context
     * @see #extractFileFromContentUri(Uri, String, Context)
     */
    public static @NonNull File getFileFromContentUri(final Uri contentUri,
          BundleWrapper extraBundle, final Context context) {
        String fileExtraPath = extraBundle.getAs(EXTRA_OUTPUT_PATH, String.class);
        return extractFileFromContentUri(contentUri, fileExtraPath, context);
    }

    /**
     * Returns a File from the given content Uri and extra file path.
     *
     * @param contentUri the content Uri to extract the File
     * @param fileExtraPath the file path optional to create the File from.
     * @param context context
     * @see #extractFileFromContentUri(Uri, String, Context)
     */
    public static @NonNull File extractFileFromContentUri(final Uri contentUri,
          final @Nullable String fileExtraPath,
          final Context context) {
        Log.d("extractFileFromContentUri", contentUri, fileExtraPath);
        if (fileExtraPath != null) {
            return new File(fileExtraPath);
        }
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        CursorLoader loader =
              new CursorLoader(context, contentUri, filePathColumn, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String filePath = cursor.getString(column_index);
        cursor.close();
        return new File(filePath);
    }

    /**
     * Returns the uri for the specified file provided by the specfied {@link FileProvider}
     * name.
     *
     * @param file the file of which uri is required.
     * @param fileProviderName the {@link FileProvider} name.
     */

    public static Uri getUriForFile(final File file, String fileProviderName,
          Context context) {
        return FileProvider.getUriForFile(context, fileProviderName, file);
    }

    /**
     * Creates and returns an Observable emitting a new Image File with a prefix of {@code IMG_}
     * stored locally on the device.
     *
     * @see Context#getExternalFilesDir(String)
     */
    public static Observable<File> createImageFile(final Context context) {
        // Create an image file name
        String imageFileName = String.format(
              "IMG_%s",
              DateTimeHelper.toStoreableDateString(DateTimeHelper.nowLocal(), "yyyy_MM_dd_HH_mm_ss")
        );

        final File mediaStorageDir = new File(
              context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
              //Environment.getExternalStorageDirectory(),
              context.getPackageName()
        );

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return Observable.error(
                      new InternalErrorException(String.format("Failed to create directory.%s",
                                                               mediaStorageDir.getAbsolutePath())));
            }
        }

        return Observable.just(mediaStorageDir)
                         .flatMap(storageDir -> Observable.just(
                               new File(String.format("%s%s%s.jpg",
                                                      mediaStorageDir.getPath(),
                                                      File.separator,
                                                      imageFileName)
                               )
                         ));
    }

    /**
     * Creates and returns an Observable emitting a new Video File with a prefix of {@code VID_}
     * stored locally on the device.
     *
     * @see Context#getExternalFilesDir(String)
     */
    public static Observable<File> createVideoFile(final Context context) {
        // Create an image file name
        String imageFileName = String.format(
              "VID_%s",
              DateTimeHelper.toStoreableDateString(DateTimeHelper.nowLocal(), "yyyy_MM_dd_HH_mm_ss")
        );

        final File mediaStorageDir = new File(
              context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
              //Environment.getExternalStorageDirectory(),
              context.getPackageName()
        );

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return Observable.error(
                      new InternalErrorException(String.format("Failed to create directory.%s",
                                                               mediaStorageDir.getAbsolutePath())));
            }
        }

        return Observable.just(mediaStorageDir)
                         .flatMap(storageDir -> Observable.just(
                               new File(String.format("%s%s%s.mp4",
                                                      mediaStorageDir.getPath(),
                                                      File.separator,
                                                      imageFileName)
                               )
                         ));
    }
}
