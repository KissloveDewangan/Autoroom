package in.co.opensoftlab.yourstore.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.co.opensoftlab.yourstore.BuildConfig;
import in.co.opensoftlab.yourstore.R;
import in.co.opensoftlab.yourstore.model.ProductModel;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by dewangankisslove on 22-03-2017.
 */

public class EditListing extends BaseActivity implements View.OnClickListener {
    Toolbar toolbar;
    ImageView close;
    EditText drivenDistance;
    EditText sellingPrice;
    EditText color;
    TextView vehicleName;
    Button doneEditing;

    Bundle bundle;
    DatabaseReference mDatabaseReference;
    private ValueEventListener valueEventListener;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_listing);
        initUI();


        bundle = getIntent().getExtras().getBundle("bundle");
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(getResources().getString(R.string.server_products)).child("all").child("live");

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    ProductModel productModel = dataSnapshot.getValue(ProductModel.class);

                    vehicleName.setText(productModel.getProductName());
                    drivenDistance.setText("" + productModel.getDrivenDistance());
                    sellingPrice.setText("" + productModel.getPrice());
                    color.setText(productModel.getColor());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        mDatabaseReference.child(bundle.getString("productKey")).addListenerForSingleValueEvent(valueEventListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDatabaseReference.child(bundle.getString("productKey")).removeEventListener(valueEventListener);
    }

    private void initUI() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        close = (ImageView) findViewById(R.id.iv_close);
        drivenDistance = (EditText) findViewById(R.id.et_driven_distance);
        sellingPrice = (EditText) findViewById(R.id.et_price);
        color = (EditText) findViewById(R.id.et_color);
        vehicleName = (TextView) findViewById(R.id.tv_vechile_name);
        doneEditing = (Button) findViewById(R.id.b_next);

        close.setOnClickListener(this);
        doneEditing.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_close:
                onBackPressed();
                break;
            case R.id.b_next:
                showProgressDialog();
                mDatabaseReference.child(bundle.getString("productKey")).runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        ProductModel p = mutableData.getValue(ProductModel.class);
                        if (p == null) {
                            return Transaction.success(mutableData);
                        }
                        p.setDrivenDistance(Integer.parseInt(drivenDistance.getText().toString()));
                        p.setPrice(Integer.parseInt(sellingPrice.getText().toString()));
                        p.setColor(color.getText().toString());
                        mutableData.setValue(p);

                        // Set value and report transaction success

                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                        hideProgressDialog();
                        finish();
                    }
                });
                break;
            default:
                return;
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }
}
