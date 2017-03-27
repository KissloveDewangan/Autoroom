package in.co.opensoftlab.yourstore.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import in.co.opensoftlab.yourstore.R;
import in.co.opensoftlab.yourstore.model.WishlistItem;

/**
 * Created by dewangankisslove on 01-01-2017.
 */

public class WishListAdapter extends RecyclerView
        .Adapter<WishListAdapter
        .DataObjectHolder> {
    private List<WishlistItem> products = new ArrayList<>();
    private static WishListAdapter.MyClickListener myClickListener;
    private static Context context;


    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {

        ImageView productImg, fav;
        TextView productName, productPrice, productInfo;

        public DataObjectHolder(View itemView) {
            super(itemView);
            productName = (TextView) itemView.findViewById(R.id.tv_product_name);
            productPrice = (TextView) itemView.findViewById(R.id.tv_product_price);
            productImg = (ImageView) itemView.findViewById(R.id.iv_product);
            productInfo = (TextView) itemView.findViewById(R.id.tv_info);
            fav = (ImageView) itemView.findViewById(R.id.iv_fav);
            itemView.setOnClickListener(this);
            fav.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getPosition(), v);
        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public WishListAdapter(Context context, List<WishlistItem> products) {
        this.context = context;
        this.products = products;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wishlist_object, parent, false);
        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        Picasso.with(context)
                .load(products.get(position).getProductUrl())
                .into(holder.productImg);
        holder.productName.setText(products.get(position).getProductName());
        holder.productPrice.setText("" + products.get(position).getProductPrice());
        holder.productInfo.setText(products.get(position).getInfo());
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }

}