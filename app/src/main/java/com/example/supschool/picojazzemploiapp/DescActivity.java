package com.example.supschool.picojazzemploiapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class DescActivity extends AppCompatActivity {
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desc);

        Button button = (Button)findViewById(R.id.btnDesc);
        id = getIntent().getStringExtra("id");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DescActivity.this,HomeActivity.class);
                intent.putExtra("id",id);
                startActivity(intent);
            }
        });
    }
}
