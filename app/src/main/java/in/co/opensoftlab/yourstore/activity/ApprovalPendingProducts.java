package in.co.opensoftlab.yourstore.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
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
import android.widget.RelativeLayout;

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
import in.co.opensoftlab.yourstore.adapter.ApprovalPendingAdapter;
import in.co.opensoftlab.yourstore.adapter.ListingAdapter;
import in.co.opensoftlab.yourstore.model.ListingItem;
import in.co.opensoftlab.yourstore.model.ProductModel;
import in.co.opensoftlab.yourstore.views.SpacingItemDecoration;

/**
 * Created by dewangankisslove on 21-03-2017.
 */

public class ApprovalPendingProducts extends AppCompatActivity {

    LinearLayout linearLayout;
    RelativeLayout userInfo;
    ImageView back;
    List<ListingItem> listingItems = new ArrayList<>();
    List<ProductModel> productModels = new ArrayList<>();
    RecyclerView recyclerView;
    ApprovalPendingAdapter listingAdapter;
    LinearLayoutManager linearLayoutManager;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    Query query;
    ValueEventListener valueEventListener;
    AVLoadingIndicatorView loadingIndicatorView;
    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approval_pending_products);
        initUI();

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference(getResources().getString(R.string.server_products)).child("all").child("inactive");

        query = mDatabase.orderByChild("sellerId").equalTo(getUserUID());
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    userInfo.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);

                    listingItems.clear();
                    productModels.clear();

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        ProductModel products = postSnapshot.getValue(ProductModel.class);
                        if (products.getProductType().contentEquals("Car")) {
                            ListingItem listingItem = new ListingItem(products.getProductType(), postSnapshot.getKey(),
                                    products.getProductUrls().split("::")[0], products.getProductName(), products.getPrice(),
                                    products.getFuelType() + " - " + products.getDrivenDistance() + " kms - " + products.getModelYear());
                            listingItems.add(listingItem);
                        } else {
                            ListingItem listingItem = new ListingItem(products.getProductType(), postSnapshot.getKey(),
                                    products.getProductUrls().split("::")[0], products.getProductName(), products.getPrice(),
                                    products.getDrivenDistance() + " kms - " + products.getModelYear());
                            listingItems.add(listingItem);
                        }
                        productModels.add(products);
                    }
                    if (listingItems != null) {
                        listingAdapter.setData(listingItems);
                        listingAdapter.notifyDataSetChanged();
                    }
                } else {
                    userInfo.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }

                loadingIndicatorView.hide();
                recyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("dbError", databaseError.getMessage());
                loadingIndicatorView.hide();
                recyclerView.setVisibility(View.VISIBLE);
            }
        };
        if (isNetworkConnected())
            query.addListenerForSingleValueEvent(valueEventListener);
        else {
            showMsg(linearLayout);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isNetworkConnected()) {
            if (query != null)
                query.removeEventListener(valueEventListener);
        }
    }

    private void initUI() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        userInfo = (RelativeLayout) findViewById(R.id.rl_info);
        back = (ImageView) findViewById(R.id.iv_back);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        loadingIndicatorView = (AVLoadingIndicatorView) findViewById(R.id.avi);

        recyclerView.setVisibility(View.GONE);
        loadingIndicatorView.smoothToShow();
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        listingAdapter = new ApprovalPendingAdapter(getApplicationContext(), listingItems);
        recyclerView.setAdapter(listingAdapter);
        recyclerView.addItemDecoration(new SpacingItemDecoration(1, 8, false));
        listingAdapter.setOnItemClickListener(new ApprovalPendingAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                if (isNetworkConnected())
                    lookProduct(listingItems.get(position).getId(), listingItems.get(position).getProductType());
                else
                    showMsg(linearLayout);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private void lookProduct(String productId, String productType) {
        Bundle bundle = new Bundle();
        bundle.putString("productId", productId);
        bundle.putString("productType", productType);
        bundle.putString("childName", "inactive");
        Intent intent = new Intent(ApprovalPendingProducts.this, SellerProductDescription.class);
        intent.putExtra("lookProduct", bundle);
        startActivity(intent);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    public void showMsg(LinearLayout linearLayout) {
        Snackbar snackbar = Snackbar.make(linearLayout, "No internet connection!", Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    public String getUserUID() {
        return mAuth.getCurrentUser().getUid();
    }
}
