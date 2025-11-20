package com.example.shoppygo;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class AddProduct extends AppCompatActivity {

    ImageView prodimage, prodimage2;
    ImageView colorWhite, colorBlack, colorBrown, colorGreen, colorGray, colorBeige;
    EditText addproductName, addproductRef, addproductPrice;
    Button SelectImage, addxs, adds, addm, addl, addxl, addbtn;
    DatabaseReference databaseProduct;
    StorageReference storageReference;
    Uri imageuri;

    List<String> selectedColor = new ArrayList<>();
    List<String> selectedSizes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_product);

        prodimage = findViewById(R.id.addimageProd);
        prodimage2 = findViewById(R.id.addimageProd2);

        addproductName = findViewById(R.id.addproductName);
        addproductRef = findViewById(R.id.addproductRef);
        addproductPrice= findViewById(R.id.addproductPrice);

        colorWhite = findViewById(R.id.addcolorWhite);
        colorBlack = findViewById(R.id.addcolorBlack);
        colorBrown = findViewById(R.id.addcolorBrown);
        colorGreen = findViewById(R.id.addcolorGreen);
        colorGray = findViewById(R.id.addcolorGray);
        colorBeige = findViewById(R.id.addcolorBeige);

        SelectImage = findViewById(R.id.SelectImagebtn);

        addxs = findViewById(R.id.addxs);
        adds = findViewById(R.id.adds);
        addm = findViewById(R.id.addm);
        addl = findViewById(R.id.addl);
        addxl = findViewById(R.id.addxl);

        addbtn = findViewById(R.id.addbtn);

        databaseProduct = FirebaseDatabase.getInstance().getReference("products");
        storageReference = FirebaseStorage.getInstance().getReference("product_image");




        SelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImageLauncher.launch("image/*");
            }
        });

        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProduct();
            }
        });


        addxs.setOnClickListener(v -> editSize(addxs));
        adds.setOnClickListener(v -> editSize(adds));
        addm.setOnClickListener(v -> editSize(addm));
        addl.setOnClickListener(v -> editSize(addl));
        addxl.setOnClickListener(v -> editSize(addxl));

        setupColorClick(colorWhite, "#FFFFFF");
        setupColorClick(colorBlack, "#000000");
        setupColorClick(colorBrown, "#7f520a");
        setupColorClick(colorGreen, "#10470f");
        setupColorClick(colorGray, "#d8d8d8");
        setupColorClick(colorBeige, "#e8e3c2");

        showColors();

    }

    private final ActivityResultLauncher<String> selectImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    imageuri = uri;
                    prodimage.setImageURI(uri);
                }

            });

    private void assignColor(ImageView img, String hex) {
        GradientDrawable drawable = (GradientDrawable) img.getBackground();
        drawable.setColor(Color.parseColor(hex));
        img.setAlpha(selectedColor.contains(hex) ? 1f : 0.3f);
    }
    private void showColors() {
        if (selectedColor == null) return;

        assignColor(colorWhite, "#FFFFFF");
        assignColor(colorBlack, "#000000");
        assignColor(colorBrown, "#7f520a");
        assignColor(colorGreen, "#10470f");
        assignColor(colorGray, "#d8d8d8");
        assignColor(colorBeige, "#e8e3c2");
    }
    private void setupColorClick(ImageView imgView, String colorHex) {
        imgView.setOnClickListener(v -> {
            if (selectedColor.contains(colorHex)) {
                selectedColor.remove(colorHex);
                imgView.setAlpha(0.3f);
            } else {
                selectedColor.add(colorHex);
                imgView.setAlpha(1f);
            }
        });
    }
    private void editSize(Button button){
        String size = button.getText().toString();

        if (selectedSizes.contains(size)){
            selectedSizes.remove(size);
            button.setTextColor(Color.LTGRAY);
        } else {
            selectedSizes.add(size);
            button.setTextColor(Color.BLACK);
        }
    }
    private void saveProduct() {
        String name = addproductName.getText().toString().trim();
        String reference = addproductRef.getText().toString().trim();
        String priceStr = addproductPrice.getText().toString().toString();


        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(reference) || TextUtils.isEmpty(priceStr) || imageuri == null) {
            Toast.makeText(this, "All fields must be completed", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedColor == null) {
            Toast.makeText(this, "Please select a color", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = Double.parseDouble(priceStr);

        String id = databaseProduct.push().getKey();

        if (id == null) {
            return;
        }

        StorageReference fileRef = storageReference.child(id + ".jpg");
        UploadTask uploadTask = fileRef.putFile(imageuri);
        uploadTask.addOnCompleteListener(task ->
                fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();

                    Product product = new Product(id, name, reference, price, imageUrl, selectedColor, selectedSizes);
                    databaseProduct.child(id).setValue(product).addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Success product added", Toast.LENGTH_SHORT).show();
                        finish();
                    });

                })

        );
    }
}