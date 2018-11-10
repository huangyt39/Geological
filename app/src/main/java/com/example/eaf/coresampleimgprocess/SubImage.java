package com.example.eaf.coresampleimgprocess;

import android.graphics.Bitmap;

public class SubImage {

    private Bitmap bitmap;
    private String index;

    public SubImage(Bitmap image) {
        bitmap = image;
    }

    public SubImage() {
        bitmap = null;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap image) {
        bitmap = image;
    }

}
