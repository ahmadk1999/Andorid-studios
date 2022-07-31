package com.example.androidlabproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class tenantSignup extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    FirebaseFirestore mStore;

    String regex,areaCode;
    private Spinner tenantNationalitySpinner,tenantCitySpinner,currentResidenceCountrySpinner,tenantGenderSpinner;
    private ArrayAdapter<CharSequence> currentResidenceCountryAdapter,tenantCityAdapter,tenantNationalityAdapter,genderAdapter;
    private String tenantSelectedCountry, tenantSelectedCity,selectedTenantNationality,selectedGender;

    public tenantSignup() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_signup);

        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();


        String[] options = { "Male", "Female" };
        Spinner genderSpinner =(Spinner) findViewById(R.id.gender_spinner);
        ArrayAdapter<String> objGenderArr = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, options);
        genderSpinner.setAdapter(objGenderArr);

        ProgressBar tenantProgressBar = (ProgressBar) findViewById(R.id.progressBarTenantRegister);
        tenantProgressBar.setVisibility(View.INVISIBLE);

        EditText emailEditText = (EditText) findViewById(R.id.tenantEmailAddress);
        EditText firstNameEditText = (EditText) findViewById(R.id.firstName);
        EditText lastNameEditText = (EditText) findViewById(R.id.lastName);

        EditText passwordEditText = findViewById(R.id.tenantPassword);
        EditText confirmPasswordEditText = findViewById(R.id.tenantConfirmPassword);

        EditText familySizeEditText = findViewById(R.id.familySize);
        EditText occupationEditText = findViewById(R.id.occupation);
        EditText grossMonthlySalaryEditText = findViewById(R.id.grossMonthlySalary);
        EditText phoneNumberEditText = findViewById(R.id.tenantPhoneNumber);

        final Button tenantSignup = findViewById(R.id.tenant_signup);
        final String TOAST_TEXT = "You must enter first name to register!";
        TextView logIn = findViewById(R.id.textviewLogin);

        tenantNationalitySpinner =(Spinner) findViewById(R.id.nationality_spinner);
        tenantNationalityAdapter = ArrayAdapter.createFromResource(this, R.array.array_nationalities, R.layout.spinner_layouy);
        tenantNationalityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tenantNationalitySpinner.setAdapter(tenantNationalityAdapter);

        tenantNationalitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedTenantNationality = tenantNationalitySpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        tenantGenderSpinner =(Spinner) findViewById(R.id.gender_spinner);
        genderAdapter = ArrayAdapter.createFromResource(this, R.array.array_gender, R.layout.spinner_layouy);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tenantGenderSpinner.setAdapter(genderAdapter);

        tenantGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedGender = tenantGenderSpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        currentResidenceCountrySpinner =(Spinner) findViewById(R.id.current_residence_country_spinner);
        currentResidenceCountryAdapter = ArrayAdapter.createFromResource(this, R.array.array_countries, R.layout.spinner_layouy);
        currentResidenceCountryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currentResidenceCountrySpinner.setAdapter(currentResidenceCountryAdapter);

        currentResidenceCountrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tenantCitySpinner = (Spinner) findViewById(R.id.tenant_city_spinner);
                tenantSelectedCountry = currentResidenceCountrySpinner.getSelectedItem().toString();

                int parentID = parent.getId();
                if (parentID == R.id.current_residence_country_spinner){
                    switch (tenantSelectedCountry){
                        case "Please select a country": tenantCityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_default_cities,R.layout.spinner_layouy);
                            break;
                        case "China": tenantCityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.China1,R.layout.spinner_layouy);
                                areaCode = "0086";
                            break;
                        case "India": tenantCityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.India1,R.layout.spinner_layouy);
                                areaCode = "0091";
                            break;
                        case "Brazil": tenantCityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.Brazil1,R.layout.spinner_layouy);
                                areaCode = "0055";
                            break;
                        case "Russia": tenantCityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.Russia1,R.layout.spinner_layouy);
                                areaCode = "0007";
                            break;
                        case "Japan": tenantCityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.Japan1,R.layout.spinner_layouy);
                                areaCode = "0081";
                            break;
                        case "Italy": tenantCityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.Italy1,R.layout.spinner_layouy);
                                areaCode = "0039";
                            break;
                        default: break;
                    }
                    tenantCityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    tenantCitySpinner.setAdapter(tenantCityAdapter);

                    tenantCitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            tenantSelectedCity = tenantCitySpinner.getSelectedItem().toString();
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(tenantSignup.this, LoginActivity.class));
            }
        });

        tenantSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isTenantFirstNameValid = true,isTenantLastNameValid = true, isTenantEmailValid= true, isTenantOccupationValid = true;
                boolean isTenantPasswordValid = true, isTenantPasswordConfirmationValid = true;
                regex = "^(?=.*[0-9])"
                        +"(?=.*[a-z])"
                        +"(?=.*[A-Z])"
                        +"(?=.*[{}@#$%!])"
                        +"(?=\\S+$).{8,15}$";
                // No spaces (?=\S+$)

// Email Validation
                if (isEmail(emailEditText) == false) {
                    emailEditText.setError("Enter valid email!");
                    isTenantEmailValid = false;
                }else {
                    isTenantEmailValid = true;
                }
// First name Validation
                if (isEmpty(firstNameEditText)) {
                    firstNameEditText.setError("First name is required!");
                    isTenantFirstNameValid = false;
                } else if (firstNameEditText.getText().length() > 3 && firstNameEditText.getText().length() < 20){
                    isTenantFirstNameValid = true;
                }
// Last name Validation
                if (isEmpty(lastNameEditText)) {
                    lastNameEditText.setError("Last name is required!");
                    isTenantLastNameValid = false;
                }else if (lastNameEditText.getText().length() > 3 && lastNameEditText.getText().length() < 20){
                    isTenantLastNameValid = true;
                }
// Password Validation
                if (isEmpty(passwordEditText)) {
                    passwordEditText.setError("Password is required!");
                    isTenantPasswordValid = false;
                }else {
                    Pattern p = Pattern.compile(regex);
                    Matcher m = p.matcher(passwordEditText.getText().toString());
                    if(m.matches()){
                        isTenantPasswordValid = m.matches();
                    }else{
                        isTenantPasswordValid = m.matches();
                        passwordEditText.setError("Password must be minimum 8 characters and maximum 15 characters. It must contain at least one number,\n" +
                                "one lowercase letter, one uppercase letter, and at least one special character from this character set\n" +
                                "only: $, %, #, @, !, {, and }.");
                    }
                }
// Password Confirmation Validation
                if (isEmpty(confirmPasswordEditText)) {
                    confirmPasswordEditText.setError("Password confirmation is required!");
                    isTenantPasswordConfirmationValid = false;
                }else if (confirmPasswordEditText.getText().length() < 8 && confirmPasswordEditText.getText().length() > 15){
                    confirmPasswordEditText.setError("Password must be between 8 and 15 characters long!");
                    isTenantPasswordConfirmationValid = false;
                }else if (confirmPasswordEditText.getText().toString().equals(passwordEditText.getText().toString())) {

                    isTenantPasswordConfirmationValid = true;
                }else {
                    confirmPasswordEditText.setError("Those passwords did not match. Try again!");
                    isTenantPasswordConfirmationValid = false;

                }
// Occupation Validation
                if (isEmpty(occupationEditText)) {
                    occupationEditText.setError("Occupation is required!");
                    isTenantOccupationValid = false;
                }else if (occupationEditText.getText().length() > 20){
                    occupationEditText.setError("Occupation must be under 20 characters!");
                    isTenantOccupationValid = false;
                }else if (occupationEditText.getText().length() < 20){
                    isTenantOccupationValid = true;
                }
// Phone Number Validation
               /* if (phone.getText().toString().isEmpty()) {
                    phoneError.setError(getResources().getString(R.string.phone_error));
                    isPhoneValid = false;
                } else  {
                    isPhoneValid = true;
                    phoneError.setErrorEnabled(false);
                }*/

// Toast if everything is validated
                if (isTenantFirstNameValid && isTenantLastNameValid && isTenantEmailValid && isTenantPasswordValid && isTenantOccupationValid && isTenantPasswordConfirmationValid) {
                    Toast.makeText(getApplicationContext(), "Successfully", Toast.LENGTH_SHORT).show();
                    tenantProgressBar.setVisibility(View.VISIBLE);


                    String email = emailEditText.getText().toString();
                    String firstName = firstNameEditText.getText().toString();
                    String lastName = lastNameEditText.getText().toString();
                    String password = passwordEditText.getText().toString();
                    String occupation = occupationEditText.getText().toString();
                    String familySize = familySizeEditText.getText().toString();
                    String grossMonthlySalary = grossMonthlySalaryEditText.getText().toString();
                    String phoneNumber = phoneNumberEditText.getText().toString();


                    mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                mUser = mAuth.getCurrentUser();
                                Toast.makeText(tenantSignup.this, "Registered Successfully", Toast.LENGTH_LONG).show();
                                tenantProgressBar.setVisibility(View.INVISIBLE);
                                DocumentReference df = mStore.collection("Tenant").document(mUser.getUid());

                                Map<String,Object> tenantInfo = new HashMap<>();

                                tenantInfo.put("Email",email);
                                tenantInfo.put("firstName",firstName);
                                tenantInfo.put("lastName",lastName);
                                tenantInfo.put("Gender",selectedGender);
                                tenantInfo.put("Password",password);
                                tenantInfo.put("Nationality",selectedTenantNationality);
                                tenantInfo.put("Occupation",occupation);
                                tenantInfo.put("Family Size",familySize);
                                tenantInfo.put("grossMonthlySalary",grossMonthlySalary);
                                tenantInfo.put("Current Residence Country",tenantSelectedCountry);
                                tenantInfo.put("City",tenantSelectedCity);
                                tenantInfo.put("PhoneNumber",phoneNumber);


                                // specify access level
                               // tenantInfo.put("isAgency","0");
                                //tenantInfo.put("isAuthedToPost","0");

                                df.set(tenantInfo);
                                sendUserToHomeActivity();


                            }else{
                                Toast.makeText(tenantSignup.this, "Something went wrong, please try again later ;(", Toast.LENGTH_LONG).show();
                                tenantProgressBar.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
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

    private void sendUserToHomeActivity() {
        finish();
        startActivity(new Intent(tenantSignup.this, listProperties.class));
    }


}