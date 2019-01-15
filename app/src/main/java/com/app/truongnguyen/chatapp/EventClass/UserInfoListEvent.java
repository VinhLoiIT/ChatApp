package com.app.truongnguyen.chatapp.EventClass;

import com.app.truongnguyen.chatapp.data.UserInfo;

import java.util.ArrayList;

public class UserInfoListEvent {
    private ArrayList<UserInfo> userInfoArrayList;

    public ArrayList<UserInfo> getUserInfoArrayList() {
        return userInfoArrayList;
    }

    public UserInfoListEvent(ArrayList<UserInfo> userInfoArrayList) {

        this.userInfoArrayList = userInfoArrayList;
    }
}
