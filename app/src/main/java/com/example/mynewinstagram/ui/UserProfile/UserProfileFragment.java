package com.example.mynewinstagram.ui.UserProfile;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.mynewinstagram.R;
import com.example.mynewinstagram.authentication_package.LoginActivity;
import com.example.mynewinstagram.authentication_package.UploadProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileFragment extends Fragment {


    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    CircleImageView userCircularImage;
    final int PERMISSION_REQUEST_CODE = 1;
    TextView userName;
    TextView userCoins;
    FirebaseStorage firebaseStorage;
    Uri imageUri;
    View view;
    TextView submittedQuestions;
    TextView atttempted;
    Button next,logOut;
    Context context;
    int imgFlag = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        instantiate();

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(firebaseAuth.getCurrentUser()!=null){
                    firebaseAuth.signOut();
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
            }
        });

        final FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        firebaseFirestore.collection("New_User").document(currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().exists()){
                        String Name = "";
                        String Url = "";
                        Name = task.getResult().get("NAME").toString();
                        Url = task.getResult().get("URL").toString();
                        userName.setText(Name);
                        Picasso.with(getActivity())
                                .load(Url)
                                .fit()
                                .centerCrop()
                                .placeholder(R.drawable.user)
                                .into(userCircularImage);
                    }else if(imgFlag==0){
                        Toast.makeText(context, "User Information not uploaded yet", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(context,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });


        final DocumentReference docRef = firebaseFirestore.collection("New_User").document(firebaseAuth.getCurrentUser().getUid()).collection("countsId").document("TotalCounts");

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        String coins = document.get("Count").toString();
                        userCoins.setText(coins);


                    }
                } else {
                    Toast.makeText(context,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });


            final DocumentReference docRef2 = firebaseFirestore.collection("New_User").document(firebaseAuth.getCurrentUser().getUid()).collection("submittedCount").document("TotalSubmittedQuestions");

            docRef2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {

                            String totalSubmissions = document.get("Count").toString();
                            submittedQuestions.setText(totalSubmissions);
                        }
                    } else {
                        Toast.makeText(context,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        final DocumentReference docRef3 = firebaseFirestore.collection("New_User").document(firebaseAuth.getCurrentUser().getUid()).collection("totalAttempts").document("TotalCounts");

        docRef3.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        String totalSubmissions = document.get("Count").toString();
                        atttempted.setText(totalSubmissions);

                    }
                } else {
                    Toast.makeText(context,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
    private void instantiate() {

        firebaseAuth = FirebaseAuth.getInstance();
        userName = view.findViewById(R.id.userProfileName);
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        logOut = view.findViewById(R.id.userProfileLogOut);
        userCircularImage = view.findViewById(R.id.userProfileImage);
        userCoins = view.findViewById(R.id.userProfileCoins);
        submittedQuestions = view.findViewById(R.id.userProfileContributed);

        atttempted = view.findViewById(R.id.userProfileAttempted);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
    }
}
