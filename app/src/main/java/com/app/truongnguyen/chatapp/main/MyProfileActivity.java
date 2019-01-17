package com.app.truongnguyen.chatapp.main;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.app.truongnguyen.chatapp.EventClass.DataIsChanged;
import com.app.truongnguyen.chatapp.R;
import com.app.truongnguyen.chatapp.data.Firebase;
import com.app.truongnguyen.chatapp.widget.BitmapCustom;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private final int PICK_IMAGE_REQUEST = 71;

    @BindView(R.id.edi_email)
    TextInputLayout ediEmail;
    @BindView(R.id.txt_email)
    TextInputEditText txtEmail;

    @BindView(R.id.txt_birthday)
    TextView txtBirthday;

    @BindView(R.id.rad_group_gender)
    RadioGroup radGroupGender;
    @BindView(R.id.rad_male)
    RadioButton radMale;
    @BindView(R.id.rad_female)
    RadioButton radFemale;
    @BindView(R.id.rad_other)
    RadioButton radOther;

    //Contact
    @BindView(R.id.edi_phonenumber)
    TextInputLayout ediPhoneNumber;
    @BindView(R.id.txt_phonenumber)
    TextInputEditText txtPhoneNumber;

    @BindView(R.id.edi_address)
    TextInputLayout ediAddress;
    @BindView(R.id.txt_address)
    TextInputEditText txtAddress;

    //Button
    @BindView(R.id.btn_save)
    Button btnSave;

    @BindView(R.id.avatar)
    RoundedImageView avatarImageView;
    @BindView(R.id.btn_change_avatar)
    ImageView btnChangeAvatar;
    @BindView(R.id.tv_user_name)
    TextView tvName;

    private Uri filePath;
    private Firebase firebase = Firebase.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        ButterKnife.bind(this);

        btnChangeAvatar.setOnClickListener(this);
        avatarImageView.setOnClickListener(this);

        tvName.setText(firebase.getCurrentUser().getUserName());

        String avatarUrl = firebase.getmCurrentUser().getAvatarUrl();
        if (avatarUrl != null)
            Glide.with(this).load(avatarUrl).into(avatarImageView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.avatar:
                Intent intent = new Intent(MyProfileActivity.this, ViewImageActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("imageUrl", firebase.getCurrentUser().getAvatarUrl());
                intent.putExtras(bundle);

                startActivity(intent);
                break;
            case R.id.btn_change_avatar:
                chooseImage();
                break;
        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void uploadAvatarAndAvatarCurrent(Bitmap avatar, Bitmap avatarIcon) {
        String uId = firebase.getUid();
        String destPath_avatarIcon = uId + "/avatarIcon";
        String destPath_avatar = uId + "/avatar";
        String fieldAvatarIconUri = "avatarIconUri";
        String fieldAvatarUri = "avatarUri";
        String fieldAvatarIconUrl = "avatarIconUrl";
        String fieldAvatarUrl = "avatarUrl";

        if (avatar != null || avatarIcon != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            avatarIcon.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] dataAvatarIcon = baos.toByteArray();

            baos.reset();
            avatar.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] dataAvatar = baos.toByteArray();

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            StorageReference ref = FirebaseStorage.getInstance().getReference();

            //Start uploading
            ref.child(destPath_avatarIcon).putBytes(dataAvatarIcon)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            //update avatarUri
                            firebase.getCurrentUserFolderDbs()
                                    .update(fieldAvatarIconUri, destPath_avatarIcon);

                            //update avatar icon Url
                            ref.child(destPath_avatarIcon).getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            firebase.getCurrentUserFolderDbs()
                                                    .update(fieldAvatarIconUrl, uri.toString());
                                        }
                                    });


                            //setAvatar
                            avatarImageView.setImageBitmap(avatar);

                            //upload avatar
                            ref.child(destPath_avatar).putBytes(dataAvatar)
                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            //update avatar uri
                                            firebase.getCurrentUserFolderDbs()
                                                    .update(fieldAvatarUri, destPath_avatar);

                                            //update avatarc icon Url
                                            ref.child(destPath_avatar).getDownloadUrl()
                                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                        @Override
                                                        public void onSuccess(Uri uri) {
                                                            firebase.getCurrentUserFolderDbs()
                                                                    .update(fieldAvatarUrl, uri.toString());
                                                        }
                                                    });
                                            avatarImageView.setImageBitmap(avatar);
                                        }
                                    });

                            progressDialog.dismiss();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(MyProfileActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap avatar = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                if (avatar != null) {
                    Bitmap avatarIcon = BitmapCustom.myScaleAndCrop(avatar, 32, 32);
                    avatar = BitmapCustom.myScaleAndCrop(avatar, 512, 512);

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    avatar.compress(Bitmap.CompressFormat.JPEG, 90, stream);

                    stream.reset();
                    avatarIcon.compress(Bitmap.CompressFormat.JPEG, 90, stream);

                    uploadAvatarAndAvatarCurrent(avatar, avatarIcon);

                } else Toast.makeText(this, "Wooo...\n" +
                        "Error when loading this image!!", Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
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
    public void avatarWasChanged(DataIsChanged data) {

    }

}