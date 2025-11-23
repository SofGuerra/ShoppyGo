package com.example.shoppygo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private Context context;
    private ArrayList<Order> orderList;
    private OnOrderActionListener listener;

    public interface OnOrderActionListener {
        void onCancel (Order order);
    }
    public OrderAdapter(Context c, ArrayList<Order> list, OnOrderActionListener l) {
        context = c;
        orderList = list;
        listener = l;
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        ImageView ordimageProd;
        TextView poRef, poSize, poColor, deliverby;
        TextView shippingaddress, customer, shipby;
        Button cancelOrder;


        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);

            ordimageProd = itemView.findViewById(R.id.imageProd);
            poRef = itemView.findViewById(R.id.poref);
            poSize = itemView.findViewById(R.id.posize);
            poColor = itemView.findViewById(R.id.pocolor);
            deliverby = itemView.findViewById(R.id.deliverby);

            shippingaddress = itemView.findViewById(R.id.shippingaddress);
            customer = itemView.findViewById(R.id.customer);
            shipby = itemView.findViewById(R.id.shipby);

            cancelOrder = itemView.findViewById(R.id.cancelOrder);
        }
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        holder.poRef.setText("REF: " + order.getOrdreference());
        holder.poSize.setText("Size: " + order.getSize());
        holder.poColor.setText("Color: " + order.getColor());
        holder.deliverby.setText("Deliver by: " + order.getDeliverBy());

        holder.shippingaddress.setText("Address: " + order.getShippingAddress());
        holder.customer.setText("Customer: " + order.getCustomerName());
        holder.shipby.setText("Ship by: " + order.getShipBy());

        if(order.getOrdimageUrl() != null && !order.getOrdimageUrl().isEmpty()){
            new ImageLoadTask(order.getOrdimageUrl(), holder.ordimageProd).execute();
        } else {
            holder.ordimageProd.setImageResource(android.R.drawable.ic_menu_report_image);
        }

        holder.cancelOrder.setOnClickListener(v -> listener.onCancel(order));
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    private static class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {
        private String url;
        private ImageView imageView;

        public ImageLoadTask(String url, ImageView imageView){
            this.url = url;
            this.imageView = imageView;
        }


        @Override
        protected Bitmap doInBackground(Void... voids) {
            try{
                URL urlConnections = new URL(url);
                HttpsURLConnection connection = (HttpsURLConnection) urlConnections.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();

                return BitmapFactory.decodeStream(input);

            }catch(Exception e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap result) {

            if (result != null){
                imageView.setImageBitmap(result);
            }else{
                imageView.setImageResource(android.R.drawable.ic_menu_report_image);
            }
        }
    }
}
