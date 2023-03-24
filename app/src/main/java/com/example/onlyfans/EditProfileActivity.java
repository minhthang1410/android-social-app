package com.example.onlyfans;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.Manifest;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {
    private static final int MY_REQUEST_CODE = 10;
    private ImageView close;
    private ImageView image_profile;
    private TextView tv_change;
    private User user;
    private Bitmap imageBitmap;
    private String uploadUrl = "http://10.0.2.2/update-avatar";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        close = findViewById(R.id.close);
        image_profile = findViewById(R.id.image_profile);
        tv_change = findViewById(R.id.tv_change);
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");

        loadAvatar(user);
        close.setOnClickListener(view -> finish());
        tv_change.setOnClickListener(view -> uploadAvatar());
        image_profile.setOnClickListener(view -> ocClickRequestPermission());
    }

    private void ocClickRequestPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            openGallery();
            return;
        }
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        } else {
            String [] permission = {Manifest.permission.READ_EXTERNAL_STORAGE};
            requestPermissions(permission, MY_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                openGallery();
            }
        }

    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activityResultLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }

    private ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data == null) {
                            return;
                        }
                        Uri uri = data.getData();
                        try {
                            imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                            image_profile.setImageBitmap(imageBitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

    private void uploadAvatar() {
        RequestQueue queue = Volley.newRequestQueue(this);
        ByteArrayOutputStream bAOS = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bAOS);
        byte[] imageData = bAOS.toByteArray();

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, uploadUrl,
                response -> {
                    String resp = new String(response.data);
                    try {
                        JSONObject json = new JSONObject(resp);
                        String newAvatar = json.getString("data");
                        if (newAvatar.length() > 4) {
                            user.set_avatar(newAvatar);
                            loadAvatar(user);
                            Toast.makeText(EditProfileActivity.this, "Update avatar successful", Toast.LENGTH_SHORT).show();
                        } else {
                            JSONArray err = json.getJSONArray("errors");
                            String mess = err.getJSONObject(0).getString("message");
                            Toast.makeText(EditProfileActivity.this, mess, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(EditProfileActivity.this, "Unauthorized", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                params.put("avatar", new DataPart("avatar.jpg", imageData));
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", user.get_token());
                return headers;
            }
        };

        queue.add(multipartRequest);
    }

    private void loadAvatar(User user) {
        GlideUrl glideUrl = new GlideUrl(user.get_avatar(), new LazyHeaders.Builder().addHeader("Authorization", user.get_token()).build());
        Glide.with(this).load(glideUrl).into(image_profile);
    }
}