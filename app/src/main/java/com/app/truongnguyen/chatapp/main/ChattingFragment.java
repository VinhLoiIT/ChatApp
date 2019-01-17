package com.app.truongnguyen.chatapp.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.app.truongnguyen.chatapp.EventClass.ListMessageEvent;
import com.app.truongnguyen.chatapp.R;
import com.app.truongnguyen.chatapp.call.CallScreenActivity;
import com.app.truongnguyen.chatapp.call.SinchService;
import com.app.truongnguyen.chatapp.data.Conversation;
import com.app.truongnguyen.chatapp.data.Firebase;
import com.app.truongnguyen.chatapp.data.Message;
import com.app.truongnguyen.chatapp.data.UserInfo;
import com.app.truongnguyen.chatapp.fragmentnavigationcontroller.PresentStyle;
import com.app.truongnguyen.chatapp.fragmentnavigationcontroller.SupportFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.sinch.android.rtc.calling.Call;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressLint("ValidFragment")
public class ChattingFragment extends SupportFragment {
    private static final int RC_CHECK_PERMISSION = 100;

    @BindView(R.id.text_input_msg)
    TextInputLayout textInputMsg;

    @BindView(R.id.edit_text_msg)
    TextInputEditText editTextMsg;

    @BindView(R.id.btn_send_mess)
    ImageView btnSendMess;

    @BindView(R.id.list_of_message)
    ListView listView;

    @BindView(R.id.chatToolbar)
    Toolbar chatToolbar;

    private Firebase firebase = Firebase.getInstance();
    private ArrayList<Message> messageList;
    private MessageAdapter adapter;
    private Context mconContext;
    private UserInfo hisInfo;
    private String cvsId;

    private static ChattingFragment instance = null;

    public static ChattingFragment newInstance(Context mconContext, UserInfo hisInfo, String cvsId) {

        if (instance == null) {
            instance = new ChattingFragment(mconContext, hisInfo, cvsId);
            return instance;
        } else
            return null;
    }

    @Override
    public int getPresentTransition() {
        return PresentStyle.NONE;
    }

    @Override
    public void onDestroy() {
        instance = null;
        super.onDestroy();
    }

    public ChattingFragment(Context mconContext, UserInfo hisInfo, String cvsId) {
        this.mconContext = mconContext;
        this.hisInfo = hisInfo;
        this.cvsId = cvsId;
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

        messageList = new ArrayList<>();
        adapter = new MessageAdapter(mconContext, R.layout.left_message_item, messageList);
        listView.setAdapter(adapter);

        btnSendMess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMsg();
            }
        });

        btnSendMess.setVisibility(View.GONE);

        editTextMsg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!editTextMsg.getText().toString().trim().equals(""))
                    btnSendMess.setVisibility(View.VISIBLE);
                else
                    btnSendMess.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (cvsId == null) {
            String tempCvsId = firebase.makeCvsId(firebase.getUid(), hisInfo.getId());
            if (firebase.getmCurrentUser() != null &&
                    firebase.getmCurrentUser().getConversationsId() != null
                    && firebase.getmCurrentUser().getConversationsId().contains(tempCvsId)) {
                cvsId = tempCvsId;
                getMessages();
            }
        }

        setupToolbar();
    }

    private void setupToolbar() {
        getMainActivity().setSupportActionBar(chatToolbar);

        chatToolbar.setTitle(hisInfo.getUserName());
        setHasOptionsMenu(true);
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

    public void getMessages() {
        Log.d("1234567", "getMessages: ");
        firebase.getCvsDocWithId(cvsId)
                .collection(Firebase.MESSAGES_FOLDER)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("", "Listen failed.", e);
                            return;
                        }

                        List<Message> data = queryDocumentSnapshots.toObjects(Message.class);

                        messageList.clear();
                        messageList.addAll(data);

                        Collections.sort(messageList, new Comparator<Message>() {
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
                });
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void dataIsChanged(ListMessageEvent data) {
        getMessages();
    }

    private void sendMsg() {
        String content = editTextMsg.getText().toString().trim();
        if (content.equals(""))
            return;

        //conversation doesn't exists
        if (cvsId == null) {
            cvsId = firebase.makeCvsId(firebase.getUid(), hisInfo.getId());

            createCvsWithAMsg(cvsId, firebase.getUid(), hisInfo.getId(), content);
        } else
            sendMsg(cvsId, firebase.getUid(), content);

        editTextMsg.setText("");
        editTextMsg.requestFocus();
    }

    public void sendMsg(String cvsId, String senderId, String content) {

        //make message ID
        String msgID = firebase.getMessageFolderDoc(cvsId).document().getId();

        Message m = new Message(msgID, senderId, "text", content, Calendar.getInstance().getTime().getTime());

        firebase.getMessageIdDocWithId(cvsId, msgID).set(m);

        firebase.getCvsDocWithId(cvsId)
                .update("lastMessage.sender", senderId,
                        "lastMessage.type", "text",
                        "lastMessage.id", msgID,
                        "lastMessage.content", content,
                        "lastMessage.timeStamp", m.getTimeStamp());
    }

    public void createCvsWithAMsg(String cvsId, String sender, String receiver, String content) {

        textInputMsg.setVisibility(View.GONE);

        ArrayList<String> usersId = new ArrayList<>();
        usersId.add(sender);
        usersId.add(receiver);
        Conversation c = new Conversation();
        c.setId(cvsId);
        c.setUserId(usersId);
        c.setLastMessage(new Message(cvsId, sender, "text", content
                , Calendar.getInstance().getTime().getTime()));

        firebase.getCvsDocWithId(cvsId).set(c)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        firebase.getUserIdFolderDbs(sender)
                                .update("conversationsId", FieldValue.arrayUnion(cvsId))
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        firebase.getUserIdFolderDbs(receiver)
                                                .update("conversationsId", FieldValue.arrayUnion(cvsId))
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        textInputMsg.setVisibility(View.VISIBLE);
                                                        sendMsg(cvsId, sender, content);
                                                        getMessages();
                                                    }
                                                });
                                    }
                                });

                    }
                });
        //Todo: Handler error not sendding message
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_call, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menuCall) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || checkAllPermission())
                makeVideoCall();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private String[] listPermission =
            {
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_PHONE_STATE
            };

    private boolean checkAllPermission() {
        List<String> listNeedPermission = new ArrayList<>();

        for (String p : listPermission)
            if (ContextCompat.checkSelfPermission(getContext(), p) != PackageManager.PERMISSION_GRANTED)
                listNeedPermission.add(p);

        if (listNeedPermission.isEmpty())
            return true;

        ActivityCompat.requestPermissions(getMainActivity(), listNeedPermission.toArray(new String[listNeedPermission.size()]), RC_CHECK_PERMISSION);
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RC_CHECK_PERMISSION:
                if (grantResults.length > 0) {
                    String per = "";
                    for (int i = 0; i < grantResults.length; i++)
                        if (grantResults[i] == PackageManager.PERMISSION_DENIED)
                            per += ('\n' + permissions[i]);
                    if (!per.equals(""))
                        Toast.makeText(getContext(), "Permission denied: " + per, Toast.LENGTH_SHORT).show();
                    else
                        makeVideoCall();
                }
        }
    }

    private void makeVideoCall() {
        Call call = getMainActivity().getSinchServiceInterface().callUserVideo(hisInfo.getId());
        Intent intent = new Intent(getActivity(), CallScreenActivity.class);
        String callID = call.getCallId();
        intent.putExtra(SinchService.CALL_ID, call.getCallId());
        startActivity(intent);
    }
}
