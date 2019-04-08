package com.test.safs.Home;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.test.safs.R;
import com.test.safs.Utils.ActivityInformation;
import com.test.safs.Utils.CustomItemClickListener;
import com.test.safs.Utils.MainFeedListAdapter;
import com.test.safs.Utils.MyAdapter;
import com.test.safs.models.Activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UpcomingFragment extends Fragment{

    private static final String TAG = "UpcomingFragment";

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageReference;
    private String userID;

    //RecyclerView
    private RecyclerView recyclerView;
    private MyAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Activity> listActivity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_upcoming, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getActivity());

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listActivity = new ArrayList<Activity>();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference();


        // Write a message to the database
        DatabaseReference databaseReference = myRef.child(this.getString(R.string.dbname_activities));
        Query query = databaseReference.orderByChild(this.getString(R.string.field_date));
        databaseReference.keepSynced(true);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot snapshot) {

                mAdapter = new MyAdapter(getActivity(), listActivity, new CustomItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {
                        Activity activity = new Activity();
                        Log.d(TAG, "onItemClick: Clicked position" + position);
                        TextView tv = v.findViewById(R.id.key);
                        Log.d(TAG, "onItemClick: "+tv.getText().toString());
                        //Intent intent = new Intent(getActivity(),OpenActivity.class);
                        Intent intent = new Intent(getActivity(),ActivityDetails.class);
                        intent.putExtra("EXTRA_FRAGMENT_NAME","UpcomingFragment");
                        intent.putExtra("EXTRA_KEY",tv.getText().toString());
                        startActivity(intent);
                    }
                });
                recyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
                recyclerView.setLayoutManager(layoutManager);

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    Log.d(TAG, "onDataChange: Getting Activities list");
                    Log.d(TAG, "onDataChange: "+ snapshot);
                    Activity activity = dataSnapshot.getValue(Activity.class);
                    listActivity.add(activity);
                    Log.d(TAG, "onDataChange: Activities added to list");
                    mAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

}
