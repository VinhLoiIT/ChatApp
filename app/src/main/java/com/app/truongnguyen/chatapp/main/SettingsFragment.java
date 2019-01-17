package com.app.truongnguyen.chatapp.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.app.truongnguyen.chatapp.R;
import com.app.truongnguyen.chatapp.data.Firebase;
import com.app.truongnguyen.chatapp.fragmentnavigationcontroller.SupportFragment;
import com.app.truongnguyen.chatapp.inout.SignIn;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsFragment extends SupportFragment implements View.OnClickListener {
    @BindView(R.id.update_profile)
    LinearLayout updateProfile;
    @BindView(R.id.change_password)
    LinearLayout changePassword;
    @BindView(R.id.login_history)
    LinearLayout loginHistory;
    @BindView(R.id.about_us)
    LinearLayout aboutUs;
    @BindView(R.id.logout)
    LinearLayout logout;


    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Nullable
    @Override
    protected View onCreateView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        updateProfile.setOnClickListener(this);
        changePassword.setOnClickListener(this);
        aboutUs.setOnClickListener(this);
        loginHistory.setOnClickListener(this);
        logout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                Firebase.getInstance().signOut();
                Intent intent = new Intent(getMainActivity(), SignIn.class);
                getMainActivity().startActivity(intent);
                getMainActivity().finish();
                break;
            case R.id.change_password:
                ChangePasswordFragment changePasswordFragment = new ChangePasswordFragment();
                getMainActivity().presentFragment(changePasswordFragment);
                break;
            case R.id.about_us:
                IntroAppFragment introAppFragment = new IntroAppFragment();
                getMainActivity().presentFragment(introAppFragment);
                break;
        }
    }
}

