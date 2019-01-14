package com.app.truongnguyen.chatapp;

import java.text.DateFormat;
import java.util.Date;

public class Common {
    public Common() {

    }

    static public String getTime(long timeStamp) {
        Date date = new Date(timeStamp);
        return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(date).toString();
    }
}
