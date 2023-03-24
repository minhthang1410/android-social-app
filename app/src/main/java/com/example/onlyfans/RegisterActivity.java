package com.example.onlyfans;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    private Button registerBtn;
    private EditText email, username, password, confirmPassword;
    private TextView txt_login;
    private String url = "http://10.0.2.2/graphql";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerBtn = findViewById(R.id.register);
        email = findViewById(R.id.email);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        txt_login = findViewById(R.id.txt_login);

        txt_login.setOnClickListener(view -> startActivity(new Intent(RegisterActivity.this , LoginActivity.class)));

        registerBtn.setOnClickListener(view -> {
            if (email.getText().toString().isEmpty() || username.getText().toString().isEmpty() || password.getText().toString().isEmpty() || confirmPassword.getText().toString().isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Please enter all the values", Toast.LENGTH_SHORT).show();
                return;
            }
            register(email.getText().toString(), username.getText().toString(), password.getText().toString(), confirmPassword.getText().toString());
        });
    }

    private void register(String email, String username, String password, String confirmPassword) {
        RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
        JSONObject data = buildParamsRegister(email, username, password, confirmPassword);
        Log.e("data", String.valueOf(data));
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, data,
                response -> {
                    try {
                        JSONObject dataResp = response.getJSONObject("data");
                        String registerResp = dataResp.getString("register");
                        Toast.makeText(RegisterActivity.this, registerResp, Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        JSONArray errorResp = null;
                        JSONObject errorValue = null;
                        try {
                            errorResp = response.getJSONArray("errors");
                            errorValue = errorResp.getJSONObject(0);
                            String errorMessage = errorValue.getString("message");
                            Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        } catch (JSONException jsonException) {
                            jsonException.printStackTrace();
                        }
                    }
                },
                error -> {
                    Toast.makeText(RegisterActivity.this, "Something wrongs: " + error, Toast.LENGTH_SHORT).show();
                });
        queue.add(request);
    }

    private JSONObject buildParamsRegister(String email, String username, String password, String confirmPassword) {
        JSONObject data = new JSONObject();
        JSONObject variables = new JSONObject();
        JSONObject registerInput = new JSONObject();
        String query = "mutation Register($registerInput: RegisterInput) {register(registerInput: $registerInput)}";
        try {
            registerInput.put("email", email);
            registerInput.put("username", username);
            registerInput.put("password", password);
            registerInput.put("confirmPassword", confirmPassword);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            variables.put("registerInput", registerInput);
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