package com.example.barry.photoblog;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import id.zelory.compressor.Compressor;

/**
 * cont from main
 * 6. declare the toolbar and init it in onCreate()
 * 7. Add an image view to the xml for the page logo
 * ---------------------------------------------------
 * Setup cont
 * Part 9: https://youtu.be/1g9mnaPY5TA?list=PLGCjwl1RrtcR4ptHvrc_PQIxDBB5MGiJA
 * 
 * 1. Add and init the widgets
 * 2. Add a click listener to the image btn so user can upload an image for the blog post
 *      same as we did for the setup , we also need the crop, but the aspect ratio may be bigger
 *      we dont need to ask for permission here as we already got one at SettingsActivity
 *
 * 3. add the image crop to the onClick and set set the img resolution to 512x512
 *      so users can only upload good quality images
 * 4. override onActivityResult for the images
 * 5. def a Uri for the image and use it in the onActivityResult()
 * 6. add a click listener to the new post btn to post the new post with the image
 * 7. add progressbar to the xml to watch progress
 *      declare the progress bar, init it in Oncreate() and use it in the btn click
 *
 *  8. Next we want to put the data in storage so declare
 *          storage ref and firestore, init the 2 and use in the btn
 *
 *  9. In Firestore, we need create another collection to store the post (with an id and who posted)
 *      so:
 *          1. create a map, 2. put the needed fields,
 *          3. get FirebaseAuth, current user id and init it to get the current user who is posting
 *
 *  10. To put a back arrow/btn for user to mainActivity:
 *         - add the parent activity to NewPostActivity in the manifest
 *          <activity android:name=".NewPostActivity"
  *              android:parentActivityName=".MainActivity"></activity>
  *          - then in the NewPostActivity near the Toolbar,
 *              getSupportActionBar.setDisplayHomeAsUpEnabled(true)
 *  ------------------------------------------------------------
 *  Part 10:
 *  
 *  1. Fix for random string
 *      google "android randowm string generator" > starkoverflow >
 *      https://stackoverflow.com/questions/12116092/android-random-string-generator
 *      copy and paste at the bottom of our code
 *      define the MAX_LENGTH at the top
 *
 *  2. In newPostBtn, change the FieldValue line to "random()" - the nme of our method
 *  3. We need a thumbnail image to load before the high quality one
 *      this low quality image helps us load faster and good for these with slow internet
 *      so use "image compressor" to do that. may search android image compressor for this
 *      https://github.com/zetbaitsu/Compressor
 *      - copy the dependency to gradle and sync
 *
 *      - add this to our codes (1st dec Compressor = commpressedImageFile; then add:
 *      compressedImageFile = new Compressor(this).compressToFile(actualImageFile);
 *
 *      - We don't have the last part (actualImageFile) so create it above that line
 *      File newImageFile = new File(postImageUri.getPath(); (note we used our own name "newImageFile)
 *      - do try and catch to fix the error
 * 4. Check what type of files we can upload at https://firebase.google.com/docs/storage/android/upload-files?authuser=1
 */
public class NewPostActivity extends AppCompatActivity {

    /*//10.x
    //private static int MAX_LENGTH = 100;
    // 7.6.0 declare the toolbar
    private Toolbar newPostToolbar;

    // 9.1 add the widgets
    private ImageView newPostImage;
    private EditText newPostDesc;
    private Button newPostBtn;

    //9.5.0
    private Uri postImageUri = null;

    //9.7.2
    private ProgressBar newPostProgress;

    //9.8.0
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;

    // 9.9.3 get FirebaseAuth for the current user
    private FirebaseAuth firebaseAuth;
    private String current_user_id;

    // 10.3.3
    private Bitmap compressedImageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        //9.8.1
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        // 9.9.3 init FirebaseAuth and current_user_id
        firebaseAuth = FirebaseAuth.getInstance();
        current_user_id = firebaseAuth.getCurrentUser().getUid();

        // 7.6.1 init the toolbar
        newPostToolbar = findViewById(R.id.new_post_toolbar);
        setSupportActionBar(newPostToolbar);
        getSupportActionBar().setTitle("Add New Post!"); // for the blog post
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //9.10.1

        // 9.1.1 init the widgets
        newPostImage = findViewById(R.id.new_post_image);
        newPostDesc = findViewById(R.id.new_post_desc);
        newPostBtn = findViewById(R.id.post_btn);
        newPostProgress = findViewById(R.id.new_post_progress); // 9.7.3

        //9.2. Add a click listener to the image btn
        // same as we did for the setup , we also need the crop,
        newPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //9.3 crop the image
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setMinCropResultSize(512, 512) // set the img resolution
                        .setAspectRatio(1,1)
                        .start(NewPostActivity.this);
            }
        });

        //9.6 now when the user presses the btn, we post the new post with the image
        // add a click listener to the newPostbtn
        newPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //9.6.1 get the string from the edittext view to string
                final String desc = newPostDesc.getText().toString();

                // check that the fields are not empty
                if (!TextUtils.isEmpty(desc) && postImageUri != null) {

                    //9.6.2 if all are ok, show progress
                    newPostProgress.setVisibility(View.VISIBLE);

                    // post to storage this storage path
                    //9.8.2 define a path where to store the image
                    // 1st def a random name so user can have diff names for diff images, not the userId
                    // 10.2 edited to random()
                    final String randomName = UUID.randomUUID().toString();//random(); //FieldValue.serverTimestamp().toString();
                    //888 then use the random in the file path
                    StorageReference filePath = storageReference.child("post_image")
                            .child(randomName + ".jpg");
                    filePath.putFile(postImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {

                            //10.xx
                            final String downloadUri = task.getResult().getDownloadUrl().toString();

                            // if post to storage this storage is successful, start post to Firestore
                            if (task.isSuccessful()) {
                                // start posting to firestore
                                //10.3.3
                                File newImageFile = new File(postImageUri.getPath());
                                //10.3.2
                                try {
                                    compressedImageFile = new Compressor(NewPostActivity.this)
                                            .setMaxHeight(100) // set the max height
                                            .setMaxWidth(100) // set the max width
                                            .setQuality(2) // set the img quality low
                                            .compressToBitmap(newImageFile); // compress(imag to compress)
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                //10.4x copied from firebase link above
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos); // change bitmap to compressedImageBitmap we defined
                                byte[] thumbData = baos.toByteArray(); // lets just rename data as thumbData

                                // Photo Upload
                                // 10.4 use upload task instead of StorageReference since this is an upload task for the thumbnails
                                UploadTask uploadTask = storageReference
                                        .child("post_images/thumbs")
                                        .child(randomName + ".jpg")// store with same name as the other
                                        .putBytes(thumbData); // pass in the data you want to upload

                                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        //10.xxx if thumbs upload is successful, we do as always


                                        // get download uri for string
                                        String downloadThumbUri = taskSnapshot.getDownloadUrl().toString();

                                        //4444444444444444444444444444444444444444444444444444444444444444444444444444444444444


                                        Map<String, Object> postMap = new HashMap<>();
                                        postMap.put("image_url", downloadUri);
                                        postMap.put("image_thumb", downloadThumbUri); // put the thumbnail to the store
                                        postMap.put("desc", desc);
                                        postMap.put("user_id", current_user_id); // this user will the the current user
                                        postMap.put("timeStamp", FieldValue.serverTimestamp());

                                        // put the new collection in firestore
                                        // we want a doc with a random num so lets just omit and let firestore do that for us
                                        firebaseFirestore.collection("Posts")
                                                .add(postMap) // we omitted .document and say add(postMap) instead
                                                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentReference> task) {

                                                        if (task.isSuccessful()) {

                                                            Toast.makeText(NewPostActivity.this,
                                                                    "Post was added.", Toast.LENGTH_LONG).show();

                                                            Intent mainIntent = new Intent(NewPostActivity.this,
                                                                    MainActivity.class);
                                                            startActivity(mainIntent);
                                                            finish();
                                                        }
                                                        else {
                                                            //for failure
                                                            String errorMsg = task.getException().getMessage();
                                                            Toast.makeText(NewPostActivity.this,
                                                                    "FIRESTORE Post Error : " + errorMsg,
                                                                    Toast.LENGTH_SHORT).show();
                                                        }
                                                        // hide progress bar
                                                        newPostProgress.setVisibility(View.INVISIBLE);
                                                    }
                                                });




                                        //444444444444444444444444444444444444444444444444444444444444444444444444444444444444
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                       // if it fails
                                    }
                                });

                                //thumbFilePath.putFile(compressedImageFile)

                                //9.9.0 define a string to hold the image from the result
                               // String downloadUri = task.getResult().getDownloadUrl().toString();

                                // 9.9.1 create a Map
                                Map<String, Object> postMap = new HashMap<>();
                                postMap.put("image_url", downloadUri);
                                postMap.put("desc", desc);
                                postMap.put("user_id", current_user_id); // this user will the the current user
                                postMap.put("timeStamp", FieldValue.serverTimestamp());

                                // 9.9.4 put the new collection in firestore
                                // we want a doc with a random num so lets just omit and let firestore do that for us
                                firebaseFirestore.collection("Posts")
                                        .add(postMap) // we omitted .document and say add(postMap) instead
                                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentReference> task) {

                                        if (task.isSuccessful()) {

                                            Toast.makeText(NewPostActivity.this,
                                                    "Post was added.", Toast.LENGTH_LONG).show();

                                            Intent mainIntent = new Intent(NewPostActivity.this,
                                                    MainActivity.class);
                                            startActivity(mainIntent);
                                            finish();
                                        }
                                        else {
                                            //for failure
                                            String errorMsg = task.getException().getMessage();
                                            Toast.makeText(NewPostActivity.this,
                                                    "FIRESTORE Post Error : " + errorMsg,
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                        // hide progress bar
                                        newPostProgress.setVisibility(View.INVISIBLE);
                                    }
                                });

                            } else {
                                //for failure to add to firebase storage
                                Toast.makeText(NewPostActivity.this,
                                        "FBStorage Error: Failed to add image to storage",
                                        Toast.LENGTH_SHORT).show();

                                // make the progress bar invisible if we have an error
                                newPostProgress.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                }
                else {
                    // if the fields are not completed
                    Toast.makeText(NewPostActivity.this,
                            "Error: You need to add an image and description",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //9.4 override onActivityResult for the image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                //9.5.2 give/hand the result to the postImageUri
                postImageUri = result.getUri();
                //9.5.3 set the new post image to the uri we just got
                newPostImage.setImageURI(postImageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
    
   *//*  used this to generate random number for firebase staorage,
   video 11: don't need it anyore and we must also remove the MAX_LENGTH at top
   //10.0 random string generator
    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(MAX_LENGTH);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }*/

    private Toolbar newPostToolbar;

    private ImageView newPostImage;
    private EditText newPostDesc;
    private Button newPostBtn;

    private Uri postImageUri = null;

    private ProgressBar newPostProgress;

    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    private String current_user_id;

    private Bitmap compressedImageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        current_user_id = firebaseAuth.getCurrentUser().getUid();

        newPostToolbar = findViewById(R.id.new_post_toolbar);
        setSupportActionBar(newPostToolbar);
        getSupportActionBar().setTitle("Add New Post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        newPostImage = findViewById(R.id.new_post_image);
        newPostDesc = findViewById(R.id.new_post_desc);
        newPostBtn = findViewById(R.id.post_btn);
        newPostProgress = findViewById(R.id.new_post_progress);

        newPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setMinCropResultSize(512, 512)
                        .setAspectRatio(1, 1)
                        .start(NewPostActivity.this);

            }
        });

        newPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String desc = newPostDesc.getText().toString();

                if(!TextUtils.isEmpty(desc) && postImageUri != null){

                    newPostProgress.setVisibility(View.VISIBLE);

                    final String randomName = UUID.randomUUID().toString();

                    // COMPRESSED PHOTO UPLOAD
                    File newImageFile = new File(postImageUri.getPath());
                    try {

                        compressedImageFile = new Compressor(NewPostActivity.this)
                                .setMaxHeight(720)
                                .setMaxWidth(720)
                                .setQuality(50)
                                .compressToBitmap(newImageFile);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] imageData = baos.toByteArray();

                    // PHOTO UPLOAD

                    UploadTask filePath = storageReference.child("post_images")
                            .child(randomName + ".jpg").putBytes(imageData);
                    filePath.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {

                            final String downloadUri = task.getResult().getDownloadUrl().toString();

                            if(task.isSuccessful()){

                                File newThumbFile = new File(postImageUri.getPath());
                                try {

                                    compressedImageFile = new Compressor(NewPostActivity.this)
                                            .setMaxHeight(100)
                                            .setMaxWidth(100)
                                            .setQuality(1)
                                            .compressToBitmap(newThumbFile);

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                byte[] thumbData = baos.toByteArray();

                                UploadTask uploadTask = storageReference.child("post_images/thumbs")
                                        .child(randomName + ".jpg").putBytes(thumbData);

                                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                        String downloadthumbUri = taskSnapshot.getDownloadUrl().toString();

                                        Map<String, Object> postMap = new HashMap<>();
                                        postMap.put("image_url", downloadUri);
                                        postMap.put("image_thumb", downloadthumbUri);
                                        postMap.put("desc", desc);
                                        postMap.put("user_id", current_user_id);
                                        postMap.put("timestamp", FieldValue.serverTimestamp());

                                        firebaseFirestore.collection("Posts")
                                                .add(postMap)
                                                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentReference> task) {

                                                if(task.isSuccessful()){

                                                    Toast.makeText(NewPostActivity.this,
                                                            "Post was added", Toast.LENGTH_LONG)
                                                            .show();
                                                    Intent mainIntent = new Intent(NewPostActivity.this, MainActivity.class);
                                                    startActivity(mainIntent);
                                                    finish();

                                                } else {


                                                }

                                                newPostProgress.setVisibility(View.INVISIBLE);

                                            }
                                        });

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        //Error handling

                                    }
                                });


                            } else {

                                newPostProgress.setVisibility(View.INVISIBLE);

                            }

                        }
                    });


                }

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                postImageUri = result.getUri();
                newPostImage.setImageURI(postImageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }

    }
}
