package com.app.truongnguyen.chatapp.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.app.truongnguyen.chatapp.R;
import com.app.truongnguyen.chatapp.fragmentnavigationcontroller.SupportFragment;
import com.app.truongnguyen.chatapp.widget.OnOneClickListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChangePasswordFragment extends SupportFragment {
    @BindView(R.id.current_password_edittextinput)
    TextInputEditText currentPass;

    @BindView(R.id.new_password_edittextinput)
    TextInputEditText newPass;

    @BindView(R.id.retype_new_password_edittextinput)
    TextInputEditText retypeNewPass;

    @BindView(R.id.btn_change)
    Button btnChangePass;

    @BindView(R.id.progressbar)
    ProgressBar progressBar;

    @BindView(R.id.btn_back)
    ImageView back;

    String currentPassword;
    String newPassword;
    String retype;


    private static ChangePasswordFragment instance = null;

    public static ChangePasswordFragment newInstance() {

        if (instance == null) {
            instance = new ChangePasswordFragment();
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
        return inflater.inflate(R.layout.fragment_change_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        back.setOnClickListener(new OnOneClickListener() {
            @Override
            public void onOneClick(View v) {
                getMainActivity().dismiss();
            }
        });

        btnChangePass.setOnClickListener(new OnOneClickListener() {
            @Override
            public void onOneClick(View v) {
                if (FirebaseAuth.getInstance() == null || FirebaseAuth.getInstance().getCurrentUser() == null)
                    return;

                currentPassword = currentPass.getText().toString().trim();
                newPassword = newPass.getText().toString().trim();
                retype = retypeNewPass.getText().toString().trim();

                if (!validate()) return;

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                progressBar.setVisibility(View.VISIBLE);
                btnChangePass.setVisibility(View.GONE);

                disableEditing();

                FirebaseAuth.getInstance().signInWithEmailAndPassword(user.getEmail(), currentPassword)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                user.updatePassword(newPassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        enableEditing();
                                        progressBar.setVisibility(View.GONE);
                                        btnChangePass.setVisibility(View.VISIBLE);
                                        Toast.makeText(getActivity(), "Successfull", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        enableEditing();
                                        progressBar.setVisibility(View.GONE);
                                        btnChangePass.setVisibility(View.VISIBLE);
                                        Toast.makeText(getActivity(), "Something went wrong!!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        enableEditing();
                        Toast.makeText(getActivity(), "Wrong password", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        btnChangePass.setVisibility(View.VISIBLE);
                        currentPass.setError(null);
                    }
                });
            }
        });
    }

    public boolean validate() {
        boolean validate = true;
        if (currentPassword.isEmpty()) {
            currentPass.setError(getString(R.string.password_empty));
            validate = false;
        } else if (currentPassword.length() < 6) {
            currentPass.setError(getString(R.string.password_length));
            validate = false;
        } else {
            currentPass.setError(null);
        }

        if (newPassword.isEmpty()) {
            newPass.setError(getString(R.string.password_empty));
            validate = false;
        } else if (newPassword.length() < 6) {
            newPass.setError(getString(R.string.password_length));
            validate = false;
        } else {
            newPass.setError(null);
        }

        if (retype.isEmpty()) {
            retypeNewPass.setError(getString(R.string.retype_empty));
            validate = false;
        } else if (!retype.matches(newPassword)) {
            retypeNewPass.setError(getString(R.string.retype_match));
            validate = false;
        } else {
            retypeNewPass.setError(null);
        }
        return validate;
    }

    private void disableEditing() {
        boolean b = false;
        currentPass.setEnabled(b);
        newPass.setEnabled(b);
        retypeNewPass.setEnabled(b);
    }

    private void enableEditing() {
        boolean b = true;
        currentPass.setEnabled(b);
        newPass.setEnabled(b);
        retypeNewPass.setEnabled(b);
    }
}
