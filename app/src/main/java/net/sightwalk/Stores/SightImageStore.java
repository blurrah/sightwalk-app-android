package net.sightwalk.Stores;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import net.sightwalk.Models.Sight;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Frank on 1/14/2016.
 */
public class SightImageStore {

    private static SightImageStore singleInstance;
    private Context context;

    public static SightImageStore getSingleInstance(Context context) {
        if (SightImageStore.singleInstance == null) {
            SightImageStore.singleInstance = new SightImageStore(context);
        }
        return SightImageStore.singleInstance;
    }

    private SightImageStore(Context context) {
        this.context = context;
    }

    public File renameImage(String from, Sight to) {
        return renameImage(from, Integer.toString(to.id));
    }

    public File renameImage(String from, String to) {
        File file = getOutputMediaFile(from);
        File toFile = getOutputMediaFile(to);
        if (file != null && toFile != null) {
            file.renameTo(toFile);
            return toFile;
        }
        return null;
    }

    public File scaleAndCompressImage(String image, String compressed) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(String.valueOf(getOutputMediaFile(image)), options);

        File pictureFile = getOutputMediaFile(compressed);

        // scale
        Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 800, 800, false);
        storeImage(scaled, compressed);
        return getOutputMediaFile(compressed);
    }

    public File storeImage(Bitmap image, Sight sight) {
        return storeImage(image, Integer.toString(sight.id));
    }

    public File storeImage(Bitmap image, String identifier) {
        File pictureFile = getOutputMediaFile(identifier);
        if (pictureFile == null) {
            Log.d("SightImageStore", "Error creating media file, check storage permissions: ");
            return null;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.JPEG, 70, fos);
            fos.close();
            return pictureFile;
        } catch (FileNotFoundException e) {
            Log.d("SightImageStore", "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d("SightImageStore", "Error accessing file: " + e.getMessage());
        }
        return null;
    }

    public FileInputStream readImage(String identifier) {
        File file = getOutputMediaFile(identifier);
        if (file != null) {
            try {
                return new FileInputStream(file);
            } catch (FileNotFoundException e) {
                Log.d("SightImageStore", "could not read file");
            }
        }
        return null;
    }

    /**
     * Create a File for saving an image or video
     */
    public File getOutputMediaFile(String id) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + context.getPackageName()
                + "/Sights");

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        // Create a media file name
        File mediaFile;
        String mImageName = id + ".jpg";
        Log.d("filepath", mediaStorageDir.getPath() + File.separator + mImageName);
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);

        return mediaFile;
    }
}
