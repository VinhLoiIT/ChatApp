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
import android.widget.ImageView;
import android.widget.TextView;

import com.app.truongnguyen.chatapp.R;
import com.app.truongnguyen.chatapp.data.Firebase;
import com.app.truongnguyen.chatapp.data.UserInfo;
import com.app.truongnguyen.chatapp.fragmentnavigationcontroller.PresentStyle;
import com.app.truongnguyen.chatapp.fragmentnavigationcontroller.SupportFragment;
import com.app.truongnguyen.chatapp.widget.OnOneClickListener;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FieldValue;
import com.makeramen.roundedimageview.RoundedImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressLint("ValidFragment")
public class ViewProfileFragment extends SupportFragment {
    public static ViewProfileFragment instance = null;
    @BindView(R.id.user_profile_photo)
    RoundedImageView avatarImageView;
    @BindView(R.id.user_profile_name)
    TextView tvName;
    @BindView(R.id.address)
    TextView textAddress;
    @BindView(R.id.email)
    TextView tvtEmail;
    @BindView(R.id.phone_number)
    TextView tvtphoneNumber;
    @BindView(R.id.edittext_sex)
    TextView gender;
    @BindView(R.id.btn_addfriend)
    ImageView btnAddFrienf;
    @BindView(R.id.btn_to_chat)
    ImageView btnToChat;

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

    private ViewProfileFragment(Context mContext, UserInfo hisInfo) {
        this.mContext = mContext;
        this.hisInfo = hisInfo;
    }

    @Nullable
    @Override
    protected View onCreateView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_view_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        mContext = getMainActivity();

        setBtnAddFrienf(firebase.isFriend(hisInfo.getId()));

        setOnclick();


        String nullInfo = "nothing";
        tvName.setText(hisInfo.getUserName());
        tvtEmail.setText(hisInfo.getEmail());
        textAddress.setText(nullInfo);
        if (hisInfo.getAddress() != null)
            textAddress.setText(hisInfo.getAddress());
        tvtphoneNumber.setText(nullInfo);
        if (hisInfo.getPhoneNumber() != null)
            tvtphoneNumber.setText(hisInfo.getPhoneNumber());
        gender.setText(nullInfo);
        if (hisInfo.getGender() != null)
            gender.setText(hisInfo.getGender());

        //set avatar
        if (hisInfo.getAvatarIconUrl() != null) {
            Glide.with(mContext).load(hisInfo.getAvatarIconUrl()).into(avatarImageView);
            if (hisInfo.getAvatarUrl() != null)
                Glide.with(mContext).load(hisInfo.getAvatarUrl()).into(avatarImageView);
        }
    }

    private void setBtnAddFrienf(boolean isFirend) {
        if (isFirend) {
            btnAddFrienf.setImageResource(R.drawable.ic_remove_friend);
        } else btnAddFrienf.setImageResource(R.drawable.ic_person_add_blue_24dp);
    }

    public void setOnclick() {
        btnToChat.setOnClickListener(new OnOneClickListener() {
            @Override
            public void onOneClick(View v) {
                ChattingFragment chattingFragment = ChattingFragment.newInstance(mContext, hisInfo, null);
                ((MainActivity) mContext).presentFragment(chattingFragment);
            }
        });
        btnAddFrienf.setOnClickListener(new OnOneClickListener() {
            @Override
            public void onOneClick(View v) {
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

            }
        });
        avatarImageView.setOnClickListener(new OnOneClickListener() {
            @Override
            public void onOneClick(View v) {
                Intent intent = new Intent(getMainActivity(), ViewImageActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("imageUrl", hisInfo.getAvatarUrl());
                intent.putExtras(bundle);

                startActivity(intent);
            }
        });
    }
}
