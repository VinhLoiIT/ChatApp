package com.app.truongnguyen.chatapp.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.truongnguyen.chatapp.EventClass.Signal;
import com.app.truongnguyen.chatapp.PagerAdapter;
import com.app.truongnguyen.chatapp.R;
import com.app.truongnguyen.chatapp.data.Firebase;
import com.app.truongnguyen.chatapp.data.UserInfo;
import com.app.truongnguyen.chatapp.fragmentnavigationcontroller.FragmentNavigationController;
import com.app.truongnguyen.chatapp.fragmentnavigationcontroller.PresentStyle;
import com.app.truongnguyen.chatapp.fragmentnavigationcontroller.SupportFragment;
import com.app.truongnguyen.chatapp.inout.SignIn;
import com.app.truongnguyen.chatapp.widget.BitmapCustom;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.makeramen.roundedimageview.RoundedImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsFragment extends SupportFragment implements View.OnClickListener {
    @BindView(R.id.update_profile)
    LinearLayout updateProfile;
    @BindView(R.id.change_password)
    LinearLayout changePassword;
    @BindView(R.id.login_history)
    LinearLayout loginHistory;
    @BindView(R.id.about_us)
    LinearLayout aboutUs;
    @BindView(R.id.logout)
    LinearLayout logout;


    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Nullable
    @Override
    protected View onCreateView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        updateProfile.setOnClickListener(this);
        changePassword.setOnClickListener(this);
        aboutUs.setOnClickListener(this);
        loginHistory.setOnClickListener(this);
        logout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                Firebase.getInstance().signOut();
                Intent intent = new Intent(getMainActivity(), SignIn.class);
                getMainActivity().startActivity(intent);
                getMainActivity().finish();
                break;
        }
    }

    public static class MyProfileActivity extends AppCompatActivity implements View.OnClickListener {
        private final int PICK_IMAGE_REQUEST = 71;
        @BindView(R.id.avatar)
        RoundedImageView avatar;
        @BindView(R.id.tv_user_name)
        TextView tvName;

        private Uri filePath;
        private Firebase firebase = Firebase.getInstance();

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_my_profile);
            ButterKnife.bind(this);

            avatar.setOnClickListener(this);
            tvName.setText(firebase.getUserData().getUserName());
            //firebase.setAvatarFor(avatar);
            avatar.setImageBitmap(firebase.getAvatar());
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.avatar:
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

    public static class MainActivity extends AppCompatActivity {

        private ViewPager pager;
        private TabLayout tabLayout;
        private FragmentNavigationController mNavigationController;
        private Firebase firebase = Firebase.getInstance();

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            initBackStack(savedInstanceState);

            addControl();
        }

        private void addControl() {
            pager = (ViewPager) findViewById(R.id.view_pager);
            tabLayout = (TabLayout) findViewById(R.id.tab_layout);

            FragmentManager manager = getSupportFragmentManager();
            PagerAdapter adapter = new PagerAdapter(this, manager);
            pager.setAdapter(adapter);
            tabLayout.setupWithViewPager(pager);
            pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
            tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(pager));

            tabLayout.getTabAt(0).setIcon(R.drawable.ic_chat_black_24dp);
            tabLayout.getTabAt(1).setIcon(R.drawable.ic_contact_black_24dp);
            tabLayout.getTabAt(2).setIcon(R.drawable.ic_settings_black_24dp);
        }

        private void initBackStack(Bundle savedInstanceState) {
            FragmentManager fm = getSupportFragmentManager();
            mNavigationController = FragmentNavigationController.navigationController(fm, R.id.container);
            mNavigationController.setPresentStyle(PresentStyle.PRESENT_STYLE_DEFAULT);
            mNavigationController.setDuration(250);
            mNavigationController.setInterpolator(new AccelerateDecelerateInterpolator());
            presentFragment(MainFragment.newInstance());
        }

        @Override
        public void onBackPressed() {
            if (mNavigationController.getTopFragment().isReadyToDismiss())
                if (!(isNavigationControllerInit() && mNavigationController.dismissFragment(true)))
                    super.onBackPressed();
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case android.R.id.home:
                    dismiss();
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }

        private boolean isNavigationControllerInit() {
            return null != mNavigationController;
        }

        public void presentFragment(SupportFragment fragment) {
            if (isNavigationControllerInit()) {
                mNavigationController.setPresentStyle(fragment.getPresentTransition());
                mNavigationController.presentFragment(fragment, true);
            }
        }

        public void dismiss() {
            if (isNavigationControllerInit()) {
                mNavigationController.dismissFragment();
            }
        }

        public void presentFragment(SupportFragment fragment, boolean animated) {
            if (isNavigationControllerInit()) {
                mNavigationController.presentFragment(fragment, animated);
            }
        }

        public void dismiss(boolean animated) {
            if (isNavigationControllerInit()) {
                mNavigationController.dismissFragment(animated);
            }
        }

        public void restartHomeScreen() {
            Intent intent = new Intent(this, MainActivity.class);
            finish();
            startActivity(intent);
            addControl();

        }

        public void goHomeScreen() {
            dismiss();
            restartHomeScreen();
        }
    }

    public static class SearchFragment extends SupportFragment {
        @BindView(R.id.input_search)
        EditText input;
        @BindView(R.id.btn_back)
        ImageButton back;
        @BindView(R.id.list_people_result)
        RecyclerView recyclerView;

        private Context mcContext;
        private ListPeopleAdapter adapter;
        private ArrayList<UserInfo> resultList;
        private ArrayList<UserInfo> peopleList;
        private Firebase firebase = Firebase.getInstance();

        public static SearchFragment newInstance() {
            return new SearchFragment();
        }

        @Nullable
        @Override
        protected View onCreateView(LayoutInflater inflater, ViewGroup container) {
            return inflater.inflate(R.layout.fragment_search, container, false);
        }


        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            ButterKnife.bind(this, view);

            mcContext = getMainActivity();

            peopleList = new ArrayList<>();
            resultList = new ArrayList<>();
            adapter = new ListPeopleAdapter(mcContext, resultList);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(mcContext));

            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getMainActivity().dismiss();
                }
            });

            firebase.getUserFolderDbs().get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot ds) {
                    if (ds != null && !ds.isEmpty()) {

                        List<UserInfo> userInfos = ds.toObjects(UserInfo.class);
                        for (UserInfo u : userInfos)
                            if (u.getAvatarUri() != null)

                                //Download avatar
                                if (u.getAvatarUri() != null) {
                                    FirebaseStorage.getInstance().getReference().child(u.getAvatarUri()).getBytes(Firebase.MAX_DONWLOAD_SIZE_BYTES)
                                            .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                                @Override
                                                public void onSuccess(byte[] bytes) {
                                                    ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(bytes);
                                                    Bitmap b = BitmapFactory.decodeStream(arrayInputStream);

                                                    int index = resultList.indexOf(u);
                                                    u.setAvatarBitmap(b);
                                                    adapter.notifyItemChanged(index);
                                                }
                                            });
                                }
                        resultList.clear();
                        resultList.addAll(userInfos);
                        peopleList.clear();
                        peopleList.addAll(userInfos);
                        adapter.notifyDataSetChanged();
                    }
                }
            });


            input.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String key = input.getText().toString().toLowerCase();
                    resultList.clear();
                    for (UserInfo u : peopleList)
                                    if (u.getEmail().toLowerCase().contains(key)
                                            || u.getUserName().toLowerCase().contains(key))
                                        resultList.add(u);
                                adapter.notifyDataSetChanged();
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
    }

    public static class ListPeopleAdapter extends RecyclerView.Adapter<ListPeopleAdapter.ViewHolder> {
        private ArrayList<UserInfo> mData;
        private Context context;

        public ListPeopleAdapter(Context context, ArrayList<UserInfo> userInfoArrayList) {
            this.mData = userInfoArrayList;
            this.context = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View inflater = LayoutInflater.from(context)
                    .inflate(R.layout.list_people_item, parent, false);
            return new ViewHolder(inflater);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.onBind(mData.get(position));
        }

        public void setFriendArrayList(ArrayList<UserInfo> friendArrayList) {
            this.mData = friendArrayList;
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView name;
            public TextView email;
            public RoundedImageView avatar;

            public String id;
            public String hisName;
            public Bitmap avatarBitmap = null;

            public ViewHolder(View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.friend_name);
                email = itemView.findViewById(R.id.friend_email);
                avatar = itemView.findViewById(R.id.avatar);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!Firebase.getInstance().getUid().equals(id)) {
                            ViewProfileFragment viewProfileFragment = ViewProfileFragment.newInstance();

                            Bundle bundle = new Bundle();
                            bundle.putString("id", id);
                            bundle.putString("hisName", hisName);

                            if (avatarBitmap != null) {
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                avatarBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                                byte[] byteArray = stream.toByteArray();

                                bundle.putByteArray("hisAvatarBitmap", byteArray);
                            }

                            viewProfileFragment.setArguments(bundle);
                            ((MainActivity) context).presentFragment(viewProfileFragment);
                        } else {
                            Intent intent = new Intent(context, MyProfileActivity.class);
                            context.startActivity(intent);
                        }
                    }
                });
            }

            public void onBind(UserInfo u) {
                this.name.setText(u.getUserName());
                this.email.setText(u.getEmail());
                if (u.getAvatarUri() != null) {
                    this.avatarBitmap = u.getAvatarBitmap();
                    this.avatar.setImageBitmap(avatarBitmap);
                }

                this.id = u.getId();
                this.hisName = u.getUserName();
            }
        }
    }
}

