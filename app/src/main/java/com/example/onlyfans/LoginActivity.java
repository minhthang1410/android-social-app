package com.example.onlyfans;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class LoginActivity extends AppCompatActivity {

    private Button loginBtn;
    private EditText username, password;
    private TextView txt_signup;
    private String url = "http://10.0.2.2/graphql";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        loginBtn = findViewById(R.id.login);
        txt_signup = findViewById(R.id.txt_signup);

        txt_signup.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this , RegisterActivity.class)));

        loginBtn.setOnClickListener(view -> {
            if (username.getText().toString().isEmpty() || password.getText().toString().isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please enter both the values", Toast.LENGTH_SHORT).show();
                return;
            }
            login(username.getText().toString(), password.getText().toString());
        });
    }


    private void login(String username, String password) {
        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
        JSONObject data = buildParamsLogin(username, password);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, data,
                response -> {
                    try {
                        JSONObject dataResp = response.getJSONObject("data");
                        JSONObject login = dataResp.getJSONObject("login");
                        String token = login.getString("token");
                        String usernameResp = login.getString("username");
                        String avatar = login.getString("avatar");
                        String bio = login.getString("bio");
                        Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                        User user = new User(token, usernameResp, bio, avatar);
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("user", (Serializable) user);
                        startActivity(intent);
                    } catch (JSONException e) {
                        JSONArray errorResp = null;
                        JSONObject errorValue = null;
                        try {
                            errorResp = response.getJSONArray("errors");
                            errorValue = errorResp.getJSONObject(0);
                            String errorMessage = errorValue.getString("message");
                            Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        } catch (JSONException jsonException) {
                            jsonException.printStackTrace();
                        }
                    }
                },
                error -> {
                    Toast.makeText(LoginActivity.this, "Something wrongs: " + error, Toast.LENGTH_SHORT).show();
                });
        queue.add(request);
    }

    private JSONObject buildParamsLogin(String username, String password) {
        JSONObject data = new JSONObject();
        JSONObject variables = new JSONObject();
        String query = "mutation Login($username: String!, $password: String!) {login(username: $username, password: $password) {\r\ntoken\r\nusername\r\navatar\r\nbio}}";
        try {
            variables.put("username", username);
            variables.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            data.put("query", query);
            data.put("variables", variables);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return data;
    }
}
