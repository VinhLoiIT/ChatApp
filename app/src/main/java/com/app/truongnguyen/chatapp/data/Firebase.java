package com.app.truongnguyen.chatapp.data;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.app.truongnguyen.chatapp.EventClass.EmptyObjectEvent;
import com.app.truongnguyen.chatapp.EventClass.ListMessageEvent;
import com.app.truongnguyen.chatapp.EventClass.Signal;
import com.app.truongnguyen.chatapp.EventClass.UserInfoListEvent;
import com.app.truongnguyen.chatapp.widget.BitmapCustom;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.Nullable;

public class Firebase {
    private static Firebase instance = null;
    final public static long MAX_DONWLOAD_SIZE_BYTES = 10000000;
    private FirebaseFirestore mDbs = null;
    private FirebaseStorage storage;
    private User mUser;
    private String userId;
    private Uri filePath;
    private Bitmap avatar;
    static final public String LIST_FRIEND_FOLDER = "friendList";
    static final public String USERS_FOLDER = "users";

    private Firebase() {
        initData();
    }

    public static Firebase getInstance() {
        if (instance == null)
            instance = new Firebase();
        return instance;
    }

    public Bitmap getAvatar() {
        return avatar;
    }

    public void setAvatar(Bitmap avatar) {
        this.avatar = avatar;
        EventBus.getDefault().postSticky(new Signal());
    }

    public Bitmap getAvatar(int height, int width) {
        return BitmapCustom.scale(avatar, height, width);
    }

    public void signOut() {
        instance = null;
    }

    private void initData() {
        mDbs = FirebaseFirestore.getInstance();

        storage = FirebaseStorage.getInstance();

        userId = FirebaseAuth.getInstance().getUid();

        //getUserData
        getUserCurrentFolderDbs()
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot,
                                        @Nullable FirebaseFirestoreException e) {

                        if (snapshot != null && snapshot.exists()) {
                            mUser = snapshot.toObject(User.class);
                            if (mUser.getAvatarUri() != null)
                                downloadAvatar(mUser.getAvatarUri());
                        }
                    }
                });
    }

    public User getUserData() {
        return mUser;
    }

    public String getUid() {
        return userId;
    }

    public CollectionReference getUserFolderDbs() {
        return FirebaseFirestore.getInstance().collection(USERS_FOLDER);
    }

    public DocumentReference getUserCurrentFolderDbs() {
        return mDbs.collection(USERS_FOLDER).document(userId);
    }

    public void sendMess(String cvsId, String senderId, String content) {

        //make message ID
        String msgID = mDbs.collection("conversations").document(cvsId)
                .collection("messages").document().getId();

        Message m = new Message(senderId, msgID, "text", content, Calendar.getInstance().getTime().getTime());

        mDbs.collection("conversations").document(cvsId)
                .collection("messages").document(msgID).set(m);

        mDbs.collection("conversations").document(cvsId)
                .update("lastMessage.sender", senderId,
                        "lastMessage.type", "text",
                        "lastMessage.id", msgID,
                        "lastMessage.content", content,
                        "lastMessage.timeStamp", m.getTimeStamp());
    }

    public void createCvsWithAMsg(String cvsId, String u1, String u2, String content) {
        ArrayList<String> usersId = new ArrayList<>();
        usersId.add(u1);
        usersId.add(u2);
        Conversation c = new Conversation();
        c.setId(cvsId);
        c.setUserId(usersId);
        c.setLastMessage(new Message(cvsId, u1, u2, content, Calendar.getInstance().getTime().getTime()));

        mDbs.collection("conversations").document(cvsId).set(c)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        mDbs.collection("users").document(u1)
                                .update("conversationsId", FieldValue.arrayUnion(cvsId))
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        mDbs.collection("users").document(u2)
                                                .update("conversationsId", FieldValue.arrayUnion(cvsId))
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {

                                                        sendMess(cvsId, u1, content);

                                                        getMessages(cvsId);
                                                    }
                                                });
                                    }
                                });

                    }
                });
        //Todo: Handler error not sendding message
    }

    public String makeCvsId(String u1, String u2) {
        String cvsId;
        if (u1.compareTo(u2) == -1)
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

    public String getOtherIdFromCvsId(String cvsId) {
        return this.getUId2FromCvsId(cvsId, this.userId);
    }

    public void getConversation() {
        getUserCurrentFolderDbs()
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot,
                                        @Nullable FirebaseFirestoreException e) {

                        //get ConversationId
                        if (snapshot != null && snapshot.exists()) {
                            mUser = snapshot.toObject(User.class);

                            if (mUser.getConversationsId() == null || mUser.getConversationsId().isEmpty()) {

                                EventBus.getDefault().postSticky(new EmptyObjectEvent());

                            } else {
                                ArrayList<String> arrayCvsId = new ArrayList<>();
                                arrayCvsId.addAll(mUser.getConversationsId());

                                //Get Conversation
                                for (String cvsId : arrayCvsId) {
                                    mDbs.collection("conversations").document(cvsId)
                                            .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                                @Override
                                                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                                    if (documentSnapshot.exists()) {
                                                        Conversation c = documentSnapshot.toObject(Conversation.class);

                                                        String otherId = getOtherIdFromCvsId(c.getId());

                                                        //Get user name and icon (his avatar)
                                                        mDbs.collection("users").document(otherId).get()
                                                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                    @Override
                                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                        if (documentSnapshot.exists()) {

                                                                            UserInfo u = documentSnapshot.toObject(UserInfo.class);
                                                                            c.setConversationName(u.getUserName());
                                                                            EventBus.getDefault().postSticky(c);

                                                                            //Download avatar
                                                                            if (u.getAvatarUri() != null) {
                                                                                storage.getReference().child(u.getAvatarUri()).getBytes(MAX_DONWLOAD_SIZE_BYTES)
                                                                                        .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                                                                            @Override
                                                                                            public void onSuccess(byte[] bytes) {
                                                                                                ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(bytes);
                                                                                                Bitmap b = BitmapFactory.decodeStream(arrayInputStream);

                                                                                                c.setIcon(b);

                                                                                                EventBus.getDefault().postSticky(c);
                                                                                            }
                                                                                        });
                                                                            }
                                                                        }
                                                                    }
                                                                });
                                                    }
                                                }
                                            });
                                }
                            }
                        }
                    }
                });
    }

    public void getMessages(String cvsId) {
        mDbs.collection("conversations").document(cvsId)
                .collection("messages")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("", "Listen failed.", e);
                            return;
                        }

                        List<Message> data = queryDocumentSnapshots.toObjects(Message.class);

                        ListMessageEvent listMessageEvent = new ListMessageEvent(data);

                        EventBus.getDefault().postSticky(listMessageEvent);
                    }
                });
    }

    public void uploadImage(String destPath, Uri filePath, Context context) {

        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Uploading...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            StorageReference ref = FirebaseStorage.getInstance().getReference()
                    .child(destPath);
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    mDbs.collection("users").document(userId)
                                            .update("avatarUri", userId + "/avatar")
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(context, "Uploaded", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressDialog.dismiss();
                                            Toast.makeText(context, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(context, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }
    }

    public void uploadAvatar(Bitmap bitmap, Context context) {
        String destPath = userId + "/avatar" + ".jpeg";
        if (bitmap != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            final ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Uploading...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            StorageReference ref = FirebaseStorage.getInstance().getReference()
                    .child(destPath);

            ref.putBytes(data)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    mDbs.collection("users").document(userId)
                                            .update("avatarUri", destPath)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(context, "Uploaded", Toast.LENGTH_SHORT).show();
                                                    setAvatar(bitmap);
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressDialog.dismiss();
                                            ref.delete();
                                            Toast.makeText(context, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(context, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }
    }

    public void downloadAvatar(String path) {

        storage.getReference().child(path).getBytes(MAX_DONWLOAD_SIZE_BYTES)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(bytes);
                        setAvatar(BitmapFactory.decodeStream(arrayInputStream));
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("1234567", "onFailure: ");
            }
        });
    }

    public void getFriend() {
        getUserCurrentFolderDbs()
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("", "Listen failed.", e);
                            EventBus.getDefault().postSticky(new EmptyObjectEvent());
                            return;
                        }

                        mUser = value.toObject(User.class);
                        ArrayList<String> data = mUser.getFriendList();
                        ArrayList<UserInfo> infoArrayList = new ArrayList<>();

                        if (data != null && !data.isEmpty()) {
                            for (String id : data)
                                mDbs.collection(USERS_FOLDER).document(id)
                                        .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                            @Override
                                            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                                if (e != null) {
                                                    Log.w("", "Listen failed.", e);
                                                    EventBus.getDefault().postSticky(new EmptyObjectEvent());
                                                    return;
                                                }

                                                UserInfo u = documentSnapshot.toObject(UserInfo.class);
                                                infoArrayList.add(u);
                                                EventBus.getDefault().postSticky(new UserInfoListEvent(infoArrayList));

                                                //Download avatar
                                                if (u.getAvatarUri() != null) {
                                                    storage.getReference().child(u.getAvatarUri()).getBytes(MAX_DONWLOAD_SIZE_BYTES)
                                                            .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                                                @Override
                                                                public void onSuccess(byte[] bytes) {
                                                                    ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(bytes);
                                                                    Bitmap b = BitmapFactory.decodeStream(arrayInputStream);

                                                                    u.setAvatarBitmap(b);

                                                                    EventBus.getDefault().postSticky(new UserInfoListEvent(infoArrayList));
                                                                }
                                                            });
                                                }
                                            }
                                        });

                        } else {
                            EventBus.getDefault().postSticky(new EmptyObjectEvent());
                        }
                    }
                });
    }


    public boolean isFriend(String hisId) {
        if (mUser.getFriendList() == null || mUser.getFriendList().isEmpty())
            return false;
        if (mUser.getFriendList().contains(hisId))
            return true;
        return false;
    }
}
