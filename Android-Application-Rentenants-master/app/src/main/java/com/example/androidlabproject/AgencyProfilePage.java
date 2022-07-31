package com.example.androidlabproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class AgencyProfilePage extends AppCompatActivity {

    TextView agencyProfileName, agencyProfileEmail, agencyProfilePhoneNumber, agencyProfileCountry;
    EditText agencyPasswordEdit, agencyConfirmPasswordEdit,agencyEditProfileName, agencyEditProfileEmail, agencyEditProfilePhoneNumber;
    Spinner countrySpinnerAgencyEdit, citySpinnerAgencyEdit;
    ImageView agencyProfilePicture;
    Button homeButton,addProperty, agencyEditProfile, updateAgencyProfile, cancelUpdateAgencyProfile, viewRentalApplications;
    Button viewAgencyHistory,viewAgencyProperties;

    String selectedCountry, selectedCity, currentPassword;

    ProgressBar editAgencyProgressBar;

    FirebaseAuth mAuth;
    FirebaseFirestore mStore;
    String agencyID,areaCode;

    String email;
    static final String AGENCIES = "Agency";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agency_profile_page);

        Intent intent = getIntent();
        email = intent.getStringExtra("Email");

        ConstraintLayout profileLayout = findViewById(R.id.agencyProfilePage);
        profileLayout.setVisibility(View.VISIBLE);
        // Profile Page
        agencyProfileName = findViewById(R.id.profile_agency_name);
        agencyProfileEmail = findViewById(R.id.profile_agency_email);
        agencyProfilePhoneNumber = findViewById(R.id.profile_agency_phone_number);
        agencyProfileCountry = findViewById(R.id.profile_agency_country_city);
        agencyProfilePicture = findViewById(R.id.profile_agency_image);

        homeButton = findViewById(R.id.home_button);
        addProperty = findViewById(R.id.add_property_agency_profile);
        agencyEditProfile = findViewById(R.id.edit_profile_agency);
        viewRentalApplications = findViewById(R.id.agency_view_rental_applications);
        viewAgencyHistory = findViewById(R.id.view_agency_history);
        viewAgencyProperties = findViewById(R.id.view_agency_properties);


        // Edit Information
        ConstraintLayout editLayout = findViewById(R.id.linearLayout2_agency_edit);
        editLayout.setVisibility(View.GONE);
        editAgencyProgressBar = (ProgressBar) findViewById(R.id.progressBarAgencyEdit);
        editAgencyProgressBar.setVisibility(View.GONE);

        agencyEditProfileName = findViewById(R.id.agencyName_edit);
        agencyEditProfileEmail = findViewById(R.id.agencyEmailAddress_edit);
        agencyEditProfilePhoneNumber = findViewById(R.id.agencyPhone_edit);
        agencyPasswordEdit = findViewById(R.id.agencyPassword_edit);
        agencyConfirmPasswordEdit = findViewById(R.id.agencyConfirmPassword_edit);
        countrySpinnerAgencyEdit = findViewById(R.id.country_spinner_agency_edit);
        citySpinnerAgencyEdit = findViewById(R.id.city_spinner_agency_edit);
        updateAgencyProfile = findViewById(R.id.agency_update_profile);
        cancelUpdateAgencyProfile = findViewById(R.id.agency_update_cancel);

        countrySpinnerAgencyEdit.setVisibility(View.GONE);
        citySpinnerAgencyEdit.setVisibility(View.GONE);


        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        agencyID = mAuth.getCurrentUser().getUid();

        viewAgencyProperties.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToAgencyViewProperties();
            }
        });

        viewAgencyHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToUsersHistory();
            }
        });

        viewRentalApplications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToViewApplicationsActivity();
            }
        });

        addProperty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("addPropertyButton" , "addPropertyButton");
                sendToAddPropertyActivity();
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToHomeActivity();
            }
        });

        DocumentReference df = mStore.collection("Agency").document(agencyID);
        df.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                agencyProfileName.setText(value.getString("AgencyName"));
                agencyProfileEmail.setText(value.getString("Email"));
                agencyProfilePhoneNumber.setText(value.getString("PhoneNumber"));
                currentPassword = value.getString("Password");
                selectedCountry = value.getString("Country") ;
                selectedCity = value.getString("City") ;

                agencyProfileCountry.setText(value.getString("Country") + ", " + value.getString("City"));


            }
        });

        agencyEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(AgencyProfilePage.this ,"Updated Successfully", Toast.LENGTH_LONG).show();
                profileLayout.setVisibility(View.GONE);
                editLayout.setVisibility(View.VISIBLE);
                updateAgencyInfo();

            }

        });

        updateAgencyProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean isAgencyNameValid = true, isAgencyEmailValid= true,isAgencyPasswordValid = false, isRealPassword = false;
                boolean isAgencyPasswordConfirmationValid = true, isPhoneValid = true, isCountryValid = true, isCityValid = true;
// Email Validation
                if (isEmail(agencyEditProfileEmail) == false) {
                    agencyEditProfileEmail.setError("Enter valid email!");
                    isAgencyEmailValid = false;
                    agencyEditProfileEmail.requestFocus();
                }else {
                    isAgencyEmailValid = true;
                }
// agency name Validation
                if (isEmpty(agencyEditProfileName)) {
                    agencyEditProfileName.setError("Agency name is required!");
                    isAgencyNameValid = false;
                    agencyEditProfileName.requestFocus();
                } else if (agencyEditProfileName.getText().length() > 20){
                    agencyEditProfileName.setError("Agency name must be under 20 characters!");
                    isAgencyNameValid = false;
                    agencyEditProfileName.requestFocus();
                }else if (agencyEditProfileName.getText().length() < 20){
                    isAgencyNameValid = true;
                }

// Password Validation
                if (isEmpty(agencyPasswordEdit)) {
                    agencyPasswordEdit.setError("Password is required!");
                    isAgencyPasswordValid = false;
                    agencyPasswordEdit.requestFocus();
               /*}else if (agencyPasswordEdit.getText().length() < 8 && agencyPasswordEdit.getText().length() > 15){
                    agencyPasswordEdit.setError("Password must be between 8 and 15 characters long!!");
                    isAgencyPasswordValid = false;
                    agencyPasswordEdit.requestFocus();*/
                }else {
                    isAgencyPasswordValid = true;
                }

// Password Confirmation Validation
                if (isEmpty(agencyConfirmPasswordEdit)) {
                    agencyConfirmPasswordEdit.setError("Password confirmation is required!");
                    agencyConfirmPasswordEdit.requestFocus();
                    isAgencyPasswordConfirmationValid = false;
                /*}else if (agencyConfirmPasswordEdit.getText().length() < 8 && agencyConfirmPasswordEdit.getText().length() > 15){
                    agencyConfirmPasswordEdit.setError("Password must be between 8 and 15 characters long!");
                    isAgencyPasswordConfirmationValid = false;
                    agencyConfirmPasswordEdit.requestFocus();*/
                }else if (agencyConfirmPasswordEdit.getText().toString().equals(agencyPasswordEdit.getText().toString())) {
                    isAgencyPasswordConfirmationValid = true;
                }else {
                    agencyConfirmPasswordEdit.setError("Those passwords did not match. Try again!");
                    isAgencyPasswordConfirmationValid = false;
                    agencyConfirmPasswordEdit.requestFocus();
                }
// Phone Number Validation
                if (agencyEditProfilePhoneNumber.getText().toString().isEmpty()) {
                    agencyEditProfilePhoneNumber.setError("Phone number is required!");
                    isPhoneValid = false;
                } else  {
                    isPhoneValid = true;
                }
// Confirmed if it is the true password
                if (agencyPasswordEdit.getText().toString().equals(currentPassword)){
                    isRealPassword = true;
                }else{
                    Toast.makeText(getApplicationContext(), "Please Enter The Old Password!", Toast.LENGTH_SHORT).show();
                }
                if (isAgencyNameValid && isAgencyEmailValid && isAgencyPasswordValid && isAgencyPasswordConfirmationValid
                        && isPhoneValid &&  isCountryValid &&  isCityValid && isRealPassword){


                    editAgencyProgressBar.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), "Updated Successfully", Toast.LENGTH_SHORT).show();

                    Map<String,Object> updatedAgencyInfo = new HashMap<>();
                    updatedAgencyInfo.put("Email",agencyEditProfileEmail.getText().toString());
                    updatedAgencyInfo.put("AgencyName",agencyEditProfileName.getText().toString());
                    updatedAgencyInfo.put("Password",agencyPasswordEdit.getText().toString());
                    switch (selectedCountry){
                        case "China":
                            areaCode = "0086";
                            break;
                        case "India":
                            areaCode = "0091";
                            break;
                        case "Brazil":
                            areaCode = "0055";
                            break;
                        case "Russia":
                            areaCode = "0007";
                            break;
                        case "Japan":
                            areaCode = "0081";
                            break;
                        case "Italy":
                            areaCode = "0039";
                            break;
                        default: break;
                    }
                    updatedAgencyInfo.put("PhoneNumber",areaCode + agencyEditProfilePhoneNumber.getText().toString());
                    mStore.collection("Agency").document(mAuth.getUid()).update(updatedAgencyInfo);
                    editAgencyProgressBar.setVisibility(View.GONE);
                    sendUserToProfilePage();
                }
            }
        });

        cancelUpdateAgencyProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileLayout.setVisibility(View.VISIBLE);
                editLayout.setVisibility(View.GONE);
            }
        });

    }

    private void updateAgencyInfo() {

        DocumentReference df2 = mStore.collection("Agency").document(agencyID);
        df2.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                agencyEditProfileName.setText(value.getString("AgencyName"));
                agencyEditProfileEmail.setText(value.getString("Email"));
                if (value.getString("PhoneNumber").length() > 5){
                    agencyEditProfilePhoneNumber.setText(value.getString("PhoneNumber").substring(4));
                }else{
                    agencyEditProfilePhoneNumber.setText(value.getString("PhoneNumber"));
                }

                agencyPasswordEdit.getText().clear();
                agencyPasswordEdit.setHint("Enter Password");
                agencyConfirmPasswordEdit.getText().clear();
                agencyConfirmPasswordEdit.setHint("Confirm Password");


            }
        });
    }

    private void sendToUsersHistory() {
        finish();
        startActivity(new Intent(AgencyProfilePage.this, usersHistory.class));
    }

    private void sendToAddPropertyActivity() {
        finish();
        startActivity(new Intent(AgencyProfilePage.this, AddProperty.class));
    }

    private void sendToAgencyViewProperties() {
        finish();
        startActivity(new Intent(AgencyProfilePage.this, agencyViewProperties.class));
    }

    private void sendToViewApplicationsActivity() {
        finish();
        startActivity(new Intent(AgencyProfilePage.this, viewApplication.class));
    }

    private boolean isEmpty(EditText text) {
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }

    boolean isEmail(EditText text) {
        CharSequence email = text.getText().toString();
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    private void sendUserToProfilePage() {
        finish();
        startActivity(new Intent(AgencyProfilePage.this, AgencyProfilePage.class));
    }

    private void sendToHomeActivity(){
        finish();
        startActivity(new Intent(AgencyProfilePage.this, listProperties.class));
    }


}