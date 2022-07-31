

package com.example.androidlabproject;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;


import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirestoreRegistrar;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class HomeActivity extends AppCompatActivity implements recycleviewadapter.ONoteListenter {
    private Uri filePath;
    private List<Properties>propertiesList=new ArrayList<>();;

    FirebaseStorage storage;
    StorageReference storageReference;
    int Num=0;

    LinearLayout constraintLayout;
    private final static String tagy="Tag";
    ImageView firstImage,secondImage,thirdImage3;
    private ImageButton image_click;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    FirebaseFirestore mStore ;
    DatabaseReference databasereference;
    FirebaseFirestore mFirestore ;
    Button loginButton, logoutButton, profileButton,viewButton;
    int is_agency;
    String UID;
    private final int PICK_IMAGE_REQUEST = 71;
    FirestoreRecyclerAdapter adapter;
    private RecyclerView mMainList;
    ProgressDialog progressDialog;

    private RecyclerView.Adapter Adapter;
    AddProperty property = new AddProperty();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);


        image_click= (ImageButton) findViewById(R.id.image_click);

        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        mUser = mAuth.getCurrentUser();

        logoutButton = findViewById(R.id.home_layout_logout);
        logoutButton.setVisibility(View.INVISIBLE);

        loginButton = findViewById(R.id.home_layout_login);
        loginButton.setVisibility(View.INVISIBLE);

        profileButton = findViewById(R.id.home_layout_profile);
        profileButton.setVisibility(View.INVISIBLE);





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
            Log.d("ISAGENCY", "isAgency: " + is_agency);
        }


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(HomeActivity.this, "GoodBye!", Toast.LENGTH_LONG).show();
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



        mMainList=(RecyclerView)findViewById(R.id.main_list) ;
        mMainList.hasFixedSize();
        mMainList.setLayoutManager(new LinearLayoutManager(this));
        mFirestore= FirebaseFirestore.getInstance();


        Adapter=new recycleviewadapter(propertiesList,this,this);
        mMainList.setAdapter(Adapter);

        Recycleiewlistener();









    }



    private void Recycleiewlistener() {



        mFirestore.collection("properties").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent( QuerySnapshot value,  FirebaseFirestoreException error) {



                if(error!=null){
                    Log.d("tag","error fetching from server");
                    return;
                }
                for(DocumentChange dc:value.getDocumentChanges()){
                    if(dc.getType()==DocumentChange.Type.ADDED){
                        propertiesList.add(dc.getDocument().toObject(Properties.class));
                    }

                    Adapter.notifyDataSetChanged();

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

    private void sendTenantToProfile() {
        startActivity(new Intent(HomeActivity.this, TenantProfilePage.class));
    }

    private void sendAgencyToProfileActivity() {
        startActivity(new Intent(HomeActivity.this, AgencyProfilePage.class));
    }

    private void sendUserToLogInActivity() {
        startActivity(new Intent(HomeActivity.this, LoginActivity.class));
    }


    @Override
    public void onclick(int position) {
        Log.d("tag","clicked");

        Intent intent = new Intent(this,viewProperty.class);

        startActivity(intent);

    }
}