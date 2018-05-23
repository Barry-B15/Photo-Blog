package com.example.barry.photoblog;

/**
 * create a User model class to retrieve the user data
 *      - add 2 String values image and name to it
 *      - add a constructor,
 *      - add getters and setters
 *      - and an empty constructor
 */
public class User {

    private String image, name;

    public User() {
        // empty constructor required
    }

    public User(String image, String name) {
        this.image = image;
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
