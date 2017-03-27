package in.co.opensoftlab.yourstore.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import in.co.opensoftlab.yourstore.R;

/**
 * Created by dewangankisslove on 20-01-2017.
 */
public class FeaturesAdapter extends RecyclerView
        .Adapter<FeaturesAdapter
        .DataObjectHolder> {
    private List<String> val;
    private List<Integer> attr;
    private Context context;

    public static class DataObjectHolder extends RecyclerView.ViewHolder {

        TextView value;
        ImageView attribute;

        public DataObjectHolder(View itemView) {
            super(itemView);
            value = (TextView) itemView.findViewById(R.id.tv_val);
            attribute = (ImageView) itemView.findViewById(R.id.iv_attr);
        }
    }

    public FeaturesAdapter(Context context, List<String> val, List<Integer> attr) {
        this.context = context;
        this.val = val;
        this.attr = attr;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feature_item, parent, false);
        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        holder.value.setText(val.get(position));
        holder.attribute.setImageResource(attr.get(position));
    }

    @Override
    public int getItemCount() {
        return val.size();
    }

}
