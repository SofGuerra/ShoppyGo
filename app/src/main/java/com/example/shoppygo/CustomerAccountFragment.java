package com.example.shoppygo;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class CustomerAccountFragment extends Fragment {

    private EditText customerName, customerAddress;
    private Button updateProfile, seeBuyAgain;
    private CustomerActivity parent;
    private FirebaseAuth firebaseAuth;

    public CustomerAccountFragment(CustomerActivity parent) {
        this.parent = parent;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.customer_security_login, container, false);

        customerName     = view.findViewById(R.id.customer_name);
        customerAddress  = view.findViewById(R.id.customer_address);
        updateProfile    = view.findViewById(R.id.updateProfile);
        seeBuyAgain      = view.findViewById(R.id.see_buy_again);

        customerName.setText(parent.getUser().getName());

        customerAddress.setText(parent.getUser().getAddress());


        updateProfile.setOnClickListener(v -> {
            String name = customerName.getText().toString().trim();
            String address = customerAddress.getText().toString().trim();

            parent.getUser().setName(name);
            parent.getUser().setAddress(address);

            FirebaseDatabase.getInstance().getReference("Users")
                    .child(parent.getUser().getId()).setValue(parent.getUser())
                    .addOnSuccessListener( e ->
                    Toast.makeText(getContext(), "Updated", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(i ->
                    Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show());


        });



        seeBuyAgain.setOnClickListener(v -> loadFragment(new BuyAgainFragment(parent)));

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
