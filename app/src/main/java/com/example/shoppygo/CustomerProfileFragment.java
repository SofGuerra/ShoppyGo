package com.example.shoppygo;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class CustomerProfileFragment extends Fragment {

    private TextView companyTxt;
    private TextView lognsecbtn, accountbtn;
    private FrameLayout fragmentCont;

    private CustomerActivity parent;
    private ImageButton logout;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    public CustomerProfileFragment(CustomerActivity parent) {
        this.parent = parent;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_account_customer, container, false);

        // Match XML IDs
        companyTxt   = view.findViewById(R.id.welcomecompany);
        lognsecbtn   = view.findViewById(R.id.lognsecbtn);
        accountbtn   = view.findViewById(R.id.accountbtn);
        fragmentCont = view.findViewById(R.id.fragmentCont);
        logout       = view.findViewById(R.id.logoutbtn);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            String email = currentUser.getEmail();
            databaseReference = FirebaseDatabase.getInstance().getReference("Sellers");

            Query query = databaseReference.orderByChild("email").equalTo(email);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot sellerSnap : snapshot.getChildren()) {
                            String companyName = sellerSnap.child("companyname").getValue(String.class);
                            if (companyName != null) {
                                companyTxt.setText("Welcome " + companyName);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });
        }

        lognsecbtn.setOnClickListener(v -> loadFragment(new LoginSecurityFragment()));
        accountbtn.setOnClickListener(v -> loadFragment(new CustomerAccountFragment(parent)));

        logout.setOnClickListener(v -> {
            firebaseAuth.signOut();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            requireActivity().finish();
        });

        return view;
    }

    private void loadFragment(Fragment fragment) {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentCont, fragment)
                .addToBackStack(null)
                .commit();
    }

}
