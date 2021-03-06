package com.example.supschool.picojazzemploiapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {
    private Button btnConnectR ,btnSignUp;
    EditText txtUsername,txtPassword,txtEmail;
    String username,password,email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnConnectR = (Button)findViewById(R.id.btnConnectR);
        btnSignUp = (Button)findViewById(R.id.btnSignUpR);
        txtUsername = (EditText) findViewById(R.id.txtUsernameR);
        txtPassword = (EditText) findViewById(R.id.txtPasswordR);
        txtEmail = (EditText) findViewById(R.id.txtEmailR);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = txtUsername.getText().toString();
                password = txtPassword.getText().toString();
                email = txtEmail.getText().toString();

                RegisterServer registerServer = new RegisterServer();
                registerServer.execute("http://192.168.56.1:8080/register");

            }
        });

        btnConnectR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this,ConnexionActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
    protected class RegisterServer extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... params) {
            try{


                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(params[0]);
                List<NameValuePair> form = new ArrayList<>();
                form.add(new BasicNameValuePair("username",username));
                form.add(new BasicNameValuePair("password",password));
                form.add(new BasicNameValuePair("email",email));

                post.setEntity(new UrlEncodedFormEntity(form, HTTP.UTF_8));
                ResponseHandler<String> buffer = new BasicResponseHandler();
                String result = client.execute(post,buffer);

                return result;
            }catch(Exception e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            if (s == null) {
                Toast.makeText(RegisterActivity.this, "erreur de connexion", Toast.LENGTH_SHORT).show();
                return;
            }

            try{
                JSONObject jsonObject = new JSONObject(s);
                if(jsonObject.getString("status").equals("ok")){
                    Toast.makeText(RegisterActivity.this, "Bienvenue !", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this,DescActivity.class);
                    intent.putExtra("id",jsonObject.getString("id"));
                    startActivity(intent);
                }else{
                    Toast.makeText(RegisterActivity.this, "identifiant ou email deja utilisé", Toast.LENGTH_SHORT).show();

                }

            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }
}
