package com.app.truongnguyen.chatapp.EventClass;

import com.app.truongnguyen.chatapp.data.Message;

import java.util.List;

public class ListMessageEvent {
    public List<Message> getMessageList() {
        return messageList;
    }

    private List<Message> messageList;

    public ListMessageEvent(List<Message> messageList) {
        this.messageList = messageList;
    }
}
