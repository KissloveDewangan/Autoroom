package in.co.opensoftlab.yourstore.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import in.co.opensoftlab.yourstore.R;
import in.co.opensoftlab.yourstore.model.NotifyItemSeller;


/**
 * Created by dewangankisslove on 27-01-2017.
 */
public class OrderListAdapterSeller extends RecyclerView
        .Adapter<OrderListAdapterSeller
        .DataObjectHolder> {
    private List<NotifyItemSeller> orderItems = new ArrayList<>();
    List<Integer> dates = new ArrayList<>();
    private static OrderListAdapterSeller.MyClickListener myClickListener;
    private static Context context;

    public static class DataObjectHolder extends RecyclerView.ViewHolder implements View
            .OnClickListener {

        TextView buyerName, msg, date, time;
        ImageView buyerImg;
        LinearLayout accept, reject, reply;

        public DataObjectHolder(View itemView) {
            super(itemView);
            buyerName = (TextView) itemView.findViewById(R.id.tv_name);
            buyerImg = (ImageView) itemView.findViewById(R.id.iv_profile_pic);
            reply = (LinearLayout) itemView.findViewById(R.id.ll_reply);
            msg = (TextView) itemView.findViewById(R.id.tv_title);
            date = (TextView) itemView.findViewById(R.id.tv_date);
            time = (TextView) itemView.findViewById(R.id.tv_time);
            accept = (LinearLayout) itemView.findViewById(R.id.ll_accept);
            reject = (LinearLayout) itemView.findViewById(R.id.ll_reject);

            reply.setOnClickListener(this);
            accept.setOnClickListener(this);
            reject.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            myClickListener.onItemClick(getPosition(), view);
        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public OrderListAdapterSeller(Context context, List<NotifyItemSeller> orderItems, List<Integer> dates) {
        this.context = context;
        this.orderItems = orderItems;
        this.dates = dates;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_deal, parent, false);
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

        if (orderItems.get(position).getStatus().contentEquals("accepted") ||
                orderItems.get(position).getStatus().contentEquals("rejected")) {
            holder.accept.setVisibility(View.GONE);
            holder.reject.setVisibility(View.GONE);
            holder.reply.setVisibility(View.GONE);
        } else {
            if (orderItems.get(position).getRequestType().contentEquals("reqMob")) {
                holder.accept.setVisibility(View.GONE);
                holder.reject.setVisibility(View.GONE);
                holder.reply.setVisibility(View.VISIBLE);
            } else {
                holder.reply.setVisibility(View.GONE);
                holder.accept.setVisibility(View.VISIBLE);
                holder.reject.setVisibility(View.VISIBLE);
            }
        }
        holder.buyerName.setText(orderItems.get(position).getBuyerName());
        holder.msg.setText(orderItems.get(position).getMsg());
        holder.date.setText(orderItems.get(position).getRequestDate().split(" ")[0]);
        holder.time.setText(orderItems.get(position).getRequestDate().split(" ")[1] + " " +
                orderItems.get(position).getRequestDate().split(" ")[2]);
        Picasso.with(context)
                .load(orderItems.get(position).getBuyerUrl())
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
