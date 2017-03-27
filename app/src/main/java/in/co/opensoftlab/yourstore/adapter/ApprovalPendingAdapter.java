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
import java.util.Locale;

import in.co.opensoftlab.yourstore.R;
import in.co.opensoftlab.yourstore.model.ListingItem;


/**
 * Created by dewangankisslove on 01-01-2017.
 */

public class ApprovalPendingAdapter extends RecyclerView
        .Adapter<ApprovalPendingAdapter
        .DataObjectHolder> {
    private List<ListingItem> filterProducts = new ArrayList<>();
    private ArrayList<ListingItem> arraylist;
    private static ApprovalPendingAdapter.MyClickListener myClickListener;
    private static Context context;

    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {

        ImageView productImg, more, edit;
        TextView productName, productPrice, productInfo;

        public DataObjectHolder(View itemView) {
            super(itemView);
            productName = (TextView) itemView.findViewById(R.id.tv_product_name);
            productPrice = (TextView) itemView.findViewById(R.id.tv_product_price);
            productImg = (ImageView) itemView.findViewById(R.id.iv_product);
            productInfo = (TextView) itemView.findViewById(R.id.tv_info);
            more = (ImageView) itemView.findViewById(R.id.iv_more);
            edit = (ImageView) itemView.findViewById(R.id.iv_edit);
            more.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getPosition(), v);
        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public ApprovalPendingAdapter(Context context, List<ListingItem> filterProducts) {
        this.context = context;
        this.filterProducts = filterProducts;
    }

    public void setData(List<ListingItem> filterProducts) {
        this.arraylist = new ArrayList<ListingItem>();
        this.arraylist.addAll(filterProducts);
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listing_row_object, parent, false);
        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        holder.edit.setVisibility(View.GONE);
        holder.more.setImageResource(R.drawable.ic_remove_red_eye_black_24dp);

        Picasso.with(context)
                .load(filterProducts.get(position).getProductUrl())
                .into(holder.productImg);
        holder.productName.setText(filterProducts.get(position).getProductName());
        holder.productPrice.setText("" + filterProducts.get(position).getProductPrice());
        holder.productInfo.setText(filterProducts.get(position).getInfo());
    }

    @Override
    public int getItemCount() {
        return filterProducts.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }

}