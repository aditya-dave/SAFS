package com.test.safs.Home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
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
import com.test.safs.R;
import com.test.safs.Utils.CustomItemClickListener;
import com.test.safs.Utils.CreatedFragmentAdapter;
import com.test.safs.models.Activity;

import java.util.ArrayList;

public class CreatedFragment extends Fragment {

    private static final String TAG = "CreatedFragment";

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageReference;
    private String userID;

    /*//RecyclerView
    private RecyclerView recyclerView;
    private MyAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Activity> listActivity;*/

    //RecyclerView
    private RecyclerView recyclerViewCreated;
    private CreatedFragmentAdapter mCreatedAdapter;
    private RecyclerView.LayoutManager layoutManagerNewCreated;
    private ArrayList<Activity> listCreatedActivity;

    //vars
    private ArrayList<Activity> mActivity;
    private ListView mListView;
    //private MainFeedListAdapter mAdapter;
    private int mResults;
    private Context context;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView: Creating Past fragment");
        View view = inflater.inflate(R.layout.fragment_created, container, false);

        recyclerViewCreated = (RecyclerView) view.findViewById(R.id.recyclerviewcreated);
        recyclerViewCreated.setHasFixedSize(true);

        layoutManagerNewCreated = new LinearLayoutManager(getActivity());

        return view;
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listCreatedActivity = new ArrayList<Activity>();

        Log.d(TAG, "onCreate: ");
        //Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        myRef = database.getReference();
        final DatabaseReference myRef = database.getReference();

        // Write a message to the database
        DatabaseReference databaseReference = myRef.child(this.getString(R.string.dbname_user_activities)).child(mUser.getUid());
        Query query = databaseReference.orderByChild(this.getString(R.string.field_date));

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot snapshot) {

                listCreatedActivity.clear();
                Log.d(TAG, "onDataChange: Value is " + snapshot);
                mCreatedAdapter = new CreatedFragmentAdapter(getActivity(), listCreatedActivity, new CustomItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {
                        Log.d(TAG, "onItemClick: Clicked position" + position);
                        TextView tv = v.findViewById(R.id.key);
                        Log.d(TAG, "onItemClick: "+tv.getText().toString());
                        Intent intent = new Intent(getActivity(),ActivityDetails.class);
                        intent.putExtra("EXTRA_FRAGMENT_NAME","CreatedFragment");
                        intent.putExtra("EXTRA_KEY",tv.getText().toString());
                        startActivity(intent);
                    }
                });
                recyclerViewCreated.setAdapter(mCreatedAdapter);
                mCreatedAdapter.notifyDataSetChanged();
                recyclerViewCreated.setLayoutManager(layoutManagerNewCreated);

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    Log.d(TAG, "onDataChange: Getting Activities list");
                    Log.d(TAG, "onDataChange: "+ snapshot);
                    Activity activity = dataSnapshot.getValue(Activity.class);
                    listCreatedActivity.add(activity);
                    Log.d(TAG, "onDataChange: Activities added to list");
                    mCreatedAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    /*@Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listActivity = new ArrayList<Activity>();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference();



        // Write a message to the database
        DatabaseReference databaseReference = myRef.child(this.getString(R.string.dbname_activities));
        Query query = databaseReference.orderByKey();

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String s) {
                mAdapter = new MyAdapter(getActivity(), listActivity, new CustomItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {
                        Activity activity = new Activity();
                        Log.d(TAG, "onItemClick: Clicked position" + position);
                        TextView tv = v.findViewById(R.id.key);
                        Log.d(TAG, "onItemClick: "+tv.getText().toString());
                        Intent intent = new Intent(getActivity(),OpenActivity.class);
                        intent.putExtra("EXTRA_KEY",tv.getText().toString());
                        startActivity(intent);
                    }
                });
                recyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
                recyclerView.setLayoutManager(layoutManager);

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    if(snapshot.child(mAuth.getInstance().getCurrentUser().getUid()).exists()){

                        Log.d(TAG, "onDataChange: Getting Activities list");
                        Log.d(TAG, "onDataChange: "+ snapshot);
                        Activity activity = dataSnapshot.getValue(Activity.class);
                        listActivity.add(activity);
                        Log.d(TAG, "onDataChange: Activities added to list");
                        mAdapter.notifyDataSetChanged();
                    }

                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot snapshot) {

                *//*mAdapter = new MyAdapter(getActivity(), listActivity, new CustomItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {
                        Activity activity = new Activity();
                        Log.d(TAG, "onItemClick: Clicked position" + position);
                        TextView tv = v.findViewById(R.id.key);
                        Log.d(TAG, "onItemClick: "+tv.getText().toString());
                        Intent intent = new Intent(getActivity(),OpenActivity.class);
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

                }*//*
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }*/

}
