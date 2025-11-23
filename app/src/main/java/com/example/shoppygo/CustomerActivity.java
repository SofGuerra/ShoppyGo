package com.example.shoppygo;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class CustomerActivity extends AppCompatActivity {
    ImageView homeBtn, cartBtn, profileBtn;
    private Customer user;

    public Customer getUser() {
        return user;
    }

    public void setUser(Customer user) {
        this.user = user;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_customer);

        user = (Customer) getIntent().getSerializableExtra("user");

        homeBtn = findViewById(R.id.nav_home);
        cartBtn = findViewById(R.id.nav_cart);
        profileBtn = findViewById(R.id.nav_profile);

        loadFragment(new CustomerHomeFragment(this));

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new CustomerHomeFragment(CustomerActivity.this));
            }
        });

        cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new CustomerCartFragment(CustomerActivity.this));
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