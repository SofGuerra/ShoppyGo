package com.example.shoppygo;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.TextView;

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
import java.util.HashSet;
import java.util.Objects;

public class CustomerHomeFragment extends Fragment implements CustomerProductAdapter.CustomerProductListener {

    RecyclerView productsrecyclerview;
    CustomerProductAdapter adapter;
    ArrayList<Product> allProducts = new ArrayList<>();
    CustomerActivity parent;

    EditText searchEditText;

    ArrayList<Button> filtersButtons = new ArrayList<>();

    String currentFilter = "";

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

        searchEditText = view.findViewById(R.id.searchBox);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                currentFilter = searchEditText.getText().toString();
                filterProducts();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

        filtersButtons.add(view.findViewById(R.id.shirtbtnfilter));
        filtersButtons.add(view.findViewById(R.id.jacketbtnfilter));
        filtersButtons.add(view.findViewById(R.id.dressbtnfilter));
        for (Button b : filtersButtons) {
            b.setOnClickListener(l -> {
                currentFilter = b.getText().toString();
                filterProducts();
            });
        }

        fetchCatalog();


        return view;
    }


    void fetchCatalog() {
        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference("products");


        // read all the data from CartProducts to avoid reading one-by-one
        productsRef.get().addOnSuccessListener(snapshot -> {
            for (DataSnapshot child : snapshot.getChildren()) {
                    Product item = child.getValue(Product.class);
                    allProducts.add(item);
                }
            filterProducts();
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



    void filterProducts() {

        ArrayList<CartProduct> userCartProducts = parent.getUser().getCartItems();

        // Make a map for effecient look-ups
        HashSet<String> cartProductsById = new HashSet<>();
        for (CartProduct cartProduct : userCartProducts) {
            cartProductsById.add(cartProduct.getProductId());
        }
        adapter.productList.clear();
        for (Product product : allProducts) {
            String id = product.getId();
            if (!cartProductsById.contains(id)) {
                if (product.getName().toLowerCase().contains(currentFilter.toLowerCase())) {
                    adapter.productList.add(product);
                }
            }

        }
        adapter.notifyDataSetChanged();
    }





}