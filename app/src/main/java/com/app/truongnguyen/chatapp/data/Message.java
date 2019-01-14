package com.app.truongnguyen.chatapp.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Message {

    @SerializedName("sender")
    @Expose
    private String sender;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("content")
    @Expose
    private String content;
    @SerializedName("timeStamp")
    @Expose
    private long timeStamp;

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public long getTimeStamp() {

        return timeStamp;
    }

    public Message() {
    }

    public Message(String sender, String id, String type, String content, long timeStamp) {
        this.sender = sender;
        this.id = id;
        this.type = type;
        this.content = content;
        this.timeStamp = timeStamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSender() {

        return sender;
    }

}