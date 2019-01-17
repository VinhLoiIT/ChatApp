package com.app.truongnguyen.chatapp.main;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.app.truongnguyen.chatapp.R;
import com.app.truongnguyen.chatapp.data.Firebase;
import com.app.truongnguyen.chatapp.data.UserInfo;
import com.app.truongnguyen.chatapp.fragmentnavigationcontroller.PresentStyle;
import com.app.truongnguyen.chatapp.fragmentnavigationcontroller.SupportFragment;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FieldValue;
import com.makeramen.roundedimageview.RoundedImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressLint("ValidFragment")
public class ViewProfileFragment extends SupportFragment implements View.OnClickListener {
    public static ViewProfileFragment instance = null;
    @BindView(R.id.user_profile_photo)
    RoundedImageView avatarImageView;
    @BindView(R.id.user_profile_name)
    TextView tvName;
    @BindView(R.id.btn_addfriend)
    Button btnAddFrienf;
    @BindView(R.id.btn_to_chat)
    Button btnToChat;

    private Context mContext;
    private Firebase firebase = Firebase.getInstance();
    private UserInfo hisInfo;

    public static ViewProfileFragment newInstance(Context mContext, UserInfo hisInfo) {
        if (instance == null) {
            instance = new ViewProfileFragment(mContext, hisInfo);
            return instance;
        } else
            return null;
    }

    @Override
    public void onDestroy() {
        instance = null;
        super.onDestroy();
    }

    @Override
    public int getPresentTransition() {
        return PresentStyle.FADE;
    }

    public ViewProfileFragment(Context mContext, UserInfo hisInfo) {
        this.mContext = mContext;
        this.hisInfo = hisInfo;
    }

    @Nullable
    @Override
    protected View onCreateView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.view_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        mContext = getMainActivity();

        setBtnAddFrienf(firebase.isFriend(hisInfo.getId()));
        btnToChat.setOnClickListener(this);
        btnAddFrienf.setOnClickListener(this);
        avatarImageView.setOnClickListener(this);

        tvName.setText(hisInfo.getUserName());
        //set avatar
        if (hisInfo.getAvatarIconUrl() != null) {
            Glide.with(mContext).load(hisInfo.getAvatarIconUrl()).into(avatarImageView);
            if (hisInfo.getAvatarUrl() != null)
                Glide.with(mContext).load(hisInfo.getAvatarUrl()).into(avatarImageView);
        }
    }

    @Override
    public void onClick(View v) {
        Bundle bundle;

        switch (v.getId()) {
            case R.id.avatar:
                Intent intent = new Intent(getMainActivity(), ViewImageActivity.class);

                bundle = new Bundle();
                bundle.putString("imageUrl", this.hisInfo.getAvatarUrl());
                intent.putExtras(bundle);

                startActivity(intent);
                break;

            case R.id.btn_to_chat:
                ChattingFragment chattingFragment = new ChattingFragment(mContext, hisInfo, null);
                ((MainActivity) mContext).presentFragment(chattingFragment);
                break;
            case R.id.btn_addfriend:
                String listFriendFolder = Firebase.LIST_FRIEND_FOLDER;

                ProgressDialog progressDialog = new ProgressDialog(getMainActivity());
                progressDialog.setTitle("Waiting...");
                progressDialog.setCanceledOnTouchOutside(true);
                progressDialog.show();

                if (!firebase.isFriend(hisInfo.getId())) {
                    firebase.getCurrentUserFolderDbs().update(listFriendFolder, FieldValue.arrayUnion(hisInfo.getId()))
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    firebase.getUserFolderDbs().document(hisInfo.getId())
                                            .update(listFriendFolder, FieldValue.arrayUnion(firebase.getUid()))
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    setBtnAddFrienf(true);
                                                    progressDialog.dismiss();
                                                }
                                            });

                                }
                            });

                } else {
                    firebase.getUserFolderDbs().document(hisInfo.getId())
                            .update(listFriendFolder, FieldValue.arrayRemove(firebase.getUid()))
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    firebase.getCurrentUserFolderDbs()
                                            .update(listFriendFolder, FieldValue.arrayRemove(hisInfo.getId()))
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    setBtnAddFrienf(false);
                                                    progressDialog.dismiss();
                                                }
                                            });
                                }
                            });
                }

                break;
        }
    }

    private void setBtnAddFrienf(boolean isFirend) {
        if (isFirend) {
            btnAddFrienf.setBackgroundResource(R.drawable.ic_remove_friend);
        } else btnAddFrienf.setBackgroundResource(R.drawable.ic_person_add_blue_24dp);
    }
}
