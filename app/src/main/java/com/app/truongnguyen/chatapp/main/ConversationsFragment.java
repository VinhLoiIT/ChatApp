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
import com.app.truongnguyen.chatapp.data.Conversation;
import com.app.truongnguyen.chatapp.data.Firebase;
import com.app.truongnguyen.chatapp.fragmentnavigationcontroller.SupportFragment;
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

public class ConversationsFragment extends SupportFragment implements View.OnClickListener {
    @BindView(R.id.avatar)
    RoundedImageView avatar;
    @BindView(R.id.search_bar)
    RelativeLayout searchBar;
    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout swipeLayout;
    @BindView(R.id.messError)
    TextView mErrorTextView;
    @BindView(R.id.list_of_conversation)
    RecyclerView mRecyclerView;

    private ConversationAdapter mAdapter;
    private Context mContext;
    private ArrayList<Conversation> conversationArrayList;
    private Firebase firebase = Firebase.getInstance();

    public static ConversationsFragment newInstance() {
        return new ConversationsFragment();
    }

    @Nullable
    @Override
    protected View onCreateView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_conversations, container, false);
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
        conversationArrayList = new ArrayList<>();
        mAdapter = new ConversationAdapter(getActivity(), conversationArrayList);
        mRecyclerView.setAdapter(mAdapter);
        swipeLayout.setOnRefreshListener(this::refreshData);

        refreshData();
    }

    public void refreshData() {
        swipeLayout.setRefreshing(true);
        firebase.getConversation();
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
    public void getConversationEvent(Conversation data) {
        Conversation stickyEvent = EventBus.getDefault().removeStickyEvent(Conversation.class);

        if (stickyEvent != null) {
            if (conversationArrayList.isEmpty())
                conversationArrayList.add(stickyEvent);
            else {
                boolean same = false;
                for (Conversation c : this.conversationArrayList)
                    if (c.getId().equals(stickyEvent.getId())) {
                        conversationArrayList.remove(c);
                        conversationArrayList.add(stickyEvent);
                        same = true;
                        break;
                    }
                if (!same)
                    conversationArrayList.add(stickyEvent);
            }

            Collections.sort(conversationArrayList, new Comparator<Conversation>() {
                @Override
                public int compare(Conversation o2, Conversation o1) {
                    long t1 = o1.getLastMessage().getTimeStamp();
                    long t2 = o2.getLastMessage().getTimeStamp();
                    if (t1 > t2)
                        return 1;
                    else if (t1 == t2)
                        return 0;
                    return -1;
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
