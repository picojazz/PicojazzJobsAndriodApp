package com.example.supschool.picojazzemploiapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

public class ConnexionActivity extends AppCompatActivity {
    Button signUp,login;
    EditText txtUsername,txtPassword;
    String username,password;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexion);
        txtUsername = (EditText)findViewById(R.id.txtUsername);
        txtPassword = (EditText)findViewById(R.id.txtPassword);
        dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.waiting));

        login = (Button)findViewById(R.id.btnConnect);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = txtUsername.getText().toString();
                password = txtPassword.getText().toString();

                LoginServer loginServer = new LoginServer();
                loginServer.execute("http://192.168.56.1:8080/api/me?username="+username+"&password="+password);

            }
        });

        signUp = (Button)findViewById(R.id.btnSignUp);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConnexionActivity.this,RegisterActivity.class);

                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    protected class LoginServer extends AsyncTask<String,Void,String>{
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
                String result = client .execute(get,buffer);
                return result;

            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            dialog.dismiss();
            if (s == null ){
                Toast.makeText(ConnexionActivity.this, "erreur de connexion", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                JSONObject json = new JSONObject(s);
                if (json.getString("status").equals("ko")) {
                    Toast.makeText(ConnexionActivity.this, "identifiant ou password incorrect", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(ConnexionActivity.this, HomeActivity.class);
                    intent.putExtra("id", json.getString("id"));
                    Log.i("debug","DEBUGME => id user = "+json.getString("id"));
                    startActivity(intent);
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

}
