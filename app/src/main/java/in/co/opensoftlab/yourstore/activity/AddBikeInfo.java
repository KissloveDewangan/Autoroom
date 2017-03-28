package in.co.opensoftlab.yourstore.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import in.co.opensoftlab.yourstore.R;
import in.co.opensoftlab.yourstore.fragment.OptionPickerDialogFragment;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by dewangankisslove on 23-12-2016.
 */

public class AddBikeInfo extends BaseActivity implements OptionPickerDialogFragment.CategoryDialogListener, View.OnClickListener {

    Toolbar toolbar;
    ImageView close;
    RelativeLayout relativeLayout;
    Button next;
    TextView brand;
    TextView modelName;
    EditText modelYear;
    EditText drivenDistance;
    EditText variantName;
    EditText price;
    TextView electricStart;
    EditText engine;
    EditText mileage;
    EditText topSpeed;
    EditText tankCapacity;
    EditText color;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference mSeller;
    private DatabaseReference mOffering;
    String pKey = "";
    String[] brands;
    String[] modelNames;
    String[] electricStarts;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addbike_info);
        initUI();

        electricStarts = getResources().getStringArray(R.array.electric_starts);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference(getResources().getString(R.string.server_products)).child(getResources().getString(R.string.server_all)).child("live");
        mSeller = FirebaseDatabase.getInstance().getReference("sellers");
        mOffering = FirebaseDatabase.getInstance().getReference(getResources().getString(R.string.server_ourdb)).child("offerings").child("bike");

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initUI() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        close = (ImageView) findViewById(R.id.iv_close);
        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
        next = (Button) findViewById(R.id.b_next);
        brand = (TextView) findViewById(R.id.tv_brand);
        modelName = (TextView) findViewById(R.id.et_model_name);
        modelYear = (EditText) findViewById(R.id.et_model_year);
        electricStart = (TextView) findViewById(R.id.tv_electric_start);
        drivenDistance = (EditText) findViewById(R.id.et_driven_distance);
        variantName = (EditText) findViewById(R.id.et_variant_name);
        price = (EditText) findViewById(R.id.et_price);
        engine = (EditText) findViewById(R.id.et_engine);
        mileage = (EditText) findViewById(R.id.et_mileage);
        topSpeed = (EditText) findViewById(R.id.et_top_speed);
        tankCapacity = (EditText) findViewById(R.id.et_tank_capacity);
        color = (EditText) findViewById(R.id.et_color);

        close.setOnClickListener(this);
        brand.setOnClickListener(this);
        modelName.setOnClickListener(this);
        next.setOnClickListener(this);
        electricStart.setOnClickListener(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }

    @Override
    public void onDialogClick(DialogFragment dialog, int position, String title) {
        if (title.contentEquals("Self Start")) {
            String object = electricStarts[position];
            electricStart.setText(object);
        } else if (title.contentEquals("Select a Brand")) {
            String object = brands[position];
            brand.setText(object);
        } else if (title.contentEquals("Select a Model")) {
            String object = modelNames[position];
            modelName.setText(object);
        }
    }


    public void showElectricStartDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = OptionPickerDialogFragment.NewInstance("Self Start", electricStarts);
        dialog.show(getSupportFragmentManager(), "ElectricStartDialogFragment");
    }


    public void showBrandDialog() {
        showProgressDialog();
        // Create an instance of the dialog fragment and show it
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> stringList = new ArrayList<>();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String key = dataSnapshot1.getKey();
                    stringList.add(key);
                }

                hideProgressDialog();
                brands = stringList.toArray(new String[stringList.size()]);

                DialogFragment dialog = OptionPickerDialogFragment.NewInstance("Select a Brand", brands);
                dialog.show(getSupportFragmentManager(), "BrandDialogFragment");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mOffering.child("brands").addListenerForSingleValueEvent(valueEventListener);

    }

    public void showModelDialog() {
        showProgressDialog();
        // Create an instance of the dialog fragment and show it
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> stringList = new ArrayList<>();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String key = dataSnapshot1.getKey();
                    stringList.add(key);
                }

                hideProgressDialog();
                modelNames = stringList.toArray(new String[stringList.size()]);

                DialogFragment dialog = OptionPickerDialogFragment.NewInstance("Select a Model", modelNames);
                dialog.show(getSupportFragmentManager(), "ModelDialogFragment");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mOffering.child("models").child(brand.getText().toString()).addListenerForSingleValueEvent(valueEventListener);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_close:
                warnUser();
                break;
            case R.id.tv_electric_start:
                showElectricStartDialog();
                break;
            case R.id.tv_brand:
                showBrandDialog();
                break;
            case R.id.et_model_name:
                if (brand.getText().toString().isEmpty() || brand.getText().toString().contentEquals("")) {
                    showMessage(relativeLayout);
                } else {
                    showModelDialog();
                }
                break;
            case R.id.b_next:
                if (!validateForm()) {
                    return;
                } else {
                    pKey = mDatabase.push().getKey();
                    mSeller.child(getUserUID()).child(pKey).setValue(true);
                    int sp = Integer.parseInt(price.getText().toString());
                    int speedTop = Integer.parseInt(topSpeed.getText().toString());
                    int drive = Integer.parseInt(drivenDistance.getText().toString());
                    int tc = Integer.parseInt(tankCapacity.getText().toString());
                    Bundle bundle = new Bundle();
                    bundle.putString("productType", "Bike");
                    bundle.putString("brand", brand.getText().toString());
                    bundle.putString("modelName", modelName.getText().toString());
                    bundle.putString("variantName", variantName.getText().toString());
                    bundle.putInt("modelYear", Integer.parseInt(modelYear.getText().toString()));
                    bundle.putString("electricStart", electricStart.getText().toString());
                    bundle.putInt("drivenDistance", drive);
                    bundle.putInt("price", sp);
                    bundle.putString("engine", engine.getText().toString());
                    bundle.putString("mileage", mileage.getText().toString());
                    bundle.putInt("topSpeed", speedTop);
                    bundle.putInt("tankCapacity", tc);
                    bundle.putString("color", color.getText().toString());
                    bundle.putString("dataKey", pKey);
                    Intent intent = new Intent(AddBikeInfo.this, BikeCondition.class);
                    intent.putExtra("temp", bundle);
                    startActivity(intent);
                    finish();
                }
                break;
            default:
                return;
        }
    }

    private void warnUser() {
        final AlertDialog.Builder alertDiallogBuilder = new AlertDialog.Builder(AddBikeInfo.this);
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

    private boolean validateForm() {
        boolean valid = true;

        String brnd = brand.getText().toString();
        if (TextUtils.isEmpty(brnd)) {
            brand.setError("Required.");
            valid = false;
        } else {
            brand.setError(null);
        }

        String nameProduct = modelName.getText().toString();
        if (TextUtils.isEmpty(nameProduct)) {
            modelName.setError("Required.");
            valid = false;
        } else {
            modelName.setError(null);
        }

        String categ = modelYear.getText().toString();
        if (TextUtils.isEmpty(categ)) {
            modelYear.setError("Required.");
            valid = false;
        } else {
            modelYear.setError(null);
        }

        String subCate = electricStart.getText().toString();
        if (TextUtils.isEmpty(subCate)) {
            electricStart.setError("Required.");
            valid = false;
        } else {
            electricStart.setError(null);
        }

        String dist = drivenDistance.getText().toString();
        if (TextUtils.isEmpty(dist)) {
            drivenDistance.setError("Required.");
            valid = false;
        } else {
            drivenDistance.setError(null);
        }

        String cost = price.getText().toString();
        if (TextUtils.isEmpty(cost)) {
            price.setError("Required.");
            valid = false;
        } else {
            price.setError(null);
        }


        String eng = engine.getText().toString();
        if (TextUtils.isEmpty(eng)) {
            engine.setError("Required.");
            valid = false;
        } else {
            engine.setError(null);
        }

        String mile = mileage.getText().toString();
        if (TextUtils.isEmpty(mile)) {
            mileage.setError("Required.");
            valid = false;
        } else {
            mileage.setError(null);
        }

        String noSeat = topSpeed.getText().toString();
        if (TextUtils.isEmpty(noSeat)) {
            topSpeed.setError("Required.");
            valid = false;
        } else {
            topSpeed.setError(null);
        }

        String tankCapa = tankCapacity.getText().toString();
        if (TextUtils.isEmpty(tankCapa)) {
            tankCapacity.setError("Required.");
            valid = false;
        } else {
            tankCapacity.setError(null);
        }

        String colr = color.getText().toString();
        if (TextUtils.isEmpty(colr)) {
            color.setError("Required.");
            valid = false;
        } else {
            color.setError(null);
        }

        return valid;
    }

    public void showMessage(RelativeLayout relativeLayout) {
        Snackbar snackbar = Snackbar.make(relativeLayout, "Select brand first.", Snackbar.LENGTH_SHORT);
        snackbar.show();
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
    public void onBackPressed() {
        warnUser();
    }
}
