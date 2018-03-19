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

public class MessReg_SignInActivity extends AppCompatActivity {

    private static final String TAG = "MessReg_SignInActivity";

    private EditText mLoginEmail, mLoginPassword;

    private Button mSignInBtn, mSignUpBtn;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mess_reg__sign_in);

        mLoginEmail = findViewById(R.id.mSignInEmail);
        mLoginPassword = findViewById(R.id.mSignInPass);

        mSignInBtn = findViewById(R.id.mSignInBtn);
        mSignUpBtn = findViewById(R.id.mSignUpBtn);

        // Init a few firebase stuff
        mAuth = FirebaseAuth.getInstance();

        mSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mLoginEmail.getText().toString();
                String password = mLoginPassword.getText().toString();

                // Do Some Filtering
                if (email.isEmpty()) {
                    mLoginEmail.requestFocus();
                    Toast.makeText(MessReg_SignInActivity.this, "Please enter an email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.isEmpty()) {
                    mLoginPassword.requestFocus();
                    Toast.makeText(MessReg_SignInActivity.this, "Please enter a password", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(MessReg_SignInActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(MessReg_SignInActivity.this, "Something isn't right: \n" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MessReg_SignInActivity.this, "You've Signed In", Toast.LENGTH_SHORT).show();
                            // If Login is successful then go to the MessProfileEditor Activity
                            Intent intent = new Intent(MessReg_SignInActivity.this, MessProfileEditor.class);
                            startActivity(intent);
                            finish();
                            return;
                        }
                    }
                });
            }
        });

        mSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MessReg_SignInActivity.this, MessRegistrationActivity.class);
                startActivity(intent);
            }
        });

    }
}
