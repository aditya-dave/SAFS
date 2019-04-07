package com.test.safs.Home;

import android.app.DatePickerDialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.test.safs.Profile.EditProfileActivity;
import com.test.safs.R;
import com.test.safs.Utils.FirebaseMethods;
import com.test.safs.Utils.ImageManager;
import com.test.safs.Utils.UniversalImageLoader;
import com.test.safs.models.Activity;
import com.test.safs.models.User;
import com.test.safs.models.UserAccountSettings;
import com.test.safs.models.UserActivitiesJoined;
import com.test.safs.models.UserSettings;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CreateActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, AdapterView.OnItemSelectedListener {

    private static final String TAG = "CreateActivity";

    //Widgets
    private EditText edittext_sport_name, edittext_date, edittext_location;
    private AppCompatButton createactivityButton;
    private String time;

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
    private UserAccountSettings mUserAccountSettings;

    //Variables
    private String append = "file://";
    private int activities_count = 0;
    private String host_name;
    private Activity activity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_activity);
        mFirebaseMethods = new FirebaseMethods(CreateActivity.this);
        setUpFirebaseAuth();


        //Initializing the widgets
        edittext_sport_name = (EditText) findViewById(R.id.edittext_sport);
        Spinner spinner = findViewById(R.id.spinner_time);
        edittext_date = (EditText) findViewById(R.id.edittext_date);
        edittext_location = (EditText) findViewById(R.id.edittext_location);

        edittext_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment datepicker = new DatePickerFragment();
                datepicker.show(getSupportFragmentManager(), "date picker");
            }
        });


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.Time, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        createactivityButton = (AppCompatButton) findViewById(R.id.button_createActivity);

        createactivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //upload the data to firebase
                Log.d(TAG, "onClick: Attempting to create a new activity");
                Toast.makeText(CreateActivity.this, "Attempting to create a new activity", Toast.LENGTH_SHORT).show();
                //String host_name = mUserSettings.getSettings().getDisplay_name();
                //String host_name = mAuth.getCurrentUser().getDisplayName();
                final Activity activity = new Activity();
                /*Log.d(TAG, mUserAccountSettings.getDisplay_name());
                Toast.makeText(CreateActivity.this, mUserAccountSettings.getDisplay_name(), Toast.LENGTH_LONG).show();*/
                //String host_name=activity.getName();
                String date = edittext_date.getText().toString();
                String sport_name = edittext_sport_name.getText().toString();
                String location = edittext_location.getText().toString();
                activity.setLocation(location);
                activity.setSport_name(sport_name);
                activity.setDate(date);
                activity.setProfilephoto("");
                activity.setTime(time);
                Log.d(TAG, "onClick: Calling createnewActivity in FirebaseMethods");
                String activity_key = mFirebaseMethods.createnewActivity(host_name, sport_name, date, location, time, activities_count);
                activity.setKey(activity_key);
                finish();

            }
        });

    }

    public void setUpFirebaseAuth() {

        //Firebase

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        mAuth = FirebaseAuth.getInstance();
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference();
        userID = mAuth.getCurrentUser().getUid();
        Log.d(TAG, "onDataChange: activities count" + activities_count);
        // RequiresPermission Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Log.d(TAG, "Value in setUpFirebaseAuth is: " + dataSnapshot);

                activities_count = mFirebaseMethods.getActivitiesCount(dataSnapshot);
                Log.d(TAG, "onDataChange: activities count" + activities_count);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        /*SimpleDateFormat format = new SimpleDateFormat("mm-dd-yyyy", Locale.ENGLISH);
        String strDate = format.format(c.getTime());*/
        String currentdatestring = DateFormat.getDateInstance().format(c.getTime());
        edittext_date.setText(currentdatestring);

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        time = adapterView.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}