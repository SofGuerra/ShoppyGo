package com.example.shoppygo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class PreviousOrderAdapter extends RecyclerView.Adapter<PreviousOrderAdapter.OrderViewHolder> {

    private Context context;
    private ArrayList<Order> orderList;
    private IOrderActionListener listener;

    private HashMap<String, Product> allProducts;

    public interface IOrderActionListener {
        void onOrderAgain(Order order);
    }

    public PreviousOrderAdapter(Context c, ArrayList<Order> list, IOrderActionListener l, HashMap<String, Product> allProducts) {
        context = c;
        orderList = list;
        listener = l;
        this.allProducts = allProducts;
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderId;
        TextView orderDate;
        TextView address;
        TextView itemsCount;
        LinearLayout productsContainer;
        Button orderAgain;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderId = itemView.findViewById(R.id.tvOrderId);
            orderDate = itemView.findViewById(R.id.tvOrderDate);
            address = itemView.findViewById(R.id.tvAddress);
            itemsCount = itemView.findViewById(R.id.tvItemsCount);
            productsContainer = itemView.findViewById(R.id.productsContainer);
            orderAgain = itemView.findViewById(R.id.orderAgain);
        }
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.previous_order_card, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        holder.orderId.setText("Order ID: " + order.getId());

        if (order.getDate() != null) {
            Date date = new Date(order.getDate());
            DateFormat df = DateFormat.getDateTimeInstance(
                    DateFormat.SHORT,
                    DateFormat.SHORT,
                    Locale.getDefault()
            );
            holder.orderDate.setText("Date: " + df.format(date));
        } else {
            holder.orderDate.setText("Date: -");
        }

        // Address
        holder.address.setText("Address: " + order.getAddress());

        // Products count
        int count = (order.getItems() != null) ? order.getItems().size() : 0;
        holder.itemsCount.setText("Items: " + count);

        // Fill product list dynamically
        holder.productsContainer.removeAllViews();

        if (order.getItems() != null && !order.getItems().isEmpty()) {
            for (CartProduct item : order.getItems()) {

                Product product = allProducts.get(item.getProductId());

                TextView productTitle = new TextView(context);
                productTitle.setText(product.getName() + " - " + item.getQty());
                productTitle.setTextSize(14);
                productTitle.setPadding(0, 10, 0, 0);
                holder.productsContainer.addView(productTitle);

                TextView color = new TextView(context);
                color.setText("Color: " + item.getColor());
                color.setTextSize(13);
                color.setPadding(20, 2, 0, 0);
                holder.productsContainer.addView(color);

                TextView size = new TextView(context);
                size.setText("Size: " + item.getSize());
                size.setTextSize(13);
                size.setPadding(20, 2, 0, 10);
                holder.productsContainer.addView(size);
            }
        }

        holder.orderAgain.setOnClickListener(v -> listener.onOrderAgain(order));
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }
}

