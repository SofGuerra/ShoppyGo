package com.example.shoppygo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context; //inflar lyo,usar getDrawable etc
    private ArrayList<Product> productList;
    private OnProductActionListener listener;



    public interface OnProductActionListener {
        void onDelete(Product product);
        void onUpdate(Product product);
    }
    public ProductAdapter(Context c, ArrayList<Product> list, OnProductActionListener l){
        context = c;
        productList = list;
        listener = l;
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder{

        ImageView prodimage, prodimage2;
        TextView productName, productRef, productPrice;
        ImageView colorWhite, colorBlack, colorBrown, colorGreen, colorGray, colorBeige;
        Button xs, s, m, l, xl;
        Button updateBtn, deleteBtn;
        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            prodimage = itemView.findViewById(R.id.imageProd);
            prodimage2 = itemView.findViewById(R.id.imageProd2);
            productName = itemView.findViewById(R.id.productName);
            productRef = itemView.findViewById(R.id.productRef);
            productPrice = itemView.findViewById(R.id.productPrice);

            colorWhite = itemView.findViewById(R.id.colorWhite);
            colorBlack = itemView.findViewById(R.id.colorBlack);
            colorBrown = itemView.findViewById(R.id.colorBrown);
            colorGreen = itemView.findViewById(R.id.colorGreen);
            colorGray = itemView.findViewById(R.id.colorGray);
            colorBeige = itemView.findViewById(R.id.colorBeige);

            xs = itemView.findViewById(R.id.xs);
            s = itemView.findViewById(R.id.s);
            m = itemView.findViewById(R.id.m);
            l = itemView.findViewById(R.id.l);
            xl = itemView.findViewById(R.id.xl);

            updateBtn = itemView.findViewById(R.id.updatebtn);
            deleteBtn = itemView.findViewById(R.id.deleteprodbtn);
        }
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_item, parent, false);

        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.productName.setText(product.getName());
        holder.productRef.setText(product.getProductRef());
        holder.productPrice.setText("$" + product.getPrice());

        if (product.getImageURL() !=null && !product.getImageURL().isEmpty()){
            new ImageLoadTask(product.getImageURL(),holder.prodimage).execute();
        }else {
            holder.prodimage.setImageResource(android.R.drawable.ic_menu_report_image);
        }

//        if (product.getImageURL2() !=null && !product.getImageURL().isEmpty()){
//            new ImageLoadTask(product.getImageURL(),holder.prodimage2).execute();
//        }else {
//            holder.prodimage2.setImageResource(android.R.drawable.ic_menu_report_image);
//        }

        updateColor(holder.colorWhite, "#FFFFFF", product);
        updateColor(holder.colorBlack, "#000000", product);
        updateColor(holder.colorBrown, "#7f520a", product);
        updateColor(holder.colorGreen, "#10470f", product);
        updateColor(holder.colorGray, "#d8d8d8", product);
        updateColor(holder.colorBeige, "#e8e3c2", product);

        List<String> productSizes = product.getitemsize();

        updateSizeButton(holder.xs, productSizes);
        updateSizeButton(holder.s, productSizes);
        updateSizeButton(holder.m, productSizes);
        updateSizeButton(holder.l, productSizes);
        updateSizeButton(holder.xl, productSizes);

        holder.updateBtn.setOnClickListener(v -> listener.onUpdate(product));
        holder.deleteBtn.setOnClickListener(v -> listener.onDelete(product));
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

        private String url;
        private ImageView imageView;

        public ImageLoadTask(String url, ImageView imageView){
            this.url = url;
            this.imageView = imageView;
        }


        private static HashMap<String, Bitmap> cache = new HashMap<>();

        @Override
        protected Bitmap doInBackground(Void... voids) {
            if (cache.containsKey(url)) {
                return cache.get(url);
            }
            try{
                URL urlConnections = new URL(url);
                HttpsURLConnection connection = (HttpsURLConnection) urlConnections.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap result =  BitmapFactory.decodeStream(input);
                cache.put(url, result);
                return result;
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

    private void updateColor(ImageView view, String hex, Product product){
        GradientDrawable drawable = (GradientDrawable) view.getBackground();
        drawable.setColor(Color.parseColor(hex));
        view.setVisibility(product.getColor().contains(hex) ? View.VISIBLE : View.GONE);
    }

    private void updateSizeButton(Button button, List<String> availableSizes) {
        String size = button.getText().toString();
        if (availableSizes != null && availableSizes.contains(size)) {
            button.setBackgroundColor(Color.GRAY);
            button.setTextColor(Color.WHITE);
        } else {
            button.isOpaque();
        }
    }



}
