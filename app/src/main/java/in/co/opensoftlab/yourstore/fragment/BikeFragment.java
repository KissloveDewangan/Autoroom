package in.co.opensoftlab.yourstore.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

import in.co.opensoftlab.yourstore.R;
import in.co.opensoftlab.yourstore.activity.BikeFilterActivity;
import in.co.opensoftlab.yourstore.activity.CarFilterActivity;
import in.co.opensoftlab.yourstore.activity.ChangeLocation;
import in.co.opensoftlab.yourstore.activity.ProductDescription;
import in.co.opensoftlab.yourstore.adapter.CarListAdapter;
import in.co.opensoftlab.yourstore.helper.DatabaseHandler;
import in.co.opensoftlab.yourstore.model.CarItem;
import in.co.opensoftlab.yourstore.model.ProductModel;
import in.co.opensoftlab.yourstore.utils.PrefUtils;
import in.co.opensoftlab.yourstore.views.SpacingItemDecoration;

/**
 * Created by dewangankisslove on 16-12-2016.
 */

public class BikeFragment extends Fragment implements CarListAdapter.MyClickListener {

    LinearLayout msg;
    RelativeLayout relativeLayout;
    RecyclerView rv_cars;
    CarListAdapter carsAdapter;
    LinearLayoutManager linearLayoutManager;

    List<CarItem> carsList = new ArrayList<>();
    List<String[]> dbListCar = new ArrayList<>();


    private DatabaseReference mDatabase;
    private DatabaseReference mGeo;
//    ValueEventListener valueEventListener;

    //Query queryDB;
    String childName;

    GeoQuery geoQuery;
    GeoFire geoFire;

    //RecyclerView searchList;
    //EditText searchName;
//    SearchListAdapter searchListAdapter;
//    List<SearchItem> searchItems = new ArrayList<>();

    ImageView selectCategory;
    RelativeLayout rlOptions;
    ImageView filtering;
    ImageView changeMyLocation;
    TextView noFilters;
    TextView myLocation;
    TextView searchDistance;
    AVLoadingIndicatorView loadingIndicatorView;

    private Boolean showMsg = true;

    private int FILTER_REQUEST_CODE = 1;
    private int LOCATION_REQUEST_CODE = 2;

//    RelativeLayout locationField;

    DatabaseHandler db;

    public static BikeFragment NewInstance(String childName) {
        Bundle args = new Bundle();
        args.putString("childName", childName);
        BikeFragment fragment = new BikeFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DatabaseHandler(getActivity().getApplicationContext());
        Bundle args = getArguments();
        childName = args.getString("childName");
        mDatabase = FirebaseDatabase.getInstance().getReference(getResources().getString(R.string.server_products)).child(childName).child("live");
        mGeo = FirebaseDatabase.getInstance().getReference(getResources().getString(R.string.server_products)).child("geoData").child("live").child("Bike");
        geoFire = new GeoFire(mGeo);
        //queryDB = mDatabase.orderByChild("price").limitToLast(20);
        PrefUtils.saveToPrefs(getActivity().getApplicationContext(), "saveBrandBike", "All");
        PrefUtils.saveToPrefs(getActivity().getApplicationContext(), "saveModelBike", "All");
        PrefUtils.saveToPrefs(getActivity().getApplicationContext(), "saveLowPriceBike", "0");
        PrefUtils.saveToPrefs(getActivity().getApplicationContext(), "saveHighPriceBike", "1000000");
        PrefUtils.saveToPrefs(getActivity().getApplicationContext(), "noFiltersBike", "0");
        PrefUtils.saveToPrefs(getActivity().getApplicationContext(), "saveSortByBike", "None");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bike, container, false);
        initUI(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        db.removeAllBikes();
        carsList.clear();
        carsAdapter.notifyDataSetChanged();
        double lat = Double.parseDouble(PrefUtils.getFromPrefs(getActivity().getApplicationContext(), "latitude", "0"));
        double lng = Double.parseDouble(PrefUtils.getFromPrefs(getActivity().getApplicationContext(), "longitude", "0"));
        double searchDis = Double.parseDouble(PrefUtils.getFromPrefs(getActivity().getApplicationContext(), "searchDistance", "25"));
        geoQuery = geoFire.queryAtLocation(new GeoLocation(lat, lng), searchDis);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                showMsg = false;
                mDatabase.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {

                            // TODO: handle the post
                            ProductModel products = dataSnapshot.getValue(ProductModel.class);
                            CarItem filterProduct = new CarItem(dataSnapshot.getKey(), products.getProductType(),
                                    products.getProductUrls(),
                                    products.getProductName(), products.getPrice(),
                                    products.getDrivenDistance() + " kms - " + products.getModelYear());
                            carsList.add(filterProduct);
                            if (!db.checkBikeExist(dataSnapshot.getKey()))
                                db.addBikes(dataSnapshot.getKey(), products.getProductName(), products.getProductUrls(),
                                        products.getEngine(), products.getBrand(),
                                        products.getModelName(), products.getModelYear(), products.getPrice(),
                                        products.getDrivenDistance(), 0,
                                        products.getColor(), products.getElectricStart(), products.getSellerType(),
                                        products.getMileage(), products.getTankCapacity(), products.getTopSpeed(),
                                        products.getOwnerNo(),
                                        products.getConditions(), products.getSellerName(), products.getSellerId(),
                                        products.getSellerUrl(), products.getProfileId(), products.getGeoLocation(),
                                        products.getAddress(), products.getUploadDate(), products.getAssuredProduct());


                            carsAdapter.notifyDataSetChanged();
                        } else {

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onKeyExited(String key) {
                System.out.println(String.format("Key %s is no longer in the search area", key));
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
            }

            @Override
            public void onGeoQueryReady() {
                loadingIndicatorView.hide();
                if (showMsg) {
                    msg.setVisibility(View.VISIBLE);
                } else {
                    msg.setVisibility(View.GONE);
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                System.err.println("There was an error with this query: " + error);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILTER_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data.getExtras().getInt("noFilters") == 0) {
                    noFilters.setVisibility(View.GONE);
                } else {
                    noFilters.setText("" + data.getExtras().getInt("noFilters"));
                    noFilters.setVisibility(View.VISIBLE);
                }
                final String tempBrand = data.getExtras().getString("brand").contentEquals("All") ? "" : data.getExtras().getString("brand");
                final String tempModel = data.getExtras().getString("model").contentEquals("All") ? "" : data.getExtras().getString("model");
                final String tempLowPrice = data.getExtras().getString("lowPrice");
                final String temphighPrice = data.getExtras().getString("highPrice");
                final String sortBy = data.getExtras().getString("sortBy");
                Log.d("returnVal", tempLowPrice);

                carsList.clear();
                dbListCar.clear();

                if (sortBy.contentEquals("Price - Low to High")) {
                    dbListCar = db.getBikes(tempBrand, tempModel, tempLowPrice, temphighPrice, "bike_price", "ASC");
                } else if (sortBy.contentEquals("Price - High to Low")) {
                    dbListCar = db.getBikes(tempBrand, tempModel, tempLowPrice, temphighPrice, "bike_price", "DESC");
                } else if (sortBy.contentEquals("Model Year - Latest to Old")) {
                    dbListCar = db.getBikes(tempBrand, tempModel, tempLowPrice, temphighPrice, "bike_year", "DESC");
                } else if (sortBy.contentEquals("KM Driven - Low to High")) {
                    dbListCar = db.getBikes(tempBrand, tempModel, tempLowPrice, temphighPrice, "driven_distance", "ASC");
                } else {
                    dbListCar = db.getBikes(tempBrand, tempModel, tempLowPrice, temphighPrice, "", "");
                }

                if (dbListCar.size() == 0) {
                    msg.setVisibility(View.VISIBLE);
                } else {
                    msg.setVisibility(View.GONE);
                }

                for (int i = 0; i < dbListCar.size(); i++) {
                    CarItem filterProduct = new CarItem(dbListCar.get(i)[0], "Bike", dbListCar.get(i)[2],
                            dbListCar.get(i)[1], Integer.parseInt(dbListCar.get(i)[7]),
                            dbListCar.get(i)[8] + " kms - " +
                                    dbListCar.get(i)[6]);
                    carsList.add(filterProduct);
                }
                carsAdapter.notifyDataSetChanged();

            }
        } else if (requestCode == LOCATION_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data.getExtras().getBoolean("locationChanged")) {
                    noFilters.setText("0");
                    noFilters.setVisibility(View.GONE);
                    myLocation.setText(PrefUtils.getFromPrefs(getActivity().getApplicationContext(), "address", "Location unknown"));
                    searchDistance.setText("Within " + PrefUtils.getFromPrefs(getActivity().getApplicationContext(), "searchDistance", "25") + " km");
                    db.removeAllBikes();
                    carsList.clear();
                    carsAdapter.notifyDataSetChanged();
                    double lat = Double.parseDouble(PrefUtils.getFromPrefs(getActivity().getApplicationContext(), "latitude", "0"));
                    double lng = Double.parseDouble(PrefUtils.getFromPrefs(getActivity().getApplicationContext(), "longitude", "0"));
                    double searchDis = Double.parseDouble(PrefUtils.getFromPrefs(getActivity().getApplicationContext(), "searchDistance", "25"));
                    geoQuery = geoFire.queryAtLocation(new GeoLocation(lat, lng), searchDis);
                    geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                        @Override
                        public void onKeyEntered(String key, GeoLocation location) {
                            showMsg = false;
                            mDatabase.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getValue() != null) {

                                        // TODO: handle the post
                                        ProductModel products = dataSnapshot.getValue(ProductModel.class);
                                        CarItem filterProduct = new CarItem(dataSnapshot.getKey(), products.getProductType(),
                                                products.getProductUrls(),
                                                products.getProductName(), products.getPrice(),
                                                products.getDrivenDistance() + " kms - " +
                                                        products.getModelYear());
                                        carsList.add(filterProduct);
                                        if (!db.checkBikeExist(dataSnapshot.getKey()))
                                            db.addBikes(dataSnapshot.getKey(), products.getProductName(), products.getProductUrls(),
                                                    products.getEngine(), products.getBrand(),
                                                    products.getModelName(), products.getModelYear(), products.getPrice(),
                                                    products.getDrivenDistance(), 0,
                                                    products.getColor(), products.getElectricStart(), products.getSellerType(),
                                                    products.getMileage(), products.getTankCapacity(), products.getTopSpeed(),
                                                    products.getOwnerNo(),
                                                    products.getConditions(), products.getSellerName(), products.getSellerId(),
                                                    products.getSellerUrl(), products.getProfileId(), products.getGeoLocation(),
                                                    products.getAddress(), products.getUploadDate(), products.getAssuredProduct());


                                        carsAdapter.notifyDataSetChanged();
                                    } else {

                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }

                        @Override
                        public void onKeyExited(String key) {
                            System.out.println(String.format("Key %s is no longer in the search area", key));
                        }

                        @Override
                        public void onKeyMoved(String key, GeoLocation location) {
                        }

                        @Override
                        public void onGeoQueryReady() {
                            loadingIndicatorView.hide();
                            if (showMsg) {
                                msg.setVisibility(View.VISIBLE);
                            } else {
                                msg.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onGeoQueryError(DatabaseError error) {
                            System.err.println("There was an error with this query: " + error);
                        }
                    });
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        geoQuery.removeAllListeners();
    }

    private void initUI(View view) {
        loadingIndicatorView = (AVLoadingIndicatorView) view.findViewById(R.id.avi);
        msg = (LinearLayout) view.findViewById(R.id.ll_msg);
        relativeLayout = (RelativeLayout) view.findViewById(R.id.relativeLayout);
        rv_cars = (RecyclerView) view.findViewById(R.id.recycler_view);
        rlOptions = (RelativeLayout) view.findViewById(R.id.rl_options);
        filtering = (ImageView) view.findViewById(R.id.iv_filter);
        changeMyLocation = (ImageView) view.findViewById(R.id.iv_edit_location);
        noFilters = (TextView) view.findViewById(R.id.tv_no_filter);
        myLocation = (TextView) view.findViewById(R.id.tv_mylocation);
        searchDistance = (TextView) view.findViewById(R.id.tv_search_distance);
        selectCategory = (ImageView) view.findViewById(R.id.iv_drop_down);

        loadingIndicatorView.smoothToShow();
        msg.setVisibility(View.GONE);
        myLocation.setText(PrefUtils.getFromPrefs(getActivity().getApplicationContext(), "address", "Location unknown"));
        searchDistance.setText("Within " + PrefUtils.getFromPrefs(getActivity().getApplicationContext(), "searchDistance", "25") + " km");
        noFilters.setVisibility(View.GONE);

        carsAdapter = new CarListAdapter(getActivity().getApplicationContext(), R.layout.car_object, carsList);
        rv_cars.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        rv_cars.setLayoutManager(linearLayoutManager);
        rv_cars.setAdapter(carsAdapter);
        rv_cars.addItemDecoration(new SpacingItemDecoration(1, 8, false));

        carsAdapter.setOnItemClickListener(this);

        selectCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSelectCategoryDialog();
            }
        });


        rv_cars.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 || dy < 0 && rlOptions.isShown())
                    rlOptions.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    rlOptions.setVisibility(View.VISIBLE);
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        filtering.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), BikeFilterActivity.class);
                startActivityForResult(intent, FILTER_REQUEST_CODE);
                getActivity().overridePendingTransition(R.anim.slide_up, R.anim.stay);
            }
        });
//
        changeMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ChangeLocation.class);
                startActivityForResult(intent, LOCATION_REQUEST_CODE);
                getActivity().overridePendingTransition(R.anim.slide_up, R.anim.stay);
            }
        });
    }

    private void showSelectCategoryDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new SelectCategoryDialog();
        dialog.show(getActivity().getSupportFragmentManager(), "SelectCategoryFragment");
    }

    @Override
    public void onItemClick(int position, View v) {
        if (isNetworkConnected()) {
            Bundle bundle = new Bundle();
            bundle.putString("productId", carsList.get(position).getId());
            bundle.putString("productType", "Bike");
            Intent intent = new Intent(getActivity().getApplicationContext(), ProductDescription.class);
            intent.putExtra("lookProduct", bundle);
            startActivity(intent);
        } else {
            showMsg(relativeLayout);
        }
    }

    public void showMsg(RelativeLayout relativeLayout) {
        Snackbar snackbar = Snackbar.make(relativeLayout, "No internet connection!", Snackbar.LENGTH_SHORT);
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
}
