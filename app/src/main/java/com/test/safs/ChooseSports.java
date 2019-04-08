package com.test.safs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.test.safs.Home.HomeActivity;

import java.util.ArrayList;

import static android.widget.Toast.LENGTH_SHORT;

public class ChooseSports extends Activity {

    private static final String TAG = "ChooseSports";

    private CheckBox checkbasketball, checkcricket, checkfootball;
    private Button buttonselectsports;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;

    @Override
    public void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_sports);

        checkbasketball = (CheckBox) findViewById(R.id.checkbasketball);
        checkcricket = (CheckBox) findViewById(R.id.checkcricket);
        checkfootball = (CheckBox) findViewById(R.id.checkfootball);
        buttonselectsports = (Button) findViewById(R.id.buttonselectsports);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        buttonselectsports.setOnClickListener(new OnClickListener() {

            //Run when button is clicked
            @Override
            public void onClick(View v) {


                ArrayList<String> sports = new ArrayList<>();
                if (!checkbasketball.isChecked() && !checkfootball.isChecked() && !checkcricket.isChecked()) {
                    Toast.makeText(ChooseSports.this, "Please select at least one sport", LENGTH_SHORT).show();
                } else {
                    if (checkbasketball.isChecked()) {
                        sports.add(checkbasketball.getText().toString());
                    }
                    if (checkcricket.isChecked()) {
                        sports.add(checkcricket.getText().toString());
                    }
                    if (checkfootball.isChecked()) {
                        sports.add(checkfootball.getText().toString());
                    }

                    FirebaseUser user = mAuth.getCurrentUser();
                    String userID = user.getUid();
                    for(int i = 0; i<sports.size(); i++)
                    {
                        myRef.child(userID).child("Sports").child(sports.get(i)).setValue("true");
                        Toast.makeText(ChooseSports.this,"Adding" + sports.get(i) + "to database",LENGTH_SHORT).show();
                    }
                }
               /* StringBuffer result = new StringBuffer();
                result.append("IPhone check : ").append(checkbasketball.isChecked());
                result.append("\nAndroid check : ").append(checkcricket.isChecked());
                result.append("\nWindows Mobile check :").append(checkcricket.isChecked());

                Toast.makeText(ChooseSports.this, result.toString(),
                        Toast.LENGTH_LONG).show();*/
                Intent intent = new Intent(ChooseSports.this, HomeActivity.class);
                startActivity(intent);
                finish();

            }
        });

    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Toast.makeText(ChooseSports.this,"Signed in with: " + currentUser.getEmail(),LENGTH_SHORT).show();
    }
}