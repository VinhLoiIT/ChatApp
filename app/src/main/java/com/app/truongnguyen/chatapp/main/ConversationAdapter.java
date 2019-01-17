package com.app.truongnguyen.chatapp.main;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.truongnguyen.chatapp.Common;
import com.app.truongnguyen.chatapp.R;
import com.app.truongnguyen.chatapp.data.Conversation;
import com.app.truongnguyen.chatapp.data.Firebase;
import com.app.truongnguyen.chatapp.data.UserInfo;
import com.app.truongnguyen.chatapp.widget.OnOneClickListener;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ItemHolder> {
    Context mContext;
    private ArrayList<Conversation> mData;
    private Firebase firebase = Firebase.getInstance();


    public ConversationAdapter(Context context, ArrayList<Conversation> data) {
        mData = data;
        mContext = context;
    }

    public void setData(ArrayList<Conversation> data) {
        mData.clear();
        if (data != null) {
            mData.addAll(data);
        }
        notifyDataSetChanged();
    }

    public void addData(ArrayList<Conversation> data) {
        if (data != null) {
            int posBefore = mData.size();
            mData.addAll(data);
            notifyItemRangeInserted(posBefore, data.size());
        }
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.list_conversation_item, viewGroup, false);

        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder itemHolder, int i) {
        itemHolder.bind(mData.get(i));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.conversation_name)
        TextView cvsName;
        @BindView(R.id.last_message_content)
        TextView messContent;
        @BindView(R.id.last_message_time)
        TextView messTime;
        @BindView(R.id.cvs_avatar)
        RoundedImageView cvsIcon;

        UserInfo userInfo;
        String cvsId;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new OnOneClickListener() {
                @Override
                public void onOneClick(View v) {
                    ChattingFragment chattingFragment = new ChattingFragment(mContext, userInfo, cvsId);
                    ((MainActivity) mContext).presentFragment(chattingFragment);
                }
            });

        }

        public void bind(Conversation cvs) {

            String hisId = firebase.getOtherIdFromCvsId(cvs.getId());

            //get his name and his avatarImageView icon
            firebase.getUserIdFolderDbs(hisId).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot doc) {

                            if (doc == null || !doc.exists())
                                return;
                            userInfo = doc.toObject(UserInfo.class);

                            cvsName.setText(userInfo.getUserName());

                            String avtarIconUrl = userInfo.getAvatarIconUrl();
                            if (avtarIconUrl != null)
                                Glide.with(mContext).load(avtarIconUrl).into(cvsIcon);
                        }
                    });

            //set Sender
            String sender = "";
            if (firebase.getUid().equals(cvs.getLastMessage().getSender()))
                sender = "You: ";
            messContent.setText(sender + cvs.getLastMessage().getContent());

            messTime.setText(Common.getTime(cvs.getLastMessage().getTimeStamp()));

            cvsName.setText(cvs.getConversationName());
        }
    }
}
