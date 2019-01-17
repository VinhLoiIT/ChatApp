package com.app.truongnguyen.chatapp.data;

import com.app.truongnguyen.chatapp.EventClass.DataIsChanged;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;

import org.greenrobot.eventbus.EventBus;

import java.util.logging.Handler;

import javax.annotation.Nullable;

public class Firebase {
    private static Firebase instance = null;
    final public static long MAX_DONWLOAD_SIZE_BYTES = 10000000;
    static final public String CONVERSATIONS_FOLDER = "conversations";
    static final public String MESSAGES_FOLDER = "messages";
    static final public String LIST_FRIEND_FOLDER = "friendList";
    static final public String USERS_FOLDER = "users";
    private FirebaseStorage storage;
    private FirebaseFirestore mDbs;
    private User mCurrentUser;
    private String userId;

    public static Firebase getInstance() {
        if (instance == null)

            instance = new Firebase();
        return instance;
    }

    public FirebaseFirestore getmDbs() {
        return mDbs;
    }

    public User getmCurrentUser() {
        return mCurrentUser;
    }

    private Firebase() {
        initData();
    }

    public void setmCurrentUser(User mCurrentUser) {
        this.mCurrentUser = mCurrentUser;
    }

    public DocumentReference getUserIdFolderDbs(String id) {
        return mDbs.collection(USERS_FOLDER).document(id);
    }

    public void signOut() {
        instance = null;
    }

    public void initData() {
        mDbs = FirebaseFirestore.getInstance();

        storage = FirebaseStorage.getInstance();

        userId = FirebaseAuth.getInstance().getUid();

        //getCurrentUser
        getCurrentUserFolderDbs()
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot,
                                        @Nullable FirebaseFirestoreException e) {
                        if (snapshot == null && !snapshot.exists())
                            return;

                        User u = snapshot.toObject(User.class);

                        if (mCurrentUser == null || mCurrentUser != u) {
                            boolean avatarChanged = false;
                            if (mCurrentUser == null)
                                avatarChanged = true;
                            else if (mCurrentUser.getAvatarIconUrl() != u.getAvatarUrl())
                                avatarChanged = true;
                            mCurrentUser = u;

                            EventBus.getDefault().postSticky(
                                    new DataIsChanged(mCurrentUser.getFriendList(),
                                            mCurrentUser.getConversationsId(),
                                            avatarChanged));
                        }
                    }
                });
    }

    public User getCurrentUser() {
        return mCurrentUser;
    }

    public String getUid() {
        return userId;
    }

    public CollectionReference getUserFolderDbs() {
        return FirebaseFirestore.getInstance().collection(USERS_FOLDER);
    }

    public DocumentReference getCurrentUserFolderDbs() {
        return mDbs.collection(USERS_FOLDER).document(userId);
    }

    public String makeCvsId(String u1, String u2) {
        String cvsId;
        if (u1.compareTo(u2) < 0)
            cvsId = u1 + u2;
        else
            cvsId = u2 + u1;
        return cvsId;
    }

    public String getUId2FromCvsId(String cvsId, String u1) {
        String u2;
        int lenUId = cvsId.length() / 2;
        if (cvsId.substring(0, lenUId).equals(u1))
            u2 = cvsId.substring(lenUId);
        else
            u2 = cvsId.substring(0, lenUId);
        return u2;
    }

    public CollectionReference getMessageFolderDoc(String cvsId) {
        return getCvsDocWithId(cvsId).collection("messages");
    }

    public DocumentReference getMessageIdDocWithId(String cvsId, String msgId) {
        return getMessageFolderDoc(cvsId).document(msgId);
    }


    public DocumentReference getCvsDocWithId(String id) {
        return mDbs.collection(CONVERSATIONS_FOLDER).document(id);
    }

    public String getOtherIdFromCvsId(String cvsId) {
        return this.getUId2FromCvsId(cvsId, this.userId);
    }


    public boolean isFriend(String hisId) {
        if (mCurrentUser.getFriendList() == null || mCurrentUser.getFriendList().isEmpty())
            return false;
        if (mCurrentUser.getFriendList().contains(hisId))
            return true;
        return false;
    }
}
