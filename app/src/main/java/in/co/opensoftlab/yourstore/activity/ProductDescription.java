package in.co.opensoftlab.yourstore.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import in.co.opensoftlab.yourstore.BuildConfig;
import in.co.opensoftlab.yourstore.R;
import in.co.opensoftlab.yourstore.adapter.FeaturesAdapter;
import in.co.opensoftlab.yourstore.adapter.ImageSlideAdapter;
import in.co.opensoftlab.yourstore.adapter.SpecificationListAdapter;
import in.co.opensoftlab.yourstore.fragment.AddMobDialogFragment;
import in.co.opensoftlab.yourstore.fragment.MakeDealDialog;
import in.co.opensoftlab.yourstore.fragment.SendMessageDialog;
import in.co.opensoftlab.yourstore.listener.CirclePageIndicator;
import in.co.opensoftlab.yourstore.listener.PageIndicator;
import in.co.opensoftlab.yourstore.model.NotificationHistory;
import in.co.opensoftlab.yourstore.model.NotificationModel;
import in.co.opensoftlab.yourstore.model.ProductViewModel;
import in.co.opensoftlab.yourstore.model.SpecificationItem;
import in.co.opensoftlab.yourstore.model.ProductModel;
import in.co.opensoftlab.yourstore.model.WishlistModel;
import in.co.opensoftlab.yourstore.utils.DistanceCalculator;
import in.co.opensoftlab.yourstore.utils.PrefUtils;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static java.security.AccessController.getContext;

/**
 * Created by dewangankisslove on 08-12-2016.
 */

public class ProductDescription extends BaseActivity implements OnMapReadyCallback {

    MixpanelAPI mMixpanel;
    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    private Circle circle;

    private static final long ANIM_VIEWPAGER_DELAY = 2000;
    private static final long ANIM_VIEWPAGER_DELAY_USER_VIEW = 3000;

    boolean stopSliding = false;
    private Runnable animateViewPager;
    private Handler handler;

    //ImageView productImg;
    ViewPager viewPager;
    PageIndicator pageIndicator;
    ImageView back;
    ImageView favourite;
    TextView productName;
    TextView productPrice;
    CircleImageView sellerImg;
    TextView sellerName;
    TextView sellerType;
    TextView ownerNo;
    RecyclerView carOverview;
    RecyclerView recyclerView;
    Button contactSeller;
    Button makeRequest;
    Button deal;
    String sellerId;
    String urlSeller;
    String nameSeller;
    String sellerLocation;
    TextView uploadDate;
    private TextView distance;
    ImageView info;
    String saveInfo = "";

    List<String> productUrls = new ArrayList<>();
    ImageSlideAdapter imageSlideAdapter;
    private RelativeLayout relativeLayout;

    LinearLayoutManager linearLayoutManager;
    List<SpecificationItem> featureItems = new ArrayList<>();
    SpecificationListAdapter specificationListAdapter;

    LinearLayoutManager layoutManager;
    List<String> listFeatures = new ArrayList<>();
    List<Integer> listFeatureImgs = new ArrayList<>();
    FeaturesAdapter carOverviewAdapter;

    private DatabaseReference mNotificationHistory;
    private DatabaseReference mNotification;

    Bundle bundle = null;
    private String productType = "";
    private DatabaseReference mDatabase;
    private DatabaseReference mWishlist;
    private DatabaseReference mStats;
    private DatabaseReference mToken;
    private FirebaseAuth mAuth;
    private ValueEventListener valueEventListener;
    private ValueEventListener bookmarkListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_description);
        initUI();

        String projectToken = BuildConfig.ANALYTICS_TOKEN;
        mMixpanel = MixpanelAPI.getInstance(this, projectToken);

        mAuth = FirebaseAuth.getInstance();
        mNotification = FirebaseDatabase.getInstance().getReference("notification");
        mNotificationHistory = FirebaseDatabase.getInstance().getReference("notificationHistory").child(getUserUID()).child("buyer");
        mDatabase = FirebaseDatabase.getInstance().getReference("products").child("all").child("live");
        mWishlist = FirebaseDatabase.getInstance().getReference("wishlist");
        mStats = FirebaseDatabase.getInstance().getReference("stats");
        mToken = FirebaseDatabase.getInstance().getReference("notificationToken");

        bundle = getIntent().getExtras().getBundle("lookProduct");
        productType = bundle.getString("productType");
        if (bundle != null) {
            Log.d("readData", bundle.getString("productId"));

            bookmarkListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                        WishlistModel model = dataSnapshot.getValue(WishlistModel.class);
                        if (model.getProductName().isEmpty())
                            favourite.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                        else
                            favourite.setImageResource(R.drawable.ic_favorite_black_24dp);
                    } else {
                        favourite.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };

            valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d("readData", dataSnapshot.toString());
                    if (dataSnapshot.exists()) {
                        ProductModel products = dataSnapshot.getValue(ProductModel.class);

                        sellerId = products.getSellerId();
                        urlSeller = products.getSellerUrl();
                        nameSeller = products.getSellerName();

                        uploadDate.setText("Upload Date: " + products.getUploadDate().split(" ")[0]);

                        productName.setText(products.getProductName());
                        productPrice.setText("" + products.getPrice());
                        sellerName.setText(products.getSellerName());
                        sellerType.setText(products.getSellerType());
                        ownerNo.setText(products.getOwnerNo());
                        Picasso.with(getApplicationContext())
                                .load(products.getSellerUrl())
                                .into(sellerImg);

                        double lat1 = Double.parseDouble(PrefUtils.getFromPrefs(getApplicationContext(), "latitude", "0"));
                        double lng1 = Double.parseDouble(PrefUtils.getFromPrefs(getApplicationContext(), "longitude", "0"));
                        double lat2 = Double.parseDouble(products.getGeoLocation().split("::")[0]);
                        double lng2 = Double.parseDouble(products.getGeoLocation().split("::")[1]);

                        float df = (float) DistanceCalculator.distance(lat1, lng1, lat2, lng2);
                        distance.setText("" + df + "km away from you");

                        for (int i = 0; i < products.getProductUrls().split("::").length; i++) {
                            String url = products.getProductUrls().split("::")[i];
                            productUrls.add(url);
                        }
                        imageSlideAdapter.notifyDataSetChanged();
                        pageIndicator.notifyDataSetChanged();

                        if (productUrls != null && productUrls.size() != 0) {
                            runnable(productUrls.size());
                            handler.postDelayed(animateViewPager,
                                    ANIM_VIEWPAGER_DELAY);
                        }

                        if (productType.contentEquals("Car")) {
                            saveInfo = products.getFuelType() + " - " + products.getDrivenDistance() + " kms - " +
                                    products.getModelYear();
                            listFeatures.add(String.valueOf(products.getDrivenDistance()) + " kms\nDriven");
                            listFeatures.add(products.getFuelType() + "\nFuel Type");
                            listFeatures.add(products.getModelYear() + "\nModel Year");
                            listFeatures.add(products.getTransmission() + "\nTransmission");
                            listFeatures.add(products.getColor() + "\nColor");
                            listFeatures.add(products.getEngine() + " CC\nEngine");
                            listFeatures.add(String.valueOf(products.getSeating()) + "\nSeating");
                            listFeatures.add(products.getMileage() + " KMPL\nMileage");
                            listFeatures.add(String.valueOf(products.getTankCapacity()) + "L Fuel Tank Capacity");

                            listFeatureImgs.add(R.drawable.icon_road);
                            listFeatureImgs.add(R.drawable.icon_fuel);
                            listFeatureImgs.add(R.drawable.icon_calender);
                            listFeatureImgs.add(R.drawable.icon_transmission);
                            listFeatureImgs.add(R.drawable.icon_colors);
                            listFeatureImgs.add(R.drawable.icon_engine);
                            listFeatureImgs.add(R.drawable.icon_seating);
                            listFeatureImgs.add(R.drawable.icon_mileage);
                            listFeatureImgs.add(R.drawable.icon_capacity);
                            carOverviewAdapter.notifyDataSetChanged();
                        } else {
                            saveInfo = products.getDrivenDistance() + " kms - " +
                                    products.getModelYear();
                            listFeatures.add(String.valueOf(products.getDrivenDistance()) + " kms\nDriven");
                            listFeatures.add(products.getModelYear() + "\nModel Year");
                            listFeatures.add(products.getTopSpeed() + " kmph\nTop Speed");
                            listFeatures.add(products.getElectricStart() + "\nSelf Start");
                            listFeatures.add(products.getColor() + "\nColor");
                            listFeatures.add(products.getEngine() + " CC\nEngine");
                            listFeatures.add(products.getMileage() + " kmpl\nMileage");
                            listFeatures.add(String.valueOf(products.getTankCapacity()) + "L Fuel Tank Capacity");

                            listFeatureImgs.add(R.drawable.icon_road);
                            listFeatureImgs.add(R.drawable.icon_calender);
                            listFeatureImgs.add(R.drawable.icon_top_speed);
                            listFeatureImgs.add(R.drawable.icon_electric_start);
                            listFeatureImgs.add(R.drawable.icon_colors);
                            listFeatureImgs.add(R.drawable.icon_engine);
                            listFeatureImgs.add(R.drawable.icon_mileage);
                            listFeatureImgs.add(R.drawable.icon_capacity);
                            carOverviewAdapter.notifyDataSetChanged();
                        }

                        Log.d("specificationLength", String.valueOf(products.getConditions().split(";;").length));
                        for (int j = 0; j < products.getConditions().split(";;").length; j++) {
                            if (products.getConditions().split(";;")[j].split("::")[1].contentEquals("N/A")) {
                                SpecificationItem featureItem = new SpecificationItem(products.getConditions().split(";;")[j].split("::")[0],
                                        "N/A");
                                featureItems.add(featureItem);
                            } else {
                                SpecificationItem featureItem = new SpecificationItem(products.getConditions().split(";;")[j].split("::")[0],
                                        products.getConditions().split(";;")[j].split("::")[1].substring(0, 1) + "/5");
                                featureItems.add(featureItem);
                            }
                        }
                        specificationListAdapter.notifyDataSetChanged();

                        sellerLocation = products.getAddress();

                        float lat = Float.parseFloat(products.getGeoLocation().split("::")[0]);
                        float lon = Float.parseFloat(products.getGeoLocation().split("::")[1]);

                        circle = mMap.addCircle(new CircleOptions()
                                .center(new LatLng(lat, lon))
                                .radius(250)
                                .strokeWidth(0)
                                .fillColor(Color.argb(40, 255, 0, 0))
                                .clickable(true));

                        // Add a marker in Sydney, Australia, and move the camera.
                        LatLng place = new LatLng(lat, lon);
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(place));
                        // Zoom in, animating the camera.
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 1000, null);
                        mMap.setMinZoomPreference(12);
                        Marker melbourne = mMap.addMarker(new MarkerOptions()
                                .position(place)
                                .title(products.getAddress()));
                        melbourne.showInfoWindow();

                        doStats();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            mDatabase.child(bundle.getString("productId")).addListenerForSingleValueEvent(valueEventListener);
        }

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    private void doStats() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c.getTime());
        String month = formattedDate.substring(0, 7);
        String year = formattedDate.substring(0, 4);
        mStats.child("views").child(sellerId).child("day").child(formattedDate).child(bundle.getString("productId")).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                ProductViewModel p = mutableData.getValue(ProductViewModel.class);
                if (p == null) {
                    ProductViewModel model = new ProductViewModel(productName.getText().toString(), 1);
                    mutableData.setValue(model);
                    return Transaction.success(mutableData);
                }
                int views = p.getNoViews();
                p.setNoViews(views + 1);
                // Set value and report transaction success
                mutableData.setValue(p);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });

        mStats.child("views").child(sellerId).child("month").child(month).child(bundle.getString("productId")).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                ProductViewModel p = mutableData.getValue(ProductViewModel.class);
                if (p == null) {
                    ProductViewModel model = new ProductViewModel(productName.getText().toString(), 1);
                    mutableData.setValue(model);
                    return Transaction.success(mutableData);
                }
                int views = p.getNoViews();
                p.setNoViews(views + 1);
                // Set value and report transaction success
                mutableData.setValue(p);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });

        mStats.child("views").child(sellerId).child("year").child(year).child(bundle.getString("productId")).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                ProductViewModel p = mutableData.getValue(ProductViewModel.class);
                if (p == null) {
                    ProductViewModel model = new ProductViewModel(productName.getText().toString(), 1);
                    mutableData.setValue(model);
                    return Transaction.success(mutableData);
                }
                int views = p.getNoViews();
                p.setNoViews(views + 1);
                // Set value and report transaction success
                mutableData.setValue(p);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWishlist.child(getUserUID()).child(bundle.getString("productId")).addValueEventListener(bookmarkListener);
    }


    private void initUI() {
        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
        viewPager = (ViewPager) findViewById(R.id.pager);
        pageIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
        //productImg = (ImageView) findViewById(R.id.product_img);
        back = (ImageView) findViewById(R.id.action_back);
        favourite = (ImageView) findViewById(R.id.action_favorite);
        productName = (TextView) findViewById(R.id.tv_product_name);
        productPrice = (TextView) findViewById(R.id.tv_product_price);
        sellerImg = (CircleImageView) findViewById(R.id.seller_image);
        sellerName = (TextView) findViewById(R.id.tv_seller_name);
        sellerType = (TextView) findViewById(R.id.tv_seller_type);
        ownerNo = (TextView) findViewById(R.id.tv_owner);
        carOverview = (RecyclerView) findViewById(R.id.recyclerOverview);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        contactSeller = (Button) findViewById(R.id.b_contact);
        makeRequest = (Button) findViewById(R.id.b_purchase);
        deal = (Button) findViewById(R.id.b_deal);
        uploadDate = (TextView) findViewById(R.id.tv_date);
        distance = (TextView) findViewById(R.id.tv_distance);
        info = (ImageView) findViewById(R.id.iv_info);

        imageSlideAdapter = new ImageSlideAdapter(getApplicationContext(), productUrls);
        viewPager.setAdapter(imageSlideAdapter);
        pageIndicator.setViewPager(viewPager);
        viewPager.setOnPageChangeListener(new PageChangeListener());
        pageIndicator.setOnPageChangeListener(new PageChangeListener());

        viewPager.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                switch (event.getAction()) {

                    case MotionEvent.ACTION_CANCEL:
                        break;

                    case MotionEvent.ACTION_UP:
                        // calls when touch release on ViewPager
                        if (productUrls != null && productUrls.size() != 0) {
                            stopSliding = false;
                            runnable(productUrls.size());
                            handler.postDelayed(animateViewPager,
                                    ANIM_VIEWPAGER_DELAY_USER_VIEW);
                        }
                        break;

                    case MotionEvent.ACTION_MOVE:
                        // calls when ViewPager touch
                        if (handler != null && stopSliding == false) {
                            stopSliding = true;
                            handler.removeCallbacks(animateViewPager);
                        }
                        break;
                }
                return false;
            }
        });

        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(ProductDescription.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        specificationListAdapter = new SpecificationListAdapter(ProductDescription.this, featureItems);
        recyclerView.setAdapter(specificationListAdapter);

        carOverview.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(ProductDescription.this, LinearLayoutManager.HORIZONTAL, false);
        carOverview.setLayoutManager(layoutManager);
        carOverviewAdapter = new FeaturesAdapter(ProductDescription.this, listFeatures, listFeatureImgs);
        carOverview.setAdapter(carOverviewAdapter);

        favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNetworkConnected()) {
                    mWishlist.child(getUserUID()).child(bundle.getString("productId")).runTransaction(new Transaction.Handler() {
                        @Override
                        public Transaction.Result doTransaction(MutableData mutableData) {
                            WishlistModel p = mutableData.getValue(WishlistModel.class);
                            if (p == null) {
                                WishlistModel model = new WishlistModel(bundle.getString("productType"), productUrls.get(0),
                                        productName.getText().toString(), Integer.parseInt(productPrice.getText().toString()),
                                        saveInfo);
                                mutableData.setValue(model);
                                return Transaction.success(mutableData);
                            }
                            // Set value and report transaction success
                            mutableData.setValue(null);
                            return Transaction.success(mutableData);
                        }

                        @Override
                        public void onComplete(DatabaseError databaseError, boolean b,
                                               DataSnapshot dataSnapshot) {
                            // Transaction completed
                        }
                    });
                } else {
                    showConnectionFailed(relativeLayout);
                }
            }
        });

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder alertDiallogBuilder = new AlertDialog.Builder(ProductDescription.this);
                alertDiallogBuilder.setTitle(Html.fromHtml("<font color='#212121'><b>Info"));
                alertDiallogBuilder.setMessage(Html.fromHtml("<font color=\"#424242\">Given condition rating is given by its owner himself. We don't guarantee about the genuinity of ratings.</font>"));
                alertDiallogBuilder.setPositiveButton(Html.fromHtml("<font color=\"#212121\"><b>Got it</b></font>"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });
                final AlertDialog alertDialog = alertDiallogBuilder.create();
                alertDialog.show();
            }
        });

        contactSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sellerId != null && !sellerId.contentEquals("") && !urlSeller.contentEquals("") && !nameSeller.contentEquals("")) {
                    if (isNetworkConnected()) {
                        trackContactOption("Message Seller");
                        showMsgDialog(sellerId, nameSeller, urlSeller);
                    } else
                        showConnectionFailed(relativeLayout);

                }
            }
        });

        makeRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNetworkConnected()) {
                    final AlertDialog.Builder alertDiallogBuilder = new AlertDialog.Builder(ProductDescription.this);
                    alertDiallogBuilder.setTitle(Html.fromHtml("<font color='#212121'><b>Send a Request"));
                    alertDiallogBuilder.setMessage(Html.fromHtml("<font color=\"#424242\">Are you sure want a mobile number of this car owner.</font>"));
                    alertDiallogBuilder.setPositiveButton(Html.fromHtml("<font color=\"#212121\"><b>Yes</b></font>"), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            trackContactOption("Mob no. request");
                            createRequest(productType);
                            dialog.dismiss();
                        }
                    });
                    alertDiallogBuilder.setNegativeButton(Html.fromHtml("<font color=\"#212121\"><b>No</b></font>"), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    final AlertDialog alertDialog = alertDiallogBuilder.create();
                    alertDialog.show();
                } else {
                    showConnectionFailed(relativeLayout);
                }

            }
        });

        deal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNetworkConnected()) {
                    trackContactOption("Make a Deal");
                    showDealDialog(sellerId, nameSeller, bundle.getString("productId"),
                            productPrice.getText().toString(), productName.getText().toString());
                } else
                    showConnectionFailed(relativeLayout);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    public void showMsgDialog(String id, String name, String url) {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = SendMessageDialog.NewInstance(id, name, url, sellerLocation);
        dialog.show(getSupportFragmentManager(), "SendMsgDialogFragment");
    }

    public void showDealDialog(String id, String name, String productId, String price, String productName) {
        // Create an instance of the dialog fragment and show it
        if (productType.contentEquals("Car")) {
            DialogFragment dialog = MakeDealDialog.NewInstance(id, name, productId, price,
                    productName, sellerLocation, "car");
            dialog.show(getSupportFragmentManager(), "DealDialogFragment");
        } else {
            DialogFragment dialog = MakeDealDialog.NewInstance(id, name, productId, price,
                    productName, sellerLocation, "bike");
            dialog.show(getSupportFragmentManager(), "DealDialogFragment");
        }
    }

    private void createRequest(String type) {
        showProgressDialog();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yy/MM/dd hh:mm aaa");
        String formattedDate = df.format(c.getTime());
        String key = mNotification.push().getKey();
        String orderKey = mNotificationHistory.push().getKey();
        NotificationModel model;
        if (type.contentEquals("Car")) {
            model = new NotificationModel("reqMob", formattedDate, getUserName(), getUserUID(),
                    getUserUri(), nameSeller + "::" + productName.getText().toString() + "::" +
                    "car", "null", sellerLocation);
        } else {
            model = new NotificationModel("reqMob", formattedDate, getUserName(), getUserUID(),
                    getUserUri(), nameSeller + "::" + productName.getText().toString() + "::" +
                    "bike", "null", sellerLocation);
        }
        NotificationHistory orderHistory = new NotificationHistory(key, bundle.getString("productId"), formattedDate);
        mNotification.child("seller").child(sellerId).child(key).setValue(model);
        mNotificationHistory.child(orderKey).setValue(orderHistory);
        mToken.child(sellerId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null)
                    sendMsg(dataSnapshot.getValue().toString(), "reqMob",
                            "Autoroom", getUserName().split(" ")[0] + "::has::requested::your::mobile::no.", "empty");

                hideProgressDialog();
                showMsg(relativeLayout);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgressDialog();
                showMsg(relativeLayout);

            }
        });
    }

    public void sendMsg(final String id, final String type, final String title, final String msg, final String extra) {
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                InputStream inputStream = null;
                HttpURLConnection urlConnection = null;
                Integer result = 0;
                try {
                    String url = "http://139.59.18.133/api/message?";
                    url += "to=" + id;
                    url += "&type=" + type + "&title=" + title + "&msg=" + msg + "&extra=" + extra;
                    Log.d("url", url);
                    URL newurl = new URL(url);
                    urlConnection = (HttpURLConnection) newurl.openConnection();
                    urlConnection.setRequestProperty("Accept", "application/json");
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();
                    int requestcode = urlConnection.getResponseCode();
                    Log.d("url", requestcode + "");
                } catch (MalformedURLException e1) {
                    e1.printStackTrace();
                } catch (ProtocolException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                } finally {
                    urlConnection.disconnect();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
            }
        }.execute();
    }

    public void showMsg(RelativeLayout relativeLayout) {
        Snackbar snackbar = Snackbar.make(relativeLayout, "Notification sent successfully!", Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    public void showConnectionFailed(RelativeLayout relativeLayout) {
        Snackbar snackbar = Snackbar.make(relativeLayout, "No internet connection!", Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    private class PageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_IDLE) {
                pageIndicator.setCurrentItem(viewPager.getCurrentItem());
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageSelected(int arg0) {
        }
    }

    public void runnable(final int size) {
        handler = new Handler();
        animateViewPager = new Runnable() {
            public void run() {
                if (!stopSliding) {
                    if (viewPager.getCurrentItem() == size - 1) {
                        viewPager.setCurrentItem(0);
                        pageIndicator.setCurrentItem(0);
                    } else {
                        int temp = viewPager.getCurrentItem() + 1;
                        viewPager.setCurrentItem(temp, true);
                        pageIndicator.setCurrentItem(temp);
                    }
                    handler.postDelayed(animateViewPager, ANIM_VIEWPAGER_DELAY);
                }
            }
        };
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDatabase.child(bundle.getString("productId")).removeEventListener(valueEventListener);
        mWishlist.child(getUserUID()).child(bundle.getString("productId")).removeEventListener(bookmarkListener);
        if (handler != null) {
            //Remove callback
            handler.removeCallbacks(animateViewPager);
        }
    }


    //    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.product_description_menu, menu);
//        return true;
//    }
//

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnCircleClickListener(new GoogleMap.OnCircleClickListener() {

            @Override
            public void onCircleClick(Circle circle) {
                // Flip the r, g and b components of the circle's
                // stroke color.
                int strokeColor = circle.getStrokeColor() ^ 0x00ffffff;
                circle.setStrokeColor(strokeColor);
            }
        });

    }

    private void trackContactOption(String optionName) {
        try {
            JSONObject props = new JSONObject();
            props.put("optionName", optionName);
            mMixpanel.track("Contact Seller Option", props);
        } catch (JSONException e) {
            Log.e("Autoroom", "Unable to add properties to JSONObject", e);
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

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

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }
}
