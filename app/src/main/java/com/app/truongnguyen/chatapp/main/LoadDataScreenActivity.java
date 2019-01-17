package com.app.truongnguyen.chatapp.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.app.truongnguyen.chatapp.R;
import com.app.truongnguyen.chatapp.data.Firebase;
import com.app.truongnguyen.chatapp.data.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoadDataScreenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_data_screen);

        Firebase firebase = Firebase.getInstance();
        FirebaseFirestore.getInstance().collection(Firebase.USERS_FOLDER)
                .document(FirebaseAuth.getInstance().getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot doc) {
                        if (doc == null || !doc.exists())
                            return;
                        User u = doc.toObject(User.class);

                        firebase.setmCurrentUser(u);

                        Intent intent = new Intent(LoadDataScreenActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                });
    }
}
