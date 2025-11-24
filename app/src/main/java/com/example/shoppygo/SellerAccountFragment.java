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


public class SellerAccountFragment extends Fragment {

    EditText newCompanyName;
    Spinner prodline;
    Button updateProfile;
    SellerActivity parent;
    String[] productLine = {"Sports", "Beachwear", "Underwear", "Casual", "Others"};


    public SellerAccountFragment(SellerActivity parent) {
        this.parent = parent;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        newCompanyName = view.findViewById(R.id.newCompanyName);
        prodline = view.findViewById(R.id.prodline);
        updateProfile = view.findViewById(R.id.updateProfile);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, productLine);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prodline.setAdapter(adapter);

        updateProfile.setOnClickListener(v -> {
            String newCompany = newCompanyName.getText().toString().trim();
            String selectedLine = prodline.getSelectedItem().toString();
            parent.user.setCompanyName(newCompany);
            parent.user.setProductLine(selectedLine);
            FirebaseDatabase.getInstance().getReference("Users")
                    .child(parent.user.getId())
                    .setValue(parent.user)
                    .addOnSuccessListener(unused ->
                            Toast.makeText(requireContext(), "User updated", Toast.LENGTH_SHORT).show()
                    )
                    .addOnFailureListener(e ->
                            Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                    );


        });

        return view;
    }
}
