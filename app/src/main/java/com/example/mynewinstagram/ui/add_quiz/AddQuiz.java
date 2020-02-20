package com.example.mynewinstagram.ui.add_quiz;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.mynewinstagram.R;
import com.example.mynewinstagram.ui.NewPost.NewPaperModelClass;
import com.example.mynewinstagram.ui.NewPost.UserPostClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.google.common.reflect.Reflection.initialize;


public class AddQuiz extends Fragment {

    EditText question;
    EditText option1,option2,option3,option4;
    EditText answer;
    View view;
    NewPaperModelClass currentPaperClass;
    Context context;


    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;
    Button submitPaperButton;
    ProgressBar progressBarAddQuiz;


    public AddQuiz() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_quiz, container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        initialise();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseUser = firebaseAuth.getCurrentUser();


        submitPaperButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToFirestoreMain();
            }
        });
    }

    private void addToFirestoreMain() {
        progressBarAddQuiz.setVisibility(View.VISIBLE);

        final String randomName = UUID.randomUUID().toString();
        CollectionReference collectionReference = firebaseFirestore.collection("NewQuizFeed");
        DocumentReference documentReference = collectionReference.document(randomName);
        String docId = documentReference.getId();
        String questionContent = question.getText().toString();
        String option1Content = option1.getText().toString();
        String option2Content = option2.getText().toString();
        String option3Content = option3.getText().toString();
        String option4Content = option4.getText().toString();
        String correctOptionContent = answer.getText().toString();

        if(TextUtils.isEmpty(questionContent) || TextUtils.isEmpty(option1Content) ||
                TextUtils.isEmpty(option2Content) || TextUtils.isEmpty(option3Content)||
        TextUtils.isEmpty(option4Content) || TextUtils.isEmpty(correctOptionContent)){
            Toast.makeText(context, "Please Dont Leave any of the option", Toast.LENGTH_SHORT).show();
            progressBarAddQuiz.setVisibility(View.INVISIBLE);
            return;
        }

        currentPaperClass = new NewPaperModelClass(docId,firebaseUser.getUid(),questionContent,option1Content,option2Content
                ,option3Content,option4Content,correctOptionContent);

        documentReference.set(currentPaperClass).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, "Post Uploaded", Toast.LENGTH_SHORT).show();
                addCounts();
                addSubmittedQuestions();
                progressBarAddQuiz.setVisibility(View.INVISIBLE);
                clearData();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                progressBarAddQuiz.setVisibility(View.INVISIBLE);

            }
        });
    }

    private void addSubmittedQuestions() {
        final DocumentReference docRef = firebaseFirestore.collection("New_User").document(firebaseAuth.getCurrentUser().getUid()).collection("submittedCount").document("TotalSubmittedQuestions");

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

    private void addCounts() {
        final DocumentReference docRef = firebaseFirestore.collection("New_User").document(firebaseAuth.getCurrentUser().getUid()).collection("countsId").document("TotalCounts");

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        int res = Integer.valueOf(document.get("Count").toString());
                        res+=5;
                        Map<String,String> map = new HashMap<>();
                        map.put("Count",Integer.toString(res));
                        docRef.set(map);


                    }else{
                        Map<String,String> map = new HashMap<>();
                        map.put("Count",Integer.toString(5));
                        docRef.set(map);
                    }
                } else {
                    Toast.makeText(context,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void clearData() {
        question.setText("");

        option1.setText("");

        option2.setText("");

        option3.setText("");

        option4.setText("");

        answer.setText("");

    }

    private void initialise() {
        question = view.findViewById(R.id.question);
        option1 = view.findViewById(R.id.option1);
        option2 = view.findViewById(R.id.option2);
        option3 = view.findViewById(R.id.option3);
        option4 = view.findViewById(R.id.option4);
        answer = view.findViewById(R.id.correctOption);
        submitPaperButton = view.findViewById(R.id.submitPaperButton);
        progressBarAddQuiz = view.findViewById(R.id.progressBarAddQuiz);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
    }
}
