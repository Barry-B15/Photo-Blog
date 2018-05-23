package com.example.barry.photoblog;

import android.support.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class BlogPostId {
    // this class gets an Id and since it is an extendable class,
    // sends the id to where ever we are going to need it

    @Exclude
    public String BlogPostId;

    public <T extends BlogPostId> T withId(@NonNull final String id) {
        this.BlogPostId = id;
        return (T) this;
    }
}
