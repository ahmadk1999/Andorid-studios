package com.example.androidlabproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class agencyViewProperties extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    FirebaseFirestore mStore;
    String UID;

    public static final String PREFS_NAME = "MyPrefsFile2";
    private static final String PREF_PROPERTY = "propertyID";
    SharedPrefManager sharedPrefManager;

    Button profileButton,logOutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agency_view_properties);

        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        mUser = mAuth.getCurrentUser();
        UID = mUser.getUid();

        sharedPrefManager = SharedPrefManager.getInstance(this);

        fillProperties();

        profileButton = findViewById(R.id.agency_properties_layout_profile);
        logOutButton = findViewById(R.id.agency_properties_layout_logout);

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(agencyViewProperties.this, "GoodBye!", Toast.LENGTH_LONG).show();
                finish();
                sendToHomeActivity();

            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendAgencyToProfileActivity();
            }
        });

    }

    public void fillProperties() {

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.agency_properties);
        linearLayout.removeAllViews();
        Task<QuerySnapshot> df2 = mStore.collection("properties").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int counter = 0;
                    List<String> properties = new ArrayList<>();
                    for(QueryDocumentSnapshot document : task.getResult()){
                        if (UID.equals(document.getString("Agency ID"))){
                            counter = counter + 1;
                            properties.add(document.getId());
                            TextView listPropertyAvailableDate = new TextView(agencyViewProperties.this);
                            listPropertyAvailableDate.setTextColor(getResources().getColor(R.color.colorWhite));
                            listPropertyAvailableDate.setTextSize(20);
                            listPropertyAvailableDate.setTypeface(listPropertyAvailableDate.getTypeface(), Typeface.BOLD);
                            listPropertyAvailableDate.setText("Available Date: " + document.getString("availabilityDate"));

                            TextView listPropertyRentalPrice = new TextView(agencyViewProperties.this);
                            listPropertyRentalPrice.setTextColor(getResources().getColor(R.color.colorWhite));
                            listPropertyRentalPrice.setTextSize(20);
                            listPropertyRentalPrice.setText("Rental Price: " + document.getString("rentalPrice"));

                            Button viewProperty = new Button(agencyViewProperties.this);
                            viewProperty.setTextSize(20);
                            viewProperty.setText("View");
                            viewProperty.setBackgroundResource(R.drawable.button_background);
                            viewProperty.setTransformationMethod(null);
                            viewProperty.setWidth(20);
                            viewProperty.setHeight(20);

                            viewProperty.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    sendToViewProperty();
                                    getSharedPreferences(PREFS_NAME,MODE_PRIVATE).edit().putString(PREF_PROPERTY,document.getId()).commit();
                                    Toast.makeText(agencyViewProperties.this, "Clicked", Toast.LENGTH_LONG).show();

                                }
                            });

                            linearLayout.addView(listPropertyAvailableDate);
                            linearLayout.addView(listPropertyRentalPrice);
                            linearLayout.addView(viewProperty);
                        }

                    }
                    if (counter == 0){
                        Toast.makeText(agencyViewProperties.this, "No Properties were found :(", Toast.LENGTH_LONG).show();

                        Button addProperty = new Button(agencyViewProperties.this);
                        addProperty.setTextSize(20);
                        addProperty.setText("Add Property");
                        addProperty.setBackgroundResource(R.drawable.button_background);
                        addProperty.setTransformationMethod(null);
                        addProperty.setWidth(20);
                        addProperty.setHeight(20);

                        addProperty.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                sendToAddPropertyActivity();

                            }
                        });

                        linearLayout.addView(addProperty);
                    }


                }
            }

        });
    }

    private void sendAgencyToProfileActivity() {
        finish();
        startActivity(new Intent(agencyViewProperties.this, AgencyProfilePage.class));
    }

    private void sendToAddPropertyActivity() {
        finish();
        startActivity(new Intent(agencyViewProperties.this, AddProperty.class));
    }

    private void sendToHomeActivity(){
        finish();
        startActivity(new Intent(agencyViewProperties.this, listProperties.class));
    }

    private void sendToViewProperty() {
        finish();
        startActivity(new Intent(agencyViewProperties.this, viewProperty.class));
    }

}