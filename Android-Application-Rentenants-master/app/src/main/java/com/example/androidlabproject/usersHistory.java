package com.example.androidlabproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class usersHistory extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseFirestore mStore;
    String currentUID;
    int is_agency,applicationCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_history);

        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        currentUID = mAuth.getCurrentUser().getUid();

        CheckUserType(currentUID);
        Button homePage = findViewById(R.id.history_home_button);
        Button profilePage = findViewById(R.id.history_profile_button);

        homePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToHomeActivity();
            }
        });

        profilePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is_agency == 1) { // agency
                    sendAgencyToProfileActivity();
                } else { //tenant
                    sendTenantToProfile();
                }
            }
        });


    }


    private void sendUserToHomeActivity() {
        finish();
        startActivity(new Intent(usersHistory.this, listProperties.class));
    }

    private void sendTenantToProfile() {
        startActivity(new Intent(usersHistory.this, TenantProfilePage.class));
    }

    private void sendAgencyToProfileActivity() {
        startActivity(new Intent(usersHistory.this, AgencyProfilePage.class));
    }

    private void fillTenantHistory(){
        LinearLayout viewTenantHistory = (LinearLayout) findViewById(R.id.show_history);
        viewTenantHistory.removeAllViews();
        currentUID = mAuth.getCurrentUser().getUid();

        Task<QuerySnapshot> df2 = mStore.collection("Application").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<String> applications = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.getString("UID").equals(currentUID)) {
                            if (!document.getString("Status").equals("Under Consideration")) {
                                applicationCounter = applicationCounter + 1;
                                applications.add(document.getId());

                                TextView applicationStatus = new TextView(usersHistory.this);
                                applicationStatus.setTextColor(getResources().getColor(R.color.colorWhite));
                                applicationStatus.setTextSize(20);
                                applicationStatus.setTypeface(applicationStatus.getTypeface(), Typeface.BOLD);
                                applicationStatus.setText("Status: " + document.getString("Status"));


                                TextView applicationPropertyID = new TextView(usersHistory.this);
                                applicationPropertyID.setTextColor(getResources().getColor(R.color.colorWhite));
                                applicationPropertyID.setTextSize(20);
                                applicationPropertyID.setText("Agency ID: " + document.getString("AgencyID"));

                                viewTenantHistory.addView(applicationStatus);
                                viewTenantHistory.addView(applicationPropertyID);

                            }

                        }

                    }
                    TextView application = new TextView(usersHistory.this);
                    application.setTextColor(getResources().getColor(R.color.colorWhite));
                    application.setTextSize(20);

                    if (applicationCounter != 0) {
                        application.setText("\nFound: " + applicationCounter + " application(s).");
                    } else {
                        application.setText("No applications were found!!");
                    }
                    viewTenantHistory.addView(application);
                } else {
                    TextView applicationStatus = new TextView(usersHistory.this);
                    applicationStatus.setTextColor(getResources().getColor(R.color.colorWhite));
                    applicationStatus.setTypeface(applicationStatus.getTypeface(), Typeface.BOLD);
                    applicationStatus.setText("Error Getting Information!!");
                    viewTenantHistory.addView(applicationStatus);


                }
            }
        });

    }

    private void fillAgencyHistory(){
        LinearLayout viewAgencyHistory = (LinearLayout) findViewById(R.id.show_history);
        viewAgencyHistory.removeAllViews();
        currentUID = mAuth.getCurrentUser().getUid();

        Task<QuerySnapshot> df2 = mStore.collection("Application").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<String> applications = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.getString("AgencyID").equals(currentUID)) {
                            if (!document.getString("Status").equals("Under Consideration")) {
                                applicationCounter = applicationCounter + 1;
                                applications.add(document.getId());

                                TextView applicationStatus = new TextView(usersHistory.this);
                                applicationStatus.setTextColor(getResources().getColor(R.color.colorWhite));
                                applicationStatus.setTextSize(20);
                                applicationStatus.setTypeface(applicationStatus.getTypeface(), Typeface.BOLD);
                                applicationStatus.setText("Status: " + document.getString("Status"));


                                TextView applicationPropertyID = new TextView(usersHistory.this);
                                applicationPropertyID.setTextColor(getResources().getColor(R.color.colorWhite));
                                applicationPropertyID.setTextSize(20);
                                applicationPropertyID.setText("User ID: " + document.getString("UID"));

                                viewAgencyHistory.addView(applicationStatus);
                                viewAgencyHistory.addView(applicationPropertyID);

                            }

                        }

                    }
                    TextView application = new TextView(usersHistory.this);
                    application.setTextColor(getResources().getColor(R.color.colorWhite));
                    application.setTextSize(20);

                    if (applicationCounter != 0) {
                        application.setText("\nFound: " + applicationCounter + " application(s).");
                    } else {
                        application.setText("No applications were found!!");
                    }
                    viewAgencyHistory.addView(application);
                } else {
                    TextView applicationStatus = new TextView(usersHistory.this);
                    applicationStatus.setTextColor(getResources().getColor(R.color.colorWhite));
                    applicationStatus.setTypeface(applicationStatus.getTypeface(), Typeface.BOLD);
                    applicationStatus.setText("Error Getting Information!!");
                    viewAgencyHistory.addView(applicationStatus);

                }
            }
        });

    }

    private void CheckUserType(String uid) {
        DocumentReference agencyDF = mStore.collection("Agency").document(uid);
        agencyDF.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d("TAG", "Agency onSuccess:" + documentSnapshot.getData());
                if (documentSnapshot.getString("isAgency") != null) { // Agency
                    is_agency = 1;
                    Log.d("TAG", "Agency onSuccess2:" + documentSnapshot.getData());
                    fillAgencyHistory();
                }
            }
        });
        Log.d("TAG", "Tenant onSuccess:" +"dsadsadsa");
        DocumentReference tenantDF = mStore.collection("Tenant").document(uid);
        tenantDF.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d("TAG", "Tenant onSuccess:" + documentSnapshot.getData());
                if (documentSnapshot.getString("Nationality") != null) { // Tenant
                    Log.d("TAG", "Tenant onSuccess2:" + documentSnapshot.getData());
                    is_agency = 0;
                    fillTenantHistory();
                }

            }
        });

    }
}