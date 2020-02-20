package com.example.mynewinstagram.ui.Quiz_feed;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.mynewinstagram.CustomAdapters.FeedCustomAdapter;
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


public class Feed extends Fragment {

    RecyclerView recyclerViewFeed;
    FeedCustomAdapter feedCustomAdapter;
    List<NewPaperModelClass> list;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    DocumentSnapshot lastVisible;
    Context context;
    boolean isLoadFirst;
    View view;

    public Feed() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_feed, container, false);
        return view;
    }

    private void initialize() {
        recyclerViewFeed = view.findViewById(R.id.recyclerViewFeed);
        list = new ArrayList<>();

        feedCustomAdapter = new FeedCustomAdapter(context,list);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        recyclerViewFeed.setHasFixedSize(true);
        recyclerViewFeed.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewFeed.setAdapter(feedCustomAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        initialize();
        CollectionReference cRef = firebaseFirestore.collection("NewQuizFeed");
        cRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (!queryDocumentSnapshots.isEmpty()){

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {

                        NewPaperModelClass currentClass = doc.toObject(NewPaperModelClass.class);
                        list.add(currentClass);
                        feedCustomAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
        // context.startActivity(new Intent(context, NewPaperUpload.class));
        /*recyclerViewFeed.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                boolean canScroll = !recyclerViewFeed.canScrollVertically(1);
                if(canScroll){
                    if(lastVisible!=null){
                        Toast.makeText(getContext(),lastVisible.get("desc").toString(), Toast.LENGTH_SHORT).show();
                        loadMoreDate();
                    }

                }
            }
        });

        Query currentQuery = firebaseFirestore.collection("NewQuizFeed").orderBy("timestamp", Query.Direction.DESCENDING).limit(3);

        if(firebaseAuth.getCurrentUser()!=null){
            currentQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                    if (!queryDocumentSnapshots.isEmpty()){
                        if(isLoadFirst){
                            lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                        }
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {

                            NewPaperModelClass currentClass = doc.toObject(NewPaperModelClass.class);
                            if(isLoadFirst){
                                list.add(currentClass);
                            }else{
                                list.add(0,currentClass);
                            }
                            feedCustomAdapter.notifyDataSetChanged();
                        }
                        isLoadFirst = false;
                    }
                }
            });

        }
       */
    }

    void loadMoreDate(){
        Query currentQuery = firebaseFirestore.collection("NewQuizFeed").orderBy("timestamp", Query.Direction.DESCENDING).startAfter(lastVisible).limit(3);
        if(firebaseAuth.getCurrentUser()!=null){

            currentQuery.addSnapshotListener(getActivity(),new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                    if (!queryDocumentSnapshots.isEmpty()){
                        lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {

                            NewPaperModelClass currentClass = doc.toObject(NewPaperModelClass.class);
                            list.add(currentClass);
                            feedCustomAdapter.notifyDataSetChanged();
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
