package com.test.safs.Home;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.test.safs.R;
import com.test.safs.Utils.FirebaseMethods;
import com.test.safs.models.Activity;
import com.test.safs.models.UserSettings;

public class OpenActivity extends AppCompatActivity {

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

    private Button joinActivityButton;

    //Variables
    private String append = "file://";
    private int activities_count = 0;
    private static final String TAG = "OpenActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_activity);
        mFirebaseMethods = new FirebaseMethods(OpenActivity.this);
        final String key = getIntent().getStringExtra("EXTRA_KEY");

        //Dialong initialization
        Dialog myDialog;
        myDialog = new Dialog(this);
        myDialog.setContentView(R.layout.dialog_join_activity);
        joinActivityButton = (Button) myDialog.findViewById(R.id.buttonJoinActivity);
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        joinActivityButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                mFirebaseMethods.JoinActivity(key);
                Log.d(TAG, "onClick: Added activity to user's Activities");
                finish();
            }
        });
        myDialog.show();
    }

}
