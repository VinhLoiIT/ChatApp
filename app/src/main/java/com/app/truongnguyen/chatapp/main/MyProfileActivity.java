package com.app.truongnguyen.chatapp.main;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

import com.app.truongnguyen.chatapp.EventClass.Signal;
import com.app.truongnguyen.chatapp.R;
import com.app.truongnguyen.chatapp.data.Firebase;
import com.app.truongnguyen.chatapp.widget.BitmapCustom;
import com.makeramen.roundedimageview.RoundedImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private final int PICK_IMAGE_REQUEST = 71;
    @BindView(R.id.avatar)
    RoundedImageView mAvatar;

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
    RoundedImageView avatar;
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
        avatar.setOnClickListener(this);
        tvName.setText(firebase.getUserData().getUserName());
        //firebase.setAvatarFor(avatar);
        avatar.setImageBitmap(firebase.getAvatar());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.avatar:
                Intent intent = new Intent(MyProfileActivity.this, ViewImageActivity.class);
                Bundle bundle = new Bundle();

                Bitmap bitmap = firebase.getAvatar();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] imageInByte = stream.toByteArray();
                ByteArrayInputStream bis = new ByteArrayInputStream(imageInByte);

                bundle.putByteArray("image", imageInByte);

                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.btn_change_avatar:
                chooseImage();
                break;
        }
    }

    public void setAvatar() {
        Bitmap b = firebase.getAvatar();
        if (b != null) {
            avatar.setImageBitmap(b);
        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void uploadAvatar(Bitmap bitmap) {
        firebase.uploadAvatar(bitmap, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap image = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //image = ImageUtils.getInstant().getCompressedBitmap(filePath.toString());
                //Bitmap bitmap = BitmapCustom.myScaleAndCrop(image, 128, 128);
                if (image != null) {
                    image = BitmapCustom.myScaleAndCrop(image, 256, 256);

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 90, stream);

                    uploadAvatar(image);
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
    public void avatarWasChanged(Signal data) {
        setAvatar();
    }

}