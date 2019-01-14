package com.app.truongnguyen.chatapp.search_fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.app.truongnguyen.chatapp.R;
import com.app.truongnguyen.chatapp.data.Firebase;
import com.app.truongnguyen.chatapp.data.UserInfo;
import com.app.truongnguyen.chatapp.fragmentnavigationcontroller.SupportFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
        adapter = new ListPeopleAdapter(mcContext, peopleList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(mcContext));

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getMainActivity().dismiss();
            }
        });

        firebase.getUserFolderDbs().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();

                    List<UserInfo> userInfos = querySnapshot.toObjects(UserInfo.class);

                    peopleList.clear();
                    peopleList.addAll(userInfos);
                    adapter.notifyDataSetChanged();
                } else
                    Log.w("", "Error getting documents.", task.getException());
            }

        });


        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                firebase.getUserFolderDbs().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            QuerySnapshot querySnapshot = task.getResult();

                            List<UserInfo> userInfos = querySnapshot.toObjects(UserInfo.class);

                            String key = input.getText().toString().toLowerCase();

                            peopleList.clear();

                            for (UserInfo u : userInfos)
                                if (u.getEmail().toLowerCase().contains(key)
                                        || u.getUserName().toLowerCase().contains(key))
                                    peopleList.add(u);

                            adapter.notifyDataSetChanged();
                        } else
                            Log.w("", "Error getting documents.", task.getException());
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
