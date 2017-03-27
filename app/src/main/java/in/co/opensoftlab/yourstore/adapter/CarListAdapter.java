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
import in.co.opensoftlab.yourstore.model.CarItem;

/**
 * Created by dewangankisslove on 09-12-2016.
 */

public class CarListAdapter extends RecyclerView
        .Adapter<CarListAdapter
        .DataObjectHolder> {
    private List<CarItem> carItems = new ArrayList<>();
    private static CarListAdapter.MyClickListener myClickListener;
    private static Context context;
    int layout;

    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {

        ImageView productImg;
        TextView productName, productPrice, productInfo;

        public DataObjectHolder(View itemView) {
            super(itemView);
            productName = (TextView) itemView.findViewById(R.id.tv_product_name);
            productPrice = (TextView) itemView.findViewById(R.id.tv_product_price);
            productImg = (ImageView) itemView.findViewById(R.id.iv_product);
            productInfo = (TextView) itemView.findViewById(R.id.tv_info);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getPosition(), v);
        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public CarListAdapter(Context context, int layout, List<CarItem> carItems) {
        this.context = context;
        this.carItems = carItems;
        this.layout = layout;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {

        Picasso.with(context)
                .load(carItems.get(position).getProductUrl().split("::")[0])
                .into(holder.productImg);
        holder.productName.setText(carItems.get(position).getProductName());
        holder.productPrice.setText("" + carItems.get(position).getProductPrice());
        holder.productInfo.setText("" + carItems.get(position).getInfo());
    }

    @Override
    public int getItemCount() {
        return carItems.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }

}
