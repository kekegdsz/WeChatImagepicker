package com.kekegdsz.imagepicker;

public class ImageLoader {

    private volatile static ImageLoader mInstance = null;

    private ImageLoader() {

    }

    public static ImageLoader getInstance() {
        if (mInstance == null) {
            synchronized (ImageLoader.class) {
                if (mInstance == null) {
                    mInstance = new ImageLoader();
                }
            }
        }
        return mInstance;
    }
}
