package com.app.truongnguyen.chatapp.main;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.truongnguyen.chatapp.R;
import com.app.truongnguyen.chatapp.data.Firebase;
import com.app.truongnguyen.chatapp.data.UserInfo;
import com.app.truongnguyen.chatapp.widget.OnOneClickListener;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ListPeopleAdapter extends RecyclerView.Adapter<ListPeopleAdapter.ViewHolder> {
    private ArrayList<UserInfo> mData;
    private Context context;

    public ListPeopleAdapter(Context context, ArrayList<UserInfo> userInfoArrayList) {
        this.mData = userInfoArrayList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflater = LayoutInflater.from(context)
                .inflate(R.layout.list_people_item, parent, false);
        return new ViewHolder(inflater);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.onBind(mData.get(position));
    }

    public void setFriendArrayList(ArrayList<UserInfo> friendArrayList) {
        this.mData = friendArrayList;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView email;
        public ImageView avatar;

        public UserInfo userInfo;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.friend_name);
            email = itemView.findViewById(R.id.friend_email);
            avatar = itemView.findViewById(R.id.avatar);

            itemView.setOnClickListener(new OnOneClickListener() {
                @Override
                public void onOneClick(View v) {
                    if (!Firebase.getInstance().getUid().equals(userInfo.getId())) {
                        ViewProfileFragment viewProfileFragment = ViewProfileFragment.newInstance(context, userInfo);
                        if (viewProfileFragment != null)
                            ((MainActivity) context).presentFragment(viewProfileFragment);
                    } else {
                        Intent intent = new Intent(context, MyProfileActivity.class);
                        context.startActivity(intent);
                    }
                }
            });
        }

        public void onBind(UserInfo u) {
            userInfo = u;

            this.name.setText(u.getUserName());
            this.email.setText(u.getEmail());

            if (u.getAvatarIconUrl() != null) {
                Glide.with(context).load(u.getAvatarIconUrl()).into(avatar);
            }
        }
    }
}