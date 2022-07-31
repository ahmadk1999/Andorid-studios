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

public class rentingAgencySignup extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    FirebaseFirestore mStore;

    private String selectedCountry, selectedCity,areaCode;
    private Spinner countrySpinner,citySpinner;
    private ArrayAdapter<CharSequence> countryAdapter,cityAdapter;
    String regex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_renting_agency_signup);

        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();

        countrySpinner =(Spinner) findViewById(R.id.country_spinner);
        countryAdapter = ArrayAdapter.createFromResource(this, R.array.array_countries, R.layout.spinner_layouy);
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countrySpinner.setAdapter(countryAdapter);

        EditText agencyEmailEditText = (EditText) findViewById(R.id.Email);
        EditText agencyNameEditText = (EditText) findViewById(R.id.AgencyName);

        EditText agencyPasswordEditText = findViewById(R.id.password);
        EditText agencyConfirmPasswordEditText = findViewById(R.id.Confirmpassword);

        EditText agencyPhoneEditText = (EditText) findViewById(R.id.PhoneNumber);
        Button agencySignup = findViewById(R.id.SIGNUP);

        TextView logIn = findViewById(R.id.textviewLogin);
        ProgressBar agencyProgressBar = (ProgressBar) findViewById(R.id.progressBarAgencyRegister);
        agencyProgressBar.setVisibility(View.INVISIBLE);

        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                citySpinner =(Spinner) findViewById(R.id.city_spinner);
                selectedCountry = countrySpinner.getSelectedItem().toString();

                int parentID = parent.getId();
                if (parentID == R.id.country_spinner){
                    switch (selectedCountry){
                        case "Please select a country": cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_default_cities,R.layout.spinner_layouy);
                            break;
                        case "China": cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.China,R.layout.spinner_layouy);
                                areaCode = "0086";
                            break;
                        case "India": cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.India,R.layout.spinner_layouy);
                                areaCode = "0091";
                            break;
                        case "Brazil": cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.Brazil,R.layout.spinner_layouy);
                                areaCode = "0055";
                            break;
                        case "Russia": cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.Russia,R.layout.spinner_layouy);
                                areaCode = "0007";
                            break;
                        case "Japan": cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.Japan,R.layout.spinner_layouy);
                                areaCode = "0081";
                            break;
                        case "Italy": cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.Italy,R.layout.spinner_layouy);
                                areaCode = "0039";
                            break;
                        default: break;
                    }
                    cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    citySpinner.setAdapter(cityAdapter);

                    citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            selectedCity = citySpinner.getSelectedItem().toString();
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
                startActivity(new Intent(rentingAgencySignup.this, LoginActivity.class));
            }
        });

        agencySignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                regex = "^(?=.*[0-9])"
                        +"(?=.*[a-z])"
                        +"(?=.*[A-Z])"
                        +"(?=.*[{}@#$%!])"
                        +"(?=\\S+$).{8,15}$";
                // No spaces (?=\S+$)
                boolean isAgencyNameValid = true, isAgencyEmailValid= true,isAgencyPasswordValid = true;
                boolean isAgencyPasswordConfirmationValid = true, isPhoneValid = true, isCountryValid = true, isCityValid = true;
// Email Validation
                if (isEmail(agencyEmailEditText) == false) {
                    agencyEmailEditText.setError("Enter valid email!");
                    isAgencyEmailValid = false;
                    agencyEmailEditText.requestFocus();
                }else {
                    isAgencyEmailValid = true;
                }
// agency name Validation
                if (isEmpty(agencyNameEditText)) {
                    agencyNameEditText.setError("Agency name is required!");
                    isAgencyNameValid = false;
                    agencyNameEditText.requestFocus();
                } else if (agencyNameEditText.getText().length() > 20){
                    agencyNameEditText.setError("Agency name must be under 20 characters!");
                    isAgencyNameValid = false;
                    agencyNameEditText.requestFocus();
                }else if (agencyNameEditText.getText().length() < 20){
                    isAgencyNameValid = true;
                }


// Password Validation
                if (isEmpty(agencyPasswordEditText)) {
                    agencyPasswordEditText.setError("Password is required!");
                    isAgencyPasswordValid = false;
                    agencyPasswordEditText.requestFocus();
                }else {
                    Pattern p = Pattern.compile(regex);
                    Matcher m = p.matcher(agencyPasswordEditText.getText().toString());
                    if(m.matches()){
                        isAgencyPasswordValid = m.matches();
                    }else{
                        isAgencyPasswordValid = m.matches();
                        agencyPasswordEditText.setError("Password must be minimum 8 characters and maximum 15 characters. It must contain at least one number,\n" +
                                "one lowercase letter, one uppercase letter, and at least one special character from this character set\n" +
                                "only: $, %, #, @, !, {, and }.");
                    }
                }

// Password Confirmation Validation
                if (isEmpty(agencyConfirmPasswordEditText)) {
                    agencyConfirmPasswordEditText.setError("Password confirmation is required!");
                    agencyConfirmPasswordEditText.requestFocus();
                    isAgencyPasswordConfirmationValid = false;
                }else if (agencyConfirmPasswordEditText.getText().length() < 8 && agencyConfirmPasswordEditText.getText().length() > 15){
                    agencyConfirmPasswordEditText.setError("Password must be between 8 and 15 characters long!");
                    isAgencyPasswordConfirmationValid = false;
                    agencyConfirmPasswordEditText.requestFocus();
                }else if (agencyConfirmPasswordEditText.getText().toString().equals(agencyPasswordEditText.getText().toString())) {
                    isAgencyPasswordConfirmationValid = true;
                }else {
                    agencyConfirmPasswordEditText.setError("Those passwords did not match. Try again!");
                    isAgencyPasswordConfirmationValid = false;
                    agencyConfirmPasswordEditText.requestFocus();
                }
// Phone Number Validation
                if (agencyPhoneEditText.getText().toString().isEmpty()) {
                    agencyPhoneEditText.setError("Phone number is required!");
                    isPhoneValid = false;
                } else  {
                    isPhoneValid = true;

                }

// to check that a city and country were selected
              /*  if (selectedCountry.equals("Please select a country")){
                    isCountryValid = false;
                    Toast.makeText(getApplicationContext(), "Please Select a Country", Toast.LENGTH_SHORT).show();
                    countrySpinner.requestFocus();
                }
                if(selectedCity.equals("Please select a country first!") || selectedCity.equals("Please select a city")){
                    isCityValid = false;
                    Toast.makeText(getApplicationContext(), "Please Select a City", Toast.LENGTH_SHORT).show();
                    citySpinner.requestFocus();
                }*/
// Toast if everything is validated
                if (isAgencyEmailValid && isAgencyNameValid && isAgencyPasswordValid && isAgencyPasswordConfirmationValid
                && isCountryValid && isCityValid && isPhoneValid) {
                    String email = agencyEmailEditText.getText().toString();
                    String name = agencyNameEditText.getText().toString();
                    String password = agencyPasswordEditText.getText().toString();
                    String phoneNumber = agencyPhoneEditText.getText().toString();
                    //Agency newAgency = new Agency(email,name,password,selectedCountry,selectedCity,phoneNumber);
                    agencyProgressBar.setVisibility(View.VISIBLE);

                    mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        mUser = mAuth.getCurrentUser();
                                        Toast.makeText(rentingAgencySignup.this, "Registered Successfully", Toast.LENGTH_LONG).show();
                                        agencyProgressBar.setVisibility(View.INVISIBLE);
                                        DocumentReference df = mStore.collection("Agency").document(mUser.getUid());

                                        Map<String,Object> agencyInfo = new HashMap<>();
                                        agencyInfo.put("Email",email);
                                        agencyInfo.put("AgencyName",name);
                                        agencyInfo.put("Password",password);
                                        agencyInfo.put("PhoneNumber",areaCode+phoneNumber);
                                        agencyInfo.put("Country",selectedCountry);
                                        agencyInfo.put("City",selectedCity);

                                        // specify access level
                                        agencyInfo.put("isAgency","1");
                                        agencyInfo.put("isAuthedToPost","1");

                                        df.set(agencyInfo);
                                        agencyProgressBar.setVisibility(View.INVISIBLE);
                                        sendUserToHomeActivity();


                                    }else{
                                        Toast.makeText(rentingAgencySignup.this, "Something went wrong, please try again later ;(", Toast.LENGTH_LONG).show();
                                        agencyProgressBar.setVisibility(View.INVISIBLE);
                                    }
                                }
                            });

                }
            }
        });


    }

    private void sendUserToHomeActivity() {
        finish();
        startActivity(new Intent(rentingAgencySignup.this, listProperties.class));
    }

    private boolean isEmpty(EditText text) {
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }

    boolean isEmail(EditText text) {
        CharSequence email = text.getText().toString();
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }
}