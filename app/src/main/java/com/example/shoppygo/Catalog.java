package com.example.shoppygo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Catalog extends AppCompatActivity {

    ListView listViewProduct;
    DatabaseReference databaseProduct;
    ArrayList<Product> productList;
    ProductAdapter adapter;
    Button addproduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_catalog);

        listViewProduct = findViewById(R.id.productinfo);
        databaseProduct = FirebaseDatabase.getInstance().getReference("products");
        addproduct = findViewById(R.id.addproduct);

        addproduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Catalog.this, AddProduct.class));
            }
        });

        productList = new ArrayList<>();
        adapter = new ProductAdapter(this, productList);
        listViewProduct.setAdapter(adapter);

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
    }
}