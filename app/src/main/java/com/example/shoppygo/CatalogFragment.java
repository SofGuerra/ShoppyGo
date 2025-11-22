package com.example.shoppygo;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;


public class CatalogFragment extends Fragment {

    RecyclerView recyclerViewProduct;
    DatabaseReference databaseProduct;
    FirebaseStorage databaseImage;
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

        recyclerViewProduct = view.findViewById(R.id.productinfo);
        recyclerViewProduct.setLayoutManager(new LinearLayoutManager(getContext()));
        addproduct = view.findViewById(R.id.addproduct);
        databaseProduct = FirebaseDatabase.getInstance().getReference("products");

        productList = new ArrayList<>();
        adapter = new ProductAdapter(requireContext(), productList, new ProductAdapter.OnProductActionListener() {
            @Override
            public void onDelete(Product product) {
                databaseProduct.child(product.getId()).removeValue();
                String imgURL = product.getImageURL();

                if (imgURL != null && !imgURL.isEmpty()) {
                    databaseImage.getInstance().getReferenceFromUrl(imgURL).delete()
                            .addOnSuccessListener(aVoid ->{
                                Toast.makeText(requireContext(), "Product deleted", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e ->{
                                Toast.makeText(requireContext(), "Error deleting image", Toast.LENGTH_SHORT).show();
                            });


                }


            }

            @Override
            public void onUpdate(Product product) {
                Intent intent = new Intent(getActivity(), UpdateProduct.class);
                intent.putExtra("id", product.getId());
                startActivity(intent);
            }
        });
        recyclerViewProduct.setAdapter(adapter);

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
                        product.setId(possnapshot.getKey());
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