package com.test.safs.Utils;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;

//import com.ittianyu.BottomNavigationView.BottomNavigationView;
import com.test.safs.Engage.EngageActivity;
import com.test.safs.Home.HomeActivity;
import com.test.safs.Profile.ProfileActivity;
import com.test.safs.R;

public class BottomNavigationViewHelper {


    private static final String TAG = "BottomNavigationViewHel";


    public static void enableNavigation(final Context context, BottomNavigationView view) {
        view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.navigation_home:
                        Intent intent1 = new Intent(context, HomeActivity.class);
                        context.startActivity(intent1);
                        break;

                    case R.id.navigation_engage:
                        Intent intent2 = new Intent(context, EngageActivity.class);
                        context.startActivity(intent2);

                        break;

                    case R.id.navigation_me:
                        Intent intent3 = new Intent(context, ProfileActivity.class);
                        context.startActivity(intent3);

                        break;
                }

                return true;
            }
        });
    }
}
