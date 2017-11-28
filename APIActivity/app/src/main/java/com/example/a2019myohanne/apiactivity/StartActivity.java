package com.example.a2019myohanne.apiactivity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;

public class StartActivity extends AppCompatActivity {
    private EditText edit;
    private Button proceed;
    //get tweets given a country, http://twitter4j.org/en/, http://twitter4j.org/javadoc/twitter4j/Twitter.html#placesGeo--, https://stackoverflow.com/questions/28309053/twitter4j-integration-through-android-studio
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // The factory instance is re-useable and thread safe.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        edit=(EditText) findViewById(R.id.editText);
        proceed=(Button) findViewById(R.id.button);
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(StartActivity.this, MapsActivity.class);
                i.putExtra("nation", String.valueOf(edit.getText()));
                startActivity(i);
            }
        });
    }
    //initial, name, lat, long

}
