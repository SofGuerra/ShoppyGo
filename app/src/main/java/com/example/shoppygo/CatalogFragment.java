package com.example.shoppygo;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class CatalogFragment extends Fragment {

    ListView listViewProduct;
    DatabaseReference databaseProduct;
    ArrayList<Product> productList;
    ProductAdapter adapter;
    Button addproduct;

    public CatalogFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_catalog, container, false);

        listViewProduct = view.findViewById(R.id.productinfo);
        addproduct = view.findViewById(R.id.addproduct);
        databaseProduct = FirebaseDatabase.getInstance().getReference("products");

        productList = new ArrayList<>();
        adapter = new ProductAdapter(requireContext(), productList);
        listViewProduct.setAdapter(adapter);

        addproduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddProduct.class));
            }
        });

        databaseProduct.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList.clear();

                for (DataSnapshot possnapshot : snapshot.getChildren()){
                    Product product = possnapshot.getValue(Product.class);
                    if (product != null){
                        productList.add(product);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }
}