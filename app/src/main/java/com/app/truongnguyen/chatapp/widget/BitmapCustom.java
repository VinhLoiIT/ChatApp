package com.app.truongnguyen.chatapp.widget;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class BitmapCustom {

    static public Bitmap myScaleAndCrop(Bitmap bitmap, int maxHeight, int maxWidth) {
        Bitmap temp = myCrop(bitmap);
        return scale(temp, maxHeight, maxWidth);
    }

    static public Bitmap myCrop(Bitmap bitmap) {

        Bitmap croped;
        float scale = 1;

        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);


        int heightOrg = bitmap.getHeight();
        int widthOrg = bitmap.getWidth();
        if (widthOrg < heightOrg) {
            croped = Bitmap.createBitmap(bitmap, 0, (heightOrg - widthOrg) / 2
                    , widthOrg, widthOrg, matrix, true);
        } else {
            croped = Bitmap.createBitmap(bitmap, (widthOrg - heightOrg) / 2
                    , 0, heightOrg, heightOrg, matrix, true);
        }
        return croped;
    }

    static public Bitmap scale(Bitmap bitmap, int maxHeight, int maxWidth) {
        int heigt = bitmap.getHeight();
        int width = bitmap.getWidth();
        float scale = Math.min((float) maxHeight / width, ((float) maxWidth / heigt));

        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        return Bitmap.createBitmap(bitmap, 0, 0, width, heigt, matrix, true);
    }
}
