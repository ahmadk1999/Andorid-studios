package com.example.androidlabproject;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
public class MainActivity extends AppCompatActivity {
    Button button;
    LinearLayout linearLayout;

    FirebaseAuth mAuth;
    FirebaseFirestore mStore;

    ImageView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setProgress(false);

        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();

        fillProperties();

        view = findViewById(R.id.imageView3);
        Animation animShake = AnimationUtils.loadAnimation(MainActivity.this, R.anim.shake);

        button = (Button) findViewById(R.id.connect_rest);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.startAnimation(animShake);
                ConnectionAsyncTask connectionAsyncTask = new ConnectionAsyncTask(MainActivity.this);
                connectionAsyncTask.execute("http://www.mocky.io/v2/5b4e6b4e3200002c009c2a44");

                startActivity(new Intent(MainActivity.this, listProperties.class));
            }
        });

        linearLayout = (LinearLayout) findViewById(R.id.layout);


    }

    public void setButtonText(String text) {
        button.setText(text);
    }

    public void fillProperties() {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layout);
        linearLayout.removeAllViews();
        Task<QuerySnapshot> df2 = mStore.collection("properties").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List <String> properties = new ArrayList<>();
                    for(QueryDocumentSnapshot document : task.getResult()){
                        properties.add(document.getId());

                        TextView propertyAvailableDate = new TextView(MainActivity.this);
                        propertyAvailableDate.setTextColor(getResources().getColor(R.color.colorWhite));
                        propertyAvailableDate.setTextSize(20);
                        propertyAvailableDate.setTypeface(propertyAvailableDate.getTypeface(), Typeface.BOLD);
                        propertyAvailableDate.setText("Available Date: "+document.getString("availabilityDate"));

                        TextView propertyRentalPrice = new TextView(MainActivity.this);
                        propertyRentalPrice.setTextColor(getResources().getColor(R.color.colorWhite));
                        propertyRentalPrice.setTextSize(20);
                        propertyRentalPrice.setText("Rental Price: " + document.getString("rentalPrice"));

                        linearLayout.addView(propertyAvailableDate);
                        linearLayout.addView(propertyRentalPrice);
                    }
                }else{
                    TextView propertyAvailableDate = new TextView(MainActivity.this);
                    propertyAvailableDate.setTextColor(getResources().getColor(R.color.colorWhite));
                    propertyAvailableDate.setTypeface(propertyAvailableDate.getTypeface(), Typeface.BOLD);
                    propertyAvailableDate.setText("Available Date: "+"Error Getting Data!");

                    TextView propertyRentalPrice = new TextView(MainActivity.this);
                    propertyRentalPrice.setTextColor(getResources().getColor(R.color.colorWhite));
                    propertyRentalPrice.setText("Rental Price: " + "Error Getting Rental Price!");

                    linearLayout.addView(propertyAvailableDate);
                    linearLayout.addView(propertyRentalPrice);

                }
            }
        });


    }


    public void setProgress(boolean progress) {
        ProgressBar progressBar = (ProgressBar)
                findViewById(R.id.progressBar);
        if (progress) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }
}
