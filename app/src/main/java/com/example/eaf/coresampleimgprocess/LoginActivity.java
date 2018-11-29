package com.example.eaf.coresampleimgprocess;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private String registerUrl = "http://47.107.126.23:5000/register";
    private String loginUrl = "http://47.107.126.23:5000/login";
    Button gotoImageProcessing;
    Button gotoTerrainPrediction;
    Button registerButton;
    Button loginButton;
    EditText usernameEditText;
    EditText passwordEditText;
    LinearLayout loginFrame;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = (EditText) findViewById(R.id.username_edittext);
        passwordEditText = (EditText) findViewById(R.id.password_edittext);
        gotoImageProcessing=findViewById(R.id.gotoImage);
        gotoTerrainPrediction=findViewById(R.id.gotoTP);
        loginFrame=findViewById(R.id.mainFrame_login);
        registerButton = (Button) findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                new RegisterNetworkTask().execute(username, password);
                Log.d(TAG, "onClick: register task begin " + username + " " + password);
            }
        });
        loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                new LoginNetworkTask().execute(username, password);
                Log.d(TAG, "onClick: login task begin " + username + " " + password);
            }
        });

        gotoImageProcessing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Image Processing.",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
        gotoTerrainPrediction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Terrain Prediction.",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(LoginActivity.this,terrainPredictionActivity.class);
                startActivity(intent);
            }
        });
    }

    private void finishLogin(String actionResult) {
        if(actionResult=="success") {
            MainActivity.currentUsername = usernameEditText.getText().toString();
            MainActivity.usernameTextView.setText(MainActivity.currentUsername);
            gotoTerrainPrediction.setVisibility(View.VISIBLE);
            gotoImageProcessing.setVisibility(View.VISIBLE);
            loginFrame.setVisibility(View.GONE);
            MainActivity.loginStatus=true;
        }
    }

    class LoginNetworkTask extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            return login(strings[0], strings[1]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (!s.equals("error")) {
                Log.d(TAG, "onPostExecute: Login task success");
                Toast.makeText(getApplicationContext(),"Login successfully.",Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(),"Please choose what you want to do next.",Toast.LENGTH_SHORT).show();
                finishLogin("success");
            }
            else Toast.makeText(getApplicationContext(),"Fail to login,you may check your password or register first.",Toast.LENGTH_SHORT).show();
        }
    }

    class RegisterNetworkTask extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            return register(strings[0], strings[1]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (!s.equals("error")) {
                Log.d(TAG, "onPostExecute: Register task success");
                Toast.makeText(getApplicationContext(),"Register successfully.",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String register(String username, String password) {
        Log.d(TAG, "register: begin");
        Request.Builder builder = new Request.Builder();
        Request request = builder.url(registerUrl + "?username=" + username + "&" + "password=" + password).get().build();
        Log.d(TAG, "register: try");
        try {
            Response response = MainActivity.okHttpClientWithCookie.newCall(request).execute();
            Log.d(TAG, "register: response code is " + String.valueOf(response.code()));
            if (response.isSuccessful()) {
                Log.d(TAG, "register: get body");
                String resultValue = response.body().string();
                Log.d(TAG, "register: success");
                return resultValue;
            }
            return "error";
        } catch (IOException e) {
            Log.d(TAG, "register: here is err");
            e.printStackTrace();
            Log.d(TAG, "register: register error");
            return "error";
        }
    }

    private String login(String username, String password) {
        Log.d(TAG, "login: begin");
        Request.Builder builder = new Request.Builder();
        Request request = builder.url(loginUrl + "?username=" + username + "&" + "password=" + password).get().build();
        Log.d(TAG, "login: try");
        try {
            Response response = MainActivity.okHttpClientWithCookie.newCall(request).execute();
            Log.d(TAG, "login: response code is" + String.valueOf(response.code()));
            if (response.isSuccessful()) {
                Log.d(TAG, "login: get body");
                String retval = response.body().string();
                Log.d(TAG, "login: success");
                return retval;
            }
            return "error";
        } catch (IOException e) {
            Log.d(TAG, "login: here is err");
            e.printStackTrace();
            Log.d(TAG, "login: login error");
            return "error";
        }
    }
}
