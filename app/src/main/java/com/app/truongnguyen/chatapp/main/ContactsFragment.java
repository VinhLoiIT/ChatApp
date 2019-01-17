package com.app.truongnguyen.chatapp.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.truongnguyen.chatapp.EventClass.DataIsChanged;
import com.app.truongnguyen.chatapp.R;
import com.app.truongnguyen.chatapp.data.Firebase;
import com.app.truongnguyen.chatapp.data.UserInfo;
import com.app.truongnguyen.chatapp.fragmentnavigationcontroller.SupportFragment;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
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
    RoundedImageView avatarImageView;

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
    private ArrayList<UserInfo> userInfoList;
    private ArrayList<String> mFriendIdList;
    private Firebase firebase = Firebase.getInstance();

    private static ContactsFragment instance = null;

    public static ContactsFragment newInstance() {

        if (instance == null) {
            instance = new ContactsFragment();
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
        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        mContext = getMainActivity();


        setAvatar(true);

        searchBar.setOnClickListener(this);
        avatarImageView.setOnClickListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        userInfoList = new ArrayList<>();
        mFriendIdList = new ArrayList<>();
        mAdapter = new ListPeopleAdapter(getActivity(), userInfoList);
        mRecyclerView.setAdapter(mAdapter);
        swipeLayout.setOnRefreshListener(this::refreshData);

        refreshData();
    }

    public void refreshData() {
        Log.d("1234567", "refreshData: ");
        swipeLayout.setRefreshing(true);
        setAvatar(true);
        getFriend();
    }

    public void refreshFinish() {
        swipeLayout.setRefreshing(false);
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
    public void dataIsChange(DataIsChanged dataIsChanged) {
        DataIsChanged stickyEvent = EventBus.getDefault().removeStickyEvent(DataIsChanged.class);
        setAvatar(dataIsChanged.isAvatarIsChanged());
        getFriend();
    }


    public void sortFriendList() {
        Collections.sort(userInfoList, new Comparator<UserInfo>() {
            @Override
            public int compare(UserInfo o2, UserInfo o1) {
                String t1 = o1.getUserName();
                String t2 = o2.getUserName();
                return t1.compareTo(t2);
            }
        });
    }

    public void getFriend() {
        if (firebase.getmCurrentUser() == null) {
            refreshFinish();
            return;
        }

        ArrayList<String> friendIdList = firebase.getmCurrentUser().getFriendList();
        if (friendIdList == null || friendIdList.equals(this.mFriendIdList)) {
            if (friendIdList == null) {
                userInfoList.clear();
                mFriendIdList.clear();
                mAdapter.notifyDataSetChanged();
            }
            refreshFinish();
            return;
        }

        userInfoList.clear();
        mFriendIdList.clear();

        mFriendIdList = (ArrayList<String>) friendIdList.clone();
        for (String id : friendIdList)
            firebase.getUserIdFolderDbs(id).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                            if (!documentSnapshot.exists())
                                return;
                            UserInfo u = documentSnapshot.toObject(UserInfo.class);
                            userInfoList.add(u);
                            sortFriendList();
                            mAdapter.notifyDataSetChanged();
                        }
                    });

        refreshFinish();
    }

    public void setAvatar(boolean avatarIsChanged) {
        if (!avatarIsChanged)
            return;
        if (firebase.getmCurrentUser() == null)
            return;
        String url = firebase.getmCurrentUser().getAvatarIconUrl();
        if (url != null)
            Glide.with(mContext).load(url).into(avatarImageView);
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
                break;
        }
    }
}
