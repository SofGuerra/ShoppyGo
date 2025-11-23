package com.example.shoppygo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class CustomerCartFragment extends Fragment implements CartProductAdapter.ICartRecyclerViewListener {
    ArrayList<CartProductAdapter.ProductPair> cartProducts;
    HashSet<String> checkedProducts = new HashSet<>();

    private PaymentSheet paymentSheet;

    RecyclerView recyclerviewcart;
    TextView subTotalTextBox;

    CustomerActivity parent;

    Button checkoutBtn;

    double subTotal;

    public CustomerCartFragment(CustomerActivity parent) {
        this.parent = parent;
        this.cartProducts = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.customer_cart, container, false);

        recyclerviewcart = view.findViewById(R.id.recyclerViewCart);
        subTotalTextBox = view.findViewById(R.id.textSubtotal);

        // Stripe setup
        PaymentConfiguration.init(getContext(), PublishableKey);
        paymentSheet = new PaymentSheet(this, paymentSheetResult -> {
            onPaymentResult(paymentSheetResult);
        });

        fetchCart();

        CartProductAdapter adapter = new CartProductAdapter(cartProducts, this);
        recyclerviewcart.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerviewcart.setAdapter(adapter);

        checkoutBtn = view.findViewById(R.id.buttonCheckout);
        checkoutBtn.setOnClickListener(e -> onCheckoutClicked());

        return view;
    }

    void fetchCart() {

        cartProducts = new ArrayList<>();
        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference("products");

        ArrayList<CartProduct> userCartProducts = parent.getUser().getCartItems();

        // Make a map for effecient look-ups
        HashMap<String, CartProduct> cartProductsById = new HashMap<>();
        for (CartProduct cartProduct : userCartProducts) {
            cartProductsById.put(cartProduct.getProductId(), cartProduct);
        }

        // read all the data from CartProducts to avoid reading one-by-one
        productsRef.get().addOnSuccessListener(snapshot -> {
            cartProducts.clear();
            for (DataSnapshot child : snapshot.getChildren()) {
                String id = child.getKey();
                if (cartProductsById.containsKey(id)) {
                    Product item = child.getValue(Product.class);
                    cartProducts.add(new CartProductAdapter.ProductPair(item, cartProductsById.get(id)));
                }
            }
            recyclerviewcart.getAdapter().notifyDataSetChanged();
            recalculateSubtotal();
        });

    }
    @Override
    public void OnItemIncrement(CartProductAdapter.ProductPair pair) {
        pair.cartProduct.setQty(pair.cartProduct.getQty() + 1);
        parent.getUser().updateCartQty(pair.cartProduct.getProductId(), pair.cartProduct.getQty());
        parent.getUser().updateCartInFirebase();
        recyclerviewcart.getAdapter().notifyDataSetChanged();
        recalculateSubtotal();
    }

    @Override
    public void OnItemDecrement(CartProductAdapter.ProductPair pair) {

        if (pair.cartProduct.getQty() <= 1) {
            OnItemTrash(pair);
            return;
        }

        pair.cartProduct.setQty(pair.cartProduct.getQty() - 1);
        parent.getUser().updateCartQty(pair.cartProduct.getProductId(), pair.cartProduct.getQty());
        parent.getUser().updateCartInFirebase();
        recyclerviewcart.getAdapter().notifyDataSetChanged();
        recalculateSubtotal();
    }

    @Override
    public void OnItemTrash(CartProductAdapter.ProductPair pair) {

        parent.getUser().removeCartProduct(pair.cartProduct.getProductId());
        cartProducts.remove(pair);
        parent.getUser().updateCartInFirebase();
        recyclerviewcart.getAdapter().notifyDataSetChanged();
        recalculateSubtotal();
    }

    @Override
    public void OnItemCheck(CartProductAdapter.ProductPair pair, boolean checked) {
        if (checked) {
            checkedProducts.add(pair.product.getId());
        } else {
            checkedProducts.remove(pair.product.getId());
        }
        recalculateSubtotal();
    }

    void recalculateSubtotal() {
        subTotal = 0;
        for (CartProductAdapter.ProductPair pair : cartProducts) {
            if (checkedProducts.contains(pair.product.getId())) {
                subTotal += pair.cartProduct.getQty() * pair.product.getPrice();
            }
        }
        subTotalTextBox.setText(String.format("$%.2f", subTotal));
    }

    void onCheckoutClicked() {

        if (checkedProducts.isEmpty()) {
            return;
        }

        paymentSheet.presentWithPaymentIntent(
                PublishableKey,
                new PaymentSheet.Configuration(
                        "My Shop",
                        null       // TODO: put customer
                )
        );
    }

    void onPaymentResult(PaymentSheetResult result) {
        if (result instanceof PaymentSheetResult.Completed) {
        } else if (result instanceof PaymentSheetResult.Canceled) {
        } else if (result instanceof PaymentSheetResult.Failed) {
        }
    }


}
