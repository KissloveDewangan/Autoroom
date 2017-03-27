package in.co.opensoftlab.yourstore.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import in.co.opensoftlab.yourstore.R;
import in.co.opensoftlab.yourstore.adapter.ChatMessageAdapter;
import in.co.opensoftlab.yourstore.model.ChatMessage;
import in.co.opensoftlab.yourstore.model.ChatModel;
import in.co.opensoftlab.yourstore.model.MsgItem;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by dewangankisslove on 14-01-2017.
 */

public class ChattingActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ImageView mButtonSend;
    private EditText mEditTextMessage;
    private CircleImageView sellerImg;
    private TextView sellerName;
    private TextView storeName;
    private TextView sellerStatus;
    private ImageView close;

    List<ChatMessage> chatMessageList = new ArrayList<>();
    List<String> dates = new ArrayList<>();

    private ChatMessageAdapter mAdapter;
    private Bundle bundle;

    private SwipeRefreshLayout swipeContainer;

    private DatabaseReference mDatabase;
    private DatabaseReference mChatSeller;
    private DatabaseReference mChatBuyer;
    ValueEventListener valueEventListener;
    ValueEventListener chatStatusListener;

    private FirebaseAuth mAuth;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bundle = getIntent().getExtras().getBundle("openChat");

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference(getResources().getString(R.string.server_msg)).child(bundle.getString("chatId"));
        mChatSeller = FirebaseDatabase.getInstance().getReference(getResources().getString(R.string.server_chat)).child("seller").child(bundle.getString("sellerId"));
        mChatBuyer = FirebaseDatabase.getInstance().getReference(getResources().getString(R.string.server_chat)).child("buyer").child(getUserUID());

        mChatBuyer.child(bundle.getString("chatId")).child("unreadMsg").setValue(0);

        close = (ImageView) findViewById(R.id.iv_close);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mButtonSend = (ImageView) findViewById(R.id.b_send);
        mEditTextMessage = (EditText) findViewById(R.id.et_message);
        sellerImg = (CircleImageView) findViewById(R.id.iv_profile_pic);
        sellerName = (TextView) findViewById(R.id.tv_name);
        storeName = (TextView) findViewById(R.id.tv_store_name);
        sellerStatus = (TextView) findViewById(R.id.tv_online);
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        chatStatusListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                    if (dataSnapshot.child("metaInfo").getValue().toString().contains("Online"))
                        sellerStatus.setBackgroundResource(R.drawable.blue_circle_view);
                    else
                        sellerStatus.setBackgroundResource(R.drawable.grey_circle_view);
                } else {
                    sellerStatus.setBackgroundResource(R.drawable.grey_circle_view);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                sellerStatus.setBackgroundResource(R.drawable.grey_circle_view);
            }
        };

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("allMsg", dataSnapshot.toString());
                if (dataSnapshot.getValue() != null) {
                    chatMessageList.clear();
                    dates.clear();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        MsgItem msgItem = postSnapshot.getValue(MsgItem.class);
                        String newDate = msgItem.getDate();
                        if (!dates.contains(newDate)) {
                            dates.add(newDate);
                            ChatMessage message;
                            if (msgItem.getWriterId().contentEquals(getUserUID()))
                                message = new ChatMessage(newDate, true);
                            else
                                message = new ChatMessage(newDate, false);
                            chatMessageList.add(message);
                        }
                        ChatMessage chatMessage;
                        if (msgItem.getWriterId().contentEquals(getUserUID()))
                            chatMessage = new ChatMessage(msgItem.getContent(), true, msgItem.getTimeStamp(), msgItem.getDate(), postSnapshot.getKey());
                        else
                            chatMessage = new ChatMessage(msgItem.getContent(), false, msgItem.getTimeStamp(), msgItem.getDate(), postSnapshot.getKey());
                        if (!chatMessageList.contains(chatMessage))
                            chatMessageList.add(chatMessage);
                    }
                    mAdapter.notifyDataSetChanged();
                    int temp = bundle.getInt("noUnread") + 1;
                    mRecyclerView.smoothScrollToPosition(chatMessageList.size() - temp);
                }
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                swipeContainer.setRefreshing(false);
            }
        };

        Picasso.with(this)
                .load(bundle.getString("sellerUrl"))
                .into(sellerImg);
        sellerName.setText(bundle.getString("sellerName"));
        storeName.setText(bundle.getString("storeName"));

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new ChatMessageAdapter(this, chatMessageList);
        mRecyclerView.setAdapter(mAdapter);

        mButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = mEditTextMessage.getText().toString();
                if (TextUtils.isEmpty(message)) {
                    return;
                }
                sendMessage(message);
                mEditTextMessage.setText("");
            }
        });

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                mDatabase.orderByKey().limitToLast(200).addListenerForSingleValueEvent(valueEventListener);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mChatSeller.removeEventListener(chatStatusListener);
        mDatabase.removeEventListener(valueEventListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mChatSeller.child(bundle.getString("chatId")).addValueEventListener(chatStatusListener);
        mDatabase.orderByKey().limitToLast(200).addValueEventListener(valueEventListener);
        mChatBuyer.child(bundle.getString("chatId")).child("metaInfo").setValue("Online");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mChatBuyer.child(bundle.getString("chatId")).child("metaInfo").setValue("Offline");
    }

    private void sendMessage(String message) {
        String time = new SimpleDateFormat("hh:mm aa", Locale.getDefault()).format(new Date());
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c.getTime());
        String tempId = mDatabase.push().getKey();
        ChatMessage chatMessage = new ChatMessage(message, true, time, formattedDate, tempId);
        mAdapter.add(chatMessage);
        mAdapter.notifyItemInserted(chatMessageList.size() - 1);
        mRecyclerView.smoothScrollToPosition(chatMessageList.size() - 1);
        MsgItem msgItem = new MsgItem(getUserUID(), message, time, formattedDate);
        mDatabase.child(tempId).setValue(msgItem);
        updateNoUnreads(mChatSeller, 1);

        //mimicOtherMessage(message);
    }

    private void updateNoUnreads(DatabaseReference ref, final int nos) {
        ref.child(bundle.getString("chatId")).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Log.d("unreadUpdate", String.valueOf(nos));
                if (nos == 1) {
                    ChatModel p = mutableData.getValue(ChatModel.class);
                    if (p == null) {
                        return Transaction.success(mutableData);
                    }
                    if (!p.getMetaInfo().contains("Online"))
                        p.setUnreadMsg(p.getUnreadMsg() + 1);
                    mutableData.setValue(p);
                }
                // Set value and report transaction success

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
            }
        });
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }

    public String getUserUID() {
        return mAuth.getCurrentUser().getUid();
    }
}
