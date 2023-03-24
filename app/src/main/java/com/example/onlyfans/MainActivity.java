package com.example.onlyfans;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    Fragment selecterFragment = null;
    Bundle bundle = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container , new HomeFragment()).commit();
        Intent intent = getIntent();
        User user = (User) intent.getSerializableExtra("user");
        bundle.putParcelable("user", user);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            menuItem -> {

                switch (menuItem.getItemId()) {
                    case R.id.nav_home:
                        selecterFragment = new HomeFragment();
                        selecterFragment.setArguments(bundle);
                        break;

                    case R.id.nav_search:
                        break;

                    case R.id.nav_add:
                        break;

                    case R.id.nav_heart:
                        break;

                    case R.id.nav_profile:
                        selecterFragment = new ProfileFragment();
                        selecterFragment.setArguments(bundle);
                        break;
                }

                if (selecterFragment != null){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container , selecterFragment).commit();
                }

                return true;
            };
}