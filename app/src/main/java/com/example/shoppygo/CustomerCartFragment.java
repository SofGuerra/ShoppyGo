package com.example.shoppygo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class CustomerCartFragment extends Fragment implements CartProductAdapter.ICartRecyclerViewListener {
    ArrayList<CartProductAdapter.ProductPair> cartProducts;
    HashSet<String> checkedProducts = new HashSet<>();

    private PaymentSheet paymentSheet;

    private static final String EphericalKeyURL = "https://api.stripe.com/v1/ephemeral_keys";
    private static final String CustomersURL = "https://api.stripe.com/v1/customers";
    private static final String ClientSecretURL = "https://api.stripe.com/v1/payment_intents";


    RecyclerView recyclerviewcart;
    TextView subTotalTextBox;

    CustomerActivity parent;

    Button checkoutBtn;

    double subTotal;

    private String customerId;
    private String ephemeralKeySecret;
    private String clientSecret;


    public CustomerCartFragment(CustomerActivity parent) {
        this.parent = parent;
        this.cartProducts = new ArrayList<>();
        this.checkedProducts = new HashSet<>();
    }
    public CustomerCartFragment(CustomerActivity parent, HashSet<String> selectedProducts) {
        this.parent = parent;
        this.cartProducts = new ArrayList<>();
        this.checkedProducts = selectedProducts;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.customer_cart, container, false);

        recyclerviewcart = view.findViewById(R.id.recyclerViewCart);
        subTotalTextBox = view.findViewById(R.id.textSubtotal);

        // Stripe setup
        PaymentConfiguration.init(getContext(), StripeApiKey.PublishableKey);
        paymentSheet = new PaymentSheet(this, paymentSheetResult -> {
            onPaymentResult(paymentSheetResult);
        });

        fetchCart();

        CartProductAdapter adapter = new CartProductAdapter(cartProducts, checkedProducts, this);
        recyclerviewcart.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerviewcart.setAdapter(adapter);

        checkoutBtn = view.findViewById(R.id.buttonCheckout);
        checkoutBtn.setOnClickListener(e -> onCheckoutClicked());


        return view;
    }

    private void createCustomer() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                CustomersURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            customerId = object.getString("id");

                            Toast.makeText(parent, "Customer ID: " + customerId, Toast.LENGTH_SHORT).show();

                            // Now that CustomerId is available, fetch the Ephemeral Key
                            if (customerId != null && !customerId.isEmpty()) {
                                getEphemeralKey();
                            } else {
                                Toast.makeText(parent, "Failed to create customer", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(parent, "Error creating customer: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(parent, "Error: " + error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + StripeApiKey.SecretKey);
                return headers;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }

    private void getEphemeralKey() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                EphericalKeyURL,
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        // Use the secret, not the id
                        ephemeralKeySecret = object.getString("secret");

                        getClientSecret(customerId, ephemeralKeySecret);
                    } catch (JSONException e) {
                        Toast.makeText(parent, "Error fetching key: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(parent, "Error fetching ephemeral key: " + error.getLocalizedMessage(), Toast.LENGTH_SHORT).show()
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + StripeApiKey.SecretKey);
                headers.put("Stripe-Version", "2022-11-15");
                return headers;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("customer", customerId);
                return params;
            }
        };
        Volley.newRequestQueue(getContext()).add(request);
    }


    private void getClientSecret(String customerId, String ephemeralKeySecret) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                ClientSecretURL,
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        clientSecret = object.getString("client_secret");
                        presentPaymentSheet();
                    } catch (JSONException e) {
                        Toast.makeText(parent, "Error fetching client secret: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(parent, "Error fetching client secret: " + error.getLocalizedMessage(), Toast.LENGTH_SHORT).show()
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + StripeApiKey.SecretKey);
                return headers;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("amount", String.valueOf((int)(subTotal * 100))); // cents
                params.put("currency", "usd");
                params.put("customer", customerId);
                params.put("automatic_payment_methods[enabled]", "true");
                return params;
            }
        };
        Volley.newRequestQueue(getContext()).add(request);
    }



    private void presentPaymentSheet() {
        if (clientSecret == null || customerId == null || ephemeralKeySecret == null) {
            Toast.makeText(parent, "Payment data not ready", Toast.LENGTH_SHORT).show();
            return;
        }

        paymentSheet.presentWithPaymentIntent(
                clientSecret,
                new PaymentSheet.Configuration(
                        "ShoppyGo",
                        new PaymentSheet.CustomerConfiguration(
                                customerId,
                                ephemeralKeySecret
                        )
                )
        );
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
        checkedProducts.remove(pair.product.getId());
        recalculateSubtotal();
    }

    @Override
    public void OnItemCheck(CartProductAdapter.ProductPair pair, boolean checked) {
        if (pair == null) {
            return; // hotfix
        }
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

        if(parent.getUser().getAddress().isEmpty()){
            Toast.makeText(parent, "Please enter an address", Toast.LENGTH_SHORT).show();
            return;
        }
        createCustomer();
    }

    void onPaymentResult(PaymentSheetResult result) {
        if (result instanceof PaymentSheetResult.Completed) {

            HashMap<String, ArrayList<CartProduct>> groupedProd = new HashMap<>();

            for(CartProductAdapter.ProductPair pair: cartProducts){
                if(checkedProducts.contains(pair.product.getId())){
                    if (!groupedProd.containsKey(pair.product.getSeller())){
                        groupedProd.put(pair.product.getSeller(), new ArrayList<>());
                    }
                    groupedProd.get(pair.product.getSeller()).add(pair.cartProduct);
                }
            }

            DatabaseReference ordersDb = FirebaseDatabase.getInstance().getReference("Orders");
            for (Map.Entry<String, ArrayList<CartProduct>> pair : groupedProd.entrySet()) {
                DatabaseReference newOrder = ordersDb.push();
                Order order = new Order(newOrder.getKey(), parent.getUser().getId(), System.currentTimeMillis(), pair.getValue(), parent.getUser().getName(), parent.getUser().getAddress());
                newOrder.setValue(order);
            }
            for(int i = cartProducts.size() - 1; i >= 0 ; i--){

                if(checkedProducts.contains(cartProducts.get(i).product.getId())){
                    cartProducts.remove(i);
                }
            }
            cartProducts.removeIf(pair -> checkedProducts.contains(pair.product.getId()));
            parent.getUser().getCartItems().removeIf(item -> checkedProducts.contains(item.getProductId()));
            parent.getUser().updateCartInFirebase();
            recyclerviewcart.getAdapter().notifyDataSetChanged();
            checkedProducts.clear();



        } else if (result instanceof PaymentSheetResult.Canceled) {
        } else if (result instanceof PaymentSheetResult.Failed) {
        }
    }


}
