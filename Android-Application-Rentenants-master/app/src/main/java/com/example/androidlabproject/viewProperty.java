package com.example.androidlabproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class viewProperty extends AppCompatActivity  {


    public static final String PREFS_NAME = "MyPrefsFile2";
    public static final String PREF_PROPERTY = "propertyID";

    private Button homeButton, applyButton,editButton,deleteButton;
    private TextView propertyCity, propertyPostalAddress, propertySurfaceArea, propertyConstructionYear , propertyNumberOfBedrooms, propertyRentalPrice;
    private TextView propertyStatus, propertyAvailableDate, propertyDescription;
    int is_agency;
    ImageView image;

    String UID, AgencyID;
    DatabaseReference databasereference;
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    private  FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private CollectionReference viewRef=mStore.collection("properties");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_property);

        SharedPreferences pref = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        String propertyID = pref.getString(PREF_PROPERTY, null);

        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        mUser = mAuth.getCurrentUser();
        UID = mUser.getUid();

        image = findViewById(R.id.imageView2);
        homeButton = findViewById(R.id.propert_view_home_button);
        applyButton = findViewById(R.id.propert_view_apply_button);
        applyButton.setVisibility(View.GONE);
        editButton = findViewById(R.id.propert_view_edit_button);
        editButton.setVisibility(View.GONE);
        deleteButton = findViewById(R.id.propert_view_delete_button);
        deleteButton.setVisibility(View.GONE);

        CheckBox viewPropertyIsFurnished = findViewById(R.id.view_propery_furnished);
        CheckBox viewPropertyHaveGarden = findViewById(R.id.view_propery_garden);
        CheckBox viewPropertyHaveBalcony = findViewById(R.id.view_propery_balcony);


        propertyCity = findViewById(R.id.propert_city);
        propertyPostalAddress = findViewById(R.id.propert_address);
        propertySurfaceArea = findViewById(R.id.propert_area);
        propertyConstructionYear = findViewById(R.id.propert_construction);
        propertyNumberOfBedrooms = findViewById(R.id.propert_bedrooms);
        propertyRentalPrice = findViewById(R.id.propert_price);
        propertyStatus = findViewById(R.id.propert_status);
        propertyAvailableDate = findViewById(R.id.propert_available);
        propertyDescription = findViewById(R.id.propert_description);

        CheckUserType(UID);

        if(propertyID != null){
            DocumentReference df2 = mStore.collection("properties").document(propertyID);
            df2.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                    AgencyID = value.getString("Agency ID");
                    if (UID.equals(AgencyID)){
                        editButton.setVisibility(View.VISIBLE);
                        deleteButton.setVisibility(View.VISIBLE);
                    }else{
                        editButton.setVisibility(View.GONE);
                        deleteButton.setVisibility(View.GONE);
                    }

                    if (value.getData() != null){
                        if(value.getString("Furnished").equals("true") && value.getString("Furnished") != null){
                            viewPropertyIsFurnished.setChecked(true);
                        }else{
                            viewPropertyIsFurnished.setChecked(false);
                        }
                        if(value.getString("haveGarden").equals("true") && value.getString("Furnished") != null){
                            viewPropertyHaveGarden.setChecked(true);
                        }else{
                            viewPropertyHaveGarden.setChecked(false);
                        }
                        if(value.getString("haveBalcony").equals("true") && value.getString("Furnished") != null){
                            viewPropertyHaveBalcony.setChecked(true);
                        }else{
                            viewPropertyHaveBalcony.setChecked(false);
                        }

                        propertyCity.setText("City: " + value.getString("propertyCity"));
                        propertyPostalAddress.setText("Postal Address: " + value.getString("Postal_Address"));
                        propertySurfaceArea.setText("Surface Area: " + value.getString("surface_area"));
                        propertyConstructionYear.setText("Construction Year: " + value.getString("con_year"));
                        propertyNumberOfBedrooms.setText("Number Of Bedrooms: " + value.getString("bedrooms"));
                        propertyRentalPrice.setText("Rental Price: " + value.getString("rentalPrice"));
                        propertyStatus.setText("Status: " + value.getString("Not Added yet!"));
                        propertyAvailableDate.setText("Available Date: " + value.getString("availabilityDate"));
                        propertyDescription.setText("Description: " + value.getString("Description"));
                    }

                }
            });
        }else{
            Toast.makeText(viewProperty.this, "Error Getting Property, Redirecting to Home Page", Toast.LENGTH_LONG).show();
            sendUserToHomeActivity();
        }

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteProperty(propertyID);
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToAgencyEditProperties();
                getSharedPreferences(PREFS_NAME,MODE_PRIVATE).edit().putString(PREF_PROPERTY,propertyID).commit();
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToHomeActivity();
            }
        });

        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUser == null){
                    Toast.makeText(getApplicationContext(), "Please Login to apply!", Toast.LENGTH_SHORT).show();
                    sendUserToLogInActivity();
                }else{

                    FirebaseFirestore databaseProperty = FirebaseFirestore.getInstance();

                    Map<String,Object> applicationInfo = new HashMap<>();
                    applicationInfo.put("UID", UID);
                    applicationInfo.put("PropertyID", propertyID);
                    applicationInfo.put("AgencyID", AgencyID);
                    applicationInfo.put("Status", "Under Consideration");
                    Toast.makeText(getApplicationContext(), "Applied", Toast.LENGTH_SHORT).show();
                    databaseProperty.collection("Application").add(applicationInfo);
                    sendUserToHomeActivity();
                }


            }
        });
    }

    private void deleteProperty(String propertyID) {
        mStore.collection("properties").document(propertyID).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    getSharedPreferences(PREFS_NAME,MODE_PRIVATE).edit().putString(PREF_PROPERTY,null).commit();
                    Toast.makeText(viewProperty.this, "Property Deleted!, Redirecting to Home Page", Toast.LENGTH_LONG).show();
                    sendUserToHomeActivity();
                    finish();
                }else{
                    Toast.makeText(viewProperty.this, "Error deleting property, please try again later!", Toast.LENGTH_LONG).show();
                }
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
                    is_agency = 1;
                    applyButton.setVisibility(View.GONE);
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
                            is_agency = 0;
                            applyButton.setVisibility(View.VISIBLE);
                        }

                    }
                });
            }
        });

    }

    private void sendToAgencyEditProperties() {
        finish();
        startActivity(new Intent(viewProperty.this, editProperty.class));
    }

    private void sendUserToHomeActivity() {
        finish();
        startActivity(new Intent(viewProperty.this, listProperties.class));
    }

    private void sendUserToLogInActivity() {
        startActivity(new Intent(viewProperty.this, LoginActivity.class));
    }

    }




