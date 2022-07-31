package com.example.androidlabproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AddProperty extends AppCompatActivity {

    private Button btnChoose, btnUpload,Addproperty;
    private ImageView imageView;

    String selectedCity;

    public Uri getFilePath() {
        return filePath;
    }
    private static final int PICK_IMG = 1;
    private ArrayList<Uri> ImageList = new ArrayList<Uri>();
    private int uploads = 0;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;
    int index = 0;
    TextView textView;
    Button choose,send,btnCancel;
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;
    boolean Furnished = false ,haveGarden = false ,haveBalcony = false ;

    //private String Description,PostalAddress,City,furnished,garden,balcony,avilablityDate,numberofbedrooms,Rentalprice,surfacearea,constructionyear;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    FirebaseFirestore mStore;
    String UID;

    List<Properties> Property;
    ListView listViewProperty;

    //Firebase
    FirebaseStorage storage;
    StorageReference storageReference;

    ProgressBar progressBarPropertyAdd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_property);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        FirebaseFirestore mfirestore=FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        mUser = mAuth.getCurrentUser();
        UID = mUser.getUid();

        Spinner addPropertyCitySpinner = (Spinner) findViewById(R.id.add_propert_spinner);
        ArrayAdapter<CharSequence> propertyCtyAdapter;
        propertyCtyAdapter = ArrayAdapter.createFromResource(this, R.array.array_cities, R.layout.spinner_layouy);
        propertyCtyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addPropertyCitySpinner.setAdapter(propertyCtyAdapter);

        addPropertyCitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCity = addPropertyCitySpinner.getSelectedItem().toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //listViewProperty = (ListView) findViewById(R.id.listViewArtists);
        Addproperty = (Button) findViewById(R.id.Add_property);
        btnChoose = (Button) findViewById(R.id.choose_button);
        btnUpload = (Button) findViewById(R.id.upload_imag);
        imageView = (ImageView) findViewById(R.id.imageView1);
        btnCancel = (Button) findViewById(R.id.add_propert_cancel);

        progressBarPropertyAdd = findViewById(R.id.progressBarAgencyAddProperty);
        progressBarPropertyAdd.setVisibility(View.GONE);

        CheckBox addPropertyIsFurnished = findViewById(R.id.add_property_furnished);
        CheckBox addPropertyHaveGarden = findViewById(R.id.add_property_garden);
        CheckBox addPropertyHaveBalcony = findViewById(R.id.add_property_balcony);

        addPropertyIsFurnished.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(buttonView.isChecked()){
                    Furnished = true;
                }else{
                    Furnished = false;
                }
            }
        });
        addPropertyHaveGarden.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(buttonView.isChecked()){
                    haveGarden = true;
                }else{
                    haveGarden = false;

                }
            }
        });
        addPropertyHaveBalcony.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(buttonView.isChecked()){
                    haveBalcony = true;
                }else{
                    haveBalcony = false;
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendAgencyToHome();
            }
        });

        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        Addproperty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Task<AuthResult> task = null;
                addProperty(task);
            }
        });



    }

    private void sendAgencyToHome() {
        finish();
        startActivity(new Intent(AddProperty.this, listProperties.class));
    }

    public void addProperty(Task<AuthResult> task){

    progressBarPropertyAdd.setVisibility(View.VISIBLE);
    Properties propertyy=new Properties();

    EditText Description = (EditText) findViewById(R.id.Description);
    EditText PostalAddress=(EditText) findViewById(R.id.postal_address);
    EditText avilablityDate=(EditText)findViewById(R.id.avliable_Date);
    EditText numberOfBedrooms=(EditText)findViewById(R.id.Numberofbedrooms);
    EditText rentalPrice=(EditText)findViewById(R.id.RentalPrice);
    EditText surfaceArea=(EditText)findViewById(R.id.surface_Area);
    EditText constructionYear=(EditText)findViewById(R.id.constructionyear);

    String description = Description.getText().toString();
    String postaladdress = PostalAddress.getText().toString();
    String avaliabledate = avilablityDate.getText().toString();
    String bedrooms = numberOfBedrooms.getText().toString();
    String Surfacearea = surfaceArea.getText().toString();
    String rent_price = rentalPrice.getText().toString();
    String con_year = constructionYear.getText().toString();
    propertyy.setAvailabilityDate(avaliabledate);
    FirebaseFirestore databaseproperty = FirebaseFirestore.getInstance();

    if (isEmpty(Description)) {
        Description.setError("Description is required!");
    }else if (isEmpty(PostalAddress)) {
        PostalAddress.setError("Postal address is required!");
    }else if (isEmpty(surfaceArea)) {
        surfaceArea.setError("Surface area is required!");
    }else if (isEmpty(constructionYear)) {
        constructionYear.setError("Construction year is required!");
    }else if (isEmpty(rentalPrice)) {
        rentalPrice.setError("Rental price is required!");
    }else if (isEmpty(avilablityDate)) {
        avilablityDate.setError("Available date is required!");
    }else if (isEmpty(numberOfBedrooms)) {
        numberOfBedrooms.setError("Number of bedrooms is required!");
    }else if (selectedCity.equals("Please select a city")){
        Toast.makeText(AddProperty.this, "Please Select A City and Try Again!", Toast.LENGTH_SHORT).show();
    }else{

        Map<String, Object> property = new HashMap<>();

        property.put("propertyCity", selectedCity);
        property.put("Description", description);
        property.put("Postal_Address", postaladdress);
        property.put("availabilityDate", avaliabledate);
        property.put("bedrooms", bedrooms);
        property.put("rentalPrice", rent_price);
        property.put("surface_area", Surfacearea);
        property.put("con_year", con_year);
        property.put("Agency ID", UID);

        if(Furnished){
            property.put("Furnished", "true");
        }else{
            property.put("Furnished", "false");
        }

        if(haveBalcony){
            property.put("haveBalcony", "true");
        }else{
            property.put("haveBalcony", "false");
        }

        if(haveGarden){
            property.put("haveGarden", "true");
        }else{
            property.put("haveGarden", "false");
        }

        databaseproperty.collection("properties").add(property);
        Toast.makeText(AddProperty.this, "Added the property!", Toast.LENGTH_SHORT).show();
        uploadImage();
        finish();
        startActivity(new Intent(AddProperty.this, listProperties.class));
    }
    progressBarPropertyAdd.setVisibility(View.GONE);
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage() {

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(AddProperty.this, "Uploaded", Toast.LENGTH_SHORT).show();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(AddProperty.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }

    private void SendLink(String url) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("link", url);
        databaseReference.push().setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                progressDialog.dismiss();
                textView.setText("Image Uploaded Successfully");
                send.setVisibility(View.GONE);
                ImageList.clear();
            }
        });
    }

    private boolean isEmpty(EditText text) {
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }


}