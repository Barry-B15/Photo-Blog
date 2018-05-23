package com.example.barry.photoblog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * part 17 : Comments
 * https://youtu.be/kwFQ2khAOjQ
 * 1. create the comment Activity and add the views, and init them here
 * 2. In the RecyclerAdapter:
 *      Add a comment click listener
 *      - add an intent extra for the post id
 *      - get the intent extra here in comment
 * 3. SEND USER POST TO DB
 *      - get the db, and current user
 *      - add click listener to the comment btn
 *      - declare a string to hold user id
 * 4. Define another string in the on click to store the text from users
 *      - check if that string is not empty
 *      - put the post in db in a sub collection
 *      - we need to pass in a Hashmap() so create one just above the db query and pass it.
 *
 * Part 18 RETRIEVE USER COMMENTS
 * https://youtu.be/kNqik9Z9ui8
 *
 * 1. to get the id of the comment list,
 *      - declare an obj of the recycler comment list
 *      - init the list in onCreate
 * 2. point to the blog post tha has the comment
 *      - add a snapshot listener
 *      - attach the commentActivity as a param so that the listener can be paused when not needed
 *      - add the if statements to check for data
 *      - if there is a comment, get the comment id
 * 3. Create a model class Comments to hold the objs of our comment
 *      - declare the widgets message, user_id and timeStamp
 * 4. Create a Recycler adapter for our comments RecyclerView
 * 5. Add the Adapter and commentList here after creating the constructor in the Comment Adapter
 *      - init the 2
 */
public class CommentsActivity extends AppCompatActivity {

    private Toolbar commentToolbar;

    //18.1.0
    private RecyclerView comment_list;
    //18.5
    private CommentsRecyclerAdapter commentsRecyclerAdapter;
    private List<Comments> commentsList;

    // 17.3
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private String current_user_id;

    private EditText comment_field;
    private ImageView comment_post_btn;

    private String blog_post_id;  //16.2.2 this id is used in the intent in BlogRecyclerAdapter

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        commentToolbar = findViewById(R.id.comment_toolbar);
        setSupportActionBar(commentToolbar);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //17.3.1
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        current_user_id = firebaseAuth.getCurrentUser().getUid();


        // 17.2.2 get the intent extra
        blog_post_id = getIntent().getStringExtra("blog_post_id");

        comment_field = findViewById(R.id.comment_field);
        comment_post_btn = findViewById(R.id.comment_post_btn);
        //18.1.1
        comment_list = findViewById(R.id.comment_list);

        //18.5.1 RecyclerView Firebase List
        commentsList = new ArrayList<>();
        commentsRecyclerAdapter = new CommentsRecyclerAdapter(commentsList);
        comment_list.setHasFixedSize(true);
        comment_list.setLayoutManager(new LinearLayoutManager(this));
        comment_list.setAdapter(commentsRecyclerAdapter);

        //18.2.0 point firebase to the blog post that has the comment and add listener
        firebaseFirestore.collection("Posts/" + blog_post_id + "/Comments")
                .addSnapshotListener(CommentsActivity.this, //18.2.1 add the activity so it pauses listener when not needed
                        new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                        if (!documentSnapshots.isEmpty()) { //18.2.2 if there is a comment

                            for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                                if (doc.getType() == DocumentChange.Type.ADDED) { // if the type is ADDED

                                    //18.2.3 get the id of the comment
                                    String commentId = doc.getDocument().getId();
                                    //18.5.2
                                    Comments comments = doc.getDocument().toObject(Comments.class); // convert the doc to obj
                                    commentsList.add(comments);  // add that to the list which goes to the adapter
                                    commentsRecyclerAdapter.notifyDataSetChanged(); // the adapter can now notify changes
                                    // gets this doc and set it to the list { public List<Comments> commentsList }
                                    // in our CommentsRecyclerAdapter.java
                                    // now go get this in the adapter { the commentsRecyclerAdapter }
                                }
                            }

                        }
                    }
                });

        // SEND USER POST TO DB
        comment_post_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //17.4.0 get user text and store in the var comment_message
                String comment_message = comment_field.getText().toString();

                //17.4.1 check that the comment is not empty
                if (!comment_message.isEmpty()) {

                    //17.4.3 Hashmap
                    Map<String, Object> commentsMap = new HashMap<>();
                    commentsMap.put("message", comment_message); // the text entered by user
                    commentsMap.put("user_id", current_user_id); // the id of the one making the comment
                    commentsMap.put("timestamp", FieldValue.serverTimestamp()); // time the comment made /optional

                    //17.4.2 put the post in db in a sub collection passing the comments map + on complete listener
                    firebaseFirestore.collection("Posts/" + blog_post_id + "/Comments")
                            .add(commentsMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {

                            // 17.4.3 check for success or failure of comment post
                            if (!task.isSuccessful()) {

                                // if task is not successful, send a toast the reason
                                Toast.makeText(CommentsActivity.this,
                                        "Error posting comment: " + task.getException()
                                                .getMessage(), Toast.LENGTH_LONG).show();
                            }
                            else {
                                // if successful, clear the edit text view
                                comment_field.setText(""); // empty the text field
                            }
                        }
                    });
                }

            }
        });
    }
}
