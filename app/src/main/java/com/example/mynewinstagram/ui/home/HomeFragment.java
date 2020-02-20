package com.example.mynewinstagram.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mynewinstagram.CustomAdapters.HomeCustomAdapter;
import com.example.mynewinstagram.R;
import com.example.mynewinstagram.ui.NewPost.NewPaperModelClass;
import com.example.mynewinstagram.ui.NewPost.UserPostClass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {


    RecyclerView homeRecyclerView;
    HomeCustomAdapter myAdapter;
    List<UserPostClass> list;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    DocumentSnapshot lastVisible;
    Context context;
    boolean isLoadFirst;
    View view;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);





        return view;
    }

    private void initialize() {
        homeRecyclerView = view.findViewById(R.id.recyclerView);
        list = new ArrayList<>();

        myAdapter = new HomeCustomAdapter(context,list);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        homeRecyclerView.setHasFixedSize(true);
        homeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        homeRecyclerView.setAdapter(myAdapter);
    }


    @Override
    public void onStart() {
        super.onStart();
        initialize();

        CollectionReference cRef = firebaseFirestore.collection("NewPosts");
        cRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (!queryDocumentSnapshots.isEmpty()){

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {

                        UserPostClass currentClass = doc.toObject(UserPostClass.class);
                        list.add(currentClass);
                        myAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
        /*
        // context.startActivity(new Intent(context, NewPaperUpload.class));
        homeRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                boolean canScroll = !recyclerView.canScrollVertically(1);
                if(canScroll){
                    if(lastVisible!=null){
                        Toast.makeText(getContext(),lastVisible.get("desc").toString(), Toast.LENGTH_SHORT).show();
                        loadMoreDate();
                    }

                }
            }
        });

        Query currentQuery = firebaseFirestore.collection("NewPosts").orderBy("timestamp", Query.Direction.DESCENDING).limit(3);

        if(firebaseAuth.getCurrentUser()!=null){
            currentQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                    if (!queryDocumentSnapshots.isEmpty()){
                        if(isLoadFirst){
                            lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                        }
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {

                            UserPostClass currentClass = doc.toObject(UserPostClass.class);
                            if(isLoadFirst){
                                list.add(currentClass);
                            }else{
                                list.add(0,currentClass);
                            }
                            myAdapter.notifyDataSetChanged();
                        }
                        isLoadFirst = false;
                    }
                }
            });
            /*currentQuery.addSnapshotListener(getActivity(),new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                    if (!queryDocumentSnapshots.isEmpty()){
                        if(isLoadFirst){
                            lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                        }
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {

                            UserPostClass currentClass = doc.toObject(UserPostClass.class);
                            if(isLoadFirst){
                                list.add(currentClass);
                            }else{
                                list.add(0,currentClass);
                            }
                            myAdapter.notifyDataSetChanged();
                        }
                        isLoadFirst = false;
                    }
                }
            });
        }
       */
    }

    void loadMoreDate(){
        Query currentQuery = firebaseFirestore.collection("NewPosts").orderBy("timestamp", Query.Direction.DESCENDING).startAfter(lastVisible).limit(3);
        if(firebaseAuth.getCurrentUser()!=null){

            currentQuery.addSnapshotListener(getActivity(),new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                    if (!queryDocumentSnapshots.isEmpty()){
                        lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {

                            UserPostClass currentClass = doc.toObject(UserPostClass.class);
                            list.add(currentClass);
                            myAdapter.notifyDataSetChanged();
                        }
                    }
                }
            });
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        //recyclerView.setAdapter(homeCustomAdapter);
        //recyclerView.setHasFixedSize(true);
        //recyclerView.setLayoutManager(new LinearLayoutManager(context));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

    }
}