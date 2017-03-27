package in.co.opensoftlab.yourstore.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;

import java.util.List;

import in.co.opensoftlab.yourstore.R;
import in.co.opensoftlab.yourstore.fragment.AddConditionRating;
import in.co.opensoftlab.yourstore.helper.DatabaseHandler;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by dewangankisslove on 27-12-2016.
 */

public class BikeCondition extends AppCompatActivity implements View.OnClickListener, AddConditionRating.conditionCompleted {


    Toolbar toolbar;
    ImageView close;
    //    TextView skip;
    Bundle bundle;

    LinearLayout linearLayout;

    DatabaseHandler db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_condition);
        initUI();
        bundle = getIntent().getExtras().getBundle("temp");
        db = new DatabaseHandler(this);

        Fragment fragment = AddConditionRating.newInstance("bike", 3, bundle.getString("dataKey"));
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.linearLayout, fragment);
        fragmentTransaction.commit();

    }

    private void initUI() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
//        skip = (TextView) findViewById(R.id.tv_skip);
        close = (ImageView) findViewById(R.id.iv_close);

//        skip.setOnClickListener(this);
        close.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_close:
                warnUser();
                break;
//            case R.id.tv_skip:
//                break;
            default:
                break;
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }

    @Override
    public void conditionCompleteEvent() {
        List<String[]> featureList = db.getFeatures(bundle.getString("dataKey"));
        String temp = "";
        for (int i = 0; i < featureList.size(); i++) {
            if (i != featureList.size() - 1)
                temp = temp + featureList.get(i)[0] + "::" + featureList.get(i)[1] + ";;";
            else
                temp = temp + featureList.get(i)[0] + "::" + featureList.get(i)[1];
        }
        bundle.putString("condition", temp);
        Intent intent = new Intent(BikeCondition.this, AddProductImage.class);
        intent.putExtra("temp", bundle);
        startActivity(intent);
        db.removeAllFeatures();
        finish();
    }

    private void warnUser() {
        final AlertDialog.Builder alertDiallogBuilder = new AlertDialog.Builder(BikeCondition.this);
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
    public void onBackPressed() {
        warnUser();
    }
}
