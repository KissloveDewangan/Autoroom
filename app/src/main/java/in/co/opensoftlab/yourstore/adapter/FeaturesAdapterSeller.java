package in.co.opensoftlab.yourstore.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import in.co.opensoftlab.yourstore.R;


/**
 * Created by dewangankisslove on 20-01-2017.
 */
public class FeaturesAdapterSeller extends RecyclerView
        .Adapter<FeaturesAdapterSeller
        .DataObjectHolder> {
    private List<String> features;
    private static Context context;

    public static class DataObjectHolder extends RecyclerView.ViewHolder {

        TextView name;

        public DataObjectHolder(View itemView) {
            super(itemView);
            //name = (TextView) itemView.findViewById(R.id.tv_feature);
        }
    }

    public FeaturesAdapterSeller(Context context, List<String> features) {
        this.context = context;
        this.features = features;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feature_item, parent, false);
        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        holder.name.setText(features.get(position));
    }

    @Override
    public int getItemCount() {
        return features.size();
    }

}
