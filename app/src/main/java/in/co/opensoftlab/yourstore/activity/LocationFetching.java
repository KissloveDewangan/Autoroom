package in.co.opensoftlab.yourstore.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import in.co.opensoftlab.yourstore.api.GetGeoApi;
import in.co.opensoftlab.yourstore.R;
import in.co.opensoftlab.yourstore.model.GeocodeResult;
import in.co.opensoftlab.yourstore.utils.PrefUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by dewangankisslove on 05-03-2017.
 */

public class LocationFetching extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static final int ACCESS_FINE_LOCATION_INTENT_ID = 3;
    private static final String BROADCAST_ACTION = "android.location.PROVIDERS_CHANGED";

    private Location mLastLocation;

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

    // boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = true;

    private LocationRequest mLocationRequest;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 5000; // 1 sec
    private static int FATEST_INTERVAL = 1000; // .5 sec

    // UI elements

    GetGeoApi getGeoApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_fetching);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users").child(getUserUID());

        Gson gson = new GsonBuilder().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        getGeoApi = retrofit.create(GetGeoApi.class);

        // First we need to check availability of play services
        if (checkPlayServices() && mGoogleApiClient == null) {
            // Building the GoogleApi client
            buildGoogleApiClient();
        }
        checkPermissions();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkPlayServices();
        registerReceiver(gpsLocationReceiver, new IntentFilter(BROADCAST_ACTION));

        if (!PrefUtils.getFromPrefs(getApplicationContext(), "address", "").contentEquals("")) {
            startActivity(new Intent(LocationFetching.this, MainActivity.class));
            finish();
        }

        // Resuming the periodic location updates
        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (gpsLocationReceiver != null)
            unregisterReceiver(gpsLocationReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected())
            stopLocationUpdates();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case RESULT_OK:
                        Log.e("Settings", "Result OK");
                        startLocationUpdates();
                        break;
                    case RESULT_CANCELED:
                        Log.e("Settings", "Result Cancel");
                        new Handler().postDelayed(sendUpdatesToUI, 10);
                        break;
                }
                break;
        }
    }

    /**
     * Method to display the location on UI
     */
    private void displayLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();

            PrefUtils.saveToPrefs(getApplicationContext(), "latitude", String.valueOf(latitude));
            PrefUtils.saveToPrefs(getApplicationContext(), "longitude", String.valueOf(longitude));

            Call<GeocodeResult> addressResultCall = getGeoApi.getFormattedAddress(latitude + "," + longitude, getResources().getString(R.string.api_key));
            addressResultCall.enqueue(new Callback<GeocodeResult>() {
                @Override
                public void onResponse(Call<GeocodeResult> call, Response<GeocodeResult> response) {
                    GeocodeResult geocodeResult = response.body();
                    String city1 = "";
                    String city2 = "";
                    String state = "";
                    String country = "";
                    String postalCode = "";
                    for (int i = 0; i < geocodeResult.getResults().get(0).getAddress_components().size(); i++) {
                        if (geocodeResult.getResults().get(0).getAddress_components().get(i).getTypes().contains("locality")) {
                            city1 = geocodeResult.getResults().get(0).getAddress_components().get(i).getLong_name();
                        } else if (geocodeResult.getResults().get(0).getAddress_components().get(i).getTypes().contains("administrative_area_level_3")) {
                            city2 = geocodeResult.getResults().get(0).getAddress_components().get(i).getLong_name();
                        } else if (geocodeResult.getResults().get(0).getAddress_components().get(i).getTypes().contains("administrative_area_level_1")) {
                            state = geocodeResult.getResults().get(0).getAddress_components().get(i).getLong_name();
                        } else if (geocodeResult.getResults().get(0).getAddress_components().get(i).getTypes().contains("country")) {
                            country = geocodeResult.getResults().get(0).getAddress_components().get(i).getLong_name();
                        } else if (geocodeResult.getResults().get(0).getAddress_components().get(i).getTypes().contains("postal_code")) {
                            postalCode = geocodeResult.getResults().get(0).getAddress_components().get(i).getLong_name();
                        }
                    }
                    if (!city1.isEmpty()) {
                        PrefUtils.saveToPrefs(getApplicationContext(), "currentCity", city1 + ", " + state +
                                " " + postalCode + ", " + country);
                    } else if (city1.isEmpty() && !city2.isEmpty()) {
                        PrefUtils.saveToPrefs(getApplicationContext(), "currentCity", city2 + ", " + state +
                                " " + postalCode + ", " + country);
                    } else {
                        PrefUtils.saveToPrefs(getApplicationContext(), "currentCity", state +
                                " " + postalCode + ", " + country);
                    }
                    String address = geocodeResult.getResults().get(0).getFormattedAddress();
//                    if (!city1.isEmpty()) {
//                        address = city1 + ", " + state + " " + postalCode + ", " + country;
//                        PrefUtils.saveToPrefs(getApplicationContext(), "address", address);
//                    } else if (city1.isEmpty() && !city2.isEmpty()) {
//                        address = city2 + ", " + state + " " + postalCode + ", " + country;
//                        PrefUtils.saveToPrefs(getApplicationContext(), "address", address);
//                    } else {
//                        address = "null" + ", " + state + " " + postalCode + ", " + country;
//                        PrefUtils.saveToPrefs(getApplicationContext(), "address", address);
//                    }
                    PrefUtils.saveToPrefs(getApplicationContext(), "address", address);
                    mDatabase.child("location").setValue(address);
                    startActivity(new Intent(LocationFetching.this, MainActivity.class));
                    finish();

                }

                @Override
                public void onFailure(Call<GeocodeResult> call, Throwable t) {

                }
            });


        } else {
        }
    }

    /**
     * Creating google api client object
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    /**
     * Creating location request object
     */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Method to verify google play services on the device
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Starting the location updates
     */
    protected void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);

        if (mLastLocation != null && mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }

    }

    /**
     * Stopping location updates
     */
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    /**
     * Google api callback methods
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.d("Connection failed:", result.getErrorMessage());
    }

    @Override
    public void onConnected(Bundle arg0) {

        // Once connected with google api, get the location

        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        // Assign the new location
        mLastLocation = location;

        // Displaying the new location on UI
        displayLocation();
        if (mGoogleApiClient.isConnected())
            stopLocationUpdates();
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(LocationFetching.this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED)
                requestLocationPermission();
            else
                showSettingDialog();
        } else
            showSettingDialog();

    }

    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(LocationFetching.this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(LocationFetching.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_FINE_LOCATION_INTENT_ID);

        } else {
            ActivityCompat.requestPermissions(LocationFetching.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_FINE_LOCATION_INTENT_ID);
        }
    }

    private void showSettingDialog() {
        createLocationRequest();
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(LocationFetching.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    /* Broadcast receiver to check status of GPS */
    private BroadcastReceiver gpsLocationReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            //If Action is Location
            if (intent.getAction().matches(BROADCAST_ACTION)) {
                LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                //Check if GPS is turned ON or OFF
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    Log.e("About GPS", "GPS is Enabled in your device");
                } else {
                    //If GPS turned OFF show Location Dialog
                    new Handler().postDelayed(sendUpdatesToUI, 10);
                    // showSettingDialog();
                    Log.e("About GPS", "GPS is Disabled in your device");
                }

            }
        }
    };

    //Run on UI
    private Runnable sendUpdatesToUI = new Runnable() {
        public void run() {
            showSettingDialog();
        }
    };

    public String getUserUID() {
        return mAuth.getCurrentUser().getUid();
    }
}
