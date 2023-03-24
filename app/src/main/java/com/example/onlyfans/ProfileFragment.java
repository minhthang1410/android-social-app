package com.example.onlyfans;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;

import java.io.Serializable;

public class ProfileFragment extends Fragment {
    private ImageView image_profile;
    private TextView followers;
    private TextView following;
    private TextView posts;
    private TextView bio;
    private TextView username;
    private Button edit_profile;
    private User user;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fra
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        assert getArguments() != null;
        user = getArguments().getParcelable("user");
        context = getContext();
        image_profile = view.findViewById(R.id.image_profile);
        followers = view.findViewById(R.id.followers);
        following = view.findViewById(R.id.following);
        posts = view.findViewById(R.id.posts);
        bio = view.findViewById(R.id.bio);
        username = view.findViewById(R.id.username);
        edit_profile = view.findViewById(R.id.edit_profile);

        loadProfile(user);

        edit_profile.setOnClickListener(view1 -> {
            Intent intent = new Intent(context, EditProfileActivity.class);
            intent.putExtra("user", (Serializable) user);
            startActivity(intent);
        });

        return view;
    }

    private void loadProfile(User user) {
        username.setText(user.get_username());
        bio.setText(user.get_bio());
        GlideUrl glideUrl = new GlideUrl(user.get_avatar(), new LazyHeaders.Builder().addHeader("Authorization", user.get_token()).build());
        Glide.with(context).load(glideUrl).into(image_profile);
        followers.setText("0");
        following.setText("0");
        posts.setText("0");
    }
}