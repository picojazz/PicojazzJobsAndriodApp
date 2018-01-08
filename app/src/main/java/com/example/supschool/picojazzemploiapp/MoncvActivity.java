package com.example.supschool.picojazzemploiapp;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MoncvActivity extends AppCompatActivity {
    String user_cv;
    ListView expListview,formationListview;
    ProgressDialog dialog;
    List<Formations> listFormations;
    List<Experiences> listExperiences;
    TextView name,email,adress,tel,age;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moncv);

        dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.waiting));

        Toolbar toolbar = (Toolbar)findViewById(R.id.moncvToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Mon CV");
        toolbar.setTitleTextColor(Color.parseColor("#ecf0f1"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        user_cv = getIntent().getStringExtra("user_cv");
        Log.i("debug "," user cv => "+user_cv);


        listFormations = new ArrayList<>();
        listExperiences = new ArrayList<>();
        name =(TextView)findViewById(R.id.cvName);
        email =(TextView)findViewById(R.id.cvEmail);
        adress =(TextView)findViewById(R.id.cvAdress);
        tel =(TextView)findViewById(R.id.cvTel);
        age =(TextView)findViewById(R.id.cvAge);





        expListview = (ListView)findViewById(R.id.expListview);
        formationListview =(ListView)findViewById(R.id.formationListview);

        MoncvServer moncvServer = new MoncvServer();
        moncvServer.execute("http://192.168.56.1:8080/api/cv/"+user_cv);


        expListview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cv, menu);
        return true;
    }

    protected class MoncvServer extends AsyncTask<String,Void,String> {
        @Override
        protected void onPreExecute() {
            dialog.show();
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
            dialog.dismiss();
            if (s == null) {
                Toast.makeText(MoncvActivity.this, "erreur de connexion", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                Log.i("debug", "enter in async");
                JSONObject jsonObject = new JSONObject(s);
                Log.i("debug", jsonObject.getString("firstName") + " " + jsonObject.getString("tel") + " " + jsonObject.getString("adress") + " " + jsonObject.getInt("age"));
                name.setText(jsonObject.getString("firstName") + " " + jsonObject.getString("lastName"));
                email.setText(jsonObject.getString("email"));
                age.setText(String.valueOf(jsonObject.getInt("age")) + " ans");
                tel.setText(jsonObject.getString("tel"));
                adress.setText(jsonObject.getString("adress"));
                // Log.i("debug",jsonObject.getString("firstName")+" "+jsonObject.getString("tel")+" "+jsonObject.getString("adress")+" "+jsonObject.getInt("age"));

                JSONArray jsonFormation = jsonObject.getJSONArray("formations");
                JSONArray jsonExperience = jsonObject.getJSONArray("experiences");


                listExperiences.clear();
                listFormations.clear();
                for (int i = 0; i < jsonExperience.length(); i++) {
                    JSONObject element = jsonExperience.getJSONObject(i);

                    Experiences exp = new Experiences(element.getString("begin"), element.getString("end"),
                            element.getString("position"), element.getString("company"), element.getString("about"));
                    listExperiences.add(exp);
                }
                for (int i = 0; i < jsonFormation.length(); i++) {
                    JSONObject element = jsonFormation.getJSONObject(i);

                    Formations formation = new Formations(element.getString("date"),
                            element.getString("name"), element.getString("school"));
                    listFormations.add(formation);
                }


                CustomAdapterExperience customAdapterExperience = new CustomAdapterExperience();
                expListview.setAdapter(customAdapterExperience);
                CustomAdapterFormation customAdapterFormation = new CustomAdapterFormation();
                formationListview.setAdapter(customAdapterFormation);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

        class CustomAdapterExperience extends BaseAdapter{

            @Override
            public int getCount() {
                return listExperiences.size();
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
                view = getLayoutInflater().inflate(R.layout.row_experience, null);



                TextView dateDebut = (TextView)view.findViewById(R.id.dateDebut);
                TextView dateFin = (TextView)view.findViewById(R.id.dateFin);
                TextView expPosition = (TextView)view.findViewById(R.id.expPosition);
                TextView expCompany = (TextView)view.findViewById(R.id.expCompany);
                TextView expAbout = (TextView)view.findViewById(R.id.expAbout);

                dateDebut.setText(listExperiences.get(i).getBegin());
                dateFin.setText(listExperiences.get(i).getEnd());
                expPosition.setText(listExperiences.get(i).getPosition());
                expCompany.setText(listExperiences.get(i).getCompany());
                expAbout.setText(listExperiences.get(i).getAbout());



                return view;
            }
        }

    class CustomAdapterFormation extends BaseAdapter{

        @Override
        public int getCount() {
            return listFormations.size();
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
            view = getLayoutInflater().inflate(R.layout.row_formation, null);



            TextView date = (TextView)view.findViewById(R.id.dateFormation);
            TextView name = (TextView)view.findViewById(R.id.nameFormation);
            TextView school = (TextView)view.findViewById(R.id.schoolformation);

            date.setText(listFormations.get(i).getDate());
            name.setText(listFormations.get(i).getName());
            school.setText(listFormations.get(i).getSchool());




            return view;
        }
    }

}
