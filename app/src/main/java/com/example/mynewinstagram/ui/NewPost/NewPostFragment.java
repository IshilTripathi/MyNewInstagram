package com.example.mynewinstagram.ui.NewPost;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.mynewinstagram.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

import id.zelory.compressor.Compressor;


public class NewPostFragment extends Fragment {

    private static final int PERMISSION_REQUEST_CODE = 1 ;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    FirebaseStorage firebaseStorage;
    ProgressBar progressBar;
    Context context;
    ImageView defaultImageNewPost;
    TextInputLayout descInputLayout;
    EditText descEditText;
    Button newPost;
    Uri imageUri;
    String thumbUrl;
    private Bitmap compressImage;
    String POST_IMAGE_URL;
    String DESC;
    String USERID;
    View view;

    public NewPostFragment() {
        // Required empty public constructor
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view = getView();
        initialize(getView());
    }


    void initialize(View view){
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        defaultImageNewPost = view.findViewById(R.id.defaultImageNewPost);
        descInputLayout = view.findViewById(R.id.descInputLayout);
        descEditText = view.findViewById(R.id.descEditText);
        newPost = view.findViewById(R.id.newPost);
        progressBar = view.findViewById(R.id.progressBarNewPost);
    }

    @Override
    public void onStart() {
        super.onStart();
        newPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPost();
            }
        });

        defaultImageNewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Build.VERSION.SDK_INT>Build.VERSION_CODES.M){
                    if(ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PERMISSION_REQUEST_CODE);
                    }else{
                        startPick();
                    }
                }
            }
        });
    }

    private void startPick() {

        CropImage.activity().setAspectRatio(1,1)
                .setMultiTouchEnabled(true)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setActivityTitle("New Post")
                .setBackgroundColor(R.color.primaryDarkColor)
                .start(getActivity());
    }

    /*@Override
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
    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
  //      Toast.makeText(context, "result", Toast.LENGTH_SHORT).show();

        if(requestCode== CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result =  CropImage.getActivityResult(data);
//            Toast.makeText(context, "result", Toast.LENGTH_SHORT).show();

            if(resultCode== Activity.RESULT_OK){
                imageUri = result.getUri();

                if(imageUri==null){
                    Toast.makeText(context, "image uri is null", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context, "not null", Toast.LENGTH_SHORT).show();

                }
                defaultImageNewPost.setImageURI(imageUri);
            }else if(resultCode==CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Toast.makeText(context, result.getError().toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }



    private void addPost() {
        progressBar.setVisibility(View.VISIBLE);
        final String randomName = UUID.randomUUID().toString();
        if(imageUri==null){
            Toast.makeText(context, "Please select an image to be posted", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.INVISIBLE);
            return;
        }
        final StorageReference storageReference = firebaseStorage.getReference("Posts").child(randomName);

        storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String desc = descEditText.getText().toString().trim();
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if(TextUtils.isEmpty(desc)){
                            desc = "NO DESCRIPTION";
                        }
                        POST_IMAGE_URL = uri.toString();
                        DESC = desc;
                        USERID = user.getUid();
                        //CURRENTTIME = FieldValue.serverTimestamp();

                        File compressFile = new File(imageUri.getPath());
                        try {
                            compressImage = new Compressor(context)
                                    .setMaxWidth(100)
                                    .setMaxHeight(100)
                                    .setQuality(10)
                                    .compressToBitmap(compressFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        ByteArrayOutputStream baos  = new ByteArrayOutputStream();
                        compressImage.compress(Bitmap.CompressFormat.JPEG,100,baos);
                        byte compressByte[] = baos.toByteArray();
                        addToStorageThumb(compressByte,randomName);


                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void addToStorageThumb(byte[] compressByte, final String randomName) {


        final StorageReference storageReference = firebaseStorage.getReference("Posts/ThumbNails").child(randomName+".jpg");
        UploadTask uploadTask = storageReference.putBytes(compressByte);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        thumbUrl = uri.toString();
                        add(randomName);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);

            }
        });
    }

    private void add(String randomName) {


        addToFirestoreMain(randomName);

    }

    private void addToFirestoreMain(String randomName) {
        CollectionReference collectionReference = firestore.collection("NewPosts");
        DocumentReference documentReference = collectionReference.document(randomName);
        String docId = documentReference.getId();
        UserPostClass userPostClass = new UserPostClass(POST_IMAGE_URL,DESC,USERID,thumbUrl,docId);
        documentReference.set(userPostClass).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(context, "Post Uploaded", Toast.LENGTH_SHORT).show();
            }
        });
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_post, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }


}
