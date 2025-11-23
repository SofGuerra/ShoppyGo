package com.example.shoppygo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CustomerProductAdapter extends RecyclerView.Adapter<CustomerProductAdapter.ViewHolder> {

    public interface CustomerProductListener {
        void OnAddToCart(Product product);
    }

    public List<Product> productList;

    private CustomerProductListener listener;

    public CustomerProductAdapter(List<Product> products, CustomerProductListener listener) {
        this.productList = products;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_product_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.productName.setText(product.getName());
        holder.productPrice.setText("$" + product.getPrice());

        holder.productImage.setImageResource(android.R.drawable.ic_menu_report_image);
        if (product.getImageURL() !=null && !product.getImageURL().isEmpty()){
            new ProductAdapter.ImageLoadTask(product.getImageURL(),holder.productImage).execute();
        }

        holder.addToCart.setOnClickListener(e -> listener.OnAddToCart(product));
        holder.itemView.setOnClickListener(e -> listener.OnAddToCart(product));
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName;
        TextView productPrice;
        Button addToCart;

        public ViewHolder(View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.image_product);
            productName = itemView.findViewById(R.id.text_product_name);
            productPrice = itemView.findViewById(R.id.text_product_price);
            addToCart = itemView.findViewById(R.id.button_add_to_cart);

        }
    }
}

