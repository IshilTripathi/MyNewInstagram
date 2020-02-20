package com.example.mynewinstagram.authentication_package;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.mynewinstagram.HomeActivity;
import com.example.mynewinstagram.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 999;
    ConstraintLayout constraintLayout;
    TextInputLayout usernameLayout,passwordInputLayout;
    TextInputEditText userNameEditText,passwordEditText;
    FirebaseAuth firebaseAuth;
    ProgressBar progressBar;
    MaterialButton loginButton;
    TextView signUp;
    GoogleSignInOptions gso;
    GoogleSignInClient mGoogleSignInClient;
    SignInButton google;
    LoginButton buttonFacebookLogin;
    CallbackManager mCallbackManager;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //startActivity(new Intent(this,UploadProfile.class));

        initializeInstances();
    }

    private void initializeInstances() {
        constraintLayout = findViewById(R.id.constraintLayout);
        constraintLayout.setOnClickListener(null);
        usernameLayout = findViewById(R.id.userNameInputLayout);
        passwordInputLayout = findViewById(R.id.passwordInputLayout);
        userNameEditText = findViewById(R.id.userNameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        progressBar = findViewById(R.id.progressBarLogin);
        signUp = findViewById(R.id.SignUp);
        loginButton = findViewById(R.id.login);
        firebaseAuth = FirebaseAuth.getInstance();
        google = findViewById(R.id.google);
        setUpFacebookLogin();

        firebaseFirestore= FirebaseFirestore.getInstance();
        //Only for generating release has key for Facebook Login Integration..
        //printhashkey();


        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


    }

    public void facebookHash(){
        FacebookSdk.sdkInitialize(getApplicationContext());
        Log.d("AppLog", "key:" + FacebookSdk.getApplicationSignature(this));

        try {
            final PackageInfo info = this.getPackageManager().getPackageInfo(this.getPackageName(), PackageManager.GET_SIGNATURES);
            for (android.content.pm.Signature signature : info.signatures) {
                final MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                final String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.i("AppLog", "key:" + hashKey + "=");
            }
        } catch (Exception e) {
            Log.e("AppLog", "error:", e);
        }
    }
    public void printhashkey(){

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.example.mynewinstagram",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
                Toast.makeText(this,Base64.encodeToString(md.digest(), Base64.DEFAULT), Toast.LENGTH_SHORT).show();
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

    }

    private void setUpFacebookLogin() {
        buttonFacebookLogin = findViewById(R.id.buttonFacebookLogin);
        mCallbackManager = CallbackManager.Factory.create();
        buttonFacebookLogin.setReadPermissions("email", "public_profile");

        buttonFacebookLogin.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                progressBar.setVisibility(View.VISIBLE);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(LoginActivity.this, "Facebook Cancel", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(LoginActivity.this, "Facebook Error"+error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleFacebookAccessToken(final AccessToken token) {
        //Toast.makeText(LoginActivity.this, "handleFacebookAccessToken:" + token.toString(), Toast.LENGTH_SHORT).show();

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        Toast.makeText(this, "first wave", Toast.LENGTH_SHORT).show();
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String userName = firebaseAuth.getCurrentUser().getDisplayName();
                            String userID = firebaseAuth.getCurrentUser().getUid();
                            String  userUri = "https://graph.facebook.com/"+token.getUserId()+"/picture?type=large";

                            Toast.makeText(LoginActivity.this,"FACEBOOK LOGIN SUCCESSFULL!!", Toast.LENGTH_SHORT).show();

                            addToFirestoreUserInfo(userID,userName,userUri);

                            progressBar.setVisibility(View.INVISIBLE);

                            startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                            finish();

                            //String uri = firebaseAuth.getCurrentUser().getPhotoUrl().toString();
                            //Log.d("uri",uri);
                            //Toast.makeText(LoginActivity.this, "third wave : "+ emp+" : "+email, Toast.LENGTH_LONG).show();


                        } else {

                               Toast.makeText(LoginActivity.this,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                               progressBar.setVisibility(View.INVISIBLE);
                        }

                    }
                });
    }


    void addToFirestoreUserInfo(String userId,String name,String uri){

        Map<String,String> profileInfo = new HashMap<>();
        profileInfo.put("URL",uri);
        profileInfo.put("NAME",name);
        firebaseFirestore.collection("New_User").document(userId).set(profileInfo);

    }

    @Override
    protected void onResume() {
        super.onResume();

        inputTextValidation();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginForm();
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
            }
        });

        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                googleSignIn();
            }
        });

    }

    private void googleSignIn() {

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mCallbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(this,e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount account) {

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

        firebaseAuth.signInWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                //Toast.makeText(LoginActivity.this, "GoogleSignIn", Toast.LENGTH_SHORT).show();
                String currentUserId = firebaseAuth.getCurrentUser().getUid();
                String name = account.getDisplayName();
                String uri = account.getPhotoUrl().toString();

                addToFirestoreUserInfo(currentUserId,name,uri);
                startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                progressBar.setVisibility(View.INVISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this,e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });



    }

    private void loginForm() {
        usernameLayout.setErrorEnabled(false);
        passwordInputLayout.setErrorEnabled(false);
        String userName = userNameEditText.getText().toString().trim();
        String passWord = passwordEditText.getText().toString().trim();
        progressBar.setVisibility(View.VISIBLE);
        if(userName.equals("")){
            progressBar.setVisibility(View.INVISIBLE);
            usernameLayout.setErrorEnabled(true);
            usernameLayout.setError("Please enter your email");
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(userName).matches()){
            progressBar.setVisibility(View.INVISIBLE);
            usernameLayout.setErrorEnabled(true);
            usernameLayout.setError("Invalid Email");
            return;
        }
        if(passWord.equals("")){
            progressBar.setVisibility(View.INVISIBLE);
            passwordInputLayout.setErrorEnabled(true);
            passwordInputLayout.setError("Please enter your password");
            return;
        }
        if(passWord.length()<6){
            progressBar.setVisibility(View.INVISIBLE);
            passwordInputLayout.setErrorEnabled(true);
            passwordInputLayout.setError("Password length should be atleast 6");
            return;
        }
        firebaseAuth.signInWithEmailAndPassword(userName,passWord).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                progressBar.setVisibility(View.INVISIBLE);
                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                Toast.makeText(LoginActivity.this,"LOGIN SUCCESSFULL", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this, HomeActivity.class));

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void inputTextValidation() {
        userNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String currentUserName = userNameEditText.getText().toString().trim();
                usernameLayout.setEndIconVisible(true);
                if(currentUserName.isEmpty()){
                    usernameLayout.setErrorEnabled(true);
                    usernameLayout.setError("Please enter your UserName");
                }else{
                    usernameLayout.setErrorEnabled(false);

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });



        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                String currentPassword = passwordEditText.getText().toString().trim();

                if(currentPassword.isEmpty()){
                    passwordInputLayout.setErrorEnabled(true);
                    passwordInputLayout.setError("Please enter your Password");
                }else{
                    passwordInputLayout.setErrorEnabled(false);

                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        userNameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(userNameEditText.getText().toString().trim().isEmpty()){
                    usernameLayout.setErrorEnabled(true);
                    usernameLayout.setError("Please enter your UserName");
                }else{
                    usernameLayout.setErrorEnabled(false);
                }
            }
        });
    }
}
