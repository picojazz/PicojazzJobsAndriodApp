package com.example.supschool.picojazzemploiapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private ListView homeListView;
    private List<Offer> listOffers;
    private ProgressDialog dialog;
    private SearchView searchView;
    String user_id;
    SwipeRefreshLayout swipeHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        homeListView = (ListView) findViewById(R.id.homeListView);
        listOffers = new ArrayList<>();
        dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.waiting));
        swipeHome = (SwipeRefreshLayout)findViewById(R.id.swipeHome);
        swipeHome.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                OfferServer offerServer = new OfferServer();
                offerServer.execute("http://192.168.56.1:8080/api/offers");
                swipeHome.setRefreshing(false);
            }
        });


        getSupportActionBar().setTitle("Acceuil");
        toolbar.setTitleTextColor(Color.parseColor("#ecf0f1"));

        user_id = getIntent().getStringExtra("id");
        Log.i("debug","DEBUGME => id user = "+user_id );
        homeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(HomeActivity.this,OfferActivity.class);
                intent.putExtra("offer_id",String.valueOf(listOffers.get(position).getId()));
                intent.putExtra("user_id",user_id);
               // Toast.makeText(HomeActivity.this,String.valueOf(listOffers.get(position).getId()) , Toast.LENGTH_SHORT).show();

                startActivity(intent);
            }
        });



        //api offers
        OfferServer offerServer = new OfferServer();
        offerServer.execute("http://192.168.56.1:8080/api/offers");

        InfoServer info = new InfoServer();
        info.execute("http://192.168.56.1:8080/api/users/"+user_id);




        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        MenuItem item = menu.findItem(R.id.menu_search);
        searchView = (SearchView)item.getActionView();
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                OfferServer offerServer = new OfferServer();
                offerServer.execute("http://192.168.56.1:8080/api/offers?p=");
                return false;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                OfferServer offerServer = new OfferServer();
                offerServer.execute("http://192.168.56.1:8080/api/offers?p="+query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_moncv) {
            Intent intent = new Intent(HomeActivity.this,MoncvActivity.class);
            intent.putExtra("user_cv",user_id);
            startActivity(intent);

        } else if (id == R.id.nav_favoris) {
            Intent intent = new Intent(HomeActivity.this,FavorisActivity.class);
            intent.putExtra("user_id",user_id);
            startActivity(intent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    protected class OfferServer extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            //dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                HttpClient client = new DefaultHttpClient();
                HttpGet get = new HttpGet(params[0]);
                ResponseHandler<String> buffer = new BasicResponseHandler();
                String result = client.execute(get, buffer);
                return result;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            //dialog.dismiss();
            if (s == null) {
                Toast.makeText(HomeActivity.this, "erreur de connexion", Toast.LENGTH_SHORT).show();
                return;
            }

            try {

               // JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray = new JSONArray(s);
                listOffers.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject element = jsonArray.getJSONObject(i);
                    /*String dateStr = element.getString("dateCreate");
                    SimpleDateFormat  sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date  date = sdf.parse(dateStr);*/
                    Offer offer = new Offer(element.getLong("id"),element.getString("title"),element.getString("place"),element.getString("contract"),element.getString("dateCreate"),element.getString("company"));
                    listOffers.add(offer);
                }

                //for (int i = 0 ; i < listOffers.size() ; i++){
                 //   System.out.println(listOffers.get(i).toString());
                //}
                //Toast.makeText(HomeActivity.this, listOffers.get(0).toString(), Toast.LENGTH_SHORT).show();
                CustomAdaptder customAdaptder = new CustomAdaptder();
                homeListView.setAdapter(customAdaptder);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
    class CustomAdaptder extends BaseAdapter{

        @Override
        public int getCount() {
            return listOffers.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup parent) {
            view = getLayoutInflater().inflate(R.layout.offer_layout, null);

            TextView title = (TextView)view.findViewById(R.id.offerTitle);
            TextView place = (TextView)view.findViewById(R.id.offerPlace);
            TextView contract = (TextView)view.findViewById(R.id.offerContract);
            TextView date = (TextView)view.findViewById(R.id.offerDate);
            TextView company = (TextView)view.findViewById(R.id.companyName);

            title.setText(listOffers.get(i).getTitle());
            place.setText(listOffers.get(i).getPlace());
            contract.setText(listOffers.get(i).getContract());
            company.setText(listOffers.get(i).getCompany());

            //date
            String dateStr = listOffers.get(i).getDateCreate();
            SimpleDateFormat  sdf = new SimpleDateFormat("yyyy-MM-dd ");
            long numberOfDay=0;
            try{
                long CONST_DURATION_OF_DAY = 1000l * 60 * 60 * 24;
                Date  datee = sdf.parse(dateStr);
                Date today = new Date();
                long diff = Math.abs(datee.getTime() - today.getTime());
                 numberOfDay = (long)diff/CONST_DURATION_OF_DAY;
            }catch (Exception e){
                e.printStackTrace();
               // date.setText(listOffers.get(i).getDateCreate());
            }

            date.setText("il y'a "+Long.toString(numberOfDay)+" jours");

            return view;
        }
    }

    protected class InfoServer extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... params) {
            try {
                HttpClient client = new DefaultHttpClient();
                HttpGet get = new HttpGet(params[0]);
                ResponseHandler<String> buffer = new BasicResponseHandler();
                String result = client.execute(get, buffer);
                return result;

            }catch (Exception e){
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            try{
                JSONObject json = new JSONObject(s);
                TextView username = (TextView)findViewById(R.id.username);
                TextView email = (TextView)findViewById(R.id.email);
                username.setText(json.getString("username"));
                email.setText(json.getString("email"));

            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }
}
