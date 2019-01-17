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
import com.app.truongnguyen.chatapp.widget.OnOneClickListener;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsFragment extends SupportFragment {

    @BindView(R.id.change_password)
    LinearLayout changePassword;
    @BindView(R.id.login_history)
    LinearLayout loginHistory;
    @BindView(R.id.about_us)
    LinearLayout aboutUs;
    @BindView(R.id.logout)
    LinearLayout logout;
    private static SettingsFragment instance = null;

    public static SettingsFragment newInstance() {

        if (instance == null) {
            instance = new SettingsFragment();
            return instance;
        } else
            return null;
    }

    @Override
    public void onDestroy() {
        instance = null;
        super.onDestroy();
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

        changePassword.setOnClickListener(new OnOneClickListener() {
            @Override
            public void onOneClick(View v) {
                ChangePasswordFragment changePasswordFragment = new ChangePasswordFragment();
                getMainActivity().presentFragment(changePasswordFragment);
            }
        });
        aboutUs.setOnClickListener(new OnOneClickListener() {
            @Override
            public void onOneClick(View v) {
                IntroAppFragment introAppFragment = new IntroAppFragment();
                getMainActivity().presentFragment(introAppFragment);
            }
        });
        loginHistory.setOnClickListener(new OnOneClickListener() {
            @Override
            public void onOneClick(View v) {

            }
        });
        logout.setOnClickListener(new OnOneClickListener() {
            @Override
            public void onOneClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Firebase.getInstance().signOut();
                Intent intent = new Intent(getMainActivity(), SignIn.class);
                getMainActivity().startActivity(intent);
                getMainActivity().finish();
            }
        });
    }
}

