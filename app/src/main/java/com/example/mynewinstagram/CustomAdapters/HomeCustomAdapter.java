package com.example.mynewinstagram.CustomAdapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mynewinstagram.R;
import com.example.mynewinstagram.ui.NewPost.UserPostClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeCustomAdapter extends RecyclerView.Adapter<HomeCustomAdapter.MyViewHolder> {


    Context context;
    List<UserPostClass> list;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;

    public HomeCustomAdapter(Context context, List<UserPostClass> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view =  LayoutInflater.from(context).inflate(R.layout.custom_post_page,parent,false);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final MyViewHolder myViewHolder = holder;
        holder.setIsRecyclable(false);
        UserPostClass userPostClass = list.get(position);
        String desc = userPostClass.getDesc();
        String postImageUrl = userPostClass.getPostImageUrl();
        final String docId = userPostClass.getDocId();

        Picasso.with(context).load(postImageUrl).placeholder(R.drawable.user).fit().into(holder.homeImageView);
        holder.homeDesc.setText(desc);
        final Date currentDate = userPostClass.getTimestamp();
        long milliSeconds = currentDate.getTime();
        String date = DateFormat.format("dd/MM/yyyy",milliSeconds).toString();
        holder.date.setText(date);
        final FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        firebaseFirestore.collection("NewPosts/"+docId+"/Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(!queryDocumentSnapshots.isEmpty()){
                    myViewHolder.likeButton.setImageResource(R.drawable.fav_button_accent);
                    int size_ = queryDocumentSnapshots.getDocuments().size();
                    holder.likeNo.setText(Integer.toString(size_));
                }else{
                    holder.likeNo.setText(Integer.toString(0));
                }
            }
        });

        holder.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Map<String, Object> currentMp = new HashMap<>();
                currentMp.put("ServerTimeStamp", FieldValue.serverTimestamp());

                firebaseFirestore.collection("NewPosts/"+docId+"/Likes").document(currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(!task.getResult().exists()){
                            holder.likeButton.setImageResource(R.drawable.fav_button_accent);
                            firebaseFirestore.collection("NewPosts/"+docId+"/Likes").document(currentUser.getUid()).set(currentMp);
                        }else{
                            holder.likeButton.setImageResource(R.drawable.fav_button);
                            firebaseFirestore.collection("NewPosts/"+docId+"/Likes").document(currentUser.getUid()).delete();
                        }
                    }
                });


            }
        });
        String userId = userPostClass.getUserId();

        firebaseFirestore.collection("New_User").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();

                    String name = documentSnapshot.get("NAME").toString();
                    String url = documentSnapshot.get("URL").toString();
                    holder.name.setText(name);
                    Picasso.with(context).load(url).placeholder(R.drawable.user).into(holder.profileIcon);
                }else{
                    Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView homeDesc;
        ImageView homeImageView;
        TextView date;
        TextView name;
        TextView likeNo;
        ImageView likeButton;
        CircleImageView profileIcon;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            homeDesc = itemView.findViewById(R.id.custom_post_page_desc);
            homeImageView =  itemView.findViewById(R.id.custom_post_page_post_img);
            date = itemView.findViewById(R.id.custom_post_page_date);
            name = itemView.findViewById(R.id.custom_post_page_user_name);
            profileIcon = itemView.findViewById(R.id.custom_post_page_profile_pic);
            likeNo = itemView.findViewById(R.id.custom_post_page_like_count);
            likeButton = itemView.findViewById(R.id.custom_post_page_like);

        }
    }
}
