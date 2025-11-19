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

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class ProductAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Product> productList;

    public ProductAdapter(Context c, ArrayList<Product> list){
        context = c;
        productList = list;
    }

    @Override
    public int getCount() {
        return productList.size();
    }

    @Override
    public Object getItem(int position) {
        return productList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.product_item,parent, false);

        }
        Product product = productList.get(position);

        ImageView prodimage = convertView.findViewById(R.id.imageProd);
        ImageView prodimage2 = convertView.findViewById(R.id.imageProd2);
        TextView productName = convertView.findViewById(R.id.productName);
        TextView productRef = convertView.findViewById(R.id.productRef);
        TextView productPrice = convertView.findViewById(R.id.productPrice);

        ImageView colorWhite = convertView.findViewById(R.id.colorWhite);
        ImageView colorBlack = convertView.findViewById(R.id.colorBlack);
        ImageView colorBrown = convertView.findViewById(R.id.colorBrown);
        ImageView colorGreen = convertView.findViewById(R.id.colorGreen);
        ImageView colorGray = convertView.findViewById(R.id.colorGray);
        ImageView colorBeige = convertView.findViewById(R.id.colorBeige);

        Button xs = convertView.findViewById(R.id.xs);
        Button s = convertView.findViewById(R.id.s);
        Button m = convertView.findViewById(R.id.m);
        Button l = convertView.findViewById(R.id.l);
        Button xl = convertView.findViewById(R.id.xl);

        productName.setText(product.getName());
        productRef.setText(product.getProductRef());
        productPrice.setText("$" + product.getPrice());


        if (product.getImageURL() !=null && !product.getImageURL().isEmpty()){
            new ImageLoadTask(product.getImageURL(),prodimage).execute();
        }else {
            prodimage.setImageResource(android.R.drawable.ic_menu_report_image);
        }
        //COMO PONGO UNA SEGUNDA IMAGEN

        GradientDrawable drawableWhite = (GradientDrawable) colorWhite.getBackground();
        drawableWhite.setColor(Color.parseColor("#FFFFFF"));
        colorWhite.setVisibility(product.getColor().contains("#FFFFFF") ? View.VISIBLE : View.GONE);

        GradientDrawable drawableBlack = (GradientDrawable) colorBlack.getBackground();
        drawableBlack.setColor(Color.parseColor("#000000"));
        colorBlack.setVisibility(product.getColor().contains("#000000") ? View.VISIBLE : View.GONE);

        GradientDrawable drawableBrown = (GradientDrawable) colorBrown.getBackground();
        drawableBrown.setColor(Color.parseColor("#7f520a"));
        colorBrown.setVisibility(product.getColor().contains("#7f520a") ? View.VISIBLE : View.GONE);

        GradientDrawable drawableGreen = (GradientDrawable) colorGreen.getBackground();
        drawableGreen.setColor(Color.parseColor("#10470f"));
        colorGreen.setVisibility(product.getColor().contains("#10470f") ? View.VISIBLE : View.GONE);

        GradientDrawable drawableGray = (GradientDrawable) colorGray.getBackground();
        drawableGray.setColor(Color.parseColor("#d8d8d8"));
        colorGray.setVisibility(product.getColor().contains("#d8d8d8") ? View.VISIBLE : View.GONE);

        GradientDrawable drawableBeige = (GradientDrawable) colorBeige.getBackground();
        drawableBeige.setColor(Color.parseColor("#e8e3c2"));
        colorBeige.setVisibility(product.getColor().contains("#e8e3c2") ? View.VISIBLE : View.GONE);


        List<String> productSizes = product.getitemsize();

        updateSizeButton(xs, productSizes);
        updateSizeButton(s, productSizes);
        updateSizeButton(m, productSizes);
        updateSizeButton(l, productSizes);
        updateSizeButton(xl, productSizes);


        return convertView;
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

    private void updateSizeButton(Button button, List<String> availableSizes) {
        String size = button.getText().toString();
        if (availableSizes != null && availableSizes.contains(size)) {
            button.setBackgroundColor(Color.GRAY);
            button.setTextColor(Color.WHITE);
        } else {
            button.isOpaque();
            button.setForeground(context.getDrawable(R.drawable.unavailable_size));
        }
    }
}
