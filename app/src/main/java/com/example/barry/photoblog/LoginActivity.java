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
 * 1. add the view widgets and initialize the them
 * 2. To check if user is signed in:
 *      - declare the firebase auth
 *      - init it in onCreate
 *      - override onStart() and check
 * 3. Create a method to send user to main activity
 *
 * 4. login user, add click listener to the login btn in onCreate()
 *
 */
public class LoginActivity extends AppCompatActivity {

    //2.1 declare the firebase auth
    private FirebaseAuth mAuth;

    //1 widgets
    private EditText loginEmailtext;
    private EditText loginPasstext;
    private Button loginBtn;
    private Button loginRegBtn;
    private ProgressBar loginProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 2.2 init fb auth
        mAuth = FirebaseAuth.getInstance();

        //1.1 init the views
        loginEmailtext = (EditText) findViewById(R.id.reg_email);
        loginPasstext = (EditText) findViewById(R.id.reg_pass);
        loginBtn = (Button) findViewById(R.id.login_btn);
        loginRegBtn = (Button) findViewById(R.id.login_reg_btn);
        loginProgress = (ProgressBar) findViewById(R.id.login_progress);

        // go to the registration page
        loginRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent regIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(regIntent);
            }
        });

        // 4.0 add click listener to loginBt to log in user
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //4.1 get the data from the edittext fields
                String loginEmail = loginEmailtext.getText().toString();
                String loginPass = loginPasstext.getText().toString();

                //4.2 check that the fields are completed by user
                if (!TextUtils.isEmpty(loginEmail) && !TextUtils.isEmpty(loginPass)) {

                    //4.3 set progress bar to visible to show progress
                    loginPasstext.setVisibility(View.VISIBLE);

                    mAuth.signInWithEmailAndPassword(loginEmail, loginPass)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    // 4.4 if signin is successful, send user to main
                                    if (task.isSuccessful()) {

                                        // call sendToMain()
                                        sendToMain();

                                    } else { // otherwise, show error msg
                                        String errorMessage = task.getException().getMessage();
                                        Toast.makeText(LoginActivity.this,
                                                "Error: " + errorMessage,
                                                Toast.LENGTH_SHORT).show();
                                    }

                                    loginProgress.setVisibility(View.INVISIBLE);
                                }
                            });
                }

            }
        });
    }

    // 2.3 override the onStart()
    @Override
    protected void onStart() {
        super.onStart();

        // 2.4 get the current user
        FirebaseUser currentUser = mAuth.getCurrentUser();

        //2.5
        if (currentUser != null) { // if user is logged in,

            sendToMain(); // send to main activity
        }
    }

    // 3. create a method to send user to the main activity
    private void sendToMain() {
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(mainIntent); // send user to main
        finish(); // do not allow for back button to return here
    }
}
