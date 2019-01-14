package com.app.truongnguyen.chatapp.inout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.app.truongnguyen.chatapp.MainActivity;
import com.app.truongnguyen.chatapp.R;
import com.app.truongnguyen.chatapp.data.Firebase;
import com.app.truongnguyen.chatapp.data.MyPrefs;
import com.app.truongnguyen.chatapp.data.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignUp extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "LogUp";
    FirebaseAuth mAuth;
    DatabaseReference mDbs;
    MyPrefs myPrefs;


    @BindView(R.id.edi_name)
    TextInputLayout ediName;
    @BindView(R.id.edi_email)
    TextInputLayout ediEmail;
    @BindView(R.id.edi_birthday)
    TextInputLayout ediBirthday;
    @BindView(R.id.edi_password)
    TextInputLayout ediPassword;
    @BindView(R.id.edi_retype_password)
    TextInputLayout ediRetypePass;
    @BindView(R.id.chb_terms_privacy)
    CheckBox chbConfirm;
    @BindView(R.id.txt_terms_privacy)
    TextView txtConfirm;
    @BindView(R.id.link_to_signin)
    TextView mSignIn;
    @BindView(R.id.btn_signup)
    Button btnSignUp;

    private ProgressDialog progressDialog;

    public static SignUp newInstance() {
        return new SignUp();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        ButterKnife.bind(this);

        mSignIn.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);

        focusOnTermsAndPrivacy();

        mAuth = FirebaseAuth.getInstance();
        mDbs = FirebaseDatabase.getInstance().getReference();

        myPrefs = new MyPrefs(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Processing...");
        progressDialog.setCanceledOnTouchOutside(false);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_signup:
                signUp();
                break;
            case R.id.link_to_signin:
                alertUser();
                break;
        }
    }

    private void signUp() {

        String name = Objects.requireNonNull(ediName.getEditText()).getText().toString().trim();
        String email = Objects.requireNonNull(ediEmail.getEditText()).getText().toString().trim();
        String password = Objects.requireNonNull(ediPassword.getEditText()).getText().toString().trim();
        String retype = Objects.requireNonNull(ediRetypePass.getEditText()).getText().toString().trim();
        String birthday = Objects.requireNonNull(ediBirthday.getEditText().getText().toString().trim());

        if (validateInfo(name, email, password, retype)) {

            progressDialog.show();

            disableEditing();

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            enableEditing();

                            if (task.isSuccessful()) {
                                //save account
                                myPrefs.setIsSignIn(true);

                                addNewUser(name, email, birthday);

                            } else {
                                progressDialog.dismiss();

                                Toast.makeText(SignUp.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void addNewUser(String name, String email, String birthday) {
        User user = new User();
        FirebaseUser auth = mAuth.getCurrentUser();

        user.setUserName(name);
        user.setId(auth.getUid());
        user.setEmail(email);
        user.setBirthday(birthday);

        sendUserData(user);
    }

    private void sendUserData(User user) {
        FirebaseFirestore.getInstance().collection("users").document(user.getId())
                .set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.dismiss();

                Toast.makeText(SignUp.this, R.string.signup_success, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(SignUp.this, MainActivity.class);
                finish();
                startActivity(intent);
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        progressDialog.dismiss();

                        Toast.makeText(SignUp.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void disableEditing() {
        boolean b = false;
        btnSignUp.setEnabled(b);
        ediName.setEnabled(b);
        ediBirthday.setEnabled(b);
        ediEmail.setEnabled(b);
        ediPassword.setEnabled(b);
        ediRetypePass.setEnabled(b);
        mSignIn.setEnabled(b);
        chbConfirm.setEnabled(b);
    }

    private void enableEditing() {
        boolean b = true;
        btnSignUp.setEnabled(b);
        ediName.setEnabled(b);
        ediBirthday.setEnabled(b);
        ediEmail.setEnabled(b);
        ediPassword.setEnabled(b);
        ediRetypePass.setEnabled(b);
        mSignIn.setEnabled(b);
        chbConfirm.setEnabled(b);
    }

    private boolean validateInfo(String name, String email, String password, String retype) {
        Boolean validate = true;

        if (name.isEmpty()) {
            ediName.setError(getString(R.string.fullname_empty));
            validate = false;
        } else {
            ediName.setError(null);
        }

        if (email.isEmpty()) {
            ediEmail.setError(getString(R.string.email_empty));
            validate = false;
        } else if (!isValidEmail(email)) {
            ediEmail.setError(getString(R.string.email_invalid));
            validate = false;
        } else {
            ediEmail.setError(null);
        }

        if (password.isEmpty()) {
            ediPassword.setError(getString(R.string.password_empty));
            validate = false;
        } else if (password.length() < 6) {
            ediPassword.setError(getString(R.string.password_length));
            validate = false;
        } else {
            ediPassword.setError(null);
        }

        if (retype.isEmpty()) {
            ediRetypePass.setError(getString(R.string.retype_empty));
            validate = false;
        } else if (!retype.matches(password)) {
            ediRetypePass.setError(getString(R.string.retype_match));
            validate = false;
        } else {
            ediRetypePass.setError(null);
        }

        if (validate) {
            if (!chbConfirm.isChecked()) {
                Toast.makeText(SignUp.this, R.string.terms_confirm, Toast.LENGTH_LONG).show();
                validate = false;
            }
        }

        return validate;
    }

    public boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }
    }

    private void alertUser() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);

        dialog.setContentView(R.layout.alert_layout);
        dialog.findViewById(R.id.comfirm).setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(SignUp.this, SignIn.class);
            startActivity(intent);
            finish();
        });
        dialog.show();
    }


    private void focusOnTermsAndPrivacy() {
        SpannableString SpanString = new SpannableString(
                getString(R.string.terms_privacy));

        ClickableSpan terms = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Toast.makeText(SignUp.this, R.string.is_updating, Toast.LENGTH_SHORT).show();
            }
        };

        ClickableSpan privacy = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Toast.makeText(SignUp.this, R.string.is_updating, Toast.LENGTH_SHORT).show();
            }
        };

        SpanString.setSpan(terms, 41, 59, 0);
        SpanString.setSpan(privacy, 63, 81, 0);
        SpanString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.quite_red)), 41, 59, 0);
        SpanString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.quite_red)), 63, 81, 0);
        SpanString.setSpan(new UnderlineSpan(), 41, 59, 0);
        SpanString.setSpan(new UnderlineSpan(), 63, 81, 0);

        txtConfirm.setMovementMethod(LinkMovementMethod.getInstance());
        txtConfirm.setText(SpanString, TextView.BufferType.SPANNABLE);
        txtConfirm.setSelected(true);
    }


}
