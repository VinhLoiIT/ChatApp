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
import com.app.truongnguyen.chatapp.data.Conversation;
import com.app.truongnguyen.chatapp.data.Firebase;
import com.app.truongnguyen.chatapp.fragmentnavigationcontroller.SupportFragment;
import com.app.truongnguyen.chatapp.widget.OnOneClickListener;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.makeramen.roundedimageview.RoundedImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ConversationsFragment extends SupportFragment {
    @BindView(R.id.avatar)
    RoundedImageView avatarImageView;

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
    private ArrayList<Conversation> cvsList;
    private ArrayList<String> mCvsIdList;
    private Firebase firebase = Firebase.getInstance();

    private static ConversationsFragment instance = null;

    public static ConversationsFragment newInstance() {

        if (instance == null) {
            instance = new ConversationsFragment();
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
        return inflater.inflate(R.layout.fragment_conversations, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        mContext = getMainActivity();

        setAvatar(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        cvsList = new ArrayList<>();
        mCvsIdList = new ArrayList<>();
        mAdapter = new ConversationAdapter(getActivity(), cvsList);
        mRecyclerView.setAdapter(mAdapter);
        swipeLayout.setOnRefreshListener(this::refreshData);

        refreshData();
        searchBar.setOnClickListener(new OnOneClickListener() {
            @Override
            public void onOneClick(View v) {
                getMainActivity().presentFragment(SearchFragment.newInstance());
            }
        });


        avatarImageView.setOnClickListener(new OnOneClickListener() {
            @Override
            public void onOneClick(View v) {
                Intent intent = new Intent(getMainActivity(), MyProfileActivity.class);
                getMainActivity().startActivity(intent);
            }
        });
    }

    public void refreshData() {
        swipeLayout.setRefreshing(true);
        setAvatar(true);
        getConversations();
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
    public void dataIsChanged(DataIsChanged dataIsChanged) {
        DataIsChanged stickyEvent = EventBus.getDefault().removeStickyEvent(DataIsChanged.class);
        setAvatar(dataIsChanged.isAvatarIsChanged());
        getConversations();
    }


    private void getConversations() {

        if (firebase.getmCurrentUser() == null) {
            refreshFinish();
            return;
        }

        ArrayList<String> cvsIdList = firebase.getmCurrentUser().getConversationsId();
        if (cvsIdList == null || cvsIdList.equals(mCvsIdList)) {
            if (cvsIdList == null) {
                cvsList.clear();
                mCvsIdList.clear();
                mAdapter.notifyDataSetChanged();
            }
            refreshFinish();
            return;
        }

        cvsList.clear();
        mCvsIdList.clear();

        mCvsIdList = (ArrayList<String>) cvsIdList.clone();
        Log.d("54321", "getConversations: ");
        //Get Conversation
        for (String cvsId : cvsIdList) {
            firebase.getCvsDocWithId(cvsId)
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot
                                , @javax.annotation.Nullable FirebaseFirestoreException e) {

                            if (documentSnapshot == null || !documentSnapshot.exists())
                                return;
                            Conversation c = documentSnapshot.toObject(Conversation.class);


                            if (cvsList.size() >= 1 && cvsList.get(0).getId().equals(c.getId())) {
                                cvsList.set(0, c);
                                mAdapter.notifyItemChanged(0);
                                return;
                            }
                            boolean replace = false;
                            for (int i = cvsList.size() - 1; i >= 0; i--)
                                if (cvsList.get(i).getId().equals(c.getId())) {
                                    replace = true;
                                    cvsList.set(i, c);
                                }
                            Log.d("54321", "onEvent: CVS" +c.getId());
                            if (!replace)
                                cvsList.add(0, c);
                            sortCvsList();
                            mAdapter.notifyDataSetChanged();
                        }
                    });
        }
        refreshFinish();
    }

    private void sortCvsList() {
        //newest conversation
        Collections.sort(cvsList, new Comparator<Conversation>() {
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

}
