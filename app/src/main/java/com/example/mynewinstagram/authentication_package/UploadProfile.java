package com.example.mynewinstagram.authentication_package;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.mynewinstagram.HomeActivity;
import com.example.mynewinstagram.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.google.common.reflect.Reflection.initialize;

public class UploadProfile extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    CircleImageView userCircularImage;
    final int PERMISSION_REQUEST_CODE = 1;
    TextInputLayout nameInputLayout;
    EditText nameEditText;
    ConstraintLayout constraintLayoutAccount;
    FirebaseStorage firebaseStorage;
    ProgressBar progressBar2,roundProgressBarImage;
    Uri imageUri;
    Button next,logOut;
    int imgFlag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_profile);

        instantiate();

    }

    private void instantiate() {

        firebaseAuth = FirebaseAuth.getInstance();
        nameInputLayout = findViewById(R.id.nameInputLayout);
        nameEditText = findViewById(R.id.userNameEditText);
        progressBar2 = findViewById(R.id.progressBarTop);
        roundProgressBarImage = findViewById(R.id.roundProgressBarImage);
        constraintLayoutAccount = findViewById(R.id.constraintLayoutAccount);
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        constraintLayoutAccount.setOnClickListener(null);
        next = findViewById(R.id.proceed);
        logOut = findViewById(R.id.logout);
        userCircularImage = findViewById(R.id.userCircularImage);


    }

    @Override
    protected void onResume() {
        super.onResume();

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addInformationUser();
            }
        });

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(firebaseAuth.getCurrentUser()!=null){
                   firebaseAuth.signOut();
                }
            }
        });

        userCircularImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                    if(ContextCompat.checkSelfPermission(UploadProfile.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(UploadProfile.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PERMISSION_REQUEST_CODE);
                    }else{
                        pickImage();
                    }
                }
            }
        });

        final FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        firebaseFirestore.collection("New_User").document(currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                roundProgressBarImage.setVisibility(View.VISIBLE);
                if(task.isSuccessful()){
                    if(task.getResult().exists()){
                        String Name = "";
                        String Url = "";
                        Name = task.getResult().get("NAME").toString();
                        Url = task.getResult().get("URL").toString();
                        nameEditText.setText(Name);
                        Picasso.with(UploadProfile.this)
                                .load(Url)
                                .fit()
                                .centerCrop()
                                .placeholder(R.drawable.user)
                                .into(userCircularImage);
                        roundProgressBarImage.setVisibility(View.INVISIBLE);
                    }else if(imgFlag==0){
                        roundProgressBarImage.setVisibility(View.INVISIBLE);
                        Toast.makeText(UploadProfile.this, "User Information not uploaded yet", Toast.LENGTH_SHORT).show();
                    }else{
                        roundProgressBarImage.setVisibility(View.INVISIBLE);
                    }
                }else{
                    roundProgressBarImage.setVisibility(View.INVISIBLE);
                    Toast.makeText(UploadProfile.this,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });





    }

    void addInformationUser(){
        String name = nameEditText.getText().toString().trim();
        progressBar2.setVisibility(View.VISIBLE);
        if(TextUtils.isEmpty(name)){
            nameInputLayout.setErrorEnabled(true);
            nameInputLayout.setError("Please enter your name");
            return;
        }
        if(imageUri==null){
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
            return;
        }
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        final StorageReference storageReference = firebaseStorage.getReference("ProfileImages/").child(firebaseUser.getUid()+".jpg");

        storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        String currentUid = user.getUid();
                        String downloadUrl = uri.toString();
                        String currentName = nameEditText.getText().toString().trim();
                        Map<String,String> map = new HashMap<>();

                        map.put("URL",downloadUrl);
                        map.put("NAME",currentName);
                        storeToFirestore(currentUid,map);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UploadProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                progressBar2.setVisibility(View.GONE);
                Toast.makeText(UploadProfile.this,"Good to go....", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(UploadProfile.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UploadProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    void storeToFirestore(String documentName,Map map){
        DocumentReference documentReference = firebaseFirestore.collection("New_User").document(documentName);
        documentReference.set(map);
    }


    void pickImage(){
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .setAspectRatio(1,1)
                .start(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result =  CropImage.getActivityResult(data);
            if(resultCode== Activity.RESULT_OK){
                imageUri = result.getUri();
                imgFlag = 1;
                try {
                    Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imageUri);
                    userCircularImage.setImageBitmap(imageBitmap);
                    roundProgressBarImage.setVisibility(View.INVISIBLE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else if(resultCode==CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Toast.makeText(this, result.getError().toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
