package in.co.opensoftlab.yourstore.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import in.co.opensoftlab.yourstore.BuildConfig;
import in.co.opensoftlab.yourstore.R;
import in.co.opensoftlab.yourstore.fragment.OptionPickerDialogFragment;
import in.co.opensoftlab.yourstore.model.ProductModel;
import in.co.opensoftlab.yourstore.model.SellerModel;
import in.co.opensoftlab.yourstore.utils.PrefUtils;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by dewangankisslove on 29-12-2016.
 */
public class AddSellerLocation extends BaseActivity implements View.OnClickListener,
        OptionPickerDialogFragment.CategoryDialogListener, GoogleApiClient.OnConnectionFailedListener {
    RelativeLayout selectLocation, selectSellerType, selectOwnerType;
    Button addProduct;
    TextView sellerType, owner, sellerLocation;
    String[] sellerTypes, ownerTypes;
    ImageView close;

    private DatabaseReference mGeo;
    private GeoFire geoFire;
    private GeoFire geoFireAll;
    private FirebaseAuth mAuth;
    private DatabaseReference mSeller;
    private DatabaseReference mDatabase;

    Bundle bundle;

    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 5;
    private GoogleApiClient mGoogleApiClient;
    Toolbar toolbar;

    MixpanelAPI mMixpanel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_location);
        initUI();

        String projectToken = BuildConfig.ANALYTICS_TOKEN;
        mMixpanel = MixpanelAPI.getInstance(this, projectToken);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient
                    .Builder(this)
                    .addApi(Places.GEO_DATA_API)
                    .addApi(Places.PLACE_DETECTION_API)
                    .enableAutoManage(this, this)
                    .build();
        }

        bundle = getIntent().getExtras().getBundle("temp");

        mAuth = FirebaseAuth.getInstance();
        mSeller = FirebaseDatabase.getInstance().getReference("sellers");
        mDatabase = FirebaseDatabase.getInstance().getReference(getResources().getString(R.string.server_products)).child("all").child("live");
        mGeo = FirebaseDatabase.getInstance().getReference("products").child("geoData");
    }

    private void initUI() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        close = (ImageView) findViewById(R.id.iv_close);
        sellerTypes = getResources().getStringArray(R.array.seller_type);
        ownerTypes = getResources().getStringArray(R.array.owner_no);
        selectLocation = (RelativeLayout) findViewById(R.id.rl_location);
        selectSellerType = (RelativeLayout) findViewById(R.id.rl_seller_type);
        selectOwnerType = (RelativeLayout) findViewById(R.id.rl_owner);
        addProduct = (Button) findViewById(R.id.b_addproduct);
        sellerType = (TextView) findViewById(R.id.tv_seller_type);
        owner = (TextView) findViewById(R.id.tv_owner);
        sellerLocation = (TextView) findViewById(R.id.tv_user_location);

        close.setOnClickListener(this);
        addProduct.setOnClickListener(this);
        selectSellerType.setOnClickListener(this);
        selectOwnerType.setOnClickListener(this);
        selectLocation.setOnClickListener(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_close:
                onBackPressed();
                break;
            case R.id.b_addproduct:
                if (!sellerLocation.getText().toString().contentEquals("Enter location") &&
                        !sellerType.getText().toString().contentEquals("Select Individual or Dealer") &&
                        !owner.getText().toString().contentEquals("First or Second Owner")) {

                    showProgressDialog();

                    geoFire = new GeoFire(mGeo.child("live").child("All"));
                    geoFireAll = new GeoFire(mGeo.child("live").child(bundle.getString("productType")));

                    SellerModel sellerModel = new SellerModel(getUserName(), sellerLocation.getText().toString(),
                            PrefUtils.getFromPrefs(getApplicationContext(), "placeLat", "0.0") + "::" +
                                    PrefUtils.getFromPrefs(getApplicationContext(), "placeLng", "0.0"));
                    mSeller.child(getUserUID()).child("info").setValue(sellerModel);
                    //PrefUtils.saveToPrefs(getApplicationContext(),"getGeoLocation","true");
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat df = new SimpleDateFormat("yy/MM/dd HH:mm:ss");
                    String formattedDate = df.format(c.getTime());
                    ProductModel products = null;
                    trackListingType(bundle.getString("productType"));
                    if (bundle.getString("productType").contentEquals("Car")) {
                        products = new ProductModel(bundle.getString("productType"), bundle.getString("brand") + " " +
                                bundle.getString("modelName") + " " +
                                bundle.getString("variantName"),
                                bundle.getString("urls"),
                                bundle.getString("bodyType"), bundle.getString("engine"), bundle.getString("brand"),
                                bundle.getString("modelName"), bundle.getInt("modelYear"), bundle.getInt("price"),
                                bundle.getString("fuelType"), bundle.getInt("seats"), bundle.getInt("drivenDistance"),
                                bundle.getString("color"), bundle.getString("transmission"),
                                sellerType.getText().toString(), bundle.getString("mileage"),
                                bundle.getInt("tankCapacity"), owner.getText().toString(), bundle.getString("condition"),
                                getUserName(), getUserUID(),
                                getUserUri(), getUserUID().substring(0, 5) + getUserName().replace(" ", "").substring(0, 3),
                                sellerLocation.getText().toString(), sellerModel.getGeoLocation(), formattedDate, 0, "null", 0);
                    } else {
                        products = new ProductModel(bundle.getString("productType"), bundle.getString("brand") + " " +
                                bundle.getString("modelName") + " " +
                                bundle.getString("variantName"),
                                bundle.getString("urls"),
                                "null", bundle.getString("engine"), bundle.getString("brand"),
                                bundle.getString("modelName"), bundle.getInt("modelYear"), bundle.getInt("price"),
                                "null", 0, bundle.getInt("drivenDistance"),
                                bundle.getString("color"), "null",
                                sellerType.getText().toString(), bundle.getString("mileage"),
                                bundle.getInt("tankCapacity"), owner.getText().toString(), bundle.getString("condition"),
                                getUserName(), getUserUID(),
                                getUserUri(), getUserUID().substring(0, 5) + getUserName().replace(" ", "").substring(0, 3),
                                sellerLocation.getText().toString(), sellerModel.getGeoLocation(), formattedDate, 0,
                                bundle.getString("electricStart"), bundle.getInt("topSpeed"));
                    }
                    mDatabase.child(bundle.getString("dataKey")).setValue(products);

                    double placeLat = Double.parseDouble(sellerModel.getGeoLocation().split("::")[0]);
                    double placeLng = Double.parseDouble(sellerModel.getGeoLocation().split("::")[1]);

                    geoFire.setLocation(bundle.getString("dataKey"),
                            new GeoLocation(placeLat, placeLng));
                    geoFireAll.setLocation(bundle.getString("dataKey"),
                            new GeoLocation(placeLat, placeLng));

                    PrefUtils.saveToPrefs(getApplicationContext(), "showStats", "yes");
                    hideProgressDialog();
                    trackListing("Listing Done");
//                    Toast.makeText(getApplicationContext(), "Listed item has been sent for approval.", Toast.LENGTH_LONG).show();
                    finish();

                } else {
                    Toast.makeText(getApplicationContext(), "Fill all the details.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.rl_seller_type:
                showSellerTypesDialog();
                break;
            case R.id.rl_owner:
                showOwnerTypesDialog();
                break;
            case R.id.rl_location:
                findPlace();
                break;
            default:
                return;
        }
    }

    public void showSellerTypesDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = OptionPickerDialogFragment.NewInstance("Select Seller Type", sellerTypes);
        dialog.show(getSupportFragmentManager(), "SellerTypeDialogFragment");
    }

    public void showOwnerTypesDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = OptionPickerDialogFragment.NewInstance("Select Owner Type", ownerTypes);
        dialog.show(getSupportFragmentManager(), "OwnerTypeDialogFragment");
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

    private void trackListingType(String type) {
        try {
            JSONObject props = new JSONObject();
            props.put("Category", type);
            mMixpanel.track("Listing Activity", props);
        } catch (JSONException e) {
            Log.e("Autoroom", "Unable to add properties to JSONObject", e);
        }
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
    public void onDialogClick(DialogFragment dialog, int position, String title) {
        if (title.contentEquals("Select Seller Type")) {
            String object = sellerTypes[position];
            sellerType.setText(object);
        } else if (title.contentEquals("Select Owner Type")) {
            String object = ownerTypes[position];
            owner.setText(object);
        }
    }

    public void findPlace() {
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setCountry("IN")
                .build();
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .setFilter(typeFilter)
                            .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                sellerLocation.setText(place.getAddress());
                PrefUtils.saveToPrefs(getApplicationContext(), "placeLat", String.valueOf(place.getLatLng().latitude));
                PrefUtils.saveToPrefs(getApplicationContext(), "placeLng", String.valueOf(place.getLatLng().longitude));
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.d("placesResult: ", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    private void warnUser() {
        final AlertDialog.Builder alertDiallogBuilder = new AlertDialog.Builder(AddSellerLocation.this);
        alertDiallogBuilder.setTitle(Html.fromHtml("<font color='#212121'><b>Alert"));
        alertDiallogBuilder.setMessage(Html.fromHtml("<font color=\"#424242\">Are you sure cancel the listing process. All the filled data will be lost?</font>"));
        alertDiallogBuilder.setPositiveButton(Html.fromHtml("<font color=\"#212121\"><b>Yes</b></font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
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
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onBackPressed() {
        warnUser();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
