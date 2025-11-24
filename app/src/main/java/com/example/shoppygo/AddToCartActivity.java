package com.example.shoppygo;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class AddToCartActivity extends AppCompatActivity {

    ImageView prodimage;
    ImageView colorWhite, colorBlack, colorBrown, colorGreen, colorGray, colorBeige;
    TextView productName, productRef, productPrice;
    Button btnXS, btnS, btnM, btnL, btnXL, addToCartBtn;
    Product product;
    Customer customer;

    List<String> availableColors = new ArrayList<>();
    List<String> availableSizes = new ArrayList<>();

    ArrayList<Button> sizeButtons = new ArrayList<>();
    List<ImageView> colorButtons = new ArrayList<>();

    String selectedColor;
    String selectedSize;

    private static final float COLOR_ALPHA_DISABLED = 0.25f;
    private static final float COLOR_ALPHA_ENABLED = 0.6f;
    private static final float COLOR_ALPHA_SELECTED = 1.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_to_cart);

        prodimage = findViewById(R.id.addimageProd);



        productName = findViewById(R.id.addproductName);
        productRef = findViewById(R.id.addproductRef);
        productPrice = findViewById(R.id.addproductPrice);

        colorWhite = findViewById(R.id.addcolorWhite);
        colorBlack = findViewById(R.id.addcolorBlack);
        colorBrown = findViewById(R.id.addcolorBrown);
        colorGreen = findViewById(R.id.addcolorGreen);
        colorGray = findViewById(R.id.addcolorGray);
        colorBeige = findViewById(R.id.addcolorBeige);

        colorButtons.add(colorWhite);
        colorButtons.add(colorBlack);
        colorButtons.add(colorBrown);
        colorButtons.add(colorGreen);
        colorButtons.add(colorGray);
        colorButtons.add(colorBeige);

        btnXS = findViewById(R.id.addxs);
        btnS = findViewById(R.id.adds);
        btnM = findViewById(R.id.addm);
        btnL = findViewById(R.id.addl);
        btnXL = findViewById(R.id.addxl);

        sizeButtons.add(btnXS);
        sizeButtons.add(btnS);
        sizeButtons.add(btnM);
        sizeButtons.add(btnL);
        sizeButtons.add(btnXL);

        addToCartBtn = findViewById(R.id.addbtn);

        product = (Product) getIntent().getSerializableExtra("product");
        customer = (Customer) getIntent().getSerializableExtra("user");

        if (product == null || customer == null) {
            Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        prodimage.setImageURI(Uri.parse(product.getImageURL()));
        if (product.getImageURL() !=null && !product.getImageURL().isEmpty()){
            new SellerProductAdapter.ImageLoadTask(product.getImageURL(), prodimage).execute();
        }else {
            prodimage.setImageResource(android.R.drawable.ic_menu_report_image);
        }

        productName.setText(product.getName());
        productPrice.setText(String.format("$%.2f", product.getPrice()));

        availableColors = product.getColor();
        availableSizes = product.getitemsize();

        for (int i = 0; i < availableColors.size(); i++) {
            availableColors.set(i, availableColors.get(i).toLowerCase());
        }
        for (int i = 0; i < availableSizes.size(); i++) {
            availableSizes.set(i, availableSizes.get(i).toLowerCase());
        }

        setupColorView(colorWhite, "#FFFFFF");
        setupColorView(colorBlack, "#000000");
        setupColorView(colorBrown, "#7f520a");
        setupColorView(colorGreen, "#10470f");
        setupColorView(colorGray, "#d8d8d8");
        setupColorView(colorBeige, "#e8e3c2");

        for (Button btn : sizeButtons) {
            setupSizeButton(btn);
        }

        addToCartBtn.setOnClickListener(v -> {
            if (selectedColor == null || selectedSize == null) {
                Toast.makeText(this, "Please select one color and one size", Toast.LENGTH_SHORT).show();
                return;
            }
            customer.addCartProduct(new CartProduct(product.getId(), 1, selectedColor, selectedSize));
            customer.updateCartInFirebase();
            Toast.makeText(this, "Added to cart", Toast.LENGTH_SHORT).show();
            Intent resultIntent = new Intent();
            resultIntent.putExtra("user", customer);
            resultIntent.putExtra("product", product);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }

    private void setupColorView(ImageView colorView, String hexColor) {
        GradientDrawable drawable = (GradientDrawable) colorView.getBackground();
        drawable.setColor(Color.parseColor(hexColor));

        boolean available = availableColors.contains(hexColor.toLowerCase());

        if (!available) {
            setColorDisabled(colorView);
            colorView.setOnClickListener(null);
        } else {
            setColorEnabled(colorView);
            colorView.setOnClickListener(v -> {
                selectedColor = hexColor;
                updateColorButtonStyles(colorView);
            });
        }
    }

    private void updateColorButtonStyles(ImageView selected) {
        for (ImageView img : colorButtons) {
            if (!img.isEnabled()) {
                setColorDisabled(img);
            } else if (img == selected) {
                setColorSelected(img);
            } else {
                setColorEnabled(img);
            }
        }
    }

    private void setColorDisabled(ImageView view) {
        view.setEnabled(false);
        view.setAlpha(COLOR_ALPHA_DISABLED);
        GradientDrawable bg = (GradientDrawable) view.getBackground();
        bg.setStroke(1, Color.LTGRAY);
    }

    private void setColorEnabled(ImageView view) {
        view.setEnabled(true);
        view.setAlpha(COLOR_ALPHA_ENABLED);
        GradientDrawable bg = (GradientDrawable) view.getBackground();
        bg.setStroke(1, Color.TRANSPARENT);
    }

    private void setColorSelected(ImageView view) {
        view.setEnabled(true);
        view.setAlpha(COLOR_ALPHA_SELECTED);
        GradientDrawable bg = (GradientDrawable) view.getBackground();
        bg.setStroke(2, Color.GREEN);
    }

    private void setupSizeButton(Button sizeButton) {
        String size = sizeButton.getText().toString();

        if (!availableSizes.contains(size.toLowerCase())) {
            sizeButton.setEnabled(false);
            sizeButton.setTextColor(Color.LTGRAY);
        } else {
            sizeButton.setEnabled(true);
            sizeButton.setTextColor(Color.BLACK);
            sizeButton.setOnClickListener(v -> {
                selectedSize = size;
                updateSizeButtonStyles(sizeButton);
            });
        }
    }

    private void updateSizeButtonStyles(Button selected) {
        for (Button btn : sizeButtons) {
            if (!btn.isEnabled()) {
                btn.setTextColor(Color.LTGRAY);
            } else if (btn == selected) {
                btn.setTextColor(Color.GREEN);
            } else {
                btn.setTextColor(Color.BLACK);
            }
        }
    }
}
