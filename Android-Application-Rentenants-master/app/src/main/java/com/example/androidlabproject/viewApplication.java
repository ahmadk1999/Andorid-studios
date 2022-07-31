package com.example.androidlabproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class viewApplication extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseFirestore mStore;
    String currentUID;
    LinearLayout linearLayout;
    int applicationCounter = 0;
    int is_agency;

    private static final String MY_CHANNEL_ID = "my_channel_1";
    private static final String MY_CHANNEL_NAME = "My channel";
    private static final int NOTIFICATION_ID = 123;
    private static final String NOTIFICATION_TITLE = "An update on your application";
    private static final String REJECT_NOTIFICATION_BODY = "We are sorry to inform you that your applications had been rejected";
    private static final String APPROVED_NOTIFICATION_BODY = "We gladly inform that your applications has been approved";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_application);

        LinearLayout viewUser = (LinearLayout) findViewById(R.id.viiew_applied_user);
        viewUser.setVisibility(View.GONE);

        LinearLayout viewTenantHistory = (LinearLayout) findViewById(R.id.tenat_history);
        viewTenantHistory.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        currentUID = mAuth.getCurrentUser().getUid();

        CheckUserType(currentUID);
        Button homePage = findViewById(R.id.view_application_home);
        Button profilePage = findViewById(R.id.view_application_profile);

        linearLayout = (LinearLayout) findViewById(R.id.view_application_layout);


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
        startActivity(new Intent(viewApplication.this, listProperties.class));
    }

    private void sendTenantToProfile() {
        startActivity(new Intent(viewApplication.this, TenantProfilePage.class));
    }

    private void sendAgencyToProfileActivity() {
        startActivity(new Intent(viewApplication.this, AgencyProfilePage.class));
    }

    public void fillAgencyApplications() {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.view_application_layout);
        linearLayout.removeAllViews();

        Task<QuerySnapshot> df2 = mStore.collection("Application").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<String> applications = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.getString("AgencyID").equals(currentUID)) {
                            applications.add(document.getId());

                            if (document.getString("Status").equals("Under Consideration")) {
                                applicationCounter = applicationCounter + 1;
                                TextView applicationStatus = new TextView(viewApplication.this);
                                applicationStatus.setTextColor(getResources().getColor(R.color.colorWhite));
                                applicationStatus.setTextSize(20);
                                applicationStatus.setTypeface(applicationStatus.getTypeface(), Typeface.BOLD);
                                applicationStatus.setText("Status: " + document.getString("Status"));

                                TextView applicationPropertyID = new TextView(viewApplication.this);
                                applicationPropertyID.setTextColor(getResources().getColor(R.color.colorWhite));
                                applicationPropertyID.setTextSize(20);
                                applicationPropertyID.setText("Property ID: " + document.getString("PropertyID"));

                                linearLayout.addView(applicationStatus);
                                linearLayout.addView(applicationPropertyID);

                                Button rejectApplication = new Button(viewApplication.this);
                                rejectApplication.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                rejectApplication.setText("Reject");
                                linearLayout.addView(rejectApplication);
                                rejectApplication.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        Map<String, Object> applicationInfo = new HashMap<>();
                                        applicationInfo.put("UID", document.getString("UID"));
                                        applicationInfo.put("PropertyID", document.getString("PropertyID"));
                                        applicationInfo.put("AgencyID", document.getString("AgencyID"));
                                        applicationInfo.put("Status", "Rejected");


                                        mStore.collection("Application").document(document.getId()).update(applicationInfo);
                                        finish();
                                        startActivity(getIntent());
                                        createNotification(NOTIFICATION_TITLE,REJECT_NOTIFICATION_BODY);
                                        Toast.makeText(getApplicationContext(), "Rejected", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                Button approveApplication = new Button(viewApplication.this);
                                approveApplication.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                approveApplication.setText("Approve");
                                linearLayout.addView(approveApplication);
                                approveApplication.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        Map<String, Object> applicationInfo = new HashMap<>();
                                        applicationInfo.put("UID", document.getString("UID"));
                                        applicationInfo.put("PropertyID", document.getString("PropertyID"));
                                        applicationInfo.put("AgencyID", document.getString("AgencyID"));
                                        applicationInfo.put("Status", "Approved");


                                        mStore.collection("Application").document(document.getId()).update(applicationInfo);
                                        finish();
                                        startActivity(getIntent());
                                        createNotification(NOTIFICATION_TITLE,APPROVED_NOTIFICATION_BODY);
                                        Toast.makeText(getApplicationContext(), "Approved", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                Button checkUserInfo = new Button(viewApplication.this);
                                checkUserInfo.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                checkUserInfo.setText("View Tenant");
                                linearLayout.addView(checkUserInfo);
                                checkUserInfo.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        CheckAppliedUserInfo(document.getString("UID"));
                                    }
                                });
                            }
                        }

                    }
                    TextView application = new TextView(viewApplication.this);
                    application.setTextColor(getResources().getColor(R.color.colorWhite));
                    application.setTextSize(20);

                    if (applicationCounter != 0) {
                        application.setText("\nFound: " + applicationCounter + " application(s).");
                    } else {
                        application.setText("No tenant applications were found!!");
                    }
                    linearLayout.addView(application);
                } else {
                    TextView applicationStatus = new TextView(viewApplication.this);
                    applicationStatus.setTextColor(getResources().getColor(R.color.colorWhite));
                    applicationStatus.setTypeface(applicationStatus.getTypeface(), Typeface.BOLD);
                    applicationStatus.setText("Error Getting Information!!");
                    linearLayout.addView(applicationStatus);


                }
            }
        });

    }

    public void createNotification(String title, String body) {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent, 0);
        createNotificationChannel();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, MY_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private void createNotificationChannel() {
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(MY_CHANNEL_ID, MY_CHANNEL_NAME, importance);
        NotificationManager notificationManager =
                getSystemService(NotificationManager.class);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void fillTenantHistory(String applierID){
        LinearLayout viewTenantHistory = (LinearLayout) findViewById(R.id.tenat_history);
        viewTenantHistory.removeAllViews();

        Task<QuerySnapshot> df2 = mStore.collection("Application").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<String> applications = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.getString("UID").equals(applierID)) {
                            if (!document.getString("Status").equals("Under Consideration")) {
                                applicationCounter = applicationCounter + 1;
                                applications.add(document.getId());

                                TextView applicationStatus = new TextView(viewApplication.this);
                                applicationStatus.setTextColor(getResources().getColor(R.color.colorWhite));
                                applicationStatus.setTextSize(20);
                                applicationStatus.setTypeface(applicationStatus.getTypeface(), Typeface.BOLD);
                                applicationStatus.setText("Status: " + document.getString("Status"));


                                TextView applicationPropertyID = new TextView(viewApplication.this);
                                applicationPropertyID.setTextColor(getResources().getColor(R.color.colorWhite));
                                applicationPropertyID.setTextSize(20);
                                applicationPropertyID.setText("Agency ID: " + document.getString("AgencyID"));

                                viewTenantHistory.addView(applicationStatus);
                                viewTenantHistory.addView(applicationPropertyID);

                            }

                        }

                    }
                    TextView application = new TextView(viewApplication.this);
                    application.setTextColor(getResources().getColor(R.color.colorWhite));
                    application.setTextSize(20);

                    if (applicationCounter != 0) {
                        application.setText("\nFound: " + applicationCounter + " application(s).");
                    } else {
                        application.setText("No applications were found!!");
                    }
                    linearLayout.addView(application);
                } else {
                    TextView applicationStatus = new TextView(viewApplication.this);
                    applicationStatus.setTextColor(getResources().getColor(R.color.colorWhite));
                    applicationStatus.setTypeface(applicationStatus.getTypeface(), Typeface.BOLD);
                    applicationStatus.setText("Error Getting Information!!");
                    linearLayout.addView(applicationStatus);


                }
            }
        });

    }

    public void fillTenantApplications() {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.view_application_layout);
        linearLayout.removeAllViews();
        Task<QuerySnapshot> df2 = mStore.collection("Application").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<String> applications = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.getString("UID").equals(currentUID)) {
                            if (document.getString("Status").equals("Under Consideration")) {
                                applicationCounter = applicationCounter + 1;
                                applications.add(document.getId());

                                TextView applicationStatus = new TextView(viewApplication.this);
                                applicationStatus.setTextColor(getResources().getColor(R.color.colorWhite));
                                applicationStatus.setTextSize(20);
                                applicationStatus.setTypeface(applicationStatus.getTypeface(), Typeface.BOLD);
                                applicationStatus.setText("Status: " + document.getString("Status"));

                                TextView applicationPropertyID = new TextView(viewApplication.this);
                                applicationPropertyID.setTextColor(getResources().getColor(R.color.colorWhite));
                                applicationPropertyID.setTextSize(20);
                                applicationPropertyID.setText("Property ID: " + document.getString("PropertyID"));

                                linearLayout.addView(applicationStatus);
                                linearLayout.addView(applicationPropertyID);

                                Button cancelApplication = new Button(viewApplication.this);
                                cancelApplication.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                cancelApplication.setText("Cancel");
                                linearLayout.addView(cancelApplication);
                                cancelApplication.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //Toast.makeText(viewApplication.this, "Cancel Application", Toast.LENGTH_LONG).show();

                                        Map<String, Object> applicationInfo = new HashMap<>();
                                        applicationInfo.put("UID", document.getString("UID"));
                                        applicationInfo.put("PropertyID", document.getString("PropertyID"));
                                        applicationInfo.put("AgencyID", document.getString("AgencyID"));
                                        applicationInfo.put("Status", "Canceled");


                                        mStore.collection("Application").document(document.getId()).update(applicationInfo);
                                        finish();
                                        startActivity(getIntent());

                                        Toast.makeText(getApplicationContext(), "Canceled", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        }
                    }
                    TextView application = new TextView(viewApplication.this);
                    application.setTextColor(getResources().getColor(R.color.colorWhite));
                    application.setTextSize(20);
                    if (applicationCounter != 0) {
                        application.setText("\nFound: " + applicationCounter + " application(s).");
                    } else {
                        application.setText("No property applications were found!!");
                    }
                    linearLayout.addView(application);
                } else {
                    TextView applicationStatus = new TextView(viewApplication.this);
                    applicationStatus.setTextColor(getResources().getColor(R.color.colorWhite));
                    applicationStatus.setTypeface(applicationStatus.getTypeface(), Typeface.BOLD);
                    applicationStatus.setText("Error Getting Information!!");
                    linearLayout.addView(applicationStatus);


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
                    fillAgencyApplications();
                }
            }
        });

        DocumentReference tenantDF = mStore.collection("Tenant").document(uid);
        tenantDF.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d("TAG", "Tenant onSuccess:" + documentSnapshot.getData());
                if (documentSnapshot.getString("Nationality") != null) { // Tenant
                    Log.d("TAG", "Tenant onSuccess2:" + documentSnapshot.getData());
                    is_agency = 0;
                    fillTenantApplications();
                }

            }
        });


    }

    private void CheckAppliedUserInfo(String applierID) {

        DocumentReference df2 = mStore.collection("Tenant").document(applierID);
        df2.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                linearLayout.setVisibility(View.GONE);
                LinearLayout viewTenantHistory = (LinearLayout) findViewById(R.id.tenat_history);
                viewTenantHistory.setVisibility(View.GONE);

                LinearLayout viewUser = (LinearLayout) findViewById(R.id.viiew_applied_user);
                viewUser.setVisibility(View.VISIBLE);

                TextView tenantProfileName = findViewById(R.id.profile_tenant_name2);
                TextView tenantProfileEmail = findViewById(R.id.profile_tenant_email2);
                TextView tenantProfilePhoneNumber = findViewById(R.id.profile_tenant_phone_number2);
                TextView tenantProfileCountry = findViewById(R.id.profile_tenant_country_city2);
                TextView tenantProfileGender = findViewById(R.id.profile_tenant_gender2);
                TextView tenantProfileSalary = findViewById(R.id.profile_tenant_gross_monthly_salary2);
                TextView tenantProfileOccupation = findViewById(R.id.profile_tenant_occupation2);
                TextView tenantProfileFamilySize = findViewById(R.id.profile_tenant_family_size2);
                Button backToApplications = findViewById(R.id.buton_back_to_applications);
                Button viewHistory = findViewById(R.id.view_tenant_history);

                tenantProfileName.setText(value.getString("firstName") + " " + value.getString("lastName"));
                tenantProfileEmail.setText(value.getString("Email"));
                tenantProfilePhoneNumber.setText(value.getString("PhoneNumber").substring(4));

                tenantProfileSalary.setText("Salary: " + value.getString("grossMonthlySalary"));
                tenantProfileFamilySize.setText("Family Size: " + value.getString("Family Size"));
                tenantProfileOccupation.setText("Occupation: " + value.getString("Occupation"));
                tenantProfileGender.setText(value.getString("Gender"));
                tenantProfileCountry.setText("Current Residence Country: " + value.getString("Current Residence Country") + ", " + value.getString("City"));

                backToApplications.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewUser.setVisibility(View.GONE);
                        viewTenantHistory.setVisibility(View.GONE);
                        linearLayout.setVisibility(View.VISIBLE);
                    }
                });

                viewHistory.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewUser.setVisibility(View.GONE);
                        linearLayout.setVisibility(View.GONE);
                        viewTenantHistory.setVisibility(View.VISIBLE);
                        fillTenantHistory(applierID);


                    }
                });

            }
        });

    }


}