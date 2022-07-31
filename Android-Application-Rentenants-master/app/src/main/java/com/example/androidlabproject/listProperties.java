package com.example.androidlabproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class listProperties extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseFirestore mStore;
    FirebaseUser mUser;
    DatabaseReference databasereference;

    EditText maxSurfaceArea, minSurfaceArea, minNumberOfBedrooms, maxNumberOfBedrooms, minRentalPrice;
    int maxArea = 10000000 ,minArea = 0, minBed  = 0 ,maxBed = 10000000 ,minPrice = 0;
    CheckBox searchGarden, searchBalcony, searchFurnished, searchGarden2, searchBalcony2, searchNotFurnished;
    boolean sFurnished = false, sHaveBalcony = false, sHaveGarden = false, sNotFurnished = false, sDoNotHaveBalcony = false, sDoNotHaveGarden = false;

    String searchSelectedCity,UID;
    int is_agency;

    Button loginButton, logoutButton, profileButton, goToSearchButton, searchButton;

    public static final String PREFS_NAME = "MyPrefsFile2";
    private static final String PREF_PROPERTY = "propertyID";
    SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_properties);

        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        mUser = mAuth.getCurrentUser();

        sharedPrefManager = SharedPrefManager.getInstance(this);

        LinearLayout search = findViewById(R.id.search_criteria);
        search.setVisibility(View.GONE);

        ScrollView scroll = findViewById(R.id.list_properties_scroll);
        scroll.setVisibility(View.VISIBLE);
        LinearLayout list = findViewById(R.id.list_main);
        list.setVisibility(View.VISIBLE);


        goToSearchButton = findViewById(R.id.list_porperties_search);
        searchButton = findViewById(R.id.button_search);

        logoutButton = findViewById(R.id.home_layout_logout2);
        logoutButton.setVisibility(View.INVISIBLE);

        loginButton = findViewById(R.id.home_layout_login2);
        loginButton.setVisibility(View.INVISIBLE);

        profileButton = findViewById(R.id.home_layout_profile2);
        profileButton.setVisibility(View.INVISIBLE);


        maxSurfaceArea = findViewById(R.id.search_max_surface_area);
        minSurfaceArea = findViewById(R.id.search_min_surface_area);
        minNumberOfBedrooms = findViewById(R.id.search_min_num_of_bedrooms);
        maxNumberOfBedrooms = findViewById(R.id.search_max_num_of_bedrooms);
        minRentalPrice = findViewById(R.id.search_min_rental_price);


        searchGarden = findViewById(R.id.search_garden);
        searchBalcony = findViewById(R.id.search_balcony);
        searchFurnished = findViewById(R.id.search_furnished);

        searchGarden2 = findViewById(R.id.search_garden2);
        searchBalcony2 = findViewById(R.id.search_balcony3);
        searchNotFurnished = findViewById(R.id.search_furnished2);

        if (mUser == null){
            logoutButton.setVisibility(View.INVISIBLE);
            profileButton.setVisibility(View.INVISIBLE);
            loginButton.setVisibility(View.VISIBLE);
        }else{
            loginButton.setVisibility(View.INVISIBLE);
            logoutButton.setVisibility(View.VISIBLE);
            profileButton.setVisibility(View.VISIBLE);
            UID = mUser.getUid();
            databasereference = FirebaseDatabase.getInstance().getReference();
            CheckUserType(UID);
        }
        fillProperties();

        Spinner searchPropertyCitySpinner = (Spinner) findViewById(R.id.search_city_spinner);
        ArrayAdapter<CharSequence> searchPropertyCtyAdapter;
        searchPropertyCtyAdapter = ArrayAdapter.createFromResource(this, R.array.array_cities, R.layout.spinner_layouy);
        searchPropertyCtyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        searchPropertyCitySpinner.setAdapter(searchPropertyCtyAdapter);

        searchPropertyCitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                searchSelectedCity = searchPropertyCitySpinner.getSelectedItem().toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        searchGarden.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sHaveGarden = buttonView.isChecked();
            }
        });

        searchGarden2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sDoNotHaveGarden = buttonView.isChecked();
            }
        });

        searchBalcony.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sHaveBalcony = buttonView.isChecked();
            }
        });

        searchBalcony2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sDoNotHaveBalcony = buttonView.isChecked();
            }
        });

        searchFurnished.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sFurnished = buttonView.isChecked();
            }
        });

        searchNotFurnished.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sNotFurnished = buttonView.isChecked();
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fillProperties();

                search.setVisibility(View.GONE);
                scroll.setVisibility(View.VISIBLE);
                list.setVisibility(View.VISIBLE);
                goToSearchButton.setVisibility(View.VISIBLE);
            }
        });

        goToSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSearchButton.setVisibility(View.GONE);
                search.setVisibility(View.VISIBLE);
                scroll.setVisibility(View.GONE);
                list.setVisibility(View.GONE);
                //searchSelectedCity  maxArea,minArea,minBed,maxBed,minPrice;



            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(listProperties.this, LoginActivity.class));
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(listProperties.this, "GoodBye!", Toast.LENGTH_LONG).show();
                finish();
                startActivity(getIntent());
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is_agency == 1){ // agency
                    sendAgencyToProfileActivity();
                }else{ //tenant
                    sendTenantToProfile();
                }
            }
        });
    }

    private void CheckUserType (String uid) {

        DocumentReference agencyDF = mStore.collection("Agency").document(uid);
        agencyDF.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d("TAG","Agency onSuccess:" + documentSnapshot.getData());
                if (documentSnapshot.getString("isAgency") != null && documentSnapshot.getString("isAgency").equals("1")){ // Agency
                    is_agency = 1;
                }else{
                    DocumentReference tenantDF = mStore.collection("Tenant").document(uid);
                    tenantDF.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Log.d("TAG","Tenant onSuccess:" + documentSnapshot.getData());
                            is_agency = 0;

                        }
                    });
                }
            }
        });


    }

    public void fillProperties() {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.list_main);
        linearLayout.removeAllViews();
        Task<QuerySnapshot> df2 = mStore.collection("properties").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<String> properties = new ArrayList<>();
                    for(QueryDocumentSnapshot document : task.getResult()){
                        properties.add(document.getId());

                        if (!(maxSurfaceArea.getText().toString().equals(""))){
                            maxArea = Integer.parseInt(maxSurfaceArea.getText().toString());
                        }else{
                            maxArea = 10000000;
                        }

                        if (!(minSurfaceArea.getText().toString().equals(""))){
                            minArea = Integer.parseInt(minSurfaceArea.getText().toString());
                        }else{
                            minArea = 0;
                        }

                        if (!(minNumberOfBedrooms.getText().toString().equals(""))){
                            minBed = Integer.parseInt(minNumberOfBedrooms.getText().toString());
                        }else{
                            minBed = 0;
                        }

                        if (!(maxNumberOfBedrooms.getText().toString().equals(""))){
                            maxBed = Integer.parseInt(maxNumberOfBedrooms.getText().toString());
                        }else{
                            maxBed = 10000000;
                        }

                        if (!(minRentalPrice.getText().toString().equals(""))){
                            minPrice = Integer.parseInt(minRentalPrice.getText().toString());
                        }else{
                            minPrice = 0;
                        }

                        boolean valid1 = false, valid2 = false, valid3 = false, valid4 = false, valid5 = false, valid6 = false;
                        boolean valid7 = false, valid8 = false, valid9 = false, valid10 = false, valid11 = false, valid12 = false;

                        if (sFurnished || sNotFurnished) { // Specified
                            if (sFurnished && document.getString("Furnished").equals("true")) { // want furnished
                                valid1 = true;

                            }else if (sNotFurnished && document.getString("Furnished").equals("false")) { // doesn't want furnished
                                valid1 = true;
                            }
                        }else{ // Don't care
                            valid1 = true;
                        }
                        if ( sDoNotHaveBalcony || sHaveBalcony ){
                            if (sHaveBalcony && document.getString("haveBalcony").equals("true")) { // want furnished
                                valid2 = true;
                            }else if (sDoNotHaveBalcony && document.getString("haveBalcony").equals("false")) { // doesn't want furnished
                                valid2 = true;
                            }
                        }else{ // Don't care
                           valid2 = true;
                        }
                        if(sHaveGarden || sDoNotHaveGarden ){
                            if (sHaveGarden && document.getString("haveGarden").equals("true")) { // want furnished
                                valid3 = true;
                            }else if (sDoNotHaveGarden && document.getString("haveGarden").equals("false")) { // doesn't want furnished
                                valid3 = true;
                            }
                        }else{ // Don't care
                            valid3 = true;
                        }

                        if (maxArea >= Integer.parseInt(Objects.requireNonNull(document.getString("surface_area")))) {
                            valid4 = true;
                        }


                        if (minArea <= Integer.parseInt(Objects.requireNonNull(document.getString("surface_area")))) {
                            valid5 = true;
                            }


                        if (minBed <= Integer.parseInt(Objects.requireNonNull(document.getString("bedrooms")))) {
                            valid6 = true;
                        }


                        if (maxBed >= Integer.parseInt(Objects.requireNonNull(document.getString("bedrooms")))) {
                            valid7 = true;
                        }


                        if (minPrice <= Integer.parseInt(Objects.requireNonNull(document.getString("rentalPrice")))) {
                            valid8 = true;
                        }

                        if (valid1 && valid2 && valid3 && valid4 && valid5 && valid6 && valid7 && valid8){
                            addToTheList(document,linearLayout);
                        }

                    }
                }else{
                    TextView listPropertyAvailableDate = new TextView(listProperties.this);
                    listPropertyAvailableDate.setTextColor(getResources().getColor(R.color.colorWhite));
                    listPropertyAvailableDate.setTypeface(listPropertyAvailableDate.getTypeface(), Typeface.BOLD);
                    listPropertyAvailableDate.setText("Available Date: "+"Error Getting Data!");

                    TextView listPropertyRentalPrice = new TextView(listProperties.this);
                    listPropertyRentalPrice.setTextColor(getResources().getColor(R.color.colorWhite));
                    listPropertyRentalPrice.setText("Rental Price: " + "Error Getting Rental Price!");

                    linearLayout.addView(listPropertyAvailableDate);
                    linearLayout.addView(listPropertyRentalPrice);

                }
            }
        });


    }

    public void addToTheList(QueryDocumentSnapshot document, LinearLayout linearLayout){

        TextView listPropertyAvailableDate = new TextView(listProperties.this);
        listPropertyAvailableDate.setTextColor(getResources().getColor(R.color.colorWhite));
        listPropertyAvailableDate.setTextSize(20);
        listPropertyAvailableDate.setTypeface(listPropertyAvailableDate.getTypeface(), Typeface.BOLD);
        listPropertyAvailableDate.setText("Available Date: "+document.getString("availabilityDate"));

        TextView listPropertyRentalPrice = new TextView(listProperties.this);
        listPropertyRentalPrice.setTextColor(getResources().getColor(R.color.colorWhite));
        listPropertyRentalPrice.setTextSize(20);
        listPropertyRentalPrice.setText("Rental Price: " + document.getString("rentalPrice"));

        Button viewProperty = new Button(listProperties.this);
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
                Toast.makeText(listProperties.this, "Clicked", Toast.LENGTH_LONG).show();

            }
        });

        linearLayout.addView(listPropertyAvailableDate);
        linearLayout.addView(listPropertyRentalPrice);
        linearLayout.addView(viewProperty);
    }

    private void sendToViewProperty() {
        startActivity(new Intent(listProperties.this, viewProperty.class));
    }

    private void sendTenantToProfile() {
        finish();
        startActivity(new Intent(listProperties.this, TenantProfilePage.class));
    }

    private void sendAgencyToProfileActivity() {
        finish();
        startActivity(new Intent(listProperties.this, AgencyProfilePage.class));
    }

}