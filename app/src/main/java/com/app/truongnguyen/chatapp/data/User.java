package com.app.truongnguyen.chatapp.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class User {


    @SerializedName("phoneNumber")
    @Expose
    private String phoneNumber;

    public String getPhoneNumber() {

        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("avatarUri")
    @Expose
    private String avatarUri;

    @SerializedName("avatarIconUri")
    @Expose
    private String avatarIconUri;
    @SerializedName("avatarIconUrl")
    @Expose
    private String avatarIconUrl;
    @SerializedName("avatarUrl")
    @Expose
    private String avatarUrl;

    public String getAvatarIconUrl() {
        return avatarIconUrl;
    }

    public void setAvatarIconUrl(String avatarIconUrl) {

        this.avatarIconUrl = avatarIconUrl;
    }

    public String getAvatarUrl() {

        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getAvatarIconUri() {

        return avatarIconUri;
    }

    public void setAvatarIconUri(String avatarIconUri) {
        this.avatarIconUri = avatarIconUri;
    }
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("birthday")
    @Expose
    private String birthday;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("userName")
    @Expose
    private String userName;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("conversationsId")
    @Expose
    private ArrayList<String> conversationsId = null;
    @SerializedName(Firebase.LIST_FRIEND_FOLDER)
    @Expose
    private ArrayList<String> friendList = null;

    public void setFriendList(ArrayList<String> friendList) {
        this.friendList = friendList;
    }

    public ArrayList<String> getFriendList() {

        return friendList;
    }

    public String getAddress() {

        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAvatarUri() {

        return avatarUri;
    }

    public void setAvatarUri(String avatarUri) {
        this.avatarUri = avatarUri;
    }

    public String getGender() {

        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<String> getConversationsId() {
        return conversationsId;
    }

    public void setConversationsId(ArrayList<String> conversationsId) {
        this.conversationsId = conversationsId;
    }
}
