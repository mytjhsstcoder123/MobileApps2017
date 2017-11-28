package com.example.a2019myohanne.apiactivity;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.widget.ProgressBar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import twitter4j.ExtendedMediaEntity;
import twitter4j.GeoLocation;
import twitter4j.HashtagEntity;
import twitter4j.MediaEntity;
import twitter4j.Place;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.RateLimitStatus;
import android.graphics.Bitmap;
import android.widget.ProgressBar;
import twitter4j.Scopes;
import twitter4j.Status;
import twitter4j.SymbolEntity;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.URLEntity;
import twitter4j.User;
import twitter4j.UserMentionEntity;
import twitter4j.conf.ConfigurationBuilder;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String nation="";
    private String[] country=new String[4];
    final Context context=this;
    private double lat;
    private double lon;
    private List<GeoLocation> tweetloc= new ArrayList<GeoLocation>();
    private List<String> tweetnames= new ArrayList<String>();
    private List<String> tweetlist= new ArrayList<String>();
    private ProgressBar pb;
    private Twitter twitter;
    private AssetManager assetManager;
    //twitter4j-4.0.4
    //background async task, codepath.com
    //ask person to go to a city, @mistersir999, password=bobjones123 ; https://stackoverflow.com/questions/13440414/getlocation-always-returns-null-searching-twitter
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        pb=(ProgressBar) findViewById(R.id.bar);
        pb.setIndeterminate(true);
        //downloadImageAsync();
        nation=getIntent().getStringExtra("nation");
        assetManager = getAssets();
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey("YNgWXd4fcr2NX8ui5zKsGJGib")
                .setOAuthConsumerSecret("gQGH5AL33KmV66I688hYiOSlHVSCetsvceZpXHyCWwFRe8H2pt")
                .setOAuthAccessToken("926140295611285504-nY07hoNYL0zNO0C8m07qplCmYlJn0aR")
                .setOAuthAccessTokenSecret("RrHRjI0obhqyt43hoBvnA54ttLmag0oLrcOq4GYxYuvxd");
        TwitterFactory tf = new TwitterFactory(cb.build());
        twitter = tf.getInstance();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //runBackground(twitter);//too slow
        Scanner sc=null;
        try {
            DataInputStream textFileStream = new DataInputStream(assetManager.open("countries.txt"));
            sc = new Scanner(textFileStream);
            while(sc.hasNextLine()) {
                String s=sc.nextLine();
                Scanner inline=new Scanner(s);
                String c="";
                String one=inline.next();
                String two=inline.next();
                String three=inline.next();
                while(inline.hasNext())
                {
                    String y=inline.next();
                    c+=y;
                }
                s=s.replace("\t", " ");
                String[] ar2=s.split(" ");
                String[] ar=new String[4];
                ar[0]=ar2[0];
                ar[1]=ar2[1];
                ar[2]=ar2[2];
                ar[3]=c;
                if(String.valueOf(ar[3]).replace(" ", "").equals(nation.replace(" ", "")))
                {
                    country=ar;
                }
            }
            sc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Query query = new Query();
            GeoLocation location = new GeoLocation(Double.parseDouble(String.valueOf(country[1])), Double.parseDouble(String.valueOf(country[2])));
            lat=location.getLatitude();
            lon=location.getLongitude();
            query.setGeoCode(location, 200.0, Query.Unit.km); //radius=50
            QueryResult result;
            do {
                result = twitter.search(query);
                List<Status> tweets = result.getTweets();
                for (Status tweet : tweets) {
                    if(tweet.getGeoLocation()!=null) {
                        tweetlist.add(tweet.getText());
                        tweetnames.add(tweet.getUser().getScreenName());
                        tweetloc.add(tweet.getGeoLocation());
                    }
                }

            } while ((query = result.nextQuery()) != null);

        } catch (TwitterException te) {

        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void createNotif()
    {
        // Executes whenever publishProgress is called from doInBackground
        // Used to update the progress indicator

        String message=nation+": "+tweetnames.get(0)+" says "+tweetlist.get(0);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(MapsActivity.this)
                        .setSmallIcon(R.drawable.image)
                        .setContentTitle("New International Tweet!")
                        .setContentText(message)//Country: user says kfdlsjfn
                        .setAutoCancel(true);
        //.setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, mBuilder.build());
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, StartActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(contentIntent);
        //pb.setVisibility(ProgressBar.INVISIBLE);
    }
    public void runBackground(Twitter twitter)
    {
        // Some long-running task like downloading an image.
        //find stuff via twitter
        Scanner sc=null;
        try {
            nation=getIntent().getStringExtra("nation");
            assetManager = getAssets();
            DataInputStream textFileStream = new DataInputStream(assetManager.open("countries.txt"));
            int random=(int)(Math.random()*246);
            sc = new Scanner(textFileStream);
            int count=0;
            nation="";
            String s="";
            while(count<random)
            {
                s=sc.nextLine();
                count+=1;
            }
            sc.close();
            Scanner inline=new Scanner(s);
            String c="";
            String one=inline.next();
            String two=inline.next();
            String three=inline.next();
            while(inline.hasNext())
            {
                String y=inline.next();
                c+=y;
            }
            nation=c;
            s=s.replace("\t", " ");
            String[] ar2=s.split(" ");
            String[] ar=new String[4];
            ar[0]=ar2[0];
            ar[1]=ar2[1];
            ar[2]=ar2[2];
            ar[3]=nation;
            country=ar;
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Query query = new Query();
            GeoLocation location = new GeoLocation(Double.parseDouble(String.valueOf(country[1])), Double.parseDouble(String.valueOf(country[2])));
            lat=location.getLatitude();
            lon=location.getLongitude();
            query.setGeoCode(location, 50.0, Query.Unit.km); //radius=50
            QueryResult result;
            do {
                result = twitter.search(query);
                List<twitter4j.Status> tweets = result.getTweets();
                for (twitter4j.Status tweet : tweets) {
                    tweetlist.add(tweet.getText());
                    tweetnames.add(tweet.getUser().getScreenName());
                }

            } while ((query = result.nextQuery()) != null);

        } catch (TwitterException te) {

        }
        createNotif();
    }
    // The types specified here are the input data type, the progress type, and the result type
    private class MyAsyncTask extends AsyncTask{
        @Override
        protected Object doInBackground(Object... objects) {
            // Some long-running task like downloading an image.
            //find stuff via twitter
            Scanner sc=null;
            try {
                //nation=getIntent().getStringExtra("nation");
                //assetManager = getAssets();
                DataInputStream textFileStream = new DataInputStream(assetManager.open("countries.txt"));
                int random=(int)(Math.random()*246);
                sc = new Scanner(textFileStream);
                int count=0;
                nation="";
                String s="";
                while(count<random)
                {
                    s=sc.nextLine();
                    count+=1;
                }
                sc.close();
                Scanner inline=new Scanner(s);
                String c="";
                String one=inline.next();
                String two=inline.next();
                String three=inline.next();
                while(inline.hasNext())
                {
                    String y=inline.next();
                    c+=y;
                }
                nation=c;
                s=s.replace("\t", " ");
                String[] ar2=s.split(" ");
                String[] ar=new String[4];
                ar[0]=ar2[0];
                ar[1]=ar2[1];
                ar[2]=ar2[2];
                ar[3]=nation;
                country=ar;
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Query query = new Query();
                GeoLocation location = new GeoLocation(Double.parseDouble(String.valueOf(country[1])), Double.parseDouble(String.valueOf(country[2])));
                lat=location.getLatitude();
                lon=location.getLongitude();
                query.setGeoCode(location, 50.0, Query.Unit.km); //radius=50
                QueryResult result;
                do {
                    result = twitter.search(query);
                    List<twitter4j.Status> tweets = result.getTweets();
                    for (twitter4j.Status tweet : tweets) {
                        tweetlist.add(tweet.getText());
                        tweetnames.add(tweet.getUser().getScreenName());
                    }

                } while ((query = result.nextQuery()) != null);

            } catch (TwitterException te) {

            }
            createNotif();
            return null;
        }

        protected void onPreExecute() {
            // Runs on the UI thread before doInBackground
            // Good for toggling visibility of a progress indicator
            pb.setVisibility(ProgressBar.VISIBLE);
        }
        protected Object onProgressUpdate()
        {
            return null;
        }
        protected Object onPostExecute() {
            pb.setVisibility(ProgressBar.INVISIBLE);
            return null;
        }

    }
    private void downloadImageAsync() {
        // Now we can execute the long-running task at any time.
        new MyAsyncTask().execute();
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if(tweetlist.size()==0)
        {
            double r=Math.random()*5;
            LatLng pos = new LatLng(lat+r, lon+r);
            String z = "No geotracked tweets in region!";
            mMap.addMarker(new MarkerOptions().position(pos).title(z)).showInfoWindow();
            mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
        }else {
            for (int i = 0; i < tweetlist.size(); i++) {
                LatLng pos = new LatLng(tweetloc.get(i).getLatitude(), tweetloc.get(i).getLongitude());
                String z = tweetnames.get(i) + ": " + tweetlist.get(i);
                mMap.addMarker(new MarkerOptions().position(pos).title(z)).showInfoWindow();
                mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
            }
        }
    }
}

