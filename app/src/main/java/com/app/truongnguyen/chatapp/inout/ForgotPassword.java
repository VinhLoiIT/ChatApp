package com.app.truongnguyen.chatapp.inout;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.app.truongnguyen.chatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ForgotPassword extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ForgotPassword";
    FirebaseAuth mAuth;
    @BindView(R.id.edi_email)
    TextInputLayout ediEmail;
    @BindView(R.id.btn_send)
    Button btnSend;
    @BindView(R.id.btn_back)
    ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        ButterKnife.bind(this);
        btnSend.setOnClickListener(this);
        btnBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_send:
                sendPasswordResetEmail();
                break;
            case R.id.btn_back:
                finish();
                break;
        }
    }

    private void sendPasswordResetEmail() {
        String email = Objects.requireNonNull(ediEmail.getEditText()).getText().toString().trim();

        if (email.isEmpty()) {
            ediEmail.setError(getString(R.string.email_empty));
        } else {
            ediEmail.setError(null);
            mAuth = FirebaseAuth.getInstance();
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Email sent.");
                                Toast.makeText(ForgotPassword.this, R.string.mail_sent, Toast.LENGTH_LONG).show();
                                finish();
                            } else {
                                Toast.makeText(ForgotPassword.this, R.string.mail_fail, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }
}
