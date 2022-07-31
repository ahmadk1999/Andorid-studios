package com.example.androidlabproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "MyPrefsFile";
    private static final String PREF_EMAIL = "email";


    SharedPrefManager sharedPrefManager;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    FirebaseFirestore mStore;

    CheckBox rememberMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPrefManager = SharedPrefManager.getInstance(this);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mStore = FirebaseFirestore.getInstance();


        ProgressBar LogInProgressBar = (ProgressBar) findViewById(R.id.progressBarLogIn);
        LogInProgressBar.setVisibility(View.INVISIBLE);

        EditText userEmailEditText = (EditText) findViewById(R.id.input_email);
        EditText userPasswordEditText = findViewById(R.id.input_password);
        rememberMe = findViewById(R.id.remember_me);

        SharedPreferences pref = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        String email = pref.getString(PREF_EMAIL, null);

        if (email != null) {
            userEmailEditText.setText(email);
            rememberMe.setChecked(true);
            Toast.makeText(LoginActivity.this, "Remembered Your Email", Toast.LENGTH_LONG).show();
        }else{
            userEmailEditText.setText(null);
            rememberMe.setChecked(false);
            Toast.makeText(LoginActivity.this, "Could Not Remember Your Email", Toast.LENGTH_LONG).show();
        }


        Button loginButton = (Button) findViewById(R.id.login_button);
        Button homeButton = (Button) findViewById(R.id.sendtohome);


        TextView signup = findViewById(R.id.textviewLogin);


        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToHomeActivity();
            }
        });

        rememberMe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(buttonView.isChecked()){
                    getSharedPreferences(PREFS_NAME,MODE_PRIVATE)
                            .edit()
                            .putString(PREF_EMAIL, userEmailEditText.getText().toString())
                            .commit();
                    Toast.makeText(LoginActivity.this, "Will Remember", Toast.LENGTH_LONG).show();
                }else{
                    getSharedPreferences(PREFS_NAME,MODE_PRIVATE)
                            .edit()
                            .putString(PREF_EMAIL,null)
                            .commit();
                    Toast.makeText(LoginActivity.this, "Will Forget your email", Toast.LENGTH_LONG).show();
                }
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(rememberMe.isChecked()){
                    getSharedPreferences(PREFS_NAME,MODE_PRIVATE)
                            .edit()
                            .putString(PREF_EMAIL, userEmailEditText.getText().toString())
                            .commit();
                    Toast.makeText(LoginActivity.this, "LogIn is Successful and Will Remember", Toast.LENGTH_LONG).show();
                }else{
                    getSharedPreferences(PREFS_NAME,MODE_PRIVATE)
                            .edit()
                            .putString(PREF_EMAIL,null)
                            .commit();
                    Toast.makeText(LoginActivity.this, "LogIn is Successful and Will Forget your email", Toast.LENGTH_LONG).show();
                }

                String email = userEmailEditText.getText().toString();
                String password = userPasswordEditText.getText().toString();

                if (isEmail(userEmailEditText) == false) {
                    userEmailEditText.setError("Enter valid email!");
                    userEmailEditText.requestFocus();
                }else if (isEmpty(userPasswordEditText)) {
                    userPasswordEditText.setError("Password is required!");
                    userPasswordEditText.requestFocus();
                }else if (userPasswordEditText.getText().length() < 8 && userPasswordEditText.getText().length() > 15){
                    userPasswordEditText.setError("Password must be between 8 and 15 characters long!!");
                    userPasswordEditText.requestFocus();
                }else {
                    LogInProgressBar.setVisibility(View.VISIBLE);
                    mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){

                                Toast.makeText(LoginActivity.this, "LogIn is Successful", Toast.LENGTH_LONG).show();
                                CheckUserType(task.getResult().getUser().getUid());
                                LogInProgressBar.setVisibility(View.INVISIBLE);
                                sendUserToHomeActivity();

                            }else{
                                Toast.makeText(LoginActivity.this, "Email and password do not match.", Toast.LENGTH_LONG).show();
                                LogInProgressBar.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                }

            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

    }

    private void CheckUserType(String uid) {
        DocumentReference agencyDF = mStore.collection("Agency").document(uid);
        agencyDF.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d("TAG","Agency onSuccess:" + documentSnapshot.getData());
                if (documentSnapshot.getString("isAuthedToPos") != null){ // Agency
                    finish();
                }
            }
        });

        agencyDF.get().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                DocumentReference tenantDF = mStore.collection("Tenant").document(uid);
                tenantDF.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.getString("Nationality") != null){ // Tenant
                            Log.d("TAG","Tenant onSuccess:" + documentSnapshot.getData());
                            finish();
                        }

                    }
                });
            }
        });

    }

    private void sendUserToHomeActivity() {
        finish();
        startActivity(new Intent(LoginActivity.this, listProperties.class));
    }


    private boolean isEmpty(EditText text) {
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }

    boolean isEmail(EditText text) {
        CharSequence email = text.getText().toString();
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    // making sure no one is logged in
    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            sendUserToHomeActivity();
        }else{

        }
    }
}