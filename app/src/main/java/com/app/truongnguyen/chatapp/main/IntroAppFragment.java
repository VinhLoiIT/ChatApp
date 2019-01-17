package com.app.truongnguyen.chatapp.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.app.truongnguyen.chatapp.R;
import com.app.truongnguyen.chatapp.fragmentnavigationcontroller.PresentStyle;
import com.app.truongnguyen.chatapp.fragmentnavigationcontroller.SupportFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IntroAppFragment extends SupportFragment {
    @BindView(R.id.btn_back)
    ImageView back;


    private static IntroAppFragment instance = null;

    public static IntroAppFragment newInstance() {

        if (instance == null) {
            instance = new IntroAppFragment();
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
        return inflater.inflate(R.layout.fragment_intro_app, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMainActivity().dismiss();
            }
        });

    }

    @Override
    public int getPresentTransition() {
        return PresentStyle.FADE;
    }
}
