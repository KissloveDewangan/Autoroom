package in.co.opensoftlab.yourstore.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import in.co.opensoftlab.yourstore.R;

/**
 * Created by dewangankisslove on 30-01-2017.
 */
public class NoViewListAdapter extends RecyclerView
        .Adapter<NoViewListAdapter
        .DataObjectHolder> {
    private List<String> productNames = new ArrayList<>();
    private List<Integer> noViews = new ArrayList<>();
    private static NoViewListAdapter.MyClickListener myClickListener;
    private static Context context;

    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {

        TextView productName, noViews;

        public DataObjectHolder(View itemView) {
            super(itemView);
            productName = (TextView) itemView.findViewById(R.id.tv_product_name);
            noViews = (TextView) itemView.findViewById(R.id.tv_total_views);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getPosition(), v);
        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public NoViewListAdapter(Context context, List<String> productNames, List<Integer> noViews) {
        this.context = context;
        this.productNames = productNames;
        this.noViews = noViews;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.no_view_item, parent, false);
        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        holder.productName.setText(productNames.get(position));
        holder.noViews.setText("" + noViews.get(position));

    }

    @Override
    public int getItemCount() {
        return productNames.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }

}
