package in.co.opensoftlab.yourstore.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import in.co.opensoftlab.yourstore.R;
import in.co.opensoftlab.yourstore.model.ChatMessage;
import in.co.opensoftlab.yourstore.views.ChatMessageView;


/**
 * Created by dewangankisslove on 14-01-2017.
 */

public class ChatMessageAdapterSeller extends RecyclerView.Adapter<ChatMessageAdapterSeller.MessageHolder> {
    private static final int MY_MESSAGE = 0, OTHER_MESSAGE = 1;

    private List<ChatMessage> mMessages = new ArrayList<>();
    private Context mContext;

    public ChatMessageAdapterSeller(Context context, List<ChatMessage> data) {
        mContext = context;
        mMessages = data;
    }

    @Override
    public int getItemCount() {
        return mMessages == null ? 0 : mMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage item = mMessages.get(position);

        if (item.isMine()) return MY_MESSAGE;
        else return OTHER_MESSAGE;
    }

    @Override
    public MessageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == MY_MESSAGE) {
            return new MessageHolder(LayoutInflater.from(mContext).inflate(R.layout.item_buyer_msg, parent, false));
        } else {
            return new MessageHolder(LayoutInflater.from(mContext).inflate(R.layout.item_seller_msg, parent, false));
        }
    }

    public void add(ChatMessage message) {
        mMessages.add(message);
        notifyItemInserted(mMessages.size() - 1);
    }

    @Override
    public void onBindViewHolder(MessageHolder holder, int position) {
        ChatMessage chatMessage = mMessages.get(position);
        if (chatMessage.isGroupHeader() == true) {
            holder.tvDate.setVisibility(View.VISIBLE);
            holder.chatMessageView.setVisibility(View.GONE);
            holder.tvDate.setText(chatMessage.getDate());
        } else {
            holder.tvDate.setVisibility(View.GONE);
            holder.chatMessageView.setVisibility(View.VISIBLE);
            holder.tvMessage.setText(chatMessage.getContent());
            holder.tvTime.setText(chatMessage.getTime());
        }


    }

    class MessageHolder extends RecyclerView.ViewHolder {
        TextView tvMessage, tvTime, tvDate;
        LinearLayout chatMessageView;

        MessageHolder(View itemView) {
            super(itemView);
            tvMessage = (TextView) itemView.findViewById(R.id.tv_message);
            tvTime = (TextView) itemView.findViewById(R.id.tv_time);
            tvDate = (TextView) itemView.findViewById(R.id.tv_date);
            chatMessageView = (LinearLayout) itemView.findViewById(R.id.chatMessageView);
        }
    }
}
