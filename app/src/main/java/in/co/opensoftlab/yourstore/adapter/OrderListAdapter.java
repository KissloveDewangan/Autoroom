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
import in.co.opensoftlab.yourstore.model.NotifyItemBuyer;

/**
 * Created by dewangankisslove on 27-01-2017.
 */
public class OrderListAdapter extends RecyclerView
        .Adapter<OrderListAdapter
        .DataObjectHolder> {
    private List<NotifyItemBuyer> orderItems = new ArrayList<>();
    List<Integer> dates = new ArrayList<>();
    private static OrderListAdapter.MyClickListener myClickListener;
    private static Context context;

    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {

        TextView buyerName, msg, text, date, time;
        ImageView buyerImg, sendMsg, call;
        View view;

        public DataObjectHolder(View itemView) {
            super(itemView);
            buyerName = (TextView) itemView.findViewById(R.id.tv_name);
            buyerImg = (ImageView) itemView.findViewById(R.id.iv_profile_pic);
            msg = (TextView) itemView.findViewById(R.id.tv_title);
            text = (TextView) itemView.findViewById(R.id.tv_text);
            date = (TextView) itemView.findViewById(R.id.tv_date);
            time = (TextView) itemView.findViewById(R.id.tv_time);
            sendMsg = (ImageView) itemView.findViewById(R.id.iv_send_msg);
            call = (ImageView) itemView.findViewById(R.id.iv_call);
            view = itemView.findViewById(R.id.view);

            sendMsg.setOnClickListener(this);
            call.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getPosition(), v);
        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public OrderListAdapter(Context context, List<NotifyItemBuyer> orderItems, List<Integer> dates) {
        this.context = context;
        this.orderItems = orderItems;
        this.dates = dates;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item, parent, false);
        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {

        if (dates.get(position) == 0) {
            holder.date.setVisibility(View.GONE);
        } else {
            holder.date.setVisibility(View.VISIBLE);
        }

        if (orderItems.get(position).getRequestType().contentEquals("reqMob")) {
            holder.text.setVisibility(View.VISIBLE);
            holder.text.setText("Call now");
            holder.sendMsg.setVisibility(View.GONE);
            holder.call.setVisibility(View.VISIBLE);
        } else {
            holder.text.setVisibility(View.VISIBLE);
            holder.text.setText("Send a message");
            holder.sendMsg.setVisibility(View.VISIBLE);
            holder.call.setVisibility(View.GONE);
        }
        holder.buyerName.setText(orderItems.get(position).getSellerName());
        holder.msg.setText(orderItems.get(position).getMsg());
        holder.date.setText(orderItems.get(position).getRequestDate().split(" ")[0]);
        holder.time.setText(orderItems.get(position).getRequestDate().split(" ")[1] + " " +
                orderItems.get(position).getRequestDate().split(" ")[2]);
        Picasso.with(context)
                .load(orderItems.get(position).getSellerUrl())
                .into(holder.buyerImg);

    }

    @Override
    public int getItemCount() {
        return orderItems.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }

}
