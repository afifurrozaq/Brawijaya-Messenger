package com.yokoding.afifur.brawijayamessenger.model;

import android.text.format.DateFormat;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

/**
 * Created by afifur on 25/05/17.
 */

public class ChatMessage implements Serializable{

    private String message;
    private String sender;
    private String recipient;
    private long timestamp;
    private String formattedTime;


    private String status_message;

    private int mRecipientOrSenderStatus;

    public ChatMessage() {
    }

    public ChatMessage(String message, String sender, String recipient,long timestamp,String formattedTime, String status_message) {
        this.message = message;
        this.recipient = recipient;
        this.sender = sender;
        this.formattedTime = formattedTime;
        this.timestamp = timestamp;
        this.status_message = status_message;
    }


    public void setRecipientOrSenderStatus(int recipientOrSenderStatus) {
        this.mRecipientOrSenderStatus = recipientOrSenderStatus;
    }
    public long getTimestamp() {
        return timestamp;
    }

    public String getStatus_message() {
        return status_message;
    }

    public void setStatus_message(String status_message){this.status_message = status_message;}

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setTime(long time) {
        this.timestamp = time;

        long oneDayInMillis = 24 * 60 * 60 * 1000;
        long timeDifference = System.currentTimeMillis() - time;

        if(timeDifference < oneDayInMillis){
            formattedTime = DateFormat.format("hh:mm a", time).toString();
        }else{
            formattedTime = DateFormat.format("dd MMM - hh:mm a", time).toString();
        }
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setFormattedTime(String formattedTime) {
        this.formattedTime = formattedTime;
    }

    public String getFormattedTime(){
        long oneDayInMillis = 24 * 60 * 60 * 1000;
        long timeDifference = System.currentTimeMillis() - timestamp;

        if(timeDifference < oneDayInMillis){
            return DateFormat.format("hh:mm a", timestamp).toString();
        }else{
            return DateFormat.format("dd MMM - hh:mm a", timestamp).toString();
        }
    }

    public String getMessage() {
        return message;
    }



    public String getRecipient(){
        return recipient;
    }

    public String getSender(){
        return sender;
    }

    @Exclude
    public int getRecipientOrSenderStatus() {
        return mRecipientOrSenderStatus;
    }
}
