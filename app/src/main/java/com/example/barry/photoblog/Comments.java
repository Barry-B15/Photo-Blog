package com.example.barry.photoblog;

import java.util.Date;

/**
 * Part 18: Comments
 * https://youtu.be/kNqik9Z9ui8
 *
 * 1. declare the widgets message, user_id and timeStamp
 *      -Be sure to import the appropriate date (java.util.Date)
 *
 *  2. get the getters ans setters for all
 *  3. get the constructor for all as well
 *  4. then an empty constructor
 */
public class Comments {

    //1.
    private String message, user_id;
    private Date timestamp;

    //4. empty constructor
    public Comments() {
    }

    //3. constructor
    public Comments(String message, String user_id, Date timestamp) {
        this.message = message;
        this.user_id = user_id;
        this.timestamp = timestamp;
    }


    //2. getters and setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
