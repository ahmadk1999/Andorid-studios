package com.example.androidlabproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TenantProfilePage extends AppCompatActivity {

    TextView tenantProfileName, tenantProfileEmail, tenantProfilePhoneNumber, tenantProfileCountry, tenantProfileGender,tenantProfileSalary,tenantProfileOccupation,tenantProfileFamilySize;
    EditText editEmailEditText,editFirstNameEditText,editLastNameEditText,editPasswordEditText,editConfirmPasswordEditText,editGrossMonthlySalaryEditText;
    EditText editFamilySizeEditText,editOccupationEditText,editPhoneNumberEditText;
    Button homeButton,editProfile,tenantCancel,tenantUpdate, viewApplications,viewTenantHistory;
    ProgressBar progressBarTenantEdit;
    String selectedCountry, selectedCity, currentPassword,regex;


    FirebaseAuth mAuth;
    FirebaseFirestore mStore;
    String tenantID,areaCode;


    String email;
    static final String tenants = "Tenant";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_profile_page);

        Intent intent = getIntent();
        email = intent.getStringExtra("Email");

        ConstraintLayout tenantProfilePage = findViewById(R.id.Show_Tenant_Profile_Page);
        tenantProfilePage.setVisibility(View.VISIBLE);

        LinearLayout editTenantProfileLayout = findViewById(R.id.Edit_Tenant_Profile);
        editTenantProfileLayout.setVisibility(View.GONE);


// Profile Page
        tenantProfileName = findViewById(R.id.profile_tenant_name);
        tenantProfileEmail = findViewById(R.id.profile_tenant_email);
        tenantProfilePhoneNumber = findViewById(R.id.profile_tenant_phone_number);
        tenantProfileCountry = findViewById(R.id.profile_tenant_country_city);
        tenantProfileGender = findViewById(R.id.profile_tenant_gender);
        tenantProfileSalary = findViewById(R.id.profile_tenant_gross_monthly_salary);
        tenantProfileOccupation = findViewById(R.id.profile_tenant_occupation);
        tenantProfileFamilySize = findViewById(R.id.profile_tenant_family_size);


        viewApplications = findViewById(R.id.tenatn_view_applications);
        homeButton = findViewById(R.id.home_button);
        editProfile = findViewById(R.id.edit_tenant_profile);
        viewTenantHistory = findViewById(R.id.view_profile_history);

//Edit Information

        progressBarTenantEdit = findViewById(R.id.progressBarTenantEdit);
        progressBarTenantEdit.setVisibility(View.GONE);

        editEmailEditText = (EditText) findViewById(R.id.tenantEmailAddressEdit);
        editFirstNameEditText = (EditText) findViewById(R.id.editFirstName);
        editLastNameEditText = (EditText) findViewById(R.id.editLastName);

        editPasswordEditText = findViewById(R.id.editTenantPassword);
        editConfirmPasswordEditText = findViewById(R.id.editTenantConfirmPassword);

        editGrossMonthlySalaryEditText = findViewById(R.id.editGrossMonthlySalary);
        editFamilySizeEditText = findViewById(R.id.editFamilySize);
        editOccupationEditText = findViewById(R.id.editOccupation);
        editPhoneNumberEditText = findViewById(R.id.editTenantPhoneNumber);

        tenantCancel = findViewById(R.id.tenant_update_cancel2);
        tenantUpdate = findViewById(R.id.tenant_update_profile2);



        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        tenantID = mAuth.getCurrentUser().getUid();

        DocumentReference df = mStore.collection("Tenant").document(tenantID);
        df.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                selectedCountry = value.getString("Current Residence Country") ;
                selectedCity = value.getString("City") ;


                tenantProfileName.setText(value.getString("firstName") + " " + value.getString("lastName"));
                tenantProfileEmail.setText(value.getString("Email"));
                tenantProfilePhoneNumber.setText(value.getString("PhoneNumber"));
                tenantProfileCountry.setText(value.getString("Current Residence Country") + ", " + value.getString("City") );
                tenantProfileGender.setText(value.getString("Gender"));
                tenantProfileSalary.setText(value.getString("grossMonthlySalary") + " $/Month");
                tenantProfileOccupation.setText("Occupation: " + value.getString("Occupation"));
                tenantProfileFamilySize.setText("Family Size: " + value.getString("Family Size"));
                currentPassword = value.getString("Password");

            }
        });

        viewTenantHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToUsersHistory();
            }
        });

        viewApplications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToViewApplicationActivity();
            }
        });

        tenantCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBarTenantEdit.setVisibility(View.GONE);
                tenantProfilePage.setVisibility(View.VISIBLE);
                editTenantProfileLayout.setVisibility(View.GONE);

            }
        });

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tenantProfilePage.setVisibility(View.GONE);
                editTenantProfileLayout.setVisibility(View.VISIBLE);
                updateTenantInfo();

            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToHomeActivity();
            }

        });

    }

    private void updateTenantInfo() {

        DocumentReference df2 = mStore.collection("Tenant").document(tenantID);
        df2.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                editFirstNameEditText.setText(value.getString("firstName"));
                editLastNameEditText.setText(value.getString("lastName"));
                editEmailEditText.setText(value.getString("Email"));
                editPhoneNumberEditText.setText(value.getString("PhoneNumber").substring(4));

                editGrossMonthlySalaryEditText.setText(value.getString("grossMonthlySalary"));
                editFamilySizeEditText.setText(value.getString("Family Size"));
                editOccupationEditText.setText(value.getString("Occupation"));

                editPasswordEditText.getText().clear();
                editPasswordEditText.setHint("Enter Password");
                editConfirmPasswordEditText.getText().clear();
                editConfirmPasswordEditText.setHint("Confirm Password");


            }
        });

        tenantUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                regex = "^(?=.*[0-9])"
                        +"(?=.*[a-z])"
                        +"(?=.*[A-Z])"
                        +"(?=.*[{}@#$%!])"
                        +"(?=\\S+$).{8,15}$";
                // No spaces (?=\S+$)
                boolean isTenantFirstNameValid = true,isTenantLastNameValid = true, isTenantEmailValid= true, isTenantOccupationValid = true;
                boolean isTenantPasswordValid = true, isTenantPasswordConfirmationValid = true, isPhoneValid = true,isRealPassword = false;

// Email Validation
                if (isEmail(editEmailEditText) == false) {
                    editEmailEditText.setError("Enter valid email!");
                    isTenantEmailValid = false;
                    editEmailEditText.requestFocus();
                }else {
                    isTenantEmailValid = true;
                }

// First name Validation
                if (isEmpty(editFirstNameEditText)) {
                    editFirstNameEditText.setError("First name is required!");
                    isTenantFirstNameValid = false;
                } else if (editFirstNameEditText.getText().length() > 3 && editFirstNameEditText.getText().length() < 20){
                    isTenantFirstNameValid = true;
                }
// Last name Validation
                if (isEmpty(editLastNameEditText)) {
                    editLastNameEditText.setError("Last name is required!");
                    isTenantLastNameValid = false;
                }else if (editLastNameEditText.getText().length() > 3 && editLastNameEditText.getText().length() < 20){
                    isTenantLastNameValid = true;
                }
// Password Validation
                if (isEmpty(editPasswordEditText)) {
                    editPasswordEditText.setError("Password is required!");
                    isTenantPasswordValid = false;
                }else {
                    Pattern p = Pattern.compile(regex);
                    Matcher m = p.matcher(editPasswordEditText.getText().toString());
                    if(m.matches()){
                        isTenantPasswordValid = true;//m.matches();
                    }/*else{
                        isTenantPasswordValid = m.matches();
                        editPasswordEditText.setError("Password must be minimum 8 characters and maximum 15 characters. It must contain at least one number,\n" +
                                "one lowercase letter, one uppercase letter, and at least one special character from this character set\n" +
                                "only: $, %, #, @, !, {, and }.");
                    }*/
                }
// Password Confirmation Validation
                if (isEmpty(editConfirmPasswordEditText)) {
                    editConfirmPasswordEditText.setError("Password confirmation is required!");
                    isTenantPasswordConfirmationValid = false;
                }else if (editConfirmPasswordEditText.getText().toString().equals(editPasswordEditText.getText().toString())) {

                    isTenantPasswordConfirmationValid = true;
                }else {
                    editConfirmPasswordEditText.setError("Those passwords did not match. Try again!");
                    isTenantPasswordConfirmationValid = false;

                }
// Occupation Validation
                if (isEmpty(editOccupationEditText)) {
                    editOccupationEditText.setError("Occupation is required!");
                    isTenantOccupationValid = false;
                }else if (editOccupationEditText.getText().length() > 20){
                    editOccupationEditText.setError("Occupation must be under 20 characters!");
                    isTenantOccupationValid = false;
                }else if (editOccupationEditText.getText().length() < 20){
                    isTenantOccupationValid = true;
                }

// Confirmed if it is the true password
                if (editPasswordEditText.getText().toString().equals(currentPassword)){
                    isRealPassword = true;
                }else{
                    Toast.makeText(getApplicationContext(), "Please Enter The Old Password!", Toast.LENGTH_SHORT).show();
                }

                if (isTenantFirstNameValid && isTenantLastNameValid && isTenantPasswordValid && isTenantPasswordConfirmationValid
                        && isPhoneValid &&  isTenantOccupationValid && isRealPassword && isTenantEmailValid){


                    progressBarTenantEdit.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), "Updated Successfully", Toast.LENGTH_SHORT).show();

                    Map<String,Object> updatedTenantInfo = new HashMap<>();
                    updatedTenantInfo.put("Email",editEmailEditText.getText().toString());
                    updatedTenantInfo.put("firstName",editFirstNameEditText.getText().toString());
                    updatedTenantInfo.put("lastName",editLastNameEditText.getText().toString());
                    updatedTenantInfo.put("grossMonthlySalary",editGrossMonthlySalaryEditText.getText().toString());
                    updatedTenantInfo.put("Occupation",editOccupationEditText.getText().toString());
                    updatedTenantInfo.put("Family Size",editFamilySizeEditText.getText().toString());

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
                    updatedTenantInfo.put("PhoneNumber",areaCode + editPhoneNumberEditText.getText().toString());
                    /*updatedAgencyInfo.put("Country",selectedCountry).getText().toString();
                    updatedAgencyInfo.put("City",selectedCity.getText().toString());*/


                    mStore.collection("Tenant").document(mAuth.getUid()).update(updatedTenantInfo);
                    progressBarTenantEdit.setVisibility(View.GONE);
                    sendUserToProfilePage();
                }
            }
        });



    }

    private boolean isEmpty(EditText text) {
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }

    boolean isEmail(EditText text) {
        CharSequence email = text.getText().toString();
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    private void sendToUsersHistory() {
        finish();
        startActivity(new Intent(TenantProfilePage.this, usersHistory.class));
    }


    private void sendToHomeActivity(){
        finish();
        startActivity(new Intent(TenantProfilePage.this, listProperties.class));
    }

    private void sendToViewApplicationActivity(){
        finish();
        startActivity(new Intent(TenantProfilePage.this, viewApplication.class));
    }

    private void sendUserToProfilePage() {
        finish();
        startActivity(new Intent(TenantProfilePage.this, TenantProfilePage.class));
    }
}