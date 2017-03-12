package com.example.zver.affectotask.Tweets;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Zver on 3/11/2017.
 */

public class Tweet {
    String tweetBy;
    String tweet;
    Long tweetTime;
    Double tweetLati;
    Double tweetLongi;
    Bitmap imageUrl;

    public Tweet(String tweetBy, String tweet, Long tweetTime, Double tweetLati, Double tweetLongi, String imageUrl) {
        this.tweetBy = tweetBy;
        this.tweet = tweet;
        this.tweetTime = tweetTime;
        this.tweetLati = tweetLati;
        this.tweetLongi = tweetLongi;
        this.imageUrl = getBitmapFromURL(imageUrl);
    }

    public String getTweetBy() {
        return tweetBy;
    }

    public String getTweet() {
        return tweet;
    }

    public String getTweetTime(){
        Calendar calendar = Calendar.getInstance();
        TimeZone tz = TimeZone.getTimeZone("Lithuania/Vilnius");
        calendar.setTimeInMillis(tweetTime);
        calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date currenTimeZone = (Date) calendar.getTime();
        return sdf.format(currenTimeZone);
    }

    public Double getTweetLati(){
        return tweetLati;
    }

    public Double getTweetLongi(){
        return tweetLongi;
    }

    public Bitmap getImageUrl() {
        return imageUrl;
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

}