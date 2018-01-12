package com.example.supschool.picojazzemploiapp;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;


public class OfferActivity extends AppCompatActivity {
    private String offer_id,id;
    private Offer offer;
    private ProgressDialog dialog;
    private TextView title,company,contract,salary,location,about;
    MenuItem item;
    int test_fav;
    FrameLayout container;
    private View.OnTouchListener mListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer);

        Toolbar toolbar =(Toolbar) findViewById(R.id.toolbar_offer);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("details de l'offre");
        toolbar.setTitleTextColor(Color.parseColor("#ecf0f1"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        container = (FrameLayout)findViewById(R.id.container);
        container.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });



        dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.waiting));
        id = getIntent().getStringExtra("user_id");
        offer_id = getIntent().getStringExtra("offer_id");

        DetailsOffer detailsOffer = new DetailsOffer();
        detailsOffer.execute("http://192.168.56.1:8080/api/offers/"+offer_id);

        title = (TextView)findViewById(R.id.offer_title);
        company = (TextView)findViewById(R.id.offer_company);
        contract = (TextView)findViewById(R.id.offer_contract);
        salary = (TextView)findViewById(R.id.offer_salary);
        location = (TextView)findViewById(R.id.offer_location);
        about = (TextView)findViewById(R.id.offer_about);

        Log.i("debug","DEBUGME => id user = "+id+" et offer id = "+offer_id);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.offer_action_bar, menu);
        item = menu.findItem(R.id.fav);
        VerifServer verifServer = new VerifServer();
        verifServer.execute("http://192.168.56.1:8080/api/verif-fav?idU="+id+"&idO="+offer_id);

        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(test_fav == 0) {
                    AddFavServer addFavServer = new AddFavServer();
                    addFavServer.execute("http://192.168.56.1:8080/api/add-fav?idU=" + id + "&idO=" + offer_id);
                }
                if(test_fav == 1) {
                    DeleteFavServer deleteFavServer = new DeleteFavServer();
                    deleteFavServer.execute("http://192.168.56.1:8080/api/delete-fav?idU=" + id + "&idO=" + offer_id);
                }
                return true;
            }
        });

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
                salary.setText(json.getString("salary")+" / an");
                location.setText(json.getString("place"));
                about.setText(json.getString("about"));
                company.setText(json.getString("company"));

                Bundle bundle = new Bundle();
                bundle.putDouble("lat",json.getDouble("lat"));
                bundle.putDouble("lng",json.getDouble(("lon")));
                bundle.putString("company",json.getString("company"));
                MapsFragment f = new MapsFragment();

                f.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.container,f).commit();


            }catch(Exception e){
                e.printStackTrace();
            }

        }
    }

    protected class VerifServer extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... params) {

            try{
                HttpClient client = new DefaultHttpClient();
                HttpGet get = new HttpGet(params[0]);
                ResponseHandler<String> buffer = new BasicResponseHandler();
                String result = client.execute(get,buffer);
                return result;
            }catch(Exception e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            if(s == null)
                item.setIcon(R.drawable.ic_star_border_black_24dp);

            try{
                JSONObject json = new JSONObject(s);
                if(json.getString("status").equals("ko")) {
                    item.setIcon(R.drawable.ic_star_border_black_24dp);
                    test_fav = 0 ;
                }

                if(json.getString("status").equals("ok")) {
                    item.setIcon(R.drawable.ic_star_white_24dp);
                    test_fav = 1 ;
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    protected class AddFavServer extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... params) {
            try{
                HttpClient client = new DefaultHttpClient();
                HttpGet get = new HttpGet(params[0]);
                ResponseHandler<String> buffer = new BasicResponseHandler();
                String result = client.execute(get,buffer);
                return result;
            }catch(Exception e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(OfferActivity.this, "cet offre a ete ajoutee a vos favoris", Toast.LENGTH_SHORT).show();
            VerifServer verifServer = new VerifServer();
            verifServer.execute("http://192.168.56.1:8080/api/verif-fav?idU="+id+"&idO="+offer_id);
        }
    }
    protected class DeleteFavServer extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... params) {
            try{
                HttpClient client = new DefaultHttpClient();
                HttpGet get = new HttpGet(params[0]);
                ResponseHandler<String> buffer = new BasicResponseHandler();
                String result = client.execute(get,buffer);
                return result;
            }catch(Exception e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(OfferActivity.this, "cet offre a ete retiree de vos favoris", Toast.LENGTH_SHORT).show();
            VerifServer verifServer = new VerifServer();
            verifServer.execute("http://192.168.56.1:8080/api/verif-fav?idU="+id+"&idO="+offer_id);
        }
    }







}
