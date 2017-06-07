package com.yokoding.afifur.brawijayamessenger.model;

import com.google.firebase.database.Exclude;

/**
 * Created by afifur on 25/05/17.
 */

public class User {

    private String displayName;
    private String email;
    private String connection;
    private String id;
    private int avatarId;
    private long createdAt;
    private String nomor;
    private String job;
    private String lokasi;
    private String address;
    private String image;

    private String mRecipientId;

    public User() {
    }

//    public User(String displayName, String email, String connection, int avatarId, long createdAt, String id) {
//        this.displayName = displayName;
//        this.email = email;
//        this.id = id;
//        this.connection = connection;
//        this.avatarId = avatarId;
//        this.createdAt = createdAt;
//
//    }

    public User(String displayName, String email,String connection,int avatarId,long createdAt,  String id,String nomor,String job,String lokasi,String address, String image) {
        this.displayName = displayName;
        this.email = email;
        this.id = id;
        this.address=address;
        this.nomor=nomor;
        this.avatarId = avatarId;
        this.connection = connection;
        this.job=job;
        this.lokasi = lokasi;
        this.createdAt = createdAt;
        this.image = image;
    }


    public String createUniqueChatRef(long createdAtCurrentUser, String currentUserEmail){
        String uniqueChatRef="";
        if(createdAtCurrentUser > getCreatedAt()){
            uniqueChatRef = cleanEmailAddress(currentUserEmail)+"-"+cleanEmailAddress(getUserEmail());
        }else {

            uniqueChatRef=cleanEmailAddress(getUserEmail())+"-"+cleanEmailAddress(currentUserEmail);
        }
        return uniqueChatRef;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    private String cleanEmailAddress(String email){
        //replace dot with comma since firebase does not allow dot
        return email.replace(".","-");
    }

    private String getUserEmail() {
        //Log.e("user email  ", userEmail);
        return email;
    }

    public String getLokasi() {
        return lokasi;
    }

    public String getJob() {
        return job;
    }

    public String getAddress() {
        return address;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmail() {
        return email;
    }

    public String getImage() {
        return image;
    }

    public String getConnection() {
        return connection;
    }

    public String getId() {
        return id;
    }

    public int getAvatarId() {
        return avatarId;
    }

    @Exclude
    public String getRecipientId() {
        return mRecipientId;
    }

    public void setRecipientId(String recipientId) {
        this.mRecipientId = recipientId;
    }
}
