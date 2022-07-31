package com.example.androidlabproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;

import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class editProperty extends AppCompatActivity {

    private ImageView imageToUpload, downloadImage;
    private String City,Description;
    private int postalAddress,surfaceArea,constructionYear,numberOfBedrooms,rentalPrice;
    private Date availabilityDate;
    private boolean Furnished,haveBalcony,haveGarden;
    FirebaseFirestore mStore;
    FirebaseAuth mAuth;

    public static final String PREFS_NAME = "MyPrefsFile2";
    private static final String PREF_PROPERTY = "propertyID";
    SharedPrefManager sharedPrefManager;

    EditText editCityEditText,editDescriptionEditText, editPostalAddressEditText,editSurfaceAreaEditText, editConstructionYearEditText,editRentalPriceEditText;
    EditText editAvailableDateEditText,editBedroomsEditText;
    Button HomePage,propertyCancel,propertyUpdate;
    CheckBox haveGardenCheckBox, isFurnishedCheckBox,haveBalconyCheckBox;
    ProgressBar progressBarPropertyEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_property);

        mStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        sharedPrefManager = SharedPrefManager.getInstance(this);
        SharedPreferences pref = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        String propertyID = pref.getString(PREF_PROPERTY, null);

        progressBarPropertyEdit = findViewById(R.id.edit_property_progress_bar);
        progressBarPropertyEdit.setVisibility(View.GONE);
        //editCityEditText = (EditText) findViewById(R.id.tenantEmailAddressEdit);
        editDescriptionEditText = (EditText) findViewById(R.id.property_description);
        editPostalAddressEditText = (EditText) findViewById(R.id.property_postal_address);
        editSurfaceAreaEditText = findViewById(R.id.property_surface_area);
        editConstructionYearEditText = findViewById(R.id.property_construction_year);
        editRentalPriceEditText = findViewById(R.id.property_rental_price);
        editAvailableDateEditText = findViewById(R.id.edit_property_available_date);
        editBedroomsEditText = findViewById(R.id.edit_property_bedrooms);

        isFurnishedCheckBox = findViewById(R.id.edit_property_furnished);
        haveGardenCheckBox = findViewById(R.id.edit_property_garden);
        haveBalconyCheckBox = findViewById(R.id.edit_property_balcony);

        propertyCancel = findViewById(R.id.edit_property_cancel);
        propertyUpdate = findViewById(R.id.edit_property_update);
        HomePage = findViewById(R.id.home_page_button);

        isFurnishedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(buttonView.isChecked()){
                    Furnished = true;
                }else{
                    Furnished = false;
                }
            }
        });
        haveGardenCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(buttonView.isChecked()){
                    haveGarden = true;
                }else{
                    haveGarden = false;

                }
            }
        });
        haveBalconyCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(buttonView.isChecked()){
                    haveBalcony = true;
                }else{
                    haveBalcony = false;
                }
            }
        });


        DocumentReference df = mStore.collection("properties").document(propertyID);
        df.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                editDescriptionEditText.setText(value.getString("Description"));
                editPostalAddressEditText.setText(value.getString("Postal_Address"));
                editSurfaceAreaEditText.setText(value.getString("surface_area"));
                editConstructionYearEditText.setText(value.getString("con_year"));
                editRentalPriceEditText.setText(value.getString("rentalPrice"));
                editAvailableDateEditText.setText(value.getString("availabilityDate"));
                editBedroomsEditText.setText(value.getString("bedrooms"));


                if(value.getString("Furnished").equals("true") && value.getString("Furnished") != null){
                    isFurnishedCheckBox.setChecked(true);
                }else{
                    isFurnishedCheckBox.setChecked(false);
                }

                if(value.getString("haveBalcony").equals("true")  && value.getString("haveBalcony") != null){
                    haveBalconyCheckBox.setChecked(true);
                }else{
                    haveBalconyCheckBox.setChecked(false);
                }

                if(value.getString("haveGarden").equals("true")  && value.getString("haveGarden") != null){
                    haveGardenCheckBox.setChecked(true);
                }else{
                    haveGardenCheckBox.setChecked(false);
                }

               /* tenantProfileOccupation.setText("Occupation: " + value.getString("Occupation"));
                tenantProfileFamilySize.setText("Family Size: " + value.getString("Family Size"));
                currentPassword = value.getString("Password");*/

            }
        });

        propertyUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePropertyInformation(propertyID);
            }
        });

        propertyCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBarPropertyEdit.setVisibility(View.GONE);
                sendAgencyToViewProperty();

            }
        });

        HomePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToHomeActivity();
            }
        });


    }

    private void updatePropertyInformation(String propID) {

        progressBarPropertyEdit.setVisibility(View.VISIBLE);
        if (isEmpty(editDescriptionEditText)) {
            editDescriptionEditText.setError("Description is required!");
        }else if (isEmpty(editPostalAddressEditText)) {
            editPostalAddressEditText.setError("Postal address is required!");
        }else if (isEmpty(editSurfaceAreaEditText)) {
            editSurfaceAreaEditText.setError("Surface area is required!");
        }else if (isEmpty(editConstructionYearEditText)) {
            editConstructionYearEditText.setError("Construction year is required!");
        }else if (isEmpty(editRentalPriceEditText)) {
            editRentalPriceEditText.setError("Rental price is required!");
        }else if (isEmpty(editAvailableDateEditText)) {
            editAvailableDateEditText.setError("Available date is required!");
        }else if (isEmpty(editBedroomsEditText)) {
            editBedroomsEditText.setError("Number of bedrooms is required!");
        }else{
            Toast.makeText(getApplicationContext(), "Updated Successfully", Toast.LENGTH_SHORT).show();
            Map<String,Object> updatedPropertyInfo = new HashMap<>();
            updatedPropertyInfo.put("Description",editDescriptionEditText.getText().toString());
            updatedPropertyInfo.put("Postal_Address",editPostalAddressEditText.getText().toString());
            updatedPropertyInfo.put("surface_area",editSurfaceAreaEditText.getText().toString());
            updatedPropertyInfo.put("con_year",editConstructionYearEditText.getText().toString());
            updatedPropertyInfo.put("rentalPrice",editRentalPriceEditText.getText().toString());
            updatedPropertyInfo.put("availabilityDate",editAvailableDateEditText.getText().toString());
            updatedPropertyInfo.put("bedrooms",editBedroomsEditText.getText().toString());

            if(Furnished){
                updatedPropertyInfo.put("Furnished", "true");
            }else{
                updatedPropertyInfo.put("Furnished", "false");
            }

            if(haveBalcony){
                updatedPropertyInfo.put("haveBalcony", "true");
            }else{
                updatedPropertyInfo.put("haveBalcony", "false");
            }

            if(haveGarden){
                updatedPropertyInfo.put("haveGarden", "true");
            }else{
                updatedPropertyInfo.put("haveGarden", "false");
            }

            progressBarPropertyEdit.setVisibility(View.GONE);
            mStore.collection("properties").document(propID).update(updatedPropertyInfo);
            sendAgencyToViewProperty();
        }

    }

    private boolean isEmpty(EditText text) {
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }

    private void sendAgencyToViewProperty() {
        finish();
        startActivity(new Intent(editProperty.this, viewProperty.class));
    }

    private void sendUserToHomeActivity() {
        finish();
        startActivity(new Intent(editProperty.this, listProperties.class));
    }
}