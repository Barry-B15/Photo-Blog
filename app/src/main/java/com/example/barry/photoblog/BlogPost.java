package com.example.barry.photoblog;

import java.util.Date;

/**
 * 1. Create a model class for our adapter.
 *      This model class needs all the attr of our db :
 *          desc, image_url, timestamp, img_thumb and user_id
 *      open the db to see these under + ADD FIELD
 *
 * 2. declare these fields
 * 3. add the getters and setters for all
 * 4. get the constructor for all too
 * 5. add an empty constructor
 * Next go create an adapter class
 *
 * part 13
 * 4. get time java.util date for the timestamp
 *      - insert the getter and setter then constrcutor for the time stamp
 *
 */
//15.4.2 have BlogPost extend BlogPostId
public class BlogPost extends BlogPostId {

   /* //2.
    public String user_id, image_url, desc, image_thumb;

    //public Timestamp timeStamp;
    //13.4
    public Date timeStamp; // timeStamp must appear same as in db or will  error

    //5. empty const
    public void BlogPost() {

    }

    //4. constructor for all
    public BlogPost(String user_id, String image_url, String desc,
                    String image_thumb, Date timeStamp) {

        this.user_id = user_id;
        this.image_url = image_url;
        this.desc = desc;
        this.image_thumb = image_thumb;
        //this.timestamp = timestamp;
        this.timeStamp = timeStamp;
    }

    //3 the getters and setters
    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImage_thumb() {
        return image_thumb;
    }

    public void setImage_thumb(String image_thumb) {
        this.image_thumb = image_thumb;
    }
    //13.4.1
    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }
*/

    public String user_id, image_url, desc, image_thumb;
    public Date timestamp;

    public BlogPost() {
        // empty constructor required
    }

    public BlogPost(String user_id, String image_url,
                    String desc, String image_thumb, Date timestamp) {
        this.user_id = user_id;
        this.image_url = image_url;
        this.desc = desc;
        this.image_thumb = image_thumb;
        this.timestamp = timestamp;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImage_thumb() {
        return image_thumb;
    }

    public void setImage_thumb(String image_thumb) {
        this.image_thumb = image_thumb;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

}
