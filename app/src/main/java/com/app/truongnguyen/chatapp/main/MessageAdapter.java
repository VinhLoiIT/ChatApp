package com.app.truongnguyen.chatapp.main;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.app.truongnguyen.chatapp.Common;
import com.app.truongnguyen.chatapp.R;
import com.app.truongnguyen.chatapp.data.Message;
import com.github.library.bubbleview.BubbleTextView;

import java.util.ArrayList;

public class MessageAdapter extends ArrayAdapter<Message> {
    private String userId;
    private ArrayList<Message> messageArrayList;
    private Context context;

    public MessageAdapter(@NonNull Context context, int resource, ArrayList<Message> objects, String userId) {
        super(context, resource, objects);
        this.context = context;
        this.messageArrayList = objects;
        this.userId = userId;
    }

    public void setMessageArrayList(ArrayList<Message> messageArrayList) {
        this.messageArrayList = messageArrayList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        int layoutResource = 0;

        Message message = messageArrayList.get(position);

        if (message.getSender().equals(userId))
            layoutResource = R.layout.right_message_item;
        else
            layoutResource = R.layout.left_message_item;

        convertView = inflater.inflate(layoutResource, parent, false);

        holder = new ViewHolder(convertView);
        convertView.setTag(holder);

        holder.time.setText(Common.getTime(message.getTimeStamp()));
        holder.content.setText(message.getContent());

        return convertView;
    }

    @Override
    public int getCount() {
        return messageArrayList.size();
    }

    public class ViewHolder {
        public TextView time;
        public BubbleTextView content;

        public ViewHolder(View v) {
            this.time = v.findViewById(R.id.time_stamp);
            this.content = v.findViewById(R.id.content);
        }
    }
}
