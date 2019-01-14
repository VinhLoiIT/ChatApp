package com.app.truongnguyen.chatapp.main;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.app.truongnguyen.chatapp.MainActivity;
import com.app.truongnguyen.chatapp.R;
import com.app.truongnguyen.chatapp.data.Firebase;
import com.app.truongnguyen.chatapp.fragmentnavigationcontroller.SupportFragment;
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
    private String uId;
    private String hisName;
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
            uId = bundle.getString("id");
            hisName = bundle.getString("hisName");
        }

        mContext = getMainActivity();

        setBtnAddFrienf(firebase.isFriend(uId));
        btnToChat.setOnClickListener(this);
        btnAddFrienf.setOnClickListener(this);

        tvName.setText(hisName);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_to_chat:
                ChattingFragment chattingFragment = ChattingFragment.newInstance();

                Bundle bundle = new Bundle();
                bundle.putString("otherId", uId);
                chattingFragment.setArguments(bundle);
                ((MainActivity) mContext).presentFragment(chattingFragment);
                break;
            case R.id.btn_addfriend:
                break;
        }
    }

    private void setBtnAddFrienf(boolean isFirend) {
        if (isFirend) {
            btnAddFrienf.setBackgroundResource(R.drawable.ic_remove_friend);
        } else btnAddFrienf.setBackgroundResource(R.drawable.ic_person_add_blue_24dp);
    }
}
