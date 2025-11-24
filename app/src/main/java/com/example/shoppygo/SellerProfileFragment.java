package com.example.shoppygo;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class SellerProfileFragment extends Fragment {

    SellerActivity parent;
    TextView Company;
    TextView lognsecbtn, accountbtn, reviewsbtn;
    ImageButton logout;

    public SellerProfileFragment(SellerActivity parent) {
        this.parent = parent;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_seller_profile, container, false);

        Company = view.findViewById(R.id.welcomecompany);
        lognsecbtn = view.findViewById(R.id.lognsecbtn);
        accountbtn = view.findViewById(R.id.accountbtn);
        //reviewsbtn = view.findViewById(R.id.reviewsbtn);
        logout = view.findViewById(R.id.logoutbtn);

        Company.setText("Welcome " + parent.user.getCompanyName());

        lognsecbtn.setOnClickListener(v -> loadFragment(new LoginSecurityFragment()));
        accountbtn.setOnClickListener(v -> loadFragment(new SellerAccountFragment(parent)));
        //reviewsbtn.setOnClickListener(v -> loadFragment(new ReviewsFragment()));

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                requireActivity().finish();
            }
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