package com.example.shoppygo;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class AccountFragment extends Fragment {

    EditText newCompanyName;
    Spinner prodline;
    Button updateProfile;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    String[] productLine = {"Sports", "Beachwear", "Underwear", "Casual", "Others"};


    public AccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        newCompanyName = view.findViewById(R.id.newCompanyName);
        prodline = view.findViewById(R.id.prodline);
        updateProfile = view.findViewById(R.id.updateProfile);

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
                                newCompanyName.setText(companyName);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, productLine);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prodline.setAdapter(adapter);

        updateProfile.setOnClickListener(v -> {
            String newCompany = newCompanyName.getText().toString().trim();
            String selectedLine = prodline.getSelectedItem().toString();

            if (currentUser != null) {

                String email = currentUser.getEmail();
                Query query = databaseReference.orderByChild("email").equalTo(email);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot sellerSnap : snapshot.getChildren()) {
                                String sellerKey = sellerSnap.getKey();
                                databaseReference.child(sellerKey).child("companyname").setValue(newCompany);
                                databaseReference.child(sellerKey).child("productLine").setValue(selectedLine);

                                Toast.makeText(getContext(), "Profile updated!", Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        return view;
    }
}
