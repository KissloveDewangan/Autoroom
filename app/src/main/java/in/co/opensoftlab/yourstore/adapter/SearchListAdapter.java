package in.co.opensoftlab.yourstore.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import in.co.opensoftlab.yourstore.R;
import in.co.opensoftlab.yourstore.model.SearchItem;

/**
 * Created by dewangankisslove on 08-01-2017.
 */

public class SearchListAdapter extends RecyclerView
        .Adapter<SearchListAdapter
        .DataObjectHolder> {
    private List<SearchItem> searchItems = new ArrayList<>();
    private ArrayList<SearchItem> arraylist;
    private static SearchListAdapter.MyClickListener myClickListener;
    private static Context context;

    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {

        TextView name;

        public DataObjectHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.search_value);
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

    public SearchListAdapter(Context context, List<SearchItem> searchItems) {
        this.context = context;
        this.searchItems = searchItems;
        this.arraylist = new ArrayList<SearchItem>();
        this.arraylist.addAll(searchItems);
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_value_object, parent, false);
        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        holder.name.setText(searchItems.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return searchItems.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        SearchItem lastKnown = null;
        String[] arrString = charText.split(" ");
        searchItems.clear();
        if (charText.length() == 0) {
            searchItems.addAll(arraylist);
        } else {
            for (SearchItem fp : arraylist) {
                if (fp.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    lastKnown = fp;
                    searchItems.add(fp);
                }
                if (searchItems.size() >= 5) {
                    notifyDataSetChanged();
                    return;
                }
            }
            for (int i = 0; i < arrString.length; i++) {
                for (SearchItem fp : arraylist) {
                    if (fp.getName().toLowerCase(Locale.getDefault()).contains(arrString[i]) && !searchItems.contains(fp)) {
                        searchItems.add(fp);
                    }
                    if (searchItems.size() >= 5) {
                        notifyDataSetChanged();
                        return;
                    }
                }
            }
        }
        if (searchItems.size() <= 0 && lastKnown != null)
            searchItems.add(lastKnown);
        notifyDataSetChanged();
    }

}
