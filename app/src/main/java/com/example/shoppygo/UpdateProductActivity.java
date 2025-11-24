package com.example.shoppygo;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class UpdateProductActivity extends AppCompatActivity {

    ImageView updateimageProd, updateimageProd2;
    EditText updateproductName, updateproductRef, updateproductPrice;
    ImageView colorWhite, colorBlack, colorBrown, colorGreen, colorGray, colorBeige;
    Button updatexs, updates, updatem, updatel, updatexl, savechangesbtn, updateImagebtn;
    String imgURL;
    ArrayList<String> productSizes = new ArrayList<>();
    ArrayList<String> productColors = new ArrayList<>();
    DatabaseReference databaseProduct;
    StorageReference storageReference;
    Uri newImageuri;

    Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_product);


        updateimageProd = findViewById(R.id.updateimageProd);
        //updateimageProd2 = findViewById(R.id.updateimageProd2);

        updateImagebtn = findViewById(R.id.updateImagebtn);

        updateproductName = findViewById(R.id.updateproductName);
        updateproductRef = findViewById(R.id.updateproductRef);
        updateproductPrice = findViewById(R.id.updateproductPrice);

        colorWhite = findViewById(R.id.colorWhite);
        colorBlack = findViewById(R.id.colorBlack);
        colorBrown = findViewById(R.id.colorBrown);
        colorGreen = findViewById(R.id.colorGreen);
        colorGray = findViewById(R.id.colorGray);
        colorBeige = findViewById(R.id.colorBeige);

        updatexs = findViewById(R.id.updatexs);
        updates = findViewById(R.id.updates);
        updatem = findViewById(R.id.updatem);
        updatel = findViewById(R.id.updatel);
        updatexl = findViewById(R.id.updatexl);
        savechangesbtn = findViewById(R.id.savechangesbtn);

        databaseProduct = FirebaseDatabase.getInstance().getReference("products");
        storageReference = FirebaseStorage.getInstance().getReference();

        product =(Product) getIntent().getSerializableExtra("product");


        updateImagebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImageLauncher.launch("image/*");
            }
        });

        updatexs.setOnClickListener(v -> editSize(updatexs));
        updates.setOnClickListener(v -> editSize(updates));
        updatem.setOnClickListener(v -> editSize(updatem));
        updatel.setOnClickListener(v -> editSize(updatel));
        updatexl.setOnClickListener(v -> editSize(updatexl));

        setupColorClick(colorWhite, "#FFFFFF");
        setupColorClick(colorBlack, "#000000");
        setupColorClick(colorBrown, "#7f520a");
        setupColorClick(colorGreen, "#10470f");
        setupColorClick(colorGray, "#d8d8d8");
        setupColorClick(colorBeige, "#e8e3c2");

        savechangesbtn.setOnClickListener(v -> saveChanges());
    }


    private final ActivityResultLauncher<String> selectImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    newImageuri = uri;
                    updateimageProd.setImageURI(uri);
                } else {
                    newImageuri = null;
                }
            });

    private void editSize(Button button) {
        String size = button.getText().toString();

        if (productSizes.contains(size)) {
            productSizes.remove(size);
            button.setTextColor(Color.GRAY);
        } else {
            productSizes.add(size);
            button.setTextColor(Color.BLACK);
        }
    }

    private void setupColorClick(ImageView imgView, String colorHex) {
        imgView.setOnClickListener(v -> {
            if (productColors.contains(colorHex)) {
                productColors.remove(colorHex);
                imgView.setAlpha(0.3f);
            } else {
                productColors.add(colorHex);
                imgView.setAlpha(1f);
            }
        });
    }

    private void markSelectedSizes() {
        markSizeButton(updatexs, "XS");
        markSizeButton(updates, "S");
        markSizeButton(updatem, "M");
        markSizeButton(updatel, "L");
        markSizeButton(updatexl, "XL");
    } //para visualizar lo que hay en el update

    private void markSizeButton(Button btn, String size) {
        if (productSizes.contains(size)) {
            btn.setTextColor(Color.BLACK);
        } else {
            btn.setTextColor(Color.LTGRAY);
        }
    }
    private void markSelectedColors() {
        if (productColors == null) return;

        markColor(colorWhite, "#FFFFFF");
        markColor(colorBlack, "#000000");
        markColor(colorBrown, "#7f520a");
        markColor(colorGreen, "#10470f");
        markColor(colorGray, "#d8d8d8");
        markColor(colorBeige, "#e8e3c2");
    }
    private void markColor(ImageView img, String hex) {
        GradientDrawable drawable = (GradientDrawable) img.getBackground();
        drawable.setColor(Color.parseColor(hex));
        img.setAlpha(productColors.contains(hex) ? 1f : 0.3f);
    }
    private void saveChanges() {
        product.setName(updateproductName.getText().toString().trim());
        product.setProductRef(updateproductRef.getText().toString().trim());
        product.setPrice(Double.parseDouble(updateproductPrice.getText().toString()));

        if (productColors == null) {
            Toast.makeText(this, "Please select a color", Toast.LENGTH_SHORT).show();
            return;
        }

        product.setColor(productColors);
        product.setitemsize(productSizes);

        if (newImageuri != null) {
            StorageReference fileRef = storageReference.child("product_image/" + product.getId() + ".jpg");

            fileRef.putFile(newImageuri).addOnSuccessListener(taskSnapshot ->
                    fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        imgURL = uri.toString();
                        product.setImageURL(imgURL);
                    })
            );
        }

        databaseProduct.child(product.getId()).setValue(product).addOnSuccessListener(aVoid -> {
            Toast.makeText(this, "Product updated", Toast.LENGTH_SHORT).show();

            Intent resultIntent = new Intent();
            resultIntent.putExtra("product", product);
            setResult(RESULT_OK, resultIntent);

            finish();
        });
    }
}