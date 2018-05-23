package com.example.barry.photoblog;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * https://youtu.be/ZqrT8ctJB4A?list=PLGCjwl1RrtcR4ptHvrc_PQIxDBB5MGiJA
 * 1 create empty new activity and remove the hello world text view
 * 2. Connect to Firebase: Authentication first to have users sign in before using the app
 * 3. Add internet permission to the Manifest
 * 4. Choose the colors on colors s needed
 * 5. In styles, change to no action bar as we will create our own
 * 6. Create the LoginActivity
 * 7. When user 1st downloads the app we want them sent to the login page so
 *      create an intent in Main.java to do that. (we will edit this later on)
 * 8. For the app to be used only in portrait mode, add to Manifest for login and main.java
 *      android:screenOrientation="portrait"
 * 9. add the views to the login page
 * -------------------------------------
 * Part 2: Login
 * https://youtu.be/WN4Xec0bNmo?list=PLGCjwl1RrtcR4ptHvrc_PQIxDBB5MGiJA
 *
 * 1. Enable email authentication in Firebase
 * 2. In main check if the user is signed in, if not, send user to login pg
 *      do tht with onStart()
 * 3. Now go and code the Login Page
 * --------------------------------------------------------
 * Part 3: Register Account
 * https://youtu.be/YfoTdnI3IKs?list=PLGCjwl1RrtcR4ptHvrc_PQIxDBB5MGiJA
 *https://youtu.be/YfoTdnI3IKs
 * 1. Create the RegisterActivity
 * 2. Remove the auto generated text view
 *      add a toolbar
 * 3. In main.java, declare the toolbar and init it
 * 4. For the toolbar menu items, create the menu folder in res
 * 5. add some items to the menu folder (drag, drop and customize)
 *      - get a search icon from the Image asset
 * 6.Now add this menu bar to the main activity, override onCreateOptionsmenu
 * 7. Now let's logout:
 *      add the Firebase auth to main
 * 8. Override onOptionItemsSelected() to handle clicks on the menu
 * Run app : logout an d see it works. and we can log in again
 *
 * 9. Now add views to the Register .xml
 *      1st copy and paste all the views from login
 *------------------------------------------------------------------
 * Part 4: Account Setting
 * https://youtu.be/DZrMZd1Uj6g?list=PLGCjwl1RrtcR4ptHvrc_PQIxDBB5MGiJA
 * 1. Create a new activity "Settings"
 * 2. Add the toolbar widget to the xml file
 * 3. Init the toolbar in the java file
 * 4. Edit, have Registration send user to setup if Registration is successful
 *
 * Part 5 Account setting cont
 * https://youtu.be/sDf7NKROoDM?list=PLGCjwl1RrtcR4ptHvrc_PQIxDBB5MGiJA
 * 1. We want the user to add a sq 1:1 image, google android crop image library ,
 *      Go to the github page :
 *      https://github.com/ArthurHub/Android-Image-Cropper
 *
 *      copy the dependency into gradle app and sync
 *      compile 'com.theartofdev.edmodo:android-image-cropper:2.6.+'
 *
 *      add this to Manifest
 *      <activity android:name="com.theartofdev.edmodo.cropper.CropImageActivity"
 *       android:theme="@style/Base.Theme.AppCompat"/>
 *
 *       Also add permission to read and write to external storage to Manifest
 *
 *      This will allow users to crop their images to size
 *
 * -------------------------------------------------------------------------
 *Part 6: https://youtu.be/RXU0J4D8OgQ?list=PLGCjwl1RrtcR4ptHvrc_PQIxDBB5MGiJA
 *
 * 1. Our app does not support lower version of android yet
 *      - add an else to the if statement for version > M
 *      - Create a new method "BringImagePicker()"
 *      - Cut the cropImage() codes, paste that in BringImagePicker()
 *      - Call BringImagePicker() instead, and also in the else statement
 * Cont. in SettingsActivity
 * --------------------------------------------------------------
 * Part 7 https://youtu.be/ZC7GwnutLQY?list=PLGCjwl1RrtcR4ptHvrc_PQIxDBB5MGiJA
 * In Main acitivity
 * 1. Change the text on the tool bar to white by adding this line to the xml file
 *      android:theme="@style/ThemeOverlay.AppCompat.Dark.ActionBar"
 *
 * 2. Add a floating btn to the main xml for users to start a new blog
 *      edit as needed
 * 3. Declare the btn in the java file and init it in Oncreate()
 * 4. Add click listener to the btn
 * 5. The click should take us to a new activity, we don't have that yet so:
 *      - create NewPostActivity()
 *      - add the views, a toolbar is optional
 * 6. Now go get the toolbar in the NewPostActivity, and init it in the onCreate
 *
 * -------------------------------------------------------------------------------
 * Part 8  https://youtu.be/Whl7_yTz5dc?list=PLGCjwl1RrtcR4ptHvrc_PQIxDBB5MGiJA
 * In Main
 *  1. Check if user exist in our db
 *     Get Firebase Firestore, init it in onCreate
 *  2. Create a String current_user_id
 *         init that in onStart
 *  3. Check if the user exist in our collection in onStart()
 *  4. Create a NewPostActivity
 *
 *----------------------------------------------------------------------
 * Part 11 Bottom Navigation
 * https://youtu.be/Lg4PVqRRO2Q?list=PLGCjwl1RrtcR4ptHvrc_PQIxDBB5MGiJA
 *
 * 1. Let's begin by adding bottom nav to main activity
 *      add bottom nav to main.xml,
 *      make the flaoting action bar to stave above the bottom nav
 * 2. In main java file, declare the bottom nav and init it
 * 3. add items to the bottom nav; get the icon and others from google
 *      https://material.io/icons
 *      - download the icons for Home, Notifications and account(person)
 *      - drag and drop each size accordingly
 * 4. create a new menu file for the bottom nav : "bottom_menu.xml"
 * 5. add the items: drag and drop in the design xml area
 * 6. add icons to the new created bottom_menu
 * 7. In main.xml Bottomnavigation add this line
 *      app:menu="@menu/bottom_menu"
 *      this will add the bottom menu to the bottom nav
 * 8. we are going to  need 3 fragments so ad a frameLayout to the design
 *      width and height should be set to match-constraint
 *9. Create 3 fragments, one each for Home, Notification and Account
 *      Edit the included text to fit what we need
 * 10. Add a method "replaceFragment()" to handle transactions to replace current fragment
 *      with another when user pushes a button
 *
 * 11. create instances of those fragments and init them
 *
 * 12. add a click listener "OnNavigationItemSelectedListener" to the botttom nav
 * 13. Add ids to the bottom nav items if not already dome
 *      add a switch to the click listener to take user to the desired fragments
 *-----------------------------------------------------------------------------------
 * Psrt 12: https://youtu.be/uVbcayAaDKk?list=PLGCjwl1RrtcR4ptHvrc_PQIxDBB5MGiJA
 * Retrieve data from Firebase Firestore
 *
 * 1. In the Home fragment, we dont need the we added earlier,
 *      Instead, put a recycler view
 *      add codes to the java Homefragment
 * 2. Create a model class BlogPost for our adapter.
 *      This model class needs all the attr of our db : desc, image_url, timestamp, img_thumb and user_id
 *      open the db to see these under + ADD FIELD
 *
 * 3. Next go create an adapter class: "BlogRecyclerAdapter()
 *      have it extends RecyclerView.Adapter passing in the newly created adapter
 *
 * 4. Finish adding codes to Blogrecycler then
 *     go to Home Fragment and add private List<BlogPost> blog_list
 *
 *
 * ---------------------------------------------------------------------------
 * part 13: https://youtu.be/rVOVU53xWLc
 * Retrieve the image
 *
 * part 14 : https://youtu.be/btPJWA3Hbvs?list=PLGCjwl1RrtcR4ptHvrc_PQIxDBB5MGiJA
 * Retrieve user data
 * 1. go to the adapter class, init firebase firestore and continue to add code
 *
 * part 15
 * Likes
 * 3. Make a new drawable image assets for 2 heart btns, gray for not yet liked, accent for liked
 *      - add the btns and text view to the blog_list_item.xml
 *      - go get them in the Adapter
 *
 * part 17 Comments
 *
 * 1. create the commentActivity, and add the views, and init them in comments.java
 * 2. In the RecyclerAdapter:
 *      Add a comment click listener
 *
 * Part 18 : Retrieve user comments
 *
 * part19: Fix for delay in image loading
 * https://youtu.be/vCa7kCiFtOM
 *
 * 1. Go to HomeFragment
 *
 * ---------------
 * Add delete button
 * 1. add the btn to blog_list_item.xml
 * 2. set up the btn in BlogRecyclerAdapter, ViewHolder class
 */
public class MainActivity extends AppCompatActivity {

   /* //3-3.0 declare the instance of the toolbar
    private Toolbar mainToolbar;

    // 3-7 add the Firebase auth
    FirebaseAuth mAuth;

    // 8.1.0
    FirebaseFirestore firebaseFirestore;

    //8.2.0
    private String current_user_id;

    // 7.3.0 floating btn
    private FloatingActionButton addPostBtn;

    // 11.2.0
    private BottomNavigationView mainBottomNav;

    // 11.11.0
    private HomeFragment homeFragment;
    private NotificationFragment notificationFragment;
    private AccountFragment accountFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 3-7.1 init the fb auth
        mAuth = FirebaseAuth.getInstance();

        //8.1.1
        firebaseFirestore = FirebaseFirestore.getInstance();



        //3-3.2 init the toolbar and get instance of the support action bar
        mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);

        getSupportActionBar().setTitle("Photo Blog");

        //13/x check for user auth: Fix for crashing when signed out
        if (mAuth.getCurrentUser() != null) {

            // 11.2.1
            mainBottomNav = findViewById(R.id.mainBottomNav);

            // 11.11.1 Fragments
            homeFragment = new HomeFragment();
            notificationFragment = new NotificationFragment();
            accountFragment = new AccountFragment();

           *//* //13.1 Whenever the main activity is created, load the HomeFragment
            replaceFragment(homeFragment); // crashing my app Why?*//*

            // 11.12.0
            mainBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    //11.13.2 switch
                    switch (item.getItemId()) {
                        case R.id.bottom_action_home:
                            replaceFragment(homeFragment);
                            return true; // a boolean so must return true. default is false

                        case R.id.bottom_action_notif:
                            replaceFragment(notificationFragment);
                            return true;

                        case R.id.bottom_action_acct:
                            replaceFragment(accountFragment);
                            return true;

                        default:
                            return false;
                    }

                }
            });
            // 7.3.1 init floating btn
            addPostBtn = findViewById(R.id.add_post_btn);
            addPostBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent newPostIntent =
                            new Intent(MainActivity.this, NewPostActivity.class);
                    startActivity(newPostIntent);
                }
            });

        }

    }

    // 2-2
    // check if the user is signed in, if not, send user
    @Override
    protected void onStart() {
        super.onStart();

        //2-2.1 get an in stance of Firebase
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) { // if user is empty/does not exist

            sendToLogin();
        }
        else {
            //8.2.1
            current_user_id = mAuth.getCurrentUser().getUid();

            //8.3 check if this user is in our collection
            firebaseFirestore.collection("User").document(current_user_id)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            if (task.isSuccessful()) { //if task is successful

                                if (!task.getResult().exists()) { // check if the result exist

                                    //if user has not completed set up, send user to setup
                                    Intent setupIntent = new Intent(MainActivity.this,
                                            SettingsActivity.class);
                                    startActivity(setupIntent);
                                }
                            } else {
                                String errorMsg = task.getException().getMessage();
                                Toast.makeText(MainActivity.this,
                                        "Error: " + errorMsg,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    // 3-6. to add the menu bar, override onCreateOptionsMenu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    // 3-8 Override onOptionItemsSelected() to handle clicks on the menu

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_logout_btn:
                logout();
                return true;

            case R.id.action_settings_btn:
                sendToSetup();
                return true;

            default:
                return false;
        }
    }


    // sign out user
    private void logout() {

        mAuth.signOut(); // sign user from fb
        sendToLogin();  // then send user to login page
    }

    private void sendToLogin() {

        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(loginIntent);  // send the user to login page
        finish();       // finish() makes sure the user cannot press back key to return to main

    }

    private void sendToSetup() {
        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
    }

    private void replaceFragment(Fragment fragment) {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_container, // the container to be replaced
                fragment);      // the new fragment we want to see
        fragmentTransaction.commit();
    }*/

    private Toolbar mainToolbar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;

    private String current_user_id;

    private FloatingActionButton addPostBtn;

    private BottomNavigationView mainbottomNav;

    private HomeFragment homeFragment;
    private NotificationFragment notificationFragment;
    private AccountFragment accountFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);

        getSupportActionBar().setTitle("Photo Blog");

        if(mAuth.getCurrentUser() != null) {

            mainbottomNav = findViewById(R.id.mainBottomNav);

            // FRAGMENTS
            homeFragment = new HomeFragment();
            notificationFragment = new NotificationFragment();
            accountFragment = new AccountFragment();

            initializeFragment();

            mainbottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_container);

                    switch (item.getItemId()) {

                        case R.id.bottom_action_home:

                            replaceFragment(homeFragment, currentFragment);
                            return true;

                        case R.id.bottom_action_acct:

                            replaceFragment(accountFragment, currentFragment);
                            return true;

                        case R.id.bottom_action_notif:

                            replaceFragment(notificationFragment, currentFragment);
                            return true;

                        default:
                            return false;


                    }

                }
            });


            addPostBtn = findViewById(R.id.add_post_btn);
            addPostBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent newPostIntent = new Intent(MainActivity.this, NewPostActivity.class);
                    startActivity(newPostIntent);

                }
            });

        }


    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser == null){

            sendToLogin();

        } else {

            current_user_id = mAuth.getCurrentUser().getUid();

            firebaseFirestore.collection("User").document(current_user_id).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if(task.isSuccessful()){

                        if(!task.getResult().exists()){

                            Intent setupIntent =
                                    new Intent(MainActivity.this,
                                            SettingsActivity.class);
                            startActivity(setupIntent);
                            finish();

                        }

                    } else {

                        String errorMessage = task.getException().getMessage();
                        Toast.makeText(MainActivity.this, "Error : " + errorMessage, Toast.LENGTH_LONG).show();


                    }

                }
            });

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_logout_btn:
                logOut();
                return true;

            case R.id.action_settings_btn:

                Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(settingsIntent);

                return true;


            default:
                return false;


        }

    }

    private void logOut() {

        mAuth.signOut();
        sendToLogin();
    }

    private void sendToLogin() {

        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();

    }

    private void initializeFragment(){

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction.add(R.id.main_container, homeFragment);
        fragmentTransaction.add(R.id.main_container, notificationFragment);
        fragmentTransaction.add(R.id.main_container, accountFragment);

        fragmentTransaction.hide(notificationFragment);
        fragmentTransaction.hide(accountFragment);

        fragmentTransaction.commit();

    }

    private void replaceFragment(Fragment fragment, Fragment currentFragment){

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if(fragment == homeFragment){

            fragmentTransaction.hide(accountFragment);
            fragmentTransaction.hide(notificationFragment);

        }

        if(fragment == accountFragment){

            fragmentTransaction.hide(homeFragment);
            fragmentTransaction.hide(notificationFragment);

        }

        if(fragment == notificationFragment){

            fragmentTransaction.hide(homeFragment);
            fragmentTransaction.hide(accountFragment);

        }
        fragmentTransaction.show(fragment);

        //fragmentTransaction.replace(R.id.main_container, fragment);
        fragmentTransaction.commit();

    }

}
