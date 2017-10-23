package com.example.rex.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by Rex on 8/06/2017.
 */

public class AboutActivity extends AppCompatActivity {
    private TextView googleLicence;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        googleLicence = (TextView) findViewById(R.id.GoogleLicence);

    }
}
