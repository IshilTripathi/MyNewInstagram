package com.example.mynewinstagram.authentication_package;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.mynewinstagram.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {

    TextInputLayout emailRegisterLayout,passwordRegisterLayout,repeatPasswordRegisterLayout,phoneRegisterLayout;
    TextInputEditText emailRegisterEditText,passwordRegisterEditText,repeatPasswordRegisterEditText,phoneRegisterEdiText;
    MaterialButton submitRegister;
    ConstraintLayout constraintLayoutRegister;
    FirebaseAuth firebaseAuth;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        inititalize();

    }

    private void inititalize() {

        emailRegisterLayout = findViewById(R.id.emailRegisterLayout);
        passwordRegisterLayout = findViewById(R.id.PasswordRegisterLayout);
        repeatPasswordRegisterLayout = findViewById(R.id.RepeatPasswordRegisterLayout);
        phoneRegisterLayout = findViewById(R.id.PhoneRegisterLayout);
        constraintLayoutRegister = findViewById(R.id.ContraintLayoutRegister);
        firebaseAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBarRegister);
        submitRegister = findViewById(R.id.submitRegister);

        emailRegisterEditText = findViewById(R.id.emaiRegisterlEditText);
        passwordRegisterEditText = findViewById(R.id.PasswordRegisterEditText);
        repeatPasswordRegisterEditText = findViewById(R.id.RepeatPasswordRegisterEditText);
        phoneRegisterEdiText = findViewById(R.id.PhoneRegisterEditText);

        constraintLayoutRegister.setOnClickListener(null);


    }

    @Override
    protected void onResume() {
        super.onResume();

        inputCredentialsVerification();

        submitRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitUser();
            }
        });



    }




    private void submitUser() {

        final String email = emailRegisterEditText.getText().toString().trim();
        String password = passwordRegisterEditText.getText().toString().trim();
        String repeatPassword = repeatPasswordRegisterEditText.getText().toString().trim();
        String phone = phoneRegisterEdiText.getText().toString().trim();
        progressBar.setVisibility(View.VISIBLE);

        if(TextUtils.isEmpty(email)){
            emailRegisterLayout.setErrorEnabled(true);
            emailRegisterLayout.setError("Please Enter the Email");
            progressBar.setVisibility(View.GONE);
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailRegisterLayout.setErrorEnabled(true);
            emailRegisterLayout.setError("Please Enter the Valid Email");
            progressBar.setVisibility(View.GONE);
            return;
        }
        if(TextUtils.isEmpty(password)){
            passwordRegisterLayout.setErrorEnabled(true);
            passwordRegisterLayout.setError("Please Enter The Password");
            progressBar.setVisibility(View.GONE);
            return;
        }
        if(!password.equals(repeatPassword)){
            repeatPasswordRegisterLayout.setErrorEnabled(true);
            repeatPasswordRegisterLayout.setError("Please enter the same Password as above");
            progressBar.setVisibility(View.GONE);
            return;
        }
        if(TextUtils.isEmpty(phone)){
            phoneRegisterLayout.setErrorEnabled(true);
            phoneRegisterLayout.setError("Please Enter your Contact NO.");
            progressBar.setVisibility(View.GONE);
            return;
        }


        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
              //  startActivity(new Intent(SignUpActivity.this, HomeActivity.class));
                progressBar.setVisibility(View.GONE);
                Toast.makeText(SignUpActivity.this,"SIGNUP SUCCESSFULL", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SignUpActivity.this,UploadProfile.class);
                startActivity(intent);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void inputCredentialsVerification() {


        emailRegisterEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String email = emailRegisterEditText.getText().toString().trim();
                if(TextUtils.isEmpty(email)){
                    emailRegisterLayout.setErrorEnabled(true);
                    emailRegisterLayout.setError("Please enter the Email");
                }else{
                    emailRegisterLayout.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        passwordRegisterEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String password = passwordRegisterEditText.getText().toString().trim();
                if(TextUtils.isEmpty(password)){
                    passwordRegisterLayout.setErrorEnabled(true);
                    passwordRegisterLayout.setError("Please enter the Password");
                }else if(password.length()<6){
                    passwordRegisterLayout.setErrorTextAppearance(R.style.errorAppearanceWeak);
                    passwordRegisterLayout.setErrorEnabled(true);
                    passwordRegisterLayout.setError("Password length should be atleast 6");
                }else if(password.length()<=12 && password.length()>=6){
                    passwordRegisterLayout.setErrorTextAppearance(R.style.errorAppearanceGood);
                    passwordRegisterLayout.setErrorEnabled(true);
                    passwordRegisterLayout.setError("Good Password strength");
                }else if(password.length()>12){
                    passwordRegisterLayout.setErrorTextAppearance(R.style.errorAppearanceStrong);
                    passwordRegisterLayout.setErrorEnabled(true);
                    passwordRegisterLayout.setError("String Password Strength");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        passwordRegisterEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                String password = passwordRegisterEditText.getText().toString().trim();
                if(!TextUtils.isEmpty(password)){
                    passwordRegisterLayout.setErrorEnabled(false);
                }
            }
        });

        repeatPasswordRegisterEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String repeatPassword = repeatPasswordRegisterEditText.getText().toString().trim();
                String password = passwordRegisterEditText.getText().toString().trim();
                if(TextUtils.isEmpty(repeatPassword)){
                    repeatPasswordRegisterLayout.setErrorEnabled(true);
                    repeatPasswordRegisterLayout.setError("Please enter the Password to Verify");
                }else if(!repeatPassword.equals(password)){
                    repeatPasswordRegisterLayout.setErrorEnabled(true);
                    repeatPasswordRegisterLayout.setError("Password did not Matched");
                }
                else{
                    repeatPasswordRegisterLayout.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        phoneRegisterEdiText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String phone = phoneRegisterEdiText.getText().toString().trim();
                if(TextUtils.isEmpty(phone)){
                    phoneRegisterLayout.setErrorEnabled(true);
                    phoneRegisterLayout.setError("Please enter your Phone NO.");
                }else{
                    phoneRegisterLayout.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        emailRegisterEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                String email = emailRegisterEditText.getText().toString().trim();
                if(TextUtils.isEmpty(email)){
                    emailRegisterLayout.setErrorEnabled(true);
                    emailRegisterLayout.setError("Please enter the Email");
                }else{
                    emailRegisterLayout.setErrorEnabled(false);
                }
            }
        });

    }
}
