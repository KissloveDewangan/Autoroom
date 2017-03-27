package in.co.opensoftlab.yourstore.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import in.co.opensoftlab.yourstore.R;

/**
 * Created by dewangankisslove on 04-03-2017.
 */

public class ListCarImgAdapter extends RecyclerView
        .Adapter<ListCarImgAdapter
        .DataObjectHolder> {
    private List<String> downloadUrls = new ArrayList<>();
    private static ListCarImgAdapter.MyClickListener myClickListener;
    private static Context context;

    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {

        ImageView carImg;
        CircleImageView removeCar;

        public DataObjectHolder(View itemView) {
            super(itemView);
            carImg = (ImageView) itemView.findViewById(R.id.carPreview);
            removeCar = (CircleImageView) itemView.findViewById(R.id.iv_remove);

            removeCar.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getPosition(), v);
        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public ListCarImgAdapter(Context context, List<String> downloadUrls) {
        this.context = context;
        this.downloadUrls = downloadUrls;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_img_row_layout, parent, false);
        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        Picasso.with(context)
                .load(downloadUrls.get(position))
                .into(holder.carImg);
    }

    @Override
    public int getItemCount() {
        return downloadUrls.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }

}
