package com.example.supschool.picojazzemploiapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

public class ModifcvActivity extends AppCompatActivity {
    LinearLayout linearLayoutP ;
    LinearLayout linearLayoutP2 ;
    String user_cv;
    EditText lastName,firstName,email,adress,tel,age;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifcv);
        Toolbar toolbar = (Toolbar)findViewById(R.id.modifcvToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Modifier mon CV");
        toolbar.setTitleTextColor(Color.parseColor("#ecf0f1"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.waiting));

        user_cv = getIntent().getStringExtra("user_cv");

        linearLayoutP = (LinearLayout) findViewById(R.id.linearLayoutModifcv);
        linearLayoutP2 = (LinearLayout) findViewById(R.id.linearLayoutModifcv2);

        firstName =(EditText)findViewById(R.id.txtPrenom);
        lastName =(EditText)findViewById(R.id.txtNom);
        email =(EditText)findViewById(R.id.txtEmail);
        adress =(EditText)findViewById(R.id.txtAdresse);
        tel =(EditText)findViewById(R.id.txtTel);
        age =(EditText)findViewById(R.id.txtAge);

        InfoCvServer infoCvServer = new InfoCvServer();
        infoCvServer.execute("http://192.168.56.1:8080/api/cv/"+user_cv);


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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_modif_cv, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    protected class InfoCvServer extends AsyncTask<String,Void,String>{
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
            if (s == null) {
                Toast.makeText(ModifcvActivity.this, "erreur de connexion", Toast.LENGTH_SHORT).show();
                return;
            }

            try{
                JSONObject jsonObject = new JSONObject(s);
                if(jsonObject.getString("email") != "null") {
                    email.setText(jsonObject.getString("email"));
                }
                if (jsonObject.getInt("age") != 0) {
                    age.setText(String.valueOf(jsonObject.getInt("age")));
                }
                if(!jsonObject.getString("tel").equals("0")) {
                    tel.setText(jsonObject.getString("tel"));
                }
                if (jsonObject.getString("adress") !="null") {
                    adress.setText(jsonObject.getString("adress"));
                }
                if(jsonObject.getString("firstName") != "null") {
                    firstName.setText(jsonObject.getString("firstName"));
                }
                if(jsonObject.getString("lastName") != "null") {
                    lastName.setText(jsonObject.getString("lastName"));
                }

                JSONArray jsonFormation = jsonObject.getJSONArray("formations");
                JSONArray jsonExperience = jsonObject.getJSONArray("experiences");

                for (int i = 0 ; i < jsonExperience.length() ; i++){
                    JSONObject element = jsonExperience.getJSONObject(i);
                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                     View rowView = inflater.inflate(R.layout.row_modif_experience, null);

                    EditText debut = (EditText)rowView.findViewById(R.id.txtDebut);
                    EditText fin = (EditText)rowView.findViewById(R.id.txtFin);
                    EditText position = (EditText)rowView.findViewById(R.id.txtPoste);
                    EditText societe = (EditText)rowView.findViewById(R.id.txtSociete);
                    EditText about = (EditText)rowView.findViewById(R.id.txtAbout);
                    debut.setText(element.getString("begin"));
                    fin.setText(element.getString("end"));
                    position.setText(element.getString("position"));
                    societe.setText(element.getString("company"));
                    about.setText(element.getString("about"));

                    linearLayoutP.addView(rowView, linearLayoutP.getChildCount() );

                }

                for (int i = 0 ; i < jsonFormation.length() ; i++){
                    JSONObject element = jsonFormation.getJSONObject(i);
                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View rowView = inflater.inflate(R.layout.row_modif_formation, null);

                    EditText date = (EditText)rowView.findViewById(R.id.txtDate);
                    EditText diplome = (EditText)rowView.findViewById(R.id.txtDiplome);
                    EditText ecole = (EditText)rowView.findViewById(R.id.txtEcole);

                    date.setText(element.getString("date"));
                    diplome.setText(element.getString("name"));
                    ecole.setText(element.getString("school"));

                    linearLayoutP2.addView(rowView, linearLayoutP2.getChildCount() );
                }

            }catch(Exception e){
                e.printStackTrace();
            }

        }
    }

    protected class UpdateCvServer extends AsyncTask<String,Void,String>{
        @Override
        protected void onPreExecute() {
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            dialog.dismiss();
        }
    }
}
