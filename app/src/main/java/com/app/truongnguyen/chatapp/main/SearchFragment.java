package com.app.truongnguyen.chatapp.main;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.app.truongnguyen.chatapp.R;
import com.app.truongnguyen.chatapp.data.Firebase;
import com.app.truongnguyen.chatapp.data.UserInfo;
import com.app.truongnguyen.chatapp.fragmentnavigationcontroller.PresentStyle;
import com.app.truongnguyen.chatapp.fragmentnavigationcontroller.SupportFragment;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchFragment extends SupportFragment {
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
    public static SearchFragment instance = null;

    public static SearchFragment newInstance() {
        if (instance == null) {
            instance = new SearchFragment();
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
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public int getPresentTransition() {
        return PresentStyle.FADE;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);

        mcContext = getMainActivity();

        input.requestFocus();

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

        firebase.getUserFolderDbs().addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot ds
                    , @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (ds == null || ds.isEmpty())
                    return;

                List<UserInfo> userInfos = ds.toObjects(UserInfo.class);

                resultList.clear();
                resultList.addAll(userInfos);

                peopleList.clear();
                peopleList.addAll(userInfos);

                adapter.notifyDataSetChanged();
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