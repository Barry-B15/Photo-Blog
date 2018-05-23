package com.example.barry.photoblog;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * 1. Initialize the widgets
 * 2. Authenticate fb auth
 * 3. Check if user is signed in, override onStart()
 *      create send to main and call it in onStart()
 * 4. add a click listener to the register btn
 * 5. add click listener to the reg_login btn so user with acct can go to log in pg
 */
public class RegisterActivity extends AppCompatActivity {

    // 1.0 Initialize the widgets
    private EditText regEmailField;
    private EditText regPassField;
    private EditText regConfirmPassField;
    private Button regBtn;
    private Button regLoginBtn;
    private ProgressBar regProgress;

    // 2.0 Authenticate fb auth
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // 2.1 Authenticate fb auth
        mAuth = FirebaseAuth.getInstance();

        // 1.1 Initialize the widgets
        regEmailField = (EditText) findViewById(R.id.reg_email);
        regPassField = (EditText) findViewById(R.id.reg_pass);
        regConfirmPassField = (EditText) findViewById(R.id.reg_pass_conf);
        regBtn = (Button) findViewById(R.id.reg_btn);
        regLoginBtn = (Button) findViewById(R.id.reg_login_btn);
        regProgress = (ProgressBar) findViewById(R.id.reg_progress);

        // 5 add click listener to the reg_login btn so user with acct can go to log in pg
        regLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish(); // this destroys the intent and sends user to the login page
            }
        });

        // 4. add a click listener to the register btn
        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = regEmailField.getText().toString();
                String pass = regPassField.getText().toString();
                String confirm_pass = regConfirmPassField.getText().toString();

                // if text fields are not empty
                if (!TextUtils.isEmpty(email)
                        && !TextUtils.isEmpty(pass)
                        && !TextUtils.isEmpty(confirm_pass)) {

                    // also check if the 2 passwords match
                    if (pass.equals(confirm_pass)) {

                        regProgress.setVisibility(View.VISIBLE);

                        mAuth.createUserWithEmailAndPassword(email, pass)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {

                                        if (task.isSuccessful()) {

                                            // sendToMain();
                                            // update: send to setup, if Registration is successful
                                            Intent setupIntent = new Intent(RegisterActivity.this,
                                                    SettingsActivity.class);
                                            startActivity(setupIntent);
                                            finish();
                                        }
                                        else {

                                            String errorMsg = task.getException().getMessage();
                                            Toast.makeText(RegisterActivity.this,
                                                    "Error : " + errorMsg,
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                        regProgress.setVisibility(View.GONE);
                                    }
                                });
                    }
                    else {
                        Toast.makeText(RegisterActivity.this,
                                "confirm password and password does not match",
                                Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
    }

    // 3.0 Check if user is signed in, override onStart()
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {

            sendToMain();
        }
    }

    //3.1 redirect to main if user is signed in
    private void sendToMain() {

        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}


