package com.test.safs.Engage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.test.safs.Home.HomeActivity;
import com.test.safs.Profile.ProfileActivity;
import com.test.safs.Utils.BottomNavigationViewHelper;
import com.test.safs.R;

public class EngageActivity extends AppCompatActivity {

    private static final String TAG = "EngageActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_engage);

        Log.d(TAG, "onCreate: starting ");

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavViewBar);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);
        BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()){
                    case R.id.navigation_home:
                        Intent intent1 = new Intent(EngageActivity.this,HomeActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.navigation_engage:
                        break;
                    case R.id.navigation_me:
                        Intent intent3 = new Intent(EngageActivity.this, ProfileActivity.class);
                        startActivity(intent3);
                        break;
                }

                return true;
            }
        };
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
    }
}
