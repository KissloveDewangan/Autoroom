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
import in.co.opensoftlab.yourstore.model.SpecificationItem;

/**
 * Created by dewangankisslove on 27-12-2016.
 */

public class SpecificationListAdapter extends RecyclerView
        .Adapter<SpecificationListAdapter
        .DataObjectHolder> {
    private List<SpecificationItem> featureItems = new ArrayList<>();
    private static SpecificationListAdapter.MyClickListener myClickListener;
    private static Context context;

    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {

        TextView attrName, attrValue;

        public DataObjectHolder(View itemView) {
            super(itemView);
            attrName = (TextView) itemView.findViewById(R.id.feature_name);
            attrValue = (TextView) itemView.findViewById(R.id.feature_value);
            //itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getPosition(), v);
        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public SpecificationListAdapter(Context context, List<SpecificationItem> featureItems) {
        this.context = context;
        this.featureItems = featureItems;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_specification_object, parent, false);
        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        holder.attrName.setText(featureItems.get(position).getName());
        holder.attrValue.setText(featureItems.get(position).getValue());
    }

    @Override
    public int getItemCount() {
        return featureItems.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }

}
