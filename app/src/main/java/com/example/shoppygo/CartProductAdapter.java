package com.example.shoppygo;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CartProductAdapter extends RecyclerView.Adapter<CartProductAdapter.ViewHolder> {

    public interface ICartRecyclerViewListener {
        void OnItemIncrement(ProductPair pair);
        void OnItemDecrement(ProductPair pair);
        void OnItemTrash(ProductPair pair);
        void OnItemCheck(ProductPair pair, boolean checked);
    }

    public static class ProductPair {
        Product product;
        CartProduct cartProduct;

        public ProductPair(Product product, CartProduct cartProduct) {
            this.product = product;
            this.cartProduct = cartProduct;
        }
    }

    private List<ProductPair> productList;
    private ICartRecyclerViewListener listener;

    public CartProductAdapter(List<ProductPair> products, ICartRecyclerViewListener listener) {
        this.productList = products;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_cart_product_item, parent, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ProductPair productPair = productList.get(position);
        holder.productName.setText(productPair.product.getName());
        holder.productPrice.setText("$" + productPair.product.getPrice());
        holder.quantity.setText("" + productPair.cartProduct.getQty());
        holder.productImage.setImageURI(Uri.parse(productPair.product.getImageURL()));


        if (productPair.product.getImageURL() !=null && !productPair.product.getImageURL().isEmpty()){
            new ProductAdapter.ImageLoadTask(productPair.product.getImageURL(),holder.productImage).execute();
        }else {
            holder.productImage.setImageResource(android.R.drawable.ic_menu_report_image);
        }

        holder.pair = productPair;
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ProductPair pair;
        ImageView productImage;
        CheckBox selected;
        TextView productName;
        TextView productPrice;
        TextView quantity;
        Button plus;
        Button minus;
        Button trashbtn;

        public ViewHolder(View itemView, ICartRecyclerViewListener listener) {
            super(itemView);
            productImage = itemView.findViewById(R.id.image_product);
            selected = itemView.findViewById(R.id.checkbox_selected);
            productName = itemView.findViewById(R.id.text_product_name);
            productPrice = itemView.findViewById(R.id.text_product_price);
            plus = itemView.findViewById(R.id.button_increase);
            minus = itemView.findViewById(R.id.button_decrease);
            trashbtn = itemView.findViewById(R.id.trashbtn);
            quantity = itemView.findViewById(R.id.text_quantity);

            selected.setOnCheckedChangeListener((checkbox, b) -> listener.OnItemCheck(pair, b));
            trashbtn.setOnClickListener(btn -> listener.OnItemTrash(pair));
            minus.setOnClickListener(btn -> listener.OnItemDecrement(pair));
            plus.setOnClickListener(btn -> listener.OnItemIncrement(pair));
        }
    }
}

