package com.example.madiba.venu_alpha.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.util.TypedValue;


import com.example.madiba.venu_alpha.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * Created by Madiba on 11/23/2016.
 */

public class ImageUitls {

    public static final int BYTE = 1;

    public static final int KB = 1024;

    public static final int MB = 1048576;

    public static final int GB = 1073741824;

    public enum MemoryUnit {
        BYTE,
        KB,
        MB,
        GB
    }


    public ImageUitls() {}

    public static long size2Byte(long size, MemoryUnit unit) {
        switch (unit) {
            default:
            case BYTE:
                return size * BYTE;
            case KB:
                return size * KB;
            case MB:
                return size * MB;
            case GB:
                return size * GB;
        }
    }

    public static byte[] bitmap2Bytes(Bitmap bitmap, Bitmap.CompressFormat format,int topLimit) {
        if (bitmap == null) return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(format, topLimit, baos);
        return baos.toByteArray();
    }

    public static Bitmap compress(Bitmap src, Bitmap.CompressFormat format, long topLimit, MemoryUnit unit) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        src.compress(format, 100, os);
        long upperSize = size2Byte(topLimit, unit);
        while (os.toByteArray().length > upperSize) {
            os.reset();
            src.compress(format, 50, os);
        }
        if (!src.isRecycled()) src.recycle();
        return BitmapFactory.decodeStream(new ByteArrayInputStream(os.toByteArray()));
    }

    public static Bitmap rotateBitmap(Bitmap src, int degrees) {
        if (src == null || degrees == 0) return src;
        Matrix matrix = new Matrix();
        matrix.setRotate(degrees, src.getWidth() / 2, src.getHeight() / 2);
        Bitmap res = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        if (!src.isRecycled()) src.recycle();
        return res;
    }

    public static Bitmap notificationResize(Context context, Bitmap currentImage) {
        try {
            int scale = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 64, context.getResources().getDisplayMetrics());

            return Bitmap.createScaledBitmap(currentImage, scale, scale, true);
        } catch (OutOfMemoryError e) {
            return currentImage;
        }
    }

    public static String getPath(Uri uri, Context context) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        String filePath = null;

        try {
            Cursor cursor = context.getContentResolver().query(
                    uri, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            filePath = cursor.getString(columnIndex);
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
            filePath = uri.getPath();
        }

        if (filePath == null) {
            filePath = uri.getPath();
            Log.v("talon_file_path", filePath);
        }

        return filePath;
    }


    public byte[] convertToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }


    public static Bitmap overlayPlay(Bitmap bmp1, Context context) {
        Bitmap bmp2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.applozic_video_play_icon, null);

        Bitmap bmOverlay = Bitmap.createBitmap( bmp1.getWidth(), bmp1.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas2 = new Canvas(bmOverlay);
        canvas2.drawBitmap(bmp1, 0, 0, null);
//        canvas2.drawBitmap(bmp2, Utils.toDP(64, context), Utils.toDP(64, context), null);
        return bmOverlay;
    }

    public static boolean deleteFile(Context context, String fileName)
    {
        return context.deleteFile(fileName);
    }


    public static boolean exists(Context context, String fileName)
    {
        return new File(context.getFilesDir(), fileName).exists();
    }

    public static Bitmap convertGreyImg(Bitmap img) {
        int width = img.getWidth(); // 获取位图的宽
        int height = img.getHeight(); // 获取位图的高

        int[] pixels = new int[width * height]; // 通过位图的大小创建像素点数组

        img.getPixels(pixels, 0, width, 0, 0, width, height);
        int alpha = 0xFF << 24;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int grey = pixels[width * i + j];

                int red = ((grey & 0x00FF0000) >> 16);
                int green = ((grey & 0x0000FF00) >> 8);
                int blue = (grey & 0x000000FF);

                grey = (int) ((float) red * 0.3 + (float) green * 0.59 + (float) blue * 0.11);
                grey = alpha | (grey << 16) | (grey << 8) | grey;
                pixels[width * i + j] = grey;
            }
        }
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        result.setPixels(pixels, 0, width, 0, 0, width, height);
        return result;
    }



}
