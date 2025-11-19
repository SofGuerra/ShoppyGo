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

        List<String> selectedSizes = new ArrayList<>();

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

        GradientDrawable drawableWhite = (GradientDrawable) colorWhite.getBackground();
        drawableWhite.setColor(Color.parseColor("#FFFFFF"));

        GradientDrawable drawableBlack = (GradientDrawable) colorBlack.getBackground();
        drawableBlack.setColor(Color.parseColor("#000000"));

        GradientDrawable drawableBrown = (GradientDrawable) colorBrown.getBackground();
        drawableBrown.setColor(Color.parseColor("#7f520a"));

        GradientDrawable drawableGreen = (GradientDrawable) colorGreen.getBackground();
        drawableGreen.setColor(Color.parseColor("#10470f"));

        GradientDrawable drawableGray = (GradientDrawable) colorGray.getBackground();
        drawableGray.setColor(Color.parseColor("#d8d8d8"));

        GradientDrawable drawableBeige = (GradientDrawable) colorBeige.getBackground();
        drawableBeige.setColor(Color.parseColor("#e8e3c2"));


        colorWhite.setOnClickListener(v -> {
            if (!selectedColor.contains("#FFFFFF")) {
                selectedColor.add("#FFFFFF");
            } else {
                selectedColor.remove("#FFFFFF");
            }
        });

        colorBlack.setOnClickListener(v -> {
            if (!selectedColor.contains("#000000")) {
                selectedColor.add("#000000");
            } else {
                selectedColor.remove("#000000");
            }
        });

        colorBrown.setOnClickListener(v -> {
            if (!selectedColor.contains("#7f520a")) {
                selectedColor.add("#7f520a");
            }
            else {
                selectedColor.remove("#7f520a");
            }
        });

        colorGreen.setOnClickListener(v -> {
            if (!selectedColor.contains("#10470f")) {
                selectedColor.add("#10470f");
            }
            else {
                selectedColor.remove("#10470f");
            }
        });

        colorGray.setOnClickListener(v -> {
            if (!selectedColor.contains("#d8d8d8")) {
                selectedColor.add("#d8d8d8");
            }
            else {
                selectedColor.remove("#d8d8d8");
            }
        });

        colorBeige.setOnClickListener(v -> {
            if (!selectedColor.contains("#e8e3c2")) {
                selectedColor.add("#e8e3c2");
            }
            else {
                selectedColor.remove("#e8e3c2");
            }
        });
    }

    private final ActivityResultLauncher<String> selectImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    imageuri = uri;
                    prodimage.setImageURI(uri);
                }

            });

    private void editSize(Button button){
        String size = button.getText().toString();

        if (selectedSizes.contains(size)){
            selectedSizes.remove(size);
            button.setTextColor(Color.GRAY);
        } else {
            selectedSizes.add(size);
            button.setTextColor(Color.GREEN);
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