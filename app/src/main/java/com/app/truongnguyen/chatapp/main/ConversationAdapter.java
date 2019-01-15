package com.app.truongnguyen.chatapp.main;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
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
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ItemHolder> {
    Context mContext;
    private ArrayList<Conversation> mData;

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
        RoundedImageView icon;

        private String cvsId;
        private String hisName;
        private Bitmap iconBitmap = null;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ChattingFragment chattingFragment = ChattingFragment.newInstance();

                    Bundle bundle = new Bundle();
                    bundle.putString("cvsId", cvsId);
                    bundle.putString("hisName", hisName);
                    chattingFragment.setArguments(bundle);
                    ((MainActivity) mContext).presentFragment(chattingFragment);
                }
            });
        }

        public void bind(Conversation cvs) {
            this.cvsId = cvs.getId();
            this.hisName = cvs.getConversationName();
            iconBitmap = cvs.getIcon();

            if (iconBitmap != null)
                icon.setImageBitmap(iconBitmap);

            String sender = "";
            if (Firebase.getInstance().getUid().equals(cvs.getLastMessage().getSender()))
                sender = "You: ";
            messContent.setText(sender + cvs.getLastMessage().getContent());

            messTime.setText(Common.getTime(cvs.getLastMessage().getTimeStamp()));

            cvsName.setText(cvs.getConversationName());
        }
    }
}
