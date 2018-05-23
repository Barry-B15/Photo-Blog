package com.example.barry.photoblog;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.os.Build.VERSION_CODES.M;

/**
 * 1. init and get the toolbar
 * 2. Have Registration send user here if Registration is successful
 * 3. Add circular image dependency to gradle and sync
 * compile 'de.hdodenhof:circleimageview:2.2.0'
 * change compile to implementation
 * <p>
 * 4. add a circle image view to settings activity xml file
 * 5. Google default image view download one, some are copy righted
 * - copy it to the drawable and add it
 * 6. get the image in the java file, init it
 *      then set click listener to it
 * 7. add permission for read and write to EXTERNAL STORAGE to the manifest
 * For versions > Marshmallow, also ask for permission here
 * ---------------------------------------------------------------
 * <p>
 * part 2: Video 5: https://youtu.be/sDf7NKROoDM?list=PLGCjwl1RrtcR4ptHvrc_PQIxDBB5MGiJA
 * <p>
 * 1. We want the user to add a sq 1:1 image, google android crop image library ,
 * Go to the github page :
 * https://github.com/ArthurHub/Android-Image-Cropper
 * <p>
 * Follow the directions:
 * This will allow users to crop their images to size
 * <p>
 * 1.1 copy the dependency into gradle app and sync
 * compile 'com.theartofdev.edmodo:android-image-cropper:2.6.+'
 * <p>
 * 1.2 add this to Manifest
 * <activity android:name="com.theartofdev.edmodo.cropper.CropImageActivity"
 * android:theme="@style/Base.Theme.AppCompat"/>
 * <p>
 * 1.3 Also add permission to read and write to external storage to Manifest
 * <p>
 * 2. Override onActivityResult()
 * Copy the code from the crop image link and paste into this
 * <p>
 * 3. Lets store the cropped image
 * define a private Uri mainImageURI, and set it to null by default
 * Replace the Uri copied from the link to this new one
 * <p>
 * 4. set the cropped image to the setupImage (our display image we are working on)
 * 5. Finally, set the image ratio to 1:1 by adding the ff to cropImage.Activity(),
 * after .setGuideline()
 * add: .setAspectRatio(1,1);
 * we can also setMaxCropResultSize(512,)
 * probably not a good idea, maybe thumbnail is better
 * <p>
 * 6. Add a text view and a button view if not already added
 * - init them
 * 7. Add click listener to the image so it gets stored in the fb data store when clicked
 * 8. Now we need Firebase Storage: Connect app to fb using assistant if not already connected
 * Storage > Add Cloud Storage to your app
 * <p>
 * 9. Declare the Storage Ref and fb auth and init in onCreate()
 * also we need the fb auth cos we want to assoc the user to the name and pic
 * 10. In the setupBtn click listener:
 * define a userId to get current user
 * define another storage ref to store the user image path
 * store the file URI into the path
 * 11. create a progress bar in the xml file
 * make it indeterminate and invisible
 * 12. add the progress bar to java
 * - and init it
 * - then show progress bar after checking that the fields are not empty
 * ------------------------------------------------------------------------------------
 * part 3: video 6
 * https://youtu.be/RXU0J4D8OgQ?list=PLGCjwl1RrtcR4ptHvrc_PQIxDBB5MGiJA
 * <p>
 * 1. Our app does not support lower version of android yet
 * - add an else to the if statement for version > M
 * - Create a new method "BringImagePicker()"
 * - Cut the cropImage() codes, paste that in BringImagePicker()
 * - Call BringImagePicker() instead, and also in the else statement
 * <p>
 * 2. Next we need to add the data to Firebase Firestore,
 * use the built in assistant , add Firestore to the app
 * Make sure all firebase dependencies use the same version or it wont work
 * <p>
 * 3. Declare the firestore and init in onCreate()
 * <p>
 * 4. Code to upload data to firestore
 * <p>
 * 5. Go to Firebase, choose Firestore > Databse , then Enable.
 * Change the RULE to: if request.auth != null > PUBLISH;
 * <p>
 * 6. Retrieve data from firestore:
 * we need to get the FirebaseFirestore, collection, users, document, then user_id
 * we need the user_id in more than one place now so let's make it global
 * then edit the "final string user_id = " in setupBtn click listener to "user_id = "
 * 7. Check for success and failure, cont : Run - see what happens
 * 8. Add Glide dependecies to handle image retrieval
 * then add the Glide codes
 * Run app again and see the diff
 * <p>
 * 9. Now if we change the user display name, nothing happens, cos na image was assoc with it
 * we need to be flexible for user to change display name
 * once the setup is completed
 * 1st set the progress bar to visible
 * 2nd set it to invisible once the process is completed, successfully or not
 * - disable the save btn then enable it again after process is completed
 * - set our imageURI to image so that whether user selects a new image or not,
 * the name is assoc with the earlier image
 * RUN and try to change the name
 * The toast say there is an HTTP error
 * 10. Fix for 9: create a separate func for Firestore and call it
 * - create a new method "storeFirestore()"
 * - take out all the codes in the in Firestore "if (task.isSuccessful)" ...  "image_path.putFile"
 * - put it here in this new method()
 * **This will throw error for task and user_name**
 * Fix: see the Type of task , pass that as param to our new method
 * - also pass user_name as the second param:
 * private void storeFirestore(@NonNull Task<UploadTask.TaskSnapshot> task, String user_name)
 * <p>
 * - then pass task and user_name where well called the method:
 * storeFirestore(task, user_name)
 * <p>
 * 11. create a new boolean isChanged to watch for changes
 * then add a new if statement to setupBtn
 * <p>
 * 12. create another if statement in the storefirestore
 * ---------------------------------------------------------------------
 * Part 4 Video 7
 * 1. In setupBtn we are having the progress bar before the isChanged, this may cause infinite progress problem so
 * - Move this the opening statement from inside if(isChanged) in the setupBtn click listener:
 * if (!TextUtils.isEmpty(user_name)
 * && mainImageURI != null) {
 * to under:  final string user_name
 * <p>
 * - then move the following else 2 places up:
 * else {
 * storeFirestore(null, user_name);
 * Now Run again and see
 */
public class SettingsActivity extends AppCompatActivity {

    // 2-9.0 Declare fb ref
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth; // we need the auth cos we want to assoc the user to the name and pic
    // 3-3.1 Firebase firestore
    private FirebaseFirestore firebaseFirestore;

    //6. define the Circle image
    private CircleImageView setupImage;

    //2-3 define a private Uri mainImageURI
    private Uri mainImageURI = null;

    // 3-6.0 declare a global user_id
    private String user_id;

    // 3-11.0 create a new boolean isChanged, default it to false
    private boolean isChanged = false;

    //2-6.1 def the text and btn views
    private EditText setupName;
    private Button setupBtn;

    // 11.0 Declare the progress bar
    private ProgressBar setupProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //1. init and get the toolbar
        Toolbar setupToolbar = (Toolbar) findViewById(R.id.setup_toolbar);
        setSupportActionBar(setupToolbar);
        getSupportActionBar().setTitle("Account Setup");

        // 2-9.1 init the fb auth and fb ref
        firebaseAuth = FirebaseAuth.getInstance();
        // 3-6.1 init the user_id
        user_id = firebaseAuth.getCurrentUser().getUid(); // get user_id b4 anything so we can use it
        firebaseFirestore = FirebaseFirestore.getInstance(); // 3-3.1 Firebase firestore
        storageReference = FirebaseStorage.getInstance().getReference(); //video 5


        //2-6.2 init the text and btn views
        setupImage = findViewById(R.id.setup_image);
        setupName = findViewById(R.id.setup_name_hint);
        setupBtn = findViewById(R.id.setup_btn);
        // 11.1 init the progress bar
        setupProgress = (ProgressBar) findViewById(R.id.setup_progress);

        // 3-9.0 set progress bar to visible
        setupProgress.setVisibility(View.VISIBLE);
        setupBtn.setEnabled(false);  // 3-9.2

        // 3-6.2 Retrieve data from firestore,
        // case sensitive be careful
        firebaseFirestore.collection("User").document(user_id)
                .get()  // we want to get this data only once so we use get() as our method
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()) {

                            //3-7.0 check if the user does exist or not
                            if (task.getResult().exists()) {
                                Toast.makeText(SettingsActivity.this, "Data exist", Toast.LENGTH_SHORT).show();

                                //3-8.0 define the string (must be same as in firestore, case-sensitive too
                                String name = task.getResult().getString("name");
                                String image = task.getResult().getString("image");

                                // 3-9.4 give a value to our null image so user can always have it for use
                                mainImageURI = Uri.parse(image);

                                // 3-8.1 load the strings to the field we defined in our xml file
                                setupName.setText(name);

                                // 3-8.2 use glide for the image. This will work without a default image.
                                // to show a default image while loading or if it fails, define a place holder and use
                                //Glide.with(SettingsActivity.this).load(image).into(setupImage);

                                //3-8.3 def the placeholder
                                RequestOptions placeholderRequest = new RequestOptions();
                                placeholderRequest.placeholder(R.drawable.ic_def_img);

                                Glide.with(SettingsActivity.this)
                                        .setDefaultRequestOptions(placeholderRequest) // use the placeholder
                                        .load(image) // load the "image" in the db with string "image"
                                        .into(setupImage); // into our reserved image view "setupImage"

                            } else {//3-7.1 if not, notify user with a toast
                                Toast.makeText(SettingsActivity.this, "Data does not exist ",
                                        Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(SettingsActivity.this,
                                    "FIRESTORE Retrieve Error : " + error,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        //3-9.1
        setupProgress.setVisibility(View.INVISIBLE);
        setupBtn.setEnabled(true);  // 3-9.3

        //2-7 Add click listener to the image so it gets stored in the fb data store
        setupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String user_name = setupName.getText().toString();

                // check that the name field and image are not empty
                // we initially set our image to null at the top but we need the image now. omit the image line to make it optional
                if (!TextUtils.isEmpty(user_name)
                        && mainImageURI != null) { // we can make it optional by removing this line


                    //2-11.2 show progress bar after checking that the fields are not empty
                    setupProgress.setVisibility(View.VISIBLE);

                    if (isChanged) { // check if the image is changed


                        // 2-10 define a userId and another storage ref to store current user image and id
                        user_id = firebaseAuth.getCurrentUser().getUid(); // get current user id

                        StorageReference image_path = storageReference  // from our earlier storage ref : root
                                .child("profile_image")  // store this in a new folder called "profile_image" : folder in root
                                .child(user_id + ".jpg"); // store with a user_id, and as a .jpg image : file in folder

                        //2-10.1 store the file URI into the path
                        image_path.putFile(mainImageURI)  // put the image into the path
                                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                        // onComplete listener will return us the task snap shot

                                        // check if task is successful
                                        if (task.isSuccessful()) { //for success

                                            // all content moved to create storeFirestore()
                                            storeFirestore(task, user_name);

                                        } else { // for failure
                                            String error = task.getException().getMessage();
                                            Toast.makeText(SettingsActivity.this,
                                                    "IMAGE Error : " + error,
                                                    Toast.LENGTH_SHORT).show();

                                            // hide the progress bar after our task is complete
                                            setupProgress.setVisibility(View.INVISIBLE);
                                        }
                                    }
                                });
                    } else {
                        storeFirestore(null, user_name);
                    }
                }
            }
        });

        // 6.1 init the image
        setupImage = findViewById(R.id.setup_image);

        //6.2 set click listener to the image to allow user choice image from storage
        setupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //7.0 ask for permission if user is using version > marshmallow
                if (Build.VERSION.SDK_INT >= M) {

                    //7.1 check if permission has been granted
                    // be sure to import android.Manifest or the READ_EXTERNAL_STORAGE will give error
                    if (ContextCompat.checkSelfPermission(SettingsActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) // if we want user to write to external storage, we will also ask here
                            != PackageManager.PERMISSION_GRANTED) {
                        // if not granted, put a toast
                        Toast.makeText(SettingsActivity.this,
                                "Permission denied", Toast.LENGTH_SHORT).show();

                        //7.2 ask for permission again, this will open a window for user to select allow
                        ActivityCompat.requestPermissions(SettingsActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                        // be sure to import android.Manifest or the READ_EXTERNAL_STORAGE will give error
                    } else {

                        BringImagePicker();
                    }

                } else {

                    BringImagePicker();

                }
            }
        });
    }

    private void storeFirestore(@NonNull Task<UploadTask.TaskSnapshot> task, String user_name) {

        // 3-12.0
        Uri download_uri;

        // 3-12.1
        if (task != null) { // if there was a change then we get a new image
            download_uri = task.getResult().getDownloadUrl();
        } else { // 3-12.2 otherwise, use the old image

            download_uri = mainImageURI;
        }

        // // 3-3.3 create a collection in Firebasefirestore w/name and image fields
        // 1st create a Hashmap
        Map<String, String> userMap = new HashMap<>();
        userMap.put("name", user_name);
        userMap.put("image", download_uri.toString());

        // 3-4.0 To upload data to firestore
        firebaseFirestore.collection("User").document(user_id)
                .set(userMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        //3-4.1 check image is successfully added to Firestore
                        if (task.isSuccessful()) {

                            Toast.makeText(SettingsActivity.this,
                                    "The user settings has been updated",
                                    Toast.LENGTH_SHORT).show();
                            Intent mainIntent = new Intent(SettingsActivity.this,
                                    MainActivity.class);
                            startActivity(mainIntent);
                            finish();
                        } else { //3-4.2 for failure

                            String error = task.getException().getMessage();
                            Toast.makeText(SettingsActivity.this,
                                    "FIRESTORE Error : " + error,
                                    Toast.LENGTH_SHORT).show();
                        }

                        //3-4.3 hide the progress bar after our task is complete
                        setupProgress.setVisibility(View.INVISIBLE);
                    }
                });

        Toast.makeText(SettingsActivity.this,
                "Your image has been uploaded",
                Toast.LENGTH_SHORT).show();
    }

    private void BringImagePicker() {
        // copy/paste from the crop image link
        // start picker to get image for cropping and then use the image in cropping activity
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(SettingsActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mainImageURI = result.getUri();  // store the result in mainImage URI here

                // 2-4 set the cropped image to the setupImage
                setupImage.setImageURI(mainImageURI);

                isChanged = true;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

}

