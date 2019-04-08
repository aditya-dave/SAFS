package com.test.safs.Home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.test.safs.Profile.ViewProfile;
import com.test.safs.R;
import com.test.safs.Utils.CustomItemClickListener;
import com.test.safs.Utils.FirebaseMethods;
import com.test.safs.Utils.MyAdapter;
import com.test.safs.Utils.PlayersJoinedAdapter;
import com.test.safs.models.Activity;
import com.test.safs.models.UserAccountSettings;
import com.test.safs.models.UserSettings;

import java.util.ArrayList;

public class ActivityDetails extends AppCompatActivity {

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseMethods mFirebaseMethods;
    private DatabaseReference myRef;
    private String userID;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private UserSettings mUserSettings;

    //Variables
    private String append = "file://";
    private int activities_count = 0;
    private static final String TAG = "ActivityDetails";
    private Activity activity = new Activity();
    private UserAccountSettings userAccountSettings;
    private String userkey;
    private String calling_fragment;
    private String key;

    //RecyclerView
    private RecyclerView recyclerViewPlayersJoined;
    private PlayersJoinedAdapter mAdapterPlayersJoined;
    private RecyclerView.LayoutManager layoutManagerPlayersJoined;
    private ArrayList<UserAccountSettings> listPlayers;

    //Widgets
    private de.hdodenhof.circleimageview.CircleImageView profilephoto;
    private ImageView backarrow;
    private TextView sport_name;
    private TextView time;
    private TextView location;
    private TextView date;
    private android.support.v7.widget.AppCompatButton buttonJoinActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Log.d(TAG, "onCreate: ");

        listPlayers = new ArrayList<UserAccountSettings>();

        //RecyclerView
        recyclerViewPlayersJoined = (RecyclerView) findViewById(R.id.recyclerviewplayersjoined);
        recyclerViewPlayersJoined.setHasFixedSize(true);
        layoutManagerPlayersJoined = new LinearLayoutManager(ActivityDetails.this);

        //Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference();
        mFirebaseMethods = new FirebaseMethods(ActivityDetails.this);

        //Initializing widgets
        sport_name = (TextView) findViewById(R.id.sport_name);
        time = (TextView) findViewById(R.id.time);
        location = (TextView) findViewById(R.id.location);
        date = (TextView) findViewById(R.id.date);
        //Toolbar Backarrow
        backarrow = (ImageView) findViewById(R.id.backarrow);
        backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //Getting Activity key
        key = getIntent().getStringExtra("EXTRA_KEY");
        calling_fragment = getIntent().getStringExtra("EXTRA_FRAGMENT_NAME");

        DatabaseReference reference = myRef.child(this.getString(R.string.dbname_activities)).child(key);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Activity activity = new Activity();
                activity = dataSnapshot.getValue(Activity.class);
                sport_name.setText(activity.getSport_name());
                time.setText(activity.getTime());
                location.setText(activity.getLocation());
                date.setText(activity.getDate());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Getting Players List from Firebase
        DatabaseReference databaseReference = myRef.child(this.getString(R.string.dbname_user_activities_joined));
        Query query = databaseReference.orderByKey();

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot snapshot) {

                //Adapter
                mAdapterPlayersJoined = new PlayersJoinedAdapter(ActivityDetails.this, listPlayers, new CustomItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {
                        Activity activity = new Activity();
                        Log.d(TAG, "onItemClick: Clicked position" + position);
                        Log.d(TAG, "onItemClick: "+userkey);
                        //Intent intent = new Intent(getActivity(),OpenActivity.class);
                        Intent intent = new Intent(ActivityDetails.this, ViewProfile.class);
                        //intent.putExtra("EXTRA_KEY",tv.getText().toString());
                        intent.putExtra("EXTRA_USERKEY",userkey);
                        startActivity(intent);
                    }
                });

                recyclerViewPlayersJoined.setAdapter(mAdapterPlayersJoined);
                mAdapterPlayersJoined.notifyDataSetChanged();
                recyclerViewPlayersJoined.setLayoutManager(layoutManagerPlayersJoined);

                Log.d(TAG, "root Datasnapshot "+snapshot);
                for(DataSnapshot ds: snapshot.getChildren()){
                    Log.d(TAG, "Children : "+ds);
                    if(ds.child(key).exists()){
                        Log.d(TAG, "onDataChange: Exists"+ds.child(key));
                        userkey= (ds.getKey());
                        Log.d(TAG, "onDataChange: "+userkey);
                        myRef.child(getString(R.string.dbname_user_account_settings)).child(userkey).getRef().addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Log.d(TAG, "Snapshot of userAccountSettings"+dataSnapshot);
                                userAccountSettings = dataSnapshot.getValue(UserAccountSettings.class);
                                Log.d(TAG, "UserAccountSettings Values: "+userAccountSettings.toString());
                                userAccountSettings.setactivities(0);
                                userAccountSettings.setFriends(0);
                                listPlayers.add(userAccountSettings);
                                mAdapterPlayersJoined.notifyDataSetChanged();
                                Log.d(TAG, "List"+listPlayers);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });



        //JoinActivity Button
        buttonJoinActivity = findViewById(R.id.buttonJoinActivity);
        if(calling_fragment.equals("UpcomingFragment")){
            buttonJoinActivity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "onClick: Attempting to Join Activity");
                    mFirebaseMethods.JoinActivity(key);
                    Log.d(TAG, "onClick: Added activity to user's Activities");
                    finish();
                }
            });
        }
        if(calling_fragment.equals("JoinedFragment")){
            buttonJoinActivity.setText("Leave Activity");
            buttonJoinActivity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "onClick: Attempting to Leave Activity");
                    mFirebaseMethods.LeaveActivity(key);
                    Log.d(TAG, "onClick: Removed activity from user's Activities");
                    finish();
                }
            });
        }
        if(calling_fragment.equals("CreatedFragment")){
            buttonJoinActivity.setText("Delete Activity");
            buttonJoinActivity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "onClick: Attempting to Leave Activity");
                    mFirebaseMethods.DeleteActivity(key);
                    Log.d(TAG, "onClick: Removed activity from user's Activities");
                    finish();
                }
            });

        }

    }

}
