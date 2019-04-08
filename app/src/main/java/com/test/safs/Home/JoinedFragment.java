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
import com.test.safs.Utils.JoinedFragmentAdapter;
import com.test.safs.models.Activity;

import java.util.ArrayList;

public class JoinedFragment extends Fragment {

    private static final String TAG = "JoinedFragment";

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
    private RecyclerView recyclerViewJoined;
    private JoinedFragmentAdapter mJoinedAdapter;
    private RecyclerView.LayoutManager layoutManagerNew;
    private ArrayList<Activity> listJoinedActivity;

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
        View view = inflater.inflate(R.layout.fragment_joined, container, false);

        recyclerViewJoined = (RecyclerView) view.findViewById(R.id.recyclerviewjoined);
        recyclerViewJoined.setHasFixedSize(true);

        layoutManagerNew = new LinearLayoutManager(getActivity());

        return view;
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listJoinedActivity = new ArrayList<Activity>();

        Log.d(TAG, "onCreate: ");
        //Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        myRef = database.getReference();
        final DatabaseReference myRef = database.getReference();

        // Write a message to the database
        DatabaseReference databaseReference = myRef.child(this.getString(R.string.dbname_user_activities_joined)).child(mUser.getUid());
        Query query = databaseReference.orderByChild(this.getString(R.string.field_date));

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot snapshot) {

                listJoinedActivity.clear();
                Log.d(TAG, "onDataChange: Value is " + snapshot);
                mJoinedAdapter = new JoinedFragmentAdapter(getActivity(), listJoinedActivity, new CustomItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {
                        Log.d(TAG, "onItemClick: Clicked position" + position);
                        TextView tv = v.findViewById(R.id.key);
                        Log.d(TAG, "onItemClick: "+tv.getText().toString());
                        Intent intent = new Intent(getActivity(),ActivityDetails.class);
                        intent.putExtra("EXTRA_FRAGMENT_NAME","JoinedFragment");
                        intent.putExtra("EXTRA_KEY",tv.getText().toString());
                        startActivity(intent);
                    }
                });
                recyclerViewJoined.setAdapter(mJoinedAdapter);
                mJoinedAdapter.notifyDataSetChanged();
                recyclerViewJoined.setLayoutManager(layoutManagerNew);

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    Log.d(TAG, "onDataChange: Getting Activities list");
                    Log.d(TAG, "onDataChange: "+ snapshot);
                    Activity activity = dataSnapshot.getValue(Activity.class);
                    listJoinedActivity.add(activity);
                    Log.d(TAG, "onDataChange: Activities added to list");
                    mJoinedAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
}
