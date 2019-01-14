package com.app.truongnguyen.chatapp.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.truongnguyen.chatapp.EventClass.EmptyObjectEvent;
import com.app.truongnguyen.chatapp.EventClass.Signal;
import com.app.truongnguyen.chatapp.MyProfileActivity;
import com.app.truongnguyen.chatapp.R;
import com.app.truongnguyen.chatapp.data.Firebase;
import com.app.truongnguyen.chatapp.data.UserInfo;
import com.app.truongnguyen.chatapp.fragmentnavigationcontroller.SupportFragment;
import com.app.truongnguyen.chatapp.search_fragment.ListPeopleAdapter;
import com.app.truongnguyen.chatapp.search_fragment.SearchFragment;
import com.makeramen.roundedimageview.RoundedImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContactsFragment extends SupportFragment implements View.OnClickListener {
    @BindView(R.id.avatar)
    RoundedImageView avatar;
    @BindView(R.id.search_bar)
    RelativeLayout searchBar;
    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout swipeLayout;
    @BindView(R.id.messError)
    TextView mErrorTextView;
    @BindView(R.id.list_of_friend)
    RecyclerView mRecyclerView;

    private ListPeopleAdapter mAdapter;
    private Context mContext;
    private ArrayList<UserInfo> userInfoArrayList;
    private Firebase firebase = Firebase.getInstance();

    public static ContactsFragment newInstance() {
        return new ContactsFragment();
    }

    @Nullable
    @Override
    protected View onCreateView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        mContext = getMainActivity();


        setAvatar();
        searchBar.setOnClickListener(this);
        avatar.setOnClickListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        userInfoArrayList = new ArrayList<>();
        mAdapter = new ListPeopleAdapter(getActivity(), userInfoArrayList);
        mRecyclerView.setAdapter(mAdapter);
        swipeLayout.setOnRefreshListener(this::refreshData);

        refreshData();
    }

    public void refreshData() {
        swipeLayout.setRefreshing(true);
        firebase.getFriend();
    }


    @Override
    public void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void emptyConversation(EmptyObjectEvent data) {
        EmptyObjectEvent stickyEvent = EventBus.getDefault().removeStickyEvent(EmptyObjectEvent.class);
        if (swipeLayout.isRefreshing())
            swipeLayout.setRefreshing(false);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void avatarWasChanged(Signal data) {
        setAvatar();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void getFriendInfo(UserInfo data) {
        UserInfo stickyEvent = EventBus.getDefault().removeStickyEvent(UserInfo.class);

        if (stickyEvent != null) {
            if (userInfoArrayList.isEmpty())
                userInfoArrayList.add(stickyEvent);
            else {
                boolean same = false;
                for (UserInfo c : this.userInfoArrayList)
                    if (c.getId().equals(stickyEvent.getId())) {
                        userInfoArrayList.remove(c);
                        userInfoArrayList.add(stickyEvent);
                        same = true;
                        break;
                    }
                if (!same)
                    userInfoArrayList.add(stickyEvent);
            }

            Collections.sort(userInfoArrayList, new Comparator<UserInfo>() {
                @Override
                public int compare(UserInfo o2, UserInfo o1) {
                    String t1 = o1.getUserName();
                    String t2 = o2.getUserName();
                    return t1.compareTo(t2);
                }
            });
            // Log.d("1234567", "sizeArray: " + conversationArrayList.size() + "getConversationEvent: " + stickyEvent.toString());
            mAdapter.notifyDataSetChanged();

            if (swipeLayout.isRefreshing())
                swipeLayout.setRefreshing(false);
        }
    }

    public void setAvatar() {
        Bitmap b = firebase.getAvatar();
        if (b != null) {
            avatar.setImageBitmap(b);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_bar:
                getMainActivity().presentFragment(SearchFragment.newInstance());

                break;
            case R.id.avatar:
                Intent intent = new Intent(getMainActivity(), MyProfileActivity.class);
                getMainActivity().startActivity(intent);
                //getMainActivity().presentFragment(MyProfileFragment.newInstance());
                break;
        }
    }
}
