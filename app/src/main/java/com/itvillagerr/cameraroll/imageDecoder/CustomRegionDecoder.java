package com.itvillagerr.cameraroll.imageDecoder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;

import com.davemorrissey.labs.subscaleview.decoder.ImageRegionDecoder;

import java.io.InputStream;

import com.itvillagerr.cameraroll.data.Settings;

//inspired by https://gist.github.com/davemorrissey/e2781ba5b966c9e95539
//simple ImageRegionDecoder to have control over Bitmap.Config
public class CustomRegionDecoder implements ImageRegionDecoder {

    private BitmapRegionDecoder decoder;
    private BitmapFactory.Options options;
    private final Object decoderLock = new Object();

    @Override
    public Point init(Context context, Uri uri) throws Exception {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        decoder = BitmapRegionDecoder.newInstance(inputStream, false);
        options = new BitmapFactory.Options();
        boolean use8BitColor = Settings.getInstance(context).use8BitColor();
        options.inPreferredConfig = use8BitColor ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        return new Point(this.decoder.getWidth(), this.decoder.getHeight());
    }

    @Override
    public Bitmap decodeRegion(Rect rect, int sampleSize) {
        synchronized (this.decoderLock) {
            options.inSampleSize = sampleSize;
            Bitmap bitmap = this.decoder.decodeRegion(rect, options);
            if (bitmap == null) {
                throw new RuntimeException("Region decoder returned null bitmap - image format may not be supported");
            } else {
                return bitmap;
            }
        }
    }

    @Override
    public boolean isReady() {
        return this.decoder != null && !this.decoder.isRecycled();
    }

    @Override
    public void recycle() {
        this.decoder.recycle();
    }
}
