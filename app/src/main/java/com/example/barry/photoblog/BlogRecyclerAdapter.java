package com.example.barry.photoblog;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.ContentValues.TAG;

/**
 * Part 12: https://youtu.be/uVbcayAaDKk?list=PLGCjwl1RrtcR4ptHvrc_PQIxDBB5MGiJA
 *
 * 1. create an adapter class: "BlogRecyclerAdapter() - this one
 * 2. have this extends RecyclerView.Adapter passing in the BlogPostRecycler adapter
 * 3. Create a ViewHolder class inside and have it extends RecyclerView.ViewHolder
 * 4. Implement the methods to fix the errors
 *
 * 5. Now go to Home Fragment and add private List<BlogPost> blog_list
 * 6. Create an empty constructor that will accept the list from our BlogPost class.
 *     - pass in (List<BlogPost> blog_list)
 *     - once we recieve that we need to assign it to a var so we can use it anywhere on the page
 *      do public List<BlogPost> blog_list   (give it same name)
 *     - get the list in the constr: this.blog_list = blog_list;
 *
 * Now the HomeFragment will recieve all our blog posts and pass it to this list
 * and getItemCount() the returns all the items in the list
 * -Go to homefragment and add codes to retrieve data from FireBase Firestore
 *
 * 7. populate the ViewHolder
 *
 * ---------------------------------------------
 * Part 13
 * 1
 * 2. Retrieve Image: declare the imageView in viewHolder and init
 * 3. Use glide to load the image:
 *      - create a Context context, init it in onCreateViewHolder
 *              and use it with glide in ViewHolder
 *      - In onBindV, def a string for the image_url
 *
 * 4. Retrieve the fb time stamp (not the sql time)
 *      go to BlogPost to instantiate the time
 * 5. Get the time in long value, we can also get directly the date.time.sec from this date
 *  for further info: https://stackoverflow.com/questions/8077530/android-get-current-timestamp
 *----------------------------------------------------------------------------
 *
 * part 14
 * 1. init firebase firestore
 * 2. retrieve user data in onBindView
 * 3. set user name and image to the views / define private TextView blogUserName, CircleImageView
 *      in ViewHolder class
 *
 * 4. Arrange post according to date posted, newest first
 *      - import firebasefirestore.Query and start to query the db in homefragment
 *      continue in HomeFragment.java
 *
 * Part 15
 * 1. Homefragment error fixes
 * 2. Retrieve thumbnails to our user image: In onBindViewHolder
 *      - ### HW: try to compress and use thumbnail for the setup image too ###
 *      Now go to Main for Likes btn
 *
 * 3.3  Cont from main: init the views in the ViewHolder
     *      private ImageView blogLikedBtn
     *      private TextView blogLikeCount
 *
 * 7. Now retrieve this data that we just passed in onBindViewHolder()
 *      String blogPistId ===
 *      and use that in the click listener
 * 8. we need the user id for the post so
 *      - init firebaseAuth)
 *      - get the current user is
 * 9. Create a Map and add the map to the firestore query
 *--------------------------------------------------------------------
 * Part 16:https://youtu.be/pXG6eghEcdU?list=PLGCjwl1RrtcR4ptHvrc_PQIxDBB5MGiJA
 *
 * 1. Check if like already exist,
 *      do a re-arrange of the codes in the click listerner to add an if statement
 * 2. For multiple pages, we may need a diff to show user already liked the post
 *      - change the color of the heart to red
 *
 * 3. Fix some performance issues add
 *      holder.setIsRecyclable(false); to the top of onBindViewHolder
 *
 * 4. Count the likes
 *      - create a method to set text to the view
 *      - create a query and call the above method in it
 *
 * */
public class BlogRecyclerAdapter
        extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder> {

   /* // 12-6.3
    public List<BlogPost> blog_list; // this can now be used anywhere in our blog

    //13.3.1
    public Context context;

    //14.1
    private FirebaseFirestore firebaseFirestore;
    // 15.8
    FirebaseAuth firebaseAuth;



    //12-6.1,2
    public BlogRecyclerAdapter(List<BlogPost> blog_list) {
        //12-6.4
        this.blog_list = blog_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate the ViewHolder
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.blog_list_item, parent, false);

        //13.3.2
        context = parent.getContext();
        //14.1.2
        firebaseFirestore = FirebaseFirestore.getInstance();

        //15.8.1
       firebaseAuth = FirebaseAuth.getInstance();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        //16.3 fix delay issue in our recycler
        holder.setIsRecyclable(false);

       // 15.7 retrieve the blogId
        final String blogPostId = blog_list.get(position).BlogPostId;
        //15.8.2
        final String currentUserId = firebaseAuth.getCurrentUser().getUid();


        // get the data as a string, notice getDesc is same as in our model class BlogPost
        String desc_data = blog_list.get(position).getDesc();
        holder.setDescText(desc_data); // setDescText is what we def in ViewHolder

        //13.3.4 def a string  and 15.2
        String image_url = blog_list.get(position).getImage_url();
        String thumbUri = blog_list.get(position).getImage_thumb(); //15.2.1
        holder.setBlogImage(image_url, thumbUri); // add thumbUri also

        //13.3.hw try this by self get the user id to show in the blog
        //14.2 retrieve user data
        String user_id = blog_list.get(position).getUser_id();
        // user data
        firebaseFirestore.collection("User").document(user_id)
                .get() // use get to get data just once, this is not for real time
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        // if task is successfull, get the username and user image
                        if (task.isSuccessful()) {
                            String userName = task.getResult().getString("name");
                            String userImage = task.getResult().getString("image");

                            // update user data
                            holder.setUserdata(userName, userImage);
                        }
                        else {
                            // Todo Error handling
                        }
                        }
                });


        //13.5 retrieve the time stamp
        long millisecond = blog_list.get(position).getTimeStamp()  // this will take this whole time stamp
                .getTime(); //and convert it to a long value of the time

        //ff class ex was giving error so replaced with that below it:
        // String dateString = DateFormat.format("MM/dd/yyyy", new Date(millisecond)).toString();
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        String dateString = String.valueOf(android.text.format.DateFormat.format("dd-MM-yyyy", new Date(millisecond)));
        holder.setTime(dateString);

        // 16.4.2 Count the likes
        firebaseFirestore.collection("Posts/" + blogPostId + "/Likes")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                // check if there are clicks
               if (!documentSnapshots.isEmpty()) {

                   // get the total counts on the post
                   int count = documentSnapshots.size();

                   // pass the count to the holder to update
                   holder.updateLikesCount(count);
               }
               else {
                   // if there are no counts
                   holder.updateLikesCount(0);
               }
            }
        });


        // 16.2 Get Likes
        firebaseFirestore.collection("Posts/" + blogPostId + "/Likes")
                .document(currentUserId) // current user id
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {

                        if (documentSnapshot.exists()) {

                            holder.blogLikeBtn.setImageDrawable(context.getDrawable(R.mipmap.action_like_accent));

                        }else {
                            holder.blogLikeBtn.setImageDrawable(context.getDrawable(R.mipmap.action_like_gray));
                        }
                    }
                });


        // LIKES FEATURE
        //15.3.5
        holder.blogLikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 16.1.0
                firebaseFirestore.collection("Posts/" + blogPostId + "/Likes")
                        .document(currentUserId) // current user id
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (!task.getResult().exists()) { // if a like does not exist, add one

                            Map<String, Object> likesMap = new HashMap<>();
                            likesMap.put("timeStamp", FieldValue.serverTimestamp());

                            firebaseFirestore.collection("Posts/" + blogPostId + "/Likes")
                                    .document(currentUserId) // current user id
                                    .set(likesMap);
                        } else { // but if it already exist, then delete
                            firebaseFirestore.collection("Posts/" + blogPostId + "/Likes")
                                    .document(currentUserId) // current user id
                                    .delete(); //
                        }
                    }
                });
                *//*
                // i5.9 create a map
                Map<String, Object> likesMap = new HashMap<>();
                likesMap.put("timeStamp", FieldValue.serverTimestamp());
                // now add this to the line below

                //15.3.6
                // which blog post do we want to like? We need to have that.
                // we go to our HomeFragment were we retrieve our posts,
                // add a string to hold each blog Id

                // 15.7.2 Now we have an Id, lets's use it.
                // 1st create a likes collection in our old collection as a sub collection
                //firebaseFirestore.collection("Posts").document().collection("Likes");
                // or short
                firebaseFirestore.collection("Posts/" + blogPostId + "/Likes")
                        .document(currentUserId) // current user id
                        .set(likesMap); //15.9.1 append the likesMap
                        *//*
                }
        });
    }

    @Override
    public int getItemCount() {
        return blog_list.size();  // this gets all the items from our list (blog_list)
    }

    // Initialize the needed views here/ get the data and give it to onBindViewHolder to hold
    public class ViewHolder extends RecyclerView.ViewHolder{

        // Initialize the needed views here
        private View mView;

        private TextView descView;
        private ImageView blogImageView;
        private TextView blogDate;
        //14.3
        private TextView blogUserName;
        private CircleImageView blogUserImage;

        // 15.3.3
        private ImageView blogLikeBtn;
        private TextView blogLikeCount;

        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;  // the view holder

            //15.3.4
            blogLikeBtn = mView.findViewById(R.id.blog_like_btn); // go add click listener in onBind
        }

        // use the view holder to populate the methods here
        // method to receive text
        public void setDescText(String descText) {
            descView = mView.findViewById(R.id.blog_desc);
            descView.setText(descText);
            // now go call this method in onBindViewHolder()
        }

        //13.2 , and 15.2 adding the thumb
        public void setBlogImage(String downloadUri, String thumbUri) { //add String thumbUri 15.2.2
            //13.2.1 init the view
            blogImageView = mView.findViewById(R.id.blog_image);
            //13.3.3
            //Glide.with(context).load(downloadUri).into(blogImageView);

            //14.3.x add placeholder to glide to avoid view distortion when loading image
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.ic_post_img);

            Glide.with(context).applyDefaultRequestOptions(requestOptions)
                    .load(downloadUri) // load the image //14.3.x
                    .thumbnail(Glide.with(context).load(thumbUri))  // load the thumbnail 15.2.3
                    .into(blogImageView);
        }

        public void setTime(String date) {

            blogDate = mView.findViewById(R.id.blog_date);
            blogDate.setText(date);
        }

        //14.3.1 method to set user data
        public void setUserdata(String name, String image) {

            blogUserImage = mView.findViewById(R.id.blog_user_image);
            blogUserName = mView.findViewById(R.id.blog_user_name);

            // set the data to the views
            blogUserName.setText(name);
           // Glide.with(context).load(image).into(blogUserImage); // use glide to load image
            // to avoid distortion when loading image, lets add a placeholder before loading with glide
            RequestOptions placeholderOption = new RequestOptions();
            placeholderOption.placeholder(R.mipmap.ic_launcher_round); // the mipmap image is same as the one osed in the xml file blog-list-item
            // then add this to glide
            Glide.with(context)
                    .applyDefaultRequestOptions(placeholderOption) // add the placeholder to glide
                    .load(image).into(blogUserImage);
            // now use this method to update data in the firestore.collection if statement
        }

        //16.4.1 set the count and call it when user clicks
        public void updateLikesCount(int count) { // if like exist we increse the count

            blogLikeCount = mView.findViewById(R.id.blog_like_count);

            blogLikeCount.setText(count + "Likes");

        }
    }
    */

    public List<BlogPost> blog_list;
    public List<User> user_list;  //19.6.2
    public Context context;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private PopupWindow popWindow;

    public BlogRecyclerAdapter(List<BlogPost> blog_list, List<User> user_list){ //19.6.1 as 2nd param

        this.blog_list = blog_list;
        this.user_list = user_list; //19.6.3

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_list_item, parent, false);
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.setIsRecyclable(false);

        final String blogPostId = blog_list.get(position).BlogPostId;
        final String currentUserId = firebaseAuth.getCurrentUser().getUid(); // signed_in user

        String desc_data = blog_list.get(position).getDesc();
        holder.setDescText(desc_data);

        String image_url = blog_list.get(position).getImage_url();
        String thumbUri = blog_list.get(position).getImage_thumb();
        holder.setBlogImage(image_url, thumbUri);

        String blog_user_id = blog_list.get(position).getUser_id(); // user making a post

        // check who can delete a post DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD
        if (blog_user_id.equals(currentUserId)) {

            holder.blogDeleteBtn.setEnabled(true);
            holder.blogDeleteBtn.setVisibility(View.VISIBLE);
        } //DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD

        /* 19.7.0 we don't need this anymore as we are now getting data from user_list
        //User Data will be retrieved here...
        firebaseFirestore.collection("User").document(user_id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){*/

        String userName = user_list.get(position).getName(); //task.getResult().getString("name");
        String userImage = user_list.get(position).getImage(); //task.getResult().getString("image");

        holder.setUserData(userName, userImage);


              /*  19.7.0 we don't need this anymore as we are now getting data from user_list
              } else {

                    //Firebase Exception

                }

            }
        });
*/
        try {
            long millisecond = blog_list.get(position).getTimestamp().getTime();
            String dateString = DateFormat
                    .format("MM-dd-yyyy", new Date(millisecond)).toString();
            holder.setTime(dateString);
        }
        catch (Exception e) {

            Toast.makeText(context, "Exception : " + e.getMessage(), Toast.LENGTH_SHORT).show();

        }

        //GET LIKE COUNT
        firebaseFirestore.collection("Posts/" + blogPostId + "/Likes")
                .addSnapshotListener( new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                //33333333333333333 Fix For Logout Crashes FB e 33333333333333333333333333333333
                if (e != null) {
                    Log.w(TAG, "listening failed", e);
                    return;
                }
                //3333333333333333333333333333333333333333333333333

                if(!documentSnapshots.isEmpty()){

                    int count = documentSnapshots.size();

                    holder.updateLikesCount(count);

                } else {

                    holder.updateLikesCount(0);

                }

            }
        });


        //Set Likes COLOR - IF LIKED CHANGE COLOR TO RED HEART
        firebaseFirestore.collection("Posts/" + blogPostId + "/Likes")
                .document(currentUserId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {

                //333333333333333333 Fix For Logout Crashes FB e 3333333333333333333333333333333
                if (e != null) {
                    Log.w(TAG, "listening failed", e);
                    return;
                }
                //3333333333333333333333333333333333333333333333333

                if(documentSnapshot.exists()){

                    holder.blogLikeBtn.setImageDrawable(context.getDrawable(R.mipmap.action_like_accent));

                } else {

                    holder.blogLikeBtn.setImageDrawable(context.getDrawable(R.mipmap.action_like_gray));

                }

            }
        });

        //Likes Feature
        holder.blogLikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firebaseFirestore.collection("Posts/" + blogPostId + "/Likes")
                        .document(currentUserId).get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if(!task.getResult().exists()){

                            Map<String, Object> likesMap = new HashMap<>();
                            likesMap.put("timestamp", FieldValue.serverTimestamp());

                            firebaseFirestore.collection("Posts/" + blogPostId + "/Likes")
                                    .document(currentUserId).set(likesMap);

                        } else {

                            firebaseFirestore.collection("Posts/" + blogPostId + "/Likes")
                                    .document(currentUserId).delete();

                        }

                    }
                });
            }
        });

        // 17.2
        holder.blogCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent commentIntent = new Intent(context, CommentsActivity.class);
                commentIntent.putExtra("blog_post_id", blogPostId); //17.2.1 pass the blog post id
                context.startActivity(commentIntent);

            }
        });

        //DDDDDDDDDDDDDDDDDDDD Delete btn click listener DDDDDDDDDDDDDDDDDDDDD
        holder.blogDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                firebaseFirestore.collection("Posts") // call to firestore collection
                        .document(blogPostId) // get the post id
                        .delete() // delete the post
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        // after deleting
                        blog_list.remove(position);   // also remove form the blog list
                        user_list.remove(position);  // also remove form the user list
                    }
                });
            }
        });
        //DDDDDDDDDDDDDDDDDDDD End Delete btn click listener DDDDDDDDDDDDDDDDDDDDD
    }


    @Override
    public int getItemCount() {
        return blog_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;

        private TextView descView;
        private ImageView blogImageView;
        private TextView blogDate;

        private TextView blogUserName;
        private CircleImageView blogUserImage;

        private ImageView blogLikeBtn;
        private TextView blogLikeCount;

        private ImageView blogCommentBtn;

        private ImageView viewBigImage;  // trying to get bigger image
        private Button blogDeleteBtn;   // for delete


        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            blogLikeBtn = mView.findViewById(R.id.blog_like_btn);
            blogCommentBtn = mView.findViewById(R.id.blog_comment_icon);
            blogDeleteBtn = mView.findViewById(R.id.blog_delete_btn);

            viewBigImage = mView.findViewById(R.id.big_image); // trying to get bigger image

        }

        public void setDescText(String descText){

            descView = mView.findViewById(R.id.blog_desc);
            descView.setText(descText);

        }

        public void setBlogImage(String downloadUri, String thumbUri){

            blogImageView = mView.findViewById(R.id.blog_image);

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.ic_post_img);

            // trying to get bigger image
            blogImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Log.d(TAG, "onClick: attempting to get bigger image");
                    Intent bigImageIntent = new Intent(context, BigImageGallery.class);
                    context.startActivity(bigImageIntent);
                }
            });
            // End trying to get bigger image

            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(downloadUri).thumbnail(
                    Glide.with(context).load(thumbUri)
            ).into(blogImageView);

        }

        public void setTime(String date) {

            blogDate = mView.findViewById(R.id.blog_date);
            blogDate.setText(date);

        }

        public void setUserData(String name, String image){

            blogUserImage = mView.findViewById(R.id.blog_user_image);
            blogUserName = mView.findViewById(R.id.blog_user_name);

            blogUserName.setText(name);

            RequestOptions placeholderOption = new RequestOptions();
            placeholderOption.placeholder(R.mipmap.ic_launcher_round);

            Glide.with(context).applyDefaultRequestOptions(placeholderOption)
                    .load(image)
                    .into(blogUserImage);

        }

        public void updateLikesCount(int count){

            blogLikeCount = mView.findViewById(R.id.blog_like_count);
            blogLikeCount.setText(count + " Likes");

        }

    }
}
