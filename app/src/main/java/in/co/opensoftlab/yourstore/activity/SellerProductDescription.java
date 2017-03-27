package in.co.opensoftlab.yourstore.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import in.co.opensoftlab.yourstore.R;
import in.co.opensoftlab.yourstore.adapter.FeaturesAdapter;
import in.co.opensoftlab.yourstore.adapter.FeaturesAdapterSeller;
import in.co.opensoftlab.yourstore.adapter.ImageSlideAdapter;
import in.co.opensoftlab.yourstore.adapter.ImageSlideAdapterSeller;
import in.co.opensoftlab.yourstore.adapter.SpecificationListAdapter;
import in.co.opensoftlab.yourstore.listener.CirclePageIndicator;
import in.co.opensoftlab.yourstore.listener.PageIndicator;
import in.co.opensoftlab.yourstore.model.ProductModel;
import in.co.opensoftlab.yourstore.model.ProductViewModel;
import in.co.opensoftlab.yourstore.model.SpecificationItem;
import in.co.opensoftlab.yourstore.model.WishlistModel;
import in.co.opensoftlab.yourstore.utils.DistanceCalculator;
import in.co.opensoftlab.yourstore.utils.PrefUtils;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by dewangankisslove on 08-12-2016.
 */

public class SellerProductDescription extends AppCompatActivity implements OnMapReadyCallback {
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
    TextView productName;
    TextView productPrice;
    CircleImageView sellerImg;
    TextView sellerName;
    TextView sellerType;
    TextView ownerNo;
    RecyclerView carOverview;
    RecyclerView recyclerView;
    String sellerId;
    String urlSeller;
    String nameSeller;
    TextView uploadDate;
    private TextView distance;

    ImageView info, favourite;
    Button message, requestMob, makeDeal;
    private RelativeLayout relativeLayout;

    List<String> productUrls = new ArrayList<>();
    ImageSlideAdapter imageSlideAdapter;

    LinearLayoutManager linearLayoutManager;
    List<SpecificationItem> featureItems = new ArrayList<>();
    SpecificationListAdapter specificationListAdapter;

    LinearLayoutManager layoutManager;
    List<String> listFeatures = new ArrayList<>();
    List<Integer> listFeatureImgs = new ArrayList<>();
    FeaturesAdapter carOverviewAdapter;

    Bundle bundle = null;
    private String productType = "";
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private ValueEventListener valueEventListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_description);
        initUI();

        bundle = getIntent().getExtras().getBundle("lookProduct");

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference(getResources().getString(R.string.server_products)).child("all").child(bundle.getString("childName"));

        productType = bundle.getString("productType");
        if (bundle != null) {
            Log.d("readData", bundle.getString("productId"));

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
                            listFeatures.add(String.valueOf(products.getDrivenDistance()) + " kms\nDriven");
                            listFeatures.add(products.getFuelType() + "\nFuel Type");
                            listFeatures.add(products.getModelYear() + "\nModel Year");
                            listFeatures.add(products.getTransmission() + "\nTransmission");
                            listFeatures.add(products.getColor() + "\nColor");
                            listFeatures.add(products.getEngine() + " CC\nEngine");
                            listFeatures.add(String.valueOf(products.getSeating()) + "\nSeating");
                            listFeatures.add(products.getMileage() + " kmpl\nMileage");
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
                            listFeatureImgs.add(R.drawable.icon_calender);
                            listFeatureImgs.add(R.drawable.icon_calender);
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

    @Override
    protected void onResume() {
        super.onResume();
    }


    private void initUI() {
        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
        viewPager = (ViewPager) findViewById(R.id.pager);
        pageIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
        //productImg = (ImageView) findViewById(R.id.product_img);
        back = (ImageView) findViewById(R.id.action_back);
        productName = (TextView) findViewById(R.id.tv_product_name);
        productPrice = (TextView) findViewById(R.id.tv_product_price);
        sellerImg = (CircleImageView) findViewById(R.id.seller_image);
        sellerName = (TextView) findViewById(R.id.tv_seller_name);
        sellerType = (TextView) findViewById(R.id.tv_seller_type);
        ownerNo = (TextView) findViewById(R.id.tv_owner);
        carOverview = (RecyclerView) findViewById(R.id.recyclerOverview);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        uploadDate = (TextView) findViewById(R.id.tv_date);
        distance = (TextView) findViewById(R.id.tv_distance);
        favourite = (ImageView) findViewById(R.id.action_favorite);
        info = (ImageView) findViewById(R.id.iv_info);
        message = (Button) findViewById(R.id.b_contact);
        makeDeal = (Button) findViewById(R.id.b_deal);
        requestMob = (Button) findViewById(R.id.b_purchase);

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
        linearLayoutManager = new LinearLayoutManager(SellerProductDescription.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        specificationListAdapter = new SpecificationListAdapter(SellerProductDescription.this, featureItems);
        recyclerView.setAdapter(specificationListAdapter);

        carOverview.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(SellerProductDescription.this, LinearLayoutManager.HORIZONTAL, false);
        carOverview.setLayoutManager(layoutManager);
        carOverviewAdapter = new FeaturesAdapter(SellerProductDescription.this, listFeatures, listFeatureImgs);
        carOverview.setAdapter(carOverviewAdapter);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMsg(relativeLayout);
            }
        });

        favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMsg(relativeLayout);
            }
        });

        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMsg(relativeLayout);
            }
        });

        requestMob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMsg(relativeLayout);
            }
        });

        makeDeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMsg(relativeLayout);
            }
        });
    }

    public void showMsg(RelativeLayout relativeLayout) {
        Snackbar snackbar = Snackbar.make(relativeLayout, "This action won't work in view mode", Snackbar.LENGTH_SHORT);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                return true;

            case R.id.action_favorite:
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

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
