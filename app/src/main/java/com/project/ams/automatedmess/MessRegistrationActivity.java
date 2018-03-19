package com.project.ams.automatedmess;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class MessRegistrationActivity extends AppCompatActivity {


    // Declare these here so that we can use them throught the this activity class
    private EditText mRegName, mRegEmail, mRegAddress, mRegPhoneNo, mRegPassword, mRegConfirmPassword;

    private Button mSignUpBtn;

    // Declare some Firebase stuff
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private static final String TAG = "MessRegistrationActivit";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mess_registration);

        // Make the connections
        mRegName = findViewById(R.id.mRegName);
        mRegEmail = findViewById(R.id.mRegEmail);
        mRegAddress = findViewById(R.id.mRegAddress);
        mRegPhoneNo = findViewById(R.id.mRegPhoneNo);
        mRegPassword = findViewById(R.id.mRegPassword);
        mRegConfirmPassword = findViewById(R.id.mRegCPassword);
        mSignUpBtn = findViewById(R.id.mRegisterBtn);

        // Init Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        mSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = mRegName.getText().toString();
                String email = mRegEmail.getText().toString();
                String address = mRegAddress.getText().toString();
                String phoneNo = mRegPhoneNo.getText().toString();
                String password = mRegPassword.getText().toString();
                String confirmPassword = mRegConfirmPassword.getText().toString();

                if (name.isEmpty()) {
                    mRegName.requestFocus();
                    Toast.makeText(MessRegistrationActivity.this, "Please enter a name", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!Pattern.matches("[a-zA-z\\s]+", name)) { // Used for checking that the name field only contains letters
                    mRegName.requestFocus();
                    Toast.makeText(MessRegistrationActivity.this, "Please enter a person's name", Toast.LENGTH_SHORT).show();
                    return;
                } else if (name.length() < 3) {
                    mRegName.requestFocus();
                    Toast.makeText(MessRegistrationActivity.this, "Please enter a name with atleast 3 letters", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (address.isEmpty()) {
                    mRegAddress.requestFocus();
                    Toast.makeText(MessRegistrationActivity.this, "Please enter an address", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (phoneNo.isEmpty()) {
                    mRegPhoneNo.requestFocus();
                    Toast.makeText(MessRegistrationActivity.this, "Please enter a phone no.", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!Pattern.matches("\\d+", phoneNo)) { // Check whether the value given by the user is in the proper email format
                    mRegPhoneNo.requestFocus();
                    Toast.makeText(MessRegistrationActivity.this, "Please enter only numbers,\n in the phone no. field, \nlike eg. johndoe@gmail.com", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (email.isEmpty()) {
                    mRegEmail.requestFocus();
                    Toast.makeText(MessRegistrationActivity.this, "Please enter an email", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!Pattern.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", email)) { // Check whether the value given by the user is in the proper email format
                    mRegEmail.requestFocus();
                    Toast.makeText(MessRegistrationActivity.this, "Please enter an email in the right format, \nlike eg. johndoe@gmail.com", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.isEmpty()) {
                    mRegPassword.requestFocus();
                    Toast.makeText(MessRegistrationActivity.this, "Please enter a password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (confirmPassword.isEmpty()) {
                    mRegConfirmPassword.requestFocus();
                    Toast.makeText(MessRegistrationActivity.this, "Please confirm the password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    mRegConfirmPassword.requestFocus();
                    Toast.makeText(MessRegistrationActivity.this, "Please enter the same password\n as in the above field", Toast.LENGTH_SHORT).show();
                    return;
                }
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(MessRegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(MessRegistrationActivity.this, "Something isn't right: \n" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        } else {
                            // Store the misc information into the database
                            String mProviderName = mRegName.getText().toString();
                            String mProviderAddress = mRegAddress.getText().toString();
                            String mProviderPhoneNo = mRegPhoneNo.getText().toString();

                            // Store this unique userID which is created when registration is successful
                            // Used to retrive misc information for this userID/Mess Owner
                            String userID = mAuth.getCurrentUser().getUid();
                            DatabaseReference currentDBRef = FirebaseDatabase.getInstance().getReference().child("Users").child("MessProviders").child(userID).child("PersonalInformation");
                            currentDBRef.child("mProviderName").setValue(mProviderName);
                            currentDBRef.child("mProviderAddress").setValue(mProviderAddress);
                            currentDBRef.child("mProviderPhoneNo").setValue(mProviderPhoneNo);

                            // TODO: Change this later we want to add Email Verification, not ot show the MessProfileEditor
                            Toast.makeText(MessRegistrationActivity.this, "You've Signed In", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(MessRegistrationActivity.this, MessProfileEditor.class);
                            startActivity(intent);
                            finish();
                            return;
                        }
                    }
                });
            }
        });

    }
}
