package com.example.shoppygo;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class CustomerHomeFragment extends Fragment implements CustomerProductAdapter.CustomerProductListener {

    RecyclerView productsrecyclerview;
    CustomerProductAdapter adapter;
    CustomerActivity parent;

    public CustomerHomeFragment(CustomerActivity parent) {
        this.parent = parent;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.customer_home, container, false);

        productsrecyclerview = view.findViewById(R.id.customer_products);

        adapter = new CustomerProductAdapter(new ArrayList<>(), this);

        productsrecyclerview.setLayoutManager(new GridLayoutManager(getContext(), 2));

        productsrecyclerview.setAdapter(adapter);

        fetchCatalog();


        return view;
    }


    void fetchCatalog() {
        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference("products");

        ArrayList<CartProduct> userCartProducts = parent.getUser().getCartItems();

        // Make a map for effecient look-ups
        HashMap<String, CartProduct> cartProductsById = new HashMap<>();
        for (CartProduct cartProduct : userCartProducts) {
            cartProductsById.put(cartProduct.getProductId(), cartProduct);
        }

        // read all the data from CartProducts to avoid reading one-by-one
        productsRef.get().addOnSuccessListener(snapshot -> {
            adapter.productList.clear();
            for (DataSnapshot child : snapshot.getChildren()) {
                String id = child.getKey();
                if (!cartProductsById.containsKey(id)) {
                    Product item = child.getValue(Product.class);
                    adapter.productList.add(item);
                }
            }
            adapter.notifyDataSetChanged();
        });

    }

    @Override
    public void OnAddToCart(Product product) {
        Intent intent = new Intent(getContext(), AddToCartActivity.class);
        intent.putExtra("product", product);
        intent.putExtra("user", parent.getUser());
        addToCartLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> addToCartLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        Customer updatedUser = (Customer) data.getSerializableExtra("user");
                        Product product = (Product) data.getSerializableExtra("product");
                        parent.setUser(updatedUser);
                        adapter.productList.removeIf(p -> Objects.equals(p.getId(), product.getId()));
                        adapter.notifyDataSetChanged();
                    }
                }
            });


}