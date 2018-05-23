package com.example.barry.photoblog;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * 1. get an instance of the recycler view, init it
 * and define the View view to return view; to inflate the adapter
 * 2. create a layout for the blog list item
 * add a circular image view, and other views to it
 * 3. start retrieving those views in java
 * 4. Create a model class for our adapter
 * ----
 * come here after coding the adapter
 * 5. Create a list for the model class we created to help us retrieve data from the firestore
 * private List<BlogPost> blog_list
 * <p>
 * 6. In BlogRecyclerAdapter(), Create an empty const that will accept our list
 * (same as item 6 in BlogRecyclerAdapter())
 * <p>
 * ------ come here after The above adapter and other coding -------
 * 7. Init the recyclerAdapter here
 * <p>
 * 8. to retrieve data from FireBase Firestore
 * get the instance and init
 * <p>
 * -----------------------------------------------------------
 * Part 14 this started in BlogAdapter
 * https://youtu.be/btPJWA3Hbvs?list=PLGCjwl1RrtcR4ptHvrc_PQIxDBB5MGiJA
 * <p>
 * 4. Arrange post according to date posted, newest first
 * - do a Query to the db ( make sure the import is firebase firestore.Query )
 * <p>
 * 5. Pagination deals with how many item shows on a page
 * the last visible item needs to query the system to get the next set of item
 * cont this till the last item is displayed
 * start at repeats the last item: 1 2 3, 3 4 5, 5 6 7, 7 8 9
 * start after takes the next item: 1 2 3, 4 5 6, 7 8 9
 * read more at firebase.google.com/docs/firestore/query-data/query-cusors?authuser=1
 * <p>
 * 1. add limit of 3 items per page to the Query we just made
 * 2. add a method to run the next query after the first set
 * 3. add a lastvisible snapshot
 * <p>
 * 6. When to call the next set of items, must be when we scrolled to the end so:
 * - add onScroll in the recycler, do not select the onScrollState when prompted
 * - if we reached the end, call loadMorePosts() here
 * - last item will not always be the same, so we need to change our last visible item
 * define that last visible item in loadMorePosts() with -1
 * - add (!documentSnapshots.isEmpty) to the if statement in loadMorePosts() so it only
 * scrolls when there is something to load. WITHOUT THIS OUR APP WILL CRASH, seeing nothing to load
 * <p>
 * Part 15
 * 1. Fix errors:
 * - Always add getActivity() after using addsnapshot() in fragment,
 * addSnapshot(getActivity(), new EventListener...)
 * and "this" when working withActivity()
 * addSnapshot(this, new EventListener...)
 * This attaches the listener when app is active and detaches when idle.
 * <p>
 * - Pagination will be weird when people add new items while we are scrolling
 * Fix: In HomeFragment: declare a boolean
 * private Boolean isFirstPageFirstLoad
 * and use it in the codes
 * <p>
 * 2. Add thumbnails to our user image: go to BlogRecyclerAdapter()
 * <p>
 * 3. in adapter
 * <p>
 * 4. Create an Extendable class "BolgPostId" to handle the id
 * <p>
 * 5.  Have our model class BlogPost extend this class BlogPostId
 * <p>
 * 6. append the BlogPostId to the end of BlogBpost blogPost = .....withId(blogPostId) like this
 * - do the same for loadMorePosts()
 * <p>
 * 7. Now retrieve this data that we just passed in onBindViewHolder() in the RecyclerAdapter()
 * <p>
 *fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff
 * Part 19: Fix for delay in image loading
 * https://youtu.be/vCa7kCiFtOM
 * <p>
 * 1. In HomeFragment, get the blogUser id for the blogPost in View onCreateView() when data changes
 * 2. create a User model class to retrieve the user data
 * - add 2 String values image and name to it
 * - add a constructor,
 * - add getters and setters
 * - and an empty constructor
 * <p>
 * 3. create a new var " List<User> user_list " here to store all the users
 * - assign some values to it
 * - add onComplete listener to fireStore
 * - add as 2nd param to
 *         blogRecyclerAdapter = new BlogRecyclerAdapter(blog_list, user_list);
 * <p>
 * 4. copy and paste 3 to loadMorePost
 * remove the second if statement, we don't need that part
 * <p>
 * 5. add this new user_list array to the blogRecyclerAdapter in the View onCreateView()
 * <p>
 * 6, It will redline, Go to the BlogRecyclerAdapter class to retrieve it.
 * just add it to the public RecyclerAdapter(List<BlogPost> blog_list, List<User> user_list
 * as a second param
 * <p>
 * def the list and add it to the constructor
 * <p>
 * 7. In the BlogRecyclerAdapter() class, onBindViewHolder, we dont need the listener anymore
 * as we are using the User class. Remove the whole block of
 * firebaseFirestore.collection("User").document(user_id).get()
 * .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
 * and the closing part
 * RUN and see
 */

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

   /* //12-1
    private RecyclerView blog_list_view;
    //12-5.0
    private List<BlogPost> blog_list; // this is an array list, will help us retrieve data

    // 12-8.0
    private FirebaseFirestore firebaseFirestore;

    private FirebaseAuth mAuth; // 3333333333333333333333333333333
    //12-7.0
    private BlogRecyclerAdapter blogRecyclerAdapter;

    // 14.5.3
    private DocumentSnapshot lastVisible;

    //15.1.2 fix for pagination
    private Boolean isFirstPageFirstLoad = true; // true whendata loaded 2st time


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //12-5.1
        blog_list = new ArrayList<>();
        //12-1.1
        blog_list_view = view.findViewById(R.id.blog_list_view);

        //12-7.1 init passing in the blog_list
        blogRecyclerAdapter = new BlogRecyclerAdapter(blog_list);
        blog_list_view.setLayoutManager(new LinearLayoutManager(container.getContext()));
        blog_list_view.setAdapter(blogRecyclerAdapter);

        //12-8.2
        firebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();  // init fb auth

        if (mAuth.getCurrentUser() != null) { // check if user is signed in

            //14.6 add onScroll listner
            blog_list_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) { // select onScrolled()
                    super.onScrolled(recyclerView, dx, dy);

                    //14.6.1 boolean to see if the scroll has reached the bottom
                    Boolean reachedBottom = !recyclerView.canScrollVertically(1);

                    if (reachedBottom) {

                        String desc = lastVisible.getString("desc"); // "desc" must match the one in the db
                        Toast.makeText(container.getContext(),
                                "Reached : " + desc, Toast.LENGTH_SHORT).show();

                        // if bottom is reached, give a toast then load more post
                        loadMorePosts();
                    }
                }
            });

            //14.4 arrange posts, newest first
            Query firstQuery = firebaseFirestore.collection("Posts")  // get the posts
                    .orderBy("timeStamp",           // order it by the time stamp
                            Query.Direction.DESCENDING)  // in descending order
                    .limit(3);  // 14.5.1 limit the no of posts per page to 3 (choose no. as needed)

            //12.8.3
            //14.4.2 the first part of this is now given to the firstQuery above so no longer needed,
            // we just need to append the Complete listener to to firstQuery
            //12=8.3
            //firebaseFirestore.collection("Posts") // Posts are what we want to retrieve from the store here
            // use "this" for activity() and getActivity() for fragment to attach itself to the activity
            // to avoid crashes This also minimizes data use as it stops when the app is not in use
            firstQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() { // take a snapshot of the Posts
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                    //14.5.3 last visible snapshot that will be ref for the next set of items to load
                    // has to be outside the for loop cos it will run just once
                    //15.1.2iii put last visible in an if statement to check if this loaded before
                    if (isFirstPageFirstLoad) {
                        lastVisible = documentSnapshots.getDocuments()
                                .get(documentSnapshots.size() - 1); // -1 is the last item on the pg
                    }

                    //if (!documentSnapshots.isEmpty()) { // my fix for null pointer, not working

                    //12.8.4 a for loop to check for doc change
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                        //12.8.5 check if the doc is added
                        if (doc.getType() == DocumentChange.Type.ADDED) {// we also have deleted, remove, edit? types

                            //15.3.7 (see adapter 15.3.6)
                            // create a string to hold the blog id
                            String blogPostId = doc.getDocument().getId();
                            //15.4 create an extendable class to send this data to the adapter


                            //12.2.5 receive the added data and 15.6
                            BlogPost blogPost = doc.getDocument()
                                    .toObject(BlogPost.class)
                                    .withId(blogPostId); //15.6 append the BlogPostId to the end of

                            // wrap another if here and tell what the listener should do
                            if (isFirstPageFirstLoad) {
                                //12.5.5
                                blog_list.add(blogPost); // add the new post to the list

                            } else {
                                blog_list.add(0, blogPost); // add it to position 0
                            }
                            ///12-7.0 notify data chage
                            blogRecyclerAdapter.notifyDataSetChanged();

                        }
                    }
                    // 15.1.2ii
                    isFirstPageFirstLoad = false; // means data has loaded once
                    //}

                }
            });
        }// end of fb auth check

        // Inflate the layout for this fragment
        return view;
    }

    // 14.5.2 method to run the next query
    // this method loads the next set of blog post, similar to the first query but must include load after
    // after what? we need to define and documentSnapshot "lastVisible", put that in firstQuery
    // and to ref. it here, place it before the limit()
    public void loadMorePosts() {

        Query nextQuery = firebaseFirestore.collection("Posts")  // get the posts
                .orderBy("timeStamp",           // order it by the time stamp
                        Query.Direction.DESCENDING)  // in descending order
                .startAfter(lastVisible) //14.5.3x telling the blog where to start the next set of items
                .limit(3);  // 14.5.1 limit the no of posts per page to 3 (choose no. as needed)

        nextQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() { // take a snapshot of the Posts
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if (!documentSnapshots.isEmpty()) {

                    //14.6.3 keep changing the last visible item
                    lastVisible = documentSnapshots.getDocuments()
                            .get(documentSnapshots.size() - 1);

                    // a for loop to check for doc change
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                        // check if the doc is added
                        if (doc.getType() == DocumentChange.Type.ADDED) {// we also have deleted, remove, edit? types

                            //15.3.7 (see adapter 15.3.6)
                            // create a string to hold the blog id
                            String blogPostId = doc.getDocument().getId();

                            // receive the added data
                            BlogPost blogPost = doc.getDocument()
                                    .toObject(BlogPost.class)
                                    .withId(blogPostId);  // 15.6 append the blog post id

                            blog_list.add(blogPost); // add the new post to the list

                            //notify data chage
                            blogRecyclerAdapter.notifyDataSetChanged();

                        }
                    }
                }
            }
        });
    }*/

    private RecyclerView blog_list_view;
    private List<BlogPost> blog_list;

    //19.3.0
    private List<User> user_list;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private BlogRecyclerAdapter blogRecyclerAdapter;

    private DocumentSnapshot lastVisible;
    private Boolean isFirstPageFirstLoad = true;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        blog_list = new ArrayList<>();
        user_list = new ArrayList<>(); // 19.3.1
        blog_list_view = view.findViewById(R.id.blog_list_view);

        firebaseAuth = FirebaseAuth.getInstance();

        blogRecyclerAdapter = new BlogRecyclerAdapter(blog_list, user_list); //19.3.1.1 add user_list as 2nd param
        blog_list_view.setLayoutManager(new LinearLayoutManager(container.getContext()));
        blog_list_view.setAdapter(blogRecyclerAdapter);
        blog_list_view.setHasFixedSize(true);

        if (firebaseAuth.getCurrentUser() != null) {

            firebaseFirestore = FirebaseFirestore.getInstance();

            blog_list_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    Boolean reachedBottom = !recyclerView.canScrollVertically(1);

                    if (reachedBottom) {

                        loadMorePost();

                    }

                }
            });

            Query firstQuery = firebaseFirestore.collection("Posts")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .limit(3);
            firstQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(final QuerySnapshot documentSnapshots,
                                    final FirebaseFirestoreException e) {

                    //3333333333333333333Fix For Logout Crashes FB e 333333333333333333333333333333
                    if (e != null) {
                        Log.w(TAG, "listening failed", e);
                        return;
                    }
                    //3333333333333333333333333333333333333333333333333

                    if (!documentSnapshots.isEmpty()) {

                        if (isFirstPageFirstLoad) {

                            lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
                            blog_list.clear();
                            user_list.clear(); // 19.3.x
                        }

                        for (final DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                            if (doc.getType() == DocumentChange.Type.ADDED) {

                                String blogPostId = doc.getDocument().getId();
                                final BlogPost blogPost = doc.getDocument()
                                        .toObject(BlogPost.class)
                                        .withId(blogPostId);

                                // 19.1.0 get the blog user id
                                String blogUserId = doc.getDocument().getString("user_id"); // user_id is same as we have in firestore
                                firebaseFirestore.collection("Users").document(blogUserId) // 19.3.2
                                        .get()  // we will get this just one time
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                                //19.3.3 check for success
                                                if (task.isSuccessful()) {

                                                    //if (documentSnapshots.isEmpty()) {

                                                        //19.3.4 if successful, store the data in var user
                                                        User user = task.getResult().toObject(User.class);


                                                        //19.3.4 move the if statement from below into this if task is successful
                                                        // so that the blog list is added the same time as the user list is added
                                                        if (isFirstPageFirstLoad) {

                                                            // put that in to user_list and can also put in to the recycler adapter
                                                            user_list.add(user); //19.3.5
                                                            blog_list.add(blogPost); // click > alt + enter > make blogPost final to fix error

                                                        } else {

                                                            user_list.add(0, user); //19,3,5
                                                            blog_list.add(0, blogPost);

                                                        }
                                                    //}

                                                    // 19.3.7 also move this into the upper loop
                                                    blogRecyclerAdapter.notifyDataSetChanged();

                                                    //19.3.x create a User() model class to retrieve the user data
                                                } else {

                                                    // handle error
                                                }
                                            }
                                        });

                                /* //19.3.   move this if statement into the if task is successful above
                                // so that the blog list is added the same time as the user list is added
                                if (isFirstPageFirstLoad) {

                                    blog_list.add(blogPost);

                                } else {

                                    blog_list.add(0, blogPost);

                                }*/


                                // 19.3.7 also move this into the upper loop
                                //blogRecyclerAdapter.notifyDataSetChanged();

                            }
                        }

                        isFirstPageFirstLoad = false;

                    }

                }

            });

        }

        // Inflate the layout for this fragment
        return view;
    }

    public void loadMorePost() {

        if (firebaseAuth.getCurrentUser() != null) {

            Query nextQuery = firebaseFirestore.collection("Posts")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .startAfter(lastVisible)
                    .limit(3);

            nextQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                    //3333333333333333Fix For Logout Crashes FB e 33333333333333333333333333333333
                    if (e != null) {
                        Log.w(TAG, "listening failed", e);
                        return;
                    }
                    //3333333333333333333333333333333333333333333333333

                    if (!documentSnapshots.isEmpty()) {

                        lastVisible = documentSnapshots.getDocuments()
                                .get(documentSnapshots.size() - 1);

                        for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                            if (doc.getType() == DocumentChange.Type.ADDED) {

                                String blogPostId = doc.getDocument().getId();
                                final BlogPost blogPost = doc.getDocument()
                                        .toObject(BlogPost.class)
                                        .withId(blogPostId);

                                /*19.4.0 Replace this with the same as above, just copy paste
                                blog_list.add(blogPost);

                                blogRecyclerAdapter.notifyDataSetChanged();*/

                                // 19.4.0 Replace the above with the following, just copy paste from the View onCreate View
                                // but remove the second if as we don't need it here
                                String blogUserId = doc.getDocument().getString("user_id"); // user_id is same as we have in firestore
                                firebaseFirestore.collection("Users").document(blogUserId) // 19.3.2
                                        .get()  // we will get this just one time
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                                //19.3.3 check for success
                                                if (task.isSuccessful()) {

                                                    //19.3.4 if successful, store the data in var user
                                                    User user = task.getResult().toObject(User.class);

                                                    //19.3.4 move the if statement from below into this if task is successful
                                                    // so that the blog list is added the same time as the user list is added


                                                    // put that in to user_list and can also put in to the recycler adapter
                                                    user_list.add(user); //19.3.5
                                                    blog_list.add(blogPost); // click > alt + enter > make blogPost final to fix error

                                                    // 19.3.7 also move this into the upper loop
                                                    blogRecyclerAdapter.notifyDataSetChanged();

                                                    // create a user model class to retrieve the user data
                                                } else {

                                                    // handle error
                                                }
                                            }
                                        });
                            }

                        }
                    }

                }
            });

        }

    }
}
