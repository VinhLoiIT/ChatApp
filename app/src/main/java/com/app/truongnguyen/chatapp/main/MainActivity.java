package com.app.truongnguyen.chatapp.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.app.truongnguyen.chatapp.PagerAdapter;
import com.app.truongnguyen.chatapp.R;
import com.app.truongnguyen.chatapp.call.BaseActivity;
import com.app.truongnguyen.chatapp.data.Firebase;
import com.app.truongnguyen.chatapp.fragmentnavigationcontroller.FragmentNavigationController;
import com.app.truongnguyen.chatapp.fragmentnavigationcontroller.PresentStyle;
import com.app.truongnguyen.chatapp.fragmentnavigationcontroller.SupportFragment;

public class MainActivity extends BaseActivity {

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

    @Override
    protected void onServiceConnected() {
        getSinchServiceInterface().startClient(firebase.getUid());
    }
}








