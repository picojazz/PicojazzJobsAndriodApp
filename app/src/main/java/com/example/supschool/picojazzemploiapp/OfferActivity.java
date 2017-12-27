package com.example.supschool.picojazzemploiapp;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;


public class OfferActivity extends AppCompatActivity {
    private String offer_id;
    private Offer offer;
    private ProgressDialog dialog;
    private TextView title,company,contract,salary,location,about;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer);

        Toolbar toolbar =(Toolbar) findViewById(R.id.toolbar_offer);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("details de l'offre");
        //toolbar.setTitleTextColor(Color.parseColor("#000"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.waiting));

         offer_id = getIntent().getStringExtra("offer_id");

        DetailsOffer detailsOffer = new DetailsOffer();
        detailsOffer.execute("http://192.168.56.1:8080/api/offers/"+offer_id);

        title = (TextView)findViewById(R.id.offer_title);
        company = (TextView)findViewById(R.id.offer_company);
        contract = (TextView)findViewById(R.id.offer_contract);
        salary = (TextView)findViewById(R.id.offer_salary);
        location = (TextView)findViewById(R.id.offer_location);
        about = (TextView)findViewById(R.id.offer_about);

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

    protected class DetailsOffer extends AsyncTask<String, Void, String>{
        @Override
        protected void onPreExecute() {
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            try{
                HttpClient client = new DefaultHttpClient();
                HttpGet get = new HttpGet(params[0]);
                ResponseHandler<String> buffer = new BasicResponseHandler();
                String result = client.execute(get, buffer);
                return result;

            }catch (Exception e){
                e.printStackTrace();
                return null;

            }

        }

        @Override
        protected void onPostExecute(String s) {
            dialog.dismiss();
            if (s == null) {
                Toast.makeText(OfferActivity.this, "erreur de connexion", Toast.LENGTH_SHORT).show();
                return;
            }
            try{
                JSONObject json = new JSONObject(s);
                title.setText(json.getString("title"));
                contract.setText(json.getString("contract"));
                salary.setText(json.getString("salary")+" / year");
                location.setText(json.getString("place"));
                about.setText(json.getString("about"));


            }catch(Exception e){
                e.printStackTrace();
            }

        }
    }
}
