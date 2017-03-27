package in.co.opensoftlab.yourstore.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import in.co.opensoftlab.yourstore.R;
import in.co.opensoftlab.yourstore.utils.PrefUtils;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by dewangankisslove on 08-12-2016.
 */

public class ChangeLocation extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    TextView location;
    RelativeLayout locationEdit;
    DiscreteSeekBar discreteSeekBar;
    //    RelativeLayout fuelEdit;
//    TextView fuelType;
    Button result;
    ImageView cancel;
    TextView searchRadius;

    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 5;

    private GoogleApiClient mGoogleApiClient;
    boolean locationChanged = false;
    int noFilters = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_location);
        initUI();

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient
                    .Builder(this)
                    .addApi(Places.GEO_DATA_API)
                    .addApi(Places.PLACE_DETECTION_API)
                    .enableAutoManage(this, this)
                    .build();
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    private void initUI() {
        locationEdit = (RelativeLayout) findViewById(R.id.rl_location);
        location = (TextView) findViewById(R.id.tv_user_location);
        result = (Button) findViewById(R.id.b_see_result);
        discreteSeekBar = (DiscreteSeekBar) findViewById(R.id.discrete_seekbar);
        cancel = (ImageView) findViewById(R.id.iv_close);
        searchRadius = (TextView) findViewById(R.id.tv_search_radius);

        String locate = PrefUtils.getFromPrefs(getApplicationContext(), "address", "None");
        location.setText(locate);

        discreteSeekBar.setProgress(Integer.parseInt(PrefUtils.getFromPrefs(getApplicationContext(), "searchDistance", "25")));
        searchRadius.setText(PrefUtils.getFromPrefs(getApplicationContext(), "searchDistance", "25") + " km");

        discreteSeekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                locationChanged = true;
                searchRadius.setText(value + " km");
                PrefUtils.saveToPrefs(getApplicationContext(), "searchDistance", String.valueOf(value));
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

            }
        });

        locationEdit.setOnClickListener(this);
//        fuelEdit.setOnClickListener(this);
        result.setOnClickListener(this);
        cancel.setOnClickListener(this);
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
            case R.id.rl_location:
                findPlace();
                break;
            case R.id.b_see_result:
                Intent returnIntent = new Intent();
                returnIntent.putExtra("noFilters", noFilters);
                returnIntent.putExtra("locationChanged", locationChanged);
                setResult(RESULT_OK, returnIntent);
                finish();
                break;
            default:
                return;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                location.setText(place.getAddress());
                //Toast.makeText(getApplicationContext(), String.valueOf(place.getLatLng()), Toast.LENGTH_SHORT).show();
                PrefUtils.saveToPrefs(getApplicationContext(), "saveBodyType", "All");
                PrefUtils.saveToPrefs(getApplicationContext(), "saveBrand", "All");
                PrefUtils.saveToPrefs(getApplicationContext(), "saveModel", "All");
                PrefUtils.saveToPrefs(getApplicationContext(), "saveLowPrice", "0");
                PrefUtils.saveToPrefs(getApplicationContext(), "saveHighPrice", "5000000");
                PrefUtils.saveToPrefs(getApplicationContext(), "noFilters", String.valueOf(noFilters));
                PrefUtils.saveToPrefs(getApplicationContext(), "latitude", String.valueOf(place.getLatLng().latitude));
                PrefUtils.saveToPrefs(getApplicationContext(), "longitude", String.valueOf(place.getLatLng().longitude));
                PrefUtils.saveToPrefs(getApplicationContext(), "address", place.getAddress().toString());
                locationChanged = true;
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.d("placesResult: ", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    public void findPlace() {
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setCountry("IN")
                .build();
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
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
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
