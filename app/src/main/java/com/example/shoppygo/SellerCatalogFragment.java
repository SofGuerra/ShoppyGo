package com.example.shoppygo;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Objects;

public class SellerCatalogFragment extends Fragment implements SellerProductAdapter.IProductActionListener {

    SellerActivity parent;
    RecyclerView recyclerViewProduct;
    DatabaseReference databaseProduct;
    FirebaseStorage databaseImage;
    ArrayList<Product> productList;
    SellerProductAdapter adapter;
    Button addproduct;

    public SellerCatalogFragment(SellerActivity parent) {
        this.parent = parent;
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
        adapter = new SellerProductAdapter(requireContext(), productList, this);
        recyclerViewProduct.setAdapter(adapter);

        addproduct.setOnClickListener(v -> onAdd());

        databaseProduct.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList.clear();

                for (DataSnapshot possnapshot : snapshot.getChildren()) {
                    Product product = possnapshot.getValue(Product.class);
                    if (product != null && parent.user.getProducts().contains(product.getId())) {
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


    ActivityResultLauncher<Intent> updateLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            Product product =
                                    (Product) result.getData().getSerializableExtra("product");
                            if (product != null) {
                                for (int i = 0; i < productList.size(); i++) {
                                    if (productList.get(i).getId().equals(product.getId())) {
                                        productList.set(i, product);
                                    }
                                }
                                adapter.notifyDataSetChanged();
                            }
                        }
                    });

    ActivityResultLauncher<Intent> addLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            Seller user = (Seller) result.getData().getSerializableExtra("user");
                            Product product = (Product) result.getData().getSerializableExtra("product");
                            if (user != null) {
                                parent.user = user;
                                productList.add(product);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    });

    public void onAdd() {
        Intent intent = new Intent(getActivity(), AddProduct.class);
        intent.putExtra("user", parent.user);
        addLauncher.launch(intent);
    }

    @Override
    public void onDelete(Product product) {
        databaseProduct.child(product.getId()).removeValue();
        String imgURL = product.getImageURL();
        if (imgURL != null && !imgURL.isEmpty()) {
            FirebaseStorage.getInstance().getReferenceFromUrl(imgURL).delete();
        }
        parent.user.getProducts().remove(product.getId());
        FirebaseDatabase.getInstance().getReference("Users").child(parent.user.getId()).setValue(parent.user);

    }

    @Override
    public void onUpdate(Product product) {
        Intent intent = new Intent(getActivity(), UpdateProductActivity.class);
        intent.putExtra("product", product);
        intent.putExtra("user", parent.user);
        updateLauncher.launch(intent);
    }
}
