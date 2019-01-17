package com.app.truongnguyen.chatapp.EventClass;

import java.util.ArrayList;

public class DataIsChanged {
    private ArrayList<String> friendIdList;
    private ArrayList<String> cvsIdList;
    private boolean avatarIsChanged;

    public DataIsChanged(ArrayList<String> friendIdList, ArrayList<String> cvsIdList, boolean avatarIsChanged) {

        this.friendIdList = friendIdList;
        this.cvsIdList = cvsIdList;
        this.avatarIsChanged = avatarIsChanged;
    }

    public DataIsChanged(boolean avatarIsChanged) {
        this.avatarIsChanged = avatarIsChanged;
    }

    public ArrayList<String> getCvsIdList() {
        return cvsIdList;
    }

    public ArrayList<String> getFriendIdList() {

        return friendIdList;
    }

    public boolean isAvatarIsChanged() {

        return avatarIsChanged;
    }
}
