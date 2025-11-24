package com.example.shoppygo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

public class BuyAgainFragment extends Fragment implements PreviousOrderAdapter.IOrderActionListener {

    private RecyclerView recyclerView;

    ArrayList<Order> selectedOrders = new ArrayList<>();
    HashMap<String, Product> allProducts = new HashMap<>();

    CustomerActivity parent;

    public BuyAgainFragment(CustomerActivity parent) {
        this.parent = parent;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.previous_orders, container, false);
        recyclerView = view.findViewById(R.id.ordersRecyclerView);


        PreviousOrderAdapter adapter = new PreviousOrderAdapter(getContext(), selectedOrders, this, allProducts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        fetchOrders();

        return view;
    }

    void fetchOrders() {

        selectedOrders.clear();
        allProducts.clear();

        String customerId = parent.getUser().getId();

        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference("products");
        DatabaseReference ordersRef   = FirebaseDatabase.getInstance().getReference("Orders");

        productsRef.get().addOnSuccessListener(productSnapshot -> {

            for (DataSnapshot snap : productSnapshot.getChildren()) {
                Product p = snap.getValue(Product.class);
                if (p != null && p.getId() != null) {
                    allProducts.put(p.getId(), p);
                }
            }

            ordersRef.get().addOnSuccessListener(orderSnapshot -> {

                for (DataSnapshot child : orderSnapshot.getChildren()) {
                    Order order = child.getValue(Order.class);

                    System.out.println(order.getId());

                    if (order != null && order.getCustomerId() != null && order.getCustomerId().equals(customerId)) {
                        selectedOrders.add(order);
                    }
                }

                recyclerView.getAdapter().notifyDataSetChanged();

            });

        });
    }


    @Override
    public void onOrderAgain(Order order) {
        ArrayList<CartProduct> cart = parent.getUser().getCartItems();
        HashSet<String> productsSet = new HashSet<>();
        for (CartProduct orderedProduct : order.getItems()) {
            productsSet.add(orderedProduct.getProductId());


            // Skip if products with the same if is already in the cart
            if (cart.stream().anyMatch(p -> Objects.equals(p.getProductId(), orderedProduct.getProductId()))) {
                continue;
            }
            cart.add(orderedProduct);
        }

        parent.getUser().updateCartInFirebase();
        loadFragment(new CustomerCartFragment(parent, productsSet));
    }



    private void loadFragment(Fragment fragment) {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentCont, fragment)
                .addToBackStack(null)
                .commit();
    }
}
