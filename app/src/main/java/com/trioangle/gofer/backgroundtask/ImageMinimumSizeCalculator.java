package com.trioangle.gofer.backgroundtask;
/**
 * @package com.trioangle.gofer
 * @subpackage backgroundtask
 * @category ImageCompressAsyncTask
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import static com.trioangle.gofer.utils.CommonMethods.DebuggableLogI;

/*****************************************************************
 Compress Image into MinimumSize
 ****************************************************************/

public class ImageMinimumSizeCalculator {


    public static int[] getMinSquarDimension(Uri imageUri, Context context) {
        int[] imageheightAndWidth = new int[2];

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
            int minimumSize = bitmap.getHeight() / 2;
            imageheightAndWidth[0] = minimumSize;
            imageheightAndWidth[1] = minimumSize;
        } catch (IOException var5) {
            var5.printStackTrace();
        }

        return imageheightAndWidth;
    }

    public static boolean isLandscape(Uri imageUri) {
        int rotate = 0;
        boolean isLandscape = true;

        try {
            File imageFile = new File(imageUri.getPath());
            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt("Orientation", 1);
            switch (orientation) {
                case 3:
                    isLandscape = true;
                    rotate = 180;
                    break;
                case 6:
                    isLandscape = false;
                    rotate = 90;
                    break;
                case 8:
                    isLandscape = false;
                    rotate = 270;
                    break;
                default:
                    break;
            }

            DebuggableLogI("RotateImage", "Exif orientation: " + orientation);
            DebuggableLogI("RotateImage", "Rotate value: " + rotate);
        } catch (Exception var6) {
            var6.printStackTrace();
        }

        return isLandscape;
    }

    public static int[] getMinRectangleDimensions(Uri imageUri, Context context) {
        int[] imageheightAndWidth = new int[2];

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
            imageheightAndWidth[0] = bitmap.getWidth();
            if (bitmap.getHeight() < 300) {
                imageheightAndWidth[1] = 300;
            } else {
                imageheightAndWidth[1] = bitmap.getHeight() / 300 * 300;
            }
        } catch (IOException var4) {
            var4.printStackTrace();
        }

        return imageheightAndWidth;
    }
}
