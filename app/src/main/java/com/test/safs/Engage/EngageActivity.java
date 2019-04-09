package com.test.safs.Engage;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.test.safs.Home.HomeActivity;
import com.test.safs.Profile.ProfileActivity;
import com.test.safs.R;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuInflater;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.test.safs.Utils.SectionsPagerAdapterEngage;
import com.test.safs.Utils.UniversalImageLoader;

public class EngageActivity extends AppCompatActivity {

    private static final String TAG = "EngageActivity";

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageReference;
    private String userID;


    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.navigation_friendRequest:
                Fragment mFragment = null;
                mFragment = new FriendRequestFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.rellayoutcenterviewpager, mFragment).commit();
        }
        return super.onOptionsItemSelected(item);
    }*/


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_engage);

        Log.d(TAG, "onCreate: starting ");
        setUpViewPager();
        initImageLoader();

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Engage");
        setSupportActionBar(toolbar);

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

    private void initImageLoader() {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(EngageActivity.this);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());

    }

    private void setUpViewPager(){
        SectionsPagerAdapterEngage adapter = new SectionsPagerAdapterEngage(getSupportFragmentManager());
        //adapter.addFragment(new FriendsFragment());
        adapter.addFragment(new FindUsersFragment());
        //adapter.addFragment(new FriendRequestFragment());
        ViewPager viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
        viewPager.setOffscreenPageLimit(3);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setText("Find Users");
        //tabLayout.getTabAt(1).setText("Find Users");
        //tabLayout.getTabAt(2).setText("Friend Requests");
    }
}
