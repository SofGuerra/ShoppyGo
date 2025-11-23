package com.example.shoppygo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CustomerCartFragment extends Fragment implements CartProductAdapter.ICartRecyclerViewListener {


    ArrayList<CartProductAdapter.ProductPair> cartProducts;

    RecyclerView recyclerviewcart;
    TextView subTotal;

    CustomerActivity parent;

    public CustomerCartFragment(CustomerActivity parent) {
        this.parent = parent;
        this.cartProducts = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.customer_cart, container, false);

        recyclerviewcart = view.findViewById(R.id.recyclerViewCart);
        subTotal = view.findViewById(R.id.textSubtotal);

        fetchCart();

        CartProductAdapter adapter = new CartProductAdapter(cartProducts, this);
        recyclerviewcart.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerviewcart.setAdapter(adapter);


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
    }

    void recalculateSubtotal() {
        double amount = 0;
        for (CartProductAdapter.ProductPair pair : cartProducts) {
            amount += pair.cartProduct.getQty() * pair.product.getPrice();
        }
        subTotal.setText(String.format("$%.2f", amount));
    }


}
