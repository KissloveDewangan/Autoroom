package in.co.opensoftlab.yourstore.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONException;
import org.json.JSONObject;

import in.co.opensoftlab.yourstore.BuildConfig;
import in.co.opensoftlab.yourstore.R;
import in.co.opensoftlab.yourstore.views.RangeSeekBar;
import in.co.opensoftlab.yourstore.utils.PrefUtils;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by dewangankisslove on 08-12-2016.
 */

public class CarFilterActivity extends AppCompatActivity implements View.OnClickListener {

    MixpanelAPI mMixpanel;
    TextView minPrice, maxPrice;
    TextView brand, carModel, bodyType, sortBy;
    RelativeLayout brandEdit, modelEdit, bodyEdit, sortEdit;
    TextView reset;
    //    RelativeLayout fuelEdit;
//    TextView fuelType;
    Button result;
    RangeSeekBar<Integer> rangeSeekBar;
    ImageView cancel;

    private static int BRAND_REQUEST_CODE = 1;
    private static int MODEL_REQUEST_CODE = 2;
    //    private static int FUEL_REQUEST_CODE = 3;
    private static int BODY_TYPE_REQUEST_CODE = 4;
    private static int SORT_TYPE_REQUEST_CODE = 6;

    String lowPrice = "0";
    String highPrice = "5000000";
    int noFilters = 0;
    int filterPrice = 0;
    int filterBrand = 0;
    int filterModel = 0;
    int filterBody = 0;
    int sortType = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_cars);
        initUI();

        String projectToken = BuildConfig.ANALYTICS_TOKEN;
        mMixpanel = MixpanelAPI.getInstance(this, projectToken);

    }

    private void trackFilterActivity(String filterName, String propertyName, String propertyVal) {
        try {
            JSONObject props = new JSONObject();
            props.put("Filter Name", filterName);
            props.put(propertyName, propertyVal);
            mMixpanel.track("Car Filter Activity", props);
        } catch (JSONException e) {
            Log.e("Autoroom", "Unable to add properties to JSONObject", e);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void initUI() {
        minPrice = (TextView) findViewById(R.id.tv_min_price);
        maxPrice = (TextView) findViewById(R.id.tv_max_price);
        brandEdit = (RelativeLayout) findViewById(R.id.rl_choose_brand);
        modelEdit = (RelativeLayout) findViewById(R.id.rl_choose_model);
//        fuelEdit = (RelativeLayout) findViewById(R.id.rl_choose_fuel);
        brand = (TextView) findViewById(R.id.tv_brand);
        carModel = (TextView) findViewById(R.id.tv_model);
//        fuelType = (TextView) findViewById(R.id.tv_fuel_type);
        bodyEdit = (RelativeLayout) findViewById(R.id.rl_choose_body_type);
        bodyType = (TextView) findViewById(R.id.tv_body_type);
        result = (Button) findViewById(R.id.b_see_result);
        sortEdit = (RelativeLayout) findViewById(R.id.rl_sort_by);
        sortBy = (TextView) findViewById(R.id.tv_sort);
        reset = (TextView) findViewById(R.id.reset_all);
        cancel = (ImageView) findViewById(R.id.iv_close);

        minPrice.setText("Rs " + PrefUtils.getFromPrefs(getApplicationContext(), "saveLowPrice", "0"));
        maxPrice.setText("Rs " + PrefUtils.getFromPrefs(getApplicationContext(), "saveHighPrice", "5000000"));
        brand.setText(PrefUtils.getFromPrefs(getApplicationContext(), "saveBrand", "All"));
        carModel.setText(PrefUtils.getFromPrefs(getApplicationContext(), "saveModel", "All"));
        bodyType.setText(PrefUtils.getFromPrefs(getApplicationContext(), "saveBodyType", "All"));
        sortBy.setText(PrefUtils.getFromPrefs(getApplicationContext(), "saveSortBy", "None"));
        lowPrice = PrefUtils.getFromPrefs(getApplicationContext(), "saveLowPrice", "0");
        highPrice = PrefUtils.getFromPrefs(getApplicationContext(), "saveHighPrice", "5000000");

        // Setup the new range seek bar
        rangeSeekBar = new RangeSeekBar<Integer>(this);
        // Set the range
        rangeSeekBar.setRangeValues(0, 5000000);
        rangeSeekBar.setSelectedMinValue(0);
        rangeSeekBar.setSelectedMaxValue(5000000);
        rangeSeekBar.setNotifyWhileDragging(true);
        rangeSeekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
                minPrice.setText("Rs " + minValue);
                maxPrice.setText("Rs " + maxValue);

                lowPrice = String.valueOf(minValue);
                highPrice = String.valueOf(maxValue);
            }
        });

        // Add to layout
        LinearLayout layout = (LinearLayout) findViewById(R.id.seekbar_placeholder);
        layout.addView(rangeSeekBar);

        rangeSeekBar.setSelectedMinValue(Integer.valueOf(PrefUtils.getFromPrefs(getApplicationContext(), "saveLowPrice", "0")));
        rangeSeekBar.setSelectedMaxValue(Integer.valueOf(PrefUtils.getFromPrefs(getApplicationContext(), "saveHighPrice", "5000000")));


        brandEdit.setOnClickListener(this);
        modelEdit.setOnClickListener(this);
//        fuelEdit.setOnClickListener(this);
        bodyEdit.setOnClickListener(this);
        result.setOnClickListener(this);
        sortEdit.setOnClickListener(this);
        reset.setOnClickListener(this);
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
            case R.id.reset_all:
                rangeSeekBar.resetSelectedValues();
                minPrice.setText("Rs 0");
                maxPrice.setText("Rs 5000000");
                lowPrice = String.valueOf("0");
                highPrice = String.valueOf("5000000");
                brand.setText("All");
                carModel.setText("All");
                bodyType.setText("All");
                sortBy.setText("None");
                filterBody = 0;
                filterModel = 0;
                filterBrand = 0;
                filterPrice = 0;
                sortType = 0;
                break;
            case R.id.rl_choose_brand:
                Bundle bundle = new Bundle();
                bundle.putString("brand", "Brands");
                bundle.putString("what", "Car");
                Intent intent = new Intent(CarFilterActivity.this, FilterChooserActivity.class);
                intent.putExtra("chooser", bundle);
                startActivityForResult(intent, BRAND_REQUEST_CODE);
                break;
            case R.id.rl_choose_model:
                if (!brand.getText().toString().contentEquals("All")) {
                    Bundle bundle2 = new Bundle();
                    bundle2.putString("brand", brand.getText().toString());
                    bundle2.putString("model", "Car Models");
                    bundle2.putString("what", "Car");
                    Intent intent2 = new Intent(CarFilterActivity.this, FilterChooserActivity.class);
                    intent2.putExtra("chooser", bundle2);
                    startActivityForResult(intent2, MODEL_REQUEST_CODE);
                } else {
                    Toast.makeText(getApplicationContext(), "First select brand", Toast.LENGTH_SHORT).show();
                }
                break;
//            case R.id.rl_choose_fuel:
//                Bundle bundle3 = new Bundle();
//                bundle3.putString("fuelType", "Fuel Type");
//                Intent intent3 = new Intent(CarFilterActivity.this, FilterChooserActivity.class);
//                intent3.putExtra("chooser", bundle3);
//                startActivityForResult(intent3, FUEL_REQUEST_CODE);
//                break;
            case R.id.rl_choose_body_type:
                Bundle bundle4 = new Bundle();
                bundle4.putString("bodyType", "Fuel Type");
                bundle4.putString("what", "Car");
                Intent intent4 = new Intent(CarFilterActivity.this, FilterChooserActivity.class);
                intent4.putExtra("chooser", bundle4);
                startActivityForResult(intent4, BODY_TYPE_REQUEST_CODE);
                break;
            case R.id.rl_sort_by:
                Bundle bundle5 = new Bundle();
                bundle5.putString("sortBy", "Sort by");
                bundle5.putString("what", "Car");
                Intent intent5 = new Intent(CarFilterActivity.this, FilterChooserActivity.class);
                intent5.putExtra("chooser", bundle5);
                startActivityForResult(intent5, SORT_TYPE_REQUEST_CODE);
                break;
            case R.id.b_see_result:
                if (!lowPrice.contentEquals("0") || !highPrice.contentEquals("5000000")) {
                    filterPrice = 1;
                } else {
                    filterPrice = 0;
                }
                filterBrand = brand.getText().toString().contentEquals("All") ? 0 : 1;
                filterModel = carModel.getText().toString().contentEquals("All") ? 0 : 1;
                filterBody = bodyType.getText().toString().contentEquals("All") ? 0 : 1;
                sortType = sortBy.getText().toString().contentEquals("None") ? 0 : 1;
                noFilters = filterBody + filterModel + filterBrand + filterPrice + sortType;
                Intent returnIntent = new Intent();
                PrefUtils.saveToPrefs(getApplicationContext(), "saveSortBy", sortBy.getText().toString());
                PrefUtils.saveToPrefs(getApplicationContext(), "saveBodyType", bodyType.getText().toString());
                PrefUtils.saveToPrefs(getApplicationContext(), "saveBrand", brand.getText().toString());
                PrefUtils.saveToPrefs(getApplicationContext(), "saveModel", carModel.getText().toString());
                PrefUtils.saveToPrefs(getApplicationContext(), "saveLowPrice", lowPrice);
                PrefUtils.saveToPrefs(getApplicationContext(), "saveHighPrice", highPrice);
                PrefUtils.saveToPrefs(getApplicationContext(), "noFilters", String.valueOf(noFilters));
                returnIntent.putExtra("bodyType", bodyType.getText().toString());
                returnIntent.putExtra("brand", brand.getText().toString());
                returnIntent.putExtra("model", carModel.getText().toString());
                returnIntent.putExtra("lowPrice", lowPrice);
                returnIntent.putExtra("highPrice", highPrice);
                returnIntent.putExtra("noFilters", noFilters);
                returnIntent.putExtra("sortBy", sortBy.getText().toString());
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
        if (requestCode == BRAND_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                trackFilterActivity("brand", "brandName", data.getStringExtra("result"));
                brand.setText(data.getStringExtra("result"));
            }
        } else if (requestCode == MODEL_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                trackFilterActivity("model", "modelName", data.getStringExtra("result"));
                carModel.setText(data.getStringExtra("result"));
            }
        } else if (requestCode == BODY_TYPE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                trackFilterActivity("body type", "bodyType", data.getStringExtra("result"));
                bodyType.setText(data.getStringExtra("result"));
            }
        } else if (requestCode == SORT_TYPE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                trackFilterActivity("sort", "sortBy", data.getStringExtra("result"));
                sortBy.setText(data.getStringExtra("result"));
            }
        }
    }
}
