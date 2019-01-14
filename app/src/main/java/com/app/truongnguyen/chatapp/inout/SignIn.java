package com.app.truongnguyen.chatapp.inout;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.truongnguyen.chatapp.MainActivity;
import com.app.truongnguyen.chatapp.R;
import com.app.truongnguyen.chatapp.data.Firebase;
import com.app.truongnguyen.chatapp.data.MyPrefs;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignIn extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.link_to_signup)
    TextView mSignUp;
    @BindView(R.id.edi_email)
    TextInputLayout ediEmail;
    @BindView(R.id.input_email)
    TextInputEditText txtEmail;
    @BindView(R.id.edi_password)
    TextInputLayout ediPassword;
    @BindView(R.id.input_password)
    TextInputEditText txtPassword;
    @BindView(R.id.chb_remember)
    CheckBox chbRemember;
    @BindView(R.id.forgot_password)
    View mForgot;
    @BindView(R.id.signin_progressBar)
    ProgressBar progressBar;

    private MyPrefs myPrefs;
    private FirebaseAuth auth;


    public static SignIn newInstance() {
        return new SignIn();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (FirebaseAuth.getInstance() != null && FirebaseAuth.getInstance().getCurrentUser() != null)
            signInSuccessful();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);

        ButterKnife.bind(this, this);


        myPrefs = new MyPrefs(this);

        auth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(this);
        mForgot.setOnClickListener(this);
        mSignUp.setOnClickListener(this);

        if (myPrefs.getIsRememberMe()) {
            String[] account = myPrefs.getAccount();
            txtEmail.setText(account[0]);
            txtPassword.setText(account[1]);
            chbRemember.setChecked(true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                login();
                break;
            case R.id.forgot_password:
                break;
            case R.id.link_to_signup:
                Intent intent = new Intent(SignIn.this, SignUp.class);
                finish();
                startActivity(intent);
                break;
        }
    }

    private void login() {
        String email = Objects.requireNonNull(ediEmail.getEditText()).getText().toString().trim();
        String password = Objects.requireNonNull(ediPassword.getEditText()).getText().toString().trim();

        String[] account = {email, password};
        myPrefs.setAccount(account);

        if (chbRemember.isChecked()) {
            myPrefs.setIsRememberMe(true);
        } else {
            myPrefs.setIsRememberMe(false);
        }

        if (validateAccount(email, password)) {

            disableEditing();
            progressBar.setVisibility(View.VISIBLE);

            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            enableEditing();

                            progressBar.setVisibility(View.GONE);

                            if (task.isSuccessful()) {
                                signInSuccessful();
                            } else
                                Toast.makeText(SignIn.this, R.string.failed_login, Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void disableEditing() {
        boolean b = false;
        btnLogin.setEnabled(b);
        ediEmail.setEnabled(b);
        ediPassword.setEnabled(b);
        chbRemember.setEnabled(b);
        mSignUp.setEnabled(b);
    }

    private void enableEditing() {
        boolean b = true;
        btnLogin.setEnabled(b);
        ediEmail.setEnabled(b);
        ediPassword.setEnabled(b);
        chbRemember.setEnabled(b);
        mSignUp.setEnabled(b);
    }

    private boolean validateAccount(String email, String password) {
        Boolean validate = true;

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
            ediEmail.setError(null);
            ediPassword.setError(getString(R.string.password_empty));
            validate = false;
        } else if (password.length() < 6) {
            ediEmail.setError(null);
            ediPassword.setError(getString(R.string.password_length));
            validate = false;
        } else {
            ediPassword.setError(null);
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

    private void signInSuccessful() {
        Intent intent = new Intent(SignIn.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
