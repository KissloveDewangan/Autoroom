package in.co.opensoftlab.yourstore.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

import in.co.opensoftlab.yourstore.R;
import in.co.opensoftlab.yourstore.activity.ProductDescription;
import in.co.opensoftlab.yourstore.adapter.WishListAdapter;
import in.co.opensoftlab.yourstore.model.WishlistItem;
import in.co.opensoftlab.yourstore.model.WishlistModel;
import in.co.opensoftlab.yourstore.views.SpacingItemDecoration;

/**
 * Created by dewangankisslove on 28-01-2017.
 */

public class WishlistFragment extends Fragment {
    RecyclerView recyclerView;
    List<WishlistItem> products = new ArrayList<>();
    LinearLayoutManager linearLayoutManager;
    WishListAdapter wishListAdapter;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    ValueEventListener valueEventListener;

    LinearLayout linearLayout;
    AVLoadingIndicatorView loadingIndicatorView;
    LinearLayout msg;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("wishlist");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wishlist, container, false);
        initUI(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                products.clear();
                wishListAdapter.notifyDataSetChanged();
                if (dataSnapshot != null)
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        // TODO: handle the post
                        WishlistModel whModel = postSnapshot.getValue(WishlistModel.class);
                        WishlistItem product = new WishlistItem(postSnapshot.getKey(), whModel.getProductType(),
                                whModel.getProductUrl(),
                                whModel.getProductName(), whModel.getProductPrice(), whModel.getInfo());
                        products.add(product);
                    }
                wishListAdapter.notifyDataSetChanged();
                loadingIndicatorView.hide();
                recyclerView.setVisibility(View.VISIBLE);
                if (products.isEmpty())
                    msg.setVisibility(View.VISIBLE);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.d("loadPost:onCancelled", String.valueOf(databaseError.toException()));
                loadingIndicatorView.hide();
                recyclerView.setVisibility(View.VISIBLE);
                // ...
            }
        };
        mDatabase.child(getUserUID()).addValueEventListener(valueEventListener);
    }

    private void initUI(final View view) {
        linearLayout = (LinearLayout) view.findViewById(R.id.linearLayout);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        loadingIndicatorView = (AVLoadingIndicatorView) view.findViewById(R.id.avi);
        msg = (LinearLayout) view.findViewById(R.id.ll_msg);

        msg.setVisibility(View.GONE);
        loadingIndicatorView.smoothToShow();
        recyclerView.setVisibility(View.GONE);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        wishListAdapter = new WishListAdapter(getActivity().getApplicationContext(), products);
        recyclerView.setAdapter(wishListAdapter);
        recyclerView.addItemDecoration(new SpacingItemDecoration(1, 8, false));

        wishListAdapter.setOnItemClickListener(new WishListAdapter.MyClickListener() {
            @Override
            public void onItemClick(final int position, View v) {
                if (v.getId() == R.id.iv_fav) {
                    if (isNetworkConnected()) {
                        final AlertDialog.Builder alertDiallogBuilder = new AlertDialog.Builder(getContext());
                        alertDiallogBuilder.setTitle(Html.fromHtml("<font color='#212121'><b>Alert"));
                        alertDiallogBuilder.setMessage(Html.fromHtml("<font color=\"#424242\">Are you sure want to remove this item from your wishlist?</font>"));
                        alertDiallogBuilder.setCancelable(true);
                        alertDiallogBuilder.setPositiveButton(Html.fromHtml("<font color=\"#212121\"><b>Yes</b></font>"), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mDatabase.child(getUserUID()).child(products.get(position).getId()).runTransaction(new Transaction.Handler() {
                                    @Override
                                    public Transaction.Result doTransaction(MutableData mutableData) {
                                        mutableData.setValue(null);
                                        return Transaction.success(mutableData);
                                    }

                                    @Override
                                    public void onComplete(DatabaseError databaseError, boolean b,
                                                           DataSnapshot dataSnapshot) {
                                        // Transaction completed
                                    }
                                });
                                dialog.dismiss();
                            }
                        });
                        alertDiallogBuilder.setNegativeButton(Html.fromHtml("<font color=\"#212121\"><b>Cancel</b></font>"), new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        final AlertDialog alertDialog = alertDiallogBuilder.create();
                        alertDialog.show();
                    }else {
                        showMsg(linearLayout);
                    }
                } else {
                    if (isNetworkConnected()) {
                        Bundle bundle = new Bundle();
                        bundle.putString("productId", products.get(position).getId());
                        bundle.putString("productType", products.get(position).getProductType());
                        Intent intent = new Intent(getActivity().getApplicationContext(), ProductDescription.class);
                        intent.putExtra("lookProduct", bundle);
                        startActivity(intent);
                    }else{
                        showMsg(linearLayout);
                    }
                }
            }
        });
    }

    public void showMsg(LinearLayout linearLayout) {
        Snackbar snackbar = Snackbar.make(linearLayout, "No internet connection!", Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    @Override
    public void onPause() {
        super.onPause();
        mDatabase.removeEventListener(valueEventListener);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public String getUserUID() {
        return mAuth.getCurrentUser().getUid();
    }
}
