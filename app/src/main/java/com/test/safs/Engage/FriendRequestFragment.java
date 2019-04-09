package com.test.safs.Engage;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.test.safs.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.test.safs.Home.ActivityDetails;
import com.test.safs.Profile.ViewProfile;
import com.test.safs.R;
import com.test.safs.Utils.CustomItemClickListener;
import com.test.safs.Utils.FindUsersFragmentAdapter;
import com.test.safs.Utils.FirebaseMethods;
import com.test.safs.Utils.FriendRequestFragmentAdapter;
import com.test.safs.Utils.MyAdapter;
import com.test.safs.Utils.PlayersJoinedAdapter;
import com.test.safs.models.Activity;
import com.test.safs.models.User;
import com.test.safs.models.UserAccountSettings;
import com.test.safs.models.UserSettings;

import java.util.ArrayList;

public class FriendRequestFragment extends Fragment {

    private static final String TAG = "FriendRequestFragment";

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
    private Activity activity = new Activity();
    private UserAccountSettings userAccountSettings;
    private String userkey;
    private String calling_fragment;
    private String key;

    //RecyclerView
    private RecyclerView recyclerViewfriendrequests;
    private FriendRequestFragmentAdapter mAdapterFriendRequests;
    private RecyclerView.LayoutManager layoutManagerFriendRequests;
    private ArrayList<UserAccountSettings> listUsers;
    private ArrayList<String> listUserIDs;

    //Widgets
    private de.hdodenhof.circleimageview.CircleImageView profilephoto;
    private ImageView backarrow;
    private TextView sport_name;
    private TextView time;
    private TextView location;
    private TextView date;
    private android.support.v7.widget.AppCompatButton buttonJoinActivity;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView: ");
        View view = inflater.inflate(R.layout.fragment_friendrequests, container, false);

        recyclerViewfriendrequests = (RecyclerView) view.findViewById(R.id.recyclerviewfriendrequests);
        recyclerViewfriendrequests.setHasFixedSize(true);

        layoutManagerFriendRequests = new LinearLayoutManager(getActivity());

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        listUsers = new ArrayList<UserAccountSettings>();
        listUserIDs = new ArrayList<String>();

        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference();


        //Getting Users List from Firebase
        DatabaseReference databaseReference = myRef.child(this.getString(R.string.dbname_friendrequests));
        Query query = databaseReference.orderByKey();

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot snapshot) {

                //Adapter
                mAdapterFriendRequests = new FriendRequestFragmentAdapter(getActivity(), listUsers, new CustomItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {
                        Log.d(TAG, "onItemClick: Clicked position" + position);
                        Log.d(TAG, "onItemClick: " + listUserIDs.get(position));
                        Intent intent = new Intent(getActivity(), ViewProfile.class);
                        intent.putExtra("EXTRA_USERKEY", listUserIDs.get(position));
                        startActivity(intent);
                    }
                });

                recyclerViewfriendrequests.setAdapter(mAdapterFriendRequests);
                mAdapterFriendRequests.notifyDataSetChanged();
                recyclerViewfriendrequests.setLayoutManager(layoutManagerFriendRequests);

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    if (dataSnapshot.getKey().equals(mAuth.getCurrentUser().getUid())) {

                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                            Log.d(TAG, "Andar ka data: " + dataSnapshot1);

                            String request_type = dataSnapshot1.child("request_type").getValue().toString();

                            if (request_type.equals("received")) {

                                listUserIDs.add(dataSnapshot1.getKey());
                                Log.d(TAG, "Adding " + dataSnapshot1.getKey() + " to listUserIDs");

                                DatabaseReference dbref = myRef.child(getString(R.string.dbname_user_account_settings)).child(dataSnapshot1.getKey());
                                dbref.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        Log.d(TAG, ""+dataSnapshot);
                                        userAccountSettings = dataSnapshot.getValue(UserAccountSettings.class);
                                        Log.d(TAG, ""+userAccountSettings.toString());
                                        listUsers.add(userAccountSettings);
                                        Log.d(TAG, "onDataChange: "+listUsers);
                                        Log.d(TAG, "onDataChange: Users Added to list");
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }
                    }

                    /*Log.d(TAG, "ListUserIDs" + listUserIDs);
                    userAccountSettings = dataSnapshot.getValue(UserAccountSettings.class);
                    Log.d(TAG, userAccountSettings.toString());
                    listUsers.add(userAccountSettings);
                    Log.d(TAG, "onDataChange: Users added to list");*/
                }
                mAdapterFriendRequests.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }

   /* private void functionidk(String key) {
        DatabaseReference dbref = myRef.child(getString(R.string.dbname_user_account_settings)).child(key);
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "" + dataSnapshot);
                userAccountSettings = dataSnapshot.getValue(UserAccountSettings.class);
                Log.d(TAG, "" + userAccountSettings.toString());
                listUsers.add(userAccountSettings);
                Log.d(TAG, "onDataChange: " + listUsers);
                Log.d(TAG, "onDataChange: Users Added to list");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }*/
}
