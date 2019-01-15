package com.app.truongnguyen.chatapp.main;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.app.truongnguyen.chatapp.R;
import com.app.truongnguyen.chatapp.widget.TouchImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewImageActivity extends AppCompatActivity {
    @BindView(R.id.btn_back)
    ImageView btnBack;
    @BindView(R.id.image)
    ImageView imageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null) {

            byte[] byteArray = bundle.getByteArray("image");
            if (byteArray != null)
                imageView.setImageBitmap(BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length));
        }
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
