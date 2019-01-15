package com.app.truongnguyen.chatapp.main;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.app.truongnguyen.chatapp.fragmentnavigationcontroller.SupportFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FieldValue;
import com.makeramen.roundedimageview.RoundedImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewProfileFragment extends SupportFragment implements View.OnClickListener {
    @BindView(R.id.avatar)
    RoundedImageView avatar;
    @BindView(R.id.tv_user_name)
    TextView tvName;
    @BindView(R.id.btn_addfriend)
    Button btnAddFrienf;
    @BindView(R.id.btn_to_chat)
    Button btnToChat;

    private Context mContext;
    private String hisId;
    private String hisName;
    private Bitmap hisAvatarBitnap = null;

    private Firebase firebase = Firebase.getInstance();

    public static ViewProfileFragment newInstance() {
        return new ViewProfileFragment();
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

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            hisId = bundle.getString("id");
            hisName = bundle.getString("hisName");

            byte[] byteArray = bundle.getByteArray("hisAvatarBitmap");
            if (byteArray != null)
                hisAvatarBitnap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        }

        mContext = getMainActivity();

        setBtnAddFrienf(firebase.isFriend(hisId));
        btnToChat.setOnClickListener(this);
        btnAddFrienf.setOnClickListener(this);

        if (hisAvatarBitnap != null)
            avatar.setImageBitmap(hisAvatarBitnap);
        tvName.setText(hisName);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_to_chat:
                ChattingFragment chattingFragment = ChattingFragment.newInstance();

                Bundle bundle = new Bundle();
                bundle.putString("otherId", hisId);
                chattingFragment.setArguments(bundle);
                ((SettingsFragment.MainActivity) mContext).presentFragment(chattingFragment);
                break;
            case R.id.btn_addfriend:
                String listFriendFolder = Firebase.LIST_FRIEND_FOLDER;

                ProgressDialog progressDialog = new ProgressDialog(getMainActivity());
                progressDialog.setTitle("Waiting...");
                progressDialog.setCanceledOnTouchOutside(true);
                progressDialog.show();

                if (!firebase.isFriend(hisId)) {
                    firebase.getUserCurrentFolderDbs().update(listFriendFolder, FieldValue.arrayUnion(hisId))
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    firebase.getUserFolderDbs().document(hisId)
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
                    firebase.getUserFolderDbs().document(hisId)
                            .update(listFriendFolder, FieldValue.arrayRemove(firebase.getUid()))
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    firebase.getUserCurrentFolderDbs()
                                            .update(listFriendFolder, FieldValue.arrayRemove(hisId))
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
