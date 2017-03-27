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
import in.co.opensoftlab.yourstore.model.ChatItemSeller;


/**
 * Created by dewangankisslove on 03-01-2017.
 */

public class ChatListAdapterSeller extends RecyclerView
        .Adapter<ChatListAdapterSeller
        .DataObjectHolder> {
    private List<ChatItemSeller> chatItems = new ArrayList<>();
    private static ChatListAdapterSeller.MyClickListener myClickListener;
    private static Context context;

    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {

        TextView name, title, noMsg;
        ImageView pic, more;

        public DataObjectHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.tv_name);
            title = (TextView) itemView.findViewById(R.id.tv_title);
            pic = (ImageView) itemView.findViewById(R.id.iv_profile_pic);
            more = (ImageView) itemView.findViewById(R.id.iv_more);
            noMsg = (TextView) itemView.findViewById(R.id.tv_nmsg);
            itemView.setOnClickListener(this);
//            title.setOnClickListener(this);
//            pic.setOnClickListener(this);
            more.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getPosition(), v);
        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public ChatListAdapterSeller(Context context, List<ChatItemSeller> chatItems) {
        this.context = context;
        this.chatItems = chatItems;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_row_layout, parent, false);
        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        holder.name.setText(chatItems.get(position).getPersonName());
        holder.title.setText(chatItems.get(position).getTitle());
        Picasso.with(context)
                .load(chatItems.get(position).getPersonUrl())
                .into(holder.pic);
        if (chatItems.get(position).getUnreads() == 0)
            holder.noMsg.setVisibility(View.GONE);
        else
            holder.noMsg.setVisibility(View.VISIBLE);
        holder.noMsg.setText("" + chatItems.get(position).getUnreads());

    }

    @Override
    public int getItemCount() {
        return chatItems.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }

}
