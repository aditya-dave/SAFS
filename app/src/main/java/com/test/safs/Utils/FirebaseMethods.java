package com.test.safs.Utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.test.safs.Profile.EditProfileActivity;
import com.test.safs.R;
import com.test.safs.models.Activity;
import com.test.safs.models.User;
import com.test.safs.models.UserAccountSettings;
import com.test.safs.models.UserActivitiesJoined;
import com.test.safs.models.UserSettings;

import static android.support.constraint.Constraints.TAG;

public class FirebaseMethods {

    private static final String TAG = "FirebaseMethods";

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageReference;
    private String userID;
    private Activity activity = new Activity();

    private Context mContext;

    public FirebaseMethods(Context context) {
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mContext = context;

        if (mAuth.getCurrentUser() != null) {
            userID = mAuth.getCurrentUser().getUid();
        }
    }

    public int getActivitiesCount(DataSnapshot dataSnapshot) {
        int count = 0;
        for (DataSnapshot ds : dataSnapshot.child(mContext.getString(R.string.dbname_user_activities))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .getChildren()) {
            count++;

        }
        return count;
    }

    public UserSettings getUserSettingsViewProfile(DataSnapshot dataSnapshot,String userkey) {
        Log.d(TAG, "getUserAccountSettings: retrieving user account settings from database");
        UserAccountSettings settings = new UserAccountSettings();
        User user = new User();

        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            if (ds.getKey().equals(mContext.getString(R.string.dbname_user_account_settings))) {
                Log.d(TAG, "getUserAccountSettings: datasnapshot" + ds);
                settings.setname(
                        ds.child(userkey)
                                .getValue(UserAccountSettings.class)
                                .getname()
                );


                settings.setprofilephoto(
                        ds.child(userkey)
                                .getValue(UserAccountSettings.class)
                                .getprofilephoto()
                );

                settings.setactivities(
                        ds.child(userkey)
                                .getValue(UserAccountSettings.class)
                                .getactivities()
                );

                settings.setFriends(
                        ds.child(userkey)
                                .getValue(UserAccountSettings.class)
                                .getFriends()
                );
            }
            if (ds.getKey().equals(mContext.getString(R.string.dbname_users))) {
                Log.d(TAG, "getUserAccountSettings: datasnapshot" + ds);
                user.setEmail(
                        ds.child(userkey)
                                .getValue(User.class)
                                .getEmail()
                );
                user.setPhone_number(
                        ds.child(userkey)
                                .getValue(User.class)
                                .getPhone_number()
                );
                user.setUser_id(
                        ds.child(userkey)
                                .getValue(User.class)
                                .getUser_id()
                );
                Log.d(TAG, "getUserAccountSettings: Retrieved user information" + user.toString());

            }
        }
        return new UserSettings(user, settings);
    }

    public UserSettings getUserSettings(DataSnapshot dataSnapshot) {
        Log.d(TAG, "getUserAccountSettings: retrieving user account settings from database");
        UserAccountSettings settings = new UserAccountSettings();
        User user = new User();

        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            if (ds.getKey().equals(mContext.getString(R.string.dbname_user_account_settings))) {
                Log.d(TAG, "getUserAccountSettings: datasnapshot" + ds);
                settings.setname(
                        ds.child(userID)
                                .getValue(UserAccountSettings.class)
                                .getname()
                );


                settings.setprofilephoto(
                        ds.child(userID)
                                .getValue(UserAccountSettings.class)
                                .getprofilephoto()
                );

                settings.setactivities(
                        ds.child(userID)
                                .getValue(UserAccountSettings.class)
                                .getactivities()
                );

                settings.setFriends(
                        ds.child(userID)
                                .getValue(UserAccountSettings.class)
                                .getFriends()
                );
            }
            if (ds.getKey().equals(mContext.getString(R.string.dbname_users))) {
                Log.d(TAG, "getUserAccountSettings: datasnapshot" + ds);
                user.setEmail(
                        ds.child(userID)
                                .getValue(User.class)
                                .getEmail()
                );
                user.setPhone_number(
                        ds.child(userID)
                                .getValue(User.class)
                                .getPhone_number()
                );
                user.setUser_id(
                        ds.child(userID)
                                .getValue(User.class)
                                .getUser_id()
                );
                Log.d(TAG, "getUserAccountSettings: Retrieved user information" + user.toString());

            }
        }
        return new UserSettings(user, settings);
    }


    public String createnewActivity(final String host_name, final String sport_name, final String date, final String location, final String time, int count) {
        Log.d(TAG, "uploadnewAcitvity: Attempting to create new activity");

        final Activity activity = new Activity();
        final String newActivityKey = myRef.child(mContext.getString(R.string.dbname_activities)).push().getKey();
        Log.d(TAG, "createnewActivity: Created random key" + newActivityKey);
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                //String value = dataSnapshot.getValue(String.class);
                Log.d(TAG, "Value is: " + dataSnapshot);
                UserSettings userSettings = getUserSettings(dataSnapshot);
                Log.d(TAG, "Name: "+userSettings.getSettings().getname());
                activity.setName(userSettings.getSettings().getname());
                //Log.d(TAG, "Name in activity: "+activity.getName());
                activity.setProfilephoto(userSettings.getSettings().getprofilephoto());
                Log.d(TAG, "Name in activity: "+activity.getName());
                if (activity.getName() != null) {
                    Log.d(TAG, "createnewActivity: Name is not null. Value is: " + activity.getName());
                } else {
                    activity.setName(host_name);
                }
                activity.setSport_name(sport_name);
                activity.setLocation(location);
                activity.setDate(date);
                if (activity.getProfilephoto() != null) {
                    Log.d(TAG, "createnewActivity: Profile photo is not null. Value is: " + activity.getProfilephoto());
                } else {
                    activity.setProfilephoto("");
                }
                activity.setTime(time);
                activity.setKey(newActivityKey);

                myRef.child(mContext.getString(R.string.dbname_activities)).child(newActivityKey).setValue(activity);
                myRef.child(mContext.getString(R.string.dbname_user_activities))
                        .child(mAuth.getInstance().getCurrentUser().getUid())
                        .child(newActivityKey)
                        .setValue(activity);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
        count++;
        myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(mAuth.getInstance().getCurrentUser().getUid())
                .child(mContext.getString(R.string.field_activities))
                .setValue(count);
        Log.d(TAG, "createnewActivity: ");
        return newActivityKey;

    }


    public void JoinActivity(final String activityKey) {
        Log.d(TAG, "JoinActivity: Adding activity to user_activities_joined");
        // Read from the database
        myRef.child(mContext.getString(R.string.dbname_activities)).child(activityKey).getRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
/*                String value = dataSnapshot.getValue(String.class);
                Log.d(TAG, "Value is: " + value);*/
                activity = dataSnapshot.getValue(Activity.class);
                Log.d(TAG, "Value is: " + activity);
                UserActivitiesJoined user_activities_joined = new UserActivitiesJoined();
                user_activities_joined.setUserID(userID);
                user_activities_joined.setKey(activityKey);
                user_activities_joined.setProfilephoto(activity.getProfilephoto());
                user_activities_joined.setName(activity.getName());
                user_activities_joined.setSport_name(activity.getSport_name());
                user_activities_joined.setDate(activity.getDate());
                user_activities_joined.setTime(activity.getTime());
                user_activities_joined.setLocation(activity.getLocation());
                myRef.child(mContext.getString(R.string.dbname_user_activities_joined)).child(userID).child(activityKey).setValue(user_activities_joined);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }


    public void LeaveActivity(final String activityKey) {
        Log.d(TAG, "JoinActivity: Adding activity to user_activities_joined");
        // Read from the database

        DatabaseReference databaseReference = myRef.child(mContext.getString(R.string.dbname_user_activities_joined)).child(userID).child(activityKey);
        databaseReference.removeValue();
        Log.d(TAG, "LeaveActivity: Removed Activity "+activityKey);
    }

/*    public void setActivityJoined(Context context){

        myRef.child(mContext.getString(R.string.dbname_activities))
                .child(mAuth.getInstance().getCurrentUser().getUid()).setValue();


        myRef.child(mContext.getString(R.string.dbname_user_activities))
                .child(mAuth.getInstance().getCurrentUser().getUid()).setValue();
    }*/

    /*public void uploadprofilephoto(String imgURL){
        FilePaths filePaths = new FilePaths();
        Log.d(TAG, "uploadprofilephoto: Attempting to update profile photo");
        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StorageReference storageReference = mStorageReference
                .child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id);

        //convert imageURL to bitmap
        Bitmap bitmap = ImageManager.getBitmap(imgURL);
        byte[] bytes = ImageManager.getBytesFromBitmap(bitmap, 100);

        UploadTask uploadTask = null;
        uploadTask = storageReference.putBytes(bytes);
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                mStorageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.e("Tuts+", "uri: " + uri.toString());
                        //Handle whatever you're going to do with the URL here
                        String url = uri.toString();
                        myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child(mContext.getString(R.string.field_profilephoto))
                                .setValue(url);
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: Upload failed");
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                        .getTotalByteCount());
                progressDialog.setMessage("Uploaded "+(int)progress+"%");
            }
        });
    }*/

    public void updateprofilephoto(String url) {
        Log.d(TAG, "updateprofilephoto: Attempting to update profile photo" + url);
        myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(mContext.getString(R.string.field_profilephoto))
                .setValue(url);

        Log.d(TAG, "updateprofilephoto: Profile photo updated" + url);
    }


    public void updateUserAccountSettings(String displayname, long phone_number) {

        Log.d(TAG, "updateUserAccountSettings: Updating User Account Settings");
        if (displayname != null) {
            myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                    .child(userID)
                    .child(mContext.getString(R.string.field_displayname))
                    .setValue(displayname);
        }
        if (phone_number != 0) {
            myRef.child(mContext.getString(R.string.dbname_users))
                    .child(userID)
                    .child(mContext.getString(R.string.field_phonenumber))
                    .setValue(phone_number);
        }
    }

    public void updateEmail(String email) {
        Log.d(TAG, "updateEmail: upadting email to: " + email);

        myRef.child(mContext.getString(R.string.dbname_users))
                .child(userID)
                .child(mContext.getString(R.string.field_email))
                .setValue(email);

    }


}
