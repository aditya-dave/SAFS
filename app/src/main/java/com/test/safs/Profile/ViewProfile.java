package com.test.safs.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.test.safs.R;
import com.test.safs.Utils.FirebaseMethods;
import com.test.safs.Utils.SectionsStatePagerAdapter;
import com.test.safs.Utils.UniversalImageLoader;
import com.test.safs.models.User;
import com.test.safs.models.UserAccountSettings;
import com.test.safs.models.UserSettings;

public class ViewProfile extends AppCompatActivity {

    private static final String TAG = "UserProfile";

    private SectionsStatePagerAdapter pagerAdapter;
    private ViewPager viewPager;
    private RelativeLayout mRelativeLayout;
    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseMethods mFirebaseMethods;
    private DatabaseReference myRef;
    private DatabaseReference FriendRequestRef;

    //Vars
    private Button Addfriendbutton;
    private ImageView backarrow;
    private ProgressBar mProgressBar;
    private ImageView profilephoto;
    private TextView name;
    private TextView activities;
    private TextView friends;
    private TextView email;
    private TextView phone_number;
    private TextView user_id;
    private TextView userProfile;
    private String userkey;
    private String CURRENT_STATE;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        Log.d(TAG, "onCreate: UserProfile");
        setupActivityWidgets();
        initImageLoader();
        setProfileImage();
        userkey=getIntent().getStringExtra("EXTRA_USERKEY");

        MaintainAddFriendButton();
        if(!mAuth.getCurrentUser().getUid().equals(userkey)){
            Addfriendbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                /*Intent intent = new Intent(ViewProfile.this, EditProfileActivity.class);
                intent.putExtra(getString(R.string.calling_activity),getString(R.string.editprofile_activity));
                startActivity(intent);*/
                if(CURRENT_STATE.equals("not_friends")){
                    SendFriendRequest();
                }
                if(CURRENT_STATE.equals("request_sent")){
                    CancelFriendRequest();
                }

                }
            });
        }
        else{
            Addfriendbutton.setEnabled(false);
            Addfriendbutton.setVisibility(View.GONE);
        }


        mFirebaseMethods = new FirebaseMethods(ViewProfile.this);
        Log.d(TAG, "onCreate: FirebaseMethods Instance created");

        /*Toolbar toolbar = findViewById(R.id.profileToolbar);
        setSupportActionBar(toolbar);*/
        backarrow = (ImageView) findViewById(R.id.backarrow);
        backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // This method is called once with the initial value and again
            // whenever data at this location is updated.
            //retrieve user information from the database
            setProfileWidget(mFirebaseMethods.getUserSettingsViewProfile(dataSnapshot,userkey));
        }

        @Override
        public void onCancelled(DatabaseError error) {
            // Failed to read value
            Log.w(TAG, "Failed to read value.", error.toException());
        }
    });

    }

    private void MaintainAddFriendButton(){
        FriendRequestRef.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(userkey)){
                    String request_type = dataSnapshot.child(userkey).child("request_type").getValue().toString();

                    if(request_type.equals("sent")){
                        CURRENT_STATE="request_sent";
                        Addfriendbutton.setText("Cancel Friend Request");

                    }
                    if(request_type.equals("received")){
                        CURRENT_STATE="request_received";
                        Addfriendbutton.setText("Accept Friend Request");

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void CancelFriendRequest() {
        FriendRequestRef.child(mAuth.getCurrentUser().getUid()).child(userkey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    FriendRequestRef.child(userkey).child(mAuth.getCurrentUser().getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                CURRENT_STATE="not_friends";
                                Addfriendbutton.setText("Add Friend");
                            }
                        }
                    });
                }
            }
        });
    }


    private void SendFriendRequest() {
        FriendRequestRef.child(mAuth.getCurrentUser().getUid()).child(userkey).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    FriendRequestRef.child(userkey).child(mAuth.getCurrentUser().getUid()).child("request_type").setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                CURRENT_STATE="request_sent";
                                Addfriendbutton.setText("Cancel Friend Request");
                            }
                        }
                    });
                }
            }
        });
    }

    private void setProfileWidget(UserSettings userSettings){
        Log.d(TAG, "setProfileWidget: setting widgets with data retrieved from firebase database");

        User user = userSettings.getUser();
        UserAccountSettings settings = userSettings.getSettings();

        UniversalImageLoader.setImage(settings.getprofilephoto(),profilephoto,null,"");
        mProgressBar.setVisibility(View.GONE);

        String nameUpper = settings.getname();
        String nameCap = nameUpper.substring(0,1).toUpperCase() + nameUpper.substring(1);
        name.setText(nameCap);
        activities.setText(String.valueOf(settings.getactivities()));
        friends.setText(String.valueOf(settings.getFriends()));
        userProfile.setText(String.valueOf(settings.getname()));
        Addfriendbutton.setText("Add Friend");

    }

    private void initImageLoader() {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(ViewProfile.this);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());

    }

    private void setProfileImage() {
        Log.d(TAG, "setProfileImage: Setting profile photo");
        String imageURL = "cdn.vox-cdn.com/thumbor/74woraKEl8_x1PNSyxhpL85OCtk=/0x0:2040x1360/920x613/filters:focal(857x517:1183x843):format(webp)/cdn.vox-cdn.com/uploads/chorus_image/image/62857528/wjoel_180413_1777_android_001.0.jpg";
        UniversalImageLoader.setImage(imageURL, profilephoto, mProgressBar, "");
        Log.d(TAG, "setProfileImage: Profile photo set");
    }

    private void setupActivityWidgets() {

        mAuth = FirebaseAuth.getInstance();
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference();
        FriendRequestRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.dbname_friendrequests));

        viewPager = (ViewPager) findViewById(R.id.container);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.rellayout2);

        profilephoto = (ImageView) findViewById(R.id.profilephoto);
        name = (TextView) findViewById(R.id.name);
        activities = (TextView) findViewById(R.id.textview_activities);
        friends = (TextView) findViewById(R.id.textviewfriends);

        CURRENT_STATE="not_friends";
        Addfriendbutton = (Button) findViewById(R.id.button_editprofile);
        userProfile = (TextView) findViewById(R.id.userprofile);
        mProgressBar = (ProgressBar) findViewById(R.id.profileProgressBar);
    }
}
