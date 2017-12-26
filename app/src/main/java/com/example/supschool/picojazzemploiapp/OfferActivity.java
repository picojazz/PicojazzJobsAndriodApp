package com.example.supschool.picojazzemploiapp;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class OfferActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer);

        Toolbar toolbar =(Toolbar) findViewById(R.id.toolbar_offer);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("offer");
        //toolbar.setTitleTextColor(Color.parseColor("#000"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String offer_id = getIntent().getStringExtra("offer_id");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.offer_action_bar, menu);

        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
