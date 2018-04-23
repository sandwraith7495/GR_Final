package com.example.sandwraith8.gr_final.service;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.DisplayMetrics;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by sandwraith8 on 08/04/2018.
 */

public class BitmapService {
    private static final BitmapService ourInstance = new BitmapService();

    public static BitmapService getInstance() {
        return ourInstance;
    }

    private BitmapService() {
    }

    public Bitmap decodeSampledBitmapFromFile(String path) { // BEST QUALITY MATCH
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap mBitmap = BitmapFactory.decodeFile(path, options);
        return mBitmap;
    }

    public Bitmap getBitmap(Activity activity, Uri uri) {
        InputStream is = null;
        try {
            is = activity.getContentResolver().openInputStream(uri);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            options.inSampleSize = 2;
            options.inScreenDensity = DisplayMetrics.DENSITY_LOW;
            return BitmapFactory.decodeStream(is, null, options);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if(is!=null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
