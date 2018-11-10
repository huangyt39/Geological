package com.example.eaf.coresampleimgprocess;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private String registerUrl = "http://10.0.2.2:5000/register";
    private String loginUrl = "http://10.0.2.2:5000/login";

    Button registerButton;
    Button loginButton;
    EditText usernameEditText;
    EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = (EditText) findViewById(R.id.username_edittext);
        passwordEditText = (EditText) findViewById(R.id.password_edittext);

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
                MainActivity.currentUsername = username;
                new LoginNetworkTask().execute(username, password);
                Log.d(TAG, "onClick: login task begin " + username + " " + password);
            }
        });
    }

    private void closeActivity(String actionResult) {
        if(actionResult=="success") {
            Intent intent = new Intent();
            intent.putExtra("result", "success");
            LoginActivity.this.setResult(RESULT_OK, intent);
            LoginActivity.this.finish();
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
                closeActivity("success");
            }
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
                closeActivity("success");
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
