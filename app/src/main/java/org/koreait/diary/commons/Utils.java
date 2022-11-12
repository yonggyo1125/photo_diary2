package org.koreait.diary.commons;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;


public class Utils {
    public static Bitmap getBitmapFromURL(String src) {
        Bitmap bitmap = null;
        try {
            URL url = new URL(src);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            
            try (BufferedInputStream bis = new BufferedInputStream(conn.getInputStream())) {
                bitmap = BitmapFactory.decodeStream(bis);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }
}
