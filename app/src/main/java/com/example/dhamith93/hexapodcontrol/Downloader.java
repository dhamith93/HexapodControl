package com.example.dhamith93.hexapodcontrol;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Downloader extends AsyncTask<String, Integer, Long> {
    @Override
    protected Long doInBackground(String... strings) {
        saveImage();
        return null;
    }

    private Bitmap getImgFromUrl(String src) {
        try {
            java.net.URL url = new java.net.URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            return BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void saveImage() {
        Bitmap bitmap = getImgFromUrl("http://192.168.4.1:8080/?action=snapshot");
        String path = Environment.getExternalStorageDirectory().toString() + "/hexapod_images/";
        OutputStream outputStream = null;

        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String dateTime = formatter.format(date);

        File file = new File(path);

        if (!file.exists())
            file.mkdir();

        try {
            outputStream = new FileOutputStream(file + "/" + dateTime + ".png");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (bitmap != null && outputStream != null) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        }
    }
}
