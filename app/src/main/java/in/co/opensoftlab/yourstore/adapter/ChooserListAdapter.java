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
 * Created by dewangankisslove on 07-03-2017.
 */

public class ChooserListAdapter extends RecyclerView
        .Adapter<ChooserListAdapter
        .DataObjectHolder> {
    private List<String> listNames = new ArrayList<>();
    private static ChooserListAdapter.MyClickListener myClickListener;
    private static Context context;

    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {

        TextView name;

        public DataObjectHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.tv_name);
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

    public ChooserListAdapter(Context context, List<String> listNames) {
        this.context = context;
        this.listNames = listNames;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chooser_row_layout, parent, false);
        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        holder.name.setText(listNames.get(position));

    }

    @Override
    public int getItemCount() {
        return listNames.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }

}
