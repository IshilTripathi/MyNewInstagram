package com.example.mynewinstagram.ui.NewPost;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;



public class UserPostClass {

    @ServerTimestamp
    private Date timestamp;

    public Date getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
    String postImageUrl;
    String desc;
    String userId;
    String thmUrl;
    String docId;

    public UserPostClass(String postImageUrl, String desc, String userId, String thmUrl, String docId) {
        this.postImageUrl = postImageUrl;
        this.desc = desc;
        this.docId = docId;
        this.userId = userId;
        this.thmUrl = thmUrl;
    }
    public UserPostClass(){

    }

    public String getPostImageUrl() {
        return postImageUrl;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public void setPostImageUrl(String postImageUrl) {
        this.postImageUrl = postImageUrl;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public String getThmUrl() {
        return thmUrl;
    }

    public void setThmUrl(String thmUrl) {
        this.thmUrl = thmUrl;
    }
}
