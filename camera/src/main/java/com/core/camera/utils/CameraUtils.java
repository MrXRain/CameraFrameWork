package com.core.camera.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.*;
import android.hardware.camera2.CameraCharacteristics;
import android.media.ExifInterface;
import android.media.Image;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.OrientationEventListener;
import com.core.camera.option.Facing;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author rain
 * @date 2019/4/17
 */
public class CameraUtils {

    /**
     * @param image preview data
     * @return nv21 data
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static byte[] YUV_420_888toNV21(Image image) {
        byte[] nv21;
        ByteBuffer yBuffer = image.getPlanes()[0].getBuffer();
        ByteBuffer uBuffer = image.getPlanes()[1].getBuffer();
        ByteBuffer vBuffer = image.getPlanes()[2].getBuffer();

        int ySize = yBuffer.remaining();
        int uSize = uBuffer.remaining();
        int vSize = vBuffer.remaining();

        nv21 = new byte[ySize + uSize + vSize];

        //U and V are swapped
        yBuffer.get(nv21, 0, ySize);
        vBuffer.get(nv21, ySize, vSize);
        uBuffer.get(nv21, ySize + vSize, uSize);

        return nv21;
    }

    /**
     * @param data   previewData
     * @param format format
     * @param width  preview width
     * @param height preview height
     * @return worked bitmap
     */
    public static Bitmap yuv2Image(byte[] data, int format, int width, int height) {

        Bitmap bitmap = null;

        YuvImage yuvImage = new YuvImage(data, format, width, height, null);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, width, height), 90, outputStream);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;

        bitmap = BitmapFactory.decodeByteArray(outputStream.toByteArray(), 0, outputStream.size(), options);

        return bitmap;
    }

    /**
     * rotate picture
     *
     * @param bitmap the original picture
     * @return rotated picture
     */
    public static Bitmap rotaingImageView(Context context, Bitmap bitmap, Facing facing) {
        Matrix matrix = new Matrix();

        switch (facing) {
            case FRONT:
                matrix.postScale(-1, 1);
                break;
            case BACK:
                break;
            default:
                break;
        }

        if (context.getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_PORTRAIT) {
            matrix.postRotate(90);
        }

        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap
                .getHeight(), matrix, true);

        return resizedBitmap;
    }

    /**
     * @param path picture path
     * @return angle
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
                default:
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * rotate picture
     *
     * @param angle  the angle of rotated
     * @param bitmap the picture
     * @return rotated pic
     */
    public static Bitmap rotate(Bitmap bitmap, int angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);
    }

}
