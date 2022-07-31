package com.example.androidlabproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        TextView logIn = findViewById(R.id.textviewLogin);
        Button tenantButton = (Button) findViewById(R.id.temant_button);
        Button agencyButton = (Button) findViewById(R.id.ageency_button);
        Button homeButton = (Button) findViewById(R.id.homebutton);

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToHomeActivity();
            }
        });


        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });
        tenantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(RegisterActivity.this, tenantSignup.class));
            }
        });
        agencyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(RegisterActivity.this, rentingAgencySignup.class));
            }
        });
    }


    void sendToHomeActivity(){
        finish();
        startActivity(new Intent(RegisterActivity.this, listProperties.class));
    }
}