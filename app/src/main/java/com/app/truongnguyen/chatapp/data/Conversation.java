package com.app.truongnguyen.chatapp.data;

import android.graphics.Bitmap;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Conversation {

    @SerializedName("conversationName")
    @Expose
    private String conversationName;

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("icon")
    @Expose
    private Bitmap icon;

    public Bitmap getIcon() {

        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    @SerializedName("seen")
    @Expose
    private String seen;

    @SerializedName("lastMessage")
    @Expose
    private Message lastMessage;

    @SerializedName("usersId")
    @Expose
    private ArrayList<String> usersId = new ArrayList<>();

    public Conversation() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSeen() {
        return seen;
    }

    public void setSeen(String seen) {
        this.seen = seen;
    }

    public ArrayList<String> getUserId() {
        return usersId;
    }

    public void setUserId(ArrayList<String> usersId) {
        this.usersId = usersId;
    }

    public String getConversationName() {

        return conversationName;
    }

    public void setConversationName(String conversationName) {
        this.conversationName = conversationName;
    }

    @Override
    public String toString() {

        return "Conversation{" +
                "conversationName='" + conversationName + '\'' +
                ", id='" + id + '\'' +
                ", seen='" + seen + '\'' +
                ", lastMessage=" + lastMessage +
                ", usersId=" + usersId +
                '}';
    }

    public Message getLastMessage() {

        return lastMessage;
    }

    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }
}