package com.app.truongnguyen.chatapp.main;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.app.truongnguyen.chatapp.EventClass.ListMessageEvent;
import com.app.truongnguyen.chatapp.R;
import com.app.truongnguyen.chatapp.data.Firebase;
import com.app.truongnguyen.chatapp.data.Message;
import com.app.truongnguyen.chatapp.fragmentnavigationcontroller.SupportFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.BindView;
import butterknife.ButterKnife;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

public class ChattingFragment extends SupportFragment {
    private static final int RC_CHECK_PERMISSION = 100;

    @BindView(R.id.input_mess)
    EmojiconEditText inputMsg;
    @BindView(R.id.emoji_button)
    ImageView emojiButton;
    @BindView(R.id.btn_send_mess)
    ImageView btnSendMess;
    @BindView(R.id.list_of_message)
    ListView listView;
    @BindView(R.id.chatToolbar)
    Toolbar chatToolbar;

    private EmojIconActions emojIconActions;
    private String cvsId = null;
    private String otherId;
    private Firebase firebase = Firebase.getInstance();
    private String uId;
    private ArrayList<Message> messageArrayList;
    private MessageAdapter adapter;
    private Context mconContext;
    private String hisName;

    public static ChattingFragment newInstance() {
        return new ChattingFragment();
    }

    @Nullable
    @Override
    protected View onCreateView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_chatting, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        mconContext = getMainActivity();
        uId = firebase.getUid();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            if (bundle.containsKey("otherId"))
                otherId = bundle.getString("otherId");
            else if (bundle.containsKey("cvsId")) {
                cvsId = bundle.getString("cvsId");
                hisName = bundle.getString("hisName");
                //Calculate otherId from cvsId
                otherId = firebase.getUId2FromCvsId(cvsId, uId);

                //Get messages by EventBus
                firebase.getMessages(cvsId);
            }
        }

        messageArrayList = new ArrayList<>();
        adapter = new MessageAdapter(mconContext, R.layout.left_message_item, messageArrayList, uId);
        listView.setAdapter(adapter);


        emojIconActions = new EmojIconActions(getMainActivity().getApplicationContext(), listView, inputMsg, emojiButton);
        emojIconActions.ShowEmojIcon();


        btnSendMess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMsg();
            }
        });

        if (cvsId == null) {
            String tempCvsId = firebase.makeCvsId(uId, otherId);
            FirebaseFirestore.getInstance().collection("conversations").document(tempCvsId).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                cvsId = tempCvsId;
                                firebase.getMessages(cvsId);
                            }
                        }
                    });
        }

        setupToolbar();
    }

    private void setupToolbar() {
        getMainActivity().setSupportActionBar(chatToolbar);

        chatToolbar.setTitle(hisName);
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
    public void getMessagesEvent(ListMessageEvent data) {
        ListMessageEvent stickyEvent = EventBus.getDefault().removeStickyEvent(ListMessageEvent.class);

        if (stickyEvent != null) {

            messageArrayList.clear();
            messageArrayList.addAll(stickyEvent.getMessageList());

            Collections.sort(messageArrayList, new Comparator<Message>() {
                @Override
                public int compare(Message o1, Message o2) {
                    if (o1.getTimeStamp() > o2.getTimeStamp())
                        return 1;
                    else if (o1.getTimeStamp() == o2.getTimeStamp())
                        return 0;
                    return -1;
                }
            });

            adapter.notifyDataSetChanged();
        }
    }

    private void sendMsg() {
        String content = inputMsg.getText().toString().trim();
        if (content.equals(""))
            return;

        //conversation doesn't exists
        if (cvsId == null) {
            cvsId = firebase.makeCvsId(uId, otherId);

            firebase.createCvsWithAMsg(cvsId, uId, otherId, content);
        } else
            firebase.sendMess(cvsId, uId, content);

        inputMsg.setText("");
        inputMsg.requestFocus();
    }
}
