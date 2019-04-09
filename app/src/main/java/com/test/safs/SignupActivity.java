package com.test.safs;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.test.safs.Home.HomeActivity;
import com.test.safs.models.User;
import com.test.safs.models.UserAccountSettings;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    public Boolean auth;
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference myRef;
    private EditText mNameText, mAddressText, mEmailText, mphone_numberText, mPasswordText, mReEnterPasswordText;
    private TextView mLogin;
    private Button mSignupButton;
    private Context mContext;
    private String userID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference();

        mContext = SignupActivity.this;

        mNameText = findViewById(R.id.input_name);
        mAddressText = findViewById(R.id.input_address);
        mEmailText = findViewById(R.id.input_email);
        mphone_numberText = findViewById(R.id.input_phone_number);
        mPasswordText = findViewById(R.id.input_password);
        mReEnterPasswordText = findViewById(R.id.input_reEnterPassword);
        mLogin = findViewById(R.id.link_login);
        mSignupButton = findViewById(R.id.button_signup);

        String name = mNameText.getText().toString();
        String address = mAddressText.getText().toString();
        String email = mEmailText.getText().toString();
        String phone_number = mphone_numberText.getText().toString();
        String password = mPasswordText.getText().toString();
        String reEnterPassword = mReEnterPasswordText.getText().toString();


        mSignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });


    }

    public void signup() {
        Log.d(TAG, "Signup");

        String name = mNameText.getText().toString();
        String address = mAddressText.getText().toString();
        final String email = mEmailText.getText().toString();
        String phone_number = mphone_numberText.getText().toString();
        final long phone_number_int = Long.parseLong(phone_number);
        String password = mPasswordText.getText().toString();
        String reEnterPassword = mReEnterPasswordText.getText().toString();

        if (!validate()) {
            onSignupFailed();
            return;
        } else {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Log.d(TAG, "onComplete: Email Sent");
                                            }
                                        }
                                    });
                                }
                                userID = user.getUid();
                                addNewUser(email,phone_number_int,"");
                                auth = true;
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());

                                if (!task.isSuccessful()) {
                                    try {
                                        throw task.getException();
                                    }
                                    // if user enters wrong email.
                                    catch (FirebaseAuthWeakPasswordException weakPassword) {
                                        Log.d(TAG, "onComplete: weak_password");

                                        Toast.makeText(SignupActivity.this, "Weak Password", Toast.LENGTH_SHORT).show();
                                    }
                                    // if user enters wrong password.
                                    catch (FirebaseAuthInvalidCredentialsException malformedEmail) {
                                        Log.d(TAG, "onComplete: malformed_email");

                                        Toast.makeText(SignupActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                                    } catch (FirebaseAuthUserCollisionException existEmail) {
                                        Log.d(TAG, "onComplete: exist_email");

                                        Toast.makeText(SignupActivity.this, "This EmailID already exists", Toast.LENGTH_SHORT).show();
                                    } catch (Exception e) {
                                        Log.d(TAG, "onComplete: " + e.getMessage());
                                    }
                                }
                                auth = false;
                            }
                        }
                    });
        }

        mSignupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();


        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        if (!auth) {
                            onSignupFailed();
                        } else {
                            onSignupSuccess();
                        }
                        progressDialog.dismiss();
                    }
                }, 3000);
    }


    public void onSignupSuccess() {
        mSignupButton.setEnabled(true);
        //setResult(RESULT_OK, null);
        Intent intent = new Intent(SignupActivity.this, HomeActivity.class);
        finish();
        startActivity(intent);

    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        mSignupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = mNameText.getText().toString();
        String address = mAddressText.getText().toString();
        String email = mEmailText.getText().toString();
        String phone_number = mphone_numberText.getText().toString();
        String password = mPasswordText.getText().toString();
        String reEnterPassword = mReEnterPasswordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            mNameText.setError("at least 3 characters");
            valid = false;
        } else {
            mNameText.setError(null);
        }

        if (address.isEmpty()) {
            mAddressText.setError("Enter Valid Address");
            valid = false;
        } else {
            mAddressText.setError(null);
        }


        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmailText.setError("enter a valid email address");
            valid = false;
        } else {
            mEmailText.setError(null);
        }

        if (phone_number.isEmpty() || phone_number.length() != 10) {
            mphone_numberText.setError("Enter Valid phone_number Number");
            valid = false;
        } else {
            mphone_numberText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            mPasswordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            mPasswordText.setError(null);
        }

        if (reEnterPassword.isEmpty() || reEnterPassword.length() < 4 || reEnterPassword.length() > 10 || !(reEnterPassword.equals(password))) {
            mReEnterPasswordText.setError("Password Do not match");
            valid = false;
        } else {
            mReEnterPasswordText.setError(null);
        }

        return valid;
    }

    public void addNewUser(String email , long phone_number, String profile_photo) {

        String name = mNameText.getText().toString();

        UserAccountSettings userAccountSettings = new UserAccountSettings(
                0, name, 0, "");

        myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(userID).setValue(userAccountSettings);

        User user = new User(userID, phone_number, email);

        myRef.child(mContext.getString(R.string.dbname_users))
                .child(userID).setValue(user);
    }
}