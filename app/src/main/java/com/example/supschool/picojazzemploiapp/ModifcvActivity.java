package com.example.supschool.picojazzemploiapp;

import android.content.Context;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

public class ModifcvActivity extends AppCompatActivity {
    LinearLayout linearLayoutP ;
    LinearLayout linearLayoutP2 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifcv);
        Toolbar toolbar = (Toolbar)findViewById(R.id.modifcvToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Modifier mon CV");
        toolbar.setTitleTextColor(Color.parseColor("#ecf0f1"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        linearLayoutP = (LinearLayout) findViewById(R.id.linearLayoutModifcv);
        linearLayoutP2 = (LinearLayout) findViewById(R.id.linearLayoutModifcv2);


    }
    public void onAddRow(View v) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.row_modif_experience, null);
        linearLayoutP.addView(rowView, linearLayoutP.getChildCount() );
        Log.i("debug","1111");
    }
    public void onAddRow2(View v) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.row_modif_formation, null);
        linearLayoutP2.addView(rowView, linearLayoutP2.getChildCount() );
        Log.i("debug","1111");
    }
    public void onDelete(View v) {
        linearLayoutP.removeView((View) v.getParent());
    }
    public void onDelete2(View v) {
        linearLayoutP2.removeView((View) v.getParent());
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

       return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
