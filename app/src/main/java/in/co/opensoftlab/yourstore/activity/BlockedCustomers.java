package in.co.opensoftlab.yourstore.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

import in.co.opensoftlab.yourstore.R;
import in.co.opensoftlab.yourstore.adapter.ChatListAdapterSeller;
import in.co.opensoftlab.yourstore.model.ChatItemSeller;
import in.co.opensoftlab.yourstore.model.ChatModel;

/**
 * Created by dewangankisslove on 08-02-2017.
 */
public class BlockedCustomers extends AppCompatActivity {
    RecyclerView recyclerView;
    List<ChatItemSeller> chatItems = new ArrayList<>();
    LinearLayoutManager linearLayoutManager;
    ChatListAdapterSeller chatListAdapter;

    String mode;
    private DatabaseReference mDatabase;
    Query query;
    ValueEventListener valueEventListener;

    private FirebaseAuth mAuth;
    AVLoadingIndicatorView loadingIndicatorView;
    ImageView back;

    LinearLayout info;
    TextView infoVal;
    TextView toolbarText;
    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocked_customer);
        mode = getIntent().getExtras().getBundle("bundle").getString("mode");
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference(getResources().getString(R.string.server_chat)).child(mode).child(getUserUID());
        query = mDatabase.orderByChild("block").equalTo("true");
        initUI();
    }

    private void initUI() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbarText = (TextView) findViewById(R.id.tv_toolbar_text);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        loadingIndicatorView = (AVLoadingIndicatorView) findViewById(R.id.avi);
        back = (ImageView) findViewById(R.id.iv_back);
        info = (LinearLayout) findViewById(R.id.ll_info);
        infoVal = (TextView) findViewById(R.id.tv_mode);

        if (mode.contentEquals("seller")){
            toolbarText.setText("Blocked Buyers");
            infoVal.setText("No buyer in your block list.");
        }else{
            toolbarText.setText("Blocked Sellers");
            infoVal.setText("No seller in your block list.");
        }

        info.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        loadingIndicatorView.smoothToShow();
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        chatListAdapter = new ChatListAdapterSeller(getApplicationContext(), chatItems);
        recyclerView.setAdapter(chatListAdapter);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        chatListAdapter.setOnItemClickListener(new ChatListAdapterSeller.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                if (v.getId() == R.id.iv_more) {
                    showPopup(v, position);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("buyerUrl", chatItems.get(position).getPersonUrl());
                    bundle.putString("buyerName", chatItems.get(position).getPersonName());
                    bundle.putString("chatId", chatItems.get(position).getChatId());
                    bundle.putString("title", chatItems.get(position).getTitle());
                    bundle.putString("buyerId", chatItems.get(position).getPersonId());
                    bundle.putInt("noUnread", chatItems.get(position).getUnreads());
                    Intent intent = new Intent(getApplicationContext(), SellerChattingActivity.class);
                    intent.putExtra("openChat", bundle);
                    startActivity(intent);
                }
            }
        });

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                chatItems.clear();
                chatListAdapter.notifyDataSetChanged();
                if (dataSnapshot != null)
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        // TODO: handle the post
                        ChatModel chatModel = postSnapshot.getValue(ChatModel.class);
                        //db.addChat(postSnapshot.getKey(), chatModel.getUnreadMsg());
                        ChatItemSeller chatItem = new ChatItemSeller(postSnapshot.getKey(), postSnapshot.getKey().split("K17D")[0], chatModel.getName(), chatModel.getUrl(), chatModel.getTitle(), chatModel.getUnreadMsg());
                        chatItems.add(chatItem);
                    }
                chatListAdapter.notifyDataSetChanged();
                loadingIndicatorView.hide();
                recyclerView.setVisibility(View.VISIBLE);
                if (chatItems.isEmpty())
                    info.setVisibility(View.VISIBLE);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                loadingIndicatorView.hide();
                recyclerView.setVisibility(View.VISIBLE);
                Log.d("loadPost:onCancelled", String.valueOf(databaseError.toException()));
                // ...
            }
        };
    }

    private void showPopup(View view, final int position) {
        Context wrapper = new ContextThemeWrapper(view.getContext(), R.style.PopupMenu);
        final PopupMenu popup = new PopupMenu(wrapper, view);
        popup.getMenuInflater().inflate(R.menu.blocked_customer_menu, popup.getMenu());
        popup.getMenu().findItem(R.id.block).setTitle(Html.fromHtml("<font color='#000000'>Unblock"));
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.block) {
                    mDatabase.child(chatItems.get(position).getChatId()).child("block").setValue("false");
                }
                popup.dismiss();
                return true;
            }
        });
        popup.show();
    }

    @Override
    public void onPause() {
        super.onPause();
        query.removeEventListener(valueEventListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        query.addValueEventListener(valueEventListener);
    }

    public String getUserUID() {
        return mAuth.getCurrentUser().getUid();
    }
}
