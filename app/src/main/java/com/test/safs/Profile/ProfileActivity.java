package com.test.safs.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.test.safs.Engage.EngageActivity;
import com.test.safs.Engage.FriendRequestFragment;
import com.test.safs.Home.HomeActivity;
import com.test.safs.LoginActivity;
import com.test.safs.R;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";

    private FirebaseAuth mAuth;

 /*   @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.navigation_friendRequest:
                Intent intent = new Intent(ProfileActivity.this, FriendRequestFragment.class);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }*/

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Log.d(TAG, "onCreate: starting ");

        mAuth = FirebaseAuth.getInstance();

        TextView textViewlogout = (TextView) findViewById(R.id.textview_logout);
        textViewlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Logout Button Clicked");

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    // Name, email address, and profile photo Url
                    String name = user.getDisplayName();
                    String email = user.getEmail();
                    Toast.makeText(ProfileActivity.this,"Logging out " + email,Toast.LENGTH_SHORT).show();
                }
                mAuth.signOut();
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });


        TextView textViewactivityprofile = (TextView) findViewById(R.id.textview_activityprofile);
        textViewactivityprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this,UserProfile.class);
                startActivity(intent);
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavViewBar);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);
        BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()){
                    case R.id.navigation_home:
                        Intent intent1 = new Intent(ProfileActivity.this,HomeActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.navigation_engage:
                        Intent intent2 = new Intent(ProfileActivity.this,EngageActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.navigation_me:
                        break;
                }

                return true;
            }
        };
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("More");
        setSupportActionBar(toolbar);


    }

}
