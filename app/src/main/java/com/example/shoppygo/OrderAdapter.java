package com.example.shoppygo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private Context context;
    private ArrayList<Order> orderList;
    private OnOrderActionListener listener;

    public interface OnOrderActionListener {
        void onCancel(Order order);
    }

    public OrderAdapter(Context c, ArrayList<Order> list, OnOrderActionListener l) {
        context = c;
        orderList = list;
        listener = l;
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderId;
        TextView orderDate;
        TextView customerName;
        TextView address;
        TextView itemsCount;
        Button cancelOrder;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderId = itemView.findViewById(R.id.tvOrderId);
            orderDate = itemView.findViewById(R.id.tvOrderDate);
            customerName = itemView.findViewById(R.id.tvCustomerName);
            address = itemView.findViewById(R.id.tvAddress);
            itemsCount = itemView.findViewById(R.id.tvItemsCount);
            cancelOrder = itemView.findViewById(R.id.cancelOrder);
        }
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_item, parent, false);
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

        holder.customerName.setText("Customer: " + order.getCustomerName());

        holder.address.setText("Address: " + order.getAddress());

        int count = (order.getItems() != null) ? order.getItems().size() : 0;
        holder.itemsCount.setText("Items: " + count);

        holder.cancelOrder.setOnClickListener(v -> listener.onCancel(order));
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }
}
