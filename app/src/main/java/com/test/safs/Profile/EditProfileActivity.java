package com.test.safs.Profile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.test.safs.Dialogs.ConfirmPasswordDialog;
import com.test.safs.R;
import com.test.safs.Utils.FirebaseMethods;
import com.test.safs.Utils.UniversalImageLoader;
import com.test.safs.models.User;
import com.test.safs.models.UserAccountSettings;
import com.test.safs.models.UserSettings;

import java.io.IOException;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity implements ConfirmPasswordDialog.OnConfirmPasswordListener {
    private static final String TAG = "EditProfileActivity";
    private final int PICK_IMAGE_REQUEST = 71;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseMethods mFirebaseMethods;
    private DatabaseReference myRef;
    private String userID;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    //Widgets
    private EditText mDisplayname, mEmail, mPhone_number;
    private TextView changephoto;
    private Spinner gender;
    private CircleImageView mProfilePhoto;
    private String selectedgender;
    private ProgressBar progressBar;
    private ImageView mProfilephoto;
    private ImageView back_arrow;
    private ImageView checkmark;
    private String mProfileImageUrl;

    //Variables
    private UserSettings mUserSettings;
    private Context mContext;
    private Uri filePath;
    private String url;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);

        mContext = EditProfileActivity.this;
        UploadTask uploadTask;

        //Widgets
        mDisplayname = (EditText) findViewById(R.id.input_name);
        mEmail = (EditText) findViewById(R.id.input_email);
        mPhone_number = (EditText) findViewById(R.id.input_phone);
        gender = (Spinner) findViewById(R.id.spinner_gender);
        changephoto = (TextView) findViewById(R.id.changeprofilephoto);
        mProfilePhoto = (CircleImageView) findViewById(R.id.profilephoto);
        mFirebaseMethods = new FirebaseMethods(EditProfileActivity.this);
        progressBar = (ProgressBar) findViewById(R.id.profileProgressBar);

        //Firebase
        setUpFirebaseAuth();


        //back arrow for navigating back to userprofile Activity

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        back_arrow = (ImageView) findViewById(R.id.backarrow);
        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Navigating back to User Profile Activity");
                finish();
            }
        });

        //Checkmark for saving changes
        checkmark = (ImageView) findViewById(R.id.saveChanges);
        checkmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Attempting to save changes");
                saveProfileSettings();
            }
        });


        mProfilephoto = (ImageView) findViewById(R.id.profilephoto);
        initImageLoader();
        //setProfileImage();

        // RequiresPermission Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Log.d(TAG, "Value is: " + dataSnapshot);

                //retrieve user information from the database
                //setProfileWidget(mFirebaseMethods.getUserSettings(dataSnapshot));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        //spinner = (Spinner) findViewById(R.id.spinner_gender);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getBaseContext(), R.array.Genders,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        gender.setAdapter(adapter);
        gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                selectedgender = gender.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //Change the profile photo
        changephoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
                final StorageReference ref = storageReference.child("profilephotos/" + mAuth.getCurrentUser().getUid());
                //mFirebaseMethods.updateprofilephoto(url);
                //mFirebaseMethods.uploadprofilephoto(url);
            }
        });
    }

    private void uploadImage() {

        if (filePath != null) {

            //mFirebaseMethods.uploadprofilephoto(url);

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            final StorageReference ref = storageReference.child("profilephotos/"+ mAuth.getCurrentUser().getUid());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(EditProfileActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(EditProfileActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // Got the download URL for 'users/me/profile.png'
                    mFirebaseMethods.updateprofilephoto(uri.toString());
                    mProfileImageUrl = uri.toString();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                mProfilePhoto.setImageBitmap(bitmap);
                uploadImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void setProfileWidget(UserSettings userSettings) {
        Log.d(TAG, "setProfileWidget: setting widgets with data retrieved from firebase database");

        mUserSettings = userSettings;

        User user = userSettings.getUser();
        UserAccountSettings settings = userSettings.getSettings();

        UniversalImageLoader.setImage(settings.getprofilephoto(), mProfilephoto, progressBar, "");


        String nameUpper = settings.getname();
        String nameCap = nameUpper.substring(0, 1).toUpperCase() + nameUpper.substring(1);
        mDisplayname.setText(nameCap);
        mEmail.setText(userSettings.getUser().getEmail());
        mPhone_number.setText(String.valueOf(userSettings.getUser().getPhone_number()));


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
        // RequiresPermission Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Log.d(TAG, "Value is: " + dataSnapshot);

                //retrieve user information from the database
                setProfileWidget(mFirebaseMethods.getUserSettings(dataSnapshot));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }


    private void initImageLoader() {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(EditProfileActivity.this);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());

    }


    private void saveProfileSettings() {
        final String displayname = mDisplayname.getText().toString();
        final String emailID = mEmail.getText().toString();
        final Long phone_number = Long.parseLong(mPhone_number.getText().toString());
        final String gender = selectedgender;


        //Case 2: User changed their email
        if (!mUserSettings.getUser().getEmail().equals(emailID)) {
            //Step 1) Reauthenticate the user
            //      -Confirm the email and password


            ConfirmPasswordDialog dialog = new ConfirmPasswordDialog();
            dialog.show(getSupportFragmentManager(), getString(R.string.confirm_password_dialog));

            //Step 2) Check if the email is already registered
            //      -fetchProvidersForEmail(String email)
            //Step 3) Change the email
            //      -submit the new Email to the database authentication
        }
        /**
         * Change the rest of the settings that do not require uniqueness
         */
        if (!mUserSettings.getSettings().getname().equals(displayname)) {
            //update displayname
            mFirebaseMethods.updateUserAccountSettings(displayname, 0);
            Log.d(TAG, "saveProfileSettings: Display name updated");
            Toast.makeText(EditProfileActivity.this, "Name changed", Toast.LENGTH_SHORT).show();

        }
        if (mUserSettings.getUser().getPhone_number() != phone_number) {
            //update phone number
            mFirebaseMethods.updateUserAccountSettings(null, phone_number);
            Log.d(TAG, "saveProfileSettings: Phone Number updated");
            Toast.makeText(EditProfileActivity.this, "Phone number changed", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void applyTexts(String password) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // Get auth credentials from the user for re-authentication. The example below shows
        // email and password credentials but there are multiple possible providers,
        // such as GoogleAuthProvider or FacebookAuthProvider.
        AuthCredential credential = EmailAuthProvider
                .getCredential(user.getEmail(), password);

        // Prompt the user to re-provide their sign-in credentials
        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    Log.d(TAG, "User re-authenticated.");
                    mAuth.fetchSignInMethodsForEmail(mEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                        @Override
                        public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {

                            if (task.isSuccessful()) {

                                try {
                                    if (task.getResult().getSignInMethods().size() == 1) {
                                        Log.d(TAG, "onComplete: This will return the signin methods");
                                        Toast.makeText(EditProfileActivity.this, "The email is already exist", Toast.LENGTH_SHORT).show();


                                    } else {
                                        Log.d(TAG, "onComplete: Email is not present. User can change it");
                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                        user.updateEmail(mEmail.getText().toString())
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Log.d(TAG, "User email address updated.");
                                                            Toast.makeText(EditProfileActivity.this, "Email updated.", Toast.LENGTH_SHORT).show();
                                                            mFirebaseMethods.updateEmail(mEmail.getText().toString());
                                                        }
                                                    }
                                                });

                                    }
                                } catch (NullPointerException e) {
                                    Log.e(TAG, "onComplete: NullPointerException" + e.getMessage());
                                }
                            }
                        }
                    });
                } else {
                    Toast.makeText(EditProfileActivity.this, "Re authentication failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}