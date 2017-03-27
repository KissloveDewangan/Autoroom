package in.co.opensoftlab.yourstore.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
import in.co.opensoftlab.yourstore.activity.SellerChattingActivity;
import in.co.opensoftlab.yourstore.adapter.ChatListAdapterSeller;
import in.co.opensoftlab.yourstore.model.ChatItemSeller;
import in.co.opensoftlab.yourstore.model.ChatModel;
import in.co.opensoftlab.yourstore.utils.PrefUtils;


/**
 * Created by dewangankisslove on 17-12-2016.
 */

public class InboxFragmentSeller extends Fragment {

    RecyclerView recyclerView;
    List<ChatItemSeller> chatItems = new ArrayList<>();
    LinearLayoutManager linearLayoutManager;
    ChatListAdapterSeller chatListAdapter;

    private DatabaseReference mDatabase;
    Query query;
    ValueEventListener valueEventListener;

    private FirebaseAuth mAuth;
    AVLoadingIndicatorView loadingIndicatorView;
    CardView noMsgCard;
    TextView myListings;

    public interface onMyListings {
        public void listingEvent();
    }

    onMyListings listingEvent;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listingEvent = (onMyListings) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onListingEventListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference(getResources().getString(R.string.server_chat)).child("seller").child(getUserUID());
        query = mDatabase.orderByChild("block").equalTo("false");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inbox, container, false);
        initUI(view);
        return view;
    }

    private void initUI(final View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        loadingIndicatorView = (AVLoadingIndicatorView) view.findViewById(R.id.avi);
        noMsgCard = (CardView) view.findViewById(R.id.cv_msg2);
        myListings = (TextView) view.findViewById(R.id.tv_my_listings);

        noMsgCard.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        loadingIndicatorView.smoothToShow();
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        chatListAdapter = new ChatListAdapterSeller(getActivity().getApplicationContext(), chatItems);
        recyclerView.setAdapter(chatListAdapter);

        chatListAdapter.setOnItemClickListener(new ChatListAdapterSeller.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                if (v.getId() == R.id.iv_more) {
                    showPopup(v, position);
                } else {
                    if (isNetworkConnected()) {
                        Bundle bundle = new Bundle();
                        bundle.putString("buyerUrl", chatItems.get(position).getPersonUrl());
                        bundle.putString("buyerName", chatItems.get(position).getPersonName());
                        bundle.putString("chatId", chatItems.get(position).getChatId());
                        bundle.putString("title", chatItems.get(position).getTitle());
                        bundle.putString("buyerId", chatItems.get(position).getPersonId());
                        bundle.putInt("noUnread", chatItems.get(position).getUnreads());
                        Intent intent = new Intent(getActivity().getApplicationContext(), SellerChattingActivity.class);
                        intent.putExtra("openChat", bundle);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), "No internet connection!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        myListings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listingEvent.listingEvent();
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
                    noMsgCard.setVisibility(View.VISIBLE);

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
        popup.getMenuInflater().inflate(R.menu.inbox_menu, popup.getMenu());
        popup.getMenu().findItem(R.id.block).setTitle(Html.fromHtml("<font color='#000000'>Block"));
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.block) {
                    if (isNetworkConnected()) {
                        mDatabase.child(chatItems.get(position).getChatId()).child("block").setValue("true");
                        if (PrefUtils.getFromPrefs(getActivity().getApplicationContext(), "sellerBlockInfo", "yes").contentEquals("yes")) {
                            final AlertDialog.Builder alertDiallogBuilder = new AlertDialog.Builder(getContext());
                            alertDiallogBuilder.setTitle(Html.fromHtml("<font color='#212121'><b>Info"));
                            alertDiallogBuilder.setCancelable(false);
                            alertDiallogBuilder.setMessage(Html.fromHtml("<font color=\"#424242\">You can unblock the buyer by going to Account -> Settings -> Blocked buyers.</font>"));
                            alertDiallogBuilder.setPositiveButton(Html.fromHtml("<font color=\"#212121\"><b>Got it</b></font>"), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    PrefUtils.saveToPrefs(getActivity().getApplicationContext(), "sellerBlockInfo", "no");
                                    dialog.dismiss();
                                }
                            });
                            final AlertDialog alertDialog = alertDiallogBuilder.create();
                            alertDialog.show();
                        }
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), "No internet connection!", Toast.LENGTH_SHORT).show();
                    }
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

    private boolean isNetworkConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    public String getUserUID() {
        return mAuth.getCurrentUser().getUid();
    }

    public String getUserName() {
        return mAuth.getCurrentUser().getDisplayName();
    }

    public String getUserUri() {
        return mAuth.getCurrentUser().getPhotoUrl().toString();
    }
}
