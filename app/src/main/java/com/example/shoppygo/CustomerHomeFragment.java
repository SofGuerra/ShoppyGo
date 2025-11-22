package com.example.shoppygo;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;

public class CustomerHomeFragment extends Fragment {

    RecyclerView productsrecyclerview;

    public CustomerHomeFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.customer_home, container, false);

       productsrecyclerview = view.findViewById(R.id.customer_products);

       ArrayList<Product> testProducts = new ArrayList<>();
        Product p = new Product("1", "Product Name", "ref", 100.99, "https", Arrays.asList(new String[]{"red", "green"}), Arrays.asList(new String[]{"big", "small"}));
       testProducts.add(p);
       testProducts.add(p);
       testProducts.add(p);
       testProducts.add(p);
       testProducts.add(p);
       testProducts.add(p);

       CustomerProductAdapter adapter = new CustomerProductAdapter(testProducts);

       productsrecyclerview.setLayoutManager(new GridLayoutManager(getContext(), 2));

       productsrecyclerview.setAdapter(adapter);


        return view;
    }
}