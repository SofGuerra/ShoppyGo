package com.example.shoppygo;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {
    ImageView homeBtn, catalogBtn, profileBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        homeBtn = findViewById(R.id.nav_home);
        catalogBtn = findViewById(R.id.nav_orders);
        profileBtn = findViewById(R.id.nav_profile);

        loadFragment(new SellerDashboardFragment());

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new SellerDashboardFragment());
            }
        });

        catalogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new CatalogFragment());
            }
        });

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new SellerProfileFragment());
            }
        });
    }

    private void loadFragment (Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer,fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}