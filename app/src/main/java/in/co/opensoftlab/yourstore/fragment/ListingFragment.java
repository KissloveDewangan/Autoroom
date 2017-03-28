package in.co.opensoftlab.yourstore.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.geofire.GeoFire;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.co.opensoftlab.yourstore.BuildConfig;
import in.co.opensoftlab.yourstore.R;
import in.co.opensoftlab.yourstore.activity.AddProductInfo;
import in.co.opensoftlab.yourstore.activity.EditListing;
import in.co.opensoftlab.yourstore.activity.SellerProductDescription;
import in.co.opensoftlab.yourstore.adapter.ListingAdapter;
import in.co.opensoftlab.yourstore.model.ListingItem;
import in.co.opensoftlab.yourstore.model.ProductModel;
import in.co.opensoftlab.yourstore.utils.PrefUtils;
import in.co.opensoftlab.yourstore.views.SpacingItemDecoration;

/**
 * Created by dewangankisslove on 19-12-2016.
 */

public class ListingFragment extends Fragment implements View.OnClickListener {
    LinearLayout linearLayout;
    RelativeLayout userInfo;
    //    RelativeLayout listings;
    CardView search;
    RecyclerView recyclerView;
    Button addProduct;
    ImageView productAdd;
    ImageView productSearch;
    ImageView cancelSearch;
    //    ImageView pendingList;
    EditText searchName;
    List<ListingItem> listingItems = new ArrayList<>();
    List<ProductModel> productModels = new ArrayList<>();
    ListingAdapter listingAdapter;
    LinearLayoutManager linearLayoutManager;
    Query query;
    ValueEventListener valueEventListener;
    AVLoadingIndicatorView loadingIndicatorView;
    MixpanelAPI mMixpanel;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference mSeller;
    private DatabaseReference mRecycle;
    private DatabaseReference mGeo;
    private GeoFire geoFire;
    private GeoFire geoFireAll;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference(getResources().getString(R.string.server_products)).child("all").child("live");
        mSeller = FirebaseDatabase.getInstance().getReference("sellers");
        mRecycle = FirebaseDatabase.getInstance().getReference("recycleBin").child(getUserUID()).child("products");
        mGeo = FirebaseDatabase.getInstance().getReference(getResources().getString(R.string.server_products)).child("geoData").child("live");
        query = mDatabase.orderByChild("sellerId").equalTo(getUserUID());


        String projectToken = BuildConfig.ANALYTICS_TOKEN;
        mMixpanel = MixpanelAPI.getInstance(getActivity().getApplicationContext(), projectToken);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listings, container, false);
        initUI(view);
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (query != null)
            query.removeEventListener(valueEventListener);

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("onResume", "onResume");
//        listings.setVisibility(View.VISIBLE);
        if (isNetworkConnected())
            query.addValueEventListener(valueEventListener);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    userInfo.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);

                    listingItems.clear();
                    productModels.clear();
                    //recyclerView.getRecycledViewPool().clear();

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

                    if (!listingItems.isEmpty())
                        PrefUtils.saveToPrefs(getActivity().getApplicationContext(), "showStats", "yes");

                    if (listingItems != null) {
//                        listingAdapter = new ListingAdapter(getActivity().getApplicationContext(), listingItems);
//                        recyclerView.setAdapter(listingAdapter);
                        listingAdapter.setData(listingItems);
                        listingAdapter.notifyDataSetChanged();
//                        listingAdapter.setOnItemClickListener(new ListingAdapter.MyClickListener() {
//                            @Override
//                            public void onItemClick(int position, View v) {
//                                if (v.getId() == R.id.iv_more) {
//                                    showPopup(v, position);
//                                }
//                            }
//                        });
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
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void trackListing(String eventName) {
        try {
            JSONObject props = new JSONObject();
            props.put("EventName", eventName);
            mMixpanel.track("Listing Activity", props);
        } catch (JSONException e) {
            Log.e("Autoroom", "Unable to add properties to JSONObject", e);
        }
    }

    private void initUI(View view) {
        linearLayout = (LinearLayout) view.findViewById(R.id.linearLayout);
        userInfo = (RelativeLayout) view.findViewById(R.id.rl_info);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        addProduct = (Button) view.findViewById(R.id.start_listing);
        productAdd = (ImageView) view.findViewById(R.id.iv_add);
        productSearch = (ImageView) view.findViewById(R.id.iv_search);
//        listings = (RelativeLayout) view.findViewById(R.id.rl_listings);
        search = (CardView) view.findViewById(R.id.cv_search);
        searchName = (EditText) view.findViewById(R.id.et_search);
        cancelSearch = (ImageView) view.findViewById(R.id.iv_close);
//        pendingList = (ImageView) view.findViewById(R.id.iv_waiting);
        loadingIndicatorView = (AVLoadingIndicatorView) view.findViewById(R.id.avi);

        search.setVisibility(View.GONE);
        addProduct.setVisibility(View.VISIBLE);

        recyclerView.setVisibility(View.GONE);
        loadingIndicatorView.smoothToShow();
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        listingAdapter = new ListingAdapter(getActivity().getApplicationContext(), listingItems);
        recyclerView.setAdapter(listingAdapter);
        recyclerView.addItemDecoration(new SpacingItemDecoration(1, 8, false));
        listingAdapter.setOnItemClickListener(new ListingAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                if (v.getId() == R.id.iv_more) {
                    showPopup(v, position);
                } else if (v.getId() == R.id.iv_edit) {
                    if (isNetworkConnected()) {
                        trackListing("Edit Listed Product");
                        Bundle bundle = new Bundle();
                        bundle.putString("productKey", listingItems.get(position).getId());
                        Intent intent = new Intent(getActivity().getApplicationContext(), EditListing.class);
                        intent.putExtra("bundle", bundle);
                        startActivity(intent);
                    } else {
                        showMsg(linearLayout);
                    }
                }
            }
        });

        addProduct.setOnClickListener(this);
        productAdd.setOnClickListener(this);
        productSearch.setOnClickListener(this);
//        pendingList.setOnClickListener(this);
        cancelSearch.setOnClickListener(this);

        searchName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String watchable = editable.toString();
                listingAdapter.filter(watchable);


            }
        });

        searchName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if ((keyEvent != null && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (i == EditorInfo.IME_ACTION_DONE)) {
                    InputMethodManager imm2 = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm2.hideSoftInputFromWindow(searchName.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start_listing:
                if (isNetworkConnected()) {
                    trackListing("Start Listing");
                    showSelectItemDialog();
//                    startAddingProduct();
                } else
                    showMsg(linearLayout);
                break;
//            case R.id.iv_waiting:
//                if (isNetworkConnected()) {
//                    startActivity(new Intent(getActivity().getApplicationContext(), ApprovalPendingProducts.class));
////                    startAddingProduct();
//                } else
//                    showMsg(linearLayout);
//                break;
            case R.id.iv_add:
                if (isNetworkConnected()) {
                    trackListing("Start Listing");
                    showSelectItemDialog();
//                    startAddingProduct();
                } else
                    showMsg(linearLayout);
                break;
            case R.id.iv_search:
                if (listingItems.isEmpty()) {
                    showSearchMsg(linearLayout);
                } else {
                    if (isNetworkConnected()) {
//                    listings.setVisibility(View.GONE);
                        search.setVisibility(View.VISIBLE);
                        searchName.requestFocus();
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(searchName, InputMethodManager.SHOW_IMPLICIT);
                    } else {
                        showMsg(linearLayout);
                    }
                }
                break;
            case R.id.iv_close:
                searchName.setVisibility(View.VISIBLE);
                searchName.setText("");
                search.setVisibility(View.GONE);
//                listings.setVisibility(View.VISIBLE);
                InputMethodManager imm2 = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm2.hideSoftInputFromWindow(searchName.getWindowToken(), 0);
                break;
            default:
                return;
        }
    }

    private void showSelectItemDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new SelectItemSaleDialog();
        dialog.show(getActivity().getSupportFragmentManager(), "SelectItemFragment");
    }

    private void startAddingProduct() {
        startActivity(new Intent(getActivity(), AddProductInfo.class));
    }

    public String getUserUID() {
        return mAuth.getCurrentUser().getUid();
    }

    private void showPopup(View view, final int position) {
        Context wrapper = new ContextThemeWrapper(view.getContext(), R.style.PopupMenu);
        final PopupMenu popup = new PopupMenu(wrapper, view);
        popup.getMenuInflater().inflate(R.menu.listing_popup, popup.getMenu());
        popup.getMenu().findItem(R.id.view_as_user).setTitle(Html.fromHtml("<font color='#000000'>View as Buyer"));
        popup.getMenu().findItem(R.id.delete).setTitle(Html.fromHtml("<font color='#000000'>Delete"));
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.view_as_user) {
                    if (isNetworkConnected()) {
                        trackListing("View as user");
                        lookProduct(listingItems.get(position).getId(), listingItems.get(position).getProductType());
                    } else {
                        showMsg(linearLayout);
                    }
                } else if (item.getItemId() == R.id.delete) {
                    if (isNetworkConnected()) {
                        final AlertDialog.Builder alertDiallogBuilder = new AlertDialog.Builder(getContext());
                        alertDiallogBuilder.setTitle(Html.fromHtml("<font color='#212121'><b>Alert"));
                        alertDiallogBuilder.setMessage(Html.fromHtml("<font color=\"#424242\"> This will be permanently deleted. Are you sure want to remove this listed item?</font>"));
                        alertDiallogBuilder.setPositiveButton(Html.fromHtml("<font color=\"#212121\"><b>Yes</b></font>"), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mDatabase.child(listingItems.get(position).getId()).setValue(null);
                                mSeller.child(getUserUID()).child(listingItems.get(position).getId()).removeValue();
                                ProductModel productModel = productModels.get(position);

                                geoFire = new GeoFire(mGeo.child("All"));
                                geoFireAll = new GeoFire(mGeo.child(listingItems.get(position).getProductType()));

                                geoFire.removeLocation(listingItems.get(position).getId());
                                geoFireAll.removeLocation(listingItems.get(position).getId());

                                mRecycle.child(listingItems.get(position).getId()).setValue(productModel);
                                listingItems.remove(position);
                                listingAdapter.setData(listingItems);
                                listingAdapter.notifyDataSetChanged();
//                                listingAdapter = new ListingAdapter(getActivity().getApplicationContext(), listingItems);
//                                recyclerView.setAdapter(listingAdapter);
                                trackListing("Unlist Item");
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
                    } else {
                        showMsg(linearLayout);
                    }
                }
                popup.dismiss();
                return true;
            }
        });
        popup.show();
    }

    private void lookProduct(String productId, String productType) {
        Bundle bundle = new Bundle();
        bundle.putString("productId", productId);
        bundle.putString("productType", productType);
        bundle.putString("childName", "live");
        Intent intent = new Intent(getActivity().getApplicationContext(), SellerProductDescription.class);
        intent.putExtra("lookProduct", bundle);
        startActivity(intent);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    public void showMsg(LinearLayout linearLayout) {
        Snackbar snackbar = Snackbar.make(linearLayout, "No internet connection!", Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    public void showSearchMsg(LinearLayout linearLayout) {
        Snackbar snackbar = Snackbar.make(linearLayout, "You have not listed any item yet.", Snackbar.LENGTH_SHORT);
        snackbar.show();
    }
}
