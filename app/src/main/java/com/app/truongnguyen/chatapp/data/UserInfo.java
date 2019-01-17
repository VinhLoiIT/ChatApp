package com.app.truongnguyen.chatapp.data;

import android.graphics.Bitmap;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserInfo {
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
    @SerializedName("avatarBitmap")
    @Expose
    private Bitmap avatarBitmap;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("phoneNumber")
    @Expose
    private String phoneNumber;
    @SerializedName("address")
    @Expose
    private String address;

    public String getAddress() {

        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGender() {

        return gender;
    }

    public void setGender(String gender) {

        this.gender = gender;
    }

    public String getPhoneNumber() {

        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    @SerializedName("avatarUri")
    @Expose
    private String avatarUri;
    @SerializedName("avatarUrl")
    @Expose
    private String avatarUrl;
    @SerializedName("avatarIconUrl")
    @Expose
    private String avatarIconUrl;
    @SerializedName("avatarIconUri")
    @Expose
    private String avatarIconUri;

    public String getAvatarUrl() {

        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {

        this.avatarUrl = avatarUrl;
    }

    public String getAvatarIconUrl() {

        return avatarIconUrl;
    }

    public void setAvatarIconUrl(String avatarIconUrl) {
        this.avatarIconUrl = avatarIconUrl;
    }

    public String getAvatarIconUri() {

        return avatarIconUri;
    }

    public void setAvatarIconUri(String avatarIconUri) {
        this.avatarIconUri = avatarIconUri;
    }

    public void setAvatarUri(String avatarUri) {
        this.avatarUri = avatarUri;
    }

    public String getAvatarUri() {

        return avatarUri;
    }

    public String getBirthday() {

        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public Bitmap getAvatarBitmap() {

        return avatarBitmap;
    }

    public void setAvatarBitmap(Bitmap avatarBitmap) {
        this.avatarBitmap = avatarBitmap;
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
}
