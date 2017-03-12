package com.example.zver.affectotask;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zver.affectotask.Tweets.Bus;
import com.example.zver.affectotask.Tweets.Tweet;
import com.example.zver.affectotask.Tweets.TweetsEvent;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

import static com.example.zver.affectotask.R.id.map;

public class MainActivity extends FragmentActivity implements
        GoogleMap.OnCameraMoveStartedListener,
        GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraMoveCanceledListener,
        GoogleMap.OnCameraIdleListener,
        GoogleMap.OnMarkerClickListener,
        OnMapReadyCallback {

    public final static String CONSUMER_KEY = "aNodxnzCkYfLgBYDYaGH7ZJrY"; // HIDDEN, please obtain your one on twitter developers
    public	final static String CONSUMER_SECRET = "tm3LetXsvDvgasI1gIElu5386SS43UauBzcA4ZBlzSqQ6BDwgi";  // HIDDEN, please obtain your one on twitter developers
    public	final static String ACCESS_TOKEN = "840515893004238848-9VviS3nRbhonTB5xWPXQCsDojrTFvcx";
    public	final static String ACCESS_TOKEN_SECRET = "f5qADvw2wNxvh8VGOqTz3OjWV8tUB3TEoWnkYP2k6nXjt";

    private GoogleMap mMap;
    double northeastLatitude;
    double northeastLongitude;
    double southwestLatitude;
    double southwestLongitude;
    private List<Tweet> tweetsList = new ArrayList<Tweet>();
    SearchOnTwitterStream search;
    EventBus eventBus = new EventBus().getDefault();
    int markersCount = 50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try{
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(map);
            mapFragment.getMapAsync(this);
            search = new SearchOnTwitterStream(this);

        }catch (Exception e){
            Log.e("AAAAA","onCreate " + e.toString());
        }
        // Example of a call to a native method
//    TextView tv = (TextView) findViewById(R.id.sample_text);
//    tv.setText(stringFromJNI());
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        LatLng TutorialsPoint = new LatLng(55.1694, 23.8813);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(TutorialsPoint));
        CameraUpdate center= CameraUpdateFactory.newLatLng(new LatLng(55.1694, 23.8813));
        CameraUpdate zoom=CameraUpdateFactory.zoomTo(7);
        mMap.moveCamera(center);
        mMap.animateCamera(zoom);
        mMap.setOnCameraIdleListener(this);
        mMap.setOnCameraMoveStartedListener(this);
        mMap.setOnCameraMoveListener(this);
        mMap.setOnCameraMoveCanceledListener(this);
        mMap.setOnMarkerClickListener(this);
        search.execute();
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onTweetsReceived(TweetsEvent tweetsEvent){
        int size = tweetsList.size();
        while(size > markersCount){
            tweetsList.remove(0);
            size = tweetsList.size();

        }
        tweetsList.add(tweetsEvent.getTweet());
       populateMap(size);

    }

    public void populateMap(final int size){
        runOnUiThread(new Runnable() {
            public void run() {
                try{
                    if(size >= markersCount){
                        mMap.clear();
                    }
                    for (Tweet tv : tweetsList){
                        LatLng TutorialsPoint = new LatLng(tv.getTweetLati(), tv.getTweetLongi());
                        mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(tv.getImageUrl())).position(TutorialsPoint).title(tv.getTweet()));
                    }
                }catch (Exception e){
                    Log.e("AAAAA", "Whoops " + e);
                }

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        eventBus.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        eventBus.unregister(this);
    }

    @Override
    public void onCameraIdle() {
    }

    @Override
    public void onCameraMoveCanceled() {
        Log.v("AAAAA" , "CamperaMoveCanceled");
    }

    @Override
    public void onCameraMove() {
        VisibleRegion visibleRegion = mMap.getProjection().getVisibleRegion();
        LatLngBounds nearLeft = visibleRegion.latLngBounds;
        southwestLatitude = nearLeft.southwest.latitude;
        southwestLongitude = nearLeft.southwest.longitude;
        northeastLatitude = nearLeft.northeast.latitude;
        northeastLongitude = nearLeft.northeast.longitude;
        Log.e("AAAAA", + southwestLatitude + " " + southwestLongitude);
        Log.e("AAAAA", + northeastLatitude + " " + northeastLongitude);

    }

    @Override
    public void onCameraMoveStarted(int i) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    class SearchOnTwitterStream extends AsyncTask<String, Void, Integer>{
        final int SUCCESS = 0;
        final int FAILURE = SUCCESS + 1;
        ProgressDialog dialog;
        MainActivity mainActivity;
        Bus evenBus = new Bus();

        public SearchOnTwitterStream(MainActivity mainActivity){
            this.mainActivity = mainActivity;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(MainActivity.this, "", "Searching");
        }

        @Override
        protected Integer doInBackground(String... params) {
            try {
                ConfigurationBuilder builder = new ConfigurationBuilder();
                builder.setDebugEnabled(true);
                builder.setOAuthConsumerKey(CONSUMER_KEY);
                builder.setOAuthConsumerSecret(CONSUMER_SECRET);
                builder.setOAuthAccessToken(ACCESS_TOKEN);
                builder.setOAuthAccessTokenSecret(ACCESS_TOKEN_SECRET);


                TwitterStream twitterStream = new TwitterStreamFactory(builder.build()).getInstance();
                StatusListener listener = new StatusListener() {
                    @Override
                    public void onStatus(twitter4j.Status status) {
                        double longi = status.getGeoLocation().getLongitude();
                        double lati = status.getGeoLocation().getLatitude();
                        if(lati > southwestLatitude && longi > southwestLongitude && lati < northeastLatitude && longi < northeastLongitude){
                            try{
                                Tweet tweet = new Tweet("@" + status.getUser().getScreenName(), status.getText(), status.getCreatedAt().getTime(), status.getGeoLocation().getLatitude(), status.getGeoLocation().getLongitude(), status.getUser().getProfileImageURL());
                                eventBus.post(new TweetsEvent(tweet));


                            }catch (Exception e){
                                Log.e("AAAAA", e+ " ");
                            }

                        }
                    }

                    @Override
                    public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {

                    }

                    @Override
                    public void onTrackLimitationNotice(int i) {

                    }

                    @Override
                    public void onScrubGeo(long l, long l1) {

                    }

                    @Override
                    public void onStallWarning(StallWarning stallWarning) {

                    }

                    @Override
                    public void onException(Exception e) {

                    }
                };

                FilterQuery filtro = new FilterQuery();
                double[][] bb= {{-180,-90}, {180,90}};
                filtro.locations(bb);
                twitterStream.addListener(listener);
                twitterStream.filter(filtro);

                // YOu can set the count of maximum records here
                    return SUCCESS;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return FAILURE;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            dialog.dismiss();
            if (result == SUCCESS) {
            } else {
                Toast.makeText(MainActivity.this, "Error getting tweets", Toast.LENGTH_LONG).show();
            }
        }
    }

}
