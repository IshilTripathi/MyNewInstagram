package com.example.mynewinstagram.CustomAdapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mynewinstagram.R;
import com.example.mynewinstagram.ui.NewPost.NewPaperModelClass;
import com.example.mynewinstagram.ui.NewPost.UserPostClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class FeedCustomAdapter extends RecyclerView.Adapter<FeedCustomAdapter.MyViewHolder> {


    Context context;
    List<NewPaperModelClass> list;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;

    public FeedCustomAdapter(Context context, List<NewPaperModelClass> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view =  LayoutInflater.from(context).inflate(R.layout.custom_feed_page,parent,false);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final MyViewHolder myViewHolder = holder;
        holder.setIsRecyclable(false);
        NewPaperModelClass customClass = list.get(position);
        String question = customClass.getQuestion();
        final String answer = customClass.getAnswer();
        String option1 = customClass.getOption1();
        String option2 = customClass.getOption2();
        String option3 = customClass.getOption3();
        String option4 = customClass.getOption4();
        final String userId = customClass.getUserId();

        final String docId = customClass.getDocId();


        holder.feedDesc.setText(question);
        holder.radioBtnOption1.setText(option1);
        holder.radioBtnOption2.setText(option2);
        holder.radioBtnOption3.setText(option3);
        holder.radioBtnOption4.setText(option4);

        final Date currentDate = customClass.getTimestamp();
        long milliSeconds = currentDate.getTime();
        String date = DateFormat.format("dd/MM/yyyy",milliSeconds).toString();
        holder.feedDate.setText(date);
        final FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        firebaseFirestore.collection("NewQuizFeed/"+docId+"/Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(!queryDocumentSnapshots.isEmpty()){
                    myViewHolder.feedLikeButton.setImageResource(R.drawable.fav_button_accent);
                    int size_ = queryDocumentSnapshots.getDocuments().size();
                    holder.feedLikeNo.setText(Integer.toString(size_));
                }else{
                    holder.feedLikeNo.setText(Integer.toString(0));
                }
            }
        });

        final String[] ans = {""};

        holder.submitOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = holder.radioGroup.getCheckedRadioButtonId();
                if(id==R.id.radioBtnOption1){
                   // Toast.makeText(context, "1", Toast.LENGTH_SHORT).show();
                    ans[0] = "1";
                }else if(id==R.id.radioBtnOption2){
                    //Toast.makeText(context, "2", Toast.LENGTH_SHORT).show();
                    ans[0] = "2";
                }else if(id==R.id.radioBtnOption3){
                    //Toast.makeText(context, "3", Toast.LENGTH_SHORT).show();
                    ans[0] = "3";
                }else if(id==R.id.radioBtnOption4){
                    //Toast.makeText(context, "4", Toast.LENGTH_SHORT).show();
                    ans[0] = "4";
                }else{
                    //Toast.makeText(context, "nothing", Toast.LENGTH_SHORT).show();

                }

                final DocumentReference docRef3 = firebaseFirestore.collection("New_User").document(firebaseAuth.getCurrentUser().getUid()).collection("totalAttempts").document("TotalCounts");

                docRef3.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {

                                int res = Integer.valueOf(document.get("Count").toString());
                                res+=1;
                                Map<String,String> map = new HashMap<>();
                                map.put("Count",Integer.toString(res));
                                docRef3.set(map);


                            }else{
                                Map<String,String> map = new HashMap<>();
                                map.put("Count",Integer.toString(1));
                                docRef3.set(map);
                            }
                        } else {
                            Toast.makeText(context,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                int correct = 0;
                if(ans[0].equals(answer)){
                    correct = 1;
                    Toast.makeText(context, "correct", Toast.LENGTH_SHORT).show();

                }else{
                    Toast.makeText(context, "wrong", Toast.LENGTH_SHORT).show();

                }
                final Map<String, Object> currentMp = new HashMap<>();
                currentMp.put("ServerTimeStamp", FieldValue.serverTimestamp());
                currentMp.put("correct",Integer.valueOf(correct));

                if(correct==1){
                    /*Map<String, Object> map = new HashMap<>();
                    currentMp.put("ServerTimeStamp", FieldValue.serverTimestamp());
                    DocumentReference documentReference = firebaseFirestore.collection("New_User/"+userId).document(docId);
                    documentReference.set(map);*/


                    final DocumentReference docRef = firebaseFirestore.collection("New_User").document(firebaseAuth.getCurrentUser().getUid()).collection("countsId").document("TotalCounts");

                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {

                                        int res = Integer.valueOf(document.get("Count").toString());
                                        res+=1;
                                        Map<String,String> map = new HashMap<>();
                                        map.put("Count",Integer.toString(res));
                                        docRef.set(map);


                                }else{
                                    Map<String,String> map = new HashMap<>();
                                    map.put("Count",Integer.toString(1));
                                    docRef.set(map);
                                }
                            } else {
                                Toast.makeText(context,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                }

                firebaseFirestore.collection("NewQuizFeed/"+docId+"/Submitted").document(currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(!task.getResult().exists()){
                            firebaseFirestore.collection("NewQuizFeed/"+docId+"/Submitted").document(currentUser.getUid()).set(currentMp);
                        }
                    }
                });



            }
        });


        holder.feedLikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Map<String, Object> currentMp = new HashMap<>();
                currentMp.put("ServerTimeStamp", FieldValue.serverTimestamp());

                firebaseFirestore.collection("NewQuizFeed/"+docId+"/Likes").document(currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(!task.getResult().exists()){
                            holder.feedLikeButton.setImageResource(R.drawable.fav_button_accent);
                            firebaseFirestore.collection("NewQuizFeed/"+docId+"/Likes").document(currentUser.getUid()).set(currentMp);
                        }else{
                            holder.feedLikeButton.setImageResource(R.drawable.fav_button);
                            firebaseFirestore.collection("NewQuizFeed/"+docId+"/Likes").document(currentUser.getUid()).delete();
                        }
                    }
                });


            }
        });
        //String userId = customClass.getUserId();

        firebaseFirestore.collection("New_User").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();

                    String name = documentSnapshot.get("NAME").toString();
                    String url = documentSnapshot.get("URL").toString();
                    holder.feedName.setText(name);
                    Picasso.with(context).load(url).placeholder(R.drawable.user).into(holder.feedProfileIcon);
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

        TextView feedDesc;
        TextView feedDate;
        TextView feedName;
        TextView feedLikeNo;
        ImageView feedLikeButton;
        CircleImageView feedProfileIcon;
        RadioGroup radioGroup;
        RadioButton radioBtnOption1,radioBtnOption2,radioBtnOption3,radioBtnOption4;
        Button submitOption;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            feedDesc = itemView.findViewById(R.id.custom_feed_page_desc);
            feedDate = itemView.findViewById(R.id.custom_feed_page_date);
            feedName = itemView.findViewById(R.id.custom_feed_page_user_name);
            feedProfileIcon = itemView.findViewById(R.id.custom_feed_page_profile_pic);
            feedLikeNo = itemView.findViewById(R.id.custom_feed_page_like_count);
            feedLikeButton = itemView.findViewById(R.id.custom_feed_page_like);
            radioGroup = itemView.findViewById(R.id.radioGroup);
            radioBtnOption1 = itemView.findViewById(R.id.radioBtnOption1);
            radioBtnOption2 = itemView.findViewById(R.id.radioBtnOption2);
            radioBtnOption3 = itemView.findViewById(R.id.radioBtnOption3);
            radioBtnOption4 = itemView.findViewById(R.id.radioBtnOption4);
            submitOption = itemView.findViewById(R.id.submitOption);

        }
    }
}
