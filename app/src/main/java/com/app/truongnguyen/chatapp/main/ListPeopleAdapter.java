package com.app.truongnguyen.chatapp.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.truongnguyen.chatapp.R;
import com.app.truongnguyen.chatapp.data.Firebase;
import com.app.truongnguyen.chatapp.data.UserInfo;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.ByteArrayOutputStream;
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
        public RoundedImageView avatar;

        public String id;
        public String hisName;
        public Bitmap avatarBitmap = null;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.friend_name);
            email = itemView.findViewById(R.id.friend_email);
            avatar = itemView.findViewById(R.id.avatar);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!Firebase.getInstance().getUid().equals(id)) {
                        ViewProfileFragment viewProfileFragment = ViewProfileFragment.newInstance();

                        Bundle bundle = new Bundle();
                        bundle.putString("id", id);
                        bundle.putString("hisName", hisName);

                        if (avatarBitmap != null) {
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            avatarBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                            byte[] byteArray = stream.toByteArray();

                            bundle.putByteArray("hisAvatarBitmap", byteArray);
                        }

                        viewProfileFragment.setArguments(bundle);
                        ((MainActivity) context).presentFragment(viewProfileFragment);
                    } else {
                        Intent intent = new Intent(context, MyProfileActivity.class);
                        context.startActivity(intent);
                    }
                }
            });
        }

        public void onBind(UserInfo u) {
            this.name.setText(u.getUserName());
            this.email.setText(u.getEmail());
            if (u.getAvatarUri() != null) {
                this.avatarBitmap = u.getAvatarBitmap();
                this.avatar.setImageBitmap(avatarBitmap);
            }

            this.id = u.getId();
            this.hisName = u.getUserName();
        }
    }
}