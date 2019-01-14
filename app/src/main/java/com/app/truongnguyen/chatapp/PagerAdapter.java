package com.app.truongnguyen.chatapp;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.app.truongnguyen.chatapp.fragmentnavigationcontroller.SupportFragment;
import com.app.truongnguyen.chatapp.main.ContactsFragment;
import com.app.truongnguyen.chatapp.main.ConversationsFragment;
import com.app.truongnguyen.chatapp.main.SettingsFragment;

import java.util.ArrayList;

public class PagerAdapter extends FragmentStatePagerAdapter {
    private Context mContext;
    private ArrayList<SupportFragment> mData = new ArrayList<>();

    public PagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
        initData();
    }

    private void initData() {
        mData.add(ConversationsFragment.newInstance());
        mData.add(ContactsFragment.newInstance());
        mData.add(SettingsFragment.newInstance());
    }

    @Override
    public Fragment getItem(int i) {
        return mData.get(i);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

//    @Override
//    public CharSequence getPageTitle(int position) {
//        String title = "";
//        switch (position) {
//            case 0:
//                title = mContext.getResources().getString(R.string.conversations_fragment);
//                break;
//            case 1:
//                title = mContext.getResources().getString(R.string.contacts_fragment);
//                break;
//            case 2:
//                title = mContext.getResources().getString(R.string.settings_fragment);
//                break;
//        }
//        return title;
//    }
}
