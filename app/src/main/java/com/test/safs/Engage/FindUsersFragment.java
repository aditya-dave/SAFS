package com.test.safs.Engage;

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
import com.test.safs.Utils.MyAdapter;
import com.test.safs.Utils.PlayersJoinedAdapter;
import com.test.safs.models.Activity;
import com.test.safs.models.User;
import com.test.safs.models.UserAccountSettings;
import com.test.safs.models.UserSettings;

import java.util.ArrayList;

public class FindUsersFragment extends Fragment{

    private static final String TAG = "FindUsersFragment";

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
    private RecyclerView recyclerviewfindusers;
    private FindUsersFragmentAdapter mAdapterUsers;
    private RecyclerView.LayoutManager layoutManagerFindusers;
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

        View view = inflater.inflate(R.layout.fragment_findusers, container, false);

        recyclerviewfindusers = (RecyclerView) view.findViewById(R.id.recyclerviewfindusers);
        recyclerviewfindusers.setHasFixedSize(true);

        layoutManagerFindusers = new LinearLayoutManager(getActivity());

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listUsers = new ArrayList<UserAccountSettings>();
        listUserIDs = new ArrayList<String>();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference();


        //Getting Users List from Firebase
        DatabaseReference databaseReference = myRef.child(this.getString(R.string.dbname_user_account_settings));
        Query query = databaseReference.orderByKey();

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot snapshot) {

                listUsers.clear();
                //Adapter
                mAdapterUsers = new FindUsersFragmentAdapter(getActivity(), listUsers, new CustomItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {
                        Activity activity = new Activity();
                        Log.d(TAG, "onItemClick: Clicked position" + position);
                        Log.d(TAG, "onItemClick: "+listUserIDs.get(position));
                        Intent intent = new Intent(getActivity(), ViewProfile.class);
                        intent.putExtra("EXTRA_USERKEY",listUserIDs.get(position));
                        startActivity(intent);
                    }
                });

                recyclerviewfindusers.setAdapter(mAdapterUsers);
                mAdapterUsers.notifyDataSetChanged();
                recyclerviewfindusers.setLayoutManager(layoutManagerFindusers);

                Log.d(TAG, "root Datasnapshot "+snapshot);
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: Getting Users list" + dataSnapshot);
                    userAccountSettings = dataSnapshot.getValue(UserAccountSettings.class);
                    listUsers.add(userAccountSettings);
                    listUserIDs.add(dataSnapshot.getKey());
                    Log.d(TAG, "onDataChange: Users added to list");
                }
                mAdapterUsers.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }

}
